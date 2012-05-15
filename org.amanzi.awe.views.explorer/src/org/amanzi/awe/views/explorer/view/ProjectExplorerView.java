/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This
 * library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package org.amanzi.awe.views.explorer.view;

import org.amanzi.awe.views.explorer.providers.ProjectTreeContentProvider;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.IModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.INodeToNodeRelationsModel;
import org.amanzi.neo.services.model.IPropertyStatisticalModel;
import org.amanzi.neo.services.model.IRenderableModel;
import org.amanzi.neo.services.ui.enums.EventsType;
import org.amanzi.neo.services.ui.events.AnalyseEvent;
import org.amanzi.neo.services.ui.events.EventManager;
import org.amanzi.neo.services.ui.events.IEventsListener;
import org.amanzi.neo.services.ui.events.ShowOnMapEvent;
import org.amanzi.neo.services.ui.events.UpdateDataEvent;
import org.amanzi.neo.services.ui.providers.CommonViewLabelProvider;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * project explorer view
 * 
 * @author Vladislav_Kondratenko
 * @since 0.3
 */
public class ProjectExplorerView extends ViewPart {
	/*
	 * ID of this View
	 */
	public static final String PROJECT_EXPLORER_ID = "org.amanzi.awe.views.explorer.view.ProjectExplorer";
	/*
	 * required views id
	 */
	public static final String PROPERTY_TABLE_VIEW_ID = "org.amanzi.awe.views.property.views.PropertyTableView";
	public static final String NETWORK_TREE_VIEW_ID = "org.amanzi.awe.views.network.views.NewNetworkTreeView";
	public static final String NODE2NODE_VIEW_ID = "org.amanzi.awe.views.neighbours.views.NodeToNodeRelationsView";
	public static final String DRIVE_INUQIER_VIEW_ID = "org.amanzi.awe.views.drive.views.NewDriveInquirerView";
	public static final String DISTRIBUTION_ANALYSE_VIEW_ID = "org.amanzi.awe.views.reuse.views.DistributionAnalyzerView";

	private static final double ZOOM = 0d;
	/*
	 * TreeViewer for database Nodes
	 */
	protected TreeViewer viewer;

	/**
	 * event manager;
	 */
	private EventManager eventManager;

	/**
	 * The constructor.
	 */
	public ProjectExplorerView() {
		eventManager = EventManager.getInstance();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {

		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		setProviders();
		viewer.setInput(getSite());
		viewer.setComparer(new IElementComparer() {

			@Override
			public int hashCode(Object element) {
				return 0;
			}

			@Override
			public boolean equals(Object a, Object b) {
				if (a instanceof IModel && b instanceof IModel) {
					IModel aM = (IModel) a;
					IModel bM = (IModel) b;
					return aM.getName().equals(bM.getName())
							&& aM.getClass().equals(bM.getClass());
				}
				return a == null ? b == null : a.equals(b);
			}
		});

		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				openModelInViewBySelection(((IStructuredSelection) event
						.getViewer().getSelection()));
			}
		});

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				openModelInViewBySelection((IStructuredSelection) event
						.getSelection());
			}
		});

		hookContextMenu();
		getSite().setSelectionProvider(viewer);
		setLayout(parent);
		addListeners();
	}

	/**
	 * Open selected INetworkModel in NetworkTreeView
	 * 
	 * @param selection
	 *            IStructuredSelection
	 */
	private void openModelInViewBySelection(IStructuredSelection selection) {
		Object selectionObject = selection.getFirstElement();
		String source = StringUtils.EMPTY;
		if (selectionObject instanceof INetworkModel) {
			source = NETWORK_TREE_VIEW_ID;
		}
		if (!source.isEmpty()) {
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().showView(NETWORK_TREE_VIEW_ID);
			} catch (PartInitException e) {				
			}
			eventManager.fireEvent(new AnalyseEvent((IModel) selectionObject,
					source));
		}
	}

	/**
	 * add required Listener
	 */
	@SuppressWarnings("unchecked")
	private void addListeners() {
		eventManager.addListener(EventsType.UPDATE_DATA,
				new UpdateDataHandling());
	}

	/**
	 * <p>
	 * describe listener to refresh Project Explorer View
	 * </p>
	 * 
	 * @author Kondratenko_Vladislav
	 * @since 1.0.0
	 */
	private class UpdateDataHandling implements
			IEventsListener<UpdateDataEvent> {
		@Override
		public void handleEvent(UpdateDataEvent data) {
			viewer.refresh();
		}

		@Override
		public Object getSource() {
			return null;
		}

	}

	/**
	 * Creates a popup menu
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ProjectExplorerView.this.fillContextMenu(manager);
			}
		});

		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	/**
	 * fill context menu with required items
	 */
	protected void fillContextMenu(IMenuManager manager) {
		ShowPropertiesAction select = new ShowPropertiesAction(
				(IStructuredSelection) viewer.getSelection(),
				ProjectExplorerViewMessages.SHOW_IN_PROPERTY_TABLE_ITEM);
		if (select.isEnabled()) {
			manager.add(select);
		}
		ShowInUniqView showInN2N = new ShowInUniqView(
				(IStructuredSelection) viewer.getSelection());
		if (showInN2N.isEnabled()) {
			manager.add(showInN2N);
		}

		DistributionAnalyse showIndDistribute = new DistributionAnalyse(
				(IStructuredSelection) viewer.getSelection(),
				ProjectExplorerViewMessages.SHOW_IN_DISTRIBUTION_ANALYSE_ITEM);
		if (showIndDistribute.isEnabled()) {
			manager.add(showIndDistribute);
		}

		ShowOnMapAction showOnMapAction = new ShowOnMapAction(
				(IStructuredSelection) viewer.getSelection(), ProjectExplorerViewMessages.SHOW_ON_MAP);
		if (showOnMapAction.isEnabled()) {
			manager.add(showOnMapAction);
		}

		StarToolAnalyseAction starTool = new StarToolAnalyseAction(
				(IStructuredSelection) viewer.getSelection(), ProjectExplorerViewMessages.STAR_TOOL_ANALYSE);

		if (starTool.isEnabled()) {
			manager.add(starTool);
		}

	}

	/**
	 * action describes show properties actions for drive or n2n view
	 */
	private class ShowInUniqView extends Action {
		private IModel model = null;
		private boolean enabled;
		private String text;
		private String viewToFire;

		@Override
		public String getText() {
			return text;
		}

		/**
		 * @param selection
		 * @param propertyTableItem
		 */
		public ShowInUniqView(IStructuredSelection selection) {
			text = "";
			Object selected = selection.getFirstElement();
			if (selected instanceof INodeToNodeRelationsModel) {
				enabled = true;
				text = ProjectExplorerViewMessages.SHOW_IN_N2N_VIEW_ITEM;
				viewToFire = NODE2NODE_VIEW_ID;
			} else if (selected instanceof IDriveModel) {
				enabled = true;
				text = ProjectExplorerViewMessages.SHOW_IN_DRIVE_INUQIER_ITEM;
				viewToFire = DRIVE_INUQIER_VIEW_ID;
			}
			if (enabled) {
				model = (IModel) selection.getFirstElement();
			}
		}

		@Override
		public boolean isEnabled() {
			return enabled;
		}

		@Override
		public void run() {
			eventManager.fireEvent(new AnalyseEvent(model, viewToFire));
		}
	}

	/**
	 * action describes analyse items
	 */
	private class ShowPropertiesAction extends Action {
		private IPropertyStatisticalModel model = null;
		private boolean enabled;
		private String text;

		@Override
		public String getText() {
			return text;
		}

		/**
		 * @param selection
		 * @param propertyTableItem
		 */
		public ShowPropertiesAction(IStructuredSelection selection,
				String propertyTableItem) {
			text = propertyTableItem;
			if (selection.getFirstElement() instanceof IPropertyStatisticalModel) {
				enabled = true;
			}
			if (enabled) {
				model = (IPropertyStatisticalModel) selection.getFirstElement();
			}
		}

		@Override
		public boolean isEnabled() {
			return enabled;
		}

		@Override
		public void run() {
			eventManager.fireEvent(new AnalyseEvent(model,
					PROPERTY_TABLE_VIEW_ID));
		}
	}

	/**
	 * action describes show properties view
	 */
	private class DistributionAnalyse extends Action {
		private IPropertyStatisticalModel model = null;
		private boolean enabled;
		private String text;

		@Override
		public String getText() {
			return text;
		}

		/**
		 * @param selection
		 * @param propertyTableItem
		 */
		public DistributionAnalyse(IStructuredSelection selection,
				String propertyTableItem) {
			text = propertyTableItem;
			if (selection.getFirstElement() instanceof IPropertyStatisticalModel) {
				enabled = true;
			}
			if (enabled) {
				model = (IPropertyStatisticalModel) selection.getFirstElement();
			}
		}

		@Override
		public boolean isEnabled() {
			return enabled;
		}

		@Override
		public void run() {
			eventManager.fireEvent(new AnalyseEvent(model,
					DISTRIBUTION_ANALYSE_VIEW_ID));
		}
	}

	/**
	 * Action describes show model on map
	 * 
	 */
	private class ShowOnMapAction extends Action {
		private IRenderableModel model;
		private boolean enabled = false;
		private String text;

		/**
		 * 
		 * @param selection
		 * @param propertyTableItem
		 */
		public ShowOnMapAction(IStructuredSelection selection,
				String propertyTableItem) {
			text = propertyTableItem;
			if (selection.getFirstElement() instanceof IRenderableModel) {
				enabled = true;
				model = (IRenderableModel) selection.getFirstElement();
			}
		}

		@Override
		public String getText() {
			return text;
		}

		@Override
		public boolean isEnabled() {
			return enabled;
		}

		@Override
		public void run() {
			eventManager.fireEvent(new ShowOnMapEvent(model, ZOOM));
		}
	}

	private class StarToolAnalyseAction extends Action {
		private INetworkModel model;
		private boolean enabled = false;
		private String text;
		private boolean checked = false;

		public StarToolAnalyseAction(IStructuredSelection selection,
				String propertyTableItem) {
			super(propertyTableItem, AS_CHECK_BOX);
			text = propertyTableItem;
			if (selection.getFirstElement() instanceof INetworkModel) {
				enabled = true;
				model = (INetworkModel) selection.getFirstElement();
				checkSelection();
			}
		}

		@Override
		public String getText() {
			return text;
		}

		@Override
		public boolean isEnabled() {
			return enabled;
		}

		@Override
		public boolean isChecked() {
			return checked;
		}

		@Override
		public void run() {
			try {
				if (checked) {
					model.removeStarToolSelectedModel();
				} else {
					model.setStarToolSelectedModel();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void checkSelection() {
			try {
				checked = model.getStarToolSelectedModel() != null ? Boolean.TRUE
						: Boolean.FALSE;
			} catch (AWEException e) {
				checked = Boolean.FALSE;
			}
		}
	}

	/**
	 * @param parent
	 */
	private void setLayout(Composite parent) {
		FormLayout layout = new FormLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginWidth = 0;
		layout.spacing = 0;
		parent.setLayout(layout);
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(100, -5);
		viewer.getTree().setLayoutData(formData);
	}

	/**
	 * Set Label and Content providers for TreeView
	 * 
	 * @param neoServiceProvider
	 */

	protected void setProviders() {
		viewer.setContentProvider(new ProjectTreeContentProvider());
		viewer.setLabelProvider(new CommonViewLabelProvider(viewer));
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * Select node
	 * 
	 * @param dataElement
	 *            - dataElement to select
	 */
	public void selectDataElement(IModel dataElement) {
		viewer.refresh();
		viewer.reveal(dataElement);
		viewer.setSelection(new StructuredSelection(
				new Object[] { dataElement }));
	}

}
