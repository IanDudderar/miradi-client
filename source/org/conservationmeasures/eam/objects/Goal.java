/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objects;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objectdata.ORefListData;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.project.ObjectManager;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;


public class Goal extends Desire 
{
	public Goal(ObjectManager objectManager, BaseId idToUse)
	{
		super(objectManager, idToUse);
	}
	
	public Goal(BaseId idToUse)
	{
		super(idToUse);
	}
	
	public Goal(ObjectManager objectManager, int idAsInt, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, new BaseId(idAsInt), json);
	}
	
	public Goal(int idAsInt, EnhancedJsonObject json) throws Exception
	{
		super(new BaseId(idAsInt), json);
	}
	
	public int getType()
	{
		return getObjectType();
	}

	public static int getObjectType()
	{
		return ObjectType.GOAL;
	}
	
	
	public static boolean canOwnThisType(int type)
	{
		return false;
	}
	
	
	public static boolean canReferToThisType(int type)
	{
		return false;
	}
	
	public String getObjectiveChildrenAsString()
	{
		try
		{
			return objectManager.getPseudoChildren(getRef(), Factor.TAG_INDICATOR_IDS).toString();
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
		return "";
	}
	
	public String getPseudoData(String fieldTag)
	{
		if(fieldTag.equals(PSEUDO_TAG_CHILD_OBJECTIVE_OREF_LIST))
			return getObjectiveChildrenAsString(); 
		
		return super.getPseudoData(fieldTag);
	}
	
	public void clear()
	{
		super.clear();	
		objectiveChildren = new ORefListData();
		
		addField(PSEUDO_TAG_CHILD_OBJECTIVE_OREF_LIST, objectiveChildren);
	}

	public final static String PSEUDO_TAG_CHILD_OBJECTIVE_OREF_LIST = "PseudoTagChildObjectiveORefList";
	
	//FIXME: all OBJECT_NAME reference becaseu the are used in displayes shold be static methods that call EAM.text
	public static final String OBJECT_NAME = "Goal";
	
	ORefListData objectiveChildren;
}
