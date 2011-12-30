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
package org.amanzi.awe.models.catalog.neo;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IService;

import org.amanzi.neo.services.model.IDataModel;
import org.amanzi.neo.services.model.IMeasurementModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IRenderableModel;
import org.amanzi.neo.services.model.impl.RenderableModel.GisModel;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;

public class GeoResource extends IGeoResource {

    private static Logger LOGGER = Logger.getLogger(GeoResource.class);
    private IRenderableModel source;
    private IService service;
    private URL url;
    private GisModel gis;

    protected GeoResource(IService service, IRenderableModel source, GisModel gis) {
        // validate
        if (service == null) {
            throw new IllegalArgumentException("Geo service is null.");
        }
        if (source == null) {
            throw new IllegalArgumentException("Source is null.");
        }

        this.source = source;
        this.service = service;
        this.gis = gis;
        this.url = getURL(service, source, gis);
    }

    /**
     * form URL which will be used in renderer as identifier of gis
     * 
     * @param service
     * @param source
     * @param gis
     * @return
     */
    private URL getURL(IService service, IRenderableModel source, GisModel gis) {
        try {
            URL result = new URL(service.getIdentifier().toString() + "#" + ((IDataModel)source).getProject().getName()
                    + File.separator + gis.getName().replace(' ', '_'));

            return result;
        } catch (MalformedURLException e) {
            LOGGER.error("Could not build identifier url.", e);
        }
        return null;
    }

    @Override
    public Status getStatus() {
        return service.getStatus();
    }

    @Override
    public Throwable getMessage() {
        return service.getMessage();
    }

    @Override
    public IGeoResourceInfo createInfo(IProgressMonitor monitor) throws IOException {
        return new GeoResourceInfo(this.source, this.gis, monitor);
    }

    @Override
    public IService service(IProgressMonitor monitor) throws IOException {
        return service;
    }

    @Override
    public URL getIdentifier() {
        return url;
    }

    @Override
    public <T> boolean canResolve(Class<T> adaptee) {
        return (adaptee.isAssignableFrom(INetworkModel.class) && (source instanceof INetworkModel))
                || adaptee.isAssignableFrom(IRenderableModel.class)
                || (adaptee.isAssignableFrom(IMeasurementModel.class) && (source instanceof IMeasurementModel))
                || super.canResolve(adaptee);
    }

    @Override
    public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor) throws IOException {
        if ((adaptee.isAssignableFrom(INetworkModel.class)) || (adaptee.isAssignableFrom(IMeasurementModel.class))
                || (adaptee.isAssignableFrom(IRenderableModel.class))) {
            return adaptee.cast(source);
        }
        return super.resolve(adaptee, monitor);
    }

}
