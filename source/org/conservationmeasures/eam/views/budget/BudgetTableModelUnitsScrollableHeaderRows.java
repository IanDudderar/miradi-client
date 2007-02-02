/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.budget;


public class BudgetTableModelUnitsScrollableHeaderRows extends BudgetTableModelSplittableShell
{
	public BudgetTableModelUnitsScrollableHeaderRows(BudgetTableUnitsModel modelToUse)
	{
		super(modelToUse);
	}
	
	public int getColumnCount()
	{
		return model.getRowCount() - 2;
	}
}
