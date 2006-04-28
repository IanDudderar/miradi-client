package org.conservationmeasures.eam.views.strategicplan;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.conservationmeasures.eam.commands.CommandCreateObject;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.objects.ObjectType;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.umbrella.UmbrellaView;
import org.martus.swing.UiButton;

public class StrategicPlanView extends UmbrellaView
{
	public StrategicPlanView(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
		setToolBar(new StrategicPlanToolBar(mainWindowToUse.getActions()));
		setLayout(new BorderLayout());

	}

	public String cardName() 
	{
		return getViewName();
	}
	
	static public String getViewName()
	{
		return "Strategic Plan";
	}

	public void becomeActive() throws Exception
	{
		removeAll();
		JPanel strategicPanel = new JPanel(new BorderLayout());
		strategicPanel.add(createActivityTree(), BorderLayout.CENTER);		
		strategicPanel.add(createButtonBox(), BorderLayout.AFTER_LAST_LINE);

		add(strategicPanel, BorderLayout.CENTER);
		
	}

	public void becomeInactive() throws Exception
	{
	}

	private JTree createActivityTree()
	{
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
		model = new DefaultTreeModel(rootNode);
		tree = new JTree(model);
		
		//activityTree.setRootVisible(false);
		
		
		return tree;
	}

	private Box createButtonBox()
	{
		Box buttonBox = Box.createHorizontalBox();
		UiButton addButton = new UiButton("Add");
		UiButton editButton = new UiButton("Edit");
		UiButton deleteButton = new UiButton("Delete");
		addButton.addActionListener(new AddButtonHandler(getProject(), tree));
		buttonBox.add(addButton);
		buttonBox.add(editButton);
		buttonBox.add(deleteButton);
		
		return buttonBox;
	}
	
	JTree tree;
	DefaultTreeModel model;
}

class AddButtonHandler implements ActionListener
{

	AddButtonHandler(Project projectToUse, JTree treeToUse)
	{
		project = projectToUse;
		tree = treeToUse;
	}
	
	public DefaultTreeModel getModel()
	{
		return (DefaultTreeModel)tree.getModel();
	}
	
	public void actionPerformed(ActionEvent event)
	{
		try
		{
			CommandCreateObject cmd = new CommandCreateObject(ObjectType.TASK);
			project.executeCommand(cmd);
			String label = Integer.toString(cmd.getCreatedId());
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(label);
			DefaultMutableTreeNode root = (DefaultMutableTreeNode)getModel().getRoot();
			getModel().insertNodeInto(newNode, root, root.getChildCount());
			tree.expandPath(new TreePath(getModel().getPathToRoot(root)));
			
		}
		catch(CommandFailedException e)
		{
			EAM.errorDialog("Could not create activity");
		}
		
	}
	
	Project project;
	JTree tree;
}