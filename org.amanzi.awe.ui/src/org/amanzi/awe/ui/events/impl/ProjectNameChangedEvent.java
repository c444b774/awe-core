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

package org.amanzi.awe.ui.events.impl;

import org.amanzi.awe.ui.events.EventStatus;
import org.amanzi.awe.ui.events.impl.internal.AbstractEvent;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ProjectNameChangedEvent extends AbstractEvent {

    private final String newProjectName;

    /**
     * @param status
     */
    public ProjectNameChangedEvent(final String newProjectName, Object source) {
        super(EventStatus.PROJECT_CHANGED, true, source);

        this.newProjectName = newProjectName;
    }

    /**
     * @return Returns the newProjectName.
     */
    public String getNewProjectName() {
        return newProjectName;
    }

}
