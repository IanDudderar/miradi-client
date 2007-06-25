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
import org.conservationmeasures.eam.objects.ConceptualModelDiagram;
import org.conservationmeasures.eam.objects.FactorLink;
import org.conservationmeasures.eam.objects.Goal;
import org.conservationmeasures.eam.objects.Indicator;
import org.conservationmeasures.eam.objects.Objective;
import org.conservationmeasures.eam.objects.Strategy;
import org.conservationmeasures.eam.objects.Target;
import org.conservationmeasures.eam.objects.TextBox;
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
		addAllComponents();
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
		setMinimumSize(new Dimension(0,0));
	}
	
	protected JPanel createLegendButtonPanel(Actions actions)
	{
		JPanel jpanel = new JPanel(new GridLayoutPlus(0,3));
		
		addIconLineWithCheckBox(jpanel, ConceptualModelDiagram.getObjectType(), constantSCOPEBOX(), new ProjectScopeIcon());
		
		addButtonLineWithCheckBox(jpanel, Target.getObjectType(), Target.OBJECT_NAME, actions.get(ActionInsertTarget.class));
		createCustomLegendPanelSection(actions, jpanel);
		
		if (mainWindow.getDiagramView().isStategyBrainstormMode())
		{
			addButtonLineWithCheckBox(jpanel, Strategy.getObjectType(), Strategy.OBJECT_NAME, actions.get(ActionInsertStrategy.class));
			addButtonLineWithoutCheckBox(jpanel, Strategy.getObjectType(), "Draft " + Strategy.OBJECT_NAME, actions.get(ActionInsertDraftStrategy.class));
		}
		else
		{
			addButtonLineWithCheckBox(jpanel, Strategy.getObjectType(),Strategy.OBJECT_NAME, actions.get(ActionInsertStrategy.class));
		}
		
		addButtonLineWithCheckBox(jpanel, FactorLink.getObjectType(), FactorLink.OBJECT_NAME, actions.get(ActionInsertFactorLink.class));
		addTargetLinkLine(jpanel, FactorLink.getObjectType(), constantTARGETLINK());
		
		addIconLineWithCheckBox(jpanel, Goal.getObjectType(), Goal.OBJECT_NAME, new GoalIcon());
		addIconLineWithCheckBox(jpanel, Objective.getObjectType(), Objective.OBJECT_NAME, new ObjectiveIcon());
		addIconLineWithCheckBox(jpanel, Indicator.getObjectType(), Indicator.OBJECT_NAME, new IndicatorIcon());
		addIconLineWithCheckBox(jpanel, FactorLink.getObjectType(), constantSTRESS(), new StressIcon());
		addButtonLineWithCheckBox(jpanel, TextBox.getObjectType(), TextBox.OBJECT_NAME, actions.get(ActionInsertTextBox.class));
		
		return jpanel;
	}

	protected void addTargetLinkLine(JPanel jpanel, int objectType, String objectName)
	{
		jpanel.add(new JLabel(""));
		jpanel.add(new PanelTitleLabel(EAM.fieldLabel(objectType, objectName)));
		targetLinkCheckBox = createCheckBox(objectName);
		jpanel.add(targetLinkCheckBox);
	}
	
	protected void addButtonLineWithCheckBox(JPanel jpanel, int objectType, String objectName, EAMAction action)
	{
		JButton button = new LocationButton(action);
		jpanel.add(button);
		jpanel.add(new PanelTitleLabel(EAM.fieldLabel(objectType, objectName)));
		jpanel.add(createCheckBox(objectName));
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
		addIconLine(jpanel, EAM.fieldLabel(objectType, objectName), icon, createCheckBox(objectName));
	}


	protected void addIconLineWithoutCheckBox(JPanel jpanel, int objectType, String objectName, Icon icon)
	{
		addIconLine(jpanel, EAM.fieldLabel(objectType, objectName), icon, new UiLabel(""));
	}

	private JCheckBox createCheckBox(String objectName)
	{
		JCheckBox component = new PanelCheckBox();
		component.putClientProperty(LAYER, new String(objectName));
		component.addActionListener(this);
		
		updateCheckBoxes(mainWindow.getProject().getLayerManager(), component.getClientProperty(LAYER).toString(), component);
		
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
		LayerManager manager = mainWindow.getProject().getLayerManager();
		setLegendVisibilityOfFacactorCheckBoxes(manager, property, checkBox);			
	}


	protected void setLegendVisibilityOfFacactorCheckBoxes(LayerManager manager, String property, JCheckBox checkBox)
	{
		if (property.equals(Strategy.OBJECT_NAME))
			manager.setVisibility(DiagramStrategyCell.class, checkBox.isSelected());
		else if (property.equals(Target.OBJECT_NAME))
			manager.setVisibility(DiagramTargetCell.class, checkBox.isSelected());
		else if (property.equals(Target.OBJECT_NAME))
			manager.setVisibility(DiagramTargetCell.class, checkBox.isSelected());
		else if (property.equals(FactorLink.OBJECT_NAME))
		{
			manager.setFactorLinksVisible(checkBox.isSelected());
			targetLinkCheckBox.setEnabled(checkBox.isSelected());
		}
		else if (property.equals(constantTARGETLINK()))
			manager.setTargetLinksVisible(checkBox.isSelected());
		else if (property.equals(Goal.OBJECT_NAME))
			manager.setGoalsVisible(checkBox.isSelected());
		else if (property.equals(Objective.OBJECT_NAME))
			manager.setObjectivesVisible(checkBox.isSelected());
		else if (property.equals(Indicator.OBJECT_NAME))
			manager.setIndicatorsVisible(checkBox.isSelected());
		else if (property.equals(constantSCOPEBOX()))
			manager.setScopeBoxVisible(checkBox.isSelected());
		else if (property.equals(TextBox.OBJECT_NAME))
			manager.setVisibility(DiagramTextBoxCell.class, checkBox.isSelected());
		else if (property.equals(constantSTRESS()))
			manager.setStressesVisible(checkBox.isSelected());
		
		mainWindow.getDiagramView().updateVisibilityOfFactors();
		mainWindow.updateStatusBar();
	}
	
	public void resetCheckBoxes()
	{
		removeAll();
		addAllComponents();
	}
	
	public void updateCheckBoxes(LayerManager manager, String property, JCheckBox checkBox)
	{
		if (property.equals(constantSCOPEBOX()))
			checkBox.setSelected(manager.isScopeBoxVisible());
	
		else if (property.equals(Target.OBJECT_NAME))
			checkBox.setSelected(manager.isTypeVisible(DiagramTargetCell.class));
		
		else if (property.equals(Strategy.OBJECT_NAME))
			checkBox.setSelected(manager.isTypeVisible(DiagramStrategyCell.class));
		
		else if (property.equals(FactorLink.OBJECT_NAME))
			checkBox.setSelected(manager.areFactorLinksVisible());
		
		else if (property.equals(constantTARGETLINK()))
			checkBox.setSelected(manager.areTargetLinksVisible());
		
		else if (property.equals(Goal.OBJECT_NAME))
			checkBox.setSelected(manager.areGoalsVisible());
		
		else if (property.equals(Objective.OBJECT_NAME))
			checkBox.setSelected(manager.areObjectivesVisible());
		
		else if (property.equals(Indicator.OBJECT_NAME))
			checkBox.setSelected(manager.areIndicatorsVisible());
		
		else if (property.equals(TextBox.OBJECT_NAME))
			checkBox.setSelected(manager.areTextBoxesVisible());
		
		else if (property.equals(constantSTRESS()))
			checkBox.setSelected(manager.areStressesVisible());
	}

	private String constantSTRESS()
	{
		return EAM.text("Stress");
	}
	
	private String constantTARGETLINK()
	{
		return EAM.text("Targetlink");
	}
	
	private String constantSCOPEBOX()
	{
		return EAM.text("ScopeBox");
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
	
	private static final String LAYER = "LAYER";
	MainWindow mainWindow;
	JCheckBox targetLinkCheckBox;
}