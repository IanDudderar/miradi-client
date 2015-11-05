/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
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
package org.miradi.dialogs.planning;

import org.miradi.dialogs.planning.upperPanel.WorkPlanTreeTablePanel;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.CodeToCodeListMap;
import org.miradi.objects.BaseObject;
import org.miradi.objects.TableSettings;
import org.miradi.project.Project;
import org.miradi.questions.CustomPlanningColumnsQuestion;
import org.miradi.questions.WorkPlanVisibleRowsQuestion;
import org.miradi.schemas.ConceptualModelDiagramSchema;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.schemas.ResultsChainDiagramSchema;
import org.miradi.schemas.StrategySchema;
import org.miradi.schemas.TaskSchema;
import org.miradi.utils.CodeList;

public class WorkPlanRowColumnProvider extends AbstractPlanningTreeRowColumnProvider
{
	public WorkPlanRowColumnProvider(Project projectToUse)
	{
		super(projectToUse);
	}
	
	public boolean shouldIncludeResultsChain()
	{
		return getProject().getMetadata().shouldIncludeResultsChain();
	}

	public boolean shouldIncludeConceptualModelPage()
	{
		return getProject().getMetadata().shouldIncludeConceptualModelPage();
	}
	
	public CodeList getColumnCodesToShow() throws Exception
	{
		CodeList columnCodesToShow = new CodeList(new String[] {
				CustomPlanningColumnsQuestion.META_WHO_TOTAL,
				BaseObject.PSEUDO_TAG_WHEN_TOTAL,
				});
		
		columnCodesToShow.addAll(getBudgetColumnCodesFromTableSettingsMap());
		
		return columnCodesToShow;
	}
	
	protected CodeList getBudgetColumnCodesFromTableSettingsMap()
	{
		try
		{
			TableSettings tableSettings = getWorkPlanTableSettings();
			CodeToCodeListMap tableSettingsMap = tableSettings.getTableSettingsMap();
			return tableSettingsMap.getCodeList(TableSettings.WORK_PLAN_BUDGET_COLUMNS_CODELIST_KEY);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return new CodeList();
		}
	}

	public CodeList getRowCodesToShow() throws Exception
	{
		
		TableSettings tableSettings = getWorkPlanTableSettings();
		String code = tableSettings.getData(TableSettings.TAG_WORK_PLAN_VISIBLE_NODES_CODE);
		
		return getRowCodes(code);
	}

	protected CodeList getRowCodes(String code)
	{
		if (code.equals(WorkPlanVisibleRowsQuestion.SHOW_ALL_ROWS_CODE))
			return createAllRowCodeList();
		
		if (code.equals(WorkPlanVisibleRowsQuestion.SHOW_ACTION_RELATED_ROWS_CODE))
			return createActionRelatedRowCodeList();
		
		if (code.equals(WorkPlanVisibleRowsQuestion.SHOW_MONITORING_RELATED_ROWS_CODE))
			return createMonitoringRelatedRowCodeList();
		
		EAM.logDebug("No proper code found, returning empty row codes for work plan tree table");
		return new CodeList();
	}

	public String getDiagramFilter() throws Exception
	{
		TableSettings tableSettings = this.getWorkPlanTableSettings();
		return tableSettings.getData(TableSettings.TAG_WORK_PLAN_DIAGRAM_FILTER);
	}

	private CodeList createMonitoringRelatedRowCodeList()
	{
		return new CodeList(new String[] {
				ConceptualModelDiagramSchema.OBJECT_NAME,
				ResultsChainDiagramSchema.OBJECT_NAME,
				IndicatorSchema.OBJECT_NAME,
				TaskSchema.METHOD_NAME,
				TaskSchema.OBJECT_NAME,
				});
	}

	private CodeList createActionRelatedRowCodeList()
	{
		return new CodeList(new String[] {
				ConceptualModelDiagramSchema.OBJECT_NAME,
				ResultsChainDiagramSchema.OBJECT_NAME,
				StrategySchema.OBJECT_NAME,
				TaskSchema.ACTIVITY_NAME,
				TaskSchema.OBJECT_NAME,
				});
	}

	protected CodeList createAllRowCodeList()
	{
		return new CodeList(new String[] {
				ConceptualModelDiagramSchema.OBJECT_NAME,
				ResultsChainDiagramSchema.OBJECT_NAME,
				StrategySchema.OBJECT_NAME,
				TaskSchema.ACTIVITY_NAME,
				IndicatorSchema.OBJECT_NAME,
				TaskSchema.METHOD_NAME,
				TaskSchema.OBJECT_NAME,
				});
	}
	
	private TableSettings getWorkPlanTableSettings() throws Exception
	{
		return TableSettings.findOrCreate(getProject(), WorkPlanTreeTablePanel.getTabSpecificModelIdentifier());
	}
	
	@Override
	public String getWorkPlanBudgetMode() throws Exception
	{
		return getWorkPlanTableSettings().getData(TableSettings.TAG_WORK_PLAN_VISIBLE_NODES_CODE);
	}
}
