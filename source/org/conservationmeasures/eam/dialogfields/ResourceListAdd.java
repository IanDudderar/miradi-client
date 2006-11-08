/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.dialogfields;

import java.text.ParseException;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objects.ProjectMetadata;
import org.conservationmeasures.eam.views.ObjectsDoer;

public class ResourceListAdd extends ObjectsDoer
{
	public boolean isAvailable()
	{
		if(getObjects().length == 0)
			return false;
		
		return true;
	}

	public void doIt() throws CommandFailedException
	{
		if(!isAvailable())
			return;
		
		ProjectMetadata metadata = getProject().getMetadata();
		BaseId selectedId = getSelectedId();
		try
		{
			Command cmd = CommandSetObjectData.createAppendIdCommand(metadata, ProjectMetadata.TAG_TEAM_RESOURCE_IDS, selectedId);
			getProject().executeCommand(cmd);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			EAM.errorDialog(EAM.text("Text|Unknown error prevented adding this person to the team"));
		}
	}

}
