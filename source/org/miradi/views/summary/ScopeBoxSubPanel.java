/* 
Copyright 2005-2018, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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
package org.miradi.views.summary;

import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.objects.Factor;
import org.miradi.objects.ScopeBox;
import org.miradi.project.Project;
import org.miradi.schemas.ScopeBoxSchema;

public class ScopeBoxSubPanel extends ObjectDataInputPanel
{
	public ScopeBoxSubPanel(Project projectToUse) throws Exception
	{
		super(projectToUse, ScopeBoxSchema.getObjectType());
		
		addField(createReadonlyTextField(ScopeBoxSchema.getObjectType(), ScopeBox.TAG_LABEL));
		addField(createReadonlyTextField(ScopeBoxSchema.getObjectType(), ScopeBox.TAG_TEXT));

		addField(createReadOnlyObjectList(ScopeBoxSchema.getObjectType(), Factor.PSEUDO_TAG_CONCEPTUAL_DIAGRAM_REFS));
		addField(createReadOnlyObjectList(ScopeBoxSchema.getObjectType(), Factor.PSEUDO_TAG_RESULTS_CHAIN_REFS));
		
		updateFieldsFromProject();
	}

	@Override
	public String getPanelDescription()
	{
		return null;
	}
}
