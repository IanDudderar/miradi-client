/* 
Copyright 2005-2012, Foundations of Success, Bethesda, Maryland 
(on behalf of the Conservation Measures Partnership, "CMP") and 
Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 

This file is part of Miradi

Miradi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3, 
as published by the Free Software Foundation.

Miradi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Miradi.  If not, see <http://www.gnu.org/licenses/>. 
*/ 

package org.miradi.utils;

import org.miradi.objecthelpers.ORef;
import org.miradi.objects.BaseObject;
import org.miradi.project.Project;

public class BaseObjectDeepCopierNotUsingCommands extends BaseObjectDeepCopier
{
	public BaseObjectDeepCopierNotUsingCommands(Project projectToUse)
	{
		super(projectToUse);
	}
	
	@Override
	protected ORef createBaseObject(BaseObject baseObejctToClone) throws Exception
	{
		return getProject().createObject(baseObejctToClone.getType());
	}

	@Override
	protected void setBaseObjectData(BaseObject copiedBaseObjectToFill, String tag, String dataToBeSaved) throws Exception
	{
		getProject().setObjectData(copiedBaseObjectToFill, tag, dataToBeSaved);
	}
}