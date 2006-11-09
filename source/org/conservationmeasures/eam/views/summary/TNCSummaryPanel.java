/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.summary;

import org.conservationmeasures.eam.dialogfields.ObjectDataInputField;
import org.conservationmeasures.eam.dialogs.ObjectDataInputPanel;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objects.ProjectMetadata;
import org.conservationmeasures.eam.project.Project;

public class TNCSummaryPanel extends ObjectDataInputPanel
{
	public TNCSummaryPanel(Project projectToUse, ProjectMetadata metadata)
	{
		super(projectToUse, metadata.getType(), metadata.getId());

		ObjectDataInputField workbookVersionNumber = createStringField(metadata.TAG_TNC_WORKBOOK_VERSION_NUMBER);
		workbookVersionNumber.setEditable(false);
		addField(workbookVersionNumber);

		ObjectDataInputField workbookVersionDate = createDateField(metadata.TAG_TNC_WORKBOOK_VERSION_DATE);
		workbookVersionDate.setEditable(false);
		addField(workbookVersionDate);

		ObjectDataInputField databaseDownloadDate = createDateField(metadata.TAG_TNC_DATABASE_DOWNLOAD_DATE);
		databaseDownloadDate.setEditable(false);
		addField(databaseDownloadDate);

		addField(createMultilineField(metadata.TAG_TNC_LESSONS_LEARNED));

		updateFieldsFromProject();
	}
	
	public String getPanelDescription()
	{
		return EAM.text("TNC");
	}
	
}
