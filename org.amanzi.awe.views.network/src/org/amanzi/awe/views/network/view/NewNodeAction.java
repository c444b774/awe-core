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

import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.INodeType;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.IPropertyHeader;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.PropertyHeader;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Display;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class NewNodeAction extends Action {
    private final INodeType iNodeType;
    private final Node sourcedNode;
    private final GraphDatabaseService service;
    private Node targetNode;
    protected HashMap<String, Object> defaultProperties = new HashMap<String, Object>();

    public NewNodeAction(INodeType iNodeType, Node sourcedNode) {
        this.iNodeType = iNodeType;
        this.sourcedNode = sourcedNode;
        service = NeoServiceProvider.getProvider().getService();
        setText(iNodeType.getId());
    }

    @Override
    public void run() {
        System.out.println("Action " + iNodeType.getId() + " runned.");
        InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(), getText(), "Enter name of new element", "New " + iNodeType.getId(), null);
        int result = dialog.open();
        if (result != Dialog.CANCEL) {
            defaultProperties.put(INeoConstants.PROPERTY_NAME_NAME, dialog.getValue());
            createNewElement();
            postCreating();
            NeoServiceProvider.getProvider().commit();
        }

    }

    private void postCreating() {
//        if()
    }

    private void createNewElement() {
        Transaction tx = service.beginTx();
        try {
            targetNode = service.createNode();
            targetNode.setProperty("type", iNodeType.getId());
            sourcedNode.createRelationshipTo(targetNode, NetworkRelationshipTypes.CHILD);
            NodeTypes type = NodeTypes.getEnumById(iNodeType.getId());
            if (type != null) {
                IPropertyHeader ph = PropertyHeader.getPropertyStatistic(NeoUtils.getParentNode(sourcedNode, NodeTypes.NETWORK.getId()));
                Map<String, Object> statisticProperties = ph.getStatisticParams(type);
                for (String key : statisticProperties.keySet()) {
                    targetNode.setProperty(key, statisticProperties.get(key));
                }
            }
            for (String key : defaultProperties.keySet()) {
                if (!targetNode.hasProperty(key))
                    targetNode.setProperty(key, defaultProperties.get(key));
            }

            tx.success();
        } catch (Exception e) {
            e.printStackTrace();
            tx.failure();
        } finally {
            tx.finish();
        }
    }
}