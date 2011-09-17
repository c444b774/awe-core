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

package org.amanzi.neo.services.statistic.internal;

import java.util.Map;
import java.util.TreeMap;

/** 
 * <p>
 * this class stores the statistics for a particular property
 * </p>
 * @author kruglik_a
 * @since 1.0.0
 */
public class NewPropertyStatistics {
    
    /**
     * name of property
     */
    private String name;
    
    /**
     * class of property values
     */
    private Class<?> klass;
    
    /**
     * map, which makes the correspondence between the value of the property and the number of such values
     */
    private Map <Object, Integer> propertyMap = new TreeMap<Object, Integer>();
    
    /**
     * constructor with parameter name
     * @param name - name of the property, statistics which will be stored in this object
     */
    public NewPropertyStatistics(String name, Class<?> klass){
        this.name = name;
        this.klass = klass;
    }
    
    /**
     * get name of property
     *
     * @return String name
     */
    public String getName(){
        return this.name;
    }
    
    /**
     * get Class of property
     *
     * @return Class<?> klass
     */
    public Class<?> getKlass(){
        return this.klass;
    }
    
    /**
     * set Class of property
     *
     * @param klass - Class of property
     */
    public void setKlass(Class<?> klass){
        this.klass = klass;
    }
    
    /**
     * this method add property value with it count or update count if this value is already contained in the map
     *
     * @param value - property value
     * @param count - count to update for this value
     */
    public void updatePropertyMap(Object value, Integer count){
        Integer oldCount = 0;
        if (propertyMap.containsKey(value)){
            oldCount = propertyMap.get(value);
        }
        propertyMap.put(value, oldCount+count);
    }
    
    /**
     * this method get propertyMap
     *
     * @return  Map<Object, Integer> propertyMap
     */
    public Map<Object, Integer> getPropertyMap(){
        return this.propertyMap;
    }

}
