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
package org.miradi.views.diagram.doers;

import org.miradi.main.EAM;
import org.miradi.objects.BaseObject;
import org.miradi.schemas.ResultReportSchema;
import org.miradi.views.diagram.DeleteAnnotationDoer;

public class DeleteResultReportDoer extends DeleteAnnotationDoer
{
    @Override
    protected BaseObject getParent(BaseObject annotationToDelete)
    {
        return getReferrerParent(annotationToDelete);
    }

    @Override
    public String getAnnotationIdListTag()
    {
        return BaseObject.TAG_RESULT_REPORT_REFS;
    }

    @Override
    public int getAnnotationType()
    {
        return ResultReportSchema.getObjectType();
    }

    @Override
    public String[] getDialogText()
    {
        return new String[] { EAM.text("Are you sure you want to delete this Result Report?"),};
    }
}
