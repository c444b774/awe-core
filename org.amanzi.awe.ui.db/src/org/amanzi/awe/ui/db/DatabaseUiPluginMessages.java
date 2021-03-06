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
package org.amanzi.awe.ui.db;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Messages for Network Tree
 */

public class DatabaseUiPluginMessages extends NLS {

    private static final String BUNDLE_NAME = DatabaseUiPluginMessages.class.getName();
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
    public static String warningDialogMessage;
    public static String warningDialogName;
    public static String warningDialogChooseDatabaseLabel;

    private DatabaseUiPluginMessages() {
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public static String getFormattedString(String key, String... args) {
        return MessageFormat.format(key, (Object[])args);
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, DatabaseUiPluginMessages.class);
    }

}
