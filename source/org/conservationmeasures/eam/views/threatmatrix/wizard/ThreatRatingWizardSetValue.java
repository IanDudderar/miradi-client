/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.threatmatrix.wizard;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objects.RatingCriterion;
import org.conservationmeasures.eam.objects.ValueOption;
import org.conservationmeasures.eam.project.ThreatRatingBundle;
import org.conservationmeasures.eam.project.ThreatRatingFramework;
import org.conservationmeasures.eam.views.threatmatrix.ThreatMatrixView;

abstract public class ThreatRatingWizardSetValue extends ThreatRatingWizardStep
{
	public ThreatRatingWizardSetValue(ThreatRatingWizardPanel wizardToUse, String critertionNameToUse) throws Exception
	{
		super(wizardToUse);
		critertionName = critertionNameToUse;
	}

	public void refresh() throws Exception
	{
		ThreatRatingFramework framework = getFramework();
		BaseId criterionId = framework.findCriterionByLabel(critertionName).getId();
		ThreatRatingBundle bundle = ((ThreatMatrixView)getView()).getBundle();
		criterion = getFramework().getCriterion(criterionId);
		if(bundle == null)
		{
			return;
		}
		BaseId valueId = bundle.getValueId(criterion.getId());
		value = getFramework().getValueOption(valueId);
		
		super.refresh();
	}

	protected String[] getValueOptionLabels()
	{
		ValueOption[] options = getFramework().getValueOptions();
		String[] optionLabels = new String[options.length];
		for(int i = 0; i < optionLabels.length; ++i)
			optionLabels[i] = options[i].getLabel();
		return optionLabels;
	}

	private ThreatRatingFramework getFramework()
	{
		return getMainWindow().getProject().getThreatRatingFramework();
	}
	
	public void valueChanged(String widget, String newValue)
	{
	}
	
	public void setValue(String newValue)
	{
		value = findValueOptionByName(newValue);
	}
	
	public ValueOption findValueOptionByName(String label)
	{
		ValueOption[] options = getFramework().getValueOptions();
		for(int i = 0; i < options.length; ++i)
			if(label.equals(options[i].getLabel()))
				return options[i];
			
		throw new RuntimeException("Unknown value option: " + label);
	}

	public void setComponent(String name, JComponent component)
	{
		try
		{
			ThreatRatingBundle bundle = ((ThreatMatrixView)getView()).getBundle();
			if (name.equals("value"))
			{
				valueBox = (JComboBox)component;
				DefaultComboBoxModel cbm = new DefaultComboBoxModel(getValueOptionLabels());
				valueBox.setModel(cbm);
				if (bundle!=null) 
					valueBox.setSelectedItem(value.getLabel());
			}
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
				
		((JComboBox)component).addItemListener(new ValueItemListener());
	}
	
	
	class ValueItemListener implements ItemListener
	{
		public void itemStateChanged(ItemEvent arg0)
		{
			try
			{
				setValue(valueBox.getSelectedItem().toString());
				((ThreatMatrixView)getView()).setBundleValue(criterion, value);
			}
			catch(Exception e)
			{
				EAM.logException(e);
			}

		}
		ThreatRatingWizardChooseBundle wizard;
	}
	
	
	JComboBox valueBox;
	String critertionName;
	RatingCriterion criterion;
	protected ValueOption value;
}
