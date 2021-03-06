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

package org.amanzi.awe.nem.ui.widgets;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.amanzi.awe.nem.managers.network.NetworkElementManager;
import org.amanzi.awe.nem.managers.properties.KnownTypes;
import org.amanzi.awe.nem.managers.properties.PropertyContainer;
import org.amanzi.awe.nem.ui.messages.NEMMessages;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class PropertyCreationDialog extends Dialog implements ModifyListener {

    public interface IPropertyDialogListener {
        void onNewItemCreated(PropertyContainer container);
    }

    private static final GridLayout TWO_ELEMENT_LAYOUT = new GridLayout(2, false);

    private static final GridLayout ONE_ELEMENT_LAYOUT = new GridLayout(1, false);

    private final Text tText;

    private final Combo cTypes;

    private final IPropertyDialogListener listener;

    private static final Set<String> SYSTEM_PROPERTIES = new HashSet<String>();

    static {
        SYSTEM_PROPERTIES.add(NetworkElementManager.getInstance().getGeneralNodeProperties().getNodeTypeProperty());
        SYSTEM_PROPERTIES.add(NetworkElementManager.getInstance().getGeneralNodeProperties().getLastChildID());
        SYSTEM_PROPERTIES.add(NetworkElementManager.getInstance().getGeneralNodeProperties().getParentIDProperty());
        SYSTEM_PROPERTIES.add(NetworkElementManager.getInstance().getGeneralNodeProperties().getSizeProperty());
        SYSTEM_PROPERTIES.add("id");
    }

    /**
     * @param parent
     */
    public PropertyCreationDialog(final Shell parent, final IPropertyDialogListener listener) {
        super(parent);
        super.create();
        this.listener = listener;
        getShell().setText("Create new property");
        final Composite controlsComposite = createComposite((Composite)getDialogArea(), TWO_ELEMENT_LAYOUT);
        final Composite labelsCompsoite = createComposite(controlsComposite, ONE_ELEMENT_LAYOUT);

        createLabel(labelsCompsoite, "Name");
        createLabel(labelsCompsoite, "Type");

        final Composite controls = createComposite(controlsComposite, ONE_ELEMENT_LAYOUT);
        tText = new Text(controls, SWT.BORDER);
        tText.setLayoutData(new GridData(GridData.FILL_BOTH));
        tText.addModifyListener(this);

        cTypes = new Combo(controls, SWT.BORDER);
        cTypes.setLayoutData(new GridData(GridData.FILL_BOTH));
        initTypesCombo();
        cTypes.select(0);

        getButton(OK).setEnabled(false);
        getShell().pack();
    }

    /**
     * @param dialogArea
     * @param i
     * @return
     */
    private Composite createComposite(final Composite parentComposite, final GridLayout layot) {
        final Composite composite = new Composite(parentComposite, SWT.NONE);
        composite.setLayout(layot);
        final GridData data = new GridData(GridData.FILL_BOTH);
        composite.setLayoutData(data);
        return composite;
    }

    /**
     * @param labelsCompsoite
     * @param string
     */
    private void createLabel(final Composite labelsCompsoite, final String text) {
        final Label label = new Label(labelsCompsoite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));
        label.setText(text);

    }

    /**
     *
     */
    private void initTypesCombo() {
        for (final KnownTypes type : KnownTypes.values()) {
            if (type.equals(KnownTypes.OBJECT)) {
                continue;
            }
            cTypes.add(type.getId());
        }
    }

    @Override
    public void modifyText(final ModifyEvent e) {
        getButton(OK).setEnabled(!tText.getText().isEmpty());

    }

    @Override
    protected void okPressed() {
        if (SYSTEM_PROPERTIES.contains(tText.getText())) {
            MessageDialog.openError(getShell(), "Unable to create pre-defined system property",
                    MessageFormat.format(NEMMessages.ERROR_ON_CREATING_SYSTEM_PROPERTY_MESSAGE, tText.getText()));
            return;
        }
        listener.onNewItemCreated(new PropertyContainer(tText.getText(), KnownTypes.getTypeById(cTypes.getText())));
        super.okPressed();
    }
}
