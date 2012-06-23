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

import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.services.exceptions.PropertyNotFoundException;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.internal.IService;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public interface INodeService extends IService {

    /**
     * Returns a Name property of Node
     * 
     * @param node
     * @return
     * @throws ServiceException in case of Name Property not found
     */
    public String getNodeName(Node node) throws ServiceException;

    /**
     * Returns a NodeType property of Node
     * 
     * @param node
     * @return
     * @throws ServiceException in case of Type Property not found
     */
    public INodeType getNodeType(Node node) throws ServiceException;

    /**
     * Returns a Paret of Node Parent is a Node that stand on higher hierarchy level for provided
     * node Searching for a Parent based on 'parent' property of a Child Node containing ID of
     * Parent Node
     * 
     * @param child
     * @return
     * @throws PropertyNotFoundException in case 'parent' property node found
     */
    public Node getParent(Node child) throws ServiceException;

}
