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

package org.amanzi.neo.services.impl.statistics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Stack;

import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeManager;
import org.amanzi.neo.nodetypes.NodeTypeNotExistsException;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.impl.NodeService.NodeServiceRelationshipType;
import org.amanzi.neo.services.impl.internal.AbstractService;
import org.amanzi.neo.services.impl.statistics.internal.NodeTypeVault;
import org.amanzi.neo.services.impl.statistics.internal.PropertyVault;
import org.amanzi.neo.services.impl.statistics.internal.StatisticsVault;
import org.amanzi.neo.services.statistics.IPropertyStatisticsNodeProperties;
import org.amanzi.neo.services.statistics.IPropertyStatisticsService;
import org.apache.commons.lang3.math.NumberUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class PropertyStatisticsService extends AbstractService implements IPropertyStatisticsService {

    public enum PropertyStatisticsRelationshipType implements RelationshipType {
        PROPERTY_STATISTICS;
    }

    private final INodeService nodeService;

    private final IPropertyStatisticsNodeProperties statisticsNodeProperties;

    /**
     * @param graphDb
     * @param generalNodeProperties
     */
    public PropertyStatisticsService(final GraphDatabaseService graphDb, final IGeneralNodeProperties generalNodeProperties,
            final INodeService nodeService, final IPropertyStatisticsNodeProperties statisticsNodeProperties) {
        super(graphDb, generalNodeProperties);

        this.nodeService = nodeService;
        this.statisticsNodeProperties = statisticsNodeProperties;
    }

    private Node createChildVaultNode(final Node parentVaultNode, final String name) throws ServiceException {
        return nodeService.createNode(parentVaultNode, PropertyStatisticsNodeType.STATISTICS_VAULT,
                NodeServiceRelationshipType.CHILD, name);
    }

    private Iterator<Node> getChildren(final Node node) throws ServiceException {
        return nodeService.getChildren(node, PropertyStatisticsNodeType.STATISTICS_VAULT);
    }

    private Node getChildVaultNode(final Node parentVaultNode, final String name) throws ServiceException {
        return nodeService.getChildByName(parentVaultNode, name, PropertyStatisticsNodeType.STATISTICS_VAULT);
    }

    private int getCount(final Node node, final boolean shouldExist) throws ServiceException {
        return nodeService.getNodeProperty(node, statisticsNodeProperties.getCountProperty(), shouldExist ? null : 0, shouldExist);
    }

    protected Node getStatisticsNode(final Node datasetNode) throws ServiceException {
        Node result = nodeService.getSingleChild(datasetNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                PropertyStatisticsRelationshipType.PROPERTY_STATISTICS);

        if (result == null) {
            result = nodeService.createNode(datasetNode, PropertyStatisticsNodeType.PROPERTY_STATISTICS,
                    PropertyStatisticsRelationshipType.PROPERTY_STATISTICS);
        }

        return result;
    }

    protected NodeTypeVault loadNodeTypeVault(final Node nodeTypeVaultNode) throws ServiceException, NodeTypeNotExistsException {
        String nodeTypeId = nodeService.getNodeName(nodeTypeVaultNode);
        INodeType nodeType = NodeTypeManager.getInstance().getType(nodeTypeId);

        NodeTypeVault vault = new NodeTypeVault(nodeType);
        vault.setCount(getCount(nodeTypeVaultNode, true));

        Iterator<Node> propertyVaultNodeIterator = getChildren(nodeTypeVaultNode);
        while (propertyVaultNodeIterator.hasNext()) {
            vault.addPropertyVault(loadPropertyVault(propertyVaultNodeIterator.next()));
        }

        return vault;
    }

    protected PropertyVault loadPropertyVault(final Node propertyVaultNode) throws ServiceException {
        String propertyName = nodeService.getNodeName(propertyVaultNode);

        PropertyVault vault = new PropertyVault(propertyName);
        vault.setClass((String)nodeService.getNodeProperty(propertyVaultNode, statisticsNodeProperties.getClassProperty(), null,
                true));

        int size = nodeService.getNodeProperty(propertyVaultNode, getGeneralNodeProperties().getSizeProperty(), null, true);

        for (int i = 0; i < size; i++) {
            Object value = nodeService
                    .getNodeProperty(propertyVaultNode, statisticsNodeProperties.getValuePrefix() + i, null, true);
            int count = nodeService.getNodeProperty(propertyVaultNode, statisticsNodeProperties.getCountPrefix() + i, null, true);

            vault.addValue(value, count);
        }
        vault.setDefaultValue(nodeService.getNodeProperty(propertyVaultNode, statisticsNodeProperties.getDefaultValueProperty(),
                null, false));
        return vault;
    }

    @Override
    public synchronized StatisticsVault loadStatistics(final Node rootNode) throws ServiceException, NodeTypeNotExistsException {
        assert rootNode != null;

        Node statisticsNode = getStatisticsNode(rootNode);

        return loadStatisticsVault(statisticsNode);
    }

    protected StatisticsVault loadStatisticsVault(final Node node) throws ServiceException, NodeTypeNotExistsException {
        StatisticsVault vault = new StatisticsVault();

        vault.setCount(getCount(node, false));

        Iterator<Node> nodeTypeVaultNodesIterator = getChildren(node);
        while (nodeTypeVaultNodesIterator.hasNext()) {
            vault.addNodeTypeVault(loadNodeTypeVault(nodeTypeVaultNodesIterator.next()));
        }

        return vault;
    }

    @Override
    public void renameProperty(final Node rootNode, final INodeType nodeType, final String propertyName, final Object oldValue,
            final Object newValue) throws ServiceException {
        Node statisticsNode = getStatisticsNode(rootNode);
        Node nodeTypeVault = getChildVaultNode(statisticsNode, nodeType.getId());
        Node propertyVault = getChildVaultNode(nodeTypeVault, propertyName);
        for (String property : propertyVault.getPropertyKeys()) {
            if (propertyVault.getProperty(property).equals(oldValue)) {
                nodeService.updateProperty(propertyVault, property, newValue);
                break;
            }
        }
    }

    protected void saveNodeTypeVault(final Node statisticsNode, final NodeTypeVault vault) throws ServiceException {
        Node nodeTypeVault = updateNodeTypeVault(statisticsNode, vault);

        for (PropertyVault propertyVault : vault.getAllPropertyVaults()) {
            if (propertyVault.isChanged()) {
                savePropertyStatistics(nodeTypeVault, propertyVault);
            }
        }
    }

    protected void savePropertyStatistics(final Node nodeTypeVault, final PropertyVault vault) throws ServiceException {
        Node propertyVault = getChildVaultNode(nodeTypeVault, vault.getPropertyName());

        if (propertyVault == null) {
            propertyVault = createChildVaultNode(nodeTypeVault, vault.getPropertyName());
        }

        updatePropertyVault(propertyVault, vault);
    }

    @Override
    public synchronized void saveStatistics(final Node node, final StatisticsVault vault) throws ServiceException {
        assert node != null;
        assert vault != null;

        Node statisticsNode = getStatisticsNode(node);

        saveStatisticsVault(statisticsNode, vault);
    }

    protected void saveStatisticsVault(final Node node, final StatisticsVault vault) throws ServiceException {
        if (vault.isChanged()) {
            updateStatisticsInfo(node, vault);

            for (NodeTypeVault nodeTypeVault : vault.getAllNodeTypeVaults()) {
                if (nodeTypeVault.isChanged()) {
                    saveNodeTypeVault(node, nodeTypeVault);
                }
            }
        }
    }

    protected Node updateNodeTypeVault(final Node statisticsNode, final NodeTypeVault vault) throws ServiceException {
        Node vaultNode = getChildVaultNode(statisticsNode, vault.getNodeType().getId());

        if (vaultNode == null) {
            vaultNode = createChildVaultNode(statisticsNode, vault.getNodeType().getId());
        }

        nodeService.updateProperty(vaultNode, statisticsNodeProperties.getCountProperty(), vault.getCount());

        return vaultNode;
    }

    protected void updatePropertyVault(final Node propertyVault, final PropertyVault vault) throws ServiceException {
        nodeService.updateProperty(propertyVault, statisticsNodeProperties.getClassProperty(), vault.getClassName());

        int size = nodeService.getNodeProperty(propertyVault, getGeneralNodeProperties().getSizeProperty(),
                NumberUtils.INTEGER_ZERO, false);

        Map<Object, Integer> values = new HashMap<Object, Integer>(vault.getValuesMap());

        Queue<Integer> removedIndexes = new LinkedList<Integer>();
        Stack<Integer> processedIndex = new Stack<Integer>();

        if (size > 0) {
            for (int i = 0; i < size; i++) {
                Object property = nodeService.getNodeProperty(propertyVault, statisticsNodeProperties.getValuePrefix() + i, null,
                        true);

                Integer newCount = values.remove(property);
                if (newCount != null) {
                    nodeService.updateProperty(propertyVault, statisticsNodeProperties.getCountPrefix() + i, newCount);
                } else {
                    removedIndexes.add(i);
                }
                processedIndex.add(i);
            }
        }

        // remove old values
        for (Integer index : removedIndexes) {
            nodeService.removeNodeProperty(propertyVault, statisticsNodeProperties.getValuePrefix() + index, false);
            nodeService.removeNodeProperty(propertyVault, statisticsNodeProperties.getCountPrefix() + index, false);
        }

        int counter = size;
        for (Entry<Object, Integer> statEntry : values.entrySet()) {
            counter = removedIndexes.isEmpty() ? counter : removedIndexes.remove();

            nodeService.updateProperty(propertyVault, statisticsNodeProperties.getValuePrefix() + counter, statEntry.getKey());
            nodeService.updateProperty(propertyVault, statisticsNodeProperties.getCountPrefix() + counter, statEntry.getValue());

            counter++;
        }

        for (Integer newIndex : removedIndexes) {
            int oldIndex = processedIndex.pop();
            nodeService.renameNodeProperty(propertyVault, statisticsNodeProperties.getValuePrefix() + oldIndex,
                    statisticsNodeProperties.getValuePrefix() + newIndex, false);
            nodeService.renameNodeProperty(propertyVault, statisticsNodeProperties.getCountPrefix() + oldIndex,
                    statisticsNodeProperties.getCountPrefix() + newIndex, false);
        }

        nodeService.updateProperty(propertyVault, getGeneralNodeProperties().getSizeProperty(), values.size());
        nodeService.updateProperty(propertyVault, statisticsNodeProperties.getDefaultValueProperty(), vault.getDefaultValue());
    }

    @Override
    public void updateStatistics(final Node rootNode, final StatisticsVault vault, final NodeTypeVault nodeType)
            throws ServiceException {
        Node statisticsNode = getStatisticsNode(rootNode);
        Node nodeTypeVault = getChildVaultNode(statisticsNode, nodeType.getNodeType().getId());
        updateNodeTypeVault(statisticsNode, nodeType);
        updateStatisticsInfo(statisticsNode, vault);

        for (PropertyVault propertyVault : nodeType.getAllPropertyVaults()) {
            savePropertyStatistics(nodeTypeVault, propertyVault);
        }
    }

    protected void updateStatisticsInfo(final Node node, final StatisticsVault vault) throws ServiceException {
        nodeService.updateProperty(node, statisticsNodeProperties.getCountProperty(), vault.getCount());
    }
}
