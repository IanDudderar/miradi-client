/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */

package org.conservationmeasures.eam.diagram.nodes;


public class Objective extends NodeAnnotation
{
	public Objective()
	{
		super();
	}
	
	public Objective(String objective)
	{
		super(objective);
	}

	public boolean hasObjective()
	{
		return hasAnnotation();
	}
	
	public String getLabel()
	{
		return getAnnotation();
	}

	public boolean equals(Object obj) 
	{
		if(!(obj instanceof Objective))
			return false;
		return ((Objective)obj).getAnnotation().equals(getAnnotation());
	}
}
