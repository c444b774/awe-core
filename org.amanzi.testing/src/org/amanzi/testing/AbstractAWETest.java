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

package org.amanzi.testing;

import java.io.File;

import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceInitializer;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author gerzog
 * @since 1.0.0
 */
public abstract class AbstractAWETest {
    
    private static Logger LOGGER = Logger.getLogger(AbstractAWETest.class);
    
    protected static GraphDatabaseService graphDatabaseService;
    
    /**
     * Initialized Database on selected Directory
     */
    protected static void initializeDb() {
        LOGGER.info("Initialize Database");
        graphDatabaseService = new EmbeddedGraphDatabase(getDbLocation());
        NeoServiceProviderUi.initProvider(graphDatabaseService, getDbLocation());
        DatabaseManager.setDatabaseAndIndexServices(graphDatabaseService, NeoServiceProviderUi.getProvider().getIndexService());
        
        NeoServiceFactory.getInstance().clear();
        
        LOGGER.info("Database was successfully initialized");
    }
    
    protected static void initPreferences() {
        LOGGER.info("Load Preferences");
        DataLoadPreferenceInitializer initializer = new DataLoadPreferenceInitializer();
        initializer.initializeDefaultPreferences();
    }
    
    protected static String getDbLocation() {
        return System.getProperty("user.home") + File.separator + ".amanzi" + File.separator + "awe_test";
    }
    
    protected static void stopDb() {
        NeoServiceProviderUi.getProvider().getIndexService().shutdown();
        graphDatabaseService.shutdown();        
    }
    
    /**
     * Clears Database Directory
     */
    protected static void clearDb() {
       
        deleteDirectory(new File(getDbLocation()));
    }
    
    private static void deleteDirectory(File directory) {
        if (directory.exists()) {
            for (File subFile : directory.listFiles()) {
                if (subFile.isDirectory()) {
                    deleteDirectory(subFile);
                } else {
                    subFile.delete();
                }
            }
            directory.delete();
        }
    }
    
    protected  Node createNodeWithType(String typeid) {
        Node node=graphDatabaseService.createNode();
        node.setProperty("type",typeid);
        return node;
    }
    protected Relationship createRelation(Node parent, Node child, String relationName) {
        return parent.createRelationshipTo(child, DynamicRelationshipType.withName(relationName));
    }
}
