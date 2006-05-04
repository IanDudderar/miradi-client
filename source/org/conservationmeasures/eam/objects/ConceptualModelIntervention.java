/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.objects;

import java.text.ParseException;

import org.conservationmeasures.eam.diagram.nodes.DiagramNode;
import org.json.JSONObject;


public class ConceptualModelIntervention extends ConceptualModelNode
{
	public ConceptualModelIntervention()
	{
		super(DiagramNode.TYPE_INTERVENTION);
		activityIds = new IdList();
	}
	
	public ConceptualModelIntervention(JSONObject json) throws ParseException
	{
		super(DiagramNode.TYPE_INTERVENTION, json);
		String activityIdsAsString = json.optString(TAG_ACTIVITY_IDS, "{}");
		activityIds = new IdList(activityIdsAsString);
	}

	public boolean isIntervention()
	{
		return true;
	}
	
	public boolean canHaveObjectives()
	{
		return true;
	}
	
	public void insertActivityId(int activityId, int insertAt)
	{
		activityIds.insertAt(activityId, insertAt);
	}
	
	public void removeActivityId(int activityId)
	{
		activityIds.removeId(activityId);
	}
	
	public IdList getActivityIds()
	{
		return activityIds;
	}

	public JSONObject toJson()
	{
		JSONObject json = createBaseJsonObject(INTERVENTION_TYPE);
		json.put(TAG_ACTIVITY_IDS, activityIds.toString());
		return json;
	}
	
	public static final String TAG_ACTIVITY_IDS = "ActivityIds";

	IdList activityIds;
}
