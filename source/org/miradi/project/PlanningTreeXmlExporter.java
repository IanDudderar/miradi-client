/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.miradi.project;

import java.io.IOException;

import org.martus.util.UnicodeWriter;
import org.martus.util.xml.XmlUtilities;
import org.miradi.dialogs.planning.upperPanel.PlanningTreeTable;
import org.miradi.dialogs.planning.upperPanel.PlanningTreeTableModel;
import org.miradi.dialogs.planning.upperPanel.PlanningViewBudgetAnnualTotalTableModel;
import org.miradi.dialogs.planning.upperPanel.PlanningViewBudgetAnnualTotalsTable;
import org.miradi.dialogs.planning.upperPanel.PlanningViewFutureStatusTable;
import org.miradi.dialogs.planning.upperPanel.PlanningViewFutureStatusTableModel;
import org.miradi.dialogs.planning.upperPanel.PlanningViewMeasurementTable;
import org.miradi.dialogs.planning.upperPanel.PlanningViewMeasurementTableModel;
import org.miradi.dialogs.tablerenderers.PlanningViewFontProvider;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Indicator;
import org.miradi.objects.Measurement;
import org.miradi.objects.Task;
import org.miradi.utils.CodeList;
import org.miradi.utils.ExportableTableInterface;
import org.miradi.utils.MultiTableCombinedAsOneExporter;
import org.miradi.views.planning.ColumnManager;
import org.miradi.views.planning.RowManager;

import com.java.sun.jtreetable.TreeTableModelAdapter;

public class PlanningTreeXmlExporter
{
	public PlanningTreeXmlExporter(Project projectToUse) throws Exception
	{
		project = projectToUse;
	}
	
	//NOTE: this code was copied from PlanningTreeTablePanel, and
	//that we believe it will go away when we change the report to use an
	//exported tree of refs instead of a table of cell values.              
	private void createTables(CodeList rowsToShow, CodeList columnsToShow) throws Exception
	{
		PlanningTreeTableModel model = new PlanningTreeTableModel(getProject(), rowsToShow, columnsToShow);
		PlanningTreeTable treeTable = new PlanningTreeTable(getProject(), model, new PlanningViewFontProvider());
		
		ORefList fullyExpandedNodeRefs = treeTable.getFullyExpandedRefList();
		treeTable.restoreTreeState(fullyExpandedNodeRefs);
		
		PlanningViewFontProvider fontProvider = new PlanningViewFontProvider();
		TreeTableModelAdapter treeTableModelAdapter = treeTable.getTreeTableAdapter();
		multiTableExporter = new MultiTableCombinedAsOneExporter();
		multiTableExporter.addTable(treeTable);
		if (columnsToShow.contains(Task.PSEUDO_TAG_TASK_BUDGET_DETAIL))
		{
			PlanningViewBudgetAnnualTotalTableModel annualTotalsModel = new PlanningViewBudgetAnnualTotalTableModel(getProject(), treeTableModelAdapter);
			PlanningViewBudgetAnnualTotalsTable annualTotalsTable = new PlanningViewBudgetAnnualTotalsTable(annualTotalsModel, fontProvider);
			multiTableExporter.addTable(annualTotalsTable);
		}
		if (columnsToShow.contains(Measurement.META_COLUMN_TAG))
		{
			PlanningViewMeasurementTableModel measurementModel = new PlanningViewMeasurementTableModel(getProject(), treeTableModelAdapter);
			PlanningViewMeasurementTable measurementTable = new PlanningViewMeasurementTable(measurementModel, fontProvider);
			multiTableExporter.addTable(measurementTable);
		}
		if (columnsToShow.contains(Indicator.META_COLUMN_TAG))
		{
			PlanningViewFutureStatusTableModel futureStatusModel = new PlanningViewFutureStatusTableModel(getProject(), treeTableModelAdapter);
			PlanningViewFutureStatusTable futureStatusTable = new PlanningViewFutureStatusTable(futureStatusModel, fontProvider);
			multiTableExporter.addTable(futureStatusTable);
		}
	}
	
	public void toXmlPlanningTreeTables(UnicodeWriter out) throws Exception
	{
		toXml(out, RowManager.getStrategicPlanRows(), ColumnManager.getStrategicPlanColumns(), "StrategicPlanTree");
		toXml(out, RowManager.getMonitoringPlanRows(), ColumnManager.getMonitoringPlanColumns(), "MonitoringPlanTree");
		toXml(out, RowManager.getWorkPlanRows(), ColumnManager.getWorkPlanColumns(), "WorkPlanTree");
	}
	
	private void toXml(UnicodeWriter out, CodeList rowsToShow, CodeList columnsToShow, String treeName) throws Exception
	{
		createTables(rowsToShow, columnsToShow);		
		int columnCount = multiTableExporter.getColumnCount();
		int rowCount = multiTableExporter.getRowCount();

		out.writeln("<" + treeName + ">");
		for (int row = 0; row < rowCount; ++row)
		{
			BaseObject objectForRow = multiTableExporter.getObjectForRow(row);
			String objectTypeName = getSafeTypeName(objectForRow);
			out.writeln("<Row ObjectTypeName='" + objectTypeName + "'>");
			for (int column = 0; column < columnCount; ++column)
			{
				
				out.write("<" + getElementName(column) + ">");
				String padding = pad(multiTableExporter.getDepth(row), column);
				String safeValue = getSafeValue(multiTableExporter, row, column);
				out.write(padding + safeValue);
				out.writeln("</" + getElementName(column) + ">");
			}

			out.writeln("</Row>");
		}

		out.writeln("</" + treeName + ">");
	}

	private String getSafeTypeName(BaseObject objectForRow)
	{
		if (objectForRow == null)
			return "";
		
		return objectForRow.getTypeName();
	}

	private String getElementName(int column)
	{
		return multiTableExporter.getHeaderFor(column).replaceAll(" ", "");
	}
	
	private String getSafeValue(ExportableTableInterface table, int row, int column)
	{
		Object value = table.getValueAt(row, column);
		if (value == null)
			return "";
		
		return XmlUtilities.getXmlEncoded(value.toString());
	}
	
	private boolean isTreeColumn(int column)
	{
		return (column == 0);
	}
		
	private String pad(int padCount, int column) throws IOException
	{
		if (!isTreeColumn(column))
			return ""; 

		final String FIVE_SPACES = "     ";
		String padding = "";
		for (int i = 0; i < padCount; ++i)
		{
			padding += FIVE_SPACES;
		}
		
		return padding;
	}

	private Project getProject()
	{
		return project;
	}
	
	private Project project;
	private MultiTableCombinedAsOneExporter multiTableExporter;
}
