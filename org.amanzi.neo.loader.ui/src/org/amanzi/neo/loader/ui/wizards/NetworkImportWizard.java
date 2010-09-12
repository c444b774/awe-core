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

package org.amanzi.neo.loader.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.loader.core.CommonConfigData;
import org.eclipse.jface.wizard.IWizardPage;

/**
 * <p>
 *Network import wizard page
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class NetworkImportWizard extends AbstractLoaderWizard<CommonConfigData> {

    private CommonConfigData data;

    @Override
    protected List<IWizardPage> getMainPagesList() {
        List<IWizardPage> result=new ArrayList<IWizardPage>();
        result.add(new LoadNetworkMainPage());
        return result;
    }

    @Override
    public CommonConfigData getConfigurationData() {
        if (data==null){
            data=new CommonConfigData();
        }
        return data;
    }

}