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
package org.miradi.questions;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.martus.util.UnicodeReader;
import org.miradi.main.EAM;
import org.miradi.main.ResourcesHandler;
import org.miradi.utils.IgnoreCaseStringComparator;

public class LanguageQuestion extends DynamicChoiceQuestion
{
	public LanguageQuestion()
	{
		threeLetterCodeToTwoLetterCodes = new HashMap<String, String>();
		try
		{
			loadChoices();
		}
		catch(Exception e)
		{
			EAM.logException(e);
		}
	}
	
	@Override
	public ChoiceItem[] getChoices()
	{
		return choices;
	}
	
	private void loadChoices() throws Exception
	{
		Vector<ChoiceItem> loadedChoices = new Vector<ChoiceItem>();
		URL url = ResourcesHandler.getEnglishResourceURL("/resources/Languages.dat");
		loadedChoices.add(new ChoiceItem("", EAM.text("Unspecified")));
		
		InputStream inputStream = url.openStream();
		UnicodeReader reader = new UnicodeReader(inputStream);
		try
		{
			while(true)
			{
				String line = reader.readLine();
				if(line == null)
					break;
				
				// NOTE: File was downloaded directly from ISO, and has these fields,
				// separated by vertical bars (|):
				// 3-letter code | other code | 2-letter code | name (in English) | name (in French)
				String[] parts = line.split("\\|");
				String threeLetterCode = parts[0];
				String twoLetterCode = parts[2];
				String name = parts[3];
				if(twoLetterCode.length() != 0)
				{
					loadedChoices.add(new ChoiceItem(twoLetterCode, name));
					threeLetterCodeToTwoLetterCodes.put(threeLetterCode, twoLetterCode);
				}
			}
		}
		finally
		{
			reader.close();
		}
		
		choices = loadedChoices.toArray(new ChoiceItem[0]);
		Arrays.sort(choices, new IgnoreCaseStringComparator());
	}

	public String lookupLanguageCode(String threeLetterLanguageCode)
	{
		String twoLetterCode = threeLetterCodeToTwoLetterCodes.get(threeLetterLanguageCode);
		return getValue(twoLetterCode);
	}

	private ChoiceItem[] choices;
	private Map<String, String> threeLetterCodeToTwoLetterCodes;
}
