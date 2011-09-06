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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.amanzi.log4j.LogStarter;
import org.amanzi.neo.db.manager.IDatabaseManager.AccessType;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests on DatabaseManager Test
 * @author gerzog
 * @since 1.0.0
 */
public class Neo4jDatabaseManagerTest {
    
    private final static Logger LOGGER = Logger.getLogger(Neo4jDatabaseManagerTest.class);
    
    private final static String USER_HOME = "user.home";
    
    private final static String[] TEST_DIRECTORIES = new String[] {".amanzi", "test"};
    
    private final static String DATABASE_DIRECTORY = "neo";
    
    private static String dbLocation = null;
    
    @BeforeClass
    public static void setUpTest() {
    	//initialize Log4j
    	new LogStarter().earlyStartup();
    }
   
    /**
     *
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    	clearDbLocation(new File(getTestDirectoryLocation()));
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        clearDbLocation(new File(getTestDirectoryLocation()));
    }
    
    private static String getTestDirectoryLocation() {
        if (dbLocation == null) {
            String userHome = System.getProperty(USER_HOME);
            
            File testHomeFile = new File(userHome);
            for (String subDir : TEST_DIRECTORIES) {
                testHomeFile = new File(testHomeFile, subDir);
            }
            testHomeFile.mkdirs();
            
            dbLocation = testHomeFile.getAbsolutePath();
            
            LOGGER.info("Test directory initialized on <" + dbLocation + ">");
        }
        
        return dbLocation;
    }
    
    private static void clearDbLocation(File dbLocation) {
        if (dbLocation.exists()) {
            for (File subFile : dbLocation.listFiles()) {
                if (subFile.isDirectory()) {
                    clearDbLocation(subFile);
                } else {
                    subFile.delete();
                }
            }
            dbLocation.delete();
        }
    }
    
    private String getRandomDatabaseLocation() {
    	File databaseDirectory = new File(new File(getTestDirectoryLocation()), DATABASE_DIRECTORY + new Random().nextInt());
    	databaseDirectory.mkdirs();
    	return databaseDirectory.getAbsolutePath();
    }
    
    private String getDefaultDatabaseLocation() {
    	File databaseDirectory = new File(new File(getTestDirectoryLocation()), DATABASE_DIRECTORY);
    	databaseDirectory.mkdirs();
    	return databaseDirectory.getAbsolutePath();
    }
    
    private Map<String, String> getFakeMappingParameters() {
    	HashMap<String, String> result = new HashMap<String, String>();
    	
    	result.put("some key", "some value");
    	
    	return result;
    }

    @Test
    public void checkFullConstructor() {
    	final String dbLocation = getRandomDatabaseLocation();
    	final AccessType accessType = AccessType.READ_ONLY;
    	final Map<String, String> memoryMapping = getFakeMappingParameters();
    	
        Neo4jDatabaseManager dbManager = new Neo4jDatabaseManager(dbLocation, accessType, memoryMapping);
        
        Assert.assertEquals("Incorrect Database location", dbLocation, dbManager.getLocation());
        Assert.assertEquals("Incorrect Access Type", accessType, dbManager.getAccessType());
        Assert.assertEquals("Incorrect Memory Mapping", memoryMapping, dbManager.getMemoryMapping());
    }
    
    @Test
    public void checkConstructorWithDefaultMemoryMapping() {
    	final String dbLocation = getRandomDatabaseLocation();
    	final AccessType accessType = AccessType.READ_ONLY;
    	
        Neo4jDatabaseManager dbManager = new Neo4jDatabaseManager(dbLocation, accessType);
        
        Assert.assertEquals("Incorrect Database location", dbLocation, dbManager.getLocation());
        Assert.assertEquals("Incorrect Access Type", accessType, dbManager.getAccessType());
        Assert.assertEquals("Incorrect Memory Mapping", Neo4jDatabaseManager.DEFAULT_MEMORY_MAPPING, dbManager.getMemoryMapping());
    }
    
    @Test
    public void checkConstructorWithDefaultAccessType() {
    	final String dbLocation = getRandomDatabaseLocation();
    	final Map<String, String> memoryMapping = getFakeMappingParameters();
    	
        Neo4jDatabaseManager dbManager = new Neo4jDatabaseManager(dbLocation, memoryMapping);
        
        Assert.assertEquals("Incorrect Database location", dbLocation, dbManager.getLocation());
        Assert.assertEquals("Incorrect Access Type", AccessType.READ_WRITE, dbManager.getAccessType());
        Assert.assertEquals("Incorrect Memory Mapping", memoryMapping, dbManager.getMemoryMapping());
    }
    
    @Test
    public void checkConstructorWithDefaultLocation() {
    	final AccessType accessType = AccessType.READ_ONLY;
    	final Map<String, String> memoryMapping = getFakeMappingParameters();
    	
        Neo4jDatabaseManager dbManager = new Neo4jDatabaseManager(accessType, memoryMapping);
        
        Assert.assertEquals("Incorrect Database location", getDefaultDatabaseLocation(), dbManager.getLocation());
        Assert.assertEquals("Incorrect Access Type", AccessType.READ_ONLY, dbManager.getAccessType());
        Assert.assertEquals("Incorrect Memory Mapping", memoryMapping, dbManager.getMemoryMapping());
    }
    
    @Test
    public void checkConstructorWithDefaultMemoryMappingAndAccessType() {
    	final String dbLocation = getRandomDatabaseLocation();
    	
        Neo4jDatabaseManager dbManager = new Neo4jDatabaseManager(dbLocation);
        
        Assert.assertEquals("Incorrect Database location", dbLocation, dbManager.getLocation());
        Assert.assertEquals("Incorrect Access Type", AccessType.getDefaulAccessType(), dbManager.getAccessType());
        Assert.assertEquals("Incorrect Memory Mapping", Neo4jDatabaseManager.DEFAULT_MEMORY_MAPPING, dbManager.getMemoryMapping());
    }
    
    @Test
    public void checkConstructorWithDefaultMemoryMappingAndLocation() {
    	final AccessType accessType = AccessType.READ_ONLY;
    	
        Neo4jDatabaseManager dbManager = new Neo4jDatabaseManager(accessType);
        
        Assert.assertEquals("Incorrect Database location", dbLocation, dbManager.getLocation());
        Assert.assertEquals("Incorrect Access Type", AccessType.READ_ONLY, dbManager.getAccessType());
        Assert.assertEquals("Incorrect Memory Mapping", Neo4jDatabaseManager.DEFAULT_MEMORY_MAPPING, dbManager.getMemoryMapping());
    }
    
    @Test
    public void checkConstructorWithDefaultLocationAndAccessType() {
    	final Map<String, String> memoryMapping = getFakeMappingParameters();
    	
        Neo4jDatabaseManager dbManager = new Neo4jDatabaseManager(memoryMapping);
        
        Assert.assertEquals("Incorrect Database location", getDefaultDatabaseLocation(), dbManager.getLocation());
        Assert.assertEquals("Incorrect Access Type", AccessType.READ_WRITE, dbManager.getAccessType());
        Assert.assertEquals("Incorrect Memory Mapping", memoryMapping, dbManager.getMemoryMapping());
    }
    
    @Test
    public void checkDefaultConstructor() {
    	Neo4jDatabaseManager dbManager = new Neo4jDatabaseManager();
        
        Assert.assertEquals("Incorrect Database location", getDefaultDatabaseLocation(), dbManager.getLocation());
        Assert.assertEquals("Incorrect Access Type", AccessType.READ_WRITE, dbManager.getAccessType());
        Assert.assertEquals("Incorrect Memory Mapping", Neo4jDatabaseManager.DEFAULT_MEMORY_MAPPING, dbManager.getMemoryMapping());
    }

}
