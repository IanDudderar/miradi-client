/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objects;

import org.conservationmeasures.eam.commands.CommandCreateObject;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.DiagramFactorId;
import org.conservationmeasures.eam.ids.DiagramFactorLinkId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.ids.FactorLinkId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.main.EAMTestCase;
import org.conservationmeasures.eam.objecthelpers.CreateDiagramFactorLinkParameter;
import org.conservationmeasures.eam.objecthelpers.CreateFactorLinkParameter;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.project.ProjectForTesting;

public class TestObjectFindOwnerAndFindReferrer extends EAMTestCase
{
	public TestObjectFindOwnerAndFindReferrer(String name)
	{
		super(name);
	}

	public void setUp() throws Exception
	{
		project = new ProjectForTesting(getName());
		super.setUp();
	}
	
	public void tearDown() throws Exception
	{
		super.tearDown();
		project.close();
	}

	public void testCauseOwn() throws Exception
	{
		BaseId factorId = project.createFactor(Factor.TYPE_CAUSE);
		BaseId indicatorId = project.addItemToFactorList(factorId, ObjectType.INDICATOR, Factor.TAG_INDICATOR_IDS);
		BaseId objectiveId = project.addItemToFactorList(factorId, ObjectType.OBJECTIVE, Factor.TAG_OBJECTIVE_IDS);
		
		//----------- start test -----------
		
	   	ORef owner = new ORef(ObjectType.CAUSE, factorId);
		verifyOwner(owner, new ORef(ObjectType.INDICATOR, indicatorId));
		verifyOwner(owner, new ORef(ObjectType.OBJECTIVE, objectiveId));
	}
	
	public void testStrategyOwn() throws Exception
	{
		BaseId factorId = project.createFactor(Factor.TYPE_STRATEGY);
		BaseId indicatorId = project.addItemToFactorList(factorId, ObjectType.INDICATOR, Factor.TAG_INDICATOR_IDS);
		BaseId objectiveId = project.addItemToFactorList(factorId, ObjectType.OBJECTIVE, Factor.TAG_OBJECTIVE_IDS);
		
		BaseId taskId = project.createTask(new ORef(ObjectType.STRATEGY, factorId));
		IdList taskList = new IdList(new BaseId[] {taskId});
		project.setObjectData(ObjectType.STRATEGY, factorId, Strategy.TAG_ACTIVITY_IDS, taskList.toString());
		
		//----------- start test -----------
		
	   	ORef owner = new ORef(ObjectType.STRATEGY, factorId);
		verifyOwner(owner, new ORef(ObjectType.INDICATOR, indicatorId));
		verifyOwner(owner, new ORef(ObjectType.OBJECTIVE, objectiveId));
		verifyOwner(owner, new ORef(ObjectType.TASK, taskId));
	}

	public void testTargetOwn() throws Exception
	{
		BaseId factorId = project.createFactor(Factor.TYPE_TARGET);
		BaseId indicatorId = project.addItemToFactorList(factorId, ObjectType.INDICATOR, Factor.TAG_INDICATOR_IDS);
		BaseId goalId = project.addItemToFactorList(factorId, ObjectType.GOAL, Factor.TAG_GOAL_IDS);
		BaseId keaId = project.addItemToFactorList(factorId, ObjectType.KEY_ECOLOGICAL_ATTRIBUTE, Factor.TAG_KEY_ECOLOGICAL_ATTRIBUTE_IDS);
		
		//----------- start test -----------
		
	   	ORef owner = new ORef(ObjectType.TARGET, factorId);
		verifyOwner(owner, new ORef(ObjectType.INDICATOR, indicatorId));
		verifyOwner(owner, new ORef(ObjectType.GOAL, goalId));
		verifyOwner(owner, new ORef(ObjectType.KEY_ECOLOGICAL_ATTRIBUTE, keaId));
	}
	
	public void testTaskOwn() throws Exception
	{
		BaseId factorId = project.createFactor(Factor.TYPE_STRATEGY);
		BaseId taskId = project.createTask(new ORef(ObjectType.STRATEGY,factorId));
		BaseId subTaskId = project.createTask(new ORef(ObjectType.TASK,taskId));
		BaseId assignmentId = project.createAssignment(new ORef(ObjectType.TASK,taskId));
		
		IdList taskList = new IdList(new BaseId[] {taskId});
		project.setObjectData(ObjectType.STRATEGY, factorId, Strategy.TAG_ACTIVITY_IDS, taskList.toString());
		
		IdList subTaskList = new IdList(new BaseId[] {subTaskId});
		project.setObjectData(ObjectType.TASK, taskId, Task.TAG_SUBTASK_IDS, subTaskList.toString());

		IdList assignmentList = new IdList(new BaseId[] {assignmentId});
		project.setObjectData(ObjectType.TASK, taskId, Task.TAG_ASSIGNMENT_IDS, assignmentList.toString());

		//----------- start test -----------
		
	   	ORef owner = new ORef(ObjectType.TASK, taskId);
		verifyOwner(owner, new ORef(ObjectType.TASK, subTaskId));
		verifyOwner(owner, new ORef(ObjectType.ASSIGNMENT, assignmentId));
		
	}
	
	public void testTaskRefer() throws Exception
	{
		BaseId factorId = project.createFactor(Factor.TYPE_STRATEGY);
		BaseId taskId = project.createTask(new ORef(ObjectType.STRATEGY,factorId));
		BaseId subTaskId = project.createTask(new ORef(ObjectType.TASK,taskId));
		
		//----------- start test -----------
		
	   	ORef owner = new ORef(ObjectType.TASK, subTaskId);
		vertifyRefer(owner, new ORef(ObjectType.TASK, taskId));
	}
	
	
	public void testAssignmentRefer() throws Exception
	{
		BaseId factorId = project.createFactor(Factor.TYPE_STRATEGY);
		BaseId taskId = project.createTask(new ORef(ObjectType.STRATEGY,factorId));
		BaseId assignmentId = project.createAssignment(new ORef(ObjectType.TASK,taskId));
		
		BaseId projectResourceId = project.createObject(ObjectType.PROJECT_RESOURCE);
		project.setObjectData(ObjectType.ASSIGNMENT, assignmentId, Assignment.TAG_ASSIGNMENT_RESOURCE_ID, projectResourceId.toString());
		
		BaseId accountingCodeId = project.createObject(ObjectType.ACCOUNTING_CODE);
		project.setObjectData(ObjectType.ASSIGNMENT, assignmentId, Assignment.TAG_ACCOUNTING_CODE, accountingCodeId.toString());
		
		BaseId fundingSourceId = project.createObject(ObjectType.FUNDING_SOURCE);
		project.setObjectData(ObjectType.ASSIGNMENT, assignmentId, Assignment.TAG_FUNDING_SOURCE, fundingSourceId.toString());
		
		BaseId subTaskId = project.createTask(new ORef(ObjectType.ASSIGNMENT,assignmentId));
		project.setObjectData(ObjectType.ASSIGNMENT, assignmentId, Assignment.TAG_ASSIGNMENT_TASK_ID, subTaskId.toString());
		
		//----------- start test -----------
		
	   	ORef owner = new ORef(ObjectType.ASSIGNMENT, assignmentId);
		vertifyRefer(owner, new ORef(ObjectType.PROJECT_RESOURCE, projectResourceId));
		vertifyRefer(owner, new ORef(ObjectType.ACCOUNTING_CODE, accountingCodeId));
		vertifyRefer(owner, new ORef(ObjectType.FUNDING_SOURCE, fundingSourceId));
		vertifyRefer(owner, new ORef(ObjectType.TASK, subTaskId));
	}


	public void testDiagramFactorRefer() throws Exception
	{
		DiagramFactorId diagramFactorId = project.createAndAddFactorToDiagram(Factor.TYPE_STRATEGY);
		DiagramFactor diagramFactor = (DiagramFactor)project.findObject(ObjectType.DIAGRAM_FACTOR, diagramFactorId);
		ORef orefFactor = diagramFactor.getReferencedObjects(ObjectType.STRATEGY).get(0);
		
		//----------- start test -----------
		
	   	ORef owner = new ORef(ObjectType.DIAGRAM_FACTOR, diagramFactorId);
		vertifyRefer(owner, orefFactor);
	}
	
	
	public void testDiagramFactorLinkAndLinkFactorRefer() throws Exception
	{
		//TODO: look at this method to refactor
		FactorId interventionId = project.createNodeAndAddToDiagram(Factor.TYPE_STRATEGY);
		FactorId factorId = project.createNodeAndAddToDiagram(Factor.TYPE_CAUSE);
		
		CreateFactorLinkParameter extraInfo = new CreateFactorLinkParameter(interventionId, factorId);
		CommandCreateObject createModelLinkage = new CommandCreateObject(ObjectType.FACTOR_LINK, extraInfo);
    	project.executeCommand(createModelLinkage);
    	FactorLinkId modelLinkageId = (FactorLinkId)createModelLinkage.getCreatedId();
		
    	DiagramFactorId fromDiagramFactorId = project.createAndAddFactorToDiagram(Factor.TYPE_CAUSE);
		DiagramFactorId toDiagramFactorId =  project.createAndAddFactorToDiagram(Factor.TYPE_TARGET);
		
		CreateDiagramFactorLinkParameter diagramLinkExtraInfo = new CreateDiagramFactorLinkParameter(modelLinkageId, fromDiagramFactorId, toDiagramFactorId);
		CommandCreateObject createDiagramLinkCommand =  new CommandCreateObject(ObjectType.DIAGRAM_LINK, diagramLinkExtraInfo);
    	project.executeCommand(createDiagramLinkCommand);
    	DiagramFactorLinkId diagramFactorLinkId = (DiagramFactorLinkId)createDiagramLinkCommand.getCreatedId();

		//----------- start test -----------

    	
    	ORef linkRef = new ORef(ObjectType.FACTOR_LINK, modelLinkageId);
		vertifyRefer(linkRef, new ORef(ObjectType.STRATEGY, interventionId));
		vertifyRefer(linkRef, new ORef(ObjectType.CAUSE, factorId));
    	
		ORef diagramLinkRef = new ORef(ObjectType.DIAGRAM_LINK, diagramFactorLinkId);
		vertifyRefer(diagramLinkRef, new ORef(ObjectType.DIAGRAM_FACTOR, fromDiagramFactorId));
		vertifyRefer(diagramLinkRef, new ORef(ObjectType.DIAGRAM_FACTOR, toDiagramFactorId));
		vertifyRefer(diagramLinkRef, new ORef(ObjectType.FACTOR_LINK, modelLinkageId));
	}
	
	public void testIndicatorOwn() throws Exception
	{
		BaseId indicatorId = project.createObject(ObjectType.INDICATOR);
		BaseId goalId = project.addItemToIndicatorList(indicatorId, ObjectType.GOAL, Indicator.TAG_GOAL_IDS);
	
		BaseId taskId = project.createTask(new ORef(ObjectType.INDICATOR, indicatorId));
		IdList taskList = new IdList(new BaseId[] {taskId});
		project.setObjectData(ObjectType.INDICATOR, indicatorId, Indicator.TAG_TASK_IDS, taskList.toString());
		
		//----------- start test -----------
		
		ORef owner = new ORef(ObjectType.INDICATOR, indicatorId);
		verifyOwner(owner, new ORef(ObjectType.GOAL, goalId));
		verifyOwner(owner, new ORef(ObjectType.TASK, taskId));
	}
	
	
	public void testKeyEcologicalAttributeOwn() throws Exception
	{
		BaseId keaId = project.createObject(ObjectType.KEY_ECOLOGICAL_ATTRIBUTE);
		BaseId indicatorId = project.addItemToKeyEcologicalAttributeList(keaId, ObjectType.INDICATOR, KeyEcologicalAttribute.TAG_INDICATOR_IDS);
		
		//----------- start test -----------
		
		ORef owner = new ORef(ObjectType.KEY_ECOLOGICAL_ATTRIBUTE, keaId);
		verifyOwner(owner, new ORef(ObjectType.INDICATOR, indicatorId));
	}
	
	
	public void testVeiwDataRefer() throws Exception
	{
		BaseId viewDataId = project.createObject(ObjectType.VIEW_DATA);
		BaseId factorId = project.createFactor(Factor.TYPE_TARGET);
		ORefList oRefList = new ORefList(new ORef[] {new ORef(ObjectType.TARGET, factorId)});
		project.setObjectData(ObjectType.VIEW_DATA, viewDataId, ViewData.TAG_CHAIN_MODE_FACTOR_REFS, oRefList.toString());
		
		//----------- start test -----------
		
		ORef owner = new ORef(ObjectType.VIEW_DATA, viewDataId);
		vertifyRefer(owner, new ORef(ObjectType.TARGET, factorId));
	}
	
	private void vertifyRefer(ORef referrer, ORef referred)
	{
		ORefList foundReferrers1 = BaseObject.findObjectsThatReferToUs(project.getObjectManager(), referrer.getObjectType(), referred);
		assertEquals(1,foundReferrers1.size());
		assertEquals(referrer.getObjectId(), foundReferrers1.get(0).getObjectId());


		BaseObject referredObject =  project.getObjectManager().findObject(referred);
		ORefList foundReferrers2 = referredObject.findObjectThatReferToUs();
		
		
		assertContains(referrer, foundReferrers2.toArray());
		
		
		//TODO following assert seems invalid
		//assertNotEquals("Parentage wrong:", referred, foundReferrers2.get(0));
	}
	
	private void verifyOwner(ORef owner, ORef ref)
	{
		ORef oref = BaseObject.findObjectWhoOwnesUs(project.getObjectManager(), owner.getObjectType(), ref);
		assertEquals(owner, oref);
		
		BaseObject baseObject =  project.getObjectManager().findObject(ref);
		ORef orefOwner = baseObject.getOwnerRef();
		
		assertEquals(oref, orefOwner);
		assertNotEquals("Parentage wrong:", oref, ref);
		assertNotEquals("Parentage wrong:", oref, baseObject.getRef());
	}
	
	
	ProjectForTesting project;
}
