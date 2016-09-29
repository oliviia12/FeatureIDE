/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2016  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 * 
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
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
package de.ovgu.featureide.fm.ui.editors.featuremodel.actions;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PlatformUI;

import de.ovgu.featureide.fm.core.base.IConstraint;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.ui.FMUIPlugin;
import de.ovgu.featureide.fm.ui.editors.featuremodel.editparts.ConstraintEditPart;
import de.ovgu.featureide.fm.ui.editors.featuremodel.editparts.ModelEditPart;
import de.ovgu.featureide.fm.ui.editors.featuremodel.operations.ExpandConstraintOperation;
import static de.ovgu.featureide.fm.core.localization.StringTable.EXPAND_CONSTRAINT;

import java.util.Iterator;

/**
 * TODO description
 * 
 * @author Maximilian K�hl
 * @author Christopher Sontag
 */
public class ExpandConstraintAction extends Action {

	private IFeatureModel featureModel;
	private IConstraint constraint;
	private ISelectionChangedListener listener = new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			setEnabled(isValidSelection(selection));
		}
	};

	/**
	 * @param viewer
	 * @param featuremodel
	 * @param menuname
	 */
	public ExpandConstraintAction(Object viewer, IFeatureModel featureModel) {
		super(EXPAND_CONSTRAINT);
		this.featureModel = featureModel;
		if (viewer instanceof TreeViewer) {
			((TreeViewer) viewer).addSelectionChangedListener(listener);
		} else {
			((GraphicalViewerImpl) viewer).addSelectionChangedListener(listener);
		}
	}

	@Override
	public void run() {
		ExpandConstraintOperation op = new ExpandConstraintOperation(featureModel, constraint);
		try {
			PlatformUI.getWorkbench().getOperationSupport().getOperationHistory().execute(op, null, null);
		} catch (ExecutionException e) {
			FMUIPlugin.getDefault().logError(e);
		}
	}
	
	protected boolean isValidSelection(IStructuredSelection selection) {
		if (selection.size() == 1 && selection.getFirstElement() instanceof ModelEditPart)
			return false;

		Iterator<?> iter = selection.iterator();
		while (iter.hasNext()) {
			Object editPart = iter.next();
			if (editPart instanceof ConstraintEditPart) {
				constraint = ((ConstraintEditPart) editPart).getConstraintModel().getObject();
				return true;
			}
			if (editPart instanceof IConstraint) {
				constraint = (IConstraint) editPart;
				return true;
			}
		}
		return false;
	}

}
