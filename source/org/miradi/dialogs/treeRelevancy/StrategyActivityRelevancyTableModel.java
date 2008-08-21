/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
(on behalf of the Conservation Measures Partnership, "CMP") and 
Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 

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
package org.miradi.dialogs.treeRelevancy;

import org.miradi.dialogs.base.SingleBooleanColumnEditableModel;
import org.miradi.dialogs.tablerenderers.RowColumnBaseObjectProvider;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.RelevancyOverrideSet;
import org.miradi.objects.Objective;
import org.miradi.project.Project;

public class StrategyActivityRelevancyTableModel extends SingleBooleanColumnEditableModel
{
	public StrategyActivityRelevancyTableModel(Project projectToUse, RowColumnBaseObjectProvider providerToUse, Objective objectiveAsParentToUse)
	{
		super(projectToUse, providerToUse);
		
		objectiveAsParent = objectiveAsParentToUse;
	}

	public String getColumnName(int column)
	{
		return EAM.text("Is Relevant");
	}	
	
	public void setValueAt(Object value, int row, int column)
	{
		if (value == null)
			return;

		try
		{
			ORefList selectedRefs = getCurrentlyCheckedRefs((Boolean) value, row);
			RelevancyOverrideSet relevancySet = objectiveAsParent.getCalculatedRelevantStrategyActivityOverrides(selectedRefs);	
			setValueUsingCommand(objectiveAsParent.getRef(), Objective.TAG_RELEVANT_STRATEGY_ACTIVITY_SET, relevancySet.toString());
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
	}

	private ORefList getRelevantStrategyActivityRefs() throws Exception
	{
		return new ORefList(objectiveAsParent.getRelevantStrategyAndActivityRefs());
	}
	
	protected ORefList getCurrentRefList() throws Exception
	{
		return getRelevantStrategyActivityRefs();
	}
	
	private Objective objectiveAsParent;
}
