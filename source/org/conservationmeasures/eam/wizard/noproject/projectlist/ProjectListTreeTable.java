/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.wizard.noproject.projectlist;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;

import org.conservationmeasures.eam.database.ProjectServer;
import org.conservationmeasures.eam.dialogs.fieldComponents.PanelTreeTable;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.wizard.noproject.FileSystemTreeNode;
import org.conservationmeasures.eam.wizard.noproject.NoProjectWizardStep;

public class ProjectListTreeTable extends PanelTreeTable
{
	public ProjectListTreeTable(ProjectListTreeTableModel treeTableModel, NoProjectWizardStep handlerToUse)
	{
		super(treeTableModel);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		addMouseListener(new MouseHandler());
	}

	public String getUniqueTableIdentifier()
	{
		return EAM.text("Project List");
	}
	
	private void doProjectOpen(Point point)
	{
		int row = rowAtPoint(point);
		if(row < 0 || row > getRowCount())
			return;
		
		FileSystemTreeNode node = (FileSystemTreeNode)getObjectForRow(row);
		File file = node.getFile();
		doProjectOpen(file);
	}
	
	public static boolean isProjectDirectory(File file)
	{
		return ProjectServer.isExistingProject(file);
	}

	public static void doProjectOpen(File file)
	{
		if(!isProjectDirectory(file))
			return;
		
		MainWindow mainWindow = EAM.getMainWindow();
		Cursor cursor = mainWindow.getCursor();
		mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try
		{
			mainWindow.createOrOpenProject(file);
		}
		finally
		{
			mainWindow.setCursor(cursor);
		}
	}
	
	private void doContextMenu(Point point)
	{
		int row = rowAtPoint(point);
		if(row < 0 || row > getRowCount())
			return;
		
		getSelectionModel().setSelectionInterval(row, row);
		FileSystemTreeNode node = (FileSystemTreeNode)getObjectForRow(row);
		JPopupMenu menu = getRightClickMenu(node.getFile());
		menu.show(this, point.x, point.y);

	}

	public JPopupMenu getRightClickMenu(File selectedFile)
	{
		JPopupMenu menu = new JPopupMenu();
		menu.add(new ProjectListOpenAction(this, selectedFile));
		menu.addSeparator();
		menu.add(new ProjectListRenameAction(this, selectedFile)); 
		menu.add(new ProjectListCopyToAction(this, selectedFile));
		menu.add(new ProjectListExportAction(this, selectedFile));
		menu.addSeparator();
		menu.add(new ProjectListDeleteAction(this, selectedFile));
		return menu;
	}
	
	void refresh()
	{
		ProjectListTreeTableModel model = (ProjectListTreeTableModel)tree.getModel();
		model.rebuildEntireTree();
		repaint();
	}
	
	class MouseHandler extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			super.mousePressed(e);
			if(e.isPopupTrigger())
				doContextMenu(e.getPoint());
		}
		
		public void mouseReleased(MouseEvent e)
		{
			super.mouseReleased(e);
			if(e.isPopupTrigger())
				doContextMenu(e.getPoint());
		}
		
		public void mouseClicked(MouseEvent e)
		{
			super.mouseClicked(e);
			if(e.getClickCount() == 2)
				doProjectOpen(e.getPoint());
		}
	}
}
