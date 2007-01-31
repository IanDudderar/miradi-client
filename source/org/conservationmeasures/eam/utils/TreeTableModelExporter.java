/* 
 * Copyright 2005-2007, Wildlife Conservation Society, 
 * Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
 * Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
 */ 
package org.conservationmeasures.eam.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import javax.swing.tree.TreePath;

import org.conservationmeasures.eam.views.TreeTableNode;

import com.java.sun.jtreetable.TreeTableModel;

public class TreeTableModelExporter
{
	public TreeTableModelExporter(File fileToUse, TreeTableModel treeTableToUse)
	{
		fileToExportTo = fileToUse;
		treeTableModelToExport = treeTableToUse;	
	}

	public void export() throws Exception
	{
		PrintWriter printWriter = null;
		try
		{
			printWriter = new PrintWriter(fileToExportTo);
			writeTreeTableModel(printWriter);
		}
		catch (IOException e)
		{
			throw new Exception(e);
		}
		finally
		{
			if (printWriter == null)
				throw new IOException();
	
			printWriter.close();
		}
	}
	
	private void writeTreeTableModel(PrintWriter printWriter)
	{
		TreePath[] paths = getAllTreePaths();
		int maxTreeDepth = getMaxTreeDepth(paths);
		
		writeAllPaths(printWriter, paths, maxTreeDepth);
	}
	
	private void writeAllPaths(PrintWriter printWriter, TreePath[] paths, int maxTreeDepth)
	{
		for (int i = 0; i < paths.length; i++)
		{
			writeNode(paths[i], printWriter, maxTreeDepth);
			writeNonTreeColumns(paths[i], printWriter);
	        printWriter.print(NEW_LINE);
		}
	}

	private int getMaxTreeDepth(TreePath[] paths)
	{
		int maxTreeDepthCount = 0;
		for (int i = 0; i < paths.length; i++)
			maxTreeDepthCount = Math.max(paths[i].getPathCount(), maxTreeDepthCount);
		
		return maxTreeDepthCount;
	}

    public TreePath[] getAllTreePaths() 
    {
        TreeTableNode root = (TreeTableNode)treeTableModelToExport.getRoot();
        Vector allPaths = new Vector();
        addPath(new TreePath(root), allPaths);

        return (TreePath[])allPaths.toArray(new TreePath[0]);
    }
    
    public void addPath(TreePath parent, Vector allPathsToUse) 
    {
    	allPathsToUse.add(parent);
        TreeTableNode node = (TreeTableNode)parent.getLastPathComponent();
        if (node.getChildCount() >= 0)
        {
        	for (int i  = 0; i < node.getChildCount(); i++)
            {
        		TreeTableNode n = node.getChild(i);
                TreePath path = parent.pathByAddingChild(n);
                addPath(path, allPathsToUse);
            }
        }
    }

    private void writeNode(TreePath path, PrintWriter printWriter, int maxTreeDepth)
	{
    	int pathCount = path.getPathCount() - INVISIBLE_ROOT;
		writeTabs(printWriter, pathCount);
        Object lastPathComponent = path.getLastPathComponent();

        if (lastPathComponent.toString() == null)
        	return;

        printWriter.print(lastPathComponent.toString());
        int diff = maxTreeDepth  - pathCount;
        writeTabs(printWriter, diff - INVISIBLE_ROOT);
     }

    private void writeNonTreeColumns(TreePath path, PrintWriter printWriter)
	{
    	Object lastPathComponent = path.getLastPathComponent();
        if (lastPathComponent.toString() == null)
        	return;

    	int colCount = treeTableModelToExport.getColumnCount();
		for (int colCounter = 0; colCounter < colCount; colCounter++ )
		{
			String valueToWrite = treeTableModelToExport.getValueAt(lastPathComponent, colCounter).toString();
			printWriter.print(valueToWrite);
			printWriter.print(TAB_SEPARATOR);
		}
	}

	private void writeTabs(PrintWriter printWriter, int tabCountWrites)
	{
    	for (int i = 0; i < tabCountWrites; i++)
    		printWriter.print(TAB_SEPARATOR);
	}

	private File fileToExportTo;
	private TreeTableModel treeTableModelToExport;
	private static final String TAB_SEPARATOR = "\t";
	private static final String NEW_LINE = "\n";
	private static final int INVISIBLE_ROOT = 2;
}
