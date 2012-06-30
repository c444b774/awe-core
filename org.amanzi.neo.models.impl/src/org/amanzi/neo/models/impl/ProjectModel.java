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

package org.amanzi.neo.models.impl;

import org.amanzi.neo.models.IProjectModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.nodetypes.NodeTypeUtils;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.IncorrectParentException;
import org.amanzi.neo.services.exceptions.IncorrectPropertyException;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ProjectModel extends AbstractModel implements IProjectModel {

    private static final Logger LOGGER = Logger.getLogger(ProjectModel.class);

    public enum ProjectModelNodeType implements INodeType {
        PROJECT;

        @Override
        public String getId() {
            return NodeTypeUtils.getTypeId(this);
        }
    }

    /**
     * @param nodeService
     */
    public ProjectModel(INodeService nodeService, IGeneralNodeProperties generalNodeProperties) {
        super(nodeService, generalNodeProperties);
    }

    public void initialize(String name) throws ModelException {
        assert name != null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement(INITIALIZE_METHOD_NAME, name));
        }

        try {
            Node parentNode = getNodeService().getReferencedNode();

            initialize(parentNode, name, ProjectModelNodeType.PROJECT);
        } catch (ServiceException e) {
            processException("Exception on creating new ProjectModel by name <" + name + ">.", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement(INITIALIZE_METHOD_NAME));
        }
    }

    @Override
    public void initialize(Node rootNode) throws ModelException {
        assert rootNode != null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement(INITIALIZE_METHOD_NAME, rootNode));
        }

        super.initialize(rootNode);

        // check parent
        try {
            Node referencedNode = getNodeService().getReferencedNode();

            if (!referencedNode.equals(getParentNode())) {
                throw new IncorrectParentException(rootNode, referencedNode, getParentNode());
            }

            if (!getType().equals(ProjectModelNodeType.PROJECT)) {
                throw new IncorrectPropertyException(rootNode, getGeneralNodeProperties().getNodeTypeProperty(),
                        ProjectModelNodeType.PROJECT, getType());
            }
        } catch (ServiceException e) {
            processException("Exception on initialization ProjectModel from Node <" + rootNode + ">", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement(INITIALIZE_METHOD_NAME, rootNode));
        }
    }

    @Override
    public void finishUp() throws ModelException {
    }

}
