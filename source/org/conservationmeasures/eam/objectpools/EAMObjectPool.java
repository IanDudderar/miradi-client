/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.objectpools;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.objects.EAMObject;

public class EAMObjectPool extends ObjectPool
{
	public EAMObjectPool(int objectTypeToStore)
	{
		objectType = objectTypeToStore;
	}
	
	public EAMObject findObject(BaseId id)
	{
		return (EAMObject)getRawObject(id);
	}
	
	public int getObjectType()
	{
		return objectType;
	}
	
	int objectType;
}
