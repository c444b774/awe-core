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

package org.amanzi.awe.properties.ui.views.internal;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class PropertyCellEditor extends TextCellEditor {

    private static final Logger LOGGER = Logger.getLogger(PropertyCellEditor.class);

    private Object oldValue;

    private Object castedObject;

    /**
     * @param parent
     * @param border
     */
    public PropertyCellEditor(final Composite parent, final int border) {
        super(parent, border);
    }

    @Override
    protected Object doGetValue() {
        String newValue = (String)super.doGetValue();
        if (castedObject != null && castedObject.toString().equals(newValue)) {
            return castedObject;
        }
        try {
            if (oldValue.getClass().isArray()) {
                castedObject = performArrayCasting(newValue);
            } else {
                if (oldValue.getClass().equals(Character.class)) {
                    castedObject = oldValue.getClass().cast(newValue);
                }
                castedObject = oldValue.getClass().getConstructor(newValue.getClass()).newInstance(newValue);
            }
        } catch (Exception e) {
            LOGGER.error("Error on casting value ", e);
            castedObject = newValue;
            return castedObject;
        }
        return castedObject;
    }

    @Override
    protected void doSetValue(final Object value) {
        oldValue = value;
        text.setText(value.toString());
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public boolean isValueValid() {
        return castedObject == null || !castedObject.toString().equals(doGetValue());
    }

    /**
     * @param newValue
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SecurityException
     * @throws IllegalArgumentException
     */
    private Object performArrayCasting(final String newValue) throws IllegalArgumentException, SecurityException,
            InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        String preparedString = prepareStringToConverting(newValue);
        String[] stringArray = preparedString.split(",");

        ArraysConverter converter = ArraysConverter.findByType(oldValue.getClass());

        if (converter == null) {
            LOGGER.error("can't find converter for type" + oldValue.getClass());
            return null;
        }

        return converter.convertToArray(stringArray);
    }

    /**
     * @param newValue
     * @return
     */
    private String prepareStringToConverting(String newValue) {
        newValue = newValue.replace("[", StringUtils.EMPTY);
        newValue = newValue.replace("]", StringUtils.EMPTY);
        return newValue;
    }
}
