/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.diagram;

import org.conservationmeasures.eam.commands.CommandDiagramAddLinkage;
import org.conservationmeasures.eam.commands.CommandDiagramRemoveLinkage;
import org.conservationmeasures.eam.commands.CommandDiagramRemoveNode;
import org.conservationmeasures.eam.diagram.nodes.DiagramNode;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.ModelNodeId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.project.ProjectForTesting;
import org.conservationmeasures.eam.testall.EAMTestCase;

public class TestDelete extends EAMTestCase
{
	public TestDelete(String name)
	{
		super(name);
	}
	
	public void testBasics() throws Exception
	{
		ProjectForTesting project = new ProjectForTesting(getName());
		DiagramModel model = project.getDiagramModel();
		
		ModelNodeId interventionId = CommandDiagramRemoveNode.createNode(project, DiagramNode.TYPE_INTERVENTION, BaseId.INVALID);
		DiagramNode intervention = model.getNodeById(interventionId);
		ModelNodeId factorId = CommandDiagramRemoveNode.createNode(project, DiagramNode.TYPE_FACTOR, BaseId.INVALID);
		DiagramNode factor = model.getNodeById(factorId);
		CommandDiagramAddLinkage link = new CommandDiagramAddLinkage(interventionId, factorId);
		link.execute(project);
		BaseId linkageId = link.getLinkageId();
		
		assertTrue("linkage not found?", model.hasLinkage(intervention, factor));
		
		CommandDiagramRemoveLinkage delete = new CommandDiagramRemoveLinkage(linkageId);
		delete.execute(project);
		assertFalse("linkage not deleted?", model.hasLinkage(intervention, factor));
		
		EAM.setLogToString();
		try
		{
			delete.execute(project);
			fail("should have thrown for deleting non-existant linkage");
		}
		catch(CommandFailedException ignoreExpected)
		{
		}
		EAM.setLogToConsole();
		
		CommandDiagramRemoveNode deleteNode = new CommandDiagramRemoveNode(factorId);
		deleteNode.execute(project);
		assertFalse("node not deleted?", model.isNodeInProject(factor));

		EAM.setLogToString();
		try
		{
			deleteNode.execute(project);
			fail("should have thrown for deleting non-existant linkage");
		}
		catch(CommandFailedException ignoreExpected)
		{
		}
		EAM.setLogToConsole();
		
		project.close();
	}
}
