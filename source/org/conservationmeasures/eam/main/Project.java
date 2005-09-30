/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.main;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandDoNothing;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.exceptions.UnknownCommandException;
import org.conservationmeasures.eam.views.NoProjectView;
import org.conservationmeasures.eam.views.diagram.DiagramView;

public class Project extends BaseProject
{
	public Project() throws IOException
	{
		storage = new FileStorage();
	}
	
	public void load(MainWindow mainWindow, File projectDirectory) throws IOException, CommandFailedException, UnknownCommandException
	{
		getDiagramModel().clear();
		getStorage().setDirectory(projectDirectory);
		if(!getStorage().exists())
			getStorage().createEmpty();
		
		Vector commands = getStorage().load();
		for(int i=0; i < commands.size(); ++i)
		{
			Command command = (Command)commands.get(i);
			EAM.logDebug("Executing " + command);
			replayCommand(command);
			getStorage().addCommandWithoutSaving(command);
		}
		
		if(currentView.length() == 0)
		{
			currentView = DiagramView.getViewName();
			fireSwitchToView(currentView);
		}
		
		fireCommandExecuted(new CommandDoNothing());
	}

	public boolean isOpen()
	{
		return getStorage().hasFile();
	}
	
	public void close()
	{
		try
		{
			getStorage().close();
		}
		catch (IOException e)
		{
			EAM.logException(e);
		}
		currentView = NoProjectView.getViewName();
		fireSwitchToView(currentView);
	}

	public String getName()
	{
		if(isOpen())
			return getStorage().getName();
		return EAM.text("[No Project]");
	}

	private FileStorage getStorage()
	{
		return (FileStorage)storage;
	}
}
