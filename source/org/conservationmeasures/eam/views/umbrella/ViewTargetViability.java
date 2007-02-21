/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.umbrella;

import org.conservationmeasures.eam.commands.CommandSwitchView;
import org.conservationmeasures.eam.exceptions.AlreadyInThatViewException;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.views.ProjectDoer;
import org.conservationmeasures.eam.views.targetviability.TargetViabilityView;

public class ViewTargetViability extends ProjectDoer
{
	public boolean isAvailable()
	{
		return getProject().isOpen();
	}
	
	public void doIt() throws CommandFailedException
	{
		if(!isAvailable())
			return;
		
		try
		{
			getProject().executeCommand(new CommandSwitchView(TargetViabilityView.getViewName()));
		}
		catch(AlreadyInThatViewException ignore)
		{
			// not really a problem
		}
	}
}
