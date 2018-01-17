/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2017  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 *
 * FeatureIDE is free software: you can redistributecolorSchemeNameText/or modify
 * it under the terms of the GNU LcolorSchemeNameTexteneral PucolorSchemeNameTextcense as published by
 * the FrecolorSchemeNameTextare Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package de.ovgu.featureide.fm.ui.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.color.ColorScheme;
import de.ovgu.featureide.fm.core.color.FeatureColorManager;

/**
 * Page for {@link ColorSchemeWizard}.
 *
 * @author Sebastian Krieter
 * @author Paul Westphal
 */
public class ColorSchemePage extends WizardPage {

	private final IFeatureModel featureModel;
	private final ArrayList<String> colorSchemeNames;

	private Button newLayoutButton;
	private List colorSchemeList;
	private Text newColorSchemeText;

	protected ColorSchemePage(IFeatureModel featureModel) {
		super("SelectColorSchemePage");
		this.featureModel = featureModel;
		setTitle("Color Schemes");
		setDescription("Select, Rename, Delete, or Create Color Schemes");

		final Collection<ColorScheme> colorSchemes = FeatureColorManager.getColorSchemes(featureModel);
		colorSchemeNames = new ArrayList<>(colorSchemes.size() + 1);

		for (final ColorScheme colorScheme : colorSchemes) {
			if (!colorScheme.isDefault()) {
				colorSchemeNames.add(colorScheme.getName());
			}
		}
	}

	@Override
	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, false));
		Button button;

		newColorSchemeText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		newLayoutButton = new Button(composite, SWT.NULL);
		newColorSchemeText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));
		newColorSchemeText.addFocusListener(new FocusAdapter() {
			Button defaultButton;

			@Override
			public void focusGained(FocusEvent e) {
				defaultButton = getWizard().getContainer().getShell().getDefaultButton();
				getWizard().getContainer().getShell().setDefaultButton(newLayoutButton);
			}

			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				getWizard().getContainer().getShell().setDefaultButton(defaultButton);
			}
		});

		newLayoutButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		newLayoutButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createNewColorScheme();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		colorSchemeList = new List(composite, SWT.FILL | SWT.BORDER | SWT.NO_FOCUS | SWT.SINGLE | SWT.HIDE_SELECTION);
		colorSchemeList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		colorSchemeList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final String[] selection = ((List) e.getSource()).getSelection();
				if (selection.length > 0) {
					newColorSchemeText.setText(selection[0]);
					selectColorScheme();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		colorSchemeList.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {
				// Delete selected color scheme when pressing del
				if (e.keyCode == 127) {
					deleteColorScheme();
				}
			}
		});

		// Col 3
		final Composite buttonComposite = new Composite(composite, SWT.NULL);
		final GridLayout buttonLayout = new GridLayout(1, true);
		buttonLayout.marginWidth = 0;
		buttonLayout.marginHeight = 0;
		buttonComposite.setLayout(buttonLayout);
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));

		button = new Button(buttonComposite, SWT.NULL);
		button.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE));
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteColorScheme();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		button = new Button(buttonComposite, SWT.NULL);
		button.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_CLEAR));
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				renameColorScheme();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		updateColorSchemeList();

		final ColorScheme currentColorScheme = FeatureColorManager.getCurrentColorScheme(featureModel);
		if (!currentColorScheme.isDefault()) {
			final String name = currentColorScheme.getName();
			selectColorScheme(name);
			newColorSchemeText.setText(name);
		}

		setControl(composite);
	}

	private void selectColorScheme() {
		final String[] selection = colorSchemeList.getSelection();
		if (selection.length > 0) {
			final String colorSchemeName = selection[0];
			if (FeatureColorManager.hasColorScheme(featureModel, colorSchemeName)) {
				newColorSchemeText.setText(colorSchemeName);
			}
		} else if (newColorSchemeText.getText().equals("")) {
			if (colorSchemeList.getItemCount() > 0) {
				final String first = colorSchemeList.getItem(0);
				if (FeatureColorManager.hasColorScheme(featureModel, first)) {
					newColorSchemeText.setText(first);
				}
			}
		}
	}

	private void renameColorScheme() {
		final String[] selection = colorSchemeList.getSelection();
		if (selection.length > 0) {
			final String oldSchemeName = selection[0];
			final String newSchemeName = newColorSchemeText.getText();
			if ((oldSchemeName != null) && !oldSchemeName.isEmpty() && FeatureColorManager.hasColorScheme(featureModel, oldSchemeName)
				&& !FeatureColorManager.hasColorScheme(featureModel, newSchemeName)) {
				FeatureColorManager.renameColorScheme(featureModel, oldSchemeName, newSchemeName);
				final int index = colorSchemeNames.indexOf(oldSchemeName);
				colorSchemeNames.set(index, newSchemeName);
				updateColorSchemeList();
				selectColorScheme(newSchemeName);
			}
		}
	}

	private void deleteColorScheme() {
		final String[] selection = colorSchemeList.getSelection();
		if (selection.length > 0) {
			final String colorSchemeName = selection[0];
			if (FeatureColorManager.hasColorScheme(featureModel, colorSchemeName)) {
				FeatureColorManager.removeColorScheme(featureModel, colorSchemeName);
				final int index = colorSchemeList.getSelectionIndex();
				colorSchemeNames.remove(index);
				colorSchemeList.remove(index);
				if (colorSchemeNames.size() > index) {
					colorSchemeList.setSelection(index);
				} else if (colorSchemeNames.size() > 0) {
					colorSchemeList.setSelection(index - 1);
				} else {
					newColorSchemeText.setText("");
				}
				selectColorScheme();
			}
		}
	}

	private void updateColorSchemeList() {
		Collections.sort(colorSchemeNames);
		final String[] array = colorSchemeNames.toArray(new String[0]);
		colorSchemeList.setItems(array);
	}

	private void selectColorScheme(final String name) {
		final int index = Arrays.binarySearch(colorSchemeNames.toArray(new String[0]), name);
		if (index >= 0) {
			colorSchemeList.setSelection(index);
		}
	}

	private void createNewColorScheme() {
		final String newSchemeName = newColorSchemeText.getText();
		if ((newSchemeName != null) && !newSchemeName.isEmpty() && !FeatureColorManager.hasColorScheme(featureModel, newSchemeName)) {
			FeatureColorManager.newColorScheme(featureModel, newSchemeName);
			colorSchemeNames.add(newSchemeName);
			updateColorSchemeList();
			selectColorScheme(newSchemeName);
			if (colorSchemeList.getItemCount() == 1) {
				selectColorScheme();
			}
		}
		newColorSchemeText.setFocus();
	}

	public void setActiveColorScheme() {
		final int selectionIndex = colorSchemeList.getSelectionIndex();
		if (selectionIndex >= 0) {
			final String collName = colorSchemeList.getSelection()[0];
			if (FeatureColorManager.hasColorScheme(featureModel, collName)) {
				FeatureColorManager.setActive(featureModel, collName);
			}
		}

	}
}
