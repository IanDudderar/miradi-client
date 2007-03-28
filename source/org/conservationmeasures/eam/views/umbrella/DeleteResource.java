/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.umbrella;

import java.util.Vector;

import org.conservationmeasures.eam.commands.CommandBeginTransaction;
import org.conservationmeasures.eam.commands.CommandDeleteObject;
import org.conservationmeasures.eam.commands.CommandEndTransaction;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objects.Assignment;
import org.conservationmeasures.eam.objects.ProjectResource;
import org.conservationmeasures.eam.project.ChainManager;
import org.conservationmeasures.eam.views.ObjectsDoer;

public class DeleteResource extends ObjectsDoer
{
	public boolean isAvailable()
	{
		return (getObjects().length == 1);
	}

	public void doIt() throws CommandFailedException
	{
		if(!isAvailable())
			return;
		
		ProjectResource resource = (ProjectResource)getObjects()[0];
		BaseId idToRemove = resource.getId();
		Vector dialogText = new Vector();
		ChainManager chainManager = getProject().getChainManager();
		Vector allThatUseThisResource = chainManager.getRefferedInObject(resource.getRef());

		//TODO fix dialog text
		if (allThatUseThisResource.size() > 0)
			dialogText.add("This project resource is being used in the Work Plan and Financial Budget.");
		
		dialogText.add("\nAre you sure you want to delete this resource?");
		String[] buttons = {"Yes", "No", };
		if(!EAM.confirmDialog("Delete Resource", (String[])dialogText.toArray(new String[0]), buttons))
			return;

		clearAllAssignmentResources(allThatUseThisResource);
		try
		{
			getProject().executeCommand(new CommandBeginTransaction());
			try
			{
				int type = resource.getType();
				BaseId id = idToRemove;
				getProject().executeCommands(resource.createCommandsToClear());
				getProject().executeCommand(new CommandDeleteObject(type, id));
			}
			finally
			{
				getProject().executeCommand(new CommandEndTransaction());
			}
		}
		catch(CommandFailedException e)
		{
			throw(e);
		}
		catch(Exception e)
		{
			EAM.logException(e);
			throw new CommandFailedException(e);
		}
	}

	private void clearAllAssignmentResources(Vector allThatUseThisResource)
	{
		for (int i = 0; i < allThatUseThisResource.size(); i++)
		{
			Assignment assignment = (Assignment) allThatUseThisResource.get(i);
			assignment.setResourceId(BaseId.INVALID);
		}
	}

}
