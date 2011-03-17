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

package org.amanzi.awe.afp.wizards;



import java.util.Arrays;

import org.amanzi.awe.afp.models.AfpModel;
import org.amanzi.awe.afp.models.AfpSeparationDomainModel;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class AfpWizardUtils {
	
	private static String domainName;
	private static String[] selectedArray;
	
	protected static Group getStepsGroup(Composite parent, int stepNumber){
		Group stepsGroup = new Group(parent, SWT.NONE);
		stepsGroup.setLayout(new GridLayout(1, false));
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, false, true, 1 ,2);
		gridData.widthHint = 220;
		stepsGroup.setLayoutData(gridData);
		
		String steps[] = {"Step 1 - Optimization Goals  ",
						  "Step 2 - Available Resources  ",
						  "Step 3 - Frequency Type  ",
						  "Step 4 - SY Hopping MALs  ",
						  "Step 5 - Separation Rules  ",
						  "Step 6 - Scaling Rules  ",
						  "Step 7 - Summary  "};
		
		for (int i = 0; i < steps.length; i++){
			Label label = new Label(stepsGroup, SWT.LEFT_TO_RIGHT);
			label.setText(steps[i]);
			if (i == stepNumber - 1)
				makeFontBold(label);
		}
		
		
		return stepsGroup;
	}
	
	protected static void makeFontBold(Control label){
		FontData[] fD = label.getFont().getFontData();
		fD[0].setStyle(SWT.BOLD);
		Font font = new	Font(label.getDisplay(),fD[0]);
		label.setFont(font);
		font.dispose();
	}
	
	
	protected static void createFrequencySelector(Shell parentShell, Text frequenciesText, String frequencies[]){
		final Text frequenciesTextLocal = frequenciesText;
		int numSelected = 0;
		String[] frequenciesLeft = null;
		String[] selectedRanges = new String[]{};
		if (!frequenciesText.getText().trim().equals(""))
			selectedRanges = frequenciesText.getText().split(",");
		
		if (selectedRanges.length > 0 && selectedRanges[0] != null && !selectedRanges[0].trim().equals("")){
			String[] selected = AfpModel.rangeArraytoArray(selectedRanges);
			numSelected = selected.length;
			frequenciesLeft = new String[frequencies.length - selected.length];
			
			Arrays.sort(selected);
			int i = 0;
			for (String item: frequencies){
				if (Arrays.binarySearch(selected, item) < 0){
					frequenciesLeft[i] = item;
					i++;
				}		
			}
		}
		else {
			frequenciesLeft = frequencies;
		}
		
		final Shell subShell = new Shell(parentShell, SWT.PRIMARY_MODAL|SWT.TITLE);
		subShell.setText("Frequency Selector");
		subShell.setLayout(new GridLayout(3, false));
		subShell.setLocation(200, 200);
		
		Group freqGroup = new Group(subShell, SWT.NONE);
		freqGroup.setLayout(new GridLayout(3, false));
		freqGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false,3 ,1));
		freqGroup.setText("Frequency Selector");
		Label freqLabel = new Label (freqGroup, SWT.LEFT);
		freqLabel.setText("Frequencies");
		freqLabel.setLayoutData(new GridData(GridData.FILL, SWT.LEFT, true, false,2 ,1));
		
		Label selectionLabel = new Label (freqGroup, SWT.LEFT);
		//TODO update this label on selection and removal of frequencies
		selectionLabel.setText(numSelected + " Frequencies Selected");
		selectionLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false,1 ,1));
		
		final List selectedList = createListSelector(freqGroup, frequenciesLeft, selectedRanges, selectionLabel);
		
		Button selectButton = new Button(subShell, SWT.PUSH);
		selectButton.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, true, false, 2, 1));
		selectButton.setText("Select");
		selectButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				String selected[] = AfpModel.arrayToRangeArray(selectedList.getItems());
				String selectedString = "";
				for (int i = 0; i< selected.length; i++){
					if (i == selected.length - 1)
						selectedString += selected[i];
					else 
						selectedString += selected[i] + ",";
				}
				frequenciesTextLocal.setText(selectedString);
				subShell.dispose();
			}
		});
		
		Button cancelButton = new Button(subShell, SWT.PUSH);
		cancelButton.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false, 1, 1));
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				subShell.dispose();
			}
		});
		
		subShell.pack();
		subShell.open();
		
	 }
	
	
	
	/**
	 * Creates a list GUI to select from the given list on the parent group
	 * @param parentGroup
	 * @param leftList
	 * @return
	 */
	public static List createListSelector(Group parentGroup, String[] leftList, String[] rightList, Label selectionLabel){
		
		final Label thisSelectionLabel = selectionLabel;
		final List freqList = new List(parentGroup, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2);
		int listHeight = freqList.getItemHeight() * 12;
		int listWidth = selectionLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		Rectangle trim = freqList.computeTrim(0, 0, 0, listHeight);
		gridData.heightHint = trim.height;
		gridData.widthHint = listWidth;
		freqList.setLayoutData(gridData);
		freqList.setItems(leftList);
		
		Button rightArrowButton = new Button (parentGroup, SWT.ARROW | SWT.RIGHT | SWT.BORDER);
		GridData arrowGridData = new GridData(GridData.FILL, GridData.END, true, false,1 ,1);
		arrowGridData.verticalIndent = trim.height/2;
		rightArrowButton.setLayoutData(arrowGridData);
		
		
		final List selectedList = new List(parentGroup, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2);
		gridData.heightHint = trim.height;
		gridData.widthHint = listWidth;
		selectedList.setLayoutData(gridData);
		selectedList.setItems(AfpModel.rangeArraytoArray(rightList));
		
		rightArrowButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (freqList.getSelectionCount() > 0){
					String selectedNew[] = freqList.getSelection();
					for (String item: selectedNew){//int i = 0; i < selectedNew.length; i++){
						selectedList.add(item);
						freqList.remove(item);
					}
					selectedArray = selectedList.getItems();
					thisSelectionLabel.setText("" + selectedArray.length + " Frequencies selected");
				}
				
			}
		});
		
		
		Button leftArrowButton = new Button (parentGroup, SWT.ARROW | SWT.LEFT | SWT.BORDER);
		leftArrowButton.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false,1 ,1));
		leftArrowButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selectedList.getSelectionCount() > 0){
					String deSelected[] = selectedList.getSelection();
					for (String item: deSelected){//int i = 0; i < deSelected.length; i++){
						freqList.add(item);
						selectedList.remove(item);
					}
					selectedArray = selectedList.getItems();
					String array[] = AfpModel.rangeArraytoArray(selectedArray);
					thisSelectionLabel.setText("" + array.length + " Frequencies selected");
					String notSelected[] = AfpModel.rangeArraytoArray(freqList.getItems());
					freqList.setItems(notSelected);
				}
				
			}
		});
		
		return selectedList;
	}
	

	public static void main(String args[]){
//		String[] array = {"0","2","4","8","9","10","12","13","15","16","17","18","19","20", "22"};
//		String[] rangeArray = arrayToRangeArray(array);
//		String[] rangeArray = {"0","2","4","8-10","12","13","15-20", "22"};
//		String[] array = rangeArraytoArray(rangeArray);
//		for (String item : array)
//			System.out.println(item);
		
	}
	
	
	protected static void createButtonsGroup(final WizardPage page, final Group parentGroup, String caller, final AfpModel model){

		final Shell parentShell = parentGroup.getShell();
		final String thisCaller = caller;
		
		Group buttonsGroup = new Group(parentGroup, SWT.NONE);
    	buttonsGroup.setLayout(new GridLayout(1, false));
    	buttonsGroup.setLayoutData(new GridData(GridData.END, GridData.FILL, false, true, 1 , 10));
    	Button addButton = new Button(buttonsGroup, GridData.BEGINNING);
    	addButton.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1 , 1));
    	addButton.setText("Add Domain");
    	addButton.addSelectionListener(new SelectionAdapter(){
    		
    		@Override
			public void widgetSelected(SelectionEvent e) {
    			if (thisCaller.equals("FrequencyType"))
    				new AfpFrequencySelector(page,parentShell, "Add", parentGroup, model);
    			else if (thisCaller.equals("HoppingMAL"))
    				new AfpMalSelector(page,parentShell, "Add", parentGroup, model);
    			else if (thisCaller.equals("Sector SeparationRules"))
    				new AfpSeperationSelector(page,parentShell, "Add", parentGroup, model,true );
    				//AfpWizardUtils.createSeparationDomainShell(page,parentShell, "Add", true, parentGroup, model);
    			else if (thisCaller.equals("Site SeparationRules"))
    				new AfpSeperationSelector(page,parentShell, "Add", parentGroup, model,false );
    				//AfpWizardUtils.createSeparationDomainShell(page,parentShell, "Add", false, parentGroup, model);
			}
    		
    	});
    	
    	Button editButton = new Button(buttonsGroup, GridData.BEGINNING);
    	editButton.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1 , 1));
    	editButton.setText("Edit Domain");
    	editButton.addSelectionListener(new SelectionAdapter(){
    		
    		@Override
			public void widgetSelected(SelectionEvent e) {
    			if (thisCaller.equals("FrequencyType"))
    				new AfpFrequencySelector(page,parentShell, "Edit", parentGroup, model);
    			else if (thisCaller.equals("HoppingMAL"))
    				new AfpMalSelector(page,parentShell, "Edit", parentGroup, model);
    			else if (thisCaller.equals("Sector SeparationRules"))
    				new AfpSeperationSelector(page,parentShell, "Edit", parentGroup, model,true );
    				//AfpWizardUtils.createSeparationDomainShell(page,parentShell, "Edit", true, parentGroup, model);
    			else if (thisCaller.equals("Site SeparationRules"))
    				new AfpSeperationSelector(page,parentShell, "Edit", parentGroup, model, false);
    				//AfpWizardUtils.createSeparationDomainShell(page,parentShell, "Edit", false, parentGroup, model);
			}
    		
    	});
    	
    	Button deleteButton = new Button(buttonsGroup, GridData.BEGINNING);
    	deleteButton.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1 , 1));
    	deleteButton.setText("Delete Domain");
    	deleteButton.addSelectionListener(new SelectionAdapter(){
    		
    		@Override
			public void widgetSelected(SelectionEvent e) {
    			if (thisCaller.equals("FrequencyType"))
    				new AfpFrequencySelector(page,parentShell, "Delete", parentGroup, model);
    			else if (thisCaller.equals("HoppingMAL"))
    				new AfpMalSelector(page,parentShell, "Delete", parentGroup, model);
    			else if (thisCaller.equals("Sector SeparationRules"))
    				new AfpSeperationSelector(page,parentShell, "Delete", parentGroup, model, true);
    				//AfpWizardUtils.createSeparationDomainShell(page,parentShell, "Delete", true, parentGroup, model);
    			else if (thisCaller.equals("Site SeparationRules"))
    				new AfpSeperationSelector(page,parentShell, "Delete", parentGroup, model, false);
    				//AfpWizardUtils.createSeparationDomainShell(page,parentShell, "Delete", false, parentGroup, model);
				
			}
    		
    	});
	}
	
	protected static void createSeparationDomainShell(final WizardPage page,Shell parentShell, final String action, final boolean isSector, final Group parentGroup, final AfpModel model){
		final Shell subShell = new Shell(parentShell, SWT.PRIMARY_MODAL|SWT.TITLE);
		final AfpSeparationDomainModel domainModel = new AfpSeparationDomainModel(); 
		String entity = isSector? "Sector" : "Site";
		String title = action + " " + entity + " Separation Domain";	
		                
		subShell.setText(title);
		subShell.setLayout(new GridLayout(2, false));
		subShell.setLocation(200, 100);
		
		Label nameLabel = new Label(subShell, SWT.NONE);
		nameLabel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		nameLabel.setText("Domain Name");

		if (action.equals("Add")){
			Text nameText = new Text (subShell, SWT.BORDER | SWT.SINGLE);
			nameText.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
			nameText.addModifyListener(new ModifyListener(){

				@Override
				public void modifyText(ModifyEvent e) {
					domainName = ((Text)e.widget).getText();
				}
				
			});
		}
		
		if (action.equals("Edit") || action.equals("Delete")){
			Combo nameCombo = new Combo(subShell, SWT.DROP_DOWN | SWT.READ_ONLY);
			//TODO populate combo values
			if (isSector)
				nameCombo.setItems(model.getAllSectorSeparationDomainNames());
			else
				nameCombo.setItems(model.getAllSiteSeparationDomainNames());
			
			nameCombo.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
			nameCombo.addModifyListener(new ModifyListener(){

				@Override
				public void modifyText(ModifyEvent e) {
					domainName = ((Combo)e.widget).getText();
					
				}
				
			});
			
			nameCombo.addSelectionListener(new SelectionListener(){

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
					
				}

				@Override
				public void widgetSelected(SelectionEvent e) {
					domainName = ((Combo)e.widget).getText();
				}
				
			});
		}
		
		
		
		Group rulesGroup = new Group(subShell, SWT.NONE);
		rulesGroup.setLayout(new GridLayout(3, true));
		rulesGroup.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 3, 10));
		
		rulesGroup.setText(entity + " Separation Rules");
		
		Label servingLabel = new Label(rulesGroup, SWT.LEFT);
		servingLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		servingLabel.setText("Serving");
		makeFontBold(servingLabel);
		
		Label interferingLabel = new Label(rulesGroup, SWT.LEFT);
		interferingLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		interferingLabel.setText("Interfering");
		makeFontBold(interferingLabel);
		
		Label separationLabel = new Label(rulesGroup, SWT.LEFT);
		separationLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		separationLabel.setText("Separation");
		makeFontBold(separationLabel);
		
		new Label(rulesGroup, SWT.LEFT).setText("BCCH");
		new Label(rulesGroup, SWT.LEFT).setText("BCCH");
		Text text1 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text1.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text1.setText("NA");
		//TODO add some mechanism to have only one listener class for all these texts.
		text1.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				domainModel.setSeparation0(((Text)e.widget).getText());	
			}
			
		});

		
		new Label(rulesGroup, SWT.LEFT).setText("BCCH");
		new Label(rulesGroup, SWT.LEFT).setText("Non/BB TCH");
		Text text2 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text2.setText("2");
		
		new Label(rulesGroup, SWT.LEFT).setText("BCCH");
		new Label(rulesGroup, SWT.LEFT).setText("SY TCH");
		Text text3 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text3.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text3.setText("2");
		
		new Label(rulesGroup, SWT.LEFT).setText("Non/BB TCH");
		new Label(rulesGroup, SWT.LEFT).setText("BCCH");
		Text text4 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text4.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text4.setText("2");
		
		new Label(rulesGroup, SWT.LEFT).setText("Non/BB TCH");
		new Label(rulesGroup, SWT.LEFT).setText("Non/BB TCH");
		Text text5 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text5.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text5.setText("2");
		
		new Label(rulesGroup, SWT.LEFT).setText("Non/BB TCH");
		new Label(rulesGroup, SWT.LEFT).setText("SY TCH");
		Text text6 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text6.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text6.setText("2");
		
		new Label(rulesGroup, SWT.LEFT).setText("SY TCH");
		new Label(rulesGroup, SWT.LEFT).setText("BCCH");
		Text text7 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text7.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text7.setText("2");
		
		new Label(rulesGroup, SWT.LEFT).setText("SY TCH");
		new Label(rulesGroup, SWT.LEFT).setText("Non/BB TCH");
		Text text8 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text8.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text8.setText("2");
		
		new Label(rulesGroup, SWT.LEFT).setText("Non/BB TCH");
		new Label(rulesGroup, SWT.LEFT).setText("Non/BB TCH");
		Text text9 = new Text (rulesGroup, SWT.BORDER | SWT.SINGLE);
		text9.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		text9.setText("2");
		
		
		Button actionButton = new Button(subShell, SWT.PUSH);
		actionButton.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false, 1, 1));
		actionButton.setText(action);
		actionButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO do something here
				if (action.equals("Add")){
					domainModel.setName(domainName);
					if (isSector){
						model.addSectorSeparationDomain(domainModel);
						Label newDomainLabel = new Label(parentGroup, SWT.LEFT);
						newDomainLabel.setText(domainName);
						Label domainSectorsLabel = new Label(parentGroup, SWT.LEFT);
						//TODO Do something for TRX
						domainSectorsLabel.setText("0");
						((AfpSeparationRulesPage)page).refreshPage();
					}
					else{
						model.addSiteSeparationDomain(domainModel);
						Label newDomainLabel = new Label(parentGroup, SWT.LEFT);
						newDomainLabel.setText(domainName);
						Label domainSiteLabel = new Label(parentGroup, SWT.LEFT);
						//TODO Do something for TRX
						domainSiteLabel.setText("0");
						((AfpSeparationRulesPage)page).refreshPage();
					}
					
				}
				
				//TODO add for edit and delete
				
				if (action.equals("Delete")){
					if (isSector){
						AfpSeparationDomainModel domainModel = model.findSectorSeparationDomain(domainName);
						
						if (domainModel == null){
							//TODO Do some error handling here;
						}
						model.deleteSectorSeparationDomain(domainModel);
						((AfpSeparationRulesPage)page).refreshPage();
					}
					else{
						AfpSeparationDomainModel domainModel = model.findSiteSeparationDomain(domainName);
						
						if (domainModel == null){
							//TODO Do some error handling here;
						}
						model.deleteSiteSeparationDomain(domainModel);
						((AfpSeparationRulesPage)page).refreshPage();
					}
				}
				
				
				subShell.dispose();
			}
		});
	
		
		
		Button cancelButton = new Button(subShell, SWT.PUSH);
		cancelButton.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false, 1, 1));
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				subShell.dispose();
			}
		});
		
		subShell.pack();
		subShell.open();
	}
	

}
