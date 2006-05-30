/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.diagram;

import java.awt.Point;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandBeginTransaction;
import org.conservationmeasures.eam.commands.CommandDiagramMove;
import org.conservationmeasures.eam.commands.CommandEndTransaction;
import org.conservationmeasures.eam.commands.CommandLinkNodes;
import org.conservationmeasures.eam.commands.CommandSetNodeName;
import org.conservationmeasures.eam.diagram.nodes.DiagramNode;
import org.conservationmeasures.eam.diagram.nodetypes.NodeType;
import org.conservationmeasures.eam.exceptions.CommandFailedException;

abstract public class InsertNode extends LocationDoer
{
	abstract public NodeType getTypeToInsert();
	abstract public String getInitialText();

	public boolean isAvailable()
	{
		return getProject().isOpen();
	}

	public void doIt() throws CommandFailedException
	{
		try
		{
			DiagramNode[] selectedNodes = getProject().getOnlySelectedNodes();
			int id = insertNodeItself();
			if(selectedNodes.length > 0)
				linkToPreviouslySelectedNodes(id, selectedNodes);
			
			launchPropertiesEditor(id);
		}
		catch (Exception e)
		{
			throw new CommandFailedException(e);
		}
	}
	
	void launchPropertiesEditor(int id) throws Exception, CommandFailedException
	{
		DiagramNode newNode = getProject().getDiagramModel().getNodeById(id);
		getDiagramView().getPropertiesDoer().doNodeProperties(newNode);
	}
	
	private int insertNodeItself() throws Exception
	{
		getProject().executeCommand(new CommandBeginTransaction());
		NodeType nodeType = getTypeToInsert();
		int id = getProject().createNode(nodeType);
		
		Command setNameCommand = new CommandSetNodeName(id, getInitialText());
		getProject().executeCommand(setNameCommand);
		
		Point createAt = getLocation();
		//Snap to Grid
		int deltaX = createAt.x;
		int deltaY = createAt.y;
		deltaX -= deltaX % getProject().getGridSize(); 
		deltaY -= deltaY % getProject().getGridSize();
		
		Command moveCommand = new CommandDiagramMove(deltaX, deltaY, new int[] {id});
		getProject().executeCommand(moveCommand);
		
		doExtraSetup(id);
		
		getProject().executeCommand(new CommandEndTransaction());
		
		return id;
	}
	
	private void linkToPreviouslySelectedNodes(int newlyInsertedId, DiagramNode[] nodesToLinkTo) throws CommandFailedException
	{
		getProject().executeCommand(new CommandBeginTransaction());
		for(int i = 0; i < nodesToLinkTo.length; ++i)
		{
			CommandLinkNodes cmd = new CommandLinkNodes(newlyInsertedId, nodesToLinkTo[i].getId());
			getProject().executeCommand(cmd);
		}
		getProject().executeCommand(new CommandEndTransaction());
	}
	
	void doExtraSetup(int id) throws CommandFailedException
	{
		
	}
	
	public DiagramView getDiagramView()
	{
		return (DiagramView)getView();
	}
}
