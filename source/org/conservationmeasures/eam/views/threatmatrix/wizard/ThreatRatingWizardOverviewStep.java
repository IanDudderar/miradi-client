/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.threatmatrix.wizard;


public class ThreatRatingWizardOverviewStep extends ThreatRatingWizardStep
{
	public ThreatRatingWizardOverviewStep(ThreatRatingWizardPanel wizardToUse) 
	{
		super(wizardToUse);
	}

	public String getResourceFileName()
	{
		return HTML_FILENAME;
	}

	String HTML_FILENAME = "ThreatRatingOverview.html";

}


