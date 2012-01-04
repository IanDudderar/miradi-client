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
package org.miradi.utils;

import org.martus.util.xml.XmlUtilities;

public class HtmlUtilities
{
	public static String plainStringWithNewlinesToHtml(String plainString)
	{
		if(plainString == null)
			return "";
		
		String formatted =  XmlUtilities.getXmlEncoded(plainString);
		String formattedFactorName = formatted.replace("\n", "<br>");
		return formattedFactorName;
	}
	
	public static String removeNonHtmlNewLines(String htmlText)
	{
		htmlText = htmlText.replaceAll("\n", "");
		
		return htmlText;
	}
	
	public static final String BR_TAG = "<br/>";
	public static final String BR_TAG_UNCLOSED = "<br>";
}
