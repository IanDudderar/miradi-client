/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.project;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandSwitchView;
import org.conservationmeasures.eam.database.DataUpgrader;
import org.conservationmeasures.eam.database.ProjectServer;
import org.conservationmeasures.eam.diagram.DiagramModel;
import org.conservationmeasures.eam.diagram.EAMGraphSelectionModel;
import org.conservationmeasures.eam.diagram.PartialGraphLayoutCache;
import org.conservationmeasures.eam.diagram.cells.DiagramFactor;
import org.conservationmeasures.eam.diagram.cells.DiagramFactorCluster;
import org.conservationmeasures.eam.diagram.cells.EAMGraphCell;
import org.conservationmeasures.eam.diagram.cells.LinkCell;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.exceptions.FutureVersionException;
import org.conservationmeasures.eam.exceptions.OldVersionException;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.DiagramFactorId;
import org.conservationmeasures.eam.ids.DiagramFactorLinkId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.ids.FactorLinkId;
import org.conservationmeasures.eam.ids.IdAssigner;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.main.CommandExecutedEvent;
import org.conservationmeasures.eam.main.CommandExecutedListener;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.CreateObjectParameter;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objectpools.AssignmentPool;
import org.conservationmeasures.eam.objectpools.EAMObjectPool;
import org.conservationmeasures.eam.objectpools.FactorLinkPool;
import org.conservationmeasures.eam.objectpools.FactorPool;
import org.conservationmeasures.eam.objectpools.GoalPool;
import org.conservationmeasures.eam.objectpools.IndicatorPool;
import org.conservationmeasures.eam.objectpools.ObjectivePool;
import org.conservationmeasures.eam.objectpools.ResourcePool;
import org.conservationmeasures.eam.objectpools.TaskPool;
import org.conservationmeasures.eam.objectpools.ViewPool;
import org.conservationmeasures.eam.objects.DiagramFactorLink;
import org.conservationmeasures.eam.objects.EAMObject;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.objects.FactorLink;
import org.conservationmeasures.eam.objects.ProjectMetadata;
import org.conservationmeasures.eam.objects.ProjectResource;
import org.conservationmeasures.eam.objects.ViewData;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;
import org.conservationmeasures.eam.views.diagram.LayerManager;
import org.conservationmeasures.eam.views.noproject.NoProjectView;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.ParentMap;


public class Project
{

	public Project() throws IOException
	{
		this(new ProjectServer());
	}
	
	public Project(ProjectServer databaseToUse) throws IOException
	{
		database = databaseToUse;
		commandExecutedListeners = new Vector();
		
		clear();
	}

	private void clear() throws IOException
	{
		if(diagramSaver != null)
			removeCommandExecutedListener(diagramSaver);
		
		projectInfo = new ProjectInfo();
		objectManager = new ObjectManager(this);
		undoRedoState = new UndoRedoState();
		
		diagramModel = new DiagramModel(this);
		layerManager = new LayerManager();
		threatRatingFramework = new ThreatRatingFramework(this);
		strategyRatingFramework = new StrategyRatingFramework(this);
		graphLayoutCache = new PartialGraphLayoutCache(diagramModel);
		
		diagramSaver = new DiagramSaver();
		addCommandExecutedListener(diagramSaver);
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// simple getters
	
	public IdAssigner getNodeIdAssigner()
	{
		return projectInfo.getFactorAndLinkIdAssigner();
	}
	
	public IdAssigner getAnnotationIdAssigner()
	{
		return projectInfo.getNormalIdAssigner();
	}
	
	public ProjectServer getDatabase()
	{
		return database;
	}
	
	public ObjectManager getObjectManager()
	{
		return objectManager;
	}
	
	public EAMObjectPool getPool(int objectType)
	{
		return objectManager.getPool(objectType);
	}
	
	public FactorPool getFactorPool()
	{
		return objectManager.getNodePool();
	}
	
	public FactorLinkPool getFactorLinkPool()
	{
		return objectManager.getLinkagePool();
	}
	
	public TaskPool getTaskPool()
	{
		return objectManager.getTaskPool();
	}
	
	public ViewPool getViewPool()
	{
		return objectManager.getViewPool();
	}
	
	public ResourcePool getResourcePool()
	{
		return objectManager.getResourcePool();
	}
	
	public IndicatorPool getIndicatorPool()
	{
		return objectManager.getIndicatorPool();
	}

	public ObjectivePool getObjectivePool()
	{
		return objectManager.getObjectivePool();
	}
	
	public GoalPool getGoalPool()
	{
		return objectManager.getGoalPool();
	}
	
	public AssignmentPool getAssignmentPool()
	{
		return objectManager.getAssignmentPool();
	}
	
	public DiagramModel getDiagramModel()
	{
		return diagramModel;
	}
	
	public LayerManager getLayerManager()
	{
		return layerManager;
	}
	
	public String getCurrentView()
	{
		if(!isOpen())
			return NO_PROJECT_VIEW_NAME;
		
		return projectInfo.getCurrentView();
	}
	
	public ViewData getCurrentViewData() throws Exception
	{
		return getViewData(getCurrentView());
	}
	
	public ViewData getViewData(String viewName) throws Exception
	{
		ViewData found = getViewPool().findByLabel(viewName);
		if(found != null)
			return found;
		
		BaseId createdId = createObject(ObjectType.VIEW_DATA);
		setObjectData(ObjectType.VIEW_DATA, createdId, ViewData.TAG_LABEL, viewName);
		return getViewPool().find(createdId);
	}
	
	public ThreatRatingFramework getThreatRatingFramework()
	{
		return threatRatingFramework;
	}
	
	public StrategyRatingFramework getStrategyRatingFramework()
	{
		return strategyRatingFramework;
	}
	
	public GraphLayoutCache getGraphLayoutCache()
	{
		return graphLayoutCache;
	}
	
	public EAMObject findObject(int objectType, BaseId objectId)
	{
		EAMObjectPool pool = getPool(objectType);
		return pool.findObject(objectId);
	}
	
	public Factor findNode(FactorId nodeId)
	{
		return (Factor)findObject(ObjectType.FACTOR, nodeId);
	}
	
	public ProjectInfo getProjectInfo()
	{
		return projectInfo;
	}
	
	public ProjectMetadata getMetadata()
	{
		return (ProjectMetadata)findObject(ObjectType.PROJECT_METADATA, getMetadataId());
	}

	private BaseId getMetadataId()
	{
		return projectInfo.getMetadataId();
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// objects
	
	public void setMetadata(String tag, String value) throws Exception
	{
		setObjectData(ObjectType.PROJECT_METADATA, getMetadataId(), tag, value);
	}
	
	public FactorLinkId obtainRealLinkageId(BaseId proposedId)
	{
		return projectInfo.obtainRealLinkId(proposedId);
	}
	
	public BaseId obtainRealNodeId(BaseId proposedId)
	{
		return projectInfo.obtainRealFactorId(proposedId);
	}
	
	public BaseId createObject(int objectType) throws Exception
	{
		return createObject(objectType, BaseId.INVALID);
	}
	
	public BaseId createObject(int objectType, BaseId objectId) throws Exception
	{
		return createObject(objectType, objectId, null);
	}
	
	public BaseId createObject(int objectType, CreateObjectParameter extraInfo) throws Exception
	{
		return createObject(objectType, BaseId.INVALID, extraInfo);
	}
	
	public BaseId createObject(int objectType, BaseId objectId, CreateObjectParameter extraInfo) throws Exception
	{
		BaseId createdId = objectManager.createObject(objectType, objectId, extraInfo);
		saveProjectInfo();
		return createdId;
	}
	
	public void deleteObject(int objectType, BaseId objectId) throws IOException, ParseException
	{
		objectManager.deleteObject(objectType, objectId);
	}
	
	public void setObjectData(int objectType, BaseId objectId, String fieldTag, String dataValue) throws Exception
	{
		objectManager.setObjectData(objectType, objectId, fieldTag, dataValue);
		if(objectType == ObjectType.FACTOR)
		{
			DiagramModel model = getDiagramModel();
			FactorId modelNodeId = (FactorId)objectId;
			if(model.doesFactorExist(modelNodeId))
			{
				DiagramFactor diagramNode = getDiagramModel().getDiagramFactorByWrappedId(modelNodeId);
				getDiagramModel().updateCell(diagramNode);
			}
		}
	}
	
	public String getObjectData(int objectType, BaseId objectId, String fieldTag)
	{
		return objectManager.getObjectData(objectType, objectId, fieldTag);
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// database
	
	public void createOrOpen(File projectDirectory) throws Exception
	{
		clear();

		if(ProjectServer.isExistingProject(projectDirectory))
			openProject(projectDirectory);
		else
			createProject(projectDirectory);
		
		finishOpening();
	}

	private void createDefaultObjectsIfNeeded() throws Exception
	{
		threatRatingFramework.createDefaultObjectsIfNeeded();
		strategyRatingFramework.createDefaultObjectsIfNeeded();
	}
	
	private void createProjectMetadata() throws Exception
	{
		BaseId createdId = createObject(ObjectType.PROJECT_METADATA);
		projectInfo.setMetadataId(createdId);
		getDatabase().writeProjectInfo(projectInfo);
	}

	private void openProject(File projectDirectory) throws Exception
	{
		if(ProjectServer.readDataVersion(projectDirectory) > ProjectServer.DATA_VERSION)
			throw new FutureVersionException();

		if(ProjectServer.readDataVersion(projectDirectory) < ProjectServer.DATA_VERSION)
			DataUpgrader.attemptUpgrade(projectDirectory);
		
		if(ProjectServer.readDataVersion(projectDirectory) < ProjectServer.DATA_VERSION)
			throw new OldVersionException();

		ProjectServer db = getDatabase();
		db.open(projectDirectory);
		try
		{
			loadProjectInfo();
			objectManager.loadFromDatabase();
			EAM.logDebug("Highest Factor Id: " + getNodeIdAssigner().getHighestAssignedId());
			EAM.logDebug("Highest Normal Id: " + getAnnotationIdAssigner().getHighestAssignedId());
		}
		catch(Exception e)
		{
			close();
			throw e;
		}
	}
	
	private void createProject(File projectDirectory) throws Exception
	{
		getDatabase().create(projectDirectory);
	}
	
	private void loadProjectInfo() throws IOException, ParseException
	{
		getDatabase().readProjectInfo(projectInfo);
	}
	
	private void saveProjectInfo() throws IOException
	{
		getDatabase().writeProjectInfo(projectInfo);
	}

	private void loadThreatRatingFramework() throws Exception
	{
		getThreatRatingFramework().load();
	}
	
	private void loadStrategyRatingFramework() throws Exception
	{
		EnhancedJsonObject json = getDatabase().readRawStrategyRatingFramework();
		strategyRatingFramework = new StrategyRatingFramework(this, json);
	}
	
	private void loadDiagram() throws Exception
	{
		getDatabase().readDiagram(getDiagramModel());
	}

	protected void finishOpening() throws Exception
	{
		if(getMetadataId().isInvalid())
			createProjectMetadata();
		
		loadThreatRatingFramework();
		loadStrategyRatingFramework();
		loadDiagram();
		
		createDefaultObjectsIfNeeded();
		database.writeVersion();

	}

	public void forceMainWindowToSwitchViews(String currentView)
	{
		CommandSwitchView cmd = new CommandSwitchView(currentView);
		fireCommandExecuted(cmd);
	}
	
	public String getFilename()
	{
		if(isOpen())
			return getDatabase().getName();
		return EAM.text("[No Project]");
	}

	public boolean isOpen()
	{
		return getDatabase().isOpen();
	}
	
	public void close() throws Exception
	{
		if(!isOpen())
			return;
		
		try
		{
			getDatabase().close();
		}
		catch (IOException e)
		{
			EAM.logException(e);
		}
		forceMainWindowToSwitchViews(NoProjectView.getViewName());
	}
	
	static public boolean isValidProjectFilename(String candidate)
	{
		if(candidate.length() > 32)
			return false;
		
		char[] asArray = candidate.toCharArray();
		for(int i = 0; i < candidate.length(); ++i)
		{
			char c = asArray[i];
			if(c >= 128)
				continue;
			if(Character.isLetterOrDigit(c))
				continue;
			if(c == ' ' || c == '.' || c == '-')
				continue;
			
			return false;
		}
		return true;
	}

	/////////////////////////////////////////////////////////////////////////////////
	// command execution

	public void executeCommand(Command command) throws CommandFailedException
	{
		try
		{
			isExecuting = true;
			executeWithoutRecording(command);
			recordCommand(command);
		}
		finally
		{
			isExecuting = false;
		}
	}
	
	public void executeCommands(Command[] commands) throws CommandFailedException
	{
		for(int i = 0; i < commands.length; ++i)
			executeCommand(commands[i]);
	}

	public Command undo() throws CommandFailedException
	{
		Command cmd = undoRedoState.popCommandToUndo();
		try
		{
			isExecuting = true;
			undoWithoutRecording(cmd);
			fireCommandUndone(cmd);
			return cmd;
		}
		finally
		{
			isExecuting = false;
		}
	}
	
	public Command redo() throws CommandFailedException
	{
		Command cmd = undoRedoState.popCommandToRedo();
		try
		{
			isExecuting = true;
			executeWithoutRecording(cmd);
			fireCommandExecuted(cmd);
			return cmd;
		}
		finally
		{
			isExecuting = false;
		}
	}

	private void executeWithoutRecording(Command command) throws CommandFailedException
	{
		try 
		{
			command.execute(this);
		} 
		catch (CommandFailedException e) 
		{
			fireCommandFailed(command, e);
			throw(e);
		}
	}
	
	private void undoWithoutRecording(Command command) throws CommandFailedException
	{
		try 
		{
			command.undo(this);
		} 
		catch (CommandFailedException e) 
		{
			fireCommandFailed(command, e);
			throw(e);
		}
	}
	
	public void recordCommand(Command command)
	{
		try
		{
			undoRedoState.pushUndoableCommand(command);
			fireCommandExecuted(command);
		}
		catch (IOException e)
		{
			EAM.logException(e);
		}
	}
	
	public boolean isExecutingACommand()
	{
		return isExecuting;
	}

	public void addCommandExecutedListener(CommandExecutedListener listener)
	{
		EAM.logVerbose("addCommandExecutedListener: " + listener.getClass());
		commandExecutedListeners.add(listener);
	}
	
	public void removeCommandExecutedListener(CommandExecutedListener listener)
	{
		EAM.logVerbose("removeCommandExecutedListener: " + listener.getClass());
		if(!commandExecutedListeners.contains(listener))
			EAM.logWarning("removeCommandExecutedListener not in list: " + listener.getClass());
		commandExecutedListeners.remove(listener);
	}

	void fireCommandExecuted(Command command)
	{
		EAM.logVerbose("Command executed: " + command.toString());
		CommandExecutedEvent event = new CommandExecutedEvent(command);
		for(int i=0; i < commandExecutedListeners.size(); ++i)
		{
			CommandExecutedListener listener = (CommandExecutedListener)commandExecutedListeners.get(i);
			listener.commandExecuted(event);
		}
	}
	
	void fireCommandUndone(Command command)
	{
		EAM.logVerbose("Command undone: " + command.toString());
		CommandExecutedEvent event = new CommandExecutedEvent(command);
		for(int i=0; i < commandExecutedListeners.size(); ++i)
		{
			CommandExecutedListener listener = (CommandExecutedListener)commandExecutedListeners.get(i);
			listener.commandUndone(event);
		}
	}
	
	void fireCommandFailed(Command command, CommandFailedException e)
	{
		for(int i=0; i < commandExecutedListeners.size(); ++i)
		{
			CommandExecutedListener listener = (CommandExecutedListener)commandExecutedListeners.get(i);
			listener.commandFailed(command, e);
		}
	}
	
	public boolean canUndo()
	{
		if(!isOpen())
			return false;
		
		return undoRedoState.canUndo();
	}
	
	public boolean canRedo()
	{
		if(!isOpen())
			return false;
		
		return undoRedoState.canRedo();
	}
	

	
	/////////////////////////////////////////////////////////////////////////////////
	// views
	
	public void switchToView(String viewName) throws CommandFailedException
	{
		if(!isLegalViewName(viewName))
			throw new CommandFailedException("Attempted switch to unknown view: " + viewName);
		
		projectInfo.setCurrentView(viewName);
		
		int newListenerCount = commandExecutedListeners.size();
		if(previousCommandListenerCount != newListenerCount)
			EAM.logDebug("Listener count went from " + previousCommandListenerCount + " to " + newListenerCount);
		previousCommandListenerCount = newListenerCount;
	}

	public boolean isLegalViewName(String viewName)
	{
		return Arrays.asList(getLegalViewNames()).contains(viewName);
	}
	
	public String[] getLegalViewNames()
	{
		return new String[] {
			SUMMARY_VIEW_NAME,
			DIAGRAM_VIEW_NAME,
			NO_PROJECT_VIEW_NAME,
			THREAT_MATRIX_VIEW_NAME,
			BUDGET_VIEW_NAME,
			WORK_PLAN_VIEW_NAME,
			MAP_VIEW_NAME,
			SCHEDULE_VIEW_NAME,
			IMAGES_VIEW_NAME,
			STRATEGIC_PLAN_VIEW_NAME,
			MONITORING_VIEW_NAME,
		};
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// diagram view
	
	public void addDiagramFactorToCluster(DiagramFactorCluster cluster, DiagramFactor node)
	{
		ParentMap parentMap = new ParentMap();
		parentMap.addEntry(node, cluster);
		getGraphLayoutCache().edit(null, null, parentMap, null);
	}

	public void removeDiagramFactorFromCluster(DiagramFactorCluster cluster, DiagramFactor node)
	{
		DiagramFactor[] nodes = {node};
		ParentMap parentMap = ParentMap.create(getDiagramModel(), nodes, true, false);
		getGraphLayoutCache().edit(null, null, parentMap, null);
	}

	public FactorId removeDiagramFactorFromDiagram(DiagramFactorId idToDelete) throws Exception
	{
		DiagramModel model = getDiagramModel();
		DiagramFactor nodeToDelete = model.getDiagramFactorById(idToDelete);
		FactorId modelNodeId = nodeToDelete.getWrappedId();
		model.deleteDiagramFactor(nodeToDelete);
		return modelNodeId;
	}

	public DiagramFactorId addFactorToDiagram(FactorId modelNodeId) throws Exception
	{
		return addFactorToDiagram(modelNodeId, new DiagramFactorId(BaseId.INVALID.asInt()));
	}
	
	public DiagramFactorId addFactorToDiagram(FactorId modelNodeId, DiagramFactorId requestedId) throws Exception
	{
		DiagramModel model = getDiagramModel();
		DiagramFactor node = model.createDiagramFactor(modelNodeId, requestedId);
		updateVisibilityOfSingleFactor(node);
		return node.getDiagramFactorId();
	}
	
	public FactorLinkId removeLinkFromDiagram(DiagramFactorLinkId idToDelete) throws Exception
	{
		DiagramModel model = getDiagramModel();
		DiagramFactorLink linkageToDelete = model.getDiagramFactorLinkById(idToDelete);
		FactorLinkId modelLinkageId = linkageToDelete.getWrappedId();
		model.deleteDiagramFactorLink(linkageToDelete);
		return modelLinkageId;
	}

	public DiagramFactorLinkId addLinkToDiagram(FactorLinkId linkId) throws Exception
	{
		FactorLink cmLinkage = getFactorLinkPool().find(linkId);
		DiagramModel model = getDiagramModel();
		DiagramFactorLink linkage = model.createDiagramFactorLink(cmLinkage);
		return linkage.getDiagramLinkageId();
	}

	protected void writeFactor(FactorId factorId) throws IOException, ParseException
	{
		Factor cmNode = getFactorPool().find(factorId);
		database.writeObject(cmNode);
	}

	public void moveFactors(int deltaX, int deltaY, DiagramFactorId[] ids) throws Exception 
	{
		getDiagramModel().moveFactors(deltaX, deltaY, ids);
	}
	
	public void setSelectionModel(EAMGraphSelectionModel selectionModelToUse)
	{
		selectionModel = selectionModelToUse;
	}
	
	public EAMGraphCell[] getSelectedAndRelatedCells()
	{
		Object[] selectedCells = selectionModel.getSelectionCells();
		Vector cellVector = getAllSelectedCellsWithRelatedLinkages(selectedCells);
		return (EAMGraphCell[])cellVector.toArray(new EAMGraphCell[0]);
	}

	public Vector getAllSelectedCellsWithRelatedLinkages(Object[] selectedCells) 
	{
		DiagramModel model = getDiagramModel();
		Vector selectedCellsWithLinkages = new Vector();
		for(int i=0; i < selectedCells.length; ++i)
		{
			EAMGraphCell cell = (EAMGraphCell)selectedCells[i];
			if(cell.isFactorLink())
			{
				if(!selectedCellsWithLinkages.contains(cell))
					selectedCellsWithLinkages.add(cell);
			}
			else if(cell.isFactor())
			{
				Set linkages = model.getFactorLinks((DiagramFactor)cell);
				for (Iterator iter = linkages.iterator(); iter.hasNext();) 
				{
					EAMGraphCell link = (EAMGraphCell) iter.next();
					if(!selectedCellsWithLinkages.contains(link))
						selectedCellsWithLinkages.add(link);
				}
				selectedCellsWithLinkages.add(cell);
			}
		}
		return selectedCellsWithLinkages;
	}
	
	public boolean isLinked(FactorId nodeId1, FactorId nodeId2)
	{
		return getFactorLinkPool().isLinked(nodeId1, nodeId2);
	}

	public EAMGraphCell[] getOnlySelectedCells()
	{
		Object[] rawCells = selectionModel.getSelectionCells();
		EAMGraphCell[] cells = new EAMGraphCell[rawCells.length];
		for(int i=0; i < cells.length; ++i)
			cells[i] = (EAMGraphCell)rawCells[i];
		return cells;
	}
	
	public DiagramFactor[] getOnlySelectedFactors()
	{
		if(selectionModel == null)
			return new DiagramFactor[0];
		
		Object[] rawCells = selectionModel.getSelectionCells();
		return getOnlySelectedFactors(rawCells);
	}

	public DiagramFactor[] getOnlySelectedFactors(Object[] allSelectedCells)
	{
		Vector nodes = new Vector();
		for(int i = 0; i < allSelectedCells.length; ++i)
		{
			if(((EAMGraphCell)allSelectedCells[i]).isFactor())
				nodes.add(allSelectedCells[i]);
		}
		return (DiagramFactor[])nodes.toArray(new DiagramFactor[0]);
	}
	
	public DiagramFactorLink[] getOnlySelectedLinks()
	{
		if(selectionModel == null)
			return new DiagramFactorLink[0];
		
		Object[] rawCells = selectionModel.getSelectionCells();
		return getOnlySelectedLinks(rawCells);
	}
	
	public DiagramFactorLink[] getOnlySelectedLinks(Object [] allSelectedCells)
	{
		Vector linkages = new Vector();
		for(int i = 0; i < allSelectedCells.length; ++i)
		{
			if(((EAMGraphCell)allSelectedCells[i]).isFactorLink())
			{
				LinkCell cell = (LinkCell)allSelectedCells[i];
				linkages.add(cell.getDiagramFactorLink());
			}
		}
		return (DiagramFactorLink[])linkages.toArray(new DiagramFactorLink[0]);
	}

	
	public int getGridSize()
	{
		return DEFAULT_GRID_SIZE;
	}
	
	public Point getSnapped(int x, int y)
	{
		return getSnapped(new Point(x, y));
	}
	
	public Point getSnapped(Point point)
	{
		int gridSize = getGridSize();
		return new Point(roundTo(point.x, gridSize), roundTo(point.y, gridSize));
	}
	
	int roundTo(int valueToRound, int incrementToRoundTo)
	{
		int sign = 1;
		if(valueToRound < 0)
			sign = -1;
		valueToRound = Math.abs(valueToRound);
		
		int half = incrementToRoundTo / 2;
		valueToRound += half;
		valueToRound -= (valueToRound % incrementToRoundTo);
		return valueToRound * sign;
	}

	public void updateVisibilityOfFactors()
	{
		DiagramModel model = getDiagramModel();
		
		Vector nodes = model.getAllDiagramFactors();
		for(int i = 0; i < nodes.size(); ++i)
		{
			DiagramFactor node = (DiagramFactor)nodes.get(i);
			updateVisibilityOfSingleFactor(node);
		}
		
		getGraphLayoutCache().setVisible(getDiagramModel().getProjectScopeBox(), true);
	}

	public void updateVisibilityOfSingleFactor(DiagramFactor node)
	{
		LayerManager manager = getLayerManager();
		boolean isVisible = manager.isVisible(node);
		getGraphLayoutCache().setVisible(node, isVisible);
	}
	
	
	public ProjectResource[] getAllProjectResources()
	{
		IdList allResourceIds = getResourcePool().getIdList();
		return getResources(allResourceIds);
	}

	public ProjectResource[] getResources(IdList resourceIds)
	{
		ProjectResource[] availableResources = new ProjectResource[resourceIds.size()];
		for(int i = 0; i < availableResources.length; ++i)
			availableResources[i] = getResourcePool().find(resourceIds.get(i));
		return availableResources;
	}

	public void selectFactor(FactorId idToUse)
	{
		try
		{
			DiagramFactor nodeToSelect = diagramModel.getDiagramFactorByWrappedId(idToUse);
			selectionModel.setSelectionCell(nodeToSelect);
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
	}
	
	class DiagramSaver implements CommandExecutedListener
	{
		public void commandExecuted(CommandExecutedEvent event)
		{
			save();
		}

		public void commandUndone(CommandExecutedEvent event)
		{
			save();
		}

		public void commandFailed(Command command, CommandFailedException e)
		{
		}
		
		void save()
		{
			if(!isOpen())
				return;
			
			try
			{
				saveProjectInfo();
				getDatabase().writeDiagram(getDiagramModel());
			}
			catch (IOException e)
			{
				e.printStackTrace();
				EAM.errorDialog(EAM.text("Error|Error writing to project"));
			}
		}

	}

	public static final String MONITORING_VIEW_NAME = "Monitoring Plan";
	public static final String STRATEGIC_PLAN_VIEW_NAME = "Strategic Plan";
	public static final String IMAGES_VIEW_NAME = "Images";
	public static final String SCHEDULE_VIEW_NAME = "Schedule";
	public static final String MAP_VIEW_NAME = "Map";
	public static final String WORK_PLAN_VIEW_NAME = "Work Plan";
	public static final String BUDGET_VIEW_NAME = "Finances";
	public static final String THREAT_MATRIX_VIEW_NAME = "ThreatMatrix";
	public static final String NO_PROJECT_VIEW_NAME = "";
	public static final String DIAGRAM_VIEW_NAME = "Diagram";
	public static final String SUMMARY_VIEW_NAME = "Summary";

	public static final String DEFAULT_VIEW_NAME = SUMMARY_VIEW_NAME;
	
	public static final int DEFAULT_GRID_SIZE = 15;

	ProjectInfo projectInfo;
	ObjectManager objectManager;
	UndoRedoState undoRedoState;
	boolean isExecuting;

	ThreatRatingFramework threatRatingFramework;
	StrategyRatingFramework strategyRatingFramework;
	
	ProjectServer database;
	DiagramModel diagramModel;

	Vector commandExecutedListeners;
	
	LayerManager layerManager;
	EAMGraphSelectionModel selectionModel;
	GraphLayoutCache graphLayoutCache;
	DiagramSaver diagramSaver;
	
	private int previousCommandListenerCount;

}

