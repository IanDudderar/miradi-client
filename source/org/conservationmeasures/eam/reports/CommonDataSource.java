/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.reports;

import java.util.Arrays;
import java.util.Comparator;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.project.Project;

abstract public class CommonDataSource implements JRDataSource
{
	public CommonDataSource(Project projectToUse)
	{
		setRowCount(1);
		setCurrentRow(-1);
		project = projectToUse;
	}
	
	public void setObjectList(ORefList list)
	{
		setRowCount(list.size());
		objectList = loadObjects(list);
	}
	
	public void setSingleObject(ORef oref)
	{
		setRowCount(1);
		ORefList list = new ORefList();
		list.add(oref);
		objectList = loadObjects(list);
	}
	
	public BaseObject[] loadObjects(ORefList list)
	{
		BaseObject[] objects = new BaseObject[list.size()];
		for (int i=0; i<list.size(); ++i)
		{
			objects[i] = project.findObject(list.get(i));
		}
		return objects;
	}
	
	
	public void sortObjectList(Comparator comparator)
	{
		Arrays.sort(objectList, comparator);
	}
	

	public boolean next() throws JRException 
	{
		currentRow = currentRow + 1;
		return (currentRow < rowCount);
	}
	
	public Object getFieldValue(JRField field)
	{
		BaseObject object = getCurrentObject();
		if (object==null)
			return "";
		return getValue(field, object);
	}
	
	
	public Object getValue(JRField field, BaseObject baseObject)
	{
		if (isLabelFiedl(field.getName()))
			return translateToLabel(baseObject.getType(), field.getName());
		return baseObject.getData(field.getName());
	}
	
	public boolean isLabelFiedl(String name)
	{
		return (name.startsWith(LABEL_PREFIX));
	}
	
	public String translateToLabel(int objectType, String name)
	{
		return EAM.fieldLabel(objectType, name.substring(LABEL_PREFIX.length()));
	}
	
	private int setCurrentRow(int row)
	{
		return currentRow = row;
	}

	public int getCurrentRow()
	{
		return currentRow;
	}

	public int setRowCount(int rows)
	{
		return rowCount = rows;
	}
	
	//FIXME: The special case logic for row management where the objects are not managed here can be better
	public BaseObject getCurrentObject()
	{
		if (objectList==null)
			return null;
		if ((rowCount==0 || getCurrentRow()>=rowCount))
			return null;
		return objectList[getCurrentRow()];
	}

	private static String LABEL_PREFIX = "Label:";
	
	private BaseObject[] objectList;
	private int currentRow;
	private int rowCount;
	public Project project;
}
