/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.monitoring;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.conservationmeasures.eam.icons.IndicatorIcon;
import org.conservationmeasures.eam.icons.ObjectiveIcon;
import org.conservationmeasures.eam.objecthelpers.ObjectType;

import com.java.sun.jtreetable.JTreeTable;

public class MonitoringTreeTable extends JTreeTable
{
	public MonitoringTreeTable(MonitoringModel monitoringModelToUse)
	{
		super(monitoringModelToUse);
		monitoringModel = monitoringModelToUse;
		setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		getTree().setShowsRootHandles(true);
		getTree().setRootVisible(false);
		getTree().setCellRenderer(new Renderer());
		expandEverything();
	}

	void expandEverything()
	{
		MonitoringNode root = (MonitoringNode)getTreeTableModel().getRoot();
		TreePath rootPath = new TreePath(root);
		expandNode(rootPath);
	}
	
	public MonitoringModel getTreeTableModel()
	{
		return monitoringModel;
	}

	private void expandNode(TreePath thisPath)
	{
		MonitoringNode topLevelObject = (MonitoringNode)thisPath.getLastPathComponent();
		getTree().expandPath(thisPath);
		for(int childIndex = 0; childIndex < topLevelObject.getChildCount(); ++childIndex)
		{
			MonitoringNode secondLevelObject = topLevelObject.getChild(childIndex);
			TreePath secondLevelPath = thisPath.pathByAddingChild(secondLevelObject);
			expandNode(secondLevelPath);
		}
	}

	static class Renderer extends DefaultTreeCellRenderer
	{
		public Renderer()
		{
			
			objectiveRenderer = new DefaultTreeCellRenderer();
			objectiveRenderer.setClosedIcon(new ObjectiveIcon());
			objectiveRenderer.setOpenIcon(new ObjectiveIcon());
			objectiveRenderer.setLeafIcon(new ObjectiveIcon());

			indicatorRenderer = new DefaultTreeCellRenderer();
			indicatorRenderer.setClosedIcon(new IndicatorIcon());
			indicatorRenderer.setOpenIcon(new IndicatorIcon());
			indicatorRenderer.setLeafIcon(new IndicatorIcon());
			
			defaultRenderer = new DefaultTreeCellRenderer();
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocusToUse)
		{
			TreeCellRenderer renderer = defaultRenderer;
			
			MonitoringNode node = (MonitoringNode)value;
			if(node.getType() == ObjectType.INDICATOR)
				renderer = indicatorRenderer;
			if(node.getType() == ObjectType.OBJECTIVE)
				renderer = objectiveRenderer;
			
			return renderer.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		}
		
		DefaultTreeCellRenderer objectiveRenderer;
		DefaultTreeCellRenderer indicatorRenderer;
		DefaultTreeCellRenderer defaultRenderer;
	}
	
	MonitoringModel monitoringModel;
}
