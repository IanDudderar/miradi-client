/* 
Copyright 2005-2018, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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
package org.miradi.objecthelpers;

import java.util.Vector;

import org.miradi.diagram.ThreatTargetChainWalker;
import org.miradi.main.EAM;
import org.miradi.objects.*;
import org.miradi.project.Project;
import org.miradi.project.threatrating.SimpleThreatRatingFramework;
import org.miradi.project.threatrating.ThreatRatingBundle;
import org.miradi.questions.ThreatRatingModeChoiceQuestion;
import org.miradi.schemas.HumanWelfareTargetSchema;
import org.miradi.schemas.TargetSchema;
import org.miradi.schemas.ThreatStressRatingSchema;
import org.miradi.utils.ThreatStressRatingHelper;
import org.miradi.utils.Translation;
import org.miradi.utils.Utility;

public class ThreatTargetVirtualLinkHelper
{
	public ThreatTargetVirtualLinkHelper(Project projectToUse)
	{
		project = projectToUse;
	}

	public ORefSet getDownstreamTargetsVisTSR(Cause threat)
	{
		ORefSet downstreamTargetRefs = new ORefSet();
		ORefList referringThreatStressRatingRefs = threat.findObjectsThatReferToUs(ThreatStressRatingSchema.getObjectType());
		for (int index = 0; index < referringThreatStressRatingRefs.size(); ++index)
		{
			ThreatStressRating threatStressRating = ThreatStressRating.find(getProject(), referringThreatStressRatingRefs.get(index));
			Stress stress = Stress.find(getProject(), threatStressRating.getStressRef());
			downstreamTargetRefs.addAllRefs(stress.findObjectsThatReferToUs(TargetSchema.getObjectType()));
 			downstreamTargetRefs.addAllRefs(stress.findObjectsThatReferToUs(HumanWelfareTargetSchema.getObjectType()));
		}
		
		return downstreamTargetRefs;
	}
	
	public ORefSet getUpstreamThreatRefsViaTSR(Target target)
	{
		ORefSet upstreamOfTargetThreatRefs = new ORefSet();
		ORefList stressRefs = target.getStressRefs();
		for(int stressIndex = 0; stressIndex < stressRefs.size(); ++stressIndex)
		{
			Stress stress = Stress.find(getProject(), stressRefs.get(stressIndex));
			
			ORefSet upstreamOfStressThreatRefs = getUpstreamThreatRefsViaTSR(stress);
			upstreamOfTargetThreatRefs.addAll(upstreamOfStressThreatRefs);
		}
		
		return upstreamOfTargetThreatRefs;
	}

	private ORefSet getUpstreamThreatRefsViaTSR(Stress stress)
	{
		ORefSet upstreamOfStressThreatRefs = new ORefSet();
		ORefList relevantRatingRefs = stress.findObjectsThatReferToUs(ThreatStressRatingSchema.getObjectType());
		for(int ratingIndex = 0; ratingIndex < relevantRatingRefs.size(); ++ratingIndex)
		{
			ThreatStressRating rating = ThreatStressRating.find(getProject(), relevantRatingRefs.get(ratingIndex));
			upstreamOfStressThreatRefs.add(rating.getThreatRef());
		}
		return upstreamOfStressThreatRefs;
	}

	public String getCalculatedThreatRatingBundleValue(ORef threatRef, ORef targetRef)
	{
		try
		{
			int calculatedThreatRatingBundleValue = calculateThreatRatingBundleValue(threatRef, targetRef);
			if (calculatedThreatRatingBundleValue == 0)
				return "";
			
			return Integer.toString(calculatedThreatRatingBundleValue);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return Translation.getCellTextWhenException();
		}
	}
	
	public int calculateThreatRatingBundleValue(ORef threatRef, ORef targetRef) throws Exception
	{
		return calculateThreatRatingBundleValue(getProject().getMetadata().getThreatRatingMode(), threatRef, targetRef);
	}
	
	public int calculateThreatRatingBundleValue(String threatRatingMode, ORef threatRef, ORef targetRef) throws Exception
	{
		if(threatRatingMode.equals(ThreatRatingModeChoiceQuestion.STRESS_BASED_CODE))
			return calculateStressBasedThreatRating(threatRef, targetRef);

		return calculateSimpleThreatRating(threatRef, targetRef);
	}

	public int calculateSimpleThreatRating(ORef threatRef, ORef targetRef) throws Exception
	{
		SimpleThreatRatingFramework framework = getProject().getSimpleThreatRatingFramework();
		ThreatRatingBundle bundle = framework.getBundle(threatRef, targetRef);
		ValueOption valueOption = framework.getBundleValue(bundle);
		return valueOption.getNumericValue();
	}

	public int calculateStressBasedThreatRating(ORef threatRef, ORef targetRef)
	{
		ORefList ratingRefs = getThreatStressRatingRefs(threatRef, targetRef);
		Vector<Integer> ratingBundleValues = new Vector<Integer>();
		for (int i = 0; i < ratingRefs.size(); ++i)
		{
			ThreatStressRating rating = ThreatStressRating.find(getProject(), ratingRefs.get(i));
			if (rating.isActive())
				ratingBundleValues.add(rating.calculateThreatRating());
		}

		return getProject().getStressBasedThreatFormula().getHighestRatingRule(Utility.convertToIntArray(ratingBundleValues));
	}
	
	public ORef findThreatStressRatingReferringToStress(ORef threatRef, ORef targetRef, ORef stressRef) throws Exception
	{
		ORef threatStressRatingRef = findThreatStressRating(threatRef, targetRef, stressRef);
		if (threatStressRatingRef.isValid())
			return threatStressRatingRef;
		
		throw new Exception("Stress has no matching Threat Stress Rating.  threatRef =" + threatRef + " targetRef =" + targetRef + " stressRef =" + stressRef); 
	}

	//TODO this method needs to use threat and stress to get all referring TSRs, and then return the intersection of the referrers
	public ORef findThreatStressRating(ORef threatRef, ORef targetRef,	ORef stressRef)
	{
		ORefList threatStressRatingRefsToUse = getThreatStressRatingRefs(threatRef, targetRef);
		for(int index = 0; index < threatStressRatingRefsToUse.size(); ++index)
		{
			ORef threatStressRatingRef = threatStressRatingRefsToUse.get(index);
			ThreatStressRating threatStressRating = (ThreatStressRating) getProject().findObject(threatStressRatingRef);
			if (stressRef.equals(threatStressRating.getStressRef()))
				return threatStressRatingRef;
		}
		
		return ORef.createInvalidWithType(ThreatStressRatingSchema.getObjectType());
	}
	
	private ORefList getThreatStressRatingRefs(ORef threatRef, ORef targetRef)
	{
		try
		{
			ThreatStressRatingHelper helper = new ThreatStressRatingHelper(getProject());
			return helper.getRelatedThreatStressRatingRefs(threatRef, targetRef);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return new ORefList();
		}
	}
		
	public Project getProject()
	{
		return project;
	}
	
	public static boolean canSupportThreatRatings(Project projectToUse, Cause threat, ORef targetRef)
	{
		try
		{
			ThreatTargetChainWalker chain = new ThreatTargetChainWalker(projectToUse);
			ORefSet downStreamTargets = chain.getDownstreamTargetRefsFromThreat(threat);
			return downStreamTargets.contains(targetRef);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return false;
		}
	}

	public static boolean isThreatRatingNotApplicable(Project project, ThreatRatingBundle bundle)
	{
		ORef threatRef = new ORef(ObjectType.CAUSE, bundle.getThreatId());
		if (project.findObject(threatRef) == null)
			return false;

		ORef targetRef = new ORef(ObjectType.TARGET, bundle.getTargetId());
		if (project.findObject(targetRef) == null)
			return false;

		return isThreatRatingNotApplicable(project, threatRef, targetRef);
	}

	public static boolean isThreatRatingNotApplicable(Project project, ORef threatRef, ORef targetRef)
	{
		AbstractThreatRatingData threatRatingData = AbstractThreatRatingData.findThreatRatingData(project, threatRef, targetRef);
		return threatRatingData != null && threatRatingData.isThreatRatingNotApplicable();
	}

	private Project project;
}
