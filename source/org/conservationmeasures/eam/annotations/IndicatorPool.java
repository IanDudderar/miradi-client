/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.annotations;

import org.conservationmeasures.eam.objects.Indicator;
import org.conservationmeasures.eam.project.EAMObjectPool;

public class IndicatorPool extends EAMObjectPool
{
	public void put(Indicator indicator)
	{
		put(indicator.getId(), indicator);
	}
	
	public Indicator find(int id)
	{
		return (Indicator)getRawObject(id);
	}



}
