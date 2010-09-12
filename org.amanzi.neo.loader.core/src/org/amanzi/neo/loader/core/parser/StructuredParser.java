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

package org.amanzi.neo.loader.core.parser;

import java.util.List;

import org.amanzi.neo.loader.core.IProgressEvent;
import org.amanzi.neo.loader.core.ProgressEventImpl;
import org.amanzi.neo.loader.core.parser.StructuredParser.IStructuredElement;

/**
 * <p>
 * Parser for structured data
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public abstract class StructuredParser<S extends IStructuredElement, T extends IDataElement, C extends IConfigurationData> extends AbstractParser<T, C> {
    private double percentage;
    private long totalLen;

    @Override
    public void parce() {
        percentage = 0;
        List<S> elementList = getElementList();
        totalLen = getTotalLength(elementList);
        T initData = getInitData(getProperties());
        getSaver().init(initData);
        try {
            for (S element : elementList) {
                ProgressEventImpl event = new ProgressEventImpl(element.getDescription(), percentage);
                if (fireProgressEvent(event)){
                    return;
                }
                if (parseElement(element)){
                    return;
                }
                percentage += (double)element.getSize() / totalLen;
                ProgressEventImpl event2 = new ProgressEventImpl(element.getDescription(), percentage);
                if (fireProgressEvent(event2)){
                    return;
                }
            }
        } finally {
            getSaver().finishUp(getFinishData());
        }
    }

    /**
     * Fire sub progress event.
     * 
     * @param element the element
     * @param event the event
     */
    protected boolean fireSubProgressEvent(S element, final IProgressEvent event) {
       return fireProgressEvent(new ProgressEventImpl(event.getProcessName(), percentage +  (double)event.getPercentage()*element.getSize()/totalLen));
    }

    /**
     * Gets the percentage.
     * 
     * @return the percentage
     */
    public double getPercentage() {
        return percentage;
    }

    /**
     * Gets the total length.
     * 
     * @param elementList the element list
     * @return the total length
     */
    protected  long getTotalLength(List<S> elementList){
        long totalLen=0;
        for (S element:elementList){
            totalLen+=element.getSize();
        }
        return totalLen;
    }

    /**
     * Gets the finish data.
     * 
     * @return the finish data
     */
    protected abstract T getFinishData();

    /**
     * Parses the element.
     * 
     * @param element the element
     */
    protected abstract boolean parseElement(S element);

    /**
     * Gets the inits the data.
     * 
     * @param properties the properties
     * @return the inits the data
     */
    protected abstract T getInitData(C properties);

    /**
     * Gets the element list.
     * 
     * @return the element list
     */
    protected abstract List<S> getElementList();

    /**
     * <p>
     * Interface for one input element
     * </p>
     * 
     * @author TsAr
     * @since 1.0.0
     */
    public static interface IStructuredElement {

        /**
         * Gets the size.
         * 
         * @return the size
         */
        public long getSize();

        /**
         * Gets the description.
         * 
         * @return the description
         */
        public String getDescription();
    }
}