/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.xml.wcs;

import org.martus.util.UnicodeWriter;
import org.miradi.objects.AbstractTarget;
import org.miradi.objects.BaseObject;

public abstract class AbstractTargetContainerExporter extends FactorPoolExporter
{
	public AbstractTargetContainerExporter(WcsXmlExporter wcsXmlExporterToUse, String containerNameToUse, int objectTypeToUse)
	{
		super(wcsXmlExporterToUse, containerNameToUse, objectTypeToUse);
	}

	@Override
	protected void exportFields(UnicodeWriter writer, BaseObject baseObject) throws Exception
	{
		super.exportFields(writer, baseObject);
		
		AbstractTarget abstractTarget = (AbstractTarget) baseObject;					
		writeElementWithSameTag(baseObject, AbstractTarget.TAG_TARGET_STATUS);
		writeElementWithSameTag(baseObject, AbstractTarget.TAG_VIABILITY_MODE);
		writeOptionalElementWithSameTag(baseObject, AbstractTarget.TAG_CURRENT_STATUS_JUSTIFICATION);
		writeIds("SubTargetIds", WcsXmlConstants.SUB_TARGET, abstractTarget.getSubTargetRefs());
		writeIds(AbstractTarget.TAG_GOAL_IDS, WcsXmlConstants.GOAL, abstractTarget.getGoalRefs());
		writeIds(AbstractTarget.TAG_KEY_ECOLOGICAL_ATTRIBUTE_IDS, WcsXmlConstants.KEY_ECOLOGICAL_ATTRIBUTE, abstractTarget.getKeyEcologicalAttributeRefs());
		writeIndicatorIds(abstractTarget);
	}
}
