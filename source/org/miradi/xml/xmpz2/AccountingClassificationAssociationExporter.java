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

package org.miradi.xml.xmpz2;

import org.miradi.objects.BaseObject;
import org.miradi.schemas.AbstractTaxonomyAssociationSchema;
import org.miradi.schemas.AccountingClassificationAssociationSchema;

public class AccountingClassificationAssociationExporter extends BaseObjectExporter
{
	public AccountingClassificationAssociationExporter(Xmpz2XmlWriter writerToUse, int objectTypeToUse)
	{
		super(writerToUse, objectTypeToUse);
	}

	@Override
	protected void writeStartElement(final BaseObject baseObject) throws Exception
	{
		final String data = baseObject.getData(AbstractTaxonomyAssociationSchema.TAG_TAXONOMY_ASSOCIATION_CODE);
		getWriter().writeObjectStartElementWithAttribute(baseObject, ACCOUNTING_CLASSIFICATION_ASSOCIATION_CODE, data);
	}
	
	@Override
	protected boolean doesFieldRequireSpecialHandling(String tag)
	{
		if (tag.equals(AbstractTaxonomyAssociationSchema.TAG_TAXONOMY_ASSOCIATION_CODE))
			return true;
		
		return super.doesFieldRequireSpecialHandling(tag);
	}
	
	@Override
	protected boolean shouldOmitField(String tag)
	{
		if (tag.equals(BaseObject.TAG_UUID))
			return true;
		
		if (tag.equals(AbstractTaxonomyAssociationSchema.TAG_BASE_OBJECT_TYPE))
			return true;

		if (tag.equals(AccountingClassificationAssociationSchema.TAG_ACCOUNTING_CLASSIFICATION_ASSOCIATION_POOL_NAME))
			return true;
		
		return false;
	}
}
