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

package org.amanzi.awe.views.statistics.table;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.statistics.dto.IStatisticsCell;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.google.common.collect.Iterables;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsLabelProvider implements ITableLabelProvider {

    private IStatisticsModel statisticsModel;

    private IStatisticsRow previousRow = null;

    private final List<IStatisticsCell> cellList = new ArrayList<IStatisticsCell>();

    @Override
    public void addListener(final ILabelProviderListener listener) {

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isLabelProperty(final Object element, final String property) {
        return false;
    }

    @Override
    public void removeListener(final ILabelProviderListener listener) {

    }

    @Override
    public Image getColumnImage(final Object element, final int columnIndex) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getColumnText(final Object element, final int columnIndex) {
        if (element instanceof IStatisticsRow) {
            IStatisticsRow statisticsRow = (IStatisticsRow)element;

            if (!statisticsRow.equals(previousRow)) {
                initializeCellList(statisticsRow);

                previousRow = statisticsRow;
            }

            switch (columnIndex) {
            case 0:
                return statisticsModel.getAggregatedProperty();
            case 1:
                return statisticsRow.getStatisticsGroup().getPropertyValue();
            default:
                Number value = cellList.get(columnIndex - 2).getValue();

                return value == null ? "N/A" : value.toString();
            }
        }

        return StringUtils.EMPTY;
    }

    private void initializeCellList(final IStatisticsRow statisticsRow) {
        Iterables.addAll(cellList, statisticsRow.getStatisticsCells());
    }

    public void updateStatisticsModel(final IStatisticsModel model) {
        this.statisticsModel = model;
    }

}
