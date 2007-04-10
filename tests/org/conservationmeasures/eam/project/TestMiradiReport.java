/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.project;

import org.conservationmeasures.eam.objects.ProjectMetadata;
import org.martus.util.TestCaseEnhanced;

public class TestMiradiReport  extends TestCaseEnhanced
{
	public TestMiradiReport(String name)
	{
		super(name);
	}
	
	
	public void setUp() throws Exception
	{
		super.setUp();
		project = new ProjectForTesting("ttttttt");
	}
	
	public void tearDown() throws Exception
	{
		project.close();
		super.tearDown();
	}
	
	public void testHasLinkage() throws Exception
	{
		//TODO: harded paths are here during initl developement and will be replaced 
		project.setMetadata(ProjectMetadata.TAG_PROJECT_NAME, "this name");
		project.setMetadata(ProjectMetadata.TAG_PROJECT_SCOPE, "this scope");
		project.setMetadata(ProjectMetadata.TAG_SHORT_PROJECT_SCOPE, "this short scope");
		new MiradiReport(project).getPDFReport(
				"D:/Projects/workspace/miradi/source/JasperReports/MiradisReport.jasper", 
				"C:/JasperReports/MardisReport.pdf");
	}
	
	Project project;
}
