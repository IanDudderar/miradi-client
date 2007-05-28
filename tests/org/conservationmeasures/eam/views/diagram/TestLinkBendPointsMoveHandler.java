/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import java.awt.Point;

import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.diagram.cells.LinkCell;
import org.conservationmeasures.eam.main.EAMTestCase;
import org.conservationmeasures.eam.objects.DiagramFactorLink;
import org.conservationmeasures.eam.project.ProjectForTesting;
import org.conservationmeasures.eam.utils.PointList;

public class TestLinkBendPointsMoveHandler extends EAMTestCase
{
	public TestLinkBendPointsMoveHandler(String name)
	{
		super(name);
	}

	public void setUp() throws Exception
	{
		super.setUp();
		project = new ProjectForTesting(getName());
	}

	public void tearDown() throws Exception
	{
		super.tearDown();
		project.close();
	}
	
	public void testMoveBendPoints() throws Exception
	{
		LinkBendPointsMoveHandler handler = new LinkBendPointsMoveHandler(project);
		PointList bendPoints = new PointList();
		Point bendPoint1 = new Point(1, 1);
		Point bendPoint2 = new Point(2, 2);
		Point bendPoint3 = new Point(3, 3);
		bendPoints.add(bendPoint1);
		bendPoints.add(bendPoint2);
		bendPoints.add(bendPoint3);
	
		LinkCell linkCell = createLinkCellWithBendPoints(bendPoints);
		DiagramFactorLink diagramLink = linkCell.getDiagramFactorLink();
		assertEquals("bend points not set?", 3, diagramLink.getBendPoints().size());
		
		int deltaX = 10;
		int deltaY = 10;
		int selectionIndexes[] = {0, 1};
		handler.moveBendPoints(linkCell, selectionIndexes, deltaX, deltaY);
		
		DiagramFactorLink diagramLinkWithMovedPoints = linkCell.getDiagramFactorLink();
		PointList movedBendPoints = diagramLinkWithMovedPoints.getBendPoints();
		assertEquals("lost bendpoint in move?", 3, movedBendPoints.size());
		
		Point movedPoint1 = movedBendPoints.get(selectionIndexes[0]);
		assertEquals("selected bend point not moved?", movedPoint1, new Point(11, 11));
		
		Point movedPoint2 = movedBendPoints.get(selectionIndexes[1]);
		assertEquals("selected bend point not moved?", movedPoint2, new Point(12, 12));
	}
	
	private LinkCell createLinkCellWithBendPoints(PointList bendPoints) throws Exception
	{
		LinkCell linkCell = project.createLinkCell();
	
		CommandSetObjectData bendPointMoveCommand =	CommandSetObjectData.createNewPointList(linkCell.getDiagramFactorLink(), DiagramFactorLink.TAG_BEND_POINTS, bendPoints);
		project.executeCommand(bendPointMoveCommand);
		
		return linkCell;
	}
	
	ProjectForTesting project;

}
