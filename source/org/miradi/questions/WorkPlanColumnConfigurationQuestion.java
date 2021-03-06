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

package org.miradi.questions;

import java.util.Vector;

import org.miradi.objects.BaseObject;

public class WorkPlanColumnConfigurationQuestion extends MultipleSelectStaticChoiceQuestion
{
	public WorkPlanColumnConfigurationQuestion()
	{
		super();
	}

	@Override
	protected ChoiceItem[] createChoices()
	{
		Vector<ChoiceItem> choiceItems = new Vector<ChoiceItem>();

		choiceItems.addAll(createWhoWhenChoices());
		choiceItems.add(createChoiceItem(WorkPlanColumnConfigurationQuestion.META_RESOURCE_ASSIGNMENT_COLUMN_CODE));
		choiceItems.add(createChoiceItem(WorkPlanColumnConfigurationQuestion.META_EXPENSE_ASSIGNMENT_COLUMN_CODE));
		choiceItems.add(createChoiceItem(WorkPlanColumnConfigurationQuestion.META_BUDGET_DETAIL_COLUMN_CODE));
		choiceItems.add(createChoiceItem(BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE));
		choiceItems.add(createChoiceItem(BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_DETAILS));
		choiceItems.addAll(createCustomChoices());
		
		return choiceItems.toArray(new ChoiceItem[0]);
	}

	protected Vector<ChoiceItem> createWhoWhenChoices()
	{
		Vector<ChoiceItem> choiceItems = new Vector<ChoiceItem>();
		choiceItems.add(createChoiceItem(WorkPlanColumnConfigurationQuestion.META_TIMEFRAME_TOTAL, WorkPlanColumnConfigurationQuestion.META_TIMEFRAME_TOTAL_ALT));
		choiceItems.add(createChoiceItem(WorkPlanColumnConfigurationQuestion.META_ASSIGNED_WHO_TOTAL, WorkPlanColumnConfigurationQuestion.META_ASSIGNED_WHO_TOTAL_ALT));
		choiceItems.add(createChoiceItem(WorkPlanColumnConfigurationQuestion.META_ASSIGNED_WHEN_TOTAL, WorkPlanColumnConfigurationQuestion.META_ASSIGNED_WHEN_TOTAL_ALT));

		return choiceItems;
	}
	
	protected Vector<ChoiceItem> createCustomChoices()
	{
		Vector<ChoiceItem> choiceItems = new Vector<ChoiceItem>();
		choiceItems.add(createChoiceItem(COMMENTS_COLUMN_CODE));
		choiceItems.add(createChoiceItem(DETAILS_COLUMN_CODE));
		choiceItems.add(createChoiceItem(INDICATORS_COLUMN_CODE));

		return choiceItems;
	}

	protected static ChoiceItem createChoiceItem(String tag)
	{
		return CustomPlanningColumnsQuestion.createChoiceItem(tag);
	}

	protected static ChoiceItem createChoiceItem(String tag, String customLabel)
	{
		return CustomPlanningColumnsQuestion.createChoiceItem(tag, customLabel);
	}

	public static String getNormalizedBudgetGroupColumnCode(String budgetColumnGroupCode)
	{
		if (getAllPossibleWorkUnitsColumnGroups().contains(budgetColumnGroupCode))
			return WorkPlanColumnConfigurationQuestion.META_RESOURCE_ASSIGNMENT_COLUMN_CODE;
		
		if (getAllPossibleExpensesColumnGroups().contains(budgetColumnGroupCode))
			return WorkPlanColumnConfigurationQuestion.META_EXPENSE_ASSIGNMENT_COLUMN_CODE;
		
		if (getAllPossibleBudgetTotalsColumnGroups().contains(budgetColumnGroupCode))
			return WorkPlanColumnConfigurationQuestion.META_BUDGET_DETAIL_COLUMN_CODE;
		
		throw new RuntimeException("Column code is not a budget column. Code: " + budgetColumnGroupCode);
	}
	
	public static Vector<String> getAllPossibleWorkUnitsColumnGroups()
	{
		Vector<String> columnGroups = new Vector<String>();
		columnGroups.add(WorkPlanColumnConfigurationQuestion.META_RESOURCE_ASSIGNMENT_COLUMN_CODE);
		columnGroups.add(WorkPlanColumnConfigurationQuestion.META_PROJECT_RESOURCE_WORK_UNITS_COLUMN_CODE);
		columnGroups.add(WorkPlanColumnConfigurationQuestion.META_ANALYSIS_WORK_UNITS_COLUMN_CODE);
		columnGroups.add(WorkPlanColumnConfigurationQuestion.META_BUDGET_CATEGORY_ONE_WORK_UNITS_COLUMN_CODE);
		columnGroups.add(WorkPlanColumnConfigurationQuestion.META_BUDGET_CATEGORY_TWO_WORK_UNITS_COLUMN_CODE);
		
		return columnGroups;
	}

	public static Vector<String> getAllPossibleExpensesColumnGroups()
	{
		Vector<String> columnGroups = new Vector<String>();
		columnGroups.add(WorkPlanColumnConfigurationQuestion.META_EXPENSE_ASSIGNMENT_COLUMN_CODE);
		columnGroups.add(WorkPlanColumnConfigurationQuestion.META_ACCOUNTING_CODE_EXPENSE_COLUMN_CODE);
		columnGroups.add(WorkPlanColumnConfigurationQuestion.META_FUNDING_SOURCE_EXPENSE_COLUMN_CODE);
		columnGroups.add(WorkPlanColumnConfigurationQuestion.META_ANALYSIS_EXPENSES_CODE);
		columnGroups.add(WorkPlanColumnConfigurationQuestion.META_BUDGET_CATEGORY_ONE_EXPENSE_COLUMN_CODE);
		columnGroups.add(WorkPlanColumnConfigurationQuestion.META_BUDGET_CATEGORY_TWO_EXPENSE_COLUMN_CODE);
		
		return columnGroups;
	}

	public static Vector<String> getAllPossibleBudgetTotalsColumnGroups()
	{
		Vector<String> columnGroups = new Vector<String>();
		columnGroups.add(WorkPlanColumnConfigurationQuestion.META_BUDGET_DETAIL_COLUMN_CODE);
		columnGroups.add(WorkPlanColumnConfigurationQuestion.META_ACCOUNTING_CODE_BUDGET_DETAILS_COLUMN_CODE);
		columnGroups.add(WorkPlanColumnConfigurationQuestion.META_FUNDING_SOURCE_BUDGET_DETAILS_COLUMN_CODE);
		columnGroups.add(WorkPlanColumnConfigurationQuestion.META_PROJECT_RESOURCE_BUDGET_DETAILS_COLUMN_CODE);
		columnGroups.add(WorkPlanColumnConfigurationQuestion.META_ANALYSIS_BUDGET_DETAILS_COLUMN_CODE);
		columnGroups.add(WorkPlanColumnConfigurationQuestion.META_BUDGET_CATEGORY_ONE_BUDGET_DETAILS_COLUMN_CODE);
		columnGroups.add(WorkPlanColumnConfigurationQuestion.META_BUDGET_CATEGORY_TWO_BUDGET_DETAILS_COLUMN_CODE);
		
		return columnGroups;
	}

	public static final String META_TIMEFRAME_TOTAL = BaseObject.PSEUDO_TAG_TIMEFRAME_TOTAL;
	public static final String META_TIMEFRAME_TOTAL_ALT = META_TIMEFRAME_TOTAL + "Alt";
	public static final String META_TIMEFRAME_TOTAL_ALT_SHORT = META_TIMEFRAME_TOTAL_ALT + "Short";

	public static final String META_ASSIGNED_WHO_TOTAL = CustomPlanningColumnsQuestion.META_ASSIGNED_WHO_TOTAL;
	public static final String META_ASSIGNED_WHO_TOTAL_ALT = META_ASSIGNED_WHO_TOTAL + "Alt";
	public static final String META_ASSIGNED_WHO_TOTAL_ALT_SHORT = META_ASSIGNED_WHO_TOTAL_ALT + "Short";

	public static final String META_ASSIGNED_WHEN_TOTAL = BaseObject.PSEUDO_TAG_ASSIGNED_WHEN_TOTAL;
	public static final String META_ASSIGNED_WHEN_TOTAL_ALT = META_ASSIGNED_WHEN_TOTAL + "Alt";
	public static final String META_ASSIGNED_WHEN_TOTAL_ALT_SHORT = META_ASSIGNED_WHEN_TOTAL_ALT + "Short";

	public static final String META_ANALYSIS_WORK_UNITS_COLUMN_CODE = "MetaAnalysisWorkUnitsColumnCode";
	public static final String META_ANALYSIS_EXPENSES_CODE = "MetaAnalysisExpensesColumnCode";
	public static final String META_ANALYSIS_BUDGET_DETAILS_COLUMN_CODE = "MetaAnalysisBudgetDetailsColumnCode";
	public static final String META_RESOURCE_ASSIGNMENT_COLUMN_CODE = "MetaWorkUnitColumnCode";
	public static final String META_EXPENSE_ASSIGNMENT_COLUMN_CODE = "MetaExpenseAmountColumnCode";
	public static final String META_FUNDING_SOURCE_EXPENSE_COLUMN_CODE = "MetaFundingSourceExpenseColumnCode";
	public static final String META_FUNDING_SOURCE_BUDGET_DETAILS_COLUMN_CODE = "MetaFundingSourceBudgetDetailsColumnCode";
	public static final String META_ACCOUNTING_CODE_EXPENSE_COLUMN_CODE = "MetaAcountingCodeExpenseColumnCode";
	public static final String META_ACCOUNTING_CODE_BUDGET_DETAILS_COLUMN_CODE = "MetaAccountingCodeBudgetDetailsColumnCode";
	public static final String META_BUDGET_DETAIL_COLUMN_CODE = "MetaBudgetDetailColumnCode";
	public static final String META_PROJECT_RESOURCE_WORK_UNITS_COLUMN_CODE = "MetaProjectResourceWorkUnitsColumnCode";
	public static final String META_PROJECT_RESOURCE_BUDGET_DETAILS_COLUMN_CODE = "MetaProjectResourceBudgetDetailsColumnCode";
	
	public static final String META_BUDGET_CATEGORY_ONE_WORK_UNITS_COLUMN_CODE = "MetaBudgetCategoryOneWorkUnitsColumnCode";
	public static final String META_BUDGET_CATEGORY_ONE_EXPENSE_COLUMN_CODE = "MetaBudgetCategoryOneExpenseColumnCode";
	public static final String META_BUDGET_CATEGORY_ONE_BUDGET_DETAILS_COLUMN_CODE = "MetaBudgetCategoryOneBudgetDetailsColumnCode";
	
	public static final String META_BUDGET_CATEGORY_TWO_WORK_UNITS_COLUMN_CODE = "MetaBudgetCategoryTwoWorkUnitsColumnCode";
	public static final String META_BUDGET_CATEGORY_TWO_EXPENSE_COLUMN_CODE = "MetaBudgetCategoryTwoExpenseColumnCode";
	public static final String META_BUDGET_CATEGORY_TWO_BUDGET_DETAILS_COLUMN_CODE = "MetaBudgetCategoryTwoBudgetDetailsColumnCode";
	
	public static final String COMMENTS_COLUMN_CODE = "CommentsColumnCode";
	public static final String DETAILS_COLUMN_CODE = "DetailsColumnCode";
	public static final String INDICATORS_COLUMN_CODE = "IndicatorsColumnCode";
}
