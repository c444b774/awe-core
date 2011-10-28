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

package org.amanzi.neo.loader.core.newparser;

import java.util.LinkedList;
import java.util.List;

import org.amanzi.neo.loader.core.newsaver.IData;

/**
 * common row container contains temporary <b>HEADERS</b> and <b>ROW</b> information
 * 
 * @author Kondratenko_Vladislav
 */
public class CSVContainer implements IData {
    /**
     * contain rows values;
     */
    private List<String> row = new LinkedList<String>();
    /**
     * contain header values;
     */
    private List<String> headers;

    public CSVContainer(int minimalLength) {
        super();
    }

    /**
     * @return Returns the row.
     */
    public List<String> getValues() {
        return row;
    }

    /**
     * @param row The row to set.
     */
    public void setValues(List<String> row) {
        
        this.row = row;
    }

    /**
     * @return Returns the header.
     */
    public List<String> getHeaders() {
        return headers;
    }

    /**
     * @param header The header to set.
     */
    public void setHeaders(List<String> header) {
        this.headers = header;
    }
}
