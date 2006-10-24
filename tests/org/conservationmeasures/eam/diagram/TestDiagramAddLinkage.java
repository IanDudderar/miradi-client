/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.diagram;

import org.conservationmeasures.eam.commands.CommandDiagramAddNode;
import org.conservationmeasures.eam.commands.CommandDiagramAddLinkage;
import org.conservationmeasures.eam.diagram.nodes.DiagramLinkage;
import org.conservationmeasures.eam.diagram.nodes.DiagramNode;
import org.conservationmeasures.eam.ids.ModelNodeId;
import org.conservationmeasures.eam.project.ProjectForTesting;
import org.conservationmeasures.eam.testall.EAMTestCase;

public class TestDiagramAddLinkage extends EAMTestCase
{
	public TestDiagramAddLinkage(String name)
	{
		super(name);
	}

	public void testLinkNodes() throws Exception
	{
		ProjectForTesting project = new ProjectForTesting(getName());
		DiagramModel model = project.getDiagramModel();

		CommandDiagramAddNode insertFactor = new CommandDiagramAddNode(DiagramNode.TYPE_FACTOR);
		insertFactor.execute(project);
		ModelNodeId factorId = insertFactor.getId();
		DiagramNode factor = model.getNodeById(factorId);
		CommandDiagramAddNode insertTarget = new CommandDiagramAddNode(DiagramNode.TYPE_TARGET);
		insertTarget.execute(project);
		ModelNodeId targetId = insertTarget.getId();
		DiagramNode target = model.getNodeById(targetId);
		
		CommandDiagramAddLinkage command = new CommandDiagramAddLinkage(factorId, targetId);
		command.execute(project);
		DiagramLinkage linkage = model.getLinkageById(command.getLinkageId());

		assertEquals("not from factor?", factor, linkage.getFromNode());
		assertEquals("not to target?", target, linkage.getToNode());
		
		project.close();
	}
}
