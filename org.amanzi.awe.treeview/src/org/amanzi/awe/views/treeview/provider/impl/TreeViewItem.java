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

package org.amanzi.awe.views.treeview.provider.impl;

import org.amanzi.awe.views.treeview.provider.ITreeItem;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.IModel;

/**
 * <p>
 * storage for tree items
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class TreeViewItem<T extends IModel, E extends Object> implements ITreeItem<T, E> {

    private final T model;
    private final E element;

    /**
     * @param model
     * @param element
     */
    public TreeViewItem(T model, E element) {
        super();
        this.model = model;
        this.element = element;
    }

    @Override
    public E getChild() {
        return element;

    }

    @Override
    public T getParent() {
        return model;
    }

    @Override
    public int hashCode() {
        return element.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IDataElement) {
            IDataElement element = (IDataElement)obj;
            return this.element.equals(element);
        }
        return false;
    }

}
