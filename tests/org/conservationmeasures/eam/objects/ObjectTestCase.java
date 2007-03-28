/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.objects;

import java.awt.Point;
import java.io.File;

import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.main.EAMTestCase;
import org.conservationmeasures.eam.objectdata.ChoiceData;
import org.conservationmeasures.eam.objectdata.DateData;
import org.conservationmeasures.eam.objectdata.IdListData;
import org.conservationmeasures.eam.objectdata.ObjectData;
import org.conservationmeasures.eam.objectdata.PointListData;
import org.conservationmeasures.eam.objecthelpers.CreateObjectParameter;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.project.ProjectForTesting;
import org.conservationmeasures.eam.utils.StringMapData;
import org.martus.util.DirectoryUtils;
import org.martus.util.MultiCalendar;

public class ObjectTestCase extends EAMTestCase
{
	public ObjectTestCase(String name)
	{
		super(name);
	}

	public void verifyFields(int objectType) throws Exception
	{
		verifyFields(objectType, null);
	}
	
	public void verifyFields(int objectType, CreateObjectParameter extraInfo) throws Exception
	{
		Project project = new ProjectForTesting(getName());
		try
		{
			BaseId id = project.createObject(objectType, BaseId.INVALID, extraInfo);
			BaseObject object = project.findObject(objectType, id);
			String[] tags = object.getFieldTags();
			for(int i = 0; i < tags.length; ++i)
			{
				verifyFieldLifecycle(project, object, tags[i]);
			}
		}
		finally
		{
			project.close();
		}
		
		verifyLoadPool(objectType, extraInfo);
	}
	
	private void verifyLoadPool(int objectType, CreateObjectParameter extraInfo) throws Exception
	{
		BaseId id = BaseId.INVALID;

		File dir = createTempDirectory();
		Project project = new Project();
		project.createOrOpen(dir);
		try
		{
			id = project.createObject(objectType, BaseId.INVALID, extraInfo);
		}
		finally
		{
			project.close();
		}
		
		project.createOrOpen(dir);
		try
		{
			BaseObject object = project.findObject(objectType, id);
			assertNotNull("Didn't load pool?", object);
		}
		finally
		{
			project.close();
		}
		DirectoryUtils.deleteEntireDirectoryTree(dir);
	}
	
	private void verifyFieldLifecycle(Project project, BaseObject object, String tag) throws Exception
	{
		if(tag.equals(BaseObject.TAG_ID))
			return;
		if (object.getNoneClearedFieldTags().contains(tag))
			return;
				
		String sampleData = getSampleData(object, tag);

		assertEquals("didn't default " + tag + " blank?", "", object.getData(tag));
		object.setData(tag, sampleData);
		assertEquals("did't set " + tag + "?", sampleData, object.getData(tag));
		BaseObject got = BaseObject.createFromJson(object.getType(), object.toJson());
		assertEquals("didn't jsonize " + tag + "?", object.getData(tag), got.getData(tag));
		
		CommandSetObjectData[] commandsToDelete = object.createCommandsToClear();
		for(int i = 0; i < commandsToDelete.length; ++i)
		{
			assertNotEquals("Tried to clear Id?", BaseObject.TAG_ID, commandsToDelete[i].getFieldTag());
			project.executeCommand(commandsToDelete[i]);
		}
		assertEquals("Didn't clear " + tag + "?", "", object.getData(tag));
		for(int i = 0; i < commandsToDelete.length; ++i)
			project.undo();
		assertEquals("Didn't restore " + tag + "?", sampleData, object.getData(tag));

	}

	private String getSampleData(BaseObject object, String tag)
	{
		ObjectData field = object.getField(tag);
		if(field instanceof IdListData)
		{
			IdList list = new IdList();
			list.add(new BaseId(7));
			return list.toString();
		}
		else if(field instanceof StringMapData)
		{
			StringMapData list = new StringMapData();
			list.add("A","RolaA");
			return list.toString();
		}
		else if(field instanceof ChoiceData)
		{
			return "3";
		}
		else if(field instanceof DateData)
		{
			return MultiCalendar.createFromGregorianYearMonthDay(1953, 10, 21).toString();
		}
		else if (field instanceof PointListData)
		{
			PointListData pointList = new PointListData();
			pointList.add(new Point(-1, 55));
			
			return pointList.toString();
		}
		
		return tag + tag;
	}
}
