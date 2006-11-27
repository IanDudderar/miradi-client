/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.dialogfields;

import java.text.ParseException;

import javax.swing.JComponent;

import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.project.Project;

public class ObjectResourceListField extends ObjectDataInputField
{
	public ObjectResourceListField(Actions actionsToUse, Project projectToUse, int objectTypeToUse, BaseId objectIdToUse, String tagToUse)
	{
		super(projectToUse, objectTypeToUse, objectIdToUse, tagToUse);
		project = projectToUse;
		actions = actionsToUse;
		listComponent = new ResourceListEditorComponent(project, actions);
		idList = new IdList();
	}
	
	public JComponent getComponent()
	{
		return listComponent;
	}

	public String getText()
	{
		return idList.toString();
	}

	public void setText(String idListToUse)
	{
		try
		{
			idList =  new IdList(idListToUse);
		}
		catch(ParseException e)
		{
			EAM.logException(e);
			idList = new IdList();
		}
		rebuildComponent();
	}

	private void rebuildComponent()
	{
		listComponent.setList(idList);
	}

	private IdList idList;
	private Actions actions;
	private Project project;
	private ResourceListEditorComponent listComponent;
}
