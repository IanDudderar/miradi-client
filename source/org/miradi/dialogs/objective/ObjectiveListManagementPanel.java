/* 
Copyright 2005-2017, Foundations of Success, Bethesda, Maryland
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
package org.miradi.dialogs.objective;

import javax.swing.Icon;

import org.miradi.actions.Actions;
import org.miradi.actions.jump.ActionJumpStrategicPlanDevelopObjectivesStep;
import org.miradi.dialogs.base.ObjectListManagementPanel;
import org.miradi.icons.ObjectiveIcon;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;

public class ObjectiveListManagementPanel extends ObjectListManagementPanel
{
	public ObjectiveListManagementPanel(MainWindow mainWindowToUse, ORef nodeRef, Actions actions, ObjectiveListTablePanel objectListPanel) throws Exception
	{
		super(mainWindowToUse, objectListPanel, new ObjectivePropertiesPanel(mainWindowToUse.getProject(), actions));
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
		return new ObjectiveIcon();
	}
	
	
	@Override
	public Class getJumpActionClass()
	{
		return ActionJumpStrategicPlanDevelopObjectivesStep.class;
	}
	
	
	private static String PANEL_DESCRIPTION = EAM.text("Tab|Objectives"); 
}
