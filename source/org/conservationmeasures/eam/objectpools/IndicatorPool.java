/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objectpools;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdAssigner;
import org.conservationmeasures.eam.ids.IndicatorId;
import org.conservationmeasures.eam.objecthelpers.CreateObjectParameter;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.Indicator;

public class IndicatorPool extends EAMNormalObjectPool
{
	public IndicatorPool(IdAssigner idAssignerToUse)
	{
		super(idAssignerToUse, ObjectType.INDICATOR);
	}
	
	public void put(Indicator indicator)
	{
		put(indicator.getId(), indicator);
	}
	
	public Indicator find(BaseId id)
	{
		return (Indicator)getRawObject(id);
	}

	BaseObject createRawObject(BaseId actualId, CreateObjectParameter extraInfo)
	{
		return new Indicator(new IndicatorId(actualId.asInt()));
	}
	
	public Indicator[] getAllIndicators()
	{
		BaseId[] allIds = getIds();
		Indicator[] allIndicators = new Indicator[allIds.length];
		for (int i = 0; i < allIndicators.length; i++)
			allIndicators[i] = find(allIds[i]);
			
		return allIndicators;
	}



}
