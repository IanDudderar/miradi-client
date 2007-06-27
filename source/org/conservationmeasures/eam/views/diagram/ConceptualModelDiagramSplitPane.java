/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import javax.swing.JList;

import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.project.Project;

public class ConceptualModelDiagramSplitPane extends DiagramSplitPane
{
	//TODO nima dont pass type
	public ConceptualModelDiagramSplitPane(MainWindow mainWindow, int objectType) throws Exception
	{
		super(mainWindow, objectType);
	}

	public DiagramLegendPanel createLegendPanel(MainWindow mainWindow)
	{
		return new ConceptualModelDiagramLegendPanel(mainWindow);
	}
	
	public JList createPageList(Project project)
	{
		return new ConceptualModelPageList(project);
	}
}
