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

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.exceptions.ServiceException;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NodeTypeVault {

    private int count;

    private final INodeType nodeType;

    private boolean isChanged;

    private final Map<String, PropertyVault> propertyVaults = new TreeMap<String, PropertyVault>();

    public NodeTypeVault(final INodeType nodeType) {
        this.nodeType = nodeType;

        isChanged = false;
    }

    public int getCount() {
        return count;
    }

    public void indexElement(final Map<String, Object> properties) throws ServiceException {
        for (Entry<String, Object> propertyEntry : properties.entrySet()) {
            getPropertyVault(propertyEntry.getKey()).index(propertyEntry.getValue());
        }

        count++;
        isChanged = true;
    }

    public Set<String> getPropertyNames() {
        return propertyVaults.keySet();
    }

    public Set<Object> getValues(final String property) {
        return getPropertyVault(property).getValues();
    }

    public int getValueCount(final String property, final Object value) {
        return getPropertyVault(property).getValueCount(value);
    }

    protected PropertyVault getPropertyVault(final String property) {
        PropertyVault result = propertyVaults.get(property);

        if (result == null) {
            result = new PropertyVault(property);
            propertyVaults.put(property, result);
        }

        return result;
    }

    public Collection<PropertyVault> getAllPropertyVaults() {
        return propertyVaults.values();
    }

    public INodeType getNodeType() {
        return nodeType;
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(final boolean isChanged) {
        this.isChanged = isChanged;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    public void addPropertyVault(final PropertyVault propertyVault) {
        propertyVaults.put(propertyVault.getPropertyName(), propertyVault);
    }

    public Class< ? > getPropertyClass(final String propertyName) {
        PropertyVault propertyVault = propertyVaults.get(propertyName);

        if (propertyVault != null) {
            return propertyVault.getClassType();
        }

        return null;
    }

    /**
     * @param properties
     */
    public void updateDefaultValues(Map<String, Object> properties) {
        for (Entry<String, Object> entry : properties.entrySet()) {
            PropertyVault propertyVault = getPropertyVault(entry.getKey());
            propertyVault.setDefaultValue(entry.getValue());
        }
        isChanged = !properties.isEmpty();
    }

    /**
     * @param property
     * @return
     */
    public Object getDefaultValue(String property) {
        return getPropertyVault(property).getDefaultValue();
    }

    /**
     * @param propertyName
     * @param oldValue
     * @param newValue
     */
    public void renameProperty(String propertyName, Object oldValue, Object newValue) {
        getPropertyVault(propertyName).renameProperty(oldValue, newValue);

    }

    /**
     * @param properties
     */
    public void deleteProperties(Map<String, Object> properties) {
        for (Entry<String, Object> property : properties.entrySet()) {
            PropertyVault result = propertyVaults.get(property.getKey());
            if (result != null) {
                result.removeProperty(property.getValue());
            }
        }

    }
}
