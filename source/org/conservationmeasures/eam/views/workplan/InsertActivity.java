/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.workplan;

import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.EAMObject;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.objects.Task;

public class InsertActivity extends AbstractTaskTreeDoer
{
	public boolean isAvailable()
	{
		EAMObject[] selected = getObjects();
		if(selected == null || selected.length != 1)
			return false;
	
		if(selected[0].getType() != ObjectType.FACTOR)
			return false;
		
		if(!((Factor)selected[0]).isStrategy())
			return false;
		
		return true;
	}

	public void doIt() throws CommandFailedException
	{
		if(!isAvailable())
			return;
		
		EAMObject parent = getObjects()[0];
		String tag = AbstractTaskTreeDoer.getTaskIdsTag(parent.getRef());
		try
		{
			Task task = createTask(getProject(), parent, tag);
			getPanel().selectObject(task);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			throw new CommandFailedException(e);
		}
	}

}
