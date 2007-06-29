/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.conservationmeasures.eam.actions.ActionInsertDraftStrategy;
import org.conservationmeasures.eam.actions.ActionInsertFactorLink;
import org.conservationmeasures.eam.actions.ActionInsertStrategy;
import org.conservationmeasures.eam.actions.ActionInsertTarget;
import org.conservationmeasures.eam.actions.ActionInsertTextBox;
import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.actions.EAMAction;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.diagram.cells.DiagramStrategyCell;
import org.conservationmeasures.eam.diagram.cells.DiagramTargetCell;
import org.conservationmeasures.eam.diagram.cells.DiagramTextBoxCell;
import org.conservationmeasures.eam.dialogs.fieldComponents.PanelButton;
import org.conservationmeasures.eam.dialogs.fieldComponents.PanelCheckBox;
import org.conservationmeasures.eam.dialogs.fieldComponents.PanelTitleLabel;
import org.conservationmeasures.eam.icons.GoalIcon;
import org.conservationmeasures.eam.icons.IndicatorIcon;
import org.conservationmeasures.eam.icons.ObjectiveIcon;
import org.conservationmeasures.eam.icons.ProjectScopeIcon;
import org.conservationmeasures.eam.icons.StressIcon;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.objects.Cause;
import org.conservationmeasures.eam.objects.ConceptualModelDiagram;
import org.conservationmeasures.eam.objects.FactorLink;
import org.conservationmeasures.eam.objects.Goal;
import org.conservationmeasures.eam.objects.Indicator;
import org.conservationmeasures.eam.objects.IntermediateResult;
import org.conservationmeasures.eam.objects.Objective;
import org.conservationmeasures.eam.objects.Strategy;
import org.conservationmeasures.eam.objects.Target;
import org.conservationmeasures.eam.objects.TextBox;
import org.conservationmeasures.eam.objects.ThreatReductionResult;
import org.conservationmeasures.eam.objects.ViewData;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.questions.ChoiceItem;
import org.conservationmeasures.eam.questions.DiagramLegendQuestion;
import org.conservationmeasures.eam.utils.CodeList;
import org.conservationmeasures.eam.utils.LocationHolder;
import org.martus.swing.UiLabel;

import com.jhlabs.awt.BasicGridLayout;
import com.jhlabs.awt.GridLayoutPlus;

abstract public class DiagramLegendPanel extends JPanel implements ActionListener
{
	abstract protected void createCustomLegendPanelSection(Actions actions, JPanel jpanel);
	
	public DiagramLegendPanel(MainWindow mainWindowToUse)
	{
		super(new BasicGridLayout(0, 1));
		mainWindow = mainWindowToUse;
		checkBoxes = new Hashtable();
		
		createLegendCheckBoxes();
		addAllComponents();
		updateLegendPanel(getDiagarmLegendSettingsForSlide());
	}
	
	private void addAllComponents()
	{
		setBorder(new EmptyBorder(5,5,5,5));
		UiLabel title = new PanelTitleLabel(EAM.text("LEGEND"));
		title.setFont(title.getFont().deriveFont(Font.BOLD));
		title.setBorder(new LineBorder(Color.BLACK, 2));
		title.setHorizontalAlignment(UiLabel.CENTER);
		add(title);
		
		add(createLegendButtonPanel(mainWindow.getActions()));
		updateCheckBoxs();
		setMinimumSize(new Dimension(0,0));
	}
	
	
	private void createLegendCheckBoxes()
	{
		createCheckBox(SCOPE_BOX);
		createCheckBox( Target.OBJECT_NAME);
		
		createCheckBox(Cause.OBJECT_NAME_THREAT);
		createCheckBox(Cause.OBJECT_NAME_CONTRIBUTING_FACTOR);
		createCheckBox(ThreatReductionResult.OBJECT_NAME);
		createCheckBox(IntermediateResult.OBJECT_NAME);
		createCheckBox(Strategy.OBJECT_NAME);
		createCheckBox(Strategy.OBJECT_NAME_DRAFT);
		createCheckBox(TextBox.OBJECT_NAME);
		
		createCheckBox(FactorLink.OBJECT_NAME);
		createCheckBox(FactorLink.OBJECT_NAME_TARGETLINK);
		createCheckBox(FactorLink.OBJECT_NAME_STRESS);
		
		createCheckBox(Goal.OBJECT_NAME);
		createCheckBox(Objective.OBJECT_NAME);
		createCheckBox(Indicator.OBJECT_NAME);

	}
	
	protected JPanel createLegendButtonPanel(Actions actions)
	{
		JPanel jpanel = new JPanel(new GridLayoutPlus(0,3));
		
		addIconLineWithCheckBox(jpanel, ConceptualModelDiagram.getObjectType(), SCOPE_BOX, new ProjectScopeIcon());
		
		addButtonLineWithCheckBox(jpanel, Target.getObjectType(), Target.OBJECT_NAME, actions.get(ActionInsertTarget.class));
		createCustomLegendPanelSection(actions, jpanel);
		
		addButtonLineWithCheckBox(jpanel, Strategy.getObjectType(),Strategy.OBJECT_NAME, actions.get(ActionInsertStrategy.class));
		if (mainWindow.getDiagramView().isStategyBrainstormMode())
		{
			addButtonLineWithoutCheckBox(jpanel, Strategy.getObjectType(), Strategy.OBJECT_NAME_DRAFT, actions.get(ActionInsertDraftStrategy.class));
		}

		addButtonLineWithCheckBox(jpanel, FactorLink.getObjectType(), FactorLink.OBJECT_NAME, actions.get(ActionInsertFactorLink.class));
		addTargetLinkLine(jpanel, FactorLink.getObjectType(), FactorLink.OBJECT_NAME_TARGETLINK);
		
		addIconLineWithCheckBox(jpanel, Goal.getObjectType(), Goal.OBJECT_NAME, new GoalIcon());
		addIconLineWithCheckBox(jpanel, Objective.getObjectType(), Objective.OBJECT_NAME, new ObjectiveIcon());
		addIconLineWithCheckBox(jpanel, Indicator.getObjectType(), Indicator.OBJECT_NAME, new IndicatorIcon());
		addIconLineWithCheckBox(jpanel, FactorLink.getObjectType(), FactorLink.OBJECT_NAME_STRESS, new StressIcon());
		addButtonLineWithCheckBox(jpanel, TextBox.getObjectType(), TextBox.OBJECT_NAME, actions.get(ActionInsertTextBox.class));
		
		return jpanel;
	}

	protected void addTargetLinkLine(JPanel jpanel, int objectType, String objectName)
	{
		jpanel.add(new JLabel(""));
		jpanel.add(new PanelTitleLabel(EAM.fieldLabel(objectType, objectName)));
		targetLinkCheckBox = findCheckBox(objectName);
		jpanel.add(targetLinkCheckBox);
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
	
	protected void addIconLineWithCheckBox(JPanel jpanel, int objectType, String objectName, Icon icon)
	{
		addIconLine(jpanel, EAM.fieldLabel(objectType, objectName), icon, findCheckBox(objectName));
	}


	protected void addIconLineWithoutCheckBox(JPanel jpanel, int objectType, String objectName, Icon icon)
	{
		addIconLine(jpanel, EAM.fieldLabel(objectType, objectName), icon, new UiLabel(""));
	}

	private void updateCheckBoxs()
	{
		Object[] keys = checkBoxes.keySet().toArray();
		for (int i=0; i<keys.length; ++i)
		{
			updateCheckBox(getLayerManager(), ((JCheckBox)checkBoxes.get(keys[i])).getClientProperty(LAYER).toString());
		}
	}
	
	
	private JCheckBox createCheckBox(String objectName)
	{
		JCheckBox component = new PanelCheckBox();
		checkBoxes.put(objectName, component);
		
		component.putClientProperty(LAYER, new String(objectName));
		component.addActionListener(this);

		return component;
	}
	
	private void addIconLine(JPanel jpanel, String text, Icon icon, JComponent component)
	{
		jpanel.add(new JLabel(icon));
		jpanel.add(new PanelTitleLabel(EAM.text(text)));
		jpanel.add(component);
	}

	public void actionPerformed(ActionEvent event)
	{
		JCheckBox checkBox = (JCheckBox)event.getSource();
		String property = (String) checkBox.getClientProperty(LAYER);
		LayerManager manager = getLayerManager();
		setLegendVisibilityOfFacactorCheckBoxes(manager, property);
		updateVisiblity();
		updateProjectLegendSettings(property);
	}


	protected void setLegendVisibilityOfFacactorCheckBoxes(LayerManager manager, String property)
	{
		JCheckBox checkBox = findCheckBox(property);
		
		if (property.equals(Strategy.OBJECT_NAME))
			manager.setVisibility(DiagramStrategyCell.class, checkBox.isSelected());
		else if (property.equals(Target.OBJECT_NAME))
			manager.setVisibility(DiagramTargetCell.class, checkBox.isSelected());
		else if (property.equals(FactorLink.OBJECT_NAME))
		{
			manager.setFactorLinksVisible(checkBox.isSelected());
			targetLinkCheckBox.setEnabled(checkBox.isSelected());
		}
		else if (property.equals(FactorLink.OBJECT_NAME_TARGETLINK))
			manager.setTargetLinksVisible(checkBox.isSelected());
		else if (property.equals(Goal.OBJECT_NAME))
			manager.setGoalsVisible(checkBox.isSelected());
		else if (property.equals(Objective.OBJECT_NAME))
			manager.setObjectivesVisible(checkBox.isSelected());
		else if (property.equals(Indicator.OBJECT_NAME))
			manager.setIndicatorsVisible(checkBox.isSelected());
		else if (property.equals(SCOPE_BOX))
			manager.setScopeBoxVisible(checkBox.isSelected());
		else if (property.equals(TextBox.OBJECT_NAME))
			manager.setVisibility(DiagramTextBoxCell.class, checkBox.isSelected());
		else if (property.equals(FactorLink.OBJECT_NAME_STRESS))
			manager.setStressesVisible(checkBox.isSelected());
	}
	
	public void resetCheckBoxes()
	{
		updateLegendPanel(getDiagarmLegendSettingsForSlide());
	}
	
	public void updateCheckBox(LayerManager manager, String property)
	{
		JCheckBox checkBox = findCheckBox(property);

		if (property.equals(SCOPE_BOX))
			checkBox.setSelected(manager.isScopeBoxVisible());
	
		else if (property.equals(Target.OBJECT_NAME))
			checkBox.setSelected(manager.isTypeVisible(DiagramTargetCell.class));
		
		else if (property.equals(Strategy.OBJECT_NAME))
			checkBox.setSelected(manager.isTypeVisible(DiagramStrategyCell.class));
		
		else if (property.equals(FactorLink.OBJECT_NAME))
			checkBox.setSelected(manager.areFactorLinksVisible());
		
		else if (property.equals(FactorLink.OBJECT_NAME_TARGETLINK))
			checkBox.setSelected(manager.areTargetLinksVisible());
		
		else if (property.equals(Goal.OBJECT_NAME))
			checkBox.setSelected(manager.areGoalsVisible());
		
		else if (property.equals(Objective.OBJECT_NAME))
			checkBox.setSelected(manager.areObjectivesVisible());
		
		else if (property.equals(Indicator.OBJECT_NAME))
			checkBox.setSelected(manager.areIndicatorsVisible());
		
		else if (property.equals(TextBox.OBJECT_NAME))
			checkBox.setSelected(manager.areTextBoxesVisible());
		
		else if (property.equals(FactorLink.OBJECT_NAME_STRESS))
			checkBox.setSelected(manager.areStressesVisible());
	}
	

	private JCheckBox findCheckBox(Object property)
	{
		return (JCheckBox)checkBoxes.get(property);
	}
	
	
	public void turnOFFCheckBoxs()
	{
		Object[] keys = checkBoxes.keySet().toArray();
		for (int i=0; i<keys.length; ++i)
		{
			findCheckBox(keys[i]).setSelected(false);
		}
	}

	
	public CodeList getLegendSettings()
	{
		CodeList hiddenTypes = new CodeList();
		ChoiceItem[] choices = new DiagramLegendQuestion("").getChoices();
		for (int i=0; i<choices.length; ++i)
		{
			if (!isSelected(choices[i].getCode()))
				hiddenTypes.add(choices[i].getCode());
		}
		return hiddenTypes;
	}
	
	
	public void updateLegendPanel(CodeList hiddenTypes)
	{
		Object[] keys = checkBoxes.keySet().toArray();
		for (int i=0; i<keys.length; ++i)
		{
			findCheckBox(keys[i]).setSelected(true);
			setLegendVisibilityOfFacactorCheckBoxes(getLayerManager(), keys[i].toString());
		}
		
		for (int i=0; i<hiddenTypes.size(); ++i)
		{
			findCheckBox(hiddenTypes.get(i)).setSelected(false);
			setLegendVisibilityOfFacactorCheckBoxes(getLayerManager(), hiddenTypes.get(i));
		}

		updateVisiblity();
	}

	private void updateVisiblity()
	{
		mainWindow.getDiagramView().updateVisibilityOfFactors();
		mainWindow.updateStatusBar();
	}
	
	
	private void updateProjectLegendSettings(String property)
	{
		try
		{
			ViewData data = getProject().getCurrentViewData();
			getProject().executeCommand(new CommandSetObjectData(data.getRef(), ViewData.TAG_DIAGRAM_HIDDEN_TYPES, getLegendSettings().toString()));
		}
		catch(Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog("Unable to update project legend settings:" + e.getMessage());
		}
	}

	
	private CodeList getDiagarmLegendSettingsForSlide()
	{
		try
		{
			ViewData data = getProject().getCurrentViewData();
			return  new CodeList(data.getData(ViewData.TAG_DIAGRAM_HIDDEN_TYPES));
		}
		catch(Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog("Unable to read project settings:" + e.getMessage());
			return new CodeList();
		}
	}
	

	private LayerManager getLayerManager()
	{
		return mainWindow.getProject().getLayerManager();
	}
	
	private Project getProject()
	{
		return mainWindow.getProject();
	}
	
	public boolean isSelected(String property)
	{
		JCheckBox checkBox = findCheckBox(property);
		
		if (checkBox==null)
			return false;
		
		return checkBox.isSelected();
	}
	
	
	class LocationButton extends PanelButton implements LocationHolder
	{
		LocationButton(EAMAction action)
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
	
	public static final String SCOPE_BOX = "ScopeBox";

	private static final String LAYER = "LAYER";
	MainWindow mainWindow;
	JCheckBox targetLinkCheckBox; 
	Hashtable checkBoxes;
}