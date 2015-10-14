/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2015  FeatureIDE team, University of Magdeburg, Germany
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
package de.ovgu.featureide.featurehouse.refactoring.pullUp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.core.signature.ProjectSignatures;
import de.ovgu.featureide.core.signature.base.AbstractClassSignature;
import de.ovgu.featureide.core.signature.base.AbstractSignature;
import de.ovgu.featureide.featurehouse.refactoring.RefactoringUtil;
import de.ovgu.featureide.featurehouse.signature.fuji.FujiMethodSignature;

/**
 * TODO description
 * 
 * @author steffen
 */
public abstract class PullUpRefactoring<T extends AbstractSignature> extends Refactoring {

	protected final IFeatureProject featureProject;
	protected final T selectedElement;
	protected final String file;
	protected AbstractClassSignature destinationType;
	protected AbstractSignature[] pullUpSignatures;
	
	public PullUpRefactoring(T selection, IFeatureProject featureProject, String file) {
		this.selectedElement = selection;
		this.featureProject = featureProject;
		this.file = file;
	}
	
	public abstract FujiMethodSignature[] getPullableElements();
	
	public AbstractClassSignature[] getCandidateTypes(final RefactoringStatus status) {
	
		final Set<AbstractClassSignature> result = new HashSet<>();
		final AbstractClassSignature clazz = selectedElement.getParent();
		final Set<String> superClasses = clazz.getExtendList();
		
		if (superClasses.size() == 0) //$NON-NLS-1$
			status.addFatalError(RefactoringCoreMessages.PullUPRefactoring_not_java_lang_object);
		
		final Map<String, AbstractClassSignature> classes = RefactoringUtil.getClasses(featureProject.getProjectSignatures());
	
		for (String superClass : superClasses) {
			if (classes.containsKey(superClass))
				result.add(classes.get(superClass));
		}

		return result.toArray(new AbstractClassSignature[result.size()]);
	}

	public void setDestinationType(AbstractClassSignature destination) {
		this.destinationType = destination;
	}
	
	public AbstractClassSignature getDestinationType() {
		return destinationType;
	}

	public void setPullUpSignatures(AbstractSignature[] pullUpSignatures) {
		this.pullUpSignatures = pullUpSignatures;	
	}

	public AbstractSignature[] getPullUpSignatures() {
		return pullUpSignatures;
	}
	
	public ProjectSignatures getProjectSignatures() {
		return featureProject.getProjectSignatures();
	}

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

}