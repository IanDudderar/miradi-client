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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.miradi.diagram.ThreatTargetChainWalker;
import org.miradi.ids.BaseId;
import org.miradi.ids.FactorId;
import org.miradi.ids.IdList;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objecthelpers.ThreatTargetVirtualLinkHelper;
import org.miradi.objectpools.EAMObjectPool;
import org.miradi.objectpools.RatingCriterionPool;
import org.miradi.objectpools.ValueOptionPool;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Cause;
import org.miradi.objects.Factor;
import org.miradi.objects.RatingCriterion;
import org.miradi.objects.Target;
import org.miradi.objects.ValueOption;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;
import org.miradi.schemas.CauseSchema;
import org.miradi.schemas.RatingCriterionSchema;
import org.miradi.schemas.TargetSchema;
import org.miradi.schemas.ValueOptionSchema;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.utils.Utility;

public class SimpleThreatRatingFramework extends ThreatRatingFramework
{
	public SimpleThreatRatingFramework(Project projectToUse)
	{
		super(projectToUse);
		
		threatTargetChainObject = new ThreatTargetChainWalker(getProject());
		clear();
	}

	public void clear()
	{
		bundles = new HashMap<String, ThreatRatingBundle>();
	}
	
	public SimpleThreatFormula getSimpleThreatFormula()
	{
		return getThreatFormula();
	}

	@Override
	protected SimpleThreatFormula getThreatFormula()
	{
		return new SimpleThreatFormula(this);
	}
		
	public IdList getValueOptionIds()
	{
		return convertToIdList(getValueOptions(), ValueOptionSchema.getObjectType());
	}
	
	private IdList getCriterionIds()
	{
		return convertToIdList(getCriteria(), RatingCriterionSchema.getObjectType());
	}

	private IdList convertToIdList(final BaseObject[] baseObjects, final int objectType)
	{
		ORefList refs = new ORefList(baseObjects);
		
		return refs.convertToIdList(objectType);
	}
	
	public int getBundleCount()
	{
		return bundles.size();
	}
	
	public Collection<ThreatRatingBundle> getAllBundles()
	{
		return bundles.values();
	}
	
	public ValueOption[] getValueOptions()
	{
		EAMObjectPool pool = getProject().getPool(ValueOptionSchema.getObjectType());
		ORefList refs = pool.getORefList();
		ValueOption[] valueOptions = new ValueOption[refs.size()];
		for(int i = 0; i < refs.size(); ++i)
			valueOptions[i] = (ValueOption)ValueOption.find(getProject(), refs.get(i));
		return valueOptions;
	}
	
	public ValueOption getValueOption(BaseId id)
	{
		ValueOptionPool pool = (ValueOptionPool)getProject().getPool(ObjectType.VALUE_OPTION);
		return (ValueOption)pool.findObject(id);
	}
	
	private RatingCriterionPool getCriterionPool()
	{
		return (RatingCriterionPool)getProject().getPool(ObjectType.RATING_CRITERION);
	}
	
	public RatingCriterion[] getCriteria()
	{
		EAMObjectPool pool = getProject().getPool(RatingCriterionSchema.getObjectType());
		ORefList refs = pool.getORefList();
		RatingCriterion[] criteria = new RatingCriterion[refs.size()];
		for(int i = 0; i < refs.size(); ++i)
			criteria[i] = (RatingCriterion)RatingCriterion.find(getProject(), refs.get(i));
		return criteria;
	}
	
	public RatingCriterion getCriterion(BaseId id)
	{
		return (RatingCriterion)getCriterionPool().findObject(id);
	}
	
	public void setScope(ORef threatRef, ORef targetRef, int scopeValue) throws Exception
	{
		ThreatRatingBundle bundle = getBundle(threatRef, targetRef);
		setBundleValue(bundle, CRITERION_SCOPE, scopeValue);
	}
	
	public void setSeverity(ORef threatRef, ORef targetRef, int severityValue) throws Exception
	{
		ThreatRatingBundle bundle = getBundle(threatRef, targetRef);
		setBundleValue(bundle, CRITERION_SEVERITY, severityValue);
	}

	public void setIrreversibility(ORef threatRef, ORef targetRef, int irreversibilityValue) throws Exception
	{
		ThreatRatingBundle bundle = getBundle(threatRef, targetRef);
		setBundleValue(bundle, CRITERION_IRREVERSIBILITY, irreversibilityValue);
	}
	
	private void setBundleValue(ThreatRatingBundle bundle, String criterionLabel, int ratingValue) throws Exception
	{
		RatingCriterion ratingCriterion = findCriterionByLabel(criterionLabel);
		ValueOption valueOption = findValueOptionByNumericValue(ratingValue);
		bundle.setValueId(ratingCriterion.getId(), valueOption.getId());
		saveBundle(bundle);
	}

	public int getScopeNumericValue(ThreatRatingBundle bundle)
	{
		return getNumericValue(bundle, getScopeCriterion());
	}
	
	public ChoiceItem getScopeChoiceItem(ThreatRatingBundle bundle)
	{
		int numericValue = getScopeNumericValue(bundle);
		return convertToChoiceItem(numericValue);
	}

	public int getSeverityNumericValue(ThreatRatingBundle bundle)
	{
		return getNumericValue(bundle, getSeverityCriterion());
	}
	
	public ChoiceItem getSeverityChoiceItem(ThreatRatingBundle bundle)
	{
		int numericValue = getSeverityNumericValue(bundle);
		return convertToChoiceItem(numericValue);
	}
	
	public int getIrreversibilityNumericValue(ThreatRatingBundle bundle)
	{
		return getNumericValue(bundle, getIrreversibilityCriterion());
	}
	
	public ChoiceItem getIrreversibilityChoiceItem(ThreatRatingBundle bundle)
	{
		int numericValue = getIrreversibilityNumericValue(bundle);
		return convertToChoiceItem(numericValue);
	}

	private int getNumericValue(ThreatRatingBundle bundle, RatingCriterion criterion)
	{
		BaseId valueId = bundle.getValueId(criterion.getId());
		ValueOption valueOption = (ValueOption)getProject().findObject(ValueOptionSchema.getObjectType(), valueId);
		return valueOption.getNumericValue();
	}

	public ValueOption getBundleValue(ThreatRatingBundle bundle)
	{
		SimpleThreatFormula formula = getSimpleThreatFormula();
		int numericResult = formula.computeBundleValue(bundle);
		return findValueOptionByNumericValue(numericResult);
	}

	public int getHighestValueForTarget(BaseId targetId)
	{
		ThreatRatingBundle[] bundleArray = getBundlesForThisTarget(targetId);
		int[] bundleValues = extractBundleValues(bundleArray);
		return getSimpleThreatFormula().getHighestRating357Not2Prime(bundleValues);
	}
	
	private int[] extractBundleValues(ThreatRatingBundle[] bundleArray)
	{
		int[] values = new int[bundleArray.length];
		for(int i = 0; i < values.length; ++i)
			values[i] = getBundleValue(bundleArray[i]).getNumericValue();
		return values;
	}

	public ValueOption getProjectMajorityRating()
	{
		Factor[] targets = getProject().getTargetPool().getSortedTargets();
		Vector<Integer> highestValues = new Vector<Integer>();
		for(int i = 0; i < targets.length; ++i)
		{
			int targetRating = getHighestValueForTarget(targets[i].getId());
			if (targetRating > 0)
				highestValues.add(targetRating);
		}
		
		return getMajorityOfNumericValues(Utility.convertToIntArray(highestValues));
	}
	
	public ValueOption getOverallProjectRating()
	{
		ValueOption rollup = getProjectRollupRating();
		ValueOption majority = getProjectMajorityRating();
		if(majority.getNumericValue() > rollup.getNumericValue())
			return majority;
		return rollup;
	}

	@Override
	public ChoiceItem getThreatThreatRatingValue(ORef threatRef) throws Exception
	{
		ValueOption valueOption = getThreatThreatRatingValue(threatRef.getObjectId());
		String code = getSafeThreatRatingCode(valueOption.getNumericValue());
		return new ChoiceItem(code, valueOption.getLabel(), valueOption.getColor());
	}

	public ValueOption getThreatThreatRatingValue(BaseId threatId)
	{
		ThreatRatingBundle[] bundleArray = getBundlesForThisThreat(threatId);
		return getSummaryOfBundles(bundleArray);
	}

	private ThreatRatingBundle[] getBundlesForThisThreat(BaseId threatId)
	{
		HashSet<ThreatRatingBundle> bundlesForThisThreat = new HashSet<ThreatRatingBundle>();
		
		Iterator iter = bundles.values().iterator();
		while(iter.hasNext())
		{
			ThreatRatingBundle bundle = (ThreatRatingBundle)iter.next();
			if(bundle.getThreatId().equals(threatId) && isBundleForLinkedThreatAndTarget(bundle))
				bundlesForThisThreat.add(bundle);
		}
		ThreatRatingBundle[] bundleArray = bundlesForThisThreat.toArray(new ThreatRatingBundle[0]);
		return bundleArray;
	}

	public ValueOption getTargetThreatRatingValue(BaseId targetId)
	{
		ThreatRatingBundle[] bundleArray = getBundlesForThisTarget(targetId);
		return getSummaryOfBundles(bundleArray);
	}
	
	public ChoiceItem getTargetThreatRatingValue(ORef targetRef)
	{
		ValueOption targetRating = getTargetThreatRatingValue(targetRef.getObjectId());
		return convertToChoiceItem(targetRating.getNumericValue());
	}

	private ThreatRatingBundle[] getBundlesForThisTarget(BaseId targetId)
	{
		HashSet<ThreatRatingBundle> bundlesForThisThreat = new HashSet<ThreatRatingBundle>();
		
		Iterator iter = bundles.values().iterator();
		while(iter.hasNext())
		{
			ThreatRatingBundle bundle = (ThreatRatingBundle)iter.next();
			if(bundle.getTargetId().equals(targetId) && isBundleForLinkedThreatAndTarget(bundle))
				bundlesForThisThreat.add(bundle);
		}
		ThreatRatingBundle[] bundleArray = bundlesForThisThreat.toArray(new ThreatRatingBundle[0]);
		return bundleArray;
	}
	
	private ValueOption getProjectRollupRating()
	{
		Factor[] threats = getProject().getCausePool().getDirectThreats();
		int[] numericValues = new int[threats.length];
		for(int i = 0; i < threats.length; ++i)
		{
			ValueOption threatSummary = getThreatThreatRatingValue(threats[i].getId());
			numericValues[i] = threatSummary.getNumericValue();
		}
		return getSummaryOfNumericValues(numericValues);
	}

	public boolean isBundleForLinkedThreatAndTarget(ThreatRatingBundle bundle)
	{
		if (ThreatTargetVirtualLinkHelper.isThreatRatingNotApplicable(getProject(), bundle))
			return false;

		FactorId threatId = bundle.getThreatId();
		ORef threatRef = new ORef(CauseSchema.getObjectType(), threatId);
		Cause threat = Cause.find(getProject(), threatRef);
		if(threat == null)
			return false;
		
		ORef targetRef = new ORef(TargetSchema.getObjectType(), bundle.getTargetId());
		return ThreatTargetVirtualLinkHelper.canSupportThreatRatings(getProject(), threat, targetRef);
	}
	
	private ValueOption getSummaryOfBundles(ThreatRatingBundle[] bundlesToSummarize)
	{
		int[] bundleValues = new int[bundlesToSummarize.length];
		for(int i = 0; i < bundlesToSummarize.length; ++i)
			bundleValues[i] = getBundleValue(bundlesToSummarize[i]).getNumericValue();

		return getSummaryOfNumericValues(bundleValues);
	}

	private ValueOption getSummaryOfNumericValues(int[] bundleValues)
	{
		SimpleThreatFormula formula = getSimpleThreatFormula();
		int numericResult = formula.getSummaryOfBundlesWithTwoPrimeRule(bundleValues);
		return findValueOptionByNumericValue(numericResult);
	}

	private ValueOption getMajorityOfNumericValues(int[] bundleValues)
	{
		SimpleThreatFormula formula = getSimpleThreatFormula();
		int numericResult = formula.getMajority(bundleValues);
		return findValueOptionByNumericValue(numericResult);
	}
	
	@Override
	protected ORefSet getUpstreamThreatRefs(Target target)
	{
		return getThreatTargetChainObject().getUpstreamThreatRefsFromTarget(target);
	}
	
	@Override
	protected ORefSet getDownstreamTargetRefs(Cause threat)
	{
		return getThreatTargetChainObject().getDownstreamTargetRefsFromThreat(threat);
	}

	public RatingCriterion findCriterionByLabel(String label)
	{
		RatingCriterion[] candidates = getCriteria();
		return findCriterionByLabel(candidates, label);
	}

	private RatingCriterion findCriterionByLabel(RatingCriterion[] candidates, String label)
	{
		for(int i = 0; i < candidates.length; ++i)
		{
			RatingCriterion criterion = candidates[i];
			if(criterion.getLabel().equals(label))
				return criterion;
		}
		
		return null;
	}
	
	public ValueOption findValueOptionByNumericValue(int value)
	{
		for(int i = 0; i < getValueOptions().length; ++i)
		{
			ValueOption ratingValueOption = getValueOptions()[i];
			if(ratingValueOption.getNumericValue() == value)
				return ratingValueOption;
		}
		
		return null;
	}
	
	//FIXME medium: creating factorId from id.asInt  (cant cast)
	public ThreatRatingBundle getBundle(ORef threatRef, ORef targetRef) throws Exception
	{
		return getBundle(new FactorId(threatRef.getObjectId().asInt()), new FactorId(targetRef.getObjectId().asInt()));
	}
	
	public ThreatRatingBundle getBundle(FactorId threatId, FactorId targetId) throws Exception
	{
		ThreatRatingBundle existing = bundles.get(getBundleKey(threatId, targetId));
		if(existing != null)
			return existing;
		
		BaseId defaultValueId = getDefaultValueId();
		ThreatRatingBundle newBundle = new ThreatRatingBundle(threatId, targetId, defaultValueId);
		saveBundle(newBundle);
		return newBundle;
	}

	public BaseId getDefaultValueId()
	{
		return findValueOptionByNumericValue(0).getId();
	}
	
	public void saveBundle(ThreatRatingBundle newBundle) throws Exception
	{
		memorize(newBundle);
	}

	private void memorize(ThreatRatingBundle newBundle)
	{
		String key = getBundleKey(newBundle.getThreatId(), newBundle.getTargetId());
		bundles.put(key, newBundle);
	}

	public static String getBundleKey(BaseId threatId, BaseId targetId)
	{
		int intThreatId = threatId.asInt();
		int intTargetId = targetId.asInt();
		return getBundleKey(intThreatId, intTargetId);
	}

	public static String getBundleKey(int intThreatId, int intTargetId)
	{
		String key = intThreatId + "-" + intTargetId;
		return key;
	}
	
	public RatingCriterion getScopeCriterion()
	{
		return findCriterionByLabel(CRITERION_SCOPE);
	}
	
	public RatingCriterion getSeverityCriterion()
	{
		return findCriterionByLabel(CRITERION_SEVERITY);
	}
	
	public RatingCriterion getIrreversibilityCriterion()
	{
		return findCriterionByLabel(CRITERION_IRREVERSIBILITY);
	}
	
	public EnhancedJsonObject toJson()
	{
		return SimpleThreatFrameworkJson.toJson(new Vector<ThreatRatingBundle>(bundles.values()), getValueOptionIds(), getCriterionIds());
	}

	private ThreatTargetChainWalker getThreatTargetChainObject()
	{
		return threatTargetChainObject;
	}
	
	private static final String CRITERION_IRREVERSIBILITY = "Irreversibility";
	private static final String CRITERION_SEVERITY = "Severity";
	private static final String CRITERION_SCOPE = "Scope";
	
	public static final int NONE_VALUE = 0;
	public static final int LOW_RATING_VALUE = 1;
	public static final int MEDIUM_RATING_VALUE = 2;
	public static final int HIGH_RATING_VALUE = 3;
	public static final int VERY_HIGH_RATING_VALUE = 4;

	private HashMap<String, ThreatRatingBundle> bundles;
	private ThreatTargetChainWalker threatTargetChainObject;
}
