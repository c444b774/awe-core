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

package org.amanzi.neo.services;

import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

/**
 * <p>
 * This class manages access to network elements
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class NewNetworkService extends NewAbstractService {

    public final static String CELL_INDEX = "ci";
    public final static String LOCATION_AREA_CODE = "lac";

    private static Logger LOGGER = Logger.getLogger(NewNetworkService.class);

    // TODO: LN: do not use tx as global fiels to prevent problems with Transactions
    private Transaction tx;
    private NewDatasetService datasetService;

    /**
     * <p>
     * This enum describes types of network elements.
     * </p>
     * 
     * @author grigoreva_a
     * @since 1.0.0
     */
    public enum NetworkElementNodeType implements INodeType {
        NETWORK, BSC, SITE, SECTOR, CITY, MSC, SELECTION_LIST_ROOT;
        @Override
        public String getId() {
            return name().toLowerCase();
        }

    }

    public NewNetworkService() {
        super();
        datasetService = new NewDatasetService();
    }

    public NewNetworkService(GraphDatabaseService graphDb) {
        super(graphDb);
        datasetService = new NewDatasetService(graphDb);
    }

    /**
     * Creates a new network element of defined <code>elementType</code>, sets its <code>name</code>
     * property, adds this element to index <code>indexName</code>, and attaches this element to
     * <code>parent</code> node.
     * 
     * @param parent
     * @param indexName
     * @param name
     * @param elementType
     * @return the newly created network element node
     */
    public Node createNetworkElement(Node parent, String indexName, String name, INodeType elementType)
            throws IllegalNodeDataException, DatabaseException {
        LOGGER.debug("start createNetworkElement(Node parent, String indexName, String name, INodeType elementType)");

        // validate parameters
        if (parent == null) {
            throw new IllegalArgumentException("Parent is null.");
        }
        if ((indexName == null) || (indexName.equals(""))) {
            throw new IllegalArgumentException("indexName is null or empty");
        }
        if ((name == null) || (name.equals(""))) {
            throw new IllegalNodeDataException("Name cannot be empty");
        }
        if (elementType == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        if (elementType.equals(NetworkElementNodeType.SECTOR)) {
            throw new IllegalArgumentException("To create a sector use method createSector()");
        }

        tx = graphDb.beginTx();
        Node result = null;
        try {
            result = createNode(elementType);
            datasetService.addChild(parent, result);
            setNameProperty(result, name);
            addNodeToIndex(result, indexName, NAME, name);
            tx.success();
            // TODO: LN: where is exception handling?
        } finally {
            tx.finish();
        }
        return result;
    }

    /**
     * Finds a network element by <code>name</code> in index <code>indexName</code>
     * 
     * @param indexName name of index, where to look for the element. Call
     *        {@link NewAbstractService#getIndexKey(Node, INodeType)} to generate this name.
     * @param name the value of NAME property to look for
     * @return a network element node or <code>null</code>, if nothing found
     */
    public Node findNetworkElement(String indexName, String name) {
        LOGGER.debug("start findNetworkElement(String indexName, String name)");

        // validate parameters
        if ((indexName == null) || (indexName.equals(""))) {
            throw new IllegalArgumentException("indexName is null or empty");
        }
        if ((name == null) || (name.equals(""))) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        // Find element by index
        // TODO: LN, use Index instead of indexName
        Index<Node> index = graphDb.index().forNodes(indexName);
        Node result = index.get(NAME, name).getSingle();
        return result;
    }

    /**
     * Looks for a network element in <code>indexName</code> by <code>name</code>, or creates a new
     * network element and attaches it to <code>parent</code> node, and adds to index
     * 
     * @param parent is used only if the element is not found in index
     * @param indexName name of index
     * @param name the value of indexed NAME property
     * @param elementType is used only if element was not found
     * @return found or created node
     */
    public Node getNetworkElement(Node parent, String indexName, String name, INodeType elementType)
            throws IllegalNodeDataException, DatabaseException {
        LOGGER.debug("start getNetworkElement(Node parent, String indexName, String name, INodeType elementType)");

        Node result = findNetworkElement(indexName, name);
        if (result == null) {
            result = createNetworkElement(parent, indexName, name, elementType);
        }
        return result;
    }

    /**
     * Creates a sector node with specified parameters, attaches it with CHILD relationship to
     * <code>parent</code>, sets its properties, and adds it to index
     * 
     * @param parent
     * @param indexName
     * @param name the value of NAME property
     * @param ci the value of CELL_INDEX property
     * @param lac the value of LOCATION_AREA_CODE property
     * @return the newly created sector node
     */
    public Node createSector(Node parent, String indexName, String name, String ci, String lac) throws IllegalNodeDataException,
            DatabaseException {
        LOGGER.debug("start createSector(Node parent, String indexName, String name, String ci, String lac)");

        // validate parameters
        if (parent == null) {
            throw new IllegalArgumentException("Parent is null.");
        }
        if (!NetworkElementNodeType.SITE.getId().equals(parent.getProperty(TYPE, null))) {
            throw new IllegalArgumentException("Parent node must be of type SITE.");
        }
        if ((indexName == null) || (indexName.equals(""))) {
            throw new IllegalArgumentException("indexName is null or empty");
        }
        if (((name == null) || (name.equals(""))) && ((ci == null) || (ci.equals("")) || (lac == null) || (lac.equals("")))) {
            throw new IllegalNodeDataException("Name or CI+LAC must be set");
        }

        tx = graphDb.beginTx();
        Node result = null;
        try {
            result = createNode(NetworkElementNodeType.SECTOR);
            datasetService.addChild(parent, result);
            // set properties and index node
            if ((name != null) && (!name.equals(""))) {
                setNameProperty(result, name);
                addNodeToIndex(result, indexName, NAME, name);
            }
            // TODO: LN: use StringUtils.EMPTY_STRING constant
            if ((ci != null) && (!ci.equals(""))) {
                result.setProperty(CELL_INDEX, ci);
                addNodeToIndex(result, indexName, CELL_INDEX, ci);
            }
            if ((lac != null) && (!lac.equals(""))) {
                result.setProperty(LOCATION_AREA_CODE, lac);
                addNodeToIndex(result, indexName, LOCATION_AREA_CODE, lac);
            }
            tx.success();
        } finally {
            tx.finish();
        }
        return result;

    }

    /**
     * Looks for a sector in <code>indexName</code> by the specified parameters. At least one
     * parameter must be not <code>null</code>
     * 
     * @param indexName the name of index
     * @param name the value of NAME property
     * @param ci the value of CELL_INDEX property
     * @param lac the value of LOCATION_AREA_CODE property
     * @return a sector node or <code>null</code> if nothing was found
     */
    public Node findSector(String indexName, String name, String ci, String lac) {
        LOGGER.debug("start findSector(String indexName, String name, String ci, String lac)");
        // validate parameters
        if ((indexName == null) || (indexName.equals(""))) {
            throw new IllegalArgumentException("indexName is null or empty");
        }
        if (((name == null) || (name.equals(""))) && ((ci == null) || (ci.equals("")) || (lac == null) || (lac.equals("")))) {
            throw new IllegalArgumentException("Name or CI+LAC must be set");
        }

        // Find element by index
        Node result = null;
        Index<Node> index = graphDb.index().forNodes(indexName);

        if (!((ci == null) || (ci.equals("")))) {
            IndexHits<Node> cis = index.get(CELL_INDEX, ci);
            for (Node node : cis) {
                if (lac.equals(node.getProperty(LOCATION_AREA_CODE, null))) {
                    result = node;
                    break;
                }
            }
        }
        if (result == null) {
            result = index.get(NAME, name).getSingle();
        }
        return result;
    }

    /**
     * Looks up for a sector by the defined parameters, creates a new one if nothing was found,
     * indexes its properties and attaches it to <code>parent</code>
     * 
     * @param used only if sector was not found
     * @param name the value of NAME property
     * @param ci the value of CELL_INDEX property
     * @param lac the value of LOCATION_AREA_CODE property@param indexName
     * @return found or created sector
     */
    public Node getSector(Node parent, String indexName, String name, String ci, String lac) throws DatabaseException,
            IllegalNodeDataException {
        LOGGER.debug("start getSector(Node parent, String indexName, String name, String ci, String lac)");
        Node result = findSector(indexName, name, ci, lac);
        if (result == null) {
            result = createSector(parent, indexName, name, ci, lac);
        }
        return result;
    }

    /**
     * Traverses database to find all network elements of defined type
     * 
     * @param elementType
     * @return an <code>Iterable</code> over found nodes
     */
    public Iterable<Node> findAllNetworkElements(Node parent, INodeType elementType) {
        LOGGER.debug("start findAllNetworkElements(Node parent, INodeType elementType)");
        // validate parameters
        if (parent == null) {
            throw new IllegalArgumentException("Parent is null.");
        }
        if (elementType == null) {
            throw new IllegalArgumentException("Element type is null.");
        }

        return childElementTraversalDescription.evaluator(new FilterNodesByType(elementType)).traverse(parent).nodes();
    }
}
