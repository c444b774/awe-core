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

package org.amanzi.neo.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.IIndexService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.impl.indexes.MultiPropertyIndex;
import org.amanzi.neo.services.impl.indexes.MultiPropertyIndex.MultiDoubleConverter;
import org.amanzi.neo.services.impl.indexes.MultiPropertyIndex.MultiTimeIndexConverter;
import org.amanzi.neo.services.impl.indexes.MultiPropertyIndex.MultiValueConverter;
import org.amanzi.neo.services.impl.internal.AbstractService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class IndexService extends AbstractService implements IIndexService {

    /** int DEFAULT_MULTIPROPERTY_INDEX_STEP field */
    private static final int DEFAULT_MULTIPROPERTY_INDEX_STEP = 10;

    /** double DEFAULT_CLUSTER_SIZE field */
    private static final double DEFAULT_MULTIPROPERTY_CLUSTER_SIZE = 0.001;

    private static final String INDEX_SEPARATOR = "@";

    private static final String PROPERTY_SEPARATOR = "|";

    private final Map<String, Index<Node>> nodeIndexMap = new HashMap<String, Index<Node>>();

    /**
     * @param graphDb
     * @param generalNodeProperties
     */
    public IndexService(final GraphDatabaseService graphDb) {
        super(graphDb, null);
    }

    @Override
    public Index<Node> getIndex(final Node rootNode, final INodeType nodeType) throws ServiceException {
        assert rootNode != null;
        assert nodeType != null;

        final String key = getIndexKey(rootNode, nodeType);

        Index<Node> result = nodeIndexMap.get(key);

        if (result == null) {
            result = createNodeIndex(key);

            nodeIndexMap.put(key, result);
        }

        return result;
    }

    protected Index<Node> createNodeIndex(final String key) throws ServiceException {
        Index<Node> result = null;

        final Transaction tx = getGraphDb().beginTx();
        try {
            result = getGraphDb().index().forNodes(key);
            tx.success();
        } catch (final Exception e) {
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }

        return result;
    }

    protected String getIndexKey(final Node rootNode, final INodeType nodeType, final String... properties) {
        final StringBuilder builder = new StringBuilder().append(rootNode.getId()).append(INDEX_SEPARATOR).append(nodeType.getId());

        for (final String property : properties) {
            builder.append(PROPERTY_SEPARATOR).append(property);
        }

        return builder.toString();
    }

    @Override
    public void addToIndex(final Node rootNode, final INodeType nodeType, final Node node, final String propertyName,
            final Object value) throws ServiceException {
        assert rootNode != null;
        assert nodeType != null;
        assert !StringUtils.isEmpty(propertyName);
        assert value != null;

        final Transaction tx = getGraphDb().beginTx();
        try {
            final Index<Node> index = getIndex(rootNode, nodeType);
            index.add(node, propertyName, value);
            tx.success();
        } catch (final Exception e) {
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> MultiPropertyIndex<T> createMultiPropertyIndex(final INodeType nodeType, final Node node, final Class<T> clazz,
            final String... properties) throws ServiceException {
        assert clazz != null;
        assert properties != null;
        assert !ArrayUtils.contains(properties, null);

        MultiPropertyIndex<T> result = null;

        final Transaction tx = getGraphDb().beginTx();
        try {
            final String key = getIndexKey(node, nodeType, properties);

            MultiValueConverter<T> converter = null;

            if (clazz == Double.class) {
                converter = (MultiValueConverter<T>)new MultiDoubleConverter(DEFAULT_MULTIPROPERTY_CLUSTER_SIZE);
            } else if (clazz == Long.class) {
                converter = (MultiValueConverter<T>)new MultiTimeIndexConverter();
            }

            result = new MultiPropertyIndex<T>(key, properties, converter, DEFAULT_MULTIPROPERTY_INDEX_STEP);
            result.initialize(getGraphDb(), node);

            tx.success();
        } catch (final Exception e) {
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }

        return result;
    }

    @Override
    public void deleteFromIndexes(final Node rootNode, final Node node, final INodeType nodeType) throws DatabaseException {
        final String key = getIndexKey(rootNode, nodeType);
        final Index<Node> result = nodeIndexMap.get(key);
        final Transaction tx = getGraphDb().beginTx();
        try {
            if (result != null) {
                result.remove(node);
            }
            tx.success();
        } catch (final Exception e) {
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }
    }

    @Override
    public void deleteAll(Node rootNode) throws DatabaseException {
        final Transaction tx = getGraphDb().beginTx();
        try {
            String indexMatchPattern = rootNode.getId() + INDEX_SEPARATOR + ".*";
            List<String> deletableIndexes = new ArrayList<String>();
            for (Entry<String, Index<Node>> singleIndex : nodeIndexMap.entrySet()) {
                if (singleIndex.getKey().matches(indexMatchPattern)) {
                    singleIndex.getValue().delete();
                    deletableIndexes.add(singleIndex.getKey());
                }
            }
            for (String deletableKey : deletableIndexes) {
                nodeIndexMap.remove(deletableKey);
            }
            tx.success();
        } catch (final Exception e) {
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }

    }

    @Override
    public void updateIndex(Node rootNode, INodeType nodeType, Node node, String propertyName, Object oldValue, Object newValue) throws DatabaseException {
        assert rootNode != null;
        assert nodeType != null;
        assert !StringUtils.isEmpty(propertyName);
        assert oldValue != null;
        assert newValue != null;

        final Transaction tx = getGraphDb().beginTx();
        try {
            final Index<Node> index = getIndex(rootNode, nodeType);
            index.remove(node, propertyName, oldValue);
            index.add(node, propertyName, newValue);
            tx.success();
        } catch (final Exception e) {
            tx.failure();
            throw new DatabaseException(e);
        } finally {
            tx.finish();
        }

    }
}
