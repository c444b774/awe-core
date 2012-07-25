/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This
 * library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package org.amanzi.awe.views.explorer.view;

import org.amanzi.awe.ui.label.CommonViewLabelProvider;
import org.amanzi.awe.views.explorer.providers.ProjectTreeContentProvider;
import org.amanzi.neo.services.model.IModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.apache.commons.lang3.StringUtils;
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
     * The constructor.
     */
    public ProjectExplorerView() {

    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(final Composite parent) {

        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

        setProviders();
        viewer.setInput(getSite());
        viewer.setComparer(new IElementComparer() {

            @Override
            public int hashCode(final Object element) {
                return 0;
            }

            @Override
            public boolean equals(final Object a, final Object b) {
                if ((a instanceof IModel) && (b instanceof IModel)) {
                    IModel aM = (IModel)a;
                    IModel bM = (IModel)b;
                    return aM.getName().equals(bM.getName()) && aM.getClass().equals(bM.getClass());
                }
                return a == null ? b == null : a.equals(b);
            }
        });

        viewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(final DoubleClickEvent event) {
                openModelInViewBySelection(((IStructuredSelection)event.getViewer().getSelection()));
            }
        });

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                openModelInViewBySelection((IStructuredSelection)event.getSelection());
            }
        });

        getSite().setSelectionProvider(viewer);
        setLayout(parent);
    }

    /**
     * Open selected INetworkModel in NetworkTreeView
     * 
     * @param selection IStructuredSelection
     */
    private void openModelInViewBySelection(final IStructuredSelection selection) {
        Object selectionObject = selection.getFirstElement();
        String source = StringUtils.EMPTY;
        if (selectionObject instanceof INetworkModel) {
            source = NETWORK_TREE_VIEW_ID;
        }
        if (!source.isEmpty()) {
            try {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(NETWORK_TREE_VIEW_ID);
            } catch (PartInitException e) {
            }
        }
    }

    /**
     * @param parent
     */
    private void setLayout(final Composite parent) {
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
     * @param dataElement - dataElement to select
     */
    public void selectDataElement(final IModel dataElement) {
        viewer.refresh();
        viewer.reveal(dataElement);
        viewer.setSelection(new StructuredSelection(new Object[] {dataElement}));
    }

}
