package org.conservationmeasures.eam.actions;

import javax.swing.Icon;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;

public class ActionWizardNext extends ViewAction
{
	public ActionWizardNext(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse, getLabel(), (Icon)null);
	}

	private static String getLabel()
	{
		return EAM.text("Action|Next >");
	}

	public String getToolTipText()
	{
		return EAM.text("TT|Go to the Next wizard step");
	}
	
}
