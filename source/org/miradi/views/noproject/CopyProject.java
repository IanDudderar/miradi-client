/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
(on behalf of the Conservation Measures Partnership, "CMP") and 
Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 

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
package org.miradi.views.noproject;

import java.io.File;

import org.martus.util.DirectoryLock;
import org.martus.util.DirectoryUtils;
import org.miradi.database.ProjectServer;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.project.Project;
import org.miradi.utils.ModalRenameDialog;

public class CopyProject
{
	static public void doIt(MainWindow mainWindow, File projectToCopy) throws Exception 
	{
		
		if(!ProjectServer.isExistingProject(projectToCopy))
		{
			EAM.notifyDialog(EAM.text("Project does not exist: ") + projectToCopy.getName());
			return;
		}
		
		DirectoryLock directoryLock = new DirectoryLock();

		if (!directoryLock.getDirectoryLock(projectToCopy))
		{
			EAM.notifyDialog(EAM.text("Unable to copy this project because it is in use by another copy of this application:\n") +  projectToCopy.getName());
			return;
		}

		try
		{
			String newName = ModalRenameDialog.showDialog(mainWindow, RenameProjectDoer.RENAME_TEXT);
			if (newName == null)
				return;
			
			Project.validateNewProject(newName);
			
			final String INDENT = "  ";
			String[] body = {EAM.text("This will copy the project"), INDENT + projectToCopy.getName(), EAM.text("to"), INDENT + newName};
			String[] buttons = {EAM.text("Copy"), EAM.text("Cancel"), };
			if(!EAM.confirmDialog(EAM.text("Copy Project"), body, buttons))
				return;
		
			directoryLock.close();
			File newFile = new File(projectToCopy.getParentFile(),newName);
			DirectoryUtils.copyDirectoryTree(projectToCopy, newFile);
		}
		catch (Exception e)
		{
			EAM.notifyDialog("Copy Failed:" +e.getMessage());
		}
		finally
		{
			directoryLock.close();
		}
	}


}