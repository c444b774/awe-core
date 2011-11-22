/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.amanzi.awe.views.network.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.amanzi.awe.awe.views.view.provider.NewNetworkTreeContentProvider;
import org.amanzi.awe.awe.views.view.provider.NewNetworkTreeLabelProvider;
import org.amanzi.neo.model.distribution.IDistributionBar;
import org.amanzi.neo.model.distribution.IDistributionModel;
import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.listeners.EventManager;
import org.amanzi.neo.services.listeners.EventUIType;
import org.amanzi.neo.services.listeners.IEventListener;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.ISelectionModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * This View contains a tree of objects found in the database. The tree is built based on the
 * existence of the NetworkRelationshipTypes.CHILD relation, and the set of INetworkModel nodes
 * defined by the INetworkModel.java class.
 * 
 * @author Kasnitskij_V
 * @since 1.0.0
 */

public class NewNetworkTreeView extends ViewPart implements IEventListener {

    private static final String RENAME_MSG = "Enter new Name";

    /*
     * ID of this View
     */
    public static final String NETWORK_TREE_VIEW_ID = "org.amanzi.awe.views.network.views.NetworkTreeView";

    public static final String TRANSMISSION_VIEW_ID = "org.amanzi.awe.views.neighbours.views.TransmissionView";
    public static final String NEIGHBOUR_VIEW_ID = "org.amanzi.awe.views.neighbours.views.NeighboursView";
    public static final String N2N_VIEW_ID = "org.amanzi.awe.views.neighbours.views.Node2NodeViews";
    public static final String DB_GRAPH_VIEW_ID = "org.neo4j.neoclipse.view.NeoGraphViewPart";

    public static final String SHOW_PROPERTIES = "Show properties";
    public static final String CHANGE_MODE_TO_JUST_SHOW_PROPERTIES = "Change mode to just show";
    public static final String CHANGE_MODE_TO_EDIT_PROPERTIES = "Change mode to edit";

    /*
     * TreeViewer for database Nodes
     */
    protected TreeViewer viewer;

    private Text tSearch;

    /**
     * The constructor.
     */
    public NewNetworkTreeView() {
        EventManager.getInstance().addListener(this, EventUIType.PROJECT_CHANGED, EventUIType.DISTRIBUTIONS_CHANGED,
                EventUIType.DISTRIBUTION_BAR_SELECTED);
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {

        tSearch = new Text(parent, SWT.BORDER);
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setComparer(new IElementComparer() {

            @Override
            public int hashCode(Object element) {
                return 0;
            }

            @Override
            public boolean equals(Object a, Object b) {
                if (a instanceof IDistributionalModel && b instanceof IDistributionalModel) {
                    return ((IDistributionalModel)a).getName().equals(((IDistributionalModel)b).getName());
                } else if (a instanceof IDistributionModel && b instanceof IDistributionModel) {
                    IDistributionModel aa = (IDistributionModel)a;
                    IDistributionModel bb = (IDistributionModel)b;
                    return aa.getName().equals(bb.getName())
                            && aa.getAnalyzedModel().getName().equals(bb.getAnalyzedModel().getName());
                } else if (a instanceof IDistributionBar && b instanceof IDistributionBar) {
                    IDistributionBar aa = (IDistributionBar)a;
                    IDistributionBar bb = (IDistributionBar)b;
                    return aa.getName().equals(bb.getName())
                            && aa.getDistribution().getName().equals(bb.getDistribution().getName())
                            && aa.getDistribution().getAnalyzedModel().getName()
                                    .equals(bb.getDistribution().getAnalyzedModel().getName());

                } else {
                    return a == null ? b == null : a.equals(b);
                }
            }
        });

		setProviders();
		viewer.setInput(getSite());
		hookContextMenu();
		getSite().setSelectionProvider(viewer);

        setLayout(parent);
    }

    /**
     * Creates a popup menu
     */
    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                NewNetworkTreeView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void fillContextMenu(IMenuManager manager) {
        SelectAction select = new SelectAction((IStructuredSelection)viewer.getSelection());
        if (select.isEnabled()) {
            manager.add(select);
        }

        RenameAction renameAction = new RenameAction((IStructuredSelection)viewer.getSelection());
        manager.add(renameAction);

        DeleteAction deleteAction = new DeleteAction((IStructuredSelection)viewer.getSelection());
        manager.add(deleteAction);

        createSubmenuAddToSelectionList((IStructuredSelection)viewer.getSelection(), manager);

        createSubmenuDeleteFromSelectionList((IStructuredSelection)viewer.getSelection(), manager);
    }

    private class SelectAction extends Action {
        private boolean enabled;
        private final String text;
        private Set<IDataElement> selectedDataElements = new HashSet<IDataElement>();

        /**
         * Constructor
         * 
         * @param selection - selection
         */
        @SuppressWarnings("rawtypes")
        public SelectAction(IStructuredSelection selection) {
            Iterator it = selection.iterator();
            while (it.hasNext()) {
                Object elementObject = it.next();
                if (elementObject instanceof INetworkModel) {
                    continue;
                } else {
                    IDataElement element = (IDataElement)elementObject;
                    selectedDataElements.add(element);
                }
            }
            enabled = selectedDataElements.size() > 0;
            text = SHOW_PROPERTIES;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void run() {
            try {
            	PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IPageLayout.ID_PROP_SHEET);
            } catch (PartInitException e) {
            	//TODO: LN: handle exception!!!!!!!!!
            }
        }
    }
    
    private class RenameAction extends Action {

        private boolean enabled;
        private final String text;
        private IDataElement dataElement;

        /**
         * Constructor
         * 
         * @param selection - selection
         */
        public RenameAction(IStructuredSelection selection) {
            text = "Rename";
            enabled = selection.size() == 1 && selection.getFirstElement() instanceof IDataElement
                    && !(selection.getFirstElement() instanceof INetworkModel);
            if (enabled) {
                dataElement = (IDataElement)selection.getFirstElement();
                enabled = (dataElement.get(INeoConstants.PROPERTY_NAME_NAME) == null) ? false : true;
            }
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void run() {
            String value = getNewName(dataElement.get(INeoConstants.PROPERTY_NAME_NAME).toString());
            INetworkModel networkModel = (INetworkModel)dataElement.get(INeoConstants.NETWORK_MODEL_NAME);
            try {
                networkModel.renameElement(dataElement, value);
            } catch (AWEException e) {
                // TODO Handle AWEException
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
            viewer.refresh();
        }

        /**
         * Opens a dialog asking the user for a new name.
         * 
         * @return The new name of the element.
         */
        private String getNewName(String oldName) {
            InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(), RENAME_MSG, "", oldName, null); //$NON-NLS-1$
            int result = dialog.open();
            if (result == Dialog.CANCEL)
                return oldName;
            return dialog.getValue();
        }
    }

    /**
     * Action to delete all selected nodes and their child nodes in the graph, but not nodes related
     * by other geographic relationships. The result is designed to remove sub-tree's from the tree
     * view, leaving remaining tree nodes in place.
     * 
     * @author Kasnitskij_V
     * @since 1.0.0
     */
    private class DeleteAction extends Action {
        private final List<IDataElement> dataElementsToDelete;
        private String text = null;
        private boolean interactive = false;

        private DeleteAction(List<IDataElement> nodesToDelete, String text) {
            this.dataElementsToDelete = nodesToDelete;
            this.text = text;
        }

        @SuppressWarnings("rawtypes")
        private DeleteAction(IStructuredSelection selection) {
            interactive = true;
            dataElementsToDelete = new ArrayList<IDataElement>();
            Iterator iterator = selection.iterator();
            HashSet<String> nodeTypes = new HashSet<String>();
            while (iterator.hasNext()) {
                Object element = iterator.next();
                if (element != null && element instanceof IDataElement && !(element instanceof INetworkModel)) {
                    dataElementsToDelete.add((IDataElement)element);
                    
                    //TODO: LN: do not use raw Nodes
//                    nodeTypes.add(NeoUtils.getNodeType(((DataElement)element).getNode()));
                }
            }
            String type = nodeTypes.size() == 1 ? nodeTypes.iterator().next() : "node";
            switch (dataElementsToDelete.size()) {
            case 0:
                text = "Select data elements to delete";
                break;
            case 1:
                text = "Delete " + type + " '" + dataElementsToDelete.get(0).toString() + "'";
                break;
            case 2:
            case 3:
            case 4:
                for (IDataElement dataElement : dataElementsToDelete) {
                    if (text == null) {
                        text = "Delete " + type + "s " + dataElement;
                    } else {
                        text += ", " + dataElement;
                    }
                }
                break;
            default:
                text = "Delete " + dataElementsToDelete.size() + " " + type + "s";
                break;
            }
            // TODO: Find a more general solution
            text = text.replaceAll("citys", "cities");
        }

        @Override
        public void run() {

            if (interactive) {
                MessageBox msg = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.YES | SWT.NO);
                msg.setText("Delete data element");
                msg.setMessage(getText() + "?\n\nAll contained data will also be deleted!");
                int result = msg.open();
                if (result != SWT.YES) {
                    return;
                }
            }

            // Kasnitskij_V:
            // It's need when user want to delete nodes using bad-way.
            // For example, if we have a structure city->site->sector with values
            // Dortmund->{AMZ000210, AMZ000234->{A0234, A0236, A0289}}
            // and user choose to delete nodes Dortmund, AMZ000234, A0236.
            // We should delete in start A0236, then AMZ000234 and
            // all it remained nodes, and in the end - Dortmund and all it remained nodes
            int countOfNodesToDelete = dataElementsToDelete.size();
            IDataElement[] dataElementsToDeleteArray = new IDataElement[countOfNodesToDelete];
            dataElementsToDelete.toArray(dataElementsToDeleteArray);

            for (int i = countOfNodesToDelete - 1; i >= 0; i--) {
                IDataElement dataElement = dataElementsToDeleteArray[i];
                INetworkModel networkModel = (INetworkModel)dataElement.get(INeoConstants.NETWORK_MODEL_NAME);
                try {
                    networkModel.deleteElement(dataElement);
                } catch (AWEException e) {
                    // TODO Handle AWEException
                    throw (RuntimeException)new RuntimeException().initCause(e);
                }
            }

            viewer.refresh();
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public boolean isEnabled() {
            return dataElementsToDelete.size() > 0;
        }
    }

    /**
     * Method create submenu - Add to selection list
     * 
     * @param selection
     * @param manager
     */
    @SuppressWarnings("rawtypes")
    private void createSubmenuAddToSelectionList(IStructuredSelection selection, IMenuManager manager) {

        boolean isNetwork = false;
        boolean isSector = true;
        boolean firstNode = true;
        boolean isOneNetwork = true;
        String text = "Add to selection list";
        String nameNetwork = "";
        INetworkModel network = null;

        // Sub menu
        Set<IDataElement> selectedNodes = new HashSet<IDataElement>();
        MenuManager subMenu = new MenuManager(text);

        Iterator it = selection.iterator();
        while (it.hasNext()) {
            Object elementObject = it.next();
            if (elementObject instanceof INetworkModel) {
                isNetwork = true;
                continue;
            } else {
                IDataElement element = (IDataElement)elementObject;
                selectedNodes.add(element);
                //TODO: LN: do not use raw Nodes
//                if (!NeoUtils.getNodeType(((DataElement)element).getNode()).equals(NodeTypes.SECTOR.getId())) {
//                    isSector = false;
//                }
                network = (INetworkModel)((DataElement)element).get(INeoConstants.NETWORK_MODEL_NAME);
                if (firstNode) {
                    nameNetwork = network.getName();
                    firstNode = false;
                } else {
                    if (!network.getName().equals(nameNetwork)) {
                        isOneNetwork = false;
                    }
                }

            }
        }
        if (isSector && isOneNetwork && !isNetwork) {
            try {
                INetworkModel networkModel = ProjectModel.getCurrentProjectModel().findNetwork(nameNetwork);
                Iterable<ISelectionModel> selectionModel = networkModel.getAllSelectionModels();
                Iterator<ISelectionModel> iterator = selectionModel.iterator();
                while (iterator.hasNext()) {
                    String nameSelectionList = iterator.next().getName();
                    AddToSelectionListAction addToSelectionListAction = new AddToSelectionListAction(
                            (IStructuredSelection)viewer.getSelection(), nameSelectionList, selectedNodes, networkModel);
                    subMenu.add(addToSelectionListAction);
                }
                manager.add(subMenu);
            } catch (AWEException e) {
                // TODO Handle AWEException
                throw (RuntimeException)new RuntimeException().initCause(e);
            }

        }
    }

    /**
     * <<<<<<< HEAD ======= Method create submenu - Delete from selection list
     * 
     * @param selection
     * @param manager
     */
    @SuppressWarnings("rawtypes")
    private void createSubmenuDeleteFromSelectionList(IStructuredSelection selection, IMenuManager manager) {

        boolean isNetwork = true;
        boolean isSector = true;
        String text = "Delete from selection list";
        INetworkModel network;

        // Sub menu
        IDataElement element = null;
        MenuManager subMenu = new MenuManager(text);

        if (selection.size() == 1) {
            Iterator it = selection.iterator();
            Object elementObject = it.next();
            if (!(elementObject instanceof INetworkModel)) {
                element = (IDataElement)elementObject;
//                if (!NeoUtils.getNodeType(((DataElement)element).getNode()).equals(NodeTypes.SECTOR.getId())) {
//                    isSector = false;
//                }
                isNetwork = false;
            }
            if (!isNetwork && isSector) {
                network = (INetworkModel)((DataElement)element).get(INeoConstants.NETWORK_MODEL_NAME);
                Iterable<ISelectionModel> modelsOfSector;
                try {
                    modelsOfSector = network.getAllSelectionModelsOfSector(element);
                    Iterator<ISelectionModel> iterator = modelsOfSector.iterator();
                    while (iterator.hasNext()) {
                        ISelectionModel model = iterator.next();
                        DeleteFromSelectionListAction deleteFromSelectionListAction = new DeleteFromSelectionListAction(model,
                                element);
                        subMenu.add(deleteFromSelectionListAction);
                    }
                } catch (AWEException e) {
                    // TODO Handle AWEException
                    throw (RuntimeException)new RuntimeException().initCause(e);
                }
                manager.add(subMenu);
            }
        }
    }

    /**
     * >>>>>>> 872543a1a468ec5d3545ee31e062298907bce7dd Action for adding of sectors to selection
     * list
     * 
     * @author Ladornaya_A
     * @since 1.0.0
     */
    private class AddToSelectionListAction extends Action {
        private boolean enabled = true;
        private final String text;
        private Set<IDataElement> selectedNodes = new HashSet<IDataElement>();
        private ISelectionModel selectionModel;
        private List<String> nameExistingSector = new ArrayList<String>();

        /**
         * Constructor
         * 
         * @param selection - selection
         * @throws AWEException
         */
        public AddToSelectionListAction(IStructuredSelection selection, String nameSelectionList, Set<IDataElement> selectedNodes,
                INetworkModel networkModel) throws AWEException {
            text = nameSelectionList;
            this.selectedNodes = selectedNodes;
            this.selectionModel = networkModel.findSelectionModel(text);
            for (IDataElement element : selectedNodes) {
                if (selectionModel.isExistSelectionLink(element)) {
                    nameExistingSector.add(element.toString());
                    enabled = false;
                }
            }
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void run() {
            try {
                if (enabled == false) {
                    Collections.sort(nameExistingSector);
                    String msg = "";
                    String sectors = "Sector";
                    if (nameExistingSector.size() > 1) {
                        sectors = sectors + "s";
                    }
                    sectors = sectors + " ";
                    int i;
                    for (i = 0; i < nameExistingSector.size() - 1; i++) {
                        sectors = sectors + "" + nameExistingSector.get(i) + ", ";
                    }
                    sectors = sectors + "" + nameExistingSector.get(i) + " already exist in list - ";
                    msg = sectors + " " + text + "!";
                    MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                            "Already exist!", msg);
                } else {
                    Iterator<IDataElement> it = selectedNodes.iterator();
                    while (it.hasNext()) {
                        IDataElement element = it.next();
                        selectionModel.linkToSector(element);
                    }
                }
            } catch (AWEException e) {
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }
    }

    /**
     * Action for delete sector from selection list
     * 
     * @author Ladornaya_A
     * @since 1.0.0
     */
    private class DeleteFromSelectionListAction extends Action {
        private final String text;
        private IDataElement sector;
        private ISelectionModel model;

        /**
         * Constructor
         * 
         * @param selection - selection
         * @throws AWEException
         */
        public DeleteFromSelectionListAction(ISelectionModel model, IDataElement sector) throws AWEException {
            text = model.getName();
            this.sector = sector;
            this.model = model;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void run() {
            model.deleteSelectionLink(sector);
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
        tSearch.setLayoutData(formData);

        formData = new FormData();
        formData.top = new FormAttachment(tSearch, 5);
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
        viewer.setContentProvider(new NewNetworkTreeContentProvider());
        viewer.setLabelProvider(new NewNetworkTreeLabelProvider());
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
     * @param dataElement - dataElement to select
     */
    public void selectDataElement(IDataElement dataElement) {
        viewer.refresh();
        viewer.reveal(dataElement);
        viewer.setSelection(new StructuredSelection(new Object[] {dataElement}));
    }

	@Override
	public void handleEvent(EventUIType event, Object data) {
		// TODO Auto-generated method stub
		
	}

}
