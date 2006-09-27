/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;


public class Translation
{
	public static Locale getTranslationLocale()
	{
		return currentTranslationLocale;
	}
	
	public static void restoreDefaultLocale()
	{
		currentTranslationLocale = new Locale("en", "US");
	}

	public static void setTranslationLocale(Locale locale)
	{
		currentTranslationLocale = locale;
		currentResourceBundle = getResourceBundle(locale);
		Locale actualLocaleUsed = currentResourceBundle.getLocale();
		if(!locale.equals(actualLocaleUsed))
		{
			Logging.logWarning("Requested " + locale + " but fell back to: " + actualLocaleUsed);
		}
	}

	private static ResourceBundle getResourceBundle(Locale locale)
	{
		return ResourceBundle.getBundle("EAM", locale);
	}

	public static String text(String key)
	{
		if(currentTranslationLocale.equals(Locale.US))
			return extractPartToDisplay(key);
	
		try
		{
			ResourceBundle resources = getResourceBundle(currentTranslationLocale);
			return resources.getString(key);
		}
		catch(MissingResourceException e)
		{
			Logging.logWarning("Unknown translation key: " + key);
			return "<" + extractPartToDisplay(key) + ">";
		}
	}
	
	public static void loadFieldLabels() throws IOException
	{
		InputStream in = Translation.class.getResourceAsStream("FieldLabels.properties");
		try
		{
			properties.load(in);
		}
		finally
		{
			in.close();
		}
		
	}
	
	public static String fieldLabel(int objectType, String fieldTag)
	{
		String fullTag = Integer.toString(objectType) + "." + fieldTag;
		String label = properties.getProperty(fullTag);
		if(label == null)
			label = fieldTag;
		return label;
	}

	public static String extractPartToDisplay(String result)
	{
		int lastBar = result.lastIndexOf('|');
		if(lastBar >= 0)
			result = result.substring(lastBar + 1);
	
		return result;
	}

	private static Properties properties = new Properties();
	private static Locale currentTranslationLocale = new Locale("en", "US");
	private static ResourceBundle currentResourceBundle;
}
