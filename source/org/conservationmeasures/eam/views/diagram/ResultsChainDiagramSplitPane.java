/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.objecthelpers.ObjectType;

public class ResultsChainDiagramSplitPane extends DiagramSplitPane
{
	public ResultsChainDiagramSplitPane(MainWindow mainWindow) throws Exception
	{
		super(mainWindow, ObjectType.RESULTS_CHAIN_DIAGRAM);
	}

	public DiagramLegendPanel createLegendPanel(MainWindow mainWindow)
	{
		return new ResultsChainDiagramLegendPanel(mainWindow);
	}
	
	public DiagramPageList createPageList(MainWindow mainWindowToUse)
	{
		return new ResultsChainPageList(mainWindowToUse);
	}
}
