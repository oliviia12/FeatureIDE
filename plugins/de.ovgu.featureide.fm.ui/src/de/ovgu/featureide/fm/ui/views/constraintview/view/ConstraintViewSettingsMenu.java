/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2017  FeatureIDE team, University of Magdeburg, Germany
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
package de.ovgu.featureide.fm.ui.views.constraintview.view;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;

import de.ovgu.featureide.fm.ui.editors.FeatureDiagramViewer;
import de.ovgu.featureide.fm.ui.editors.IGraphicalFeatureModel;
import de.ovgu.featureide.fm.ui.editors.featuremodel.actions.ShowCollapsedConstraintsAction;
import de.ovgu.featureide.fm.ui.utils.FeatureModelUtil;
import de.ovgu.featureide.fm.ui.views.constraintview.ConstraintViewController;

/**
 * this class contains a menu where settings can be set for the ConstraintView
 *
 * @author Domenik Eichhorn
 */
public class ConstraintViewSettingsMenu {

	ConstraintViewController controller;

	private final IToolBarManager manager;
	private final IGraphicalFeatureModel graphicalModel; // active graphical FeatureModel
	private final FeatureDiagramViewer viewer; 	// active FeatureDiagramViewer

	/**
	 * constructor:
	 */
	public ConstraintViewSettingsMenu(ConstraintViewController controller) {
		this.controller = controller;

		manager = controller.getViewSite().getActionBars().getToolBarManager();
		graphicalModel = FeatureModelUtil.getActiveFMEditor().diagramEditor.getGraphicalFeatureModel();
		viewer = FeatureModelUtil.getActiveFMEditor().diagramEditor.getViewer();

		createToolBarLayout();
	}

	/**
	 * Action to show/hide collapsed constraints
	 */
	private IAction showCollapsedConstraints() {

		final ShowCollapsedConstraintsAction collapseAction = new ShowCollapsedConstraintsAction(viewer, graphicalModel);
		collapseAction.setToolTipText("Show Collapsed Constraints");
		return collapseAction;
	}

	/**
	 * creates the Layout from the toolbar
	 */
	private void createToolBarLayout() {
		manager.add(showCollapsedConstraints());
	}
}
