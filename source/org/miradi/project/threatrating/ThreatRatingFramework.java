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
package org.miradi.project.threatrating;

import java.util.Vector;

import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objecthelpers.ThreatTargetVirtualLinkHelper;
import org.miradi.objects.Cause;
import org.miradi.objects.Factor;
import org.miradi.objects.Target;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.StaticQuestionManager;
import org.miradi.questions.ThreatRatingQuestion;
import org.miradi.utils.Utility;

abstract public class ThreatRatingFramework
{
	public ThreatRatingFramework(Project projectToUse)
	{
		project = projectToUse;
	}
	
	public Project getProject()
	{
		return project;
	}
	
	public ChoiceItem convertToChoiceItem(int codeAsInt)
	{
		String code = getSafeThreatRatingCode(codeAsInt);
		return convertToChoiceItem(code);
	}
	
	public static String getSafeThreatRatingCode(int codeAsInt)
	{
		switch (codeAsInt)
		{
			case 1: return "1";
			case 2: return "2";
			case 3: return "3";
			case 4: return "4";

			default: return "";
		}
	}

	public ChoiceItem convertToChoiceItem(String code)
	{
		ChoiceQuestion question = StaticQuestionManager.getQuestion(ThreatRatingQuestion.class);
		return question.findChoiceByCode(code);
	}

	public int get2PrimeSummaryRatingValue(Factor factor) throws Exception
	{
		int[] summaryRatingValues = calculateSummaryRatingValues(factor);
		
		return getThreatFormula().getSummaryOfBundlesWithTwoPrimeRule(summaryRatingValues);
	}

	protected int[] calculateSummaryRatingValues(Factor factor) throws Exception
	{
		if (factor.isDirectThreat())
			return calculateSummaryRatingValue((Cause) factor);
		
		if (factor.isTarget())
			return calculateSummaryRatingValue((Target) factor);
		
		return new int[0];
	}
	
	private int[] calculateSummaryRatingValue(Target target) throws Exception
	{
		ORefSet upstreamThreatRefs = getUpstreamThreatRefs(target);

		return calculateSummaryRatingValue(upstreamThreatRefs, new ORefSet(target));
	}

	private int[] calculateSummaryRatingValue(Cause threat) throws Exception
	{
		ORefSet downStreamTargets = getDownstreamTargetRefs(threat);
		
		return calculateSummaryRatingValue(new ORefSet(threat), downStreamTargets);
	}

	private int[] calculateSummaryRatingValue(ORefSet upstreamThreats, ORefSet downstreamTargets) throws Exception
	{
		if (upstreamThreats.size() > 1 && downstreamTargets.size() > 1)
			throw new RuntimeException("Method should only be used to calculate rating from a single factor to multiple up/downstream factors");
		
		Vector<Integer> calculatedSummaryRatingValues = new Vector<Integer>();
		ThreatTargetVirtualLinkHelper threatTargetVirtualLink = new ThreatTargetVirtualLinkHelper(getProject());
		for (ORef threatRef : upstreamThreats)
		{
			for(ORef targetRef : downstreamTargets)
			{
				if (!ThreatTargetVirtualLinkHelper.isThreatRatingNotApplicable(getProject(), threatRef, targetRef))
				{
					int threatRatingBundleValue = threatTargetVirtualLink.calculateThreatRatingBundleValue(threatRef, targetRef);
					calculatedSummaryRatingValues.add(threatRatingBundleValue);
				}
			}
		}
		
		return Utility.convertToIntArray(calculatedSummaryRatingValues);
	}
	
	abstract protected ThreatFormula getThreatFormula();

	abstract public ChoiceItem getThreatThreatRatingValue(ORef threatRef) throws Exception;
	
	abstract protected ORefSet getUpstreamThreatRefs(Target target);
	
	abstract protected ORefSet getDownstreamTargetRefs(Cause threat);
		
	private Project project;
}
