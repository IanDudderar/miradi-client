/* 
 * Copyright 2005-2007, Wildlife Conservation Society, 
 * Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
 * Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
 */ 
package org.conservationmeasures.eam.diagram;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.commands.CommandBeginTransaction;
import org.conservationmeasures.eam.commands.CommandEndTransaction;
import org.conservationmeasures.eam.diagram.cells.EAMGraphCell;
import org.conservationmeasures.eam.diagram.cells.FactorCell;
import org.conservationmeasures.eam.diagram.cells.LinkCell;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.DiagramFactorId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.objects.DiagramFactorLink;
import org.conservationmeasures.eam.project.FactorMoveHandler;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.utils.PointList;
import org.conservationmeasures.eam.views.diagram.DiagramView;
import org.conservationmeasures.eam.views.diagram.LinkBendPointsMoveHandler;
import org.conservationmeasures.eam.views.diagram.Properties;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;


public class MouseEventHandler implements MouseListener, GraphSelectionListener
{
	public MouseEventHandler(MainWindow mainWindowToUse)
	{
		mainWindow = mainWindowToUse;
		selectedCells = new Object[0];
	}

	Project getProject()
	{
		return mainWindow.getProject();
	}

	DiagramComponent getDiagram()
	{
		return mainWindow.getDiagramComponent();
	}

	Actions getActions()
	{
		return mainWindow.getActions();
	}

	public void mousePressed(MouseEvent event)
	{
		dragStartedAt = null;
		if(event.isPopupTrigger())
		{
			getDiagram().showContextMenu(event);
			return;
		}

		dragStartedAt = event.getPoint();
		Object cellBeingPressed = getDiagram().getFirstCellForLocation(dragStartedAt.getX(), dragStartedAt.getY());
		if(cellBeingPressed == null)
		{
			dragStartedAt = null;
			return;
		}
		try
		{
			for(int i = 0; i < selectedCells.length; ++i)
			{
				EAMGraphCell selectedCell = (EAMGraphCell)selectedCells[i];
				if((selectedCell).isFactor())
				{
					FactorCell factor = (FactorCell)selectedCells[i];
					factor.setPreviousLocation(factor.getLocation());
					factor.setPreviousSize(factor.getSize());
				}
				if((selectedCell).isFactorLink())
				{
					LinkCell linkCell = (LinkCell)selectedCells[i];
					DiagramFactorLink diagramFactorLink = linkCell.getDiagramFactorLink();
					previousBendPointList = diagramFactorLink.getBendPoints();
				}

			}
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}

	}

	public void mouseReleased(MouseEvent event)
	{
		if(event.isPopupTrigger())
		{
			getDiagram().showContextMenu(event);
			dragStartedAt = null;
			return;
		}

		if(dragStartedAt == null)
			return;
		
		
		try
		{
			getProject().recordCommand(new CommandBeginTransaction());
			Vector selectedFactors = new Vector();
			Vector selectedLinks = new Vector();
			
			for(int i = 0; i < selectedCells.length; ++i)
			{
				EAMGraphCell selectedCell = (EAMGraphCell)selectedCells[i];
				if((selectedCell).isFactor())
					selectedFactors.add(selectedCells[i]);

				if((selectedCell).isFactorLink())
				{
					DiagramFactorLink selectedDiagramLink = ((LinkCell)selectedCells[i]).getDiagramFactorLink();
					selectedLinks.add(selectedDiagramLink);
				}
			}

			if(selectedFactors.size() == 0)
				return;

			DiagramFactorId[] selectedFactorIds = new DiagramFactorId[selectedFactors.size()];
			for(int i = 0; i < selectedFactors.size(); ++i)
			{
				selectedFactorIds[i] = ((FactorCell)selectedFactors.get(i)).getDiagramFactorId();
			}

			Point dragEndedAt = event.getPoint();
			int deltaX = dragEndedAt.x - dragStartedAt.x; 
			int deltaY = dragEndedAt.y - dragStartedAt.y;

			if(deltaX == 0 && deltaY == 0)
				return;

			// adjust for snap
			FactorCell node = (FactorCell)selectedFactors.get(0);
			deltaX = node.getLocation().x - node.getPreviousLocation().x;
			deltaY = node.getLocation().y - node.getPreviousLocation().y;

			new FactorMoveHandler(getProject()).factorsWereMovedOrResized(selectedFactorIds);
			
			DiagramFactorLink[] diagramLinks = (DiagramFactorLink[]) selectedLinks.toArray(new DiagramFactorLink[0]);
			new LinkBendPointsMoveHandler(getProject()).moveLinkBendPoints(diagramLinks, deltaX, deltaY);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog("Unexpected error");
		}
		finally
		{
			getProject().recordCommand(new CommandEndTransaction());
		}

	}

	public void mouseEntered(MouseEvent arg0)
	{
	}

	public void mouseExited(MouseEvent arg0)
	{
	}

	public void mouseClicked(MouseEvent event)
	{
		if(event.getClickCount() == 2)
		{
			try 
			{
				Point at = event.getPoint();
				Properties doer = new Properties(getDiagram());
				doer.setMainWindow(mainWindow);
				doer.setView(mainWindow.getCurrentView());
				doer.setLocation(at);
				doer.doIt();
			} 
			catch (CommandFailedException e) 
			{
				e.printStackTrace();
			}
			event.consume();
		}
	}

	// valueChanged is part of the GraphSelectionListener interface.
	// It is HORRIBLY named, so we delegate to a better-named method.
	// Don't put any code in this method. Put it in selectionChanged.
	public void valueChanged(GraphSelectionEvent event)
	{
		selectionChanged(event);
	}

	public void selectionChanged(GraphSelectionEvent event)
	{
		selectedCells = getDiagram().getSelectionCells();
		getActions().updateActionStates();
		if(mainWindow.getCurrentView().cardName().equals(DiagramView.getViewName()))
		{
			DiagramView view = (DiagramView)mainWindow.getCurrentView();
			view.selectionWasChanged();
		}
	}

	MainWindow mainWindow;
	Point dragStartedAt;
	Object[] selectedCells;
	PointList previousBendPointList;
}
