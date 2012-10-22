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

package org.amanzi.awe.views.properties.views.internal;

import java.util.HashSet;
import java.util.Set;

import org.amanzi.awe.views.properties.AWEPropertiesPlugin;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DataElementPropertyDescriptor extends PropertyDescriptor {

    private enum Category {
        PROPERTY("Properties"), HEADER("General info");

        private String title;

        private Category(final String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    protected final static String ID_PROPERTY = "id";

    private static final IGeneralNodeProperties GENERAL_NODE_PROPERTIES = AWEPropertiesPlugin.getDefault()
            .getGeneralNodeProperties();

    private static final Set<String> UNEDITABLE_PROPERTIES = new HashSet<String>();

    static {
        UNEDITABLE_PROPERTIES.add(GENERAL_NODE_PROPERTIES.getNodeTypeProperty());
        UNEDITABLE_PROPERTIES.add(GENERAL_NODE_PROPERTIES.getLastChildID());
        UNEDITABLE_PROPERTIES.add(GENERAL_NODE_PROPERTIES.getParentIDProperty());
        UNEDITABLE_PROPERTIES.add(GENERAL_NODE_PROPERTIES.getSizeProperty());
        UNEDITABLE_PROPERTIES.add(ID_PROPERTY);
    }

    private static final IGeneralNodeProperties generalNodeProperties;

    static {
        generalNodeProperties = AWEPropertiesPlugin.getDefault().getGeneralNodeProperties();
    }

    /**
     * @param id
     * @param displayName
     */
    public DataElementPropertyDescriptor(final String propertyName) {
        super(propertyName, propertyName);
        setCategory(calculateCategory(propertyName).getTitle());
    }

    private Category calculateCategory(final String propertyName) {
        if (propertyName.equals(generalNodeProperties.getNodeTypeProperty()) || propertyName.equals(ID_PROPERTY)) {
            return Category.HEADER;
        }

        return Category.PROPERTY;
    }

    @Override
    public CellEditor createPropertyEditor(Composite parent) {
        if (UNEDITABLE_PROPERTIES.contains(getId())) {
            return null;
        } else {
            return new PropertyCellEditor(parent, SWT.BORDER);
        }
    }
}
