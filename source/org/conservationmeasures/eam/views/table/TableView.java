/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */

package org.conservationmeasures.eam.views.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;

import org.conservationmeasures.eam.actions.ActionPrint;
import org.conservationmeasures.eam.diagram.DiagramModel;
import org.conservationmeasures.eam.diagram.DiagramModelEvent;
import org.conservationmeasures.eam.diagram.DiagramModelListener;
import org.conservationmeasures.eam.diagram.nodes.EAMGraphCell;
import org.conservationmeasures.eam.diagram.nodes.Linkage;
import org.conservationmeasures.eam.diagram.nodes.Node;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.views.umbrella.UmbrellaView;
import org.martus.swing.UiScrollPane;
import org.martus.swing.UiTabbedPane;
import org.martus.swing.UiTable;

public class TableView extends UmbrellaView
{
	public TableView(MainWindow mainWindowToUse) 
	{
		super(mainWindowToUse);
		setToolBar(new TableToolBar(mainWindowToUse.getActions()));
		addDiagramViewDoersToMap();

		setLayout(new BorderLayout());
		DiagramModel diagramModel = mainWindowToUse.getProject().getDiagramModel();
		TableNodesModel nodesModel = new TableNodesModel(diagramModel);
		nodesModel.addListener();
		nodesTable = new UiTable(nodesModel);
		JTableHeader header = nodesTable.getTableHeader();
		header.addMouseListener(new ColumnHeaderListener(nodesTable));
		
		TableViewLinkagesModel linkagesModel = new TableViewLinkagesModel(diagramModel);
		linkagesModel.addListener();
		linkagesTable = new UiTable(linkagesModel);
		header = linkagesTable.getTableHeader();
		header.addMouseListener(new ColumnHeaderListener(linkagesTable));

		tabbedPane = new UiTabbedPane();
		tabbedPane.add(EAM.text("Tab|Nodes"),new UiScrollPane(nodesTable));
		tabbedPane.add(EAM.text("Tab|Linkages"),new UiScrollPane(linkagesTable));
		tabbedPane.addChangeListener(new TabbedChangeListener());
		add(tabbedPane, BorderLayout.CENTER);
		setBorder(new LineBorder(Color.BLACK));
	}

	public String cardName() 
	{
		return getViewName();
	}
	
	static public String getViewName()
	{
		return "Table";
	}
	
	public JComponent getPrintableComponent()
	{
		JTable sourceTable = getCurrentTable();
		JTable printTable = new JTable(sourceTable.getModel());
		JScrollPane printPane = new JScrollPane(printTable);
		printPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		printPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		AdjustableDimension tableSize = getTableSize(sourceTable);
		tableSize.addInsets(getBorderInsets(printPane, printPane.getBorder()));
		tableSize.addInsets(getBorderInsets(printPane.getViewport(), printPane.getViewportBorder()));
		printPane.setPreferredSize(tableSize);
		return printPane;
	}
	
	private Insets getBorderInsets(JComponent borderedComponent, Border border)
	{
		if(border == null)
			return new Insets(0, 0, 0, 0);
		return border.getBorderInsets(borderedComponent);
	}
	
	static class AdjustableDimension extends Dimension
	{
		public AdjustableDimension(int initialWidth, int initialHeight)
		{
			super(initialWidth, initialHeight);
		}
		
		public void addInsets(Insets insets)
		{
			width += (insets.left + insets.right);
			height += (insets.top + insets.bottom);
		}
	}

	private JTable getCurrentTable()
	{
		if (tabbedPane.getSelectedIndex() == 0)
			return nodesTable;
		return linkagesTable;
	}
	
	private AdjustableDimension getTableSize(JTable table) 
	{
		int tableHeight = table.getHeight() + table.getTableHeader().getHeight();
		return new AdjustableDimension(table.getWidth(), tableHeight);
	}

	private void addDiagramViewDoersToMap()
	{
		addDoerToMap(ActionPrint.class, new Print());
	}
	
	public boolean anythingToPrint()
	{
		return getCurrentTable().getRowCount() > 0;
	}
	
	class ColumnHeaderListener extends MouseAdapter
	{
		ColumnHeaderListener (UiTable tableToUse)
		{
			table = tableToUse;
		}
		
		public void mouseClicked(MouseEvent e) 
		{
		     int columnToSort = getCurrentTable().getColumnModel().getColumnIndexAtX(e.getX());
		     Vector newIndexes = getNewSortedOrderOfRows(columnToSort);

		     TableViewModel model = (TableViewModel)getCurrentTable().getModel();
		     model.setSortedRowIndexes(newIndexes);
		     getCurrentTable().tableChanged(new TableModelEvent(model));
		     
		}

		private Vector getNewSortedOrderOfRows(int columnToSort) 
		{
		    sortingOrder = -sortingOrder;
			return sortTable((TableViewModel)getCurrentTable().getModel(), columnToSort);
		}
		
		private synchronized Vector sortTable(TableViewModel model, int columnToSort)
		{
			class Sorter implements Comparator
			{
				public Sorter(TableViewModel modelToUse, int column, int sortDirection)
				{
					tableModel = modelToUse;
					columnToSortOn = column;
					sorterDirection = sortDirection;
				}
				public int compare(Object o1, Object o2)
				{
					Object obj1 = tableModel.getValueAtDirect(((Integer)(o1)).intValue(), columnToSortOn);
					Object obj2 = tableModel.getValueAtDirect(((Integer)(o2)).intValue(), columnToSortOn);
					if(obj1 instanceof Integer)
						return ((Integer)obj1).compareTo((Integer)obj2) * sorterDirection;
					return ((String)obj1).compareTo((String)obj2) * sorterDirection;
				}
				TableViewModel tableModel; 
				int columnToSortOn;
				int sorterDirection;
			}

			Vector sortedRowIndexes = new Vector();
			for(int i = 0; i < model.getRowCount(); ++i)
				sortedRowIndexes.add(new Integer(i));

			Collections.sort(sortedRowIndexes, new Sorter(model, columnToSort, sortingOrder));
			return sortedRowIndexes;
		}

		UiTable table;
		int sortingOrder = 1; 
	}
	
	class TabbedChangeListener implements ChangeListener
	{
		public void stateChanged(ChangeEvent e) 
		{
			getActions().updateActionStates();
		}
	}
	
	abstract class TableViewModel extends AbstractTableModel implements DiagramModelListener
	{
		abstract Object getValueAtDirect(int rowIndex, int columnIndex);

		public Object getValueAt(int rowIndex, int columnIndex) 
		{
			int sortedRowIndex = getSortedRowIndex(rowIndex);
			return getValueAtDirect(sortedRowIndex, columnIndex);
		}

		public int getSortedRowIndex(int rowIndex)
		{
			if(sortedRowIndexes.isEmpty())
				return rowIndex;
			return ((Integer)sortedRowIndexes.get(new Integer(rowIndex))).intValue();
		}
		
		public void setSortedRowIndexes(Vector newIndexes)
		{
			sortedRowIndexes.clear();
			for(int i = 0; i < getRowCount(); ++i)
			{
				sortedRowIndexes.put(new Integer(i), newIndexes.get(i));
			}
		}
		
		HashMap sortedRowIndexes = new HashMap();
	}

	class TableNodesModel extends TableViewModel
	{
		public TableNodesModel(DiagramModel diagramModelToUse)
		{
			super();
			diagramModel = diagramModelToUse;
			columnNames = new Vector();
			columnNames.add(EAM.text("Table|Name"));
			columnNames.add(EAM.text("Table|Type"));
			columnNames.add(EAM.text("Table|X"));
			columnNames.add(EAM.text("Table|Y"));
		}
		
		protected void addListener()
		{
			diagramModel.addDiagramModelListener(this);
		}

		public int getColumnCount() 
		{
			return columnNames.size();
		}

		public int getRowCount() 
		{
			return diagramModel.getNodeCount();
		}

		public Object getValueAtDirect(int rowIndex, int columnIndex) 
		{
			try 
			{
				Node node = diagramModel.getNodeByIndex(rowIndex);
				switch (columnIndex)
				{
				case TABLE_COLUMN_NAME:
					return node.getText();
				case TABLE_COLUMN_TYPE:
					return getNodeType(node);
				case TABLE_COLUMN_X:
					return new Integer(node.getLocation().x);
				case TABLE_COLUMN_Y:
					return new Integer(node.getLocation().y);
				default:
					return null;
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				return null;
			}
		}

		public String getNodeType(EAMGraphCell cell)
		{
			if(cell.isLinkage())
				return EAM.text("Linkage");
			Node node = (Node)cell;
			if(node.isGoal())
				return EAM.text("Goal");
			if(node.isThreat())
				return EAM.text("Threat");
			if(node.isIntervention())
				return EAM.text("Intervention");
			return EAM.text("Unknown Type");
		}

		public String getColumnName(int column) 
		{
			return (String)columnNames.get(column);
		}
		
		public void nodeAdded(DiagramModelEvent event) 
		{
			int index = event.getIndex();
			fireTableRowsInserted(index,index);
			EAM.logDebug("DiagramModelListener: NodeAdded");
		}

		public void nodeDeleted(DiagramModelEvent event) 
		{
			int index = event.getIndex();
			fireTableRowsDeleted(index,index);
			EAM.logDebug("DiagramModelListener: NodeDeleted");
		}

		public void nodeChanged(DiagramModelEvent event) 
		{
			int index = event.getIndex();
			fireTableRowsUpdated(index,index);
			EAM.logDebug("DiagramModelListener: NodeChanged");
		}

		public void linkageAdded(DiagramModelEvent event) 
		{
		}

		public void linkageDeleted(DiagramModelEvent event) 
		{
		}
		
		final static int TABLE_COLUMN_NAME = 0;
		final static int TABLE_COLUMN_TYPE = 1;
		final static int TABLE_COLUMN_X = 2;
		final static int TABLE_COLUMN_Y = 3;
		
		private Vector columnNames;
		private DiagramModel diagramModel;
	}
	
	class TableViewLinkagesModel extends TableViewModel
	{
		public TableViewLinkagesModel(DiagramModel diagramModelToUse)
		{
			super();
			diagramModel = diagramModelToUse;
			columnNames = new Vector();
			columnNames.add(EAM.text("Table|From Node"));
			columnNames.add(EAM.text("Table|To Node"));
		}
		
		protected void addListener()
		{
			diagramModel.addDiagramModelListener(this);
		}

		public int getColumnCount() 
		{
			return columnNames.size();
		}

		public int getRowCount() 
		{
			return diagramModel.getLinkageCount();
		}

		public Object getValueAtDirect(int rowIndex, int columnIndex) 
		{
			try 
			{
				Linkage linkage = diagramModel.getLinkageByIndex(rowIndex);
				
				switch (columnIndex)
				{
				case TABLE_COLUMN_FROM:
					return linkage.getFromNode().getText();
				case TABLE_COLUMN_TO:
					return linkage.getToNode().getText();
				default:
					return null;
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				return null;
			}
		}

		public String getColumnName(int column) 
		{
			return (String)columnNames.get(column);
		}
		
		public void nodeAdded(DiagramModelEvent event) 
		{
		}

		public void nodeDeleted(DiagramModelEvent event) 
		{
		}

		public void nodeChanged(DiagramModelEvent event) 
		{
		}
		
		public void linkageAdded(DiagramModelEvent event) 
		{
			int index = event.getIndex();
			fireTableRowsInserted(index,index);
			EAM.logDebug("DiagramModelLinkListener: linkAdded");
		}

		public void linkageDeleted(DiagramModelEvent event) 
		{
			int index = event.getIndex();
			fireTableRowsDeleted(index,index);
			EAM.logDebug("DiagramModelLinkListener: linkDeleted");
		}

		final static int TABLE_COLUMN_FROM = 0;
		final static int TABLE_COLUMN_TO = 1;
		
		private Vector columnNames;
		private DiagramModel diagramModel;
	}
	
	UiTabbedPane tabbedPane;
	UiTable nodesTable;
	UiTable linkagesTable;
}
