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

package org.amanzi.awe.views.reuse.mess_table.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.amanzi.awe.views.reuse.Messages;
import org.amanzi.awe.views.reuse.mess_table.DataTypes;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.events.ShowPreparedViewEvent;
import org.amanzi.neo.services.events.ShowViewEvent;
import org.amanzi.neo.services.events.UpdateDrillDownEvent;
import org.amanzi.neo.services.events.UpdateViewEvent;
import org.amanzi.neo.services.events.UpdateViewEventType;
import org.amanzi.neo.services.network.FrequencyPlanModel;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.amanzi.neo.services.node2node.NodeToNodeTypes;
import org.amanzi.neo.services.statistic.IPropertyHeader;
import org.amanzi.neo.services.statistic.IStatistic;
import org.amanzi.neo.services.statistic.PropertyHeader;
import org.amanzi.neo.services.statistic.StatisticManager;
import org.amanzi.neo.services.ui.IUpdateViewListener;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoServicesUiPlugin;
import org.amanzi.neo.services.ui.NeoUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.TraversalDescription;

/**
 * View for Message and Event tabular
 * 
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class MessageAndEventTableView extends ViewPart {
    
    public static final String VIEW_ID = "org.amanzi.awe.views.reuse.mess_table.view.MessageAndEventTableView";
    public static final String NETWORK_TREE_VIEW_ID = "org.amanzi.awe.views.network.views.NetworkTreeView";
    public static final String DRIVE_TREE_VIEW_ID = "org.amanzi.awe.views.tree.drive.views.DriveTreeView";
    
    private static final String EXPRESSION_NOT_EMPTY = "not empty";
    private static final String EXPRESSION_EMPTY = "empty";
    private static final String[] DEFAULT_EXPRESSIONS = new String[]{EXPRESSION_EMPTY,EXPRESSION_NOT_EMPTY};
    
    // memento keys
    private static final String MEM_DATASET = "MEM_DATASET";
    private static final String MEM_PROPERTY = "MEM_PROPERTY";
    private static final String MEM_EXPRESSION = "MEM_EXPRESSION";
    private static final String MEM_DATASET_MAP_COUNT = "MEM_DATASET_COUNT";
    private static final String MEM_DATASET_MAP = "MEM_DATASET_MAP_";
    private static final String MEM_PROPERTY_COUNT = "MEM_PROPERTY_MAP_COUNT_";
    private static final String MEM_PROPERTY_MAP = "MEM_PROPERTY_MAP_";
    
    private static final int MIN_FIELD_WIDTH = 50;
    
    private Combo cDataset;    
    private Combo cProperty;
    private Combo cExpression;
    private TableViewer table;
    
    private TableLabelProvider labelProvider;
    private TableContentProvider contentProvider;
    
    private Action actCommit;
    private Action actRollback;
    private Action actConfigure;
    private Action actClearFilter;
    
    private IPropertySheetPage propertySheetPage;
    
    private HashMap<String, DatasetInfo> datasets;
    private Point point;
    private boolean flag = true;
    
    private String initDataset;
    private String initProperty;
    private String initExpression;
    private HashMap<String,List<String>> initDatasets;
    private DatasetService ds;

    @Override
    public void createPartControl(Composite parent) {
        datasets = initDatasetsInfo();
        initMenuBar();
        
        Composite frame = new Composite(parent, SWT.FILL);
        FormLayout formLayout = new FormLayout();
        formLayout.marginHeight = 0;
        formLayout.marginWidth = 0;
        formLayout.spacing = 0;
        frame.setLayout(formLayout);
        
        Composite child = new Composite(frame, SWT.FILL);
        FormData fData = new FormData();
        fData.top = new FormAttachment(0, 2);
        fData.left = new FormAttachment(0, 2);
        fData.right = new FormAttachment(100, -2);

        child.setLayoutData(fData);
        GridLayout layout = new GridLayout(12, false);
        child.setLayout(layout);

        Label label = new Label(child, SWT.FLAT);
        label.setText(Messages.MessageAndEventTable_label_DATASET);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        cDataset = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);

        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;
        cDataset.setLayoutData(layoutData);
        cDataset.setItems(getDatasets());
        
        label = new Label(child, SWT.FLAT);
        label.setText(Messages.MessageAndEventTable_label_PROPERTY);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        cProperty = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);

        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;
        cProperty.setLayoutData(layoutData);

        label = new Label(child, SWT.FLAT);
        label.setText(Messages.MessageAndEventTable_label_EXPRESSION);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        cExpression = new Combo(child, SWT.DROP_DOWN);

        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;
        cExpression.setLayoutData(layoutData);
        
        table = new TableViewer(frame, SWT.VIRTUAL | SWT.BORDER | SWT.FULL_SELECTION );
        fData = new FormData();
        fData.left = new FormAttachment(0, 10);
        fData.right = new FormAttachment(100, -10);
        fData.top = new FormAttachment(child, 2);
        fData.bottom = new FormAttachment(100, -10);
        table.getControl().setLayoutData(fData);
        table.getControl().setVisible(false);
        getSite().setSelectionProvider(table);
        
        initTableContent();
        
        addListeners();
        NeoServicesUiPlugin.getDefault().getUpdateViewManager().addListener(new SelectedNodeListener());

        initializeStartupProperties();
        hookContextMenu();
    }
    private class SelectedNodeListener implements IUpdateViewListener {
        private Collection<UpdateViewEventType> handedTypes;
        
        public SelectedNodeListener() {
            Collection<UpdateViewEventType> spr = new HashSet<UpdateViewEventType>();
            spr.add(UpdateViewEventType.SHOW_PREPARED_VIEW);
            
            handedTypes = Collections.unmodifiableCollection(spr);
        }
        
        @Override
        public void updateView(UpdateViewEvent event) {
        	if (!flag){
        		flag = true;
        		return;
        	}
        	ShowPreparedViewEvent showEvent = (ShowPreparedViewEvent)event;
        	if(cDataset.getSelectionIndex()<0){
        	    if (showEvent.getNodes().size()>0){
        	        Node child=showEvent.getNodes().iterator().next();
        	        Node root=ds.findRootByChild(child);
        	        if (root==null){
        	            return;
        	        }
        	        Entry<String, DatasetInfo> dataset = findFirst(root);
        	        if (dataset==null){
        	            return;
        	        }
        	        cDataset.setText(dataset.getKey());
                    updateProperty();
                    contentProvider.storeRows();
        	    }else{
        	        return;
        	    }
            }           
            cProperty.deselectAll();
            cExpression.deselectAll();    
            contentProvider.restoreRows();
        	contentProvider.uploadData(showEvent.getNodes());
        	
        	
            
        }

        @Override
        public Collection<UpdateViewEventType> getType() {
            return handedTypes;
        }
        
    }

    private void initTableContent() {
        labelProvider = new TableLabelProvider();
        labelProvider.createTableColumns();
        contentProvider = new TableContentProvider();
        table.setContentProvider(contentProvider);
    }

    /**
     * Initialize menu.
     */
    private void initMenuBar() {
        //TODO Icons
        createActions();
        IToolBarManager tm = getViewSite().getActionBars().getToolBarManager();
        IMenuManager mm = getViewSite().getActionBars().getMenuManager();        
        //mm.add(actCommit);
        //tm.add(actCommit);        
        //mm.add(actRollback);
        //tm.add(actRollback);        
        mm.add(actConfigure);
        tm.add(actConfigure);
        mm.add(actClearFilter);
        tm.add(actClearFilter);
    }
    
    /**
     * Create actions for menu bar.
     */
    private void createActions(){
        actCommit = new Action(Messages.MessageAndEventTable_menu_COMMIT){
            @Override
            public void run(){
                storeChanges();
            }
        };
        actRollback = new Action(Messages.MessageAndEventTable_menu_ROLLBACK){
            @Override
            public void run(){
                rollbackChanges();
            }
        };
        actConfigure = new Action(Messages.MessageAndEventTable_menu_CONFIGURE){
            @Override
            public void run(){
                showConfigureTableView();
            }
        };
        actClearFilter = new Action(Messages.MessageAndEventTable_menu_CLEAR){
            @Override
            public void run(){
                clearFilter();
            }
        };
    }
    
    /**
     * Clear rows filter.
     */
    private void clearFilter() {
        updateProperty();
    }

    /**
     * Commit all changes
     */
    private void storeChanges(){
        //TODO when editing added.
    }
    
    /**
     * Revert all changes.
     */
    private void rollbackChanges(){
        //TODO when editing added.
    }
    
    /**
     * Open view for configure columns visibility.
     */
    private void showConfigureTableView(){
        if(cDataset.getSelectionIndex()<0){
            return;
        }
        String datasetName = cDataset.getText();
        DatasetInfo datasetInfo = datasets.get(datasetName);
        TableConfigWizard wizard = new TableConfigWizard(datasetName,datasetInfo.getVisibleProperties(),datasetInfo.getNotVisibleProperties());
        IWorkbenchWindow workbenchWindow = getViewSite().getWorkbenchWindow();
        wizard.init(workbenchWindow.getWorkbench(), null);
        Shell parent = workbenchWindow.getShell();
        WizardDialog dialog = new WizardDialog(parent, wizard);
        dialog.create();
        int result = dialog.open();
        if(result!=0){
            return;
        }
        String[] newVisible = wizard.getVisible();
        for(String property : datasetInfo.getAllProperties()){
            datasetInfo.setPropertyVisible(property, false);
        }
        if(newVisible !=null){
            for(String property : newVisible){
                datasetInfo.setPropertyVisible(property, true);
            }
            String selected = null;
            if(cProperty.getSelectionIndex()>=0){
                selected = cProperty.getText();
            }
            String[] filteredProperties = datasetInfo.getFilteredProperties();
            cProperty.setItems(filteredProperties);
            if(selected!=null){
                int length = filteredProperties.length;
                int i;
                for(i=0; i<length; i++){
                    if(selected.equals(cProperty.getItem(i))){
                        cProperty.select(i);
                        break;
                    }
                }
                if(i==length){
                    cExpression.setItems(DEFAULT_EXPRESSIONS);
                }
            }
        }
        updateTable();        
    }
    
    /**
     * Initialize startup properties.
     */
    private void initializeStartupProperties() {
        if (!setProperty(cDataset, initDataset)) {
            return;
        }
        cProperty.setItems(getProperties(initDataset));
        setProperty(cProperty, initProperty);
        cExpression.setItems(DEFAULT_EXPRESSIONS);
        if(!setProperty(cExpression, initExpression)){
            cExpression.setText(initExpression);
        }
        if(!initDatasets.isEmpty()){
            for(String currDataset : initDatasets.keySet()){
                DatasetInfo datasetInfo = datasets.get(currDataset);
                if(datasetInfo!=null){
                    for(String property : initDatasets.get(currDataset)){
                        datasetInfo.setPropertyVisible(property, false);
                    }
                }
            }
        }
        updateTable();
    }
    
    /**
     * Sets value into property
     * 
     * @param combo - Combo
     * @param value - value
     * @return if sets is correctly - return true else false
     */
    private boolean setProperty(Combo combo, String value) {
        if (combo == null || value == null) {
            return false;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItem(i).equals(value)) {
                combo.select(i);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Add listeners to components.
     */
    private void addListeners(){
        table.getTable().addListener (SWT.SetData, new Listener () {
            public void handleEvent (Event event) {
                TableItem item = (TableItem) event.item;
                table.getTable().deselectAll();
                final int index = table.getTable().indexOf (item);
                
                contentProvider.uploadData(null,index);
            }
        });

        table.getControl().addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent e) {
            }

            @Override
            public void mouseDown(MouseEvent e) {
                point = new Point(e.x, e.y);
                flag = false;
                fireDrillDown(); 
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                point = new Point(e.x, e.y);
                flag = false;
                fireDrillDown(); 
            }
        });
        
        cDataset.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateProperty();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        cProperty.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateExpression();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        cExpression.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateTable();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }
    
    /**
     * Creates a popup menu
     */
    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(table.getControl());
        table.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, table);
    }
    
    /**
     * Fills context menu
     * 
     * @param manager - menu manager
     */
    protected void fillContextMenu(IMenuManager manager) {
        manager.add(new Action("Properties") {
            @Override
            public void run() {
                showNodeProperties();
            }
        });
    }
    
    /**
     * Returns (and creates is it need) property sheet page for this View
     * 
     * @return PropertySheetPage
     */
    private IPropertySheetPage getPropertySheetPage() {
        if (propertySheetPage == null) {
            propertySheetPage = new EventPropertySheetPage();
        }

        return propertySheetPage;
    }

    /**
     * This is how the framework determines which interfaces we implement.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Class key) {
        if (key.equals(IPropertySheetPage.class)) {
            return getPropertySheetPage();
        } else {
            return super.getAdapter(key);
        }
    }
    
    /**
     * Fire drill down event after select node.
     */
    private void fireDrillDown(){
        if(point == null){
            return;
        }
        TableItem item = table.getTable().getItem(point);
        if(item == null){
            return;
        }
        TableRowWrapper row = (TableRowWrapper)item.getData();
        Node node = row.getNode();
        NeoCorePlugin.getDefault().getUpdateViewManager().fireUpdateView(
                new UpdateDrillDownEvent(node, VIEW_ID));
        String currentTreeView = isNetworkNode(node)?NETWORK_TREE_VIEW_ID:DRIVE_TREE_VIEW_ID;
        NeoCorePlugin.getDefault().getUpdateViewManager().fireUpdateView(
                new ShowViewEvent(currentTreeView));
    }
    
    /**
     * Is node from network tree.
     *
     * @param node
     * @return boolean
     */
    private boolean isNetworkNode(Node node){
        GraphDatabaseService service = NeoServiceProviderUi.getProvider().getService();
        NodeTypes type = NodeTypes.getNodeType(node, service);
        return type!=null&&(type.equals(NodeTypes.SECTOR)||type.equals(NodeTypes.TRX));
    }
    
    /**
     * Show properties view for selected node.
     */
    private void showNodeProperties(){
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IPageLayout.ID_PROP_SHEET);
        } catch (PartInitException e) {
            NeoCorePlugin.error(null, e);
        }
    }
    
    /**
     * Update properties after changed dataset
     */
    private void updateProperty(){
        String[] propNames =null;
        if(cDataset.getSelectionIndex()<0){
            table.getControl().setVisible(false);
        } else {
            String datasetName = cDataset.getText();
            propNames = getProperties(datasetName);
            if (propNames == null || propNames.length == 0) {
                table.getControl().setVisible(false);
            }
        }
        Arrays.sort(propNames);
        cProperty.setItems(propNames);
        updateExpression();
        contentProvider.storeRows();
    }
    
    /**
     * Update expressions after change property.
     */
    private void updateExpression(){
        if(cProperty.getSelectionIndex()<0){
            cExpression.clearSelection();
            cExpression.setItems(DEFAULT_EXPRESSIONS);
        }
        updateTable();
    }
    
    /**
     * Update table for new conditions.
     */
    private void updateTable(){
        if(cDataset.getSelectionIndex()<0){
            return;
        }
        String datasetName = cDataset.getText();
        String propertyName = null;
        if(cProperty.getSelectionIndex()>=0){
            propertyName = cProperty.getText();
        }
        String expressionMask = cExpression.getText();
        expressionMask = expressionMask.length()==0?null:expressionMask;        
        setEnableAll(false);
        table.setInput(new InputTableData(datasetName,propertyName,expressionMask));
        setEnableAll(true);
    }
    
    /**
     * Set enabled for all components
     *
     * @param isEnable
     */
    private void setEnableAll(boolean isEnable){        
        cDataset.setEnabled(isEnable);
        cProperty.setEnabled(isEnable);
        cExpression.setEnabled(isEnable);
        actClearFilter.setEnabled(isEnable);
        actCommit.setEnabled(isEnable);
        actRollback.setEnabled(isEnable);
        actConfigure.setEnabled(isEnable);
        table.getControl().setVisible(isEnable);
    }

    /**
     * Update datasets.
     */
    public void updateDatasetNodes(){
        datasets = initDatasetsInfo();
        cDataset.setItems(getDatasets());
        initTableContent();
    }
    
    @Override
    public void setFocus() {
    }
    
    /**
     * Initialize all datasets information.
     *
     * @return
     */
    private HashMap<String, DatasetInfo> initDatasetsInfo(){
        HashMap<String, DatasetInfo> result = new HashMap<String, DatasetInfo>();
        for (Node root:ds.getAllRootNodes().nodes()){
            result.putAll(formDatasetInfo(root));
        }
        return result;
    }
    
/**
 * Forms all dataset info based on root node
 *
 * @param root
 * @return
 */
    private Map< String,DatasetInfo> formDatasetInfo(Node root) {
        Map<String, DatasetInfo> result=new HashMap<String, DatasetInfo>();
        final String nodeName = ds.getNodeName(root);
        result.put(nodeName,new DatasetInfo(root));
        if (NodeTypes.NETWORK.checkNode(root)){
            Set<FrequencyPlanModel> models = new NetworkModel(root).findAllFrqModel();
            if (!models.isEmpty()){
                for (FrequencyPlanModel model:models){
                    String name=String.format("%s:plan %s", nodeName,model.getName());
                    result.put(name, new DatasetInfo(root,model));
                }
            }
        }
        return result;
    }

    /**
     * Returns datasets names.
     *
     * @return String[]
     */
    private String[] getDatasets(){
        return convertListToArray(new ArrayList<String>(datasets.keySet()));
    }
    
    /**
     * Returns properties names.
     *
     * @return String[]
     */
    private String[] getProperties(String aDataset){
        return datasets.get(aDataset).getFilteredProperties();
    }
    
    /**
     * Convert list of strings to sorted array
     *
     * @param list
     * @return String[]
     */
    private String[] convertListToArray(List<String> list){
        String[] result = new String[list.size()];
        result = list.toArray(result);
        Arrays.sort(result, new Comparator<String>() {

            @Override
            public int compare(String arg0, String arg1) {
                if (arg0.equals("sector") || arg0.equals("trx")) {
                    if (arg1.equals("sector") || arg1.equals("trx")) {
                        return arg0.compareTo(arg1);
                    }
                    else {
                        return -1;
                    }
                }
                return arg0.compareTo(arg1);
            }
        });
        return result;
    }
    
    /**
     * 
     * Dataset information.
     * <p>
     *
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private class DatasetInfo{
        
        private Node dataset;
        private DataTypes type;
        private LinkedHashMap<String, Boolean> allProperties;
        private List<String> filteredProperties;
        private FrequencyPlanModel model;
        private HashSet<String> trxProp;
        private NodeToNodeRelationModel modl;
        
        /**
         * Constructor.
         * @param aDataset
         * @param service
         */
        public DatasetInfo(Node aDataset) {
            dataset = aDataset;
            type = DataTypes.getTypeByNode(dataset);
            initProperties();
        }
        
        /**
         * @param root
         * @param model
         */
        public DatasetInfo(Node networkRoot, FrequencyPlanModel model) {
            dataset = networkRoot;
            this.model = model;
            type = DataTypes.NETWORK_PLAN;
            allProperties = new LinkedHashMap<String, Boolean>();
            allProperties.put("sector",true);
            allProperties.put("trx",true);
            filteredProperties = new ArrayList<String>();
            filteredProperties.add("sector");
            filteredProperties.add("trx");
            trxProp = new HashSet<String>();
            Set<NodeToNodeRelationModel> im = new NetworkModel(networkRoot).findAllN2nModels(NodeToNodeTypes.ILLEGAL_FREQUENCY);
            if (im.size() == 1) {
                modl = im.iterator().next();
            } else {
                modl = null;
            }

            String name = ds.getNodeName(networkRoot);
            IStatistic stat = StatisticManager.getStatistic(networkRoot);
            final Comparable<Class<?>> comparable = new Comparable<Class<?>>() {

                @Override
                public int compareTo(Class<?> o) {
                    return 0;
                }
            };
            Collection<String> result = stat.getPropertyNameCollection(name, NodeTypes.TRX.getId(), comparable);
            for(String property : result){
                allProperties.put(property, true);
                filteredProperties.add(property);
                trxProp.add(property);
            }
            if (modl != null) {
                allProperties.put("Illegal Frequencies", true);
                filteredProperties.add("Illegal Frequencies");
                trxProp.add("Illegal Frequencies");
            }
            result = stat.getPropertyNameCollection(model.getName(), NodeTypes.FREQUENCY_PLAN.getId(), comparable);
            for(String property : result){
                allProperties.put(property, true);
                filteredProperties.add(property);
            }
        }

        /**
         * Initilize properties.
         */
        private void initProperties(){
            allProperties = new LinkedHashMap<String, Boolean>();
            filteredProperties = new ArrayList<String>();
            
            Node propRoot = NeoUtils.findRoot(dataset);
            IPropertyHeader header = PropertyHeader.getPropertyStatistic(propRoot);
            
            // Kasnitskij_V:
            String[] propNames = header.getAllFields("-main-type-");
            if (propNames == null){
                return;
            }
            for(String property : propNames){
                allProperties.put(property, true);
                filteredProperties.add(property);
            }
        }
        
        /**
         * @return Returns the type.
         */
        public DataTypes getType() {
            return type;
        }
        
        /**
         * @return Returns the dataset.
         */
        public Node getDataset() {
            return dataset;
        }
        
        /**
         * @return Returns the properties.
         */
        public String[] getAllProperties() {
            return convertListToArray(new ArrayList<String>(allProperties.keySet()));
        }
        
        /**
         * Returns only visible properties.
         *
         * @return String[]
         */
        public String[] getVisibleProperties(){
            List<String> result = new ArrayList<String>(allProperties.keySet().size());
            for(String property : allProperties.keySet()){
                if(allProperties.get(property)){
                    result.add(property);
                }
            }
            return convertListToArray(result);
        }
        
        /**
         * Returns only visible properties that can be in filter (string properties).
         *
         * @return String[]
         */
        public String[] getFilteredProperties(){
            List<String> result = new ArrayList<String>(filteredProperties.size());
            for(String property : filteredProperties){
                if(allProperties.get(property)){
                    result.add(property);
                }
            }
            return convertListToArray(result);
        }
        
        /**
         * Returns only invisible properties.
         *
         * @return String[]
         */
        public String[] getNotVisibleProperties(){
            List<String> result = new ArrayList<String>(allProperties.keySet().size());
            for(String property : allProperties.keySet()){
                if(!allProperties.get(property)){
                    result.add(property);
                }
            }
            return convertListToArray(result);
        }
        
        /**
         * Sets property visibility.
         *
         * @param propName
         * @param isVisible
         */
        public void setPropertyVisible(String propName, boolean isVisible){
            allProperties.put(propName,isVisible);
        }
        
        @Override
        public String toString() {
            return allProperties.toString();
        }
        
    }
    
    /**
     * 
     * Label provider for table.
     * <p>
     *
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
        
        protected static final int DEF_SIZE = 0;
        protected static final int REAL_SIZE = 150;
        
        private List<TableColumn> columns;

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof TableRowWrapper) {
                TableRowWrapper row = (TableRowWrapper)element;
                return row.getValue(columnIndex);
            } else {
                return getText(element);
            }
        }
        
        /**
         * Create all table columns.
         */
        public void createTableColumns() {
            Table tabl = table.getTable();
            int colCount = getMaxColumnCount();
            columns = new ArrayList<TableColumn>(colCount);
            for(int i=0; i<colCount;i++){
                TableViewerColumn column = new TableViewerColumn(table, SWT.LEFT);
                TableColumn col = column.getColumn();
                col.setText("");
                columns.add(col);
                col.setWidth(DEF_SIZE);
                col.setResizable(true);
            }
            tabl.setHeaderVisible(true);
            tabl.setLinesVisible(true);
            table.setLabelProvider(this);
            table.refresh();
        }
        
        /**
         * Refresh table.
         *
         * @param properties
         */
        public void refreshTable(String[] properties){
            clearColumns();
            int count = properties.length;
            for(int i=0; i<count; i++){
                TableColumn col = columns.get(i);
                col.setText(properties[i]);
                col.setWidth(REAL_SIZE);
            }
        }
        
        /**
         * Clear all columns.
         */
        private void clearColumns(){
            for(TableColumn col : columns){
                col.setText("");
                col.setWidth(DEF_SIZE);
            }
        }
        
        /**
         * Returns maximum columns that will be needed.
         *
         * @return
         */
        private int getMaxColumnCount(){
            int result = 0;
            for(String key : datasets.keySet()){
                int curr = datasets.get(key).getAllProperties().length;
                if(curr>result){
                    result = curr;
                }
            }
            return result;
        }
        
    }
    
    /**
     * Table content provider.
     * <p>
     *
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private class TableContentProvider implements IStructuredContentProvider {
        
        private static final int PAGE_SIZE = 100000;

        private List<TableRowWrapper> rows = new ArrayList<TableRowWrapper>();
        private List<TableRowWrapper> rowstore = new ArrayList<TableRowWrapper>();
        private Iterator<Node> allNodes;
        private String dataset;
        private String[] properties;


        
        @Override
        public Object[] getElements(Object inputElement) {
            return rows.toArray(new TableRowWrapper[0]);
        }


        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            if (newInput == null || !(newInput instanceof InputTableData)) {
                return;
            }
            InputTableData data = (InputTableData)newInput;
            if (!data.isNeedUpdate()) {
                return;
            }
            dataset = data.getDataset();
            String filter = data.getProperty();
            properties = datasets.get(dataset).getVisibleProperties();
            if(filter!=null){
                int filterInd = 0;
                for(int i=0; i<properties.length;i++){
                    if(properties[i].equals(filter)){
                        filterInd = i;
                        break;
                    }                                    
                }
                if(filterInd>0){
                    for(int i=filterInd-1;i>=0;i--){
                        properties[i+1]=properties[i];
                    }
                    properties[0]=filter;
                }
            }
            labelProvider.refreshTable(properties);
            rows.clear();
            
            uploadData(data,0);            
            table.getControl().setVisible(true);
        }

        
        public void uploadData( final List<Node> nodes) {
            table.getControl().setVisible(false);
        	Job updateJob = new Job("Upload data to table job") {            
        		@Override
        		protected IStatus run(IProgressMonitor monitor) { 

        		    LinkedHashSet<TableRowWrapper> result = new LinkedHashSet<TableRowWrapper>();
                    for (Node filterNode:nodes){
                        result.addAll(filterChildsFromRow(filterNode)); 
                    }
        		    rows=new ArrayList<MessageAndEventTableView.TableRowWrapper>(result);
        			return Status.OK_STATUS;
        		}


        	};
        	updateJob.addJobChangeListener(new JobChangeAdapter(){
                public void done(IJobChangeEvent event) {
                    if( PlatformUI.getWorkbench().isClosing())
                        return;
                    
                    Display.getDefault().asyncExec(new Runnable(){
                        public void run() {
                            table.getControl().setVisible(true);
                            table.refresh();
                        }
                    });
                };
        	});
        	updateJob.schedule(0);
        }
        /**
         *
         * @param filterNode
         * @return
         */
        protected Collection<TableRowWrapper> filterChildsFromRow(Node filterNode) {
            Collection<TableRowWrapper> result = new LinkedHashSet<MessageAndEventTableView.TableRowWrapper>();
            if (rows.isEmpty()) {
                return result;
            }
            final INodeType nodetype = ds.getNodeType(rows.get(0).node);
            TraversalDescription descr = ds.getChildrenTraversal(new Evaluator() {

                @Override
                public Evaluation evaluate(Path arg0) {
                    boolean includes = nodetype.equals(ds.getNodeType(arg0.endNode()));
                    return Evaluation.of(includes, !includes);
                }

            });
            for (Node child:descr.traverse(filterNode).nodes()){
               for (TableRowWrapper row:rows){
                   if (child.equals(row.getNode())){
                       result.add(row);
                       break;
                   }
               }
           }
            return result;
        }


        private void restoreRows() {
            rows.clear();
            rows.addAll(rowstore);
        }
        private void storeRows() {
            rowstore.clear();
            rowstore.addAll(rows);
        }
        	
        
        
        
        
        /**
         * Upload data for table.
         *
         * @param inputData InputTableData
         * @param index int
         */
        public void uploadData(final InputTableData inputData, final int index) {

            Job updateJob = new Job("Upload data to table job") {            
                @Override
                protected IStatus run(IProgressMonitor monitor) {                    
                    if((rows.size()-index)>PAGE_SIZE/4){
                        return Status.OK_STATUS;
                    }
                    DatasetInfo datasetInfo = datasets.get(dataset);
                    if(datasetInfo==null){
                        return Status.OK_STATUS;
                    }
                    GraphDatabaseService service = NeoServiceProviderUi.getProvider().getService();
                    Transaction tx = service.beginTx();            
                    try{
                        if (inputData != null) {
                            NodeTypes childType = datasetInfo.getType().getChildType();
                            allNodes = inputData.getNodesByFilter(service, childType);                            
                        }
                        int start = 0;
                        while(allNodes.hasNext()&&start<PAGE_SIZE){
                            rows.add(parseRow(allNodes.next(), properties));
                            start++;
                        }
                    }finally{
                        tx.finish();
                    }
                    return Status.OK_STATUS;
                }
            };
            updateJob.schedule(0);
            try {
                updateJob.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            table.refresh();
        }
        
        /**
         * Parse table row 
         *
         * @param node Node
         * @param properties String[]
         * @return TableRowWrapper
         */
        private TableRowWrapper parseRow(Node node, String[] properties){
            TableRowWrapper row = new TableRowWrapper();
            List<String> values = new ArrayList<String>(properties.length);
            DatasetInfo datasetInfo = datasets.get(dataset);
            if (datasetInfo.model != null) {
                for (String property : properties) {
                    String value;
                    if ("sector".equals(property)){
                        Node sector=node.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING).getOtherNode(node);
                        value=sector.getProperty(INeoConstants.PROPERTY_NAME_NAME, "").toString();
                    }else if ("trx".equals(property)){
                        value=node.getProperty(INeoConstants.PROPERTY_NAME_NAME, "").toString();
                    }  else if (datasetInfo.trxProp.contains(property)){
                        value = "";
                        if ("Illegal Frequencies".equals(property)) {
                            StringBuilder bl=new StringBuilder("[");
                            Node proxy = datasetInfo.modl.findProxy(node);
                            boolean fst = false;
                            if (proxy != null) {
                                for (Relationship rel : datasetInfo.modl.getOutgoingRelations(proxy)) {
                                    if (fst) {
                                        bl.append(", ");
                                    }
                                    Integer freq = (Integer)datasetInfo.modl.findNodeFromProxy(rel.getOtherNode(proxy))
                                            .getProperty("frequency");
                                    bl.append(freq);
                                    fst = true;
                                }
                            }
                            bl.append("]");
                            value = bl.toString();
                        } else {
                            value = node.getProperty(property, "").toString();
                        }
                    }else{
                        Node plan=datasetInfo.model.findPlanNode(node);
                        if (plan!=null&&INeoConstants.PROPERTY_SECTOR_ARFCN.equals(property)&&plan.hasProperty(INeoConstants.PROPERTY_MAL)){
                            int[] mal = (int[])plan.getProperty(INeoConstants.PROPERTY_MAL);
                            value=Arrays.toString(mal);
                        }else{
                            value=plan==null?"":plan.getProperty(property, "").toString();
                        }
                    }
                    values.add(value);
                }
            } else {
                for (String property : properties) {
                    String value = node.getProperty(property, "").toString();
                    values.add(value);
                }
            }
            row.setValues(values);
            row.setNode(node);
            return row;
        }
    }
    /**
     * Data for build table.
     * <p>
     *
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    private class InputTableData{
        private String dataset;
        private String property;
        private String expression;
        
        /**
         * Constructor.
         * @param datasetName
         * @param propertyName
         * @param expressionMask
         */
        public InputTableData(String datasetName, String propertyName, String expressionMask) {
            dataset = datasetName;
            property = propertyName;
            expression = expressionMask;
        }
        
        /**
         * Get nodes iterator.
         *
         * @param service NeoService
         * @param childType NodeTypes
         * @return Iterator
         */
        private Iterator<Node> getNodesByFilter(final GraphDatabaseService service, final NodeTypes childType){
            final DatasetInfo datasetInfo = datasets.get(dataset);
            Node datasetNode = datasetInfo.getDataset();
            Iterator<Node> result = datasetNode.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
                
                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    Node currentNode = currentPos.currentNode();
                    NodeTypes type = NodeTypes.getNodeType(currentNode, service);
                    if(type==null || !type.equals(childType)){
                        return false;
                    }
                    if(property==null){
                        return true;
                    }
                    Object objValue;
                    if (datasetInfo.model!=null){
                        if ("sector".equals(property)){
                            Node node = currentNode.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING).getOtherNode(currentNode);
                            objValue=node.getProperty(INeoConstants.PROPERTY_NAME_NAME, null);
                        }    else  if ("trx".equals(property)){
                            objValue=currentNode.getProperty(INeoConstants.PROPERTY_NAME_NAME, null);
                        }else  if (datasetInfo.trxProp.contains(property)){
                            objValue= currentNode.getProperty(property, null);
                        }else{
                            Node node = datasetInfo.model.findPlanNode(currentNode);
                            objValue=node==null?null:node.getProperty(property, null);
                        }
                    }else{
                        objValue= currentNode.getProperty(property, null);
                    }
                    String realValue = objValue==null?null:objValue.toString();
                    if(expression.equals(EXPRESSION_EMPTY)){
                        return realValue==null;
                    }
                    if(expression.equals(EXPRESSION_NOT_EMPTY)){
                        return realValue!=null;
                    }
                    return isGoodValue(realValue);
                }
            }, GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING,
               GeoNeoRelationshipTypes.NEXT,Direction.OUTGOING).iterator();
            return result;
        }
        
        /**
         * Is property correct.
         *
         * @param value String
         * @return boolean
         */
        private boolean isGoodValue(String value){
            return value!=null&&Pattern.matches(expression, value);
        }
        
        /**
         * @return Returns the dataset.
         */
        public String getDataset() {
            return dataset;
        }
        
        /**
         * @return Returns the property.
         */
        public String getProperty() {
            return property;
        }
         
        /**
         * Is table need update.
         *
         * @return boolean
         */
        public boolean isNeedUpdate(){
            if(dataset==null){
                return false;
            }
            if(property == null){
                return true;
            }
            return expression!=null;
        }
        
    }
    
    /**
     * Table row.
     * <p>
     *
     * </p>
     * @author Shcharbatsevich_A
     * @since 1.0.0
     */
    public class TableRowWrapper{
        private List<String> values;
        private Node node;
        
        /**
         * @param values The values to set.
         */
        public void setValues(List<String> values) {
            this.values = values;
        }
        
        /**
         * Returns value by index;
         *
         * @param index
         * @return String
         */
        public String getValue(int index){
            if(index>=values.size()){
                return "";
            }
            return values.get(index);
        }
        
        /**
         * @return Returns the node.
         */
        public Node getNode() {
            return node;
        }
        
        /**
         * @param node The node to set.
         */
        public void setNode(Node node) {
            this.node = node;
        }
    }
    
    @Override
    public void saveState(IMemento memento) {
        super.saveState(memento);
        memento.putString(MEM_DATASET, cDataset.getText());
        memento.putString(MEM_PROPERTY, cProperty.getText());
        memento.putString(MEM_EXPRESSION, cExpression.getText());
        int datasetsCount = 0;
       
        for(String dataset : datasets.keySet()){
            memento.putString(MEM_DATASET_MAP+datasetsCount++, dataset);
            String[] invisible = datasets.get(dataset).getNotVisibleProperties();
            int propCount = invisible.length;
            String memKey = getMemKey(dataset);
         
            memento.putInteger(MEM_PROPERTY_COUNT+memKey, propCount);
            for(int i=0; i<propCount;i++){
                memento.putString(MEM_PROPERTY_MAP+memKey+i, invisible[i]);
            }
        }
        memento.putInteger(MEM_DATASET_MAP_COUNT, datasetsCount);
    }

    /**
     * Convert dataset name to key for memento.
     *
     * @param dataset
     * @return String
     */
    private String getMemKey(String dataset) {   
        String result = StringEscapeUtils.escapeXml(dataset);
        result = result.replace(" ", "_");
        result = result.replace("+", "_");
        result = result.replace(",", "_");
        return result;
    }
    
    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException {
        super.init(site, memento);
        ds=NeoServiceFactory.getInstance().getDatasetService();
        if (memento == null) {
            return;
        }
        initDataset = memento.getString(MEM_DATASET);
        initProperty = memento.getString(MEM_PROPERTY);
        initExpression = memento.getString(MEM_EXPRESSION);
        int datasetsCount = memento.getInteger(MEM_DATASET_MAP_COUNT);
        initDatasets = new HashMap<String, List<String>>(datasetsCount);
        for(int i=0;i<datasetsCount;i++){
            String dataset = memento.getString(MEM_DATASET_MAP+i);
            String memKey = getMemKey(dataset);
            int propsCount = memento.getInteger(MEM_PROPERTY_COUNT+memKey);
            List<String> props = new ArrayList<String>(propsCount);
            for(int j=0; j<propsCount;j++){
                props.add(MEM_PROPERTY_MAP+memKey+i);
            }
            initDatasets.put(dataset, props);
        }
        
    }
    
    @Override
    public void dispose() {
        if (propertySheetPage != null) {
            propertySheetPage.dispose();
        }
        super.dispose();
    }

    /**
     *
     * @param root
     * @return
     */
    private Entry<String,DatasetInfo> findFirst(Node root) {
        for (Entry<String,DatasetInfo> entry:datasets.entrySet()){
            if (entry.getValue().dataset.equals(root)){
                return entry;
            }
        }
        return null;
    }
}
