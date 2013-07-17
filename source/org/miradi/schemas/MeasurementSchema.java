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

package org.miradi.schemas;

import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.Measurement;
import org.miradi.questions.PrecisionTypeQuestion;
import org.miradi.questions.StatusConfidenceQuestion;
import org.miradi.questions.StatusQuestion;
import org.miradi.questions.TrendQuestion;

public class MeasurementSchema extends BaseObjectSchema
{
	public MeasurementSchema()
	{
		super();
	}
	
	@Override
	protected void fillFieldSchemas()
	{
		super.fillFieldSchemas();
		
		createFieldSchemaChoice(Measurement.TAG_TREND, TrendQuestion.class);
		createFieldSchemaChoice(Measurement.TAG_STATUS, StatusQuestion.class);
		createFieldSchemaDate(Measurement.TAG_DATE);
		createFieldSchemaExpandingUserText(Measurement.TAG_SUMMARY);
		createFieldSchemaMultiLineUserText(Measurement.TAG_DETAIL);
		createFieldSchemaChoice(Measurement.TAG_STATUS_CONFIDENCE, StatusConfidenceQuestion.class);
		createFieldSchemaMultiLineUserText(Measurement.TAG_COMMENTS);
		createFieldSchemaInteger(Measurement.TAG_SAMPLE_SIZE);
		createFieldSchemaNumber(Measurement.TAG_SAMPLE_PRECISION);
		createFieldSchemaChoice(Measurement.TAG_SAMPLE_PRECISION_TYPE, PrecisionTypeQuestion.class);
	}

	public static int getObjectType()
	{
		return ObjectType.MEASUREMENT;
	}
	
	@Override
	public int getType()
	{
		return getObjectType();
	}

	@Override
	public String getObjectName()
	{
		return OBJECT_NAME;
	}
	
	public static final String OBJECT_NAME = "Measurement";
}
