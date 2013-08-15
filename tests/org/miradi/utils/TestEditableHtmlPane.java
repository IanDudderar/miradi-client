/* 
Copyright 2005-2011, Foundations of Success, Bethesda, Maryland 
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

import org.miradi.main.MiradiTestCase;

public class TestEditableHtmlPane extends MiradiTestCase
{
	public TestEditableHtmlPane(String name)
	{
		super(name);
	}

	public void testGetNormalizedAndSanitizedHtmlText() throws Exception
	{
		String htmlText = "<br/><b>x</b><i>x</i><ul>x</ul><ol>x</ol><li>x</li><u>x</u><strike>x</strike><a href=\"\">x</a>";

		assertEquals("wrong new lines inserted?", htmlText, EditableHtmlPane.getNormalizedAndSanitizedHtmlText(htmlText));
	}
}