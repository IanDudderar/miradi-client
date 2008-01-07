/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.summary;


import javax.swing.JToolBar;

import org.conservationmeasures.eam.actions.ActionCreateResource;
import org.conservationmeasures.eam.actions.ActionDeleteResource;
import org.conservationmeasures.eam.actions.ActionModifyResource;
import org.conservationmeasures.eam.actions.ActionTeamCreateMember;
import org.conservationmeasures.eam.actions.ActionTeamRemoveMember;
import org.conservationmeasures.eam.actions.ActionViewPossibleTeamMembers;
import org.conservationmeasures.eam.dialogs.base.ModelessDialogWithClose;
import org.conservationmeasures.eam.dialogs.base.ObjectDataInputPanel;
import org.conservationmeasures.eam.dialogs.resource.PossibleTeamMembersPanel;
import org.conservationmeasures.eam.dialogs.summary.TeamManagementPanel;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.objects.ProjectMetadata;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.utils.FastScrollPane;
import org.conservationmeasures.eam.views.TabbedView;
import org.conservationmeasures.eam.views.summary.doers.TeamCreateMemberDoer;
import org.conservationmeasures.eam.views.summary.doers.ViewPossibleTeamMembers;
import org.conservationmeasures.eam.views.umbrella.CreateResource;
import org.conservationmeasures.eam.views.umbrella.DeleteResource;
import org.conservationmeasures.eam.views.umbrella.ModifyResource;

public class SummaryView extends TabbedView
{
	public SummaryView(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
		addSummaryDoersToMap();
	}

	public String cardName() 
	{
		return getViewName();
	}
	
	static public String getViewName()
	{
		return Project.SUMMARY_VIEW_NAME;
	}

	public void becomeActive() throws Exception
	{
		super.becomeActive();
		
		teamManagementPanel.updateSplitterLocation();
	}
	
	public JToolBar createToolBar()
	{
		return new SummaryToolBar(getMainWindow().getActions());
	}
	
	public void createTabs() throws Exception
	{
		ProjectMetadata metadata = getProject().getMetadata();
		tncSummaryPanel = new TNCSummaryPanel(getProject(), metadata);
		wwfSummaryPanel = new WWFSummaryPanel(getProject(), metadata);
		wcssSummaryPanel =new WCSSummaryPanel(getProject(), metadata); 
		rareSummaryPanel = new RARESummaryPanel(getProject(), metadata);
		fosSummaryPanel = new FOSSummaryPanel(getProject(), metadata);
		
		summaryTeamPanel = new SummaryTeamPanel(getMainWindow(), metadata);
		summaryFinancialPanel = new SummaryFinancialPanel(getMainWindow());
		summaryProjectPanel = new SummaryProjectPanel(getProject(), metadata.getRef());
		summaryScopePanel = new SummaryScopePanel(getProject(), metadata.getRef());
		summaryLocationPanel = new SummaryLocationPanel(getProject(), metadata.getRef());
		summaryPlanningPanel = new SummaryPlanningPanel(getProject(), metadata.getRef());
		summaryOtherOrgPanel = new SummaryOtherOrgPanel(getProject(), metadata.getRef());
				
		addPanelAsTab(summaryProjectPanel);
		addPanelAsTab(summaryTeamPanel);

		teamManagementPanel = new TeamManagementPanel(getProject(), getMainWindow(), getMainWindow().getActions());
		addTab(teamManagementPanel.getPanelDescription(),teamManagementPanel.getIcon(), teamManagementPanel);
		
		addPanelAsTab(summaryScopePanel);
		addPanelAsTab(summaryLocationPanel);
		addPanelAsTab(summaryPlanningPanel);
		addPanelAsTab(summaryFinancialPanel);
		
		addPanelAsTab(tncSummaryPanel);
		
		addPanelAsTab(wwfSummaryPanel);
		addPanelAsTab(wcssSummaryPanel);
		addPanelAsTab(rareSummaryPanel);
		addPanelAsTab(fosSummaryPanel);
		addPanelAsTab(summaryOtherOrgPanel);
	}
	
	void addPanelAsTab(ObjectDataInputPanel panel)
	{
		addTab(panel.getPanelDescription(), new FastScrollPane(panel));
	}

	public void deleteTabs() throws Exception
	{
		summaryProjectPanel.dispose();
		summaryScopePanel.dispose();
		summaryLocationPanel.dispose();
		summaryPlanningPanel.dispose();
		
		summaryTeamPanel.dispose();
		summaryFinancialPanel.dispose();
		tncSummaryPanel.dispose();
		wwfSummaryPanel.dispose();
		wcssSummaryPanel.dispose();
		rareSummaryPanel.dispose();
		fosSummaryPanel.dispose();
		summaryOtherOrgPanel.dispose();
		teamManagementPanel.dispose();
	}

	public void showTeamAddMembersDialog() throws Exception
	{
		PossibleTeamMembersPanel panel = new PossibleTeamMembersPanel(getMainWindow());
		ModelessDialogWithClose dlg = new ModelessDialogWithClose(getMainWindow(), panel, panel.getPanelDescription());
		showFloatingPropertiesDialog(dlg);
		panel.updateSplitterLocation();
	}
	
	private void addSummaryDoersToMap()
	{
		addDoerToMap(ActionViewPossibleTeamMembers.class, new ViewPossibleTeamMembers());
		addDoerToMap(ActionTeamCreateMember.class, new TeamCreateMemberDoer());
		addDoerToMap(ActionTeamRemoveMember.class, new DeleteResource());
		addDoerToMap(ActionCreateResource.class, new CreateResource());
		addDoerToMap(ActionModifyResource.class, new ModifyResource());
		addDoerToMap(ActionDeleteResource.class, new DeleteResource());
	}
	
	private SummaryTeamPanel summaryTeamPanel;
	private SummaryFinancialPanel summaryFinancialPanel;
	private TNCSummaryPanel tncSummaryPanel;

	private WWFSummaryPanel wwfSummaryPanel;
	private WCSSummaryPanel wcssSummaryPanel; 
	private RARESummaryPanel rareSummaryPanel;
	private FOSSummaryPanel fosSummaryPanel;
	
	private SummaryProjectPanel summaryProjectPanel;
	private SummaryScopePanel summaryScopePanel;
	private SummaryLocationPanel summaryLocationPanel;
	private SummaryPlanningPanel summaryPlanningPanel;
	private SummaryOtherOrgPanel summaryOtherOrgPanel;
	private TeamManagementPanel teamManagementPanel;
}
