/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.actions;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;

public class ActionInsertMethod extends ObjectsAction
{
	public ActionInsertMethod(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse, getLabel());
	}

	private static String getLabel()
	{
		return EAM.text("Action|Manage|Insert Method");
	}

	public String getToolTipText()
	{
		return EAM.text("TT|Insert a Method for the selected Indicator");
	}
}
