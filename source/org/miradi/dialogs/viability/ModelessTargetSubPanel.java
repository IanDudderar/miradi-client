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
package org.miradi.dialogs.viability;

import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.main.EAM;
import org.miradi.objects.AbstractTarget;
import org.miradi.objects.Target;
import org.miradi.project.Project;
import org.miradi.questions.HabitatAssociationQuestion;
import org.miradi.questions.StaticQuestionManager;
import org.miradi.schemas.TargetSchema;

public class ModelessTargetSubPanel extends ObjectDataInputPanel
{
	public ModelessTargetSubPanel(Project projectToUse, int targetType) throws Exception
	{
		super(projectToUse, targetType);

		if (Target.is(targetType))
		{
			addField(createStringField(TargetSchema.getObjectType(), Target.TAG_SPECIES_LATIN_NAME));
			addField(createEditableCodeListField(TargetSchema.getObjectType(), Target.TAG_HABITAT_ASSOCIATION, StaticQuestionManager.getQuestion(HabitatAssociationQuestion.class)));
		}

		addTaxonomyFields(targetType);
		addField(createReadOnlyObjectList(targetType, AbstractTarget.PSEUDO_TAG_CONCEPTUAL_DIAGRAM_REFS));
		addField(createReadOnlyObjectList(targetType, AbstractTarget.PSEUDO_TAG_RESULTS_CHAIN_REFS));

		addField(createMultilineField(targetType, AbstractTarget.TAG_TEXT));
		addField(createMultilineField(targetType, AbstractTarget.TAG_COMMENTS));
		addField(createMultilineField(targetType, AbstractTarget.TAG_EVIDENCE_NOTES));

		updateFieldsFromProject();
	}

	@Override
	protected boolean doesSectionContainFieldWithTag(String tag)
	{
		if (tag.equals(Target.PSEUDO_TAG_HABITAT_ASSOCIATION_VALUE))
			return true;

		return super.doesSectionContainFieldWithTag(tag);
	}

	@Override
	public String getPanelDescription()
	{
		return EAM.text("Details");
	}
}
