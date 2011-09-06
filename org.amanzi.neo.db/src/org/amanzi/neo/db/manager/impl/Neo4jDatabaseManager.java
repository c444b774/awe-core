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

package org.amanzi.neo.db.manager.impl;

import java.util.Map;

import org.amanzi.neo.db.manager.IDatabaseManager;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * Database manager that give access directly to Neo4j without Neoclipse layer
 * 
 * @author gerzog
 * @since 1.0.0
 */
public class Neo4jDatabaseManager implements IDatabaseManager {

    @Override
    public GraphDatabaseService getDatabaseService() {
        return null;
    }

    @Override
    public String getLocation() {
        return null;
    }

    @Override
    public Map<String, String> getMemoryMapping() {
        return null;
    }

    @Override
    public void commit() {
    }

    @Override
    public void rollback() {
    }

    @Override
    public AccessType getAccessType() {
        return null;
    }

}
