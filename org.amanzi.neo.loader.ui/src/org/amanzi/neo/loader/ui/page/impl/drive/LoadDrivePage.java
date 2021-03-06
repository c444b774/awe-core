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

package org.amanzi.neo.loader.ui.page.impl.drive;

import java.io.File;
import java.util.Collection;

import org.amanzi.awe.ui.view.widgets.AWEWidgetFactory;
import org.amanzi.neo.loader.core.ILoader;
import org.amanzi.neo.loader.core.impl.MultiFileConfiguration;
import org.amanzi.neo.loader.ui.internal.Messages;
import org.amanzi.neo.loader.ui.page.impl.internal.AbstractLoaderPage;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectDriveNameWidget;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectDriveNameWidget.ISelectDriveListener;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectDriveResourcesWidget;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectDriveResourcesWidget.ISelectDriveResourceListener;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectLoaderWidget;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectLoaderWidget.ISelectLoaderListener;
import org.amanzi.neo.loader.ui.page.widgets.impl.WizardFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class LoadDrivePage extends AbstractLoaderPage<MultiFileConfiguration>
        implements
            ISelectLoaderListener,
            ISelectDriveListener,
            ISelectDriveResourceListener {

    private SelectDriveNameWidget driveNameCombo;

    private SelectLoaderWidget<MultiFileConfiguration> loaderCombo;

    private SelectDriveResourcesWidget driveResource;

    /**
     * @param pageName
     */
    public LoadDrivePage() {
        super(Messages.LoadDrivePage_PageName);
    }

    @Override
    public void createControl(final Composite parent) {
        super.createControl(parent);

        driveNameCombo = WizardFactory.getInstance().addDatasetNameSelectorForDrive(getMainComposite(), this);
        AWEWidgetFactory.getFactory().addCRSSelectorWidget(this, getMainComposite());
        driveResource = WizardFactory.getInstance().addDriveResourceSelector(getMainComposite(), this, getFileFilter());

        loaderCombo = WizardFactory.getInstance().addLoaderSelector(getMainComposite(), this, getLoaders());

        update();
    }

    private IOFileFilter getFileFilter() {
        if (getCurrentLoader() == null) {
            IOFileFilter filter = FileFilterUtils.trueFileFilter();
            for (ILoader< ? , ? > loader : getLoaders()) {
                filter = FileFilterUtils.orFileFilter(filter, loader.getFileFilter());
            }
            return filter;
        } else {
            return getCurrentLoader().getFileFilter();
        }
    }

    @Override
    public void onDriveChanged() {
        MultiFileConfiguration configuration = getConfiguration();

        if (driveNameCombo != null) {
            configuration.setDatasetName(driveNameCombo.getText());
        }

        update();
    }

    @Override
    public void onLoaderChanged() {
        setCurrentLoader(getLoader(loaderCombo.getText()));

        update();
    }

    @Override
    public void update() {
        super.update();

        if (driveResource != null) {
            driveResource.updateFilter(getFileFilter());
        }
    }

    @Override
    public void onResourcesSelected(final Collection<File> files) {
        MultiFileConfiguration configuration = getConfiguration();

        configuration.addFiles(files);

        autodefineLoader();

        if (getCurrentLoader() != null) {
            loaderCombo.setText(getCurrentLoader().getName());
        }
    }

    @Override
    public void onDirectorySelected(final String directoryName) {
        driveNameCombo.setText(FilenameUtils.getBaseName(directoryName));
    }

    @Override
    public void dispose() {
        loaderCombo.finishUp();
        driveNameCombo.finishUp();

        super.dispose();
    }

}
