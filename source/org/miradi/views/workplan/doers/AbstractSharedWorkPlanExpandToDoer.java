/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
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

package org.miradi.views.workplan.doers;

import org.miradi.views.MiradiTabContentsPanelInterface;
import org.miradi.views.targetviability.doers.AbstractExpandToDoer;
import org.miradi.views.workplan.SharedWorkPlanManagementPanel;

public abstract class AbstractSharedWorkPlanExpandToDoer extends AbstractExpandToDoer
{
	@Override
	public boolean isAvailable()
	{
		if(!super.isAvailable())
			return false;

		MiradiTabContentsPanelInterface currentTab = getView().getCurrentTabPanel();
		return currentTab instanceof SharedWorkPlanManagementPanel;
	}
}