/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.dialogfields;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;

import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.utils.InvalidDateException;
import org.conservationmeasures.eam.utils.InvalidNumberException;

abstract public class ObjectDataInputField implements FocusListener
{
	public ObjectDataInputField(Project projectToUse, int objectTypeToUse, BaseId objectIdToUse, String tagToUse)
	{
		project = projectToUse;
		objectType = objectTypeToUse;
		objectId = objectIdToUse;
		tag = tagToUse;
		allowEdits = true;
	}
	
	abstract public JComponent getComponent();
	abstract public String getText();
	abstract public void setText(String newValue);
	
	public int getObjectType()
	{
		return objectType;
	}
	
	public BaseId getObjectId()
	{
		return objectId;
	}
	
	public void setObjectId(BaseId newId)
	{
		objectId = newId;
	}
	
	public String getTag()
	{
		return tag;
	}
	
	void addFocusListener()
	{
		getComponent().addFocusListener(this);
	}
	
	public void focusGained(FocusEvent e)
	{
	}

	public void focusLost(FocusEvent e)
	{
		saveIfNeeded();
	}
	
	public void updateFromObject()
	{
		saveIfNeeded();
		updateEditableState();
		String text = "";
		if(isValidObject())
			text = project.getObjectData(objectType, objectId, tag);
		if (text.equals(getText()))
			return;
		setText(text);
	}
	
	public boolean allowEdits()
	{
		return allowEdits;
	}

	public boolean isValidObject()
	{
		return (!objectId.isInvalid());
	}
	
	public void setEditable(boolean newState)
	{
		allowEdits = newState;
		updateEditableState();
	}

	public void updateEditableState()
	{
		getComponent().setEnabled(false);
	}
	
	boolean needsToBeSaved()
	{
		return needsSave;
	}
	
	void setNeedsSave()
	{
		needsSave = true;
	}
	
	void clearNeedsSave()
	{
		needsSave = false;
	}
	
	public void saveIfNeeded()
	{
		if(!needsToBeSaved())
			return;
		
		clearNeedsSave();

		if(!isValidObject())
			return;
		
		String newValue = getText();
		String existingValue = project.getObjectData(objectType, objectId, tag);
		if(existingValue.equals(newValue))
			return;
		
		EAM.logDebug("ObjectDataInputField.saveIfNeeded, saving " + tag + ":" + newValue);
		CommandSetObjectData cmd = new CommandSetObjectData(objectType, objectId, tag, newValue);
		try
		{
			project.executeCommand(cmd);
			updateFromObject();
		}
		catch(CommandFailedException e)
		{
			notifyUserOfFailure(e);
			setText(existingValue);
			getComponent().requestFocus();
		}
	}
	
	private void notifyUserOfFailure(CommandFailedException cfe)
	{
		try
		{
			throw(cfe.getCause());
		}
		catch (InvalidDateException ide)
		{
			EAM.errorDialog(EAM.text("Text|Dates must be in YYYY-MM-DD format"));
		}
		catch (InvalidNumberException ine)
		{
			EAM.errorDialog(EAM.text("Text|Must be numeric"));
		}
		catch(Throwable e)
		{
			e.printStackTrace();
			EAM.errorDialog(EAM.text("Text|Unknown error prevented saving this data"));
		}
	}

	Project project;
	private int objectType;
	private BaseId objectId;
	private String tag;
	private boolean allowEdits;
	private boolean needsSave;
}
