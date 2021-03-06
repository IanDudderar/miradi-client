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
package org.miradi.dialogs.fieldComponents;

import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Icon;

import org.miradi.actions.MiradiAction;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;

public class PanelButton extends MiradiUIButton
{
	public PanelButton(String text)
	{
		super(text);
		setFont(getMainWindow().getUserDataPanelFont());
	}
	
	public PanelButton(Icon icon)
	{
		super(icon);
	}
	
	public PanelButton(String text, Icon icon)
	{
		this(text);
		setIcon(icon);
	}
	
	public PanelButton(Action action)
	{
		super(action);
		setMargin(new Insets(2,2,2,2));
		setFont(getMainWindow().getUserDataPanelFont());
	}

	public PanelButton(MiradiAction action)
	{
		this((Action)action);
		setToolTipText(action.getToolTipText());
	}

	//TODO should not use static ref here
	public MainWindow getMainWindow()
	{
		return EAM.getMainWindow();
	}
}