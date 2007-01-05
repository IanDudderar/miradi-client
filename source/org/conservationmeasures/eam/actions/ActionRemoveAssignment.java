/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.actions;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;

public class ActionRemoveAssignment extends ObjectsAction
{
	public ActionRemoveAssignment(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse, getLabel());
	}

	public static String getLabel()
	{
		return EAM.text("Action|Remove Resource");
	}

	public String getToolTipText()
	{
		return EAM.text("TT|Remove resource from list");
	}
}
