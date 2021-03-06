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

import org.miradi.commands.Command;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.ids.BaseId;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;

abstract public class EditableObjectTableModel extends AbstractObjectTableModel
{
	public EditableObjectTableModel(Project projectToUse)
	{
		super(projectToUse);
	}
	
	public BaseObject getBaseObjectForRow(int row)
	{
		return getBaseObjectForRowColumn(row, 0);
	}
	
	protected ObjectManager getObjectManager()
	{
		return getProject().getObjectManager();
	}

	public void setValueUsingCommand(ORef refToUse, String fieldTag, ORef refToSave)
	{
		setValueUsingCommand(refToUse, fieldTag, refToSave.toString());
	}
	
	public void setValueUsingCommand(ORef refToUse, String fieldTag, BaseId idToSave)
	{
		setValueUsingCommand(refToUse, fieldTag, idToSave.toString());
	}
	
	public void setValueUsingCommand(ORef refToUse, String fieldTag, ChoiceItem choiceToSave)
	{
		setValueUsingCommand(refToUse, fieldTag, choiceToSave.getCode());
	}
	
	public void setChoiceValueUsingCommand(BaseObject baseObject, final String tag, ChoiceItem choiceItem)
	{
		setValueUsingCommand(baseObject.getRef(), tag, choiceItem.getCode());
	}
	
	public void setValueUsingCommand(ORef  refToUse, String fieldTag, String valueToSave)
	{
		try
		{
			Command command = new CommandSetObjectData(refToUse, fieldTag, valueToSave);
			getProject().executeCommand(command);
		}
		catch(CommandFailedException e)
		{
			EAM.logException(e);
		}
	}
	
	@Override
	public void setRowObjectRefs(ORefList objectRowRefs)
	{
	}
	
	@Override
	protected ORefList getRowObjectRefs()
	{
		return new ORefList();
	}

	public BaseObject getObjectFromRow(int row) throws RuntimeException
	{
		try
		{
			ORef rowObjectRef = getRowObjectRefs().get(row);
			BaseObject rowObject = getProject().findObject(rowObjectRef);
			if(rowObject == null)
			{
				EAM.logWarning("ObjectTableModel.getObjectFromRow: Missing object: " + rowObjectRef);
				EAM.logStackTrace();
			}
			return rowObject;
		}
		catch(Exception e)
		{
			EAM.logException(e);
			throw new RuntimeException("model getObjectFromRow error. row = " + row);
		}
	}

	public int findRowObject(BaseId id)
	{
		for(int row = 0; row < getRowCount(); ++row)
		{
			if(getObjectFromRow(row).getId().equals(id))
				return row;
		}

		return -1;
	}

	abstract public void setObjectRefs(ORefList hierarchyToSelectedRef);
}
