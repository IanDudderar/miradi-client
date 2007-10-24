/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.planning;

import org.conservationmeasures.eam.dialogs.planning.treenodes.PlanningTreeConceptualModelPageNode;

public class TestPlanningTreeConceptualModelPageNode extends TestPlanningTree
{
	public TestPlanningTreeConceptualModelPageNode(String name)
	{
		super(name);
	}

	public void testPlanningTreeConceptualModelPageNode() throws Exception
	{
		PlanningTreeConceptualModelPageNode node = new PlanningTreeConceptualModelPageNode(project, project.getDiagramObject().getRef());
		assertEquals(1, node.getChildCount());
		assertEquals(getGoal().getRef(), node.getChild(0).getObjectReference());
	}
}
