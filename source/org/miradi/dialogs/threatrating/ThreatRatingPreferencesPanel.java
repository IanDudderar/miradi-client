/* 
Copyright 2005-2017, Foundations of Success, Bethesda, Maryland
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
package org.miradi.dialogs.threatrating;

import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.main.EAM;
import org.miradi.objects.ProjectMetadata;
import org.miradi.project.Project;
import org.miradi.questions.ThreatRatingModeChoiceQuestion;
import org.miradi.schemas.ProjectMetadataSchema;

public class ThreatRatingPreferencesPanel extends ObjectDataInputPanel
{
	public ThreatRatingPreferencesPanel(Project projectToUse)
	{
		super(projectToUse, projectToUse.getMetadata().getRef());

		addField(createChoiceField(ProjectMetadataSchema.getObjectType(), ProjectMetadata.TAG_THREAT_RATING_MODE, new ThreatRatingModeChoiceQuestion()));
		updateFieldsFromProject();
	}

	@Override
	public String getPanelDescription()
	{
		return EAM.text("Threat Rating Preferences Panel");
	}
}
