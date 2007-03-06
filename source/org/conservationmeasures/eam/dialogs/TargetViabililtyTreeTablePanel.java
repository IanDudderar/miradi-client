/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs;

import org.conservationmeasures.eam.actions.ActionCreateKeyEcologicalAttribute;
import org.conservationmeasures.eam.actions.ActionDeleteKeyEcologicalAttribute;
import org.conservationmeasures.eam.commands.CommandCreateObject;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.CommandExecutedEvent;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.Target;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.TreeTableWithStateSaving;
import org.conservationmeasures.eam.views.treeViews.TreeTablePanel;

public class TargetViabililtyTreeTablePanel extends TreeTablePanel
{
	public TargetViabililtyTreeTablePanel(MainWindow mainWindowToUse, Project projectToUse, TreeTableWithStateSaving treeToUse)
	{
		super( mainWindowToUse, projectToUse, treeToUse, buttonActions, ObjectType.KEY_ECOLOGICAL_ATTRIBUTE);
	}

	public void commandExecuted(CommandExecutedEvent event)
	{

		TargetViabilityTreeModel treeTableModel = (TargetViabilityTreeModel)getModel();
		
		int currentSelectedRow = tree.getSelectedRow();
		final boolean wereNodesAddedOrRemoved = isFactorCommand(event,  Target.TAG_KEY_ECOLOGICAL_ATTRIBUTE_IDS);
		if( wereNodesAddedOrRemoved)
		{
			treeTableModel.rebuildEntreTree();
			restoreTreeExpansionState();
		} 
		else if(isSetDataCommand(event))
		{
			CommandSetObjectData cmd = (CommandSetObjectData)event.getCommand();
			final boolean isModifiedObjectMabeyInTree = cmd.getObjectType() == ObjectType.KEY_ECOLOGICAL_ATTRIBUTE;
			if (isModifiedObjectMabeyInTree)
			{
				treeTableModel.rebuildEntreTree();
				repaint();
			}
		}

		if (isCreateObjectCommand(event))
		{
			BaseId baseId = ((CommandCreateObject)event.getCommand()).getCreatedId();
			selectObject(ObjectType.KEY_ECOLOGICAL_ATTRIBUTE, baseId);
		}
		else
			setSelectedRow(currentSelectedRow);
	}

	
	static final Class[] buttonActions = new Class[] {
			ActionCreateKeyEcologicalAttribute.class, 
			ActionDeleteKeyEcologicalAttribute.class,};
}
