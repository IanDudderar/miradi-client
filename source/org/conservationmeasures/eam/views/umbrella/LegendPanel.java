/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.umbrella;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.conservationmeasures.eam.actions.EAMAction;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.dialogs.fieldComponents.PanelButton;
import org.conservationmeasures.eam.dialogs.fieldComponents.PanelCheckBox;
import org.conservationmeasures.eam.dialogs.fieldComponents.PanelTitleLabel;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.ViewData;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.utils.CodeList;
import org.conservationmeasures.eam.utils.LocationHolder;
import org.martus.swing.UiLabel;

import com.jhlabs.awt.BasicGridLayout;

abstract public class LegendPanel extends JPanel implements ActionListener
{
	public LegendPanel(Project projectToUse, BasicGridLayout layout)
	{
		super(layout);
		project = projectToUse;
		checkBoxes = new Hashtable();
	}
	
	protected void saveSettingsToProject(String tag)
	{
		try
		{
			ViewData data = getProject().getCurrentViewData();
			CommandSetObjectData setLegendSettingsCommand = new CommandSetObjectData(data.getRef(), tag, getLegendSettings().toString());
			getProject().executeCommand(setLegendSettingsCommand);
		}
		catch(Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog("Unable to update project legend settings:" + e.getMessage());
		}
	}
	
	protected CodeList getLegendSettings(String tag)
	{
		try
		{
			ViewData data = getProject().getCurrentViewData();
			return new CodeList(data.getData(tag));
		}
		catch(Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog("Unable to read project settings:" + e.getMessage());
			return new CodeList();
		}
	}
	
	protected Project getProject()
	{
		return project;
	}

	protected JCheckBox findCheckBox(Object property)
	{
		return (JCheckBox)checkBoxes.get(property);
	}

	public boolean isSelected(String property)
	{
		JCheckBox checkBox = findCheckBox(property);
		if (checkBox == null)
			return false;
		
		return checkBox.isSelected();
	}

	public void disableAllCheckBoxes()
	{
		Object[] keys = checkBoxes.keySet().toArray();
		for (int i = 0; i < keys.length; ++i)
		{
			findCheckBox(keys[i]).setEnabled(false);
		}
	}
	
	public void unselectAllCheckBoxes()
	{
		Object[] keys = checkBoxes.keySet().toArray();
		for (int i = 0; i < keys.length; ++i)
		{
			findCheckBox(keys[i]).setSelected(false);
		}
	}
	
	protected void selectAllCheckBoxes()
	{
		Object[] keys = checkBoxes.keySet().toArray();
		for (int i = 0; i < keys.length; ++i)
		{
			String property = ((JCheckBox)checkBoxes.get(keys[i])).getClientProperty(LAYER).toString();
			JCheckBox checkBox = findCheckBox(property);
			checkBox.setSelected(true);
		}
	}
	
	public UiLabel createTitleBar(String text)
	{
		UiLabel title = new PanelTitleLabel(text);
		title.setFont(title.getFont().deriveFont(Font.BOLD));
		title.setBorder(new LineBorder(Color.BLACK, 2));
		title.setHorizontalAlignment(UiLabel.CENTER);
		
		return title;
	}
	
	protected void addTitleBar(JPanel jPanel, String text)
	{
		UiLabel titleBar = createTitleBar(text);
		jPanel.add(new JLabel(""));
		jPanel.add(titleBar);
		jPanel.add(new JLabel(""));
	}
	
	protected void addIconLine(JPanel jpanel, String text, Icon icon, JComponent component)
	{
		jpanel.add(new JLabel(icon));
		jpanel.add(new PanelTitleLabel(EAM.text(text)));
		jpanel.add(component);
	}

	protected void createCheckBox(String objectName)
	{
		JCheckBox component = new PanelCheckBox();
		checkBoxes.put(objectName, component);
		
		component.putClientProperty(LAYER, new String(objectName));
		component.addActionListener(this);
	}

	protected void addCheckBoxLine(JPanel jpanel, String objectName)
	{
		JCheckBox foundCheckBox = findCheckBox(objectName);
		String foundLabel = EAM.fieldLabel(ObjectType.FAKE, objectName);
		addIconLine(jpanel, foundLabel, null, foundCheckBox);
	}
	
	protected void addIconLineWithoutCheckBox(JPanel jpanel, int objectType, String objectName, Icon icon)
	{
		addIconLine(jpanel, EAM.fieldLabel(objectType, objectName), icon, new UiLabel(""));
	}

	protected void addIconLineWithCheckBox(JPanel jpanel, int objectType, String objectName, Icon icon)
	{
		JCheckBox foundCheckBox = findCheckBox(objectName);
		String foundLabel = EAM.fieldLabel(objectType, objectName);
		addIconLine(jpanel, foundLabel, icon, foundCheckBox);
	}

	protected void addButtonLineWithCheckBox(JPanel jpanel, int objectType, String objectName, EAMAction action)
	{
		JButton button = new LocationButton(action);
		jpanel.add(button);
		jpanel.add(new PanelTitleLabel(EAM.fieldLabel(objectType, objectName)));
		jpanel.add(findCheckBox(objectName));
	}

	protected void addButtonLineWithoutCheckBox(JPanel jpanel, int objectType, String objectName, EAMAction action)
	{
		JButton button = new LocationButton(action);
		jpanel.add(button);
		jpanel.add(new PanelTitleLabel(EAM.fieldLabel(objectType, objectName)));
		jpanel.add(new UiLabel(""));
	}
	
	protected static class LocationButton extends PanelButton implements LocationHolder
	{
		public LocationButton(EAMAction action)
		{
			super(action);
			setText(null);
			setMargin(new Insets(0, 0, 0 ,0));
		}
		
		public boolean hasLocation()
		{
			return false;
		}
	}

	abstract public CodeList getLegendSettings();
	abstract public void actionPerformed(ActionEvent event);
	
	protected static final String LAYER = "LAYER";
	Project project;
	protected Hashtable checkBoxes;
}