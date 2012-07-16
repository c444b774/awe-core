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

package org.amanzi.awe.statistics.service;

import java.util.Iterator;

import org.amanzi.awe.statistics.enumeration.DimensionTypes;
import org.amanzi.awe.statistics.enumeration.Period;
import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.awe.statistics.enumeration.StatisticsRelationshipTypes;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.DatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;

/**
 * <p>
 * Service statistics
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsService extends AbstractService {
    /*
     * logger instantiation
     */
    private static final Logger LOGGER = Logger.getLogger(StatisticsService.class);

    /**
     * reverse traversal
     */
    protected final TraversalDescription REVERSE_CHILDREN_CHAIN_TRAVERSAL_DESCRIPTION = Traversal.description().depthFirst()
            .relationships(DatasetRelationTypes.NEXT, Direction.INCOMING)
            .relationships(DatasetRelationTypes.CHILD, Direction.INCOMING).evaluator(Evaluators.all());
    /*
     * services instantiation
     */
    DatasetService datasetService;

    private StatisticsService() {
        if (datasetService == null) {
            datasetService = NeoServiceFactory.getInstance().getDatasetService();
        }
    }

    static StatisticsService service;

    public static StatisticsService getInstance() {
        if (service == null) {
            service = new StatisticsService();
        }
        return service;
    }

    /**
     * find statistic root model
     * 
     * @param parentNode
     * @param name
     * @param type
     * @return
     */
    public Node findStatistic(Node parentNode, String name) throws IllegalArgumentException {
        return findFirstRelationshipNode(parentNode, NAME, name, StatisticsRelationshipTypes.STATISTICS);
    }

    /**
     * create statistic model root node
     * 
     * @param parent
     * @param name
     * @param type
     * @return
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public Node createStatisticsModelRoot(Node parent, String name, boolean isNeedToSearchDuplicate) throws DatabaseException {
        LOGGER.info("create statistic model node not found. parent:" + parent + " name:" + name);
        if (parent == null) {
            LOGGER.error("parentNode is null");
            throw new IllegalArgumentException("parentNode can't be null");
        }
        if (isNeedToSearchDuplicate) {
            Node finded = findFirstRelationshipNode(parent, NAME, name, StatisticsRelationshipTypes.STATISTICS);
            if (finded != null) {
                LOGGER.error("Statistics model root with name " + name + " is already exists");
                throw new DatabaseException("Statistics model root with name " + name + " is already exists");
            }
        }
        Node statisticsRoot = createNode(parent, StatisticsRelationshipTypes.STATISTICS, StatisticsNodeTypes.STATISTICS_MODEL);
        try {
            setAnyProperty(parent, NAME, name);
        } catch (IllegalNodeDataException e) {
            LOGGER.error("unexpected exception");
            /*
             * cann't be thrown
             */
        }
        return statisticsRoot;
    }

    /**
     * find dimension root
     * 
     * @param rootNode
     * @param type
     * @return
     */
    public Node findDimension(Node rootNode, DimensionTypes type) {
        return findFirstRelationshipNode(rootNode, NAME, type.getId(), DatasetRelationTypes.CHILD);
    }

    /**
     * create new Dimension node
     * 
     * @param parent
     * @param type
     * @return
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public Node createDimension(Node parent, DimensionTypes type, boolean isNeedToSearchDuplicate) throws DatabaseException,
            IllegalNodeDataException {
        LOGGER.info("create dimension model node . parent:" + parent + " name:" + type.getId());
        return findOrCreateNode(parent, NAME, type.getId(), StatisticsNodeTypes.DIMENSION, isNeedToSearchDuplicate, Boolean.FALSE);
    }

    /**
     * find statistics level by name
     * 
     * @param parentNode
     * @param levelName
     */
    public Node findStatisticsLevelNode(Node parentNode, String levelName) {
        return findFirstRelationshipNode(parentNode, NAME, levelName, DatasetRelationTypes.CHILD);
    }

    /**
     * create statistics level node
     * 
     * @param parent
     * @param name
     * @return
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public Node createStatisticsLevelNode(Node parent, String name, boolean isNeedToSearchDuplicate) throws DatabaseException,
            IllegalNodeDataException {
        LOGGER.info("create StatisticsLevel model node . parent:" + parent + " name:" + name);
        return findOrCreateNode(parent, NAME, name, StatisticsNodeTypes.LEVEL, isNeedToSearchDuplicate, Boolean.FALSE);
        // return findOrCreateNamedNode(parent, name, DatasetRelationTypes.CHILD,
        // StatisticsNodeTypes.LEVEL, isNeedToSearchDuplicate);
    }

    /**
     * try to find aggregatedModel
     * 
     * @param rootNode
     * @param rootNode2
     * @return
     */
    public Node findAggregatedStatistics(Node firstLevel, Node secondLevel) {
        Iterable<Node> firstLevelChilds = datasetService.getChildrenTraverser(firstLevel);
        Iterable<Node> secondLevelChilds = datasetService.getChildrenTraverser(secondLevel);
        if (firstLevelChilds == null || secondLevelChilds == null) {
            return null;
        }
        for (Node aggregated : firstLevelChilds) {
            for (Relationship rel : aggregated.getRelationships(Direction.INCOMING, DatasetRelationTypes.CHILD)) {
                if (rel.getOtherNode(aggregated).equals(secondLevel)) {
                    return aggregated;
                }
            }
        }
        return null;
    }

    /**
     * create new statistics node and correlate it between first and second levels
     * 
     * @param firstLevel
     * @param secondLevel
     * @param name
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public Node createAggregatedStatistics(Node firstLevel, Node secondLevel, String name) throws DatabaseException,
            IllegalNodeDataException {
        Node newNode = createNode(firstLevel, DatasetRelationTypes.CHILD, StatisticsNodeTypes.STATISTICS);
        createRelationship(secondLevel, newNode, DatasetRelationTypes.CHILD);
        setAnyProperty(newNode, NAME, name);
        return newNode;
    }

    /**
     * try to create new s_group node. if isNeedToSearchDuplicate is true and node is already exist
     * - {@link DatabaseException} will be thrown
     * 
     * @param rootNode
     * @param name
     * @param isNeedToSearchDuplicate
     * @return
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public Node createSGroup(Node rootNode, String name, boolean isNeedToSearchDuplicate) throws DatabaseException,
            IllegalNodeDataException {
        return findOrCreateNode(rootNode, NAME, name, StatisticsNodeTypes.S_GROUP, isNeedToSearchDuplicate, Boolean.TRUE);
    }

    /**
     * create S_ROW node and added it to child->next chain
     * 
     * @param rootNode
     * @param timestamp
     * @param isNeedToSearchDuplicate
     * @return
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public Node createSRow(Node rootNode, Long timestamp, String name, boolean isNeedToSearchDuplicate) throws DatabaseException,
            IllegalNodeDataException {
        Node sRow = findOrCreateNode(rootNode, NAME, name, StatisticsNodeTypes.S_ROW, isNeedToSearchDuplicate, Boolean.TRUE);
        if (timestamp != null) {
            setAnyProperty(sRow, DriveModel.TIMESTAMP, timestamp);
        }
        return sRow;
    }

    /**
     * try to create new s_cell node. if isNeedToSearchDuplicate is true and node is already exist -
     * {@link DatabaseException} will be thrown
     * 
     * @param parentSrow S_ROW root of branch s_cell will belongs to
     * @param name name of S_CELL
     * @param isNeedToSearchDuplicate - if need to search duplicate
     * @return
     */
    public Node createSCell(Node parentSrow, String name, boolean isNeedToSearchDuplicate) throws DatabaseException,
            IllegalNodeDataException {
        return findOrCreateNode(parentSrow, NAME, name, StatisticsNodeTypes.S_CELL, isNeedToSearchDuplicate, Boolean.TRUE);
    }

    /**
     * create SOURCE relationship between periodNode and sourceNOde
     * 
     * @param periodNode
     * @param underline
     * @throws DatabaseException
     */
    public void addSource(Node periodNode, Node sourceNode) throws DatabaseException {
        LOGGER.info("add source:" + sourceNode + " to period:" + periodNode);
        createRelationship(periodNode, sourceNode, StatisticsRelationshipTypes.SOURCE);
    }

    /**
     * return sources nodes;
     * 
     * @param parentNode
     * @return
     */
    public Iterable<Node> getSources(Node parentNode) {
        return datasetService.getFirstRelationTraverser(parentNode, StatisticsRelationshipTypes.SOURCE, Direction.OUTGOING);
    }

    /**
     * see({@link DatasetService#getFirstRelationTraverser(Node, RelationshipType, Direction)})
     * 
     * @param parent
     * @param relType
     * @return
     */
    public Iterable<Node> getFirstRelationTraverser(Node parent, RelationshipType relType) {
        return datasetService.getFirstRelationTraverser(parent, relType, Direction.OUTGOING);
    }

    /**
     * searching for a highest period;
     * 
     * @param existedPeriods
     */
    public Node getHighestPeriod(Iterable<Node> existedPeriods) {
        Period[] sortedPeriods = Period.getSortedPeriods();
        for (Period period : sortedPeriods) {
            for (Node existed : existedPeriods) {
                if (!existed.getProperty(DatasetService.TYPE).equals(StatisticsNodeTypes.LEVEL.getId())) {
                    continue;
                }
                if (period.getId().equals(existed.getProperty(NAME))) {
                    return existed;
                }
            }
        }
        return null;
    }

    /**
     * see {@link DatasetService#findNodeInChainByProperty(Node, String, Object)}
     * 
     * @param rootNode
     * @param propertyName
     * @param value
     * @return
     */
    public Node findNodeInChain(Node rootNode, String propertyName, Object value) {
        return datasetService.findNodeInChainByProperty(rootNode, propertyName, value);
    }

    /**
     * return node property value or null if not exist
     * 
     * @param node
     * @param propertyName
     * @return
     */
    public Object getNodeProperty(Node node, String propertyName) {
        return node.getProperty(propertyName, null);
    }

    /**
     * return node type
     * 
     * @param node
     * @return
     */
    public String getType(Node node) {
        return AbstractService.getNodeType(node);
    }

    /**
     * try to create new node with required attributes node. if isNeedToSearchDuplicate is true and
     * node is already exist - {@link DatabaseException} will be thrown
     * 
     * @param rootNode start searching point
     * @param propertyName property name to search
     * @param propertyValue property value to search
     * @param nodeType node type
     * @param isNeedToSearchDuplicate
     * @return
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    private Node findOrCreateNode(Node rootNode, String propertyName, Object propertyValue, INodeType nodeType,
            boolean isNeedToSearchDuplicate, boolean addToChain) throws DatabaseException, IllegalNodeDataException {
        if (isNeedToSearchDuplicate) {
            Node srowNode = findNodeInChain(rootNode, propertyName, propertyValue);
            if (srowNode != null) {
                LOGGER.error("Duplicated node found");
                throw new DatabaseException("Duplicated node founded");
            }
        }
        return createNode(rootNode, propertyName, propertyValue, nodeType, addToChain);
    }

    /**
     * method for search required node by firstRelationship
     * 
     * @param parentNode
     * @param propertyName
     * @param propertyValue
     * @param relType
     * @return
     */
    private Node findFirstRelationshipNode(Node parentNode, String propertyName, String propertyValue, RelationshipType relType) {
        LOGGER.info("try to find node with propetyName:" + propertyName + " value:" + propertyValue + " from parent:" + parentNode
                + " by relationship: " + relType);
        Iterator<Node> statisticsNodes = getFirstRelationTraverser(parentNode, relType).iterator();
        while (statisticsNodes.hasNext()) {
            Node currentNode = statisticsNodes.next();
            if (currentNode.getProperty(propertyName, StringUtils.EMPTY).equals(propertyValue)) {
                LOGGER.info("node founded propetyName:" + propertyName + " value:" + propertyValue + " from parent:" + parentNode
                        + " by relationship: " + relType);
                return currentNode;
            }
        }
        LOGGER.info("node ngetFirstRelationsipsNodesot found. propetyName:" + propertyName + " value:" + propertyValue
                + " from parent:" + parentNode + " by relationship: " + relType);
        return null;
    }

    /**
     * see({@link DatasetService#getChildrenChainTraverser(Node)})
     * 
     * @param rootNode
     * @return
     */
    public Iterable<Node> getChildrenChainTraverser(Node rootNode) {
        return datasetService.getChildrenChainTraverser(rootNode);
    }

    /**
     * remove node property if, node doesn't contain propertyName then throw
     * {@link DatabaseException}
     * 
     * @param rootNode
     * @param propertyFlaggedName
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public void removeNodeProperty(Node node, String propertyName) throws DatabaseException, IllegalNodeDataException {
        if ((propertyName == null) || propertyName.equals(StringUtils.EMPTY)) {
            throw new IllegalNodeDataException(propertyName + " cannot be empty.");
        }
        if (node == null) {
            throw new IllegalArgumentException("Node is null.");
        }
        Transaction tx = graphDb.beginTx();
        try {
            node.removeProperty(propertyName);
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not set " + propertyName, e);
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
    }

    /**
     * search parentLevel if more
     * 
     * @param node
     * @return
     */
    public Node getParentLevelNode(Node node) {
        Iterable<Node> upperLevel = REVERSE_CHILDREN_CHAIN_TRAVERSAL_DESCRIPTION.traverse(node).nodes();
        String nodeType = (String)getNodeProperty(node, DatasetService.TYPE);
        for (Node searchable : upperLevel) {
            String searchableType = (String)getNodeProperty(searchable, DatasetService.TYPE);
            if (!searchableType.equals(nodeType)) {
                return searchable;
            }
        }
        return null;
    }

    /**
     * create new node in CHILD->NEXT chain {@link DatasetService#addChild(Node,Node,Node)} if
     * addToChain is TRUE, else add new node as child {@link DatasetService#addChild(Node,Node)}
     * 
     * @param rootNode
     * @param propertyName
     * @param value
     * @param type
     * @return
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    private Node createNode(Node rootNode, String propertyName, Object value, INodeType type, boolean addToChain)
            throws DatabaseException, IllegalNodeDataException {
        Node srowNode = null;
        try {
            srowNode = createNode(type);
            setAnyProperty(srowNode, propertyName, value);
        } catch (DatabaseException e) {
            LOGGER.error("Unexpectable exception thrown. Cann't compleatly identify S_ROW node type", e);
            throw e;
        }
        if (addToChain) {
            return datasetService.addChild(rootNode, srowNode, null);
        } else {
            return datasetService.addChild(rootNode, srowNode);
        }
    }

}
