/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.summary.wizard;

import org.conservationmeasures.eam.views.umbrella.WizardPanel;

public class SummaryWizardWelcomeStep extends SummaryWizardStep
{
	public SummaryWizardWelcomeStep(WizardPanel wizardToUse)
	{
		super(wizardToUse);
	}

	public String getResourceFileName()
	{
		return HTML_FILENAME;
	}
	
	String HTML_FILENAME = "WelcomeStep.html";
	
}
