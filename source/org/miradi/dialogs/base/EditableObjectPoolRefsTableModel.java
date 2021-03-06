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

package org.miradi.dialogs.base;

import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.project.Project;
import org.miradi.questions.TaglessChoiceItem;

abstract public class EditableObjectPoolRefsTableModel extends EditableObjectRefsTableModel
{
	public EditableObjectPoolRefsTableModel(Project projectToUse)
	{
		super(projectToUse);
	}
	
	@Override
	protected ORefList extractOutEditableRefs(ORefList hierarchyToSelectedRef)
	{	
		return getProject().getPool(getObjectType()).getRefList();
	}
	
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		BaseObject baseObject = getBaseObjectForRowColumn(rowIndex, columnIndex);
		return new TaglessChoiceItem(baseObject.getData(getColumnTags()[columnIndex]));
	}
	
	@Override
	public void setValueAt(Object value, int row, int column)
	{
		if (value == null)
			return;
		
		ORef ref = getBaseObjectForRowColumn(row, column).getRef();
		setDataValue(ref, column, value.toString());
	}
	
	private void setDataValue(ORef ref, int column, String value)
	{
		setValueUsingCommand(ref, getColumnTag(column), value);
	}
	
	public boolean isLabelColumn(int modelColumn)
	{
		return isColumnForTag(modelColumn, BaseObject.TAG_LABEL);
	}
}
