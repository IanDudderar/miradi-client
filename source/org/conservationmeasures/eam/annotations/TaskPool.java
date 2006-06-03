/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.annotations;

import org.conservationmeasures.eam.objects.Task;
import org.conservationmeasures.eam.project.EAMObjectPool;

public class TaskPool extends EAMObjectPool
{
	public void put(Task task)
	{
		put(task.getId(), task);
	}
	
	public Task find(int id)
	{
		return (Task)getRawObject(id);
	}


}
