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
package org.miradi.objects;

import java.awt.Point;

import org.miradi.commands.CommandCreateObject;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.diagram.DiagramModel;
import org.miradi.diagram.cells.DiagramCauseCell;
import org.miradi.diagram.cells.DiagramTargetCell;
import org.miradi.diagram.cells.FactorCell;
import org.miradi.diagram.cells.LinkCell;
import org.miradi.ids.BaseId;
import org.miradi.ids.DiagramFactorId;
import org.miradi.ids.DiagramLinkId;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.project.FactorCommandHelper;
import org.miradi.project.ProjectForTesting;
import org.miradi.utils.PointList;
import org.miradi.views.diagram.LinkCreator;
import org.miradi.views.diagram.TestLinkBendPointsMoveHandler;

public class TestDiagramLink extends ObjectTestCase
{
	public TestDiagramLink(String name)
	{
		super(name);
	}
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		project = ProjectForTesting.createProjectWithDefaultObjects(getName());
		model = project.getTestingDiagramModel();

		ORef strategyRef = project.createObject(ObjectType.STRATEGY);
		cmIntervention = Factor.findFactor(project, strategyRef);
		
		ORef targetRef = project.createObject(ObjectType.TARGET);
		cmTarget = Factor.findFactor(project, targetRef);
	}
	
	@Override
	public void tearDown() throws Exception
	{
		super.tearDown();
		project.close();
		project = null;
	}

	public void testAsObject() throws Exception
	{
		DiagramFactor diagramFactor1 = project.createDiagramFactorAndAddToDiagram(ObjectType.CAUSE);
		DiagramFactor diagramFactor2 = project.createDiagramFactorAndAddToDiagram(ObjectType.CAUSE);
		BaseId factorLinkId = new BaseId(44);
		createDiagramFactorLink(project, diagramFactor1.getWrappedORef(), diagramFactor2.getWrappedORef(), factorLinkId);
		
		verifyFields(ObjectType.DIAGRAM_LINK);
	}

	public void testBasics() throws Exception
	{
		FactorCommandHelper factorCommandHelper = new FactorCommandHelper(project, project.getTestingDiagramModel());
		CommandCreateObject createObject1 = factorCommandHelper.createFactorAndDiagramFactor(ObjectType.CAUSE);
		DiagramFactorId diagramFactorId1 = (DiagramFactorId) createObject1.getCreatedId();
		DiagramFactor diagramFactor1 = (DiagramFactor) project.findObject(ObjectType.DIAGRAM_FACTOR, diagramFactorId1);
		
		CommandCreateObject createObject2 = factorCommandHelper.createFactorAndDiagramFactor(ObjectType.CAUSE);
		DiagramFactorId diagramFactorId2 = (DiagramFactorId) createObject2.getCreatedId();
		DiagramFactor diagramFactor2 = (DiagramFactor) project.findObject(ObjectType.DIAGRAM_FACTOR, diagramFactorId2);
		
		LinkCreator linkCreator = new LinkCreator(project);
		DiagramLink diagramLink = linkCreator.createFactorLinkAndAddToDiagramUsingCommands(project.getTestingDiagramObject(), diagramFactor1, diagramFactor2);
		
		assertEquals("didn't remember from?", diagramFactor1.getId(), diagramLink.getFromDiagramFactorId());
		assertEquals("didn't remember to?", diagramFactor2.getId(), diagramLink.getToDiagramFactorId());
	}
	
	public void testIds() throws Exception
	{
		DiagramCauseCell factor = (DiagramCauseCell) project.createFactorCell(ObjectType.CAUSE);
		DiagramTargetCell diagramTarget = (DiagramTargetCell) project.createFactorCell(ObjectType.TARGET);
		
		BaseId linkId = new BaseId(5);
		DiagramLinkId id = new DiagramLinkId(17);
		DiagramLink linkage = new DiagramLink(getObjectManager(), id);
		linkage.setData(DiagramLink.TAG_WRAPPED_ID, linkId.toString());
		linkage.setData(DiagramLink.TAG_FROM_DIAGRAM_FACTOR_ID, factor.getDiagramFactorId().toString());
		linkage.setData(DiagramLink.TAG_TO_DIAGRAM_FACTOR_ID, diagramTarget.getDiagramFactorId().toString());
		assertEquals(id, linkage.getDiagramLinkId());
		assertEquals(linkId, linkage.getWrappedId());
		assertEquals(factor.getDiagramFactorRef(), linkage.getFromDiagramFactorRef());
		assertEquals(diagramTarget.getDiagramFactorRef(), linkage.getToDiagramFactorRef());
	}
	
	public void testLinkNodes() throws Exception
	{
		DiagramFactor intervention = project.createDiagramFactorAndAddToDiagram(ObjectType.STRATEGY);
		DiagramFactor cause = project.createDiagramFactorAndAddToDiagram(ObjectType.CAUSE);
		CommandCreateObject createModelLinkage = new CommandCreateObject(ObjectType.FACTOR_LINK);
		project.executeCommand(createModelLinkage);
		BaseId modelLinkageId = createModelLinkage.getCreatedId();
		
		ORef factorLinkRef = createModelLinkage.getObjectRef();
		project.setObjectData(factorLinkRef, FactorLink.TAG_FROM_REF, intervention.getWrappedORef().toString());
		project.setObjectData(factorLinkRef, FactorLink.TAG_TO_REF, cause.getWrappedORef().toString());
		
		ORef diagramLinkRef = createDiagramFactorLink(project, intervention.getWrappedORef(), cause.getWrappedORef(), modelLinkageId);
		DiagramObject diagramObject = project.getTestingDiagramObject();
		CommandSetObjectData addLink = CommandSetObjectData.createAppendIdCommand(diagramObject, DiagramObject.TAG_DIAGRAM_FACTOR_LINK_IDS, diagramLinkRef.getObjectId());
		project.executeCommand(addLink);
		
		assertNotNull("link not in model?", model.getDiagramLinkByRef(diagramLinkRef));
		
	}

	private static ORef createDiagramFactorLink(ProjectForTesting projectForTesting, ORef strategyRef, ORef factorRef, BaseId modelLinkageId) throws Exception
	{
		DiagramModel diagramModel = projectForTesting.getTestingDiagramModel();
		FactorCell factorCell = diagramModel.getFactorCellByWrappedRef(strategyRef);
		DiagramFactorId fromDiagramFactorId = factorCell.getDiagramFactorId();
		DiagramFactorId toDiagramFactorId = diagramModel.getFactorCellByWrappedRef(factorRef).getDiagramFactorId();
		
		CommandCreateObject createDiagramLinkCommand =  new CommandCreateObject(ObjectType.DIAGRAM_LINK);
		projectForTesting.executeCommand(createDiagramLinkCommand);
    	
    	final ORef diagramLinkRef = createDiagramLinkCommand.getObjectRef();
		projectForTesting.setObjectData(createDiagramLinkCommand.getObjectRef(), DiagramLink.TAG_WRAPPED_ID, modelLinkageId.toString());
    	projectForTesting.setObjectData(createDiagramLinkCommand.getObjectRef(), DiagramLink.TAG_FROM_DIAGRAM_FACTOR_ID, fromDiagramFactorId.toString());
    	projectForTesting.setObjectData(createDiagramLinkCommand.getObjectRef(), DiagramLink.TAG_TO_DIAGRAM_FACTOR_ID, toDiagramFactorId.toString());

		return diagramLinkRef;
	}
	
	public void testBendPointAlreadyExists() throws Exception
	{
		PointList bendPointList = TestLinkBendPointsMoveHandler.createBendPointList();
		LinkCell linkCell = project.createLinkCellWithBendPoints(bendPointList);	
		DiagramLink diagramLink = linkCell.getDiagramLink();
		
		assertEquals("bend points not added?", 3, diagramLink.getBendPoints().size());
		assertEquals("bend point doestn exist?", true, diagramLink.bendPointAlreadyExists(new Point(1, 1)));
		assertEquals("bend point doestn exist?", false, diagramLink.bendPointAlreadyExists(new Point(4, 4)));
	}
	
	public void testDirection() throws Exception
	{
		ORef diagramLinkRef = project.createDiagramLink();
		DiagramLink diagramLink = DiagramLink.find(project, diagramLinkRef);
		assertEquals(diagramLink.getFromDiagramFactorRef(), diagramLink.getDiagramFactorRef(DiagramLink.FROM));
		assertEquals(diagramLink.getToDiagramFactorRef(), diagramLink.getDiagramFactorRef(DiagramLink.TO));
		
		assertEquals(diagramLink.getToDiagramFactorRef(), diagramLink.getOppositeDiagramFactorRef(DiagramLink.FROM));
		assertEquals(diagramLink.getFromDiagramFactorRef(), diagramLink.getOppositeDiagramFactorRef(DiagramLink.TO));
		
	}
	
	public void testGetOppositeEndId() throws Exception
	{
		DiagramLink diagramLink = DiagramLink.find(getProject(), getProject().createDiagramLink());
		ORef fromRef = diagramLink.getFromDiagramFactorRef();
		ORef toRef = diagramLink.getToDiagramFactorRef();
		
		assertEquals("wrong opposite from factor id?", toRef, diagramLink.getOppositeEndRef(fromRef));
		assertEquals("wrong opposite to factor id?", fromRef, diagramLink.getOppositeEndRef(toRef));
		assertEquals("wrong opposite factor id?", ORef.INVALID, diagramLink.getOppositeEndRef(new ORef(3, new BaseId(4))));
	}
	
	ProjectForTesting project;
	DiagramModel model;
	Factor cmIntervention;
	Factor cmTarget;
}
