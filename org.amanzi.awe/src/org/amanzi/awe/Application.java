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
package org.amanzi.awe;

import net.refractions.udig.internal.ui.UDIGApplication;
import net.refractions.udig.internal.ui.UDIGWorkbenchAdvisor;

import org.amanzi.awe.ui.manager.AWEEventManager;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * This is the default application for the Amanzi Wireless Explorer. It is based directly on uDIG,
 * and uses its advisor.
 * 
 * @since AWE 1.0.0
 * @author craig
 */
public class Application extends UDIGApplication {
    private class AWEWorkbenchAdvivsor extends UDIGWorkbenchAdvisor {

        @Override
        public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer) {
            return super.createWorkbenchWindowAdvisor(configurer);
        }

        @Override
        public String getInitialWindowPerspectiveId() {
            return PerspectiveFactory.AWE_PERSPECTIVE;
        }

        @Override
        public void initialize(final IWorkbenchConfigurer configurer) {
            super.initialize(configurer);

            AWEEventManager.getManager().fireInitialiseEvent();
            configurer.setSaveAndRestore(true);
        }

        @Override
        public void postStartup() {
            AWEEventManager.getManager().fireAWEStartedEvent();
            super.postStartup();
        }

        @Override
        public boolean preShutdown() {
            AWEEventManager.getManager().fireAWEStoppedEvent();
            return super.preShutdown();
        }
    }

    /**
     * /** Create the AWE workbench advisor by using the UDIGWorkbenchAdvisor with only the
     * perspective changed to match the AWE requirements.
     * 
     * @see net.refractions.udig.internal.ui.UDIGApplication#createWorkbenchAdvisor()
     */
    @Override
    protected WorkbenchAdvisor createWorkbenchAdvisor() {
        return new AWEWorkbenchAdvivsor();
    }

}
