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

package org.amanzi.awe.views.statistics;

import org.amanzi.awe.statistics.manager.StatisticsManager;
import org.amanzi.awe.statistics.period.Period;
import org.amanzi.awe.statistics.template.Template;
import org.amanzi.awe.ui.view.widget.AWEWidgetFactory;
import org.amanzi.awe.ui.view.widget.DriveComboWidget;
import org.amanzi.awe.ui.view.widget.DriveComboWidget.IDriveSelectionListener;
import org.amanzi.awe.ui.view.widget.PeriodWidget;
import org.amanzi.awe.ui.view.widget.PeriodWidget.ITimePeriodSelectionListener;
import org.amanzi.awe.ui.view.widget.PropertyComboWidget;
import org.amanzi.awe.ui.view.widget.PropertyComboWidget.IPropertySelectionListener;
import org.amanzi.awe.views.statistics.widget.PeriodComboWidget;
import org.amanzi.awe.views.statistics.widget.PeriodComboWidget.IPeriodSelectionListener;
import org.amanzi.awe.views.statistics.widget.TemplateComboWidget;
import org.amanzi.awe.views.statistics.widget.TemplateComboWidget.ITemplateSelectionListener;
import org.amanzi.neo.models.drive.IDriveModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsView extends ViewPart
        implements
            IDriveSelectionListener,
            IPropertySelectionListener,
            ITemplateSelectionListener,
            IPeriodSelectionListener,
            ITimePeriodSelectionListener {

    /** GridLayout ONE_ROW_GRID_LAYOUT field */
    private static final GridLayout ONE_ROW_GRID_LAYOUT = new GridLayout(1, false);

    private DriveComboWidget driveCombo;

    private TemplateComboWidget templateCombo;

    private PropertyComboWidget propertyComboWidget;

    private PeriodComboWidget periodCombo;

    private StatisticsManager statisticsManager;

    private PeriodWidget timePeriod;

    private boolean isInitialized = false;

    public StatisticsView() {
    }

    @Override
    public void createPartControl(final Composite parent) {
        Composite mainComposite = new Composite(parent, SWT.BORDER);
        mainComposite.setLayout(ONE_ROW_GRID_LAYOUT);

        addTemplateComposite(mainComposite);
        addPeriodComposite(mainComposite);

        isInitialized = true;
        driveCombo.updateSelection();
    }

    private void addTemplateComposite(final Composite parent) {
        Composite composite = new Composite(parent, SWT.BORDER);
        composite.setLayoutData(getCompositeGridData());
        composite.setLayout(new GridLayout(3, false));

        addTemplateCompositeContent(composite);
    }

    private void addTemplateCompositeContent(final Composite parent) {
        driveCombo = AWEWidgetFactory.getFactory().addDriveComboWidget(this, "Dataset:", parent);

        templateCombo = addTemplateComboWidget(parent, this);
        templateCombo.setEnabled(false);

        propertyComboWidget = AWEWidgetFactory.getFactory().addPropertyComboWidget(this, "Aggregation:", parent);
        propertyComboWidget.setEnabled(false);
    }

    private void addPeriodComposite(final Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(getCompositeGridData());
        composite.setLayout(new GridLayout(2, false));

        addPeriodCompositeContent(composite);
    }

    private GridData getCompositeGridData() {
        return new GridData(SWT.FILL, SWT.CENTER, true, false);
    }

    private void addPeriodCompositeContent(final Composite parent) {
        periodCombo = addPeriodComboWidget(parent, this);
        periodCombo.setEnabled(false);

        timePeriod = AWEWidgetFactory.getFactory().addPeriodWidget(this, "Start time:", "End time", parent);
        timePeriod.setLayoutData(getCompositeGridData());
        // timePeriod.setEnabled(false);
    }

    @Override
    public void dispose() {
        driveCombo.dispose();
        templateCombo.dispose();
        propertyComboWidget.dispose();
        periodCombo.dispose();
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    // TODO: LN: 09.08.2012, duplicate code
    private TemplateComboWidget addTemplateComboWidget(final Composite parent, final ITemplateSelectionListener listener) {
        TemplateComboWidget result = new TemplateComboWidget(parent, listener, "Template:");
        result.initializeWidget();

        return result;
    }

    // TODO: LN: 09.08.2012, duplicate code
    private PeriodComboWidget addPeriodComboWidget(final Composite parent, final IPeriodSelectionListener listener) {
        PeriodComboWidget result = new PeriodComboWidget(parent, listener, "Period:");
        result.initializeWidget();

        return result;
    }

    @Override
    public void onTemplateSelected(final Template template) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPropertySelected(final String property) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDriveModelSelected(final IDriveModel model) {
        if (isInitialized && (model != null)) {
            statisticsManager = StatisticsManager.getManager(model);
            templateCombo.setStatisticsManager(statisticsManager);
            templateCombo.setEnabled(true);

            propertyComboWidget.setModel(model);
            propertyComboWidget.setEnabled(true);

            periodCombo.setModel(model);
            periodCombo.setEnabled(true);

            timePeriod.setPeriod(model.getMinTimestamp(), model.getMaxTimestamp());
            timePeriod.setEnabled(true);
        }
    }

    @Override
    public void onPeriodSelected(final Period period) {
        // TODO Auto-generated method stub

    }

}
