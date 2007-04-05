/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objects;

import java.util.HashMap;
import java.util.Iterator;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.objectdata.ChoiceData;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.project.ObjectManager;
import org.conservationmeasures.eam.project.TNCViabilityFormula;
import org.conservationmeasures.eam.questions.StatusQuestion;
import org.conservationmeasures.eam.questions.ViabilityModeQuestion;
import org.conservationmeasures.eam.utils.CodeList;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;


public class Target extends Factor
{
	public Target(ObjectManager objectManager, FactorId idToUse)
	{
		super(objectManager, idToUse, Factor.TYPE_TARGET);
		clear();
	}
	
	public Target(FactorId idToUse)
	{
		super(idToUse, Factor.TYPE_TARGET);
		clear();
	}
	
	public Target(ObjectManager objectManager, FactorId idToUse, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, idToUse, Factor.TYPE_TARGET, json);
	}
	
	public Target(FactorId idToUse, EnhancedJsonObject json) throws Exception
	{
		super(idToUse, Factor.TYPE_TARGET, json);
	}

	public static boolean canOwnThisType(int type)
	{
		if (Factor.canOwnThisType(type))
			return true;
		
		switch(type)
		{
			case ObjectType.GOAL: 
				return true;
			case ObjectType.KEY_ECOLOGICAL_ATTRIBUTE: 
				return true;
			default:
				return false;
		}
	}
	
	public ORefList getOwnedObjects(int objectType)
	{
		ORefList list = super.getOwnedObjects(objectType);
		
		switch(objectType)
		{
			case ObjectType.GOAL: 
				list.addAll(new ORefList(objectType, getGoals()));
			case ObjectType.KEY_ECOLOGICAL_ATTRIBUTE: 
				list.addAll(new ORefList(objectType, getKeyEcologicalAttributes()));

		}
		return list;
	}

	
	public boolean isTarget()
	{
		return true;
	}
	
	public boolean canHaveGoal()
	{
		return true;
	}
	
	public boolean canHaveKeyEcologicalAttribures()
	{
		return true;
	}
	
	public String getData(String fieldTag)
	{
		if(fieldTag.equals(PSEUDO_TAG_TARGET_VIABILITY))
			return getTargetViability();
		
		return super.getData(fieldTag);
	}
	
	public IdList getDirectOrIndirectGoals()
	{
		IdList goalIds = new IdList();

		if(!isViabilityModeTNC())
			return getGoals();
		
		IdList indicatorIds = getDirectOrIndirectIndicators();
		for(int i = 0; i < indicatorIds.size(); ++i)
		{
			Indicator indicator = (Indicator)objectManager.findObject(ObjectType.INDICATOR, indicatorIds.get(i));
			goalIds.addAll(indicator.getGoalIds());
		}
		
		return goalIds;
	}
	
	
	public IdList getDirectOrIndirectIndicators()
	{
		if(!isViabilityModeTNC())
			return super.getIndicators();
		
		return findAllKeaIndicators();
	}
	

	public IdList findAllKeaIndicators()
	{
		IdList list = new IdList();
		IdList keas = getKeyEcologicalAttributes();
		for (int j=0; j<keas.size(); ++j)
		{
			BaseId keyEcologicalAttributeId = keas.get(j);
			KeyEcologicalAttribute kea = (KeyEcologicalAttribute) objectManager.findObject(ObjectType.KEY_ECOLOGICAL_ATTRIBUTE, keyEcologicalAttributeId);
			list.addAll(kea.getIndicatorIds());
		}
		return list;
	}
	
	
	public String getBasicTargetStatus()
	{
		return targetStatus.get();
	}
	
	public boolean isViabilityModeTNC()
	{
		return viabiltyMode.get().equals(ViabilityModeQuestion.TNC_STYLE_CODE);
	}
	
	private String getTargetViability()
	{
		if(isViabilityModeTNC())
			return computeTNCViability();
		return getBasicTargetStatus();
	}
	
	public String computeTNCViability()
	{
		HashMap categoryKeaRatings = new HashMap();
		
		IdList keas = getKeyEcologicalAttributes();
		for(int i = 0; i < keas.size(); ++i)
		{
			KeyEcologicalAttribute kea = (KeyEcologicalAttribute)objectManager.findObject(ObjectType.KEY_ECOLOGICAL_ATTRIBUTE, keas.get(i));
			String category = kea.getData(KeyEcologicalAttribute.TAG_KEY_ECOLOGICAL_ATTRIBUTE_TYPE);
			if(category.equals(StatusQuestion.UNSPECIFIED))
				continue;
			
			CodeList codesForCategory = (CodeList)categoryKeaRatings.get(category);
			if(codesForCategory == null)
			{
				codesForCategory = new CodeList();
				categoryKeaRatings.put(category, codesForCategory);
			}

			String keaViability = kea.getData(KeyEcologicalAttribute.PSUEDO_TAG_VIABILITY_STATUS);
			codesForCategory.add(keaViability);
		}
		
		CodeList categorySummaryRatings = new CodeList();
		Iterator iter = categoryKeaRatings.keySet().iterator();
		while(iter.hasNext())
		{
			String category = (String)iter.next();
			CodeList keaCodes = (CodeList)categoryKeaRatings.get(category);
			String categoryRating = TNCViabilityFormula.getTotalCategoryRatingCode(keaCodes);
			categorySummaryRatings.add(categoryRating);
		}
		
		return TNCViabilityFormula.getAverageRatingCode(categorySummaryRatings);
	}
	
		static public String computeTNCViability(KeyEcologicalAttribute[] keas)
	{
		CodeList codes = new CodeList();
		for(int i = 0; i < keas.length; ++i)
		{
			codes.add(keas[i].computeTNCViability());
		}
		return TNCViabilityFormula.getAverageRatingCode(codes);
	}
	
	
	static public String computeTNCViability(Target[] targets)
	{
		CodeList codes = new CodeList();
		for(int i = 0; i < targets.length; ++i)
		{
			codes.add(targets[i].computeTNCViability());
		}
		return TNCViabilityFormula.getAverageRatingCode(codes);
	}
	
	
	public int getType()
	{
		return getObjectType();
	}
	
	public static int getObjectType()
	{
		return ObjectType.TARGET;
	}
	
	void clear()
	{
		super.clear();
		targetStatus = new ChoiceData();
		viabiltyMode = new ChoiceData();
		
		addField(TAG_TARGET_STATUS, targetStatus);
		addField(TAG_VIABILITY_MODE, viabiltyMode);
	}
	
	public static final String TAG_TARGET_STATUS = "TargetStatus";
	public static final String TAG_VIABILITY_MODE = "ViabilityMode";
	public static final String OBJECT_NAME = "Target";
	
	public static final String PSEUDO_TAG_TARGET_VIABILITY = "PseudoTagTargetViability";
	
	ChoiceData targetStatus;
	ChoiceData viabiltyMode;
}
