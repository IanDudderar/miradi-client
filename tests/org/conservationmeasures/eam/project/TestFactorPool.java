/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.project;

import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.ids.IdAssigner;
import org.conservationmeasures.eam.objects.Factor;
import org.martus.util.TestCaseEnhanced;
//FIXME add tests for IR and TRR pools
abstract public class TestFactorPool extends TestCaseEnhanced
{
	public TestFactorPool(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
		super.setUp();
		idAssigner = new IdAssigner();
		project = new ProjectForTesting(getName());
		
		for (int i = 0; i < FACTOR_COUNT; ++i)
		{
			addNewlyCreatedNodeToPool(getObjectType());
		}
	}	
	
	abstract public int getObjectType();

	protected FactorId addNewlyCreatedNodeToPool(int type)
	{
		FactorId id = takeNextModelNodeId();		
		Factor node = Factor.createConceptualModelObject(project.getObjectManager(), id, type);
		project.getPool(type).put(node.getId(), node);
		
		return id;
	}

	private FactorId takeNextModelNodeId()
	{
		return new FactorId(idAssigner.takeNextId().asInt());
	}
	
	IdAssigner idAssigner;
	ProjectForTesting project;
	
	protected static final int FACTOR_COUNT = 3;
}
