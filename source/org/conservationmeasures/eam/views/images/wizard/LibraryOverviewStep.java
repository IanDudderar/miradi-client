/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 

package org.conservationmeasures.eam.views.images.wizard;

import org.conservationmeasures.eam.views.library.LibraryView;
import org.conservationmeasures.eam.wizard.SplitWizardStep;
import org.conservationmeasures.eam.wizard.WizardPanel;

public class LibraryOverviewStep extends SplitWizardStep
{
	public LibraryOverviewStep(WizardPanel wizardToUse)
	{
		super(wizardToUse, LibraryView.getViewName());
	}

}
