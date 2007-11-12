/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/
package org.conservationmeasures.eam.questions;

public class FontFamiliyQuestion extends StaticChoiceQuestion
{
	public FontFamiliyQuestion(String tag)
	{
		super(tag, "Font Family", getFamilyChoices());
	}
	
	static ChoiceItem[] getFamilyChoices()
	{
		return new ChoiceItem[] {
			new ChoiceItem("", "sans-serif"),
			new ChoiceItem("serif", "serif"),
		};
	}
}
