/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objects;

import org.conservationmeasures.eam.ids.AssignmentId;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.TaskId;
import org.conservationmeasures.eam.objectdata.BaseIdData;
import org.conservationmeasures.eam.objectdata.DateRangeEffortListData;
import org.conservationmeasures.eam.objecthelpers.CreateAssignmentParameter;
import org.conservationmeasures.eam.objecthelpers.CreateObjectParameter;
import org.conservationmeasures.eam.objecthelpers.DateRangeEffortList;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;

public class Assignment extends BaseObject
{
	public Assignment(BaseId idToUse, CreateAssignmentParameter extraInfo)
	{
		super(new AssignmentId(idToUse.asInt()));
		clear();
		taskIdData.setId(extraInfo.getTaskId());
	}
	
	public Assignment(int idAsInt, EnhancedJsonObject json) throws Exception
	{
		super(new TaskId(idAsInt), json);
	}

	public int getType()
	{
		return getObjectType();
	}

	public static int getObjectType()
	{
		return ObjectType.ASSIGNMENT;
	}
	
	
	public static boolean canOwnThisType(int type)
	{
		return false;
	}
	
	
	public static boolean canReferToThisType(int type)
	{
		switch(type)
		{
			case ObjectType.PROJECT_RESOURCE: 
				return true;
			case ObjectType.ACCOUNTING_CODE: 
				return true;
			case ObjectType.FUNDING_SOURCE: 
				return true;
			case ObjectType.TASK: 
				return true;
			default:
				return false;
		}
	}
	
	
	public ORefList getReferencedObjects(int objectType)
	{
		switch(objectType)
		{
			case ObjectType.PROJECT_RESOURCE: 
				return new ORefList(new ORef[] {new ORef(objectType, resourceIdData.getId())});
			case ObjectType.ACCOUNTING_CODE: 
				return new ORefList(new ORef[] {new ORef(objectType, accountingIdData.getId())});
			case ObjectType.FUNDING_SOURCE: 
				return new ORefList(new ORef[] {new ORef(objectType, fundingIdData.getId())});
			case ObjectType.TASK: 
				return new ORefList(new ORef[] {new ORef(objectType, taskIdData.getId())});
			default:
				return new ORefList();
		}
	}
	
	
	public DateRangeEffortList getDetails()
	{
		return detailListData.getDateRangeEffortList();
	}
	
	public void setResourceId(BaseId resourceIdToUse)
	{
		resourceIdData.setId(resourceIdToUse);
	}
	
	public BaseId getResourceId()
	{
		return resourceIdData.getId();
	}
	
	public CreateObjectParameter getCreationExtraInfo()
	{
		//TODO create TaskIdData class
		TaskId taskId = new TaskId(taskIdData.getId().asInt());
		
		return new CreateAssignmentParameter(taskId);
	}
	
	public void clear()
	{
		super.clear();
		taskIdData = new BaseIdData();
		resourceIdData = new BaseIdData();
		detailListData = new DateRangeEffortListData();
		accountingIdData = new BaseIdData();
		fundingIdData = new BaseIdData();
		
		addNoClearField(TAG_ASSIGNMENT_TASK_ID, taskIdData);
		addField(TAG_ASSIGNMENT_RESOURCE_ID, resourceIdData);
		addField(TAG_DATERANGE_EFFORTS, detailListData);
		addField(TAG_ACCOUNTING_CODE, accountingIdData);
		addField(TAG_FUNDING_SOURCE, fundingIdData);
	}
	
	public static final String TAG_ASSIGNMENT_TASK_ID = "TaskId";
	public static final String TAG_ASSIGNMENT_RESOURCE_ID = "ResourceId";
	public static final String TAG_DATERANGE_EFFORTS = "Details";
	public static final String TAG_ACCOUNTING_CODE = "AccountingCode";
	public static final String TAG_FUNDING_SOURCE = "FundingSource";
	
	public static final String OBJECT_NAME = "Assignment";
	
	BaseIdData taskIdData;
	BaseIdData resourceIdData;
	DateRangeEffortListData detailListData;
	BaseIdData accountingIdData;
	BaseIdData fundingIdData;
	
}
