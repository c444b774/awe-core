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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateStatisticsException;
import org.amanzi.neo.services.exceptions.InvalidPropertyStatisticsNodeException;
import org.amanzi.neo.services.exceptions.InvalidStatisticsParameterException;
import org.amanzi.neo.services.exceptions.LoadVaultException;
import org.amanzi.neo.services.statistic.IVault;
import org.amanzi.neo.services.statistic.StatisticsVault;
import org.amanzi.neo.services.statistic.internal.NewPropertyStatistics;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;

/**
 * 
 * <p>
 * service to work with statistics
 * </p>
 * 
 * @author Kruglik_A
 * @since 1.0.0
 */
public class NewStatisticsService extends NewAbstractService {
    /**
     * constants for vaultNodes properties keys
     */
    public static final String CLASS = "class";
    public static final String COUNT = "count";
    public static final String NUMBER = "number";

    private static Logger LOGGER = Logger.getLogger(NewAbstractService.class);
    private Transaction tx;
    /**
     * TraversalDescription for child nodes
     */
    public TraversalDescription childNodes = Traversal.description()
            .relationships(StatisticsRelationships.CHILD, Direction.OUTGOING).evaluator(Evaluators.atDepth(1));

    /**
     * <p>
     * Relationship types for statistics nodes
     * </p>
     * 
     * @author kruglik_a
     * @since 1.0.0
     */
    public enum StatisticsRelationships implements RelationshipType {
        STATISTICS, CHILD;
    }

    /**
     * <p>
     * Node types for statistics nodes
     * </p>
     * 
     * @author kruglik_a
     * @since 1.0.0
     */
    public enum StatisticsNodeTypes implements INodeType {
        VAULT, PROPERTY_STATISTICS;

        @Override
        public String getId() {
            return name();
        }
    }

    /**
     * This method recursively loads subVaults
     * 
     * @param vaultNode - node to which the attached statistics vault
     * @return IVault vault for vault node
     * @throws LoadVaultException - this method may generate exception if vault node has wrong
     *         className
     */
    private IVault loadSubVault(Node vaultNode) throws LoadVaultException {
        IVault result;
        String className = (String)vaultNode.getProperty(CLASS, null);
        try {
            @SuppressWarnings("unchecked")
            Class<IVault> klass = (Class<IVault>)Class.forName(className);
            result = klass.newInstance();

            result.setCount((Integer)vaultNode.getProperty(COUNT, null));
            result.setType((String)vaultNode.getProperty(NAME, ""));
            for (Node propStatNode : getPropertyStatisticsNodes(vaultNode)){
                result.addPropertyStatistics(loadPropertyStatistics(propStatNode));
            }
            for (Node subVauldNode : getSubVaultNodes(vaultNode)) {
                result.addSubVault(loadSubVault(subVauldNode));
            }
        } catch (Exception e) {
            throw new LoadVaultException(e);
        }
        return result;

    }

    /**
     * this method get all subVault nodes for parent vaultNode
     * 
     * @param parentVaultNode
     * @return Iterable<Node> subVaultNodes
     */
    public Iterable<Node> getSubVaultNodes(Node parentVaultNode) {
        return childNodes.evaluator(new FilterNodesByType(StatisticsNodeTypes.VAULT)).traverse(parentVaultNode).nodes();
    }

    /**
     * this method get all propertyStatistics nodes for parent vaultNode
     * 
     * @param parentVaultNode
     * @return Iterable<Node> propertyStatisticsNodes
     */
    public Iterable<Node> getPropertyStatisticsNodes(Node parentVaultNode) {
        return childNodes.evaluator(new FilterNodesByType(StatisticsNodeTypes.PROPERTY_STATISTICS)).traverse(parentVaultNode)
                .nodes();
    }

    /**
     * this method save vault to database
     * 
     * @param rootNode - node to which the attached statistics vault
     * @param vault - vault of statistics
     * @throws DatabaseException - this method may generate exception if exception occurred while
     *         working with a database
     * @throws InvalidStatisticsParameterException - this method may generate exception if some
     *         parameter is null
     * @throws DuplicateStatisticsException - this method may generate exception if rootNode already
     *         has a statistics
     */
    public void saveVault(Node rootNode, IVault vault) throws DatabaseException, InvalidStatisticsParameterException,
            DuplicateStatisticsException {
        LOGGER.debug("start method saveVault(Node rootNode, IVault vault)");
        if (rootNode == null) {
            LOGGER.error("InvalidStatisticsParameterException: parameter rootNode = null");
            throw new InvalidStatisticsParameterException("rootNode", rootNode);
        }
        if (vault == null) {
            LOGGER.error("InvalidStatisticsParameterException: parameter vault = null");
            throw new InvalidStatisticsParameterException("vault", vault);
        }
        if (rootNode.getRelationships(StatisticsRelationships.STATISTICS, Direction.OUTGOING).iterator().hasNext()) {
            LOGGER.error("DuplicateStatisticsException: for this rootNode already exists statistics");
            throw new DuplicateStatisticsException("for this rootNode already exists statistics");
        }
        Node vaultNode = createNode(StatisticsNodeTypes.VAULT);

        tx = graphDb.beginTx();

        try {
            if (StatisticsNodeTypes.VAULT.getId().equals(getNodeType(rootNode))) {
                rootNode.createRelationshipTo(vaultNode, StatisticsRelationships.CHILD);
            } else {
                rootNode.createRelationshipTo(vaultNode, StatisticsRelationships.STATISTICS);
            }
            vaultNode.setProperty(NAME, vault.getType());
            vaultNode.setProperty(COUNT, vault.getCount());
            vaultNode.setProperty(CLASS, vault.getClass().getCanonicalName());
            for (NewPropertyStatistics propStat : vault.getPropertyStatisticsList()) {
                savePropertyStatistics(propStat, vaultNode);
            }
            tx.success();
        } catch (Exception e) {
            LOGGER.error("Could not create vault node in database", e);
            tx.failure();
            throw new DatabaseException(e);

        } finally {
            tx.finish();

        }

        for (IVault subVault : vault.getSubVaults()) {
            saveVault(vaultNode, subVault);
        }
        LOGGER.debug("finish method saveVault(Node rootNode, IVault vault)");
    }

    /**
     * this method load vault from database
     * 
     * @param rootNode - node to which the attached statistics
     * @return
     * @throws InvalidStatisticsParameterException - this method may generate exception if rootNode
     *         parameter is null
     * @throws LoadVaultException - this method may generate exception if vault node has wrong
     *         className
     */
    public IVault loadVault(Node rootNode) throws InvalidStatisticsParameterException, LoadVaultException {
        LOGGER.debug("start method loadVault(Node rootNode)");
        if (rootNode == null) {
            throw new InvalidStatisticsParameterException("rootNode", rootNode);
        }

        IVault result;
        if (!rootNode.hasRelationship(StatisticsRelationships.STATISTICS, Direction.OUTGOING)) {
            return new StatisticsVault();
        }
        Node vaultNode = rootNode.getSingleRelationship(StatisticsRelationships.STATISTICS, Direction.OUTGOING).getEndNode();
        try {
            result = loadSubVault(vaultNode);
        } catch (LoadVaultException e) {
            LOGGER.error("LoadVaultException: problems to create IVault");
            throw e;
        }
        LOGGER.debug("finish method loadVault(Node rootNode)");
        return result;
    }

    /**
     * this method create propertyStatistics node in database by propertyStatistics object
     * 
     * @param propStat - propertyStatistics object
     * @param vaultNode - parent vault node
     * @throws DatabaseException - this method may generate exception if exception occurred while
     *         working with a database
     * @throws InvalidStatisticsParameterException- this method may generate exception if some
     *         parameter is null
     */
    public void savePropertyStatistics(NewPropertyStatistics propStat, Node vaultNode) throws DatabaseException,
            InvalidStatisticsParameterException {

        if (propStat == null) {
            throw new InvalidStatisticsParameterException("propStat", propStat);
        }
        if (vaultNode == null) {
            throw new InvalidStatisticsParameterException("vaultNode", vaultNode);
        }

        String name = propStat.getName();
        Map<Object, Integer> propMap = propStat.getPropertyMap();
        int number = propMap.size();
        String className = propStat.getKlass().getCanonicalName();
        Transaction tx = graphDb.beginTx();
        try {
            Node propStatNode = createNode(StatisticsNodeTypes.PROPERTY_STATISTICS);
            vaultNode.createRelationshipTo(propStatNode, StatisticsRelationships.CHILD);
            propStatNode.setProperty(NAME, name);
            propStatNode.setProperty(NUMBER, number);
            propStatNode.setProperty(CLASS, className);
            Iterator<Entry<Object, Integer>> iter = propMap.entrySet().iterator();
            String valueName = "v";
            String countName = "c";
            int count = 0;
            while (iter.hasNext()) {
                count++;
                Entry<Object, Integer> entry = iter.next();
                propStatNode.setProperty(valueName + count, entry.getKey());
                propStatNode.setProperty(countName + count, entry.getValue());
            }
            tx.success();

        } catch (Exception e) {
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }

    }

    /**
     * this method load PropertyStatistics object by propertyStatisticsNode
     * 
     * @param propertyStatisticsNode - this node save propertyStatistics information
     * @return NewPropertyStatistics object
     * @throws InvalidPropertyStatisticsNodeException
     * @throws LoadVaultException - this method may generate exception if propertyStatistics node
     *         has wrong className
     * @throws InvalidStatisticsParameterException - this method may generate exception if
     *         propertyStatisticsNode parameter is null
     */
    public NewPropertyStatistics loadPropertyStatistics(Node propertyStatisticsNode) throws InvalidPropertyStatisticsNodeException,
            LoadVaultException, InvalidStatisticsParameterException {
        if (propertyStatisticsNode == null) {
            throw new InvalidStatisticsParameterException("propertyStatisticsNode", propertyStatisticsNode);
        }
        if (((String)propertyStatisticsNode.getProperty(NAME, "")).isEmpty()) {
            throw new InvalidPropertyStatisticsNodeException(NAME);
        }
        if (!propertyStatisticsNode.hasProperty(CLASS)) {
            throw new InvalidPropertyStatisticsNodeException(CLASS);
        }
        NewPropertyStatistics result = null;
        try {
            String name = (String)propertyStatisticsNode.getProperty(NAME);
            Class< ? > klass = Class.forName((String)propertyStatisticsNode.getProperty(CLASS));
            result = new NewPropertyStatistics(name, klass);
            Integer number = (Integer)propertyStatisticsNode.getProperty(NUMBER, 0);

            for (int i = 1; i <= number; i++) {
                Object value = propertyStatisticsNode.getProperty("v" + i);
                Integer count = (Integer)propertyStatisticsNode.getProperty("c" + i);
                result.updatePropertyMap(value, count);
            }

        } catch (Exception e) {
            throw new LoadVaultException(e);
        }

        return result;
    }

}
