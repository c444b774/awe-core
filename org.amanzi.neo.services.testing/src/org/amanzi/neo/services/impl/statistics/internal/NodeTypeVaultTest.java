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

package org.amanzi.neo.services.impl.statistics.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.testing.AbstractMockitoTest;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NodeTypeVaultTest extends AbstractMockitoTest {

    /** int TEST_NUMBER_OF_PROPERTIES field */
    private static final int TEST_NUMBER_OF_PROPERTIES = 10;

    private static final INodeType TEST_NODE_TYPE = new INodeType() {

        @Override
        public String getId() {
            return "test";
        }
    };

    private static final String TEST_PROPERTY = "TEST";

    private static final String TEST_VALUE = "test";

    private static final String[] PROPERTY_NAMES = new String[] {"name1", "name2", "name3"};

    private NodeTypeVault nodeTypeVault;

    private PropertyVault propertyVault;

    @Before
    public void setUp() {
        propertyVault = mock(PropertyVault.class);

        nodeTypeVault = new NodeTypeVault(TEST_NODE_TYPE);
    }

    @Test
    public void testCheckCountIncreased() throws ServiceException {
        spyVault();

        int previousCount = nodeTypeVault.getCount();

        for (int i = 0; i < TEST_NUMBER_OF_PROPERTIES; i++) {
            nodeTypeVault.indexElement(getTestPropertyMap());
        }

        assertEquals("Count increased incorrect", previousCount + TEST_NUMBER_OF_PROPERTIES, nodeTypeVault.getCount());
    }

    @Test
    public void testCheckIsChanged() throws ServiceException {
        spyVault();

        nodeTypeVault.setChanged(false);

        nodeTypeVault.indexElement(getTestPropertyMap());

        assertTrue("statistics should be changed", nodeTypeVault.isChanged());
    }

    @Test
    public void testCheckActivityOnIndexProperty() throws ServiceException {
        spyVault();

        nodeTypeVault.indexElement(getTestPropertyMap());

        verify(propertyVault).index(TEST_VALUE);
    }

    @Test
    public void testCheckActivityOnGetProperties() throws ServiceException {
        nodeTypeVault.indexElement(getTestPropertyMap(PROPERTY_NAMES));

        Set<String> result = nodeTypeVault.getPropertyNames();

        ArrayList<String> resultList = new ArrayList<String>(result);
        List<String> originalList = Arrays.asList(PROPERTY_NAMES);
        Collections.sort(resultList);
        Collections.sort(originalList);

        assertEquals("Unexpected property names", originalList, resultList);
    }

    @Test
    public void testCheckGetValues() {
        spyVault();

        nodeTypeVault.getValues(TEST_PROPERTY);

        verify(propertyVault).getValues();
    }

    @Test
    public void testCheckGetValueCount() {
        spyVault();

        nodeTypeVault.getValueCount(TEST_PROPERTY, TEST_VALUE);

        verify(propertyVault).getValueCount(TEST_VALUE);
    }

    private void spyVault() {
        nodeTypeVault = spy(nodeTypeVault);
        when(nodeTypeVault.getPropertyVault(TEST_PROPERTY)).thenReturn(propertyVault);
    }

    private Map<String, Object> getTestPropertyMap() {
        Map<String, Object> result = new HashMap<String, Object>();

        result.put(TEST_PROPERTY, TEST_VALUE);

        return result;
    }

    private Map<String, Object> getTestPropertyMap(final String[] names) {
        Map<String, Object> result = new HashMap<String, Object>();

        for (String name : names) {
            result.put(name, TEST_VALUE);
        }

        return result;
    }
}
