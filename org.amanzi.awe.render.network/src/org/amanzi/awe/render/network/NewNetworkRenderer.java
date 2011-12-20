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

package org.amanzi.awe.render.network;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.io.IOException;

import net.refractions.udig.project.ILayer;

import org.amanzi.awe.neostyle.NetworkNeoStyle;
import org.amanzi.awe.neostyle.NetworkNeoStyleContent;
import org.amanzi.awe.render.core.AbstractRenderer;
import org.amanzi.awe.render.core.Scale;
import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IRenderableModel;
import org.apache.commons.lang.ObjectUtils;
import org.eclipse.core.runtime.IProgressMonitor;

import com.vividsolutions.jts.geom.Envelope;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class NewNetworkRenderer extends AbstractRenderer {

    // TODO: find a better place for the constants
    public static final String AZIMUTH = "azimuth";
    public static final String BEAMWIDTH = "beam";
    private static final double FULL_CIRCLE = 360.0;

    @Override
    protected void renderElement(Graphics2D destination, Point point, IDataElement site, IRenderableModel model) {
        INodeType type = NodeTypeManager.getType(site.get(AbstractService.TYPE).toString());
        if (!NetworkElementNodeType.SITE.equals(type)) {
            throw new IllegalArgumentException("Could not render element of type " + type.getId());
        }

        renderSite(destination, point, site);
        if (RenderOptions.scale == Scale.LARGE) {
            int i = 0;
            Integer sCount = (Integer)site.get(NetworkService.SECTOR_COUNT);
            for (IDataElement sector : ((INetworkModel)model).getChildren(site)) {
                Double azimuth = (Double)sector.get(AZIMUTH);
                Double beamwidth = (Double)sector.get(BEAMWIDTH);
                if (azimuth == null || beamwidth == null || beamwidth == 0) {
                    beamwidth = FULL_CIRCLE / (sCount == null ? 1 : sCount);
                    azimuth = beamwidth * i;
                    beamwidth = beamwidth.doubleValue() * 0.8;
                }
                renderSector(destination, point, azimuth, beamwidth);
                i++;
            }
        }
    }

    /**
     * @param destination
     * @param point
     * @param element
     */
    private void renderSite(Graphics2D destination, Point point, IDataElement element) {
        int size = 2;
        switch (RenderOptions.scale) {
        case SMALL:
            destination.setColor(RenderOptions.border);
            destination.drawRect(point.x - size / 2, point.y - size / 2, size, size);
            break;
        case MEDIUM:
            size = RenderOptions.siteSize;
            destination.setColor(RenderOptions.border);
            destination.drawOval(point.x - size / 2, point.y - size / 2, size, size);
            break;
        case LARGE:
            size = RenderOptions.largeSectorsSize / 3;
            destination.setColor(RenderOptions.border);
            destination.drawOval(point.x - size / 2, point.y - size / 2, size, size);
            destination.setColor(RenderOptions.siteFill);
            destination.fillOval(point.x - size / 2, point.y - size / 2, size, size);
        }

    }

    /**
     * @param destination
     * @param point
     * @param element
     */
    private void renderSector(Graphics2D destination, Point point, double azimuth, double beamwidth) {
        switch (RenderOptions.scale) {
        case LARGE:

            double angle1 = 90 - azimuth - beamwidth / 2.0;
            double angle2 = angle1 + beamwidth;

            GeneralPath path = new GeneralPath();
            path.moveTo(point.x, point.y);
            Arc2D a = new Arc2D.Double();
            a.setArcByCenter(point.x, point.y, RenderOptions.largeSectorsSize, angle2, beamwidth, Arc2D.OPEN);
            path.append(a.getPathIterator(null), true);
            path.closePath();

            destination.setColor(RenderOptions.border);
            destination.draw(path);
            destination.setColor(RenderOptions.sectorFill);
            destination.fill(path);

            break;
        }
    }

    @Override
    protected void setStyle(Graphics2D destination) {
        super.setStyle(destination);

        NetworkNeoStyle newStyle = (NetworkNeoStyle)getContext().getLayer().getStyleBlackboard().get(NetworkNeoStyleContent.ID);
        if (ObjectUtils.equals(style, newStyle)) {
            return;
        }
        style = newStyle;
        RenderOptions.alpha = 255 - (int)((double)newStyle.getSymbolTransparency() / 100.0 * 255.0);
        RenderOptions.border = changeColor(newStyle.getLine(), RenderOptions.alpha);
        RenderOptions.largeSectorsSize = newStyle.getSymbolSize();
        RenderOptions.sectorFill = changeColor(newStyle.getFill(), RenderOptions.alpha);
        RenderOptions.siteFill = changeColor(newStyle.getSiteFill(), RenderOptions.alpha);

        RenderOptions.maxSitesFull = newStyle.getSmallSymb();
        RenderOptions.maxSitesLabel = newStyle.getLabeling();
        RenderOptions.maxSitesLite = newStyle.getSmallestSymb();
        RenderOptions.maxSymbolSize = newStyle.getMaximumSymbolSize();

    }

    @Override
    protected double getAverageDensity(IProgressMonitor monitor) {
        double result = 0;
        long count = 0;
        try {
            for (ILayer layer : getContext().getMap().getMapLayers()) {
                if (layer.getGeoResource().canResolve(INetworkModel.class)) {
                    INetworkModel resource = layer.getGeoResource().resolve(INetworkModel.class, monitor);
                    Envelope dbounds = resource.getBounds();
                    if (dbounds != null) {
                        result += (resource.getNodeCount(NetworkElementNodeType.SITE) / 2)
                                / (dbounds.getHeight() * dbounds.getWidth());
                        count++;
                    }
                }
            }
        } catch (IOException e) {
            // TODO Handle IOException
            // TODO: LN: error in Log and Console
            // NeoCorePlugin.error(e.getLocalizedMessage(), e);
            return 0;
        }
        return result / (double)count;
    }
}
