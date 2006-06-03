/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.project;

import java.util.Arrays;
import java.util.Vector;

import org.conservationmeasures.eam.diagram.nodetypes.NodeType;
import org.conservationmeasures.eam.diagram.nodetypes.NodeTypeDirectThreat;
import org.conservationmeasures.eam.diagram.nodetypes.NodeTypeIntervention;
import org.conservationmeasures.eam.diagram.nodetypes.NodeTypeTarget;
import org.conservationmeasures.eam.objects.ConceptualModelNode;

public class NodePool extends EAMObjectPool
{
	public void put(ConceptualModelNode node)
	{
		put(node.getId(), node);
	}
	
	public ConceptualModelNode find(int id)
	{
		return (ConceptualModelNode)getRawObject(id);
	}

	public ConceptualModelNode[] getInterventions()
	{
		return getNodesOfType(new NodeTypeIntervention());
	}
	
	public ConceptualModelNode[] getDirectThreats()
	{
		return getNodesOfType(new NodeTypeDirectThreat());
	}

	public ConceptualModelNode[] getTargets()
	{
		return getNodesOfType(new NodeTypeTarget());
	}

	private ConceptualModelNode[] getNodesOfType(NodeType type)
	{
		Vector cmNodes = new Vector();
		int[] ids = getIds();
		Arrays.sort(ids);
		for(int i = 0; i < ids.length; ++i)
		{
			ConceptualModelNode cmNode = (ConceptualModelNode)getRawObject(ids[i]);
			if(cmNode.getNodeType().equals(type))
				cmNodes.add(cmNode);
		}
		return (ConceptualModelNode[])cmNodes.toArray(new ConceptualModelNode[0]);
	}
	
}
