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

package org.miradi.schemas;

import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.Measurement;
import org.miradi.questions.PrecisionTypeQuestion;
import org.miradi.questions.MeasurementEvidenceConfidenceQuestion;
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
		createFieldSchemaChoice(Measurement.TAG_EVIDENCE_CONFIDENCE, MeasurementEvidenceConfidenceQuestion.class);
		createFieldSchemaMultiLineUserText(Measurement.TAG_COMMENTS);
		createFieldSchemaMultiLineUserText(Measurement.TAG_EVIDENCE_NOTES);
		createFieldSchemaInteger(Measurement.TAG_SAMPLE_SIZE);
		createFieldSchemaNumber(Measurement.TAG_SAMPLE_PRECISION);
		createFieldSchemaChoice(Measurement.TAG_SAMPLE_PRECISION_TYPE, PrecisionTypeQuestion.class);

		// TODO: field to be deprecated in post 4.5 release...only here to support migrations
		createFieldSchemaChoice(Measurement.TAG_STATUS_CONFIDENCE, StatusConfidenceQuestion.class);
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
