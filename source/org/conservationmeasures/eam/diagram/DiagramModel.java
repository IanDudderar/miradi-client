/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.diagram;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.conservationmeasures.eam.diagram.nodes.EAMGraphCell;
import org.conservationmeasures.eam.diagram.nodes.Linkage;
import org.conservationmeasures.eam.diagram.nodes.Node;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultGraphModel;

public class DiagramModel extends DefaultGraphModel
{
	public DiagramModel()
	{
		cellInventory = new CellInventory();
	}
	
	public void clear()
	{
		while(getRootCount() > 0)
			remove(new Object[] {getRootAt(0)});
		cellInventory.clear();
	}

	public Node createNode(int nodeType) throws Exception
	{
		return createNodeAtId(nodeType, Node.INVALID_ID);
	}
	
	public Node createNodeAtId(int nodeType, int id) throws Exception
	{
		Node node = new Node(nodeType);
		Object[] nodes = new Object[] {node};
		Hashtable nestedAttributeMap = node.getNestedAttributeMap();
		insert(nodes, nestedAttributeMap, null, null, null);
		cellInventory.add(node, id);
		notifyListeners(createDiagramModelEvent(node), new ModelEventNotifierNodeAdded());
		return node;
	}

	private DiagramModelEvent createDiagramModelEvent(EAMGraphCell node) throws Exception 
	{
		return new DiagramModelEvent(this, node);
	}
	
	public void addDiagramModelListener(DiagramModelListener listener)
	{
		diagramModelListenerList.add(listener);
	}
	
    public void removeMyEventListener(DiagramModelListener listener) 
    {
    	diagramModelListenerList.remove(listener);
    }	
    
    void notifyListeners(DiagramModelEvent event, ModelEventNotifier eventNotifier) 
    {
        for (int i=0; i<diagramModelListenerList.size(); ++i) 
        {
        	eventNotifier.doNotify((DiagramModelListener)diagramModelListenerList.get(i), event);
        }                
    }
	
    public void deleteNode(Node nodeToDelete) throws Exception
	{
		notifyListeners(createDiagramModelEvent(nodeToDelete), new ModelEventNotifierNodeDeleted());

		Object[] nodes = new Object[]{nodeToDelete};
		remove(nodes);
		cellInventory.remove(nodeToDelete);
	}
	
	public Linkage createLinkage(int linkageId, int linkFromId, int linkToId) throws Exception
	{
		Node fromNode = getNodeById(linkFromId);
		Node toNode = getNodeById(linkToId);

		Linkage linkage = new Linkage(fromNode, toNode);
		Object[] linkages = new Object[]{linkage};
		Map nestedMap = linkage.getNestedAttributeMap();
		ConnectionSet cs = linkage.getConnectionSet();
		insert(linkages, nestedMap, cs, null, null);
		cellInventory.add(linkage, linkageId);
		notifyListeners(createDiagramModelEvent(linkage), new ModelEventNotifierLinkageAdded());
		
		return linkage;
	}
	
	public void deleteLinkage(Linkage linkageToDelete) throws Exception
	{
		notifyListeners(createDiagramModelEvent(linkageToDelete), new ModelEventNotifierLinkageDeleted());
		Object[] linkages = new Object[]{linkageToDelete};
		remove(linkages);
		cellInventory.remove(linkageToDelete);
	}
	
	public boolean hasLinkage(Node fromNode, Node toNode)
	{
		Vector allLinkages = cellInventory.getAllLinkages();
		for(int i=0; i < allLinkages.size(); ++i)
		{
			Linkage linkage = (Linkage)allLinkages.get(i);
			Node thisFromNode = linkage.getFromNode();
			Node thisToNode = linkage.getToNode();
			if(thisFromNode.equals(fromNode) && thisToNode.equals(toNode))
				return true;
			
			if(thisFromNode.equals(toNode) && thisToNode.equals(fromNode))
				return true;
		}
		
		return false;
	}
	
	public int getNodeCount()
	{
		return getAllNodes().size();
	}
	
	public int getLinkageCount()
	{
		return getAllLinkages().size();
	}

	public Set getLinkages(Node node)
	{
		return getEdges(this, new Object[] {node});
	}
	
	public void updateCell(EAMGraphCell nodeToUpdate) throws Exception
	{
		edit(nodeToUpdate.getNestedAttributeMap(), null, null, null);
		notifyListeners(createDiagramModelEvent(nodeToUpdate), new ModelEventNotifierNodeChanged());
	}
	
	public Node getNodeById(int id) throws Exception
	{
		Node node = cellInventory.getNodeById(id);
		if(node == null)
			throw new Exception("Node doesn't exist, id: " + id);
		return node;
	}

	public Linkage getLinkageById(int id) throws Exception
	{
		Linkage linkage = cellInventory.getLinkageById(id);
		if(linkage == null)
			throw new Exception("Linkage doesn't exist, id: " + id);
		return linkage;
	}
	
	public boolean isNodeInProject(Node node)
	{
		return (cellInventory.getNodeById(node.getId()) != null);
	}

	public boolean isLinkageInProject(Linkage linkage)
	{
		return (cellInventory.getLinkageById(linkage.getId()) != null);
	}

	public Vector getAllNodes()
	{
		return cellInventory.getAllNodes();
	}
	
	public Vector getAllLinkages()
	{
		return cellInventory.getAllLinkages();
	}
	

	CellInventory cellInventory;
	protected List diagramModelListenerList = new ArrayList();
}

