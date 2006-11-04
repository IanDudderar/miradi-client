/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.strategicplan;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.tree.TreeSelectionModel;

import org.conservationmeasures.eam.dialogs.DisposablePanel;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.objects.ConceptualModelIntervention;

import com.java.sun.jtreetable.JTreeTable;


public class StrategicPlanPanel extends DisposablePanel
{
	static public StrategicPlanPanel createForProject(MainWindow mainWindowToUse) throws Exception
	{
		return new StrategicPlanPanel(mainWindowToUse, StrategicPlanTreeTableModel.createForProject(mainWindowToUse.getProject()));
	}
	
	static public StrategicPlanPanel createForStrategy(MainWindow mainWindowToUse, ConceptualModelIntervention intervention) throws Exception
	{
		return new StrategicPlanPanel(mainWindowToUse, StrategicPlanTreeTableModel.createForStrategy(mainWindowToUse.getProject(), intervention));
	}
	
	private StrategicPlanPanel(MainWindow mainWindowToUse, StrategicPlanTreeTableModel modelToUse) throws Exception
	{
		super(new BorderLayout());
		mainWindow = mainWindowToUse;
		model = modelToUse;
		tree = new StrategicPlanTreeTable(model);
		tree.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.getTree().setShowsRootHandles(true);
		add(new JScrollPane(tree), BorderLayout.CENTER);
		tree.getTree().addSelectionRow(0);
	}
	
	public StrategicPlanTreeTableModel getModel()
	{
		return model;
	}
	
	MainWindow mainWindow;
	JTreeTable tree;
	StrategicPlanTreeTableModel model;
}

