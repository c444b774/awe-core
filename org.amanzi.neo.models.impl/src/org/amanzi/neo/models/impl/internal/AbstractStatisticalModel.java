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

package org.amanzi.neo.models.impl.internal;

import java.util.Set;

import org.amanzi.neo.models.IPropertyStatisticalModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.INodeService;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class AbstractStatisticalModel extends AbstractDataModel implements IPropertyStatisticalModel {

    /**
     * @param nodeService
     * @param generalNodeProperties
     */
    public AbstractStatisticalModel(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties) {
        super(nodeService, generalNodeProperties);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void finishUp() throws ModelException {
        // TODO Auto-generated method stub

    }

    @Override
    public void indexProperty(final INodeType nodeType, final String property, final Object value) {
        // TODO Auto-generated method stub

    }

    @Override
    public Set<String> getPropertyNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> getPropertyNames(final INodeType nodeType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getCount(final INodeType nodeType) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Set<Object> getValues(final INodeType nodeType, final String property) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getPropertyCount(final INodeType nodeType, final String property, final Object value) {
        // TODO Auto-generated method stub
        return 0;
    }

}
