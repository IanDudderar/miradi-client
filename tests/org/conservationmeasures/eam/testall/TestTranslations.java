/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.testall;

import java.util.Locale;
import java.util.MissingResourceException;

import org.conservationmeasures.eam.main.EAM;

public class TestTranslations extends EAMTestCase
{
	public TestTranslations(String name)
	{
		super(name);
	}

	public void testExtractPartToDisplay()
	{
		assertEquals("whole thing", EAM.extractPartToDisplay("whole thing"));
		assertEquals("part", EAM.extractPartToDisplay("only|return|last|part"));
	}
	
	public void testDefaultLocale()
	{
		Locale defaultTranslationLocale = EAM.getTranslationLocale();
		assertEquals("en", defaultTranslationLocale.getLanguage());
		assertEquals("US", defaultTranslationLocale.getCountry());
		
		String sampleText = "should return unchanged";
		assertEquals(sampleText, EAM.text(sampleText));
	}
	
	public void testBadLocale()
	{
		Locale badLocale = new Locale("xx", "YY");
		try
		{
			EAM.setTranslationLocale(badLocale);
			fail("Should have thrown setting bad locale");
		}
		catch(MissingResourceException ignoreExpected)
		{
		}
	}

	public void testOtherLocale()
	{
		Locale testingLocale = new Locale("test", "LOCALE");
		EAM.setTranslationLocale(testingLocale);
		assertEquals(testingLocale, EAM.getTranslationLocale());
		
		EAM.setLogToString();
		String sampleText = "should indicate non-translated";
		assertEquals("<" + sampleText + ">", EAM.text(sampleText));
		
		assertEquals(FAKE_TRANSLATION, EAM.text(ENGLISH_STRING));
	}
	
	public static String ENGLISH_STRING = "To be translated";
	public static String FAKE_TRANSLATION = "Aha! It worked!";
}
