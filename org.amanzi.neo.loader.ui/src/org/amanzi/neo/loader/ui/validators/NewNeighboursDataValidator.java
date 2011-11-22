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

package org.amanzi.neo.loader.ui.validators;

import java.io.File;
import java.util.List;

import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.IValidator;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.IProjectModel;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.apache.log4j.Logger;

/**
 * @author Kondratenko_Vladislav
 */
public class NewNeighboursDataValidator implements IValidator {
    private static final Logger LOGGER = Logger.getLogger(NewNeighboursDataValidator.class);
    private String[] possibleFieldSepRegexes = new String[] {"\t", ",", ";"};
    private Result result = Result.FAIL;
    private String message = "";

    @Override
    public Result getResult() {
        return result;
    }

    @Override
    public String getMessages() {
        return message;
    }

    @Override
    public Result isAppropriate(List<File> fileToLoad) {
        for (File f : fileToLoad) {
            result = ValidatorUtils.checkFileAndHeaders(f, 3,
                    new String[] {DataLoadPreferences.NE_SRV_CI, DataLoadPreferences.NE_NBR_CI}, possibleFieldSepRegexes)
                    .getResult();
            return result;
        }
        message = "File doesn't contain correct header";
        return Result.FAIL;
    }

    @Override
    public Result isValid(IConfiguration config) {
        if (config.getDatasetNames().get("Project") == null) {
            return Result.FAIL;
        }
        if (result == Result.SUCCESS) {
            try {
                IProjectModel projectModel = ProjectModel.getCurrentProjectModel();
                INetworkModel network = projectModel.findNetwork(config.getDatasetNames().get("Network"));
                if (network != null) {
                    result = Result.SUCCESS;
                    return result;
                }
            } catch (AWEException e) {
                LOGGER.error("Error while Sector selection data validate", e);
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }
        message = String.format("Should select some network to build selection model");
        return Result.FAIL;
    }
}
