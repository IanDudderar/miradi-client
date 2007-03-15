/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Vector;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandBeginTransaction;
import org.conservationmeasures.eam.commands.CommandDeleteObject;
import org.conservationmeasures.eam.commands.CommandEndTransaction;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.FactorSet;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.EAMBaseObject;
import org.conservationmeasures.eam.objects.EAMObject;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.objects.Indicator;
import org.conservationmeasures.eam.objects.KeyEcologicalAttribute;
import org.conservationmeasures.eam.objects.Task;
import org.conservationmeasures.eam.project.ChainManager;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.ObjectsDoer;

public abstract class DeleteAnnotationDoer extends ObjectsDoer
{
	abstract String[] getDialogText();
	abstract String getAnnotationIdListTag();

	public boolean isAvailable()
	{
		return (getObjects().length == 1);
	}

	public void doIt() throws CommandFailedException
	{
		if(!isAvailable())
			return;
		
		String tag = getAnnotationIdListTag();
		String[] dialogText = getDialogText();
		EAMBaseObject annotationToDelete = (EAMBaseObject)getObjects()[0];
		Factor selectedFactor = getSelectedFactor();
		
		deleteAnnotationViaCommands(getProject(), selectedFactor, annotationToDelete, tag, dialogText);
	}

	public static void deleteAnnotationViaCommands(Project project, EAMBaseObject factor, EAMBaseObject annotationToDelete, String annotationIdListTag, String[] confirmDialogText) throws CommandFailedException
	{
		String[] buttons = {"Delete", "Retain", };
		if(!EAM.confirmDialog("Delete", confirmDialogText, buttons))
			return;
	
		project.executeCommand(new CommandBeginTransaction());
		try
		{
			Command[] commands = buildCommandsToDeleteAnnotation(project, factor, annotationIdListTag, annotationToDelete);
			for(int i = 0; i < commands.length; ++i)
				project.executeCommand(commands[i]);
		}
		catch(Exception e)
		{
			EAM.logException(e);
			throw new CommandFailedException(e);
		}
		finally
		{
			project.executeCommand(new CommandEndTransaction());
		}
	}
	
	public static Command[] buildCommandsToDeleteAnnotation(Project project, EAMObject eamObject, String annotationIdListTag, EAMBaseObject annotationToDelete) throws CommandFailedException, ParseException, Exception
	{
		Vector commands = new Vector();
	
		int type = annotationToDelete.getType();
		BaseId idToRemove = annotationToDelete.getId();
		commands.add(CommandSetObjectData.createRemoveIdCommand(eamObject, annotationIdListTag, idToRemove));
		FactorSet nodesThatUseThisAnnotation = new ChainManager(project).findFactorsThatUseThisAnnotation(type, idToRemove);
		if(nodesThatUseThisAnnotation.size() == 1)
		{
			commands.addAll(buildCommandsToDeleteSubTasks(project, type, idToRemove));
			commands.addAll(buildCommandsToDeleteKEAIndicators(project, type, idToRemove));
			commands.addAll(buildCommandsToDeleteSubGoals(project, type, idToRemove));
			commands.addAll(Arrays.asList(annotationToDelete.createCommandsToClear()));
			commands.add(new CommandDeleteObject(type, idToRemove));
		}
		
		return (Command[])commands.toArray(new Command[0]);
	}

	public Factor getSelectedFactor()
	{
		EAMObject selected = getView().getSelectedObject();
		if(selected == null)
			return null;
		
		if(selected.getType() != ObjectType.FACTOR)
			return null;
		
		return (Factor)selected;
	}
	
	
	private static Vector buildCommandsToDeleteKEAIndicators(Project project, int type, BaseId id) throws Exception
	{
		Vector commands = new Vector();
		if (!(type == ObjectType.KEY_ECOLOGICAL_ATTRIBUTE))
			return commands;
	
		KeyEcologicalAttribute kea = (KeyEcologicalAttribute)project.findObject(type, id);
		commands.addAll(Arrays.asList(kea.createCommandsToClear()));
		
		IdList indicatorList = kea.getIndicatorIds();
		for (int i  = 0; i < indicatorList.size(); i++)
		{
			EAMBaseObject thisAnnotation = (EAMBaseObject)project.findObject(ObjectType.INDICATOR,  indicatorList.get(i));
			Command[] deleteCommands = DeleteIndicator.buildCommandsToDeleteAnnotation(project, kea, KeyEcologicalAttribute.TAG_INDICATOR_IDS, thisAnnotation);
			commands.addAll(Arrays.asList(deleteCommands));
		}

		return commands;
	}
	
	private static Vector buildCommandsToDeleteSubTasks(Project project, int type, BaseId id) throws Exception
	{
		Vector commands = new Vector();
		if (type != ObjectType.INDICATOR)
			return commands;
	
		Indicator indicator = (Indicator)project.findObject(type, id);
		IdList subtaskList = indicator.getTaskIdList();
		for (int i  = 0; i < subtaskList.size(); i++)
		{
			Task taskToDelete = (Task)project.findObject(ObjectType.TASK, subtaskList.get(i));
			Vector returnedDeleteCommands = taskToDelete.getDeleteSelfAndSubtasksCommands(project);
			
			commands.addAll(returnedDeleteCommands);
		}
		
		return commands;
	}
	
	
	private static Vector buildCommandsToDeleteSubGoals(Project project, int type, BaseId id) throws Exception
	{
		Vector commands = new Vector();
		if (!(type == ObjectType.INDICATOR))
			return commands;
		
		Indicator indicator = (Indicator)project.findObject(type, id);
		commands.addAll(Arrays.asList(indicator.createCommandsToClear()));
		
		IdList goalList = new IdList(indicator.getData(Indicator.TAG_GOAL_IDS));
		for (int i  = 0; i < goalList.size(); i++)
		{
			EAMBaseObject thisAnnotation = (EAMBaseObject)project.findObject(ObjectType.GOAL,  goalList.get(i));
			Command[] deleteCommands = DeleteIndicator.buildCommandsToDeleteAnnotation(project, indicator, Indicator.TAG_GOAL_IDS, thisAnnotation);
			commands.addAll(Arrays.asList(deleteCommands));
		}

		return commands;
	}
}
