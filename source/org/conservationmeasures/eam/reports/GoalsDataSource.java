/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/
package org.conservationmeasures.eam.reports;

import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objects.Goal;
import org.conservationmeasures.eam.project.Project;

public class GoalsDataSource extends CommonDataSource
{
	public GoalsDataSource(Project project)
	{
		super(project);
		ORefList list = project.getPool(Goal.getObjectType()).getORefList();
		setObjectList(list);
	}
} 
