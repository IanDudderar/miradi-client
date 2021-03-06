/* 
Copyright 2005-2018, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

This file is part of Miradi

Miradi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3, 
as published by the Free Software Foundation.

Miradi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Miradi.  If not, see <http://www.gnu.org/licenses/>. 
*/ 
package org.miradi.dialogs.stress;

import javax.swing.Icon;
import javax.swing.JDialog;

import org.miradi.actions.jump.ActionJumpTargetStressesStep;
import org.miradi.dialogs.base.ObjectListManagementPanel;
import org.miradi.icons.StressIcon;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;

public class StressListManagementPanel extends ObjectListManagementPanel
{
	public static StressListManagementPanel createStressManagementPanelWithVisibilityPanel(MainWindow mainWindowToUse, ORef nodeRef) throws Exception
	{
		StressPropertiesPanel stressPropertiesPanel = StressPropertiesPanel.createWithVisibilityPanel(mainWindowToUse);

		return new StressListManagementPanel(mainWindowToUse, nodeRef, stressPropertiesPanel);
	}
	
	public static StressListManagementPanel createStressManagementPanelWithoutVisibilityPanel(JDialog parent, MainWindow mainWindowToUse, ORef nodeRef) throws Exception
	{
		StressPropertiesPanel stressPropertiesPanel = StressPropertiesPanel.createWithoutVisibilityPanel(parent, mainWindowToUse);

		return new StressListManagementPanel(mainWindowToUse, nodeRef, stressPropertiesPanel);
	}
	
	private StressListManagementPanel(MainWindow mainWindowToUse, ORef nodeRef, StressPropertiesPanel stressPropertiesPanel) throws Exception
	{
		super(mainWindowToUse, new StressListTablePanel(mainWindowToUse, nodeRef), stressPropertiesPanel);
	}

	@Override
	public String getSplitterDescription()
	{
		return getPanelDescription() + SPLITTER_TAG;
	}
	
	@Override
	public String getPanelDescription()
	{
		return PANEL_DESCRIPTION;
	}
	
	@Override
	public Icon getIcon()
	{
		return new StressIcon();
	}
	
	@Override
	public Class getJumpActionClass()
	{
		return ActionJumpTargetStressesStep.class;
	}
	
	private static String PANEL_DESCRIPTION = EAM.text("Tab|Stresses"); 
}
