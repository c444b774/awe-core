/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This
 * library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package org.amanzi.awe.views.explorer.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.amanzi.awe.views.explorer.ProjectExplorerPlugin;
import org.amanzi.awe.views.explorer.providers.ProjectTreeContentProvider;
import org.amanzi.awe.views.explorer.providers.ProjectTreeLabelProvider;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
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
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.neo4j.graphdb.Transaction;

/**
 * @author Vladislav_Kondratenko
 * @since 0.3
 */
public class ProjectExplorerView extends ViewPart {

    private static final String RENAME_MSG = "Enter new Name";

    /*
     * ID of this View
     */
    public static final String PROJECT_EXPLORER_ID = "org.amanzi.awe.views.explorer.view.ProjectExplorer";

    public static final String SHOW_PROPERTIES = "Show properties";
    public static final String CHANGE_MODE_TO_JUST_SHOW_PROPERTIES = "Change mode to just show";
    public static final String CHANGE_MODE_TO_EDIT_PROPERTIES = "Change mode to edit";

    /*
     * TreeViewer for database Nodes
     */
    protected TreeViewer viewer;

    /*
     * PropertySheetPage for Properties of Nodes
     */

    /*
     * NeoService provider
     */
    private NeoServiceProviderUi neoServiceProvider;

    /*
     * Variable show is view ready to edit property
     */
    private boolean isEditablePropertyView;

    /**
     * The constructor.
     */
    public ProjectExplorerView() {
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {

        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

        neoServiceProvider = NeoServiceProviderUi.getProvider();
        Transaction tx = neoServiceProvider.getService().beginTx();
        try {
            setProviders(neoServiceProvider);
            viewer.setInput(getSite());
            hookContextMenu();
            getSite().setSelectionProvider(viewer);
        } finally {
            tx.finish();
        }
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
                ProjectExplorerView.this.fillContextMenu(manager);
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

        ChangeModeAction editAction = new ChangeModeAction((IStructuredSelection)viewer.getSelection());
        manager.add(editAction);

        RenameAction renameAction = new RenameAction((IStructuredSelection)viewer.getSelection());
        manager.add(renameAction);

        DeleteAction deleteAction = new DeleteAction((IStructuredSelection)viewer.getSelection());
        manager.add(deleteAction);

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
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IPageLayout.ID_PROP_SHEET);
            } catch (PartInitException e) {
                ProjectExplorerPlugin.error(null, e);
            }
        }
    }

    private class ChangeModeAction extends Action {
        private boolean enabled;
        private final String text;
        private Set<IDataElement> selectedDataElements = new HashSet<IDataElement>();

        /**
         * Constructor
         * 
         * @param selection - selection
         */
        @SuppressWarnings("rawtypes")
        public ChangeModeAction(IStructuredSelection selection) {
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
            text = (isEditablePropertyView == false) ? CHANGE_MODE_TO_EDIT_PROPERTIES : CHANGE_MODE_TO_JUST_SHOW_PROPERTIES;
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
            isEditablePropertyView = (isEditablePropertyView == true) ? false : true;
            // ((NewNetworkPropertySheetPage)propertySheetPage).setEditableToPropertyView(isEditablePropertyView);
            // IDataElement lastClickedElement =
            // ((NewNetworkPropertySheetPage)propertySheetPage).getLastClickedElement();
            // selectDataElement(lastClickedElement);
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
                    nodeTypes.add(NeoUtils.getNodeType(((DataElement)element).getNode()));
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

    protected void setProviders(NeoServiceProviderUi neoServiceProvider) {
        viewer.setContentProvider(new ProjectTreeContentProvider(neoServiceProvider));
        viewer.setLabelProvider(new ProjectTreeLabelProvider(viewer));
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

    /**
     * This is how the framework determines which interfaces we implement.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Class key) {
        return super.getAdapter(key);
    }
}
