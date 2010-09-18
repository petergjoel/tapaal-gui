package pipe.gui.widgets;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import dk.aau.cs.TCTL.TCTLAbstractProperty;

import pipe.dataLayer.DataLayer;
import pipe.dataLayer.TAPNQuery;
import pipe.dataLayer.TAPNQuery.ReductionOption;
import pipe.dataLayer.TAPNQuery.SearchOption;
import pipe.dataLayer.TAPNQuery.TraceOption;
import pipe.gui.CreateGui;
import pipe.gui.Export;
import pipe.gui.Grid;
import pipe.gui.Pipe;
import pipe.gui.Verifier;
import pipe.gui.widgets.CopyOfQueryDialogue.QueryDialogueOption;

public class QueryDialogue extends JPanel{
	
	private static final long serialVersionUID = 7852107237344005546L;
	public enum QueryDialogueOption {VerifyNow, Save, Export}

	private boolean querySaved = false;

	private JRootPane rootPane;

	// Query Name Panel;
	private JPanel namePanel;

	// Boundedness check panel
	private JPanel boundednessCheckPanel;
	private JSpinner numberOfExtraTokensInNet;
	private JButton kbounded;
	private JButton kboundedOptimize;

	// Query Panel
	private JPanel queryPanel;

	private JPanel quantificationPanel;
	private ButtonGroup quantificationRadioButtonGroup;
	private JRadioButton existsDiamond;
	private JRadioButton existsBox;
	private JRadioButton forAllDiamond;
	private JRadioButton forAllBox;	
	
	private JTextField queryField;
	
	private JPanel logicButtonPanel;
	private ButtonGroup logicButtonGroup;
	private JButton conjunctionButton;
	private JButton disjunctionButton;
	
	private JPanel predicatePanel;
	private JButton addPredicateButton;
	private JComboBox placesBox;
	private JComboBox relationalOperatorBox;
	private JSpinner placeMarking;
	


	// Uppaal options panel (search + trace options)
	// search options panel
	private JPanel searchOptionsPanel;
	private JPanel uppaalOptionsPanel;
	private ButtonGroup searchRadioButtonGroup;
	private JRadioButton bFS;
	private JRadioButton dFS;
	private JRadioButton rDFS;
	private JRadioButton closestToTargetFirst;

	// Trace options panel
	private JPanel traceOptionsPanel;

	private ButtonGroup traceRadioButtonGroup;
	private JRadioButton none;
	private JRadioButton some;
	private JRadioButton fastest;

	// Reduction options panel
	private JPanel reductionOptionsPanel;
	private JComboBox reductionOption;
	private JCheckBox symmetryReduction;

	// Buttons in the bottom of the dialogue
	private JPanel buttonPanel;
	private JButton cancelButton;
	private JButton saveButton;
	private JButton saveAndVerifyButton;
	private JButton removeButton;
	private JButton saveUppaalXMLButton;

	// Private Members
	private DataLayer datalayer;
	private EscapableDialog me;

	private HashMap<JPanel, ActionListener> andActionListenerMap;

	private String name_ADVNOSYM = "Optimised Standard";
	private String name_NAIVE = "Standard";
	private String name_BROADCAST = "Broadcast Reduction";
	private String name_BROADCASTDEG2 = "Broadcast Degree 2 Reduction";
	
	private TCTLAbstractProperty property;


	public QueryDialogue (EscapableDialog me, DataLayer datalayer, QueryDialogueOption option, TAPNQuery queryToCreateFrom){

		this.datalayer = datalayer;
		this.me = me;
		this.property = queryToCreateFrom.getProperty();
		andActionListenerMap = new HashMap<JPanel, ActionListener>();
		rootPane = me.getRootPane();
		setLayout(new GridBagLayout());

		init(option, queryToCreateFrom);
	}

	private void init(QueryDialogueOption option, final TAPNQuery queryToCreateFrom) {
		initQueryNamePanel(queryToCreateFrom);
		initBoundednessCheckPanel(queryToCreateFrom);
		initQueryPanel(queryToCreateFrom);
		initUppaalOptionsPanel(queryToCreateFrom);
		initReductionOptionsPanel(queryToCreateFrom);
		initButtonPanel(option,queryToCreateFrom);

		rootPane.setDefaultButton(saveButton);

		quantificationRadioButtonChanged(null);

		//Update the selected reduction
		if (queryToCreateFrom!=null){
			String reduction = "";
			boolean symmetry = false;

			if(queryToCreateFrom.reductionOption == ReductionOption.BROADCAST_STANDARD){
				reduction = name_BROADCAST;
				symmetry = false;
				//enableTraceOptions();
			}else if(queryToCreateFrom.reductionOption == ReductionOption.BROADCAST_SYM){
				reduction = name_BROADCAST;
				symmetry = true;
				//disableTraceOptions();
			}else if(queryToCreateFrom.reductionOption == ReductionOption.BROADCAST_DEG2){
				reduction = name_BROADCASTDEG2;
				symmetry = false;
				//disableTraceOptions();
			}else if(queryToCreateFrom.reductionOption == ReductionOption.BROADCAST_DEG2_SYM){
				reduction = name_BROADCASTDEG2;
				symmetry = true;
				//disableTraceOptions();
			}
			else if (getQuantificationSelection().equals("E<>") || getQuantificationSelection().equals("A[]")){
				if (queryToCreateFrom.reductionOption == ReductionOption.NAIVE){
					reduction = name_NAIVE;
					symmetry = false;
					//enableTraceOptions();
				} else if (queryToCreateFrom.reductionOption == ReductionOption.NAIVE_UPPAAL_SYM){
					reduction = name_NAIVE;
					symmetry = true;
					//disableTraceOptions();
				} else if (queryToCreateFrom.reductionOption == ReductionOption.ADV_UPPAAL_SYM){
					reduction = name_ADVNOSYM;
					symmetry = true;
					//disableTraceOptions();
				} else if (queryToCreateFrom.reductionOption == ReductionOption.ADV_NOSYM){
					reduction = name_ADVNOSYM;
					symmetry = false;
					//enableTraceOptions();
				}
			} else {
				if (queryToCreateFrom.reductionOption == ReductionOption.ADV_UPPAAL_SYM){
					reduction = name_ADVNOSYM;
					symmetry = true;
					//disableTraceOptions();
				} else if (queryToCreateFrom.reductionOption == ReductionOption.ADV_NOSYM){
					reduction = name_ADVNOSYM;
					symmetry = false;
					//enableTraceOptions();
				}
			}

			reductionOption.setSelectedItem(reduction);
			symmetryReduction.setSelected(symmetry);
		}

	}

	private void initButtonPanel(QueryDialogueOption option, final TAPNQuery queryToCreateFrom) {
		buttonPanel = new JPanel(new FlowLayout());
		if (option == QueryDialogueOption.Save){
			saveButton = new JButton("Save");
			saveAndVerifyButton = new JButton("Save and Verify");
			cancelButton = new JButton("Cancel");
			removeButton = new JButton("Remove");
			saveUppaalXMLButton = new JButton("Save UPPAAL XML");

			saveButton.addActionListener(	
					new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							//TODO make save
							//save();
							querySaved = true;
							exit();
						}
					}
			);
			saveAndVerifyButton.addActionListener(	
					new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							querySaved = true;
							exit();
							//Verifier.runUppaalVerification(CreateGui.getModel(), getQuery());
						}
					}
			);
			cancelButton.addActionListener(	
					new ActionListener() {
						public void actionPerformed(ActionEvent evt) {

							exit();
						}
					}
			);
			removeButton.addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent evt) {

							datalayer.getQueries().remove(queryToCreateFrom);
							CreateGui.createLeftPane();
							exit();
						}
					}
			);
			saveUppaalXMLButton.addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							querySaved = true;

							String xmlFile = null, queryFile = null;
							try {
								xmlFile = new FileBrowser("Uppaal XML","xml",xmlFile).saveFile();
								String[] a = xmlFile.split(".xml");
								queryFile= a[0]+".q";

							} catch (Exception ex) {
								JOptionPane.showMessageDialog(CreateGui.getApp(),
										"There were errors performing the requested action:\n" + e,
										"Error", JOptionPane.ERROR_MESSAGE
								);				
							}

							if(xmlFile != null && queryFile != null){
								Export.exportUppaalXMLFromQuery(CreateGui.getModel(), getQuery(), xmlFile, queryFile);
							}else{
								JOptionPane.showMessageDialog(CreateGui.getApp(), "No Uppaal XML file saved.");
							}
						}
					}
			);
		}else if (option == QueryDialogueOption.Export){
			saveButton = new JButton("export");
			cancelButton = new JButton("Cancel");

			saveButton.addActionListener(	
					new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							querySaved = true;
							exit();
						}
					}
			);
			cancelButton.addActionListener(	
					new ActionListener() {
						public void actionPerformed(ActionEvent evt) {

							exit();
						}
					}
			);		
		}
		if (option == QueryDialogueOption.Save){
			buttonPanel.add(cancelButton);

			if (queryToCreateFrom!=null){
				buttonPanel.add(removeButton);	
			}

			buttonPanel.add(saveButton);

			buttonPanel.add(saveAndVerifyButton);

			buttonPanel.add(saveUppaalXMLButton);
		}else {
			buttonPanel.add(cancelButton);

			buttonPanel.add(saveButton);

			//			buttonPanel.add(saveUppaalXMLButton);
		}


		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		add(buttonPanel, gridBagConstraints);

	}

	private void initReductionOptionsPanel(final TAPNQuery queryToCreateFrom) {


		//ReductionOptions starts here:
		reductionOptionsPanel = new JPanel(new FlowLayout());
		reductionOptionsPanel.setBorder(BorderFactory.createTitledBorder("Reduction Options"));
		String[] reductionOptions = {name_NAIVE, name_ADVNOSYM, name_BROADCAST, name_BROADCASTDEG2};
		reductionOption = new JComboBox(reductionOptions);
		reductionOption.setSelectedIndex(3);


		reductionOptionsPanel.add(new JLabel("  Choose reduction method:"));
		reductionOptionsPanel.add(reductionOption);

		symmetryReduction = new JCheckBox("Use Symmetry Reduction");
		symmetryReduction.setSelected(true);
		symmetryReduction.addItemListener(new ItemListener(){


			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					disableTraceOptions();
				}else{
					enableTraceOptions();
				}

			}

		});

		reductionOptionsPanel.add(symmetryReduction);
		disableTraceOptions();

		GridBagConstraints gridBagConstraints;
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		add(reductionOptionsPanel, gridBagConstraints);

	}

	private void initUppaalOptionsPanel(final TAPNQuery queryToCreateFrom) {

		uppaalOptionsPanel = new JPanel(new GridBagLayout());

		initSearchOptionsPanel(queryToCreateFrom);
		initTraceOptionsPanel(queryToCreateFrom);

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		add(uppaalOptionsPanel, gridBagConstraints);

	}

	private void initSearchOptionsPanel(final TAPNQuery queryToCreateFrom) {
		//verification option-radio buttons starts here:		
		searchOptionsPanel = new JPanel(new GridBagLayout());

		JPanel searchOptions = new JPanel(new GridBagLayout());
		searchOptions.setBorder(BorderFactory.createTitledBorder("Analysis Options"));
		searchRadioButtonGroup = new ButtonGroup();
		bFS = new JRadioButton("Breadth First Search");
		dFS = new JRadioButton("Depth First Search");
		rDFS = new JRadioButton("Random Depth First Search");
		closestToTargetFirst = new JRadioButton("Search by Closest To Target First");
		searchRadioButtonGroup.add(bFS);
		searchRadioButtonGroup.add(dFS);
		searchRadioButtonGroup.add(rDFS);
		searchRadioButtonGroup.add(closestToTargetFirst);

		if (queryToCreateFrom==null){
			bFS.setSelected(true);
		}else{
			if (queryToCreateFrom.searchOption == SearchOption.BFS){
				bFS.setSelected(true);
			} else if (queryToCreateFrom.searchOption == SearchOption.DFS){
				dFS.setSelected(true);
			} else if (queryToCreateFrom.searchOption == SearchOption.RDFS){
				rDFS.setSelected(true);
			} else if (queryToCreateFrom.searchOption == SearchOption.CLOSE_TO_TARGET_FIRST){
				closestToTargetFirst.setSelected(true);
			}	
		}

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.gridy = 0;
		searchOptions.add(bFS,gridBagConstraints);
		gridBagConstraints.gridy = 1;
		searchOptions.add(dFS,gridBagConstraints);
		gridBagConstraints.gridy = 2;
		searchOptions.add(rDFS,gridBagConstraints);
		gridBagConstraints.gridy = 3;
		searchOptions.add(closestToTargetFirst,gridBagConstraints);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		uppaalOptionsPanel.add(searchOptions, gridBagConstraints);

	}

	private void initTraceOptionsPanel(final TAPNQuery queryToCreateFrom) {
		traceOptionsPanel = new JPanel(new GridBagLayout());
		traceOptionsPanel.setBorder(BorderFactory.createTitledBorder("Trace Options"));
		traceRadioButtonGroup = new ButtonGroup();
		some = new JRadioButton("Some encountered trace (only without symmetry reduction)");
		fastest = new JRadioButton("Fastest trace (only without symmetry reduction)");
		none = new JRadioButton("No trace");
		traceRadioButtonGroup.add(some);
		traceRadioButtonGroup.add(fastest);
		traceRadioButtonGroup.add(none);

		if (queryToCreateFrom==null){
			none.setSelected(true);
		}else{
			if (queryToCreateFrom.traceOption == TraceOption.SOME){
				some.setSelected(true);
			} else if (queryToCreateFrom.traceOption == TraceOption.FASTEST){
				fastest.setSelected(true);
			} else if (queryToCreateFrom.traceOption == TraceOption.NONE){
				none.setSelected(true);
			}	
		}

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.gridy = 0;
		traceOptionsPanel.add(some,gridBagConstraints);
		gridBagConstraints.gridy = 1;
		traceOptionsPanel.add(fastest,gridBagConstraints);
		gridBagConstraints.gridy = 2;
		traceOptionsPanel.add(none,gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		uppaalOptionsPanel.add(traceOptionsPanel, gridBagConstraints);

	}

	private void initQueryPanel(final TAPNQuery queryToCreateFrom) {
		queryPanel = new JPanel(new GridBagLayout());
		queryPanel.setBorder(BorderFactory.createTitledBorder("Query"));
		
		// Query Text Field
		queryField = new JTextField();
		//queryField.setMinimumSize(new Dimension(600,30));
		//queryField.setPreferredSize(new Dimension(600,30));
		queryField.setEditable(false);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 3;
		
		queryPanel.add(queryField,gbc);
		
		// Quantification Panel
		quantificationPanel = new JPanel(new GridBagLayout());
		quantificationPanel.setBorder(BorderFactory.createTitledBorder("Quantification"));
		quantificationRadioButtonGroup = new ButtonGroup();

		existsDiamond = new JRadioButton("(EF) There exists some reachable marking that satisifies:");
		existsBox = new JRadioButton("(EG) There exists a trace on which every marking satisfies:");
		forAllDiamond = new JRadioButton("(AF) On all traces there is eventually a marking that satisfies:");
		forAllBox = new JRadioButton("(AG) All reachable markings satisfy:");

		quantificationRadioButtonGroup.add(existsDiamond);
		quantificationRadioButtonGroup.add(existsBox);
		quantificationRadioButtonGroup.add(forAllDiamond);
		quantificationRadioButtonGroup.add(forAllBox);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		quantificationPanel.add(existsDiamond, gbc);
		if (queryToCreateFrom==null){
			existsDiamond.setSelected(true);
		}else {
			if (queryToCreateFrom.query.contains("E<>")){
				existsDiamond.setSelected(true);
			} else if (queryToCreateFrom.query.contains("E[]")){
				existsBox.setSelected(true);
			} else if (queryToCreateFrom.query.contains("A<>")){
				forAllDiamond.setSelected(true);
			} else if (queryToCreateFrom.query.contains("A[]")){
				forAllBox.setSelected(true);
			}
		}

		gbc.gridy = 1;
		quantificationPanel.add(existsBox, gbc);

		gbc.gridy = 2;
		quantificationPanel.add(forAllDiamond, gbc);

		gbc.gridy = 3;
		quantificationPanel.add(forAllBox, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		queryPanel.add(quantificationPanel,gbc);
		
		//Add action listeners to the query options
		for (Object radioButton : queryPanel.getComponents()){

			if( (radioButton instanceof JRadioButton)){
				((JRadioButton)radioButton).addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						//Update stuff
						quantificationRadioButtonChanged(arg0);
					}

				});
			}
		}
		
		// Logic panel
		logicButtonPanel = new JPanel(new GridBagLayout());
		logicButtonPanel.setBorder(BorderFactory.createTitledBorder("Logic"));
		
		logicButtonGroup = new ButtonGroup();
		conjunctionButton = new JButton("And");
		disjunctionButton = new JButton("Or");
		
		logicButtonGroup.add(conjunctionButton);
		logicButtonGroup.add(disjunctionButton);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		logicButtonPanel.add(conjunctionButton,gbc);
		
		gbc.gridy = 1;
		logicButtonPanel.add(disjunctionButton,gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.VERTICAL;
		queryPanel.add(logicButtonPanel,gbc);

		// Add Action listener for logic buttons
		// TODO: add action listeners for logic buttons
		
		// Predicate specification panel
		predicatePanel = new JPanel(new GridBagLayout());
		predicatePanel.setBorder(BorderFactory.createTitledBorder("Predicates"));
		
		String[] places = new String[datalayer.getPlaces().length];
		for (int i=0; i< places.length; i++){
			places[i] = datalayer.getPlaces()[i].getName();
		}
		
		placesBox = new JComboBox(new DefaultComboBoxModel(places));
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		predicatePanel.add(placesBox,gbc);
		
		String[] relationalSymbols= {"=","<=","<",">=",">"};
		relationalOperatorBox = new JComboBox(new DefaultComboBoxModel(relationalSymbols));
		
		gbc.gridx = 1;
		predicatePanel.add(relationalOperatorBox,gbc);
		
		int currentValue = 0;
		int min = 0;
		int step = 1;		
		placeMarking = new JSpinner(new SpinnerNumberModel(currentValue, min, Integer.MAX_VALUE, step));
		placeMarking.setMaximumSize(new Dimension(50,30));
		placeMarking.setMinimumSize(new Dimension(50,30));
		placeMarking.setPreferredSize(new Dimension(50,30));
		
		gbc.gridx = 2;
		predicatePanel.add(placeMarking,gbc);
		
		addPredicateButton = new JButton("Add Predicate to Query");
		gbc.gridx = 0;
		gbc.gridy = 1;
		predicatePanel.add(addPredicateButton,gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.VERTICAL;
		queryPanel.add(predicatePanel,gbc);
	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.WEST;
		add(queryPanel, gbc);

	}

	private void initBoundednessCheckPanel(final TAPNQuery queryToCreateFrom) {

		// Number of extra tokens field
		boundednessCheckPanel = new JPanel(new FlowLayout());
		boundednessCheckPanel.add(new JLabel("Extra number of tokens: "));

		if (queryToCreateFrom == null){
			numberOfExtraTokensInNet = new JSpinner(new SpinnerNumberModel(3,0,Integer.MAX_VALUE, 1));
		}else{
			numberOfExtraTokensInNet = new JSpinner(new SpinnerNumberModel(queryToCreateFrom.capacity,0,Integer.MAX_VALUE, 1));
		}
		numberOfExtraTokensInNet.setMaximumSize(new Dimension(50,30));
		numberOfExtraTokensInNet.setMinimumSize(new Dimension(50,30));
		numberOfExtraTokensInNet.setPreferredSize(new Dimension(50,30));
		boundednessCheckPanel.add(numberOfExtraTokensInNet);

		// Boundedness button
		kbounded = new JButton("Check Boundedness");
		kbounded.addActionListener(	
				new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						Verifier.analyseKBounded(CreateGui.getModel(), getCapacity());
					}

				}
		);		
		boundednessCheckPanel.add(kbounded);

		kboundedOptimize = new JButton("Optimize Number of Tokens");
		kboundedOptimize.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent evt){
						Verifier.analyzeAndOptimizeKBound(CreateGui.getModel(), getCapacity(), numberOfExtraTokensInNet);
					}
				}
		);
		boundednessCheckPanel.add(kboundedOptimize);

		GridBagConstraints gridBagConstraints;
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		add(boundednessCheckPanel, gridBagConstraints);
	}

	private void initQueryNamePanel(final TAPNQuery queryToCreateFrom) {
		// Query comment field starts here:		
		namePanel = new JPanel(new FlowLayout());
		namePanel.add(new JLabel("Query comment: "));
		JTextField queryComment;
		if (queryToCreateFrom==null){
			queryComment = new JTextField("Query Comment/Name Here",25);
		}else{
			queryComment = new JTextField(queryToCreateFrom.name,25);	
		}

		namePanel.add(queryComment);

		GridBagConstraints gridBagConstraints = new GridBagConstraints();		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		add(namePanel, gridBagConstraints);
	}

	public TAPNQuery getQuery() {
		if (!querySaved){
			return null;
		}
//		String name = getQueryComment();
//		int capacity = getCapacity();
//		String query = composeQuery();
//
//		TAPNQuery.TraceOption traceOption = null;
//		String traceOptionString = getTraceOptions();
//
//		if (traceOptionString.toLowerCase().contains("some")){
//			traceOption = TraceOption.SOME;
//		}else if (traceOptionString.toLowerCase().contains("fastest")){
//			traceOption = TraceOption.FASTEST;
//		}else if (traceOptionString.toLowerCase().contains("no")){
//			traceOption = TraceOption.NONE;
//		}
//
//		TAPNQuery.SearchOption searchOption = null;
//		String searchOptionString = getSearchOptions();
//
//		if (searchOptionString.toLowerCase().contains("breadth")){
//			searchOption = SearchOption.BFS;
//		}else if (searchOptionString.toLowerCase().contains("depth") && (! searchOptionString.toLowerCase().contains("random")) ){
//			searchOption = SearchOption.DFS;
//		}else if (searchOptionString.toLowerCase().contains("depth") && searchOptionString.toLowerCase().contains("random") ){
//			searchOption = SearchOption.RDFS;
//		}else if (searchOptionString.toLowerCase().contains("target")){
//			searchOption = SearchOption.CLOSE_TO_TARGET_FIRST;
//		}
//
//		TAPNQuery.ReductionOption reductionOptionToSet = null;
//		String reductionOptionString = ""+reductionOption.getSelectedItem();
//		boolean symmetry = symmetryReduction.isSelected();
//
//		if (reductionOptionString.equals(name_NAIVE) && !symmetry){
//			reductionOptionToSet = ReductionOption.NAIVE;
//		}else if(reductionOptionString.equals(name_NAIVE) && symmetry){
//			reductionOptionToSet = ReductionOption.NAIVE_UPPAAL_SYM;
//		}else if (reductionOptionString.equals(name_ADVNOSYM) && !symmetry){
//			reductionOptionToSet = ReductionOption.ADV_NOSYM;
//		}else if (reductionOptionString.equals(name_ADVNOSYM) && symmetry){
//			reductionOptionToSet = ReductionOption.ADV_UPPAAL_SYM;
//		}else if(reductionOptionString.equals(name_BROADCAST) && !symmetry){
//			reductionOptionToSet = ReductionOption.BROADCAST_STANDARD;
//		}else if(reductionOptionString.equals(name_BROADCAST) && symmetry){
//			reductionOptionToSet = ReductionOption.BROADCAST_SYM;
//		}else if(reductionOptionString.equals(name_BROADCASTDEG2) && !symmetry){
//			reductionOptionToSet = ReductionOption.BROADCAST_DEG2;
//		}else if(reductionOptionString.equals(name_BROADCASTDEG2) && symmetry){
//			reductionOptionToSet = ReductionOption.BROADCAST_DEG2_SYM;
//		}
//		
//
//		return new TAPNQuery(name, capacity, query, traceOption, searchOption, reductionOptionToSet, /*hashTableSizeToSet*/null, /*extrapolationOptionToSet*/ null);
		return null;
	}

	private int getCapacity(){
		return (Integer) ((JSpinner)boundednessCheckPanel.getComponent(1)).getValue();
	}

	private String getQueryComment() {
		return ((JTextField)namePanel.getComponent(1)).getText();
	}

	private void enableTraceOptions() {
		some.setEnabled(true);
		fastest.setEnabled(true);
	}

	private void disableTraceOptions() {
		some.setEnabled(false);
		fastest.setEnabled(false);
	}

	private void exit(){
		rootPane.getParent().setVisible(false);
	}

	public String getQuantificationSelection() {
		if (existsDiamond.isSelected()){
			return "E<>";
		}else if (existsBox.isSelected()){
			return "E[]";
		}else if (forAllDiamond.isSelected()){
			return "A<>";
		}else if (forAllBox.isSelected()){
			return "A[]";
		} else {
			return "";
		}
	}

	private void quantificationRadioButtonChanged(ActionEvent arg0) {
		// 
		if (getQuantificationSelection().equals("E[]") || getQuantificationSelection().equals("A<>")) {
			disableLivenessReductionOptions();
		} else {
			enableAllReductionOptions();
		}
	}

	private void disableLivenessReductionOptions(){
		String[] options = null;
		if(!this.datalayer.hasTAPNInhibitorArcs()){
			options = new String[]{name_ADVNOSYM, name_BROADCAST, name_BROADCASTDEG2};
		}else{
			options = new String[]{name_BROADCAST, name_BROADCASTDEG2};
		}
		reductionOption.removeAllItems();

		for (String s : options){
			reductionOption.addItem(s);
		}
	}

	private void enableAllReductionOptions(){
		reductionOption.removeAllItems();
		if(!this.datalayer.hasTAPNInhibitorArcs()){
			String[] options = {name_NAIVE, name_ADVNOSYM, name_BROADCAST, name_BROADCASTDEG2};

			for (String s : options){
				reductionOption.addItem(s);
			}
		}else {
			//reductionOption.addItem(name_INHIBSTANDARD);
			//reductionOption.addItem(name_INHIBSYM);
			reductionOption.addItem(name_BROADCAST);
			//reductionOption.addItem(name_BROADCASTSYM);
			reductionOption.addItem(name_BROADCASTDEG2);
			//reductionOption.addItem(name_BROADCASTDEG2SYM);
			//reductionOption.addItem(name_ADVBROADCASTSYM);
			//reductionOption.addItem(name_OPTBROADCAST);
			//reductionOption.addItem(name_OPTBROADCASTSYM);
			//reductionOption.addItem(name_SUPERBROADCAST);
			//reductionOption.addItem(name_SUPERBROADCASTSYM);
		}
	}
	
	private String getSearchOptions() {
		String toReturn = null;
		for (Object radioButton : searchOptionsPanel.getComponents()){
			if( (radioButton instanceof JRadioButton)){
				if ( ((JRadioButton)radioButton).isSelected() ){

					toReturn = ((JRadioButton)radioButton).getText();
					break;
				}
			}
		}
		return toReturn;
	}
	
	public static TAPNQuery ShowUppaalQueryDialogue(QueryDialogueOption option, TAPNQuery queryToRepresent){
		EscapableDialog guiDialog = 
			new EscapableDialog(CreateGui.getApp(), Pipe.getProgramName(), true);

		Container contentPane = guiDialog.getContentPane();

		// 1 Set layout
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));      

		// 2 Add query editor
		QueryDialogue queryDialogue = new QueryDialogue(guiDialog, CreateGui.getModel(), option, queryToRepresent); 
		contentPane.add( queryDialogue );

		guiDialog.setResizable(false);     

		// Make window fit contents' preferred size
		guiDialog.pack();

		// Move window to the middle of the screen
		guiDialog.setLocationRelativeTo(null);
		guiDialog.setVisible(true);

		return  queryDialogue.getQuery();
	}
	
	/*
	 
	private static final long serialVersionUID = 7852107237344005546L;
	public enum QueryDialogueOption {VerifyNow, Save, Export}

	private boolean querySaved=false;
	private JRootPane myRootPane;
	private JPanel queryPanel;
	private JPanel buttonPanel;
	private JPanel markingSpecificsPanel;
	private JPanel namePanel;
	private JPanel capacityPanel;
	private JPanel traceOptions;
	private JPanel searchOptions;
	private JPanel uppaalOptions;
	private JButton okButton;
	private JButton verifyButton;
	private JButton cancelButton;
	private JButton removeButton;
	private JButton saveUppaalXMLButton;

	private ButtonGroup quantificationRadioButtonGroup;
	private JRadioButton existsDiamond;
	private JRadioButton existsBox;
	private JRadioButton forAllDiamond;
	private JRadioButton forAllBox;	

	private ButtonGroup searchRadioButtonGroup;

	private JRadioButton bFS;
	private JRadioButton dFS;
	private JRadioButton rDFS;
	private JRadioButton closestToTargetFirst;


	private ButtonGroup traceRadioButtonGroup;

	private JRadioButton none;
	private JRadioButton some;
	private JRadioButton fastest;


	private JPanel reductionOptions;
	private JComboBox reductionOption;
	private JCheckBox symmetryReduction;

	private JSpinner numberOfExtraTokensInNet;
	private JButton kbounded;
	private JButton kboundedOptimize;

	//XXX shortest can be quite hard to guarantee, because, it might not be the shortest in UPPAAL
	//	private JRadioButtonMenuItem shortest;


	private DataLayer datalayer;
	private EscapableDialog me;
	private ArrayList<ArrayList<JPanel>> conjunctionGroups;
	private ArrayList<Integer> conjunctionsUsed;
	private HashMap<JPanel, ActionListener> andActionListenerMap;
	private ArrayList<JPanel> disjunctionGroups;
	private int disjunctionsUsed;

	private String name_ADVNOSYM = "Optimised Standard";
	private String name_NAIVE = "Standard";
	private String name_BROADCAST = "Broadcast Reduction";
	private String name_BROADCASTDEG2 = "Broadcast Degree 2 Reduction";
	public QueryDialogue (EscapableDialog me, DataLayer datalayer, QueryDialogueOption option, TAPNQuery queryToCreateFrom){

		this.datalayer = datalayer;
		this.me = me;
		andActionListenerMap = new HashMap<JPanel, ActionListener>();
		disjunctionGroups = new ArrayList<JPanel>();
		disjunctionsUsed = 0; 
		conjunctionGroups = new ArrayList<ArrayList<JPanel>>();
		conjunctionsUsed = new ArrayList<Integer>();
		myRootPane = me.getRootPane();
		setLayout(new GridBagLayout());

		init(option, queryToCreateFrom);
	}


	private void init(QueryDialogueOption option, final TAPNQuery queryToCreateFrom) {
		// Query comment field starts here:		
		namePanel = new JPanel(new FlowLayout());
		namePanel.add(new JLabel("Query comment: "));
		JTextField queryComment;
		if (queryToCreateFrom==null){
			queryComment = new JTextField("Query Comment/Name Here",25);
		}else{
			queryComment = new JTextField(queryToCreateFrom.name,25);	
		}

		namePanel.add(queryComment);

		GridBagConstraints gridBagConstraints = new GridBagConstraints();		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		add(namePanel, gridBagConstraints);

		//Capacity number field starts here:
		capacityPanel = new JPanel(new FlowLayout());
		capacityPanel.add(new JLabel("Extra number of tokens: "));

		if (queryToCreateFrom == null){
			numberOfExtraTokensInNet = new JSpinner(new SpinnerNumberModel(3,0,Integer.MAX_VALUE, 1));
		}else{
			numberOfExtraTokensInNet = new JSpinner(new SpinnerNumberModel(queryToCreateFrom.capacity,0,Integer.MAX_VALUE, 1));
		}
		numberOfExtraTokensInNet.setMaximumSize(new Dimension(50,30));
		numberOfExtraTokensInNet.setMinimumSize(new Dimension(50,30));
		numberOfExtraTokensInNet.setPreferredSize(new Dimension(50,30));
		capacityPanel.add(numberOfExtraTokensInNet);

		//Capacity boundness starts here

		kbounded = new JButton("Check Boundedness");
		kbounded.addActionListener(	
				new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						Verifier.analyseKBounded(CreateGui.getModel(), getCapacity());
					}
				}
		);		
		capacityPanel.add(kbounded);

		kboundedOptimize = new JButton("Optimize Number of Tokens");
		kboundedOptimize.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent evt){
						Verifier.analyzeAndOptimizeKBound(CreateGui.getModel(), getCapacity(), numberOfExtraTokensInNet);
					}
				}
		);
		capacityPanel.add(kboundedOptimize);


		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		add(capacityPanel, gridBagConstraints);





		// Query field starts here: 		
		queryPanel = new JPanel(new GridBagLayout());
		queryPanel.setBorder(BorderFactory.createTitledBorder("Query"));

		quantificationRadioButtonGroup = new ButtonGroup();

		existsDiamond = new JRadioButton("(EF) There exists some reachable marking that satisifies:");
		existsBox = new JRadioButton("(EG) There exists a trace on which every marking satisfies:");
		forAllDiamond = new JRadioButton("(AF) On all traces there is eventually a marking that satisfies:");
		forAllBox = new JRadioButton("(AG) All reachable markings satisfy:");

		quantificationRadioButtonGroup.add(existsDiamond);
		quantificationRadioButtonGroup.add(existsBox);
		quantificationRadioButtonGroup.add(forAllDiamond);
		quantificationRadioButtonGroup.add(forAllBox);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		queryPanel.add(existsDiamond, gridBagConstraints);
		if (queryToCreateFrom==null){
			existsDiamond.setSelected(true);
		}else {
			if (queryToCreateFrom.query.contains("E<>")){
				existsDiamond.setSelected(true);
			} else if (queryToCreateFrom.query.contains("E[]")){
				existsBox.setSelected(true);
			} else if (queryToCreateFrom.query.contains("A<>")){
				forAllDiamond.setSelected(true);
			} else if (queryToCreateFrom.query.contains("A[]")){
				forAllBox.setSelected(true);
			}
		}



		gridBagConstraints.gridy = 1;
		queryPanel.add(existsBox, gridBagConstraints);

		gridBagConstraints.gridy = 2;
		queryPanel.add(forAllDiamond, gridBagConstraints);

		gridBagConstraints.gridy = 3;
		queryPanel.add(forAllBox, gridBagConstraints);

		//Add action listeners to the query options
		for (Object radioButton : queryPanel.getComponents()){

			if( (radioButton instanceof JRadioButton)){


				((JRadioButton)radioButton).addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						//Update stuff
						quantificationRadioButtonChanged(arg0);
					}

				});
			}
		}



		markingSpecificsPanel = new JPanel(new GridBagLayout());

		if (queryToCreateFrom==null){
			addNewDisjunction();
		}else {
			addQuery(queryToCreateFrom);	
		}

		gridBagConstraints.gridy = 4;
		queryPanel.add( markingSpecificsPanel , gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		add(queryPanel, gridBagConstraints);

		//verification option-radio buttons starts here:		
		uppaalOptions = new JPanel(new GridBagLayout());

		searchOptions = new JPanel(new GridBagLayout());
		searchOptions.setBorder(BorderFactory.createTitledBorder("Analysis Options"));
		searchRadioButtonGroup = new ButtonGroup();
		bFS = new JRadioButton("Breadth First Search");
		dFS = new JRadioButton("Depth First Search");
		rDFS = new JRadioButton("Random Depth First Search");
		closestToTargetFirst = new JRadioButton("Search by Closest To Target First");
		searchRadioButtonGroup.add(bFS);
		searchRadioButtonGroup.add(dFS);
		searchRadioButtonGroup.add(rDFS);
		searchRadioButtonGroup.add(closestToTargetFirst);

		if (queryToCreateFrom==null){
			bFS.setSelected(true);
		}else{
			if (queryToCreateFrom.searchOption == SearchOption.BFS){
				bFS.setSelected(true);
			} else if (queryToCreateFrom.searchOption == SearchOption.DFS){
				dFS.setSelected(true);
			} else if (queryToCreateFrom.searchOption == SearchOption.RDFS){
				rDFS.setSelected(true);
			} else if (queryToCreateFrom.searchOption == SearchOption.CLOSE_TO_TARGET_FIRST){
				closestToTargetFirst.setSelected(true);
			}	
		}

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.gridy = 0;
		searchOptions.add(bFS,gridBagConstraints);
		gridBagConstraints.gridy = 1;
		searchOptions.add(dFS,gridBagConstraints);
		gridBagConstraints.gridy = 2;
		searchOptions.add(rDFS,gridBagConstraints);
		gridBagConstraints.gridy = 3;
		searchOptions.add(closestToTargetFirst,gridBagConstraints);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		uppaalOptions.add(searchOptions, gridBagConstraints);

		traceOptions = new JPanel(new GridBagLayout());
		traceOptions.setBorder(BorderFactory.createTitledBorder("Trace Options"));
		traceRadioButtonGroup = new ButtonGroup();
		some = new JRadioButton("Some encountered trace (only without symmetry reduction)");
		fastest = new JRadioButton("Fastest trace (only without symmetry reduction)");
		none = new JRadioButton("No trace");
		traceRadioButtonGroup.add(some);
		traceRadioButtonGroup.add(fastest);
		traceRadioButtonGroup.add(none);

		if (queryToCreateFrom==null){
			none.setSelected(true);
		}else{
			if (queryToCreateFrom.traceOption == TraceOption.SOME){
				some.setSelected(true);
			} else if (queryToCreateFrom.traceOption == TraceOption.FASTEST){
				fastest.setSelected(true);
			} else if (queryToCreateFrom.traceOption == TraceOption.NONE){
				none.setSelected(true);
			}	
		}

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.gridy = 0;
		traceOptions.add(some,gridBagConstraints);
		gridBagConstraints.gridy = 1;
		traceOptions.add(fastest,gridBagConstraints);
		gridBagConstraints.gridy = 2;
		traceOptions.add(none,gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		uppaalOptions.add(traceOptions, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		add(uppaalOptions, gridBagConstraints);

		//ReductionOptions starts here:
		this.reductionOptions = new JPanel(new FlowLayout());
		this.reductionOptions.setBorder(BorderFactory.createTitledBorder("Reduction Options"));
		String[] reductionOptions = {name_NAIVE, name_ADVNOSYM, name_BROADCAST, name_BROADCASTDEG2};
		reductionOption = new JComboBox(reductionOptions);
		reductionOption.setSelectedIndex(3);


		this.reductionOptions.add(new JLabel("  Choose reduction method:"));
		this.reductionOptions.add(reductionOption);

		this.symmetryReduction = new JCheckBox("Use Symmetry Reduction");
		this.symmetryReduction.setSelected(true);
		this.symmetryReduction.addItemListener(new ItemListener(){


			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					disableTraceOptions();
				}else{
					enableTraceOptions();
				}

			}

		});

		this.reductionOptions.add(symmetryReduction);
		disableTraceOptions();

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		add(this.reductionOptions, gridBagConstraints);

		//add save and verify buttons starts here:
		buttonPanel = new JPanel(new FlowLayout());
		if (option == QueryDialogueOption.Save){
			okButton = new JButton("Save");
			verifyButton = new JButton("Save and Verify");
			cancelButton = new JButton("Cancel");
			removeButton = new JButton("Remove");
			saveUppaalXMLButton = new JButton("Save UPPAAL XML");

			okButton.addActionListener(	
					new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							//TODO make save
							//save();
							querySaved = true;
							exit();
						}
					}
			);
			verifyButton.addActionListener(	
					new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							querySaved = true;
							exit();
							Verifier.runUppaalVerification(CreateGui.getModel(), getQuery());
						}
					}
			);
			cancelButton.addActionListener(	
					new ActionListener() {
						public void actionPerformed(ActionEvent evt) {

							exit();
						}
					}
			);
			removeButton.addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent evt) {

							datalayer.getQueries().remove(queryToCreateFrom);
							CreateGui.createLeftPane();
							exit();
						}
					}
			);
			saveUppaalXMLButton.addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							querySaved = true;

							String xmlFile = null, queryFile = null;
							try {
								xmlFile = new FileBrowser("Uppaal XML","xml",xmlFile).saveFile();
								String[] a = xmlFile.split(".xml");
								queryFile= a[0]+".q";

							} catch (Exception ex) {
								JOptionPane.showMessageDialog(CreateGui.getApp(),
										"There were errors performing the requested action:\n" + e,
										"Error", JOptionPane.ERROR_MESSAGE
								);				
							}

							if(xmlFile != null && queryFile != null){
								Export.exportUppaalXMLFromQuery(CreateGui.getModel(), getQuery(), xmlFile, queryFile);
							}else{
								JOptionPane.showMessageDialog(CreateGui.getApp(), "No Uppaal XML file saved.");
							}
						}
					}
			);
		}else if (option == QueryDialogueOption.Export){
			okButton = new JButton("export");
			cancelButton = new JButton("Cancel");

			okButton.addActionListener(	
					new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							querySaved = true;
							exit();
						}
					}
			);
			cancelButton.addActionListener(	
					new ActionListener() {
						public void actionPerformed(ActionEvent evt) {

							exit();
						}
					}
			);		
		}
		if (option == QueryDialogueOption.Save){
			buttonPanel.add(cancelButton);

			if (queryToCreateFrom!=null){
				buttonPanel.add(removeButton);	
			}

			buttonPanel.add(okButton);

			buttonPanel.add(verifyButton);

			buttonPanel.add(saveUppaalXMLButton);
		}else {
			buttonPanel.add(cancelButton);

			buttonPanel.add(okButton);

			//			buttonPanel.add(saveUppaalXMLButton);
		}


		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		add(buttonPanel, gridBagConstraints);

		myRootPane.setDefaultButton(okButton);

		quantificationRadioButtonChanged(null);

		//Update the selected reduction
		if (queryToCreateFrom!=null){
			String reduction = "";
			boolean symmetry = false;

			if(queryToCreateFrom.reductionOption == ReductionOption.BROADCAST_STANDARD){
				reduction = name_BROADCAST;
				symmetry = false;
				//enableTraceOptions();
			}else if(queryToCreateFrom.reductionOption == ReductionOption.BROADCAST_SYM){
				reduction = name_BROADCAST;
				symmetry = true;
				//disableTraceOptions();
			}else if(queryToCreateFrom.reductionOption == ReductionOption.BROADCAST_DEG2){
				reduction = name_BROADCASTDEG2;
				symmetry = false;
				//disableTraceOptions();
			}else if(queryToCreateFrom.reductionOption == ReductionOption.BROADCAST_DEG2_SYM){
				reduction = name_BROADCASTDEG2;
				symmetry = true;
				//disableTraceOptions();
			}
			//			else if(queryToCreateFrom.reductionOption == ReductionOption.ADV_BROADCAST_SYM){
			//				reductionOption.setSelectedItem(name_ADVBROADCASTSYM);
			//				disableTraceOptions();
			//			} else if(queryToCreateFrom.reductionOption == ReductionOption.OPT_BROADCAST_SYM){
			//				reductionOption.setSelectedItem(name_OPTBROADCASTSYM);
			//				disableTraceOptions();
			//			}else if(queryToCreateFrom.reductionOption == ReductionOption.SUPER_BROADCAST_SYM){
			//				reductionOption.setSelectedItem(name_SUPERBROADCASTSYM);
			//				disableTraceOptions();
			//			}
			else if (getQuantificationSelection().equals("E<>") || getQuantificationSelection().equals("A[]")){
				if (queryToCreateFrom.reductionOption == ReductionOption.NAIVE){
					reduction = name_NAIVE;
					symmetry = false;
					//enableTraceOptions();
				} else if (queryToCreateFrom.reductionOption == ReductionOption.NAIVE_UPPAAL_SYM){
					reduction = name_NAIVE;
					symmetry = true;
					//disableTraceOptions();
				} else if (queryToCreateFrom.reductionOption == ReductionOption.ADV_UPPAAL_SYM){
					reduction = name_ADVNOSYM;
					symmetry = true;
					//disableTraceOptions();
				} else if (queryToCreateFrom.reductionOption == ReductionOption.ADV_NOSYM){
					reduction = name_ADVNOSYM;
					symmetry = false;
					//enableTraceOptions();
				}
			} else {
				if (queryToCreateFrom.reductionOption == ReductionOption.ADV_UPPAAL_SYM){
					reduction = name_ADVNOSYM;
					symmetry = true;
					//disableTraceOptions();
				} else if (queryToCreateFrom.reductionOption == ReductionOption.ADV_NOSYM){
					reduction = name_ADVNOSYM;
					symmetry = false;
					//enableTraceOptions();
				}
			}

			reductionOption.setSelectedItem(reduction);
			symmetryReduction.setSelected(symmetry);
		}
	}

	private void disableTraceOptions() {
		some.setEnabled(false);
		fastest.setEnabled(false);
	}

	private void disableLivenessReductionOptions(){
		String[] options = null;
		if(!this.datalayer.hasTAPNInhibitorArcs()){
			options = new String[]{name_ADVNOSYM, name_BROADCAST, name_BROADCASTDEG2};
		}else{
			options = new String[]{name_BROADCAST, name_BROADCASTDEG2};
		}
		reductionOption.removeAllItems();

		for (String s : options){
			reductionOption.addItem(s);
		}
	}

	private void enableAllReductionOptions(){
		reductionOption.removeAllItems();
		if(!this.datalayer.hasTAPNInhibitorArcs()){
			String[] options = {name_NAIVE, name_ADVNOSYM, name_BROADCAST, name_BROADCASTDEG2};

			for (String s : options){
				reductionOption.addItem(s);
			}
		}else {
			//reductionOption.addItem(name_INHIBSTANDARD);
			//reductionOption.addItem(name_INHIBSYM);
			reductionOption.addItem(name_BROADCAST);
			//reductionOption.addItem(name_BROADCASTSYM);
			reductionOption.addItem(name_BROADCASTDEG2);
			//reductionOption.addItem(name_BROADCASTDEG2SYM);
			//reductionOption.addItem(name_ADVBROADCASTSYM);
			//reductionOption.addItem(name_OPTBROADCAST);
			//reductionOption.addItem(name_OPTBROADCASTSYM);
			//reductionOption.addItem(name_SUPERBROADCAST);
			//reductionOption.addItem(name_SUPERBROADCASTSYM);
		}
	}

	private void enableTraceOptions() {
		some.setEnabled(true);
		fastest.setEnabled(true);
	}

	private void addQuery(TAPNQuery queryToCreateFrom) {
		String[] disjunctions = queryToCreateFrom.query.subSequence(3, queryToCreateFrom.query.length()).toString().split(" \\|\\| ") ;
		//		System.out.println(queryToCreateFrom.query);
		//		for (String s : disjunctions){
		//			System.out.println(s);
		//		}

		for (int i=0; i < disjunctions.length; i++){			
			addNewDisjunction();
			if (i>0){
				((JButton)disjunctionGroups.get(i-1).getComponent(1)).setSelected(true);
			}

			String strippedDisjunction = "";

			for (int j=3; j < disjunctions[i].length()-3; j++){
				strippedDisjunction = strippedDisjunction + disjunctions[i].charAt(j);
			}
			//			System.out.println(strippedDisjunction);

			if (disjunctions[i].contains("&&")){
				String[] conjunctions = strippedDisjunction.split(" \\) \\&\\& \\( ");
				for (int j=0; j < conjunctions.length-1; j++){
					createNewConjunction(i);	
				}
				for (int j=0; j < conjunctions.length; j++){
					String[] partedConjunction = partedComparison(conjunctions[j]);
					//					for (String s : partedConjunction){
					//						System.out.println(s);
					//					}

					//set the placeDropdown to the correct place
					((JComboBox)conjunctionGroups.get(i).get(j).getComponent(0)).setSelectedItem(partedConjunction[0]);					

					//set the relational symbol
					((JComboBox)conjunctionGroups.get(i).get(j).getComponent(1)).setSelectedItem(partedConjunction[1]);

					//set the size
					((JSpinner)conjunctionGroups.get(i).get(j).getComponent(2)).setValue(Integer.parseInt(partedConjunction[2]));

					//set andButton to pressed
					if (j<conjunctions.length-1){
						((JButton)conjunctionGroups.get(i).get(j).getComponent(3)).setSelected(true);
					}
				}

			}else{
				String[] partedConjunction = partedComparison(strippedDisjunction);

				//set the placeDropdown to the correct place
				((JComboBox)conjunctionGroups.get(i).get(0).getComponent(0)).setSelectedItem(partedConjunction[0]);					

				//set the relational symbol
				((JComboBox)conjunctionGroups.get(i).get(0).getComponent(1)).setSelectedItem(partedConjunction[1]);

				//set the size
				((JSpinner)conjunctionGroups.get(i).get(0).getComponent(2)).setValue(Integer.parseInt(partedConjunction[2]));
			}
		}
	}

	private String[] partedComparison(String comparison){
		String[] toReturn = {"","",""};
		if (comparison.contains("<=")){
			toReturn[1] = "<=";
			String[] placeAndSize = comparison.split("\\<\\=");
			for (int i = 0; i < placeAndSize.length; i++) {
				placeAndSize[i] = placeAndSize[i].trim();
			}
			toReturn[0] = placeAndSize[0];
			toReturn[2] = placeAndSize[1];
		}else if (comparison.contains(">=")){
			toReturn[1] = ">=";
			String[] placeAndSize = comparison.split("\\>\\=");
			for (int i = 0; i < placeAndSize.length; i++) {
				placeAndSize[i] = placeAndSize[i].trim();
			}
			toReturn[0] = placeAndSize[0];
			toReturn[2] = placeAndSize[1];
		}else if (comparison.contains("=")){
			toReturn[1] = "=";
			String[] placeAndSize = comparison.split("\\=\\=");
			for (int i = 0; i < placeAndSize.length; i++) {
				placeAndSize[i] = placeAndSize[i].trim();
			}
			toReturn[0] = placeAndSize[0];
			toReturn[2] = placeAndSize[1];
		}else if (comparison.contains("<")){
			toReturn[1] = "<";
			String[] placeAndSize = comparison.split("\\<");
			for (int i = 0; i < placeAndSize.length; i++) {
				placeAndSize[i] = placeAndSize[i].trim();
			}
			toReturn[0] = placeAndSize[0];
			toReturn[2] = placeAndSize[1];
		}else if (comparison.contains(">")){
			toReturn[1] = ">";
			String[] placeAndSize = comparison.split("\\>");
			for (int i = 0; i < placeAndSize.length; i++) {
				placeAndSize[i] = placeAndSize[i].trim();
			}
			toReturn[0] = placeAndSize[0];
			toReturn[2] = placeAndSize[1];
		}		
		return toReturn;
	}

	public TAPNQuery getQuery() {
		if (!querySaved){
			return null;
		}
		String name = getQueryComment();
		int capacity = getCapacity();
		String query = composeQuery();

		TAPNQuery.TraceOption traceOption = null;
		String traceOptionString = getTraceOptions();

		if (traceOptionString.toLowerCase().contains("some")){
			traceOption = TraceOption.SOME;
		}else if (traceOptionString.toLowerCase().contains("fastest")){
			traceOption = TraceOption.FASTEST;
		}else if (traceOptionString.toLowerCase().contains("no")){
			traceOption = TraceOption.NONE;
		}

		TAPNQuery.SearchOption searchOption = null;
		String searchOptionString = getSearchOptions();

		if (searchOptionString.toLowerCase().contains("breadth")){
			searchOption = SearchOption.BFS;
		}else if (searchOptionString.toLowerCase().contains("depth") && (! searchOptionString.toLowerCase().contains("random")) ){
			searchOption = SearchOption.DFS;
		}else if (searchOptionString.toLowerCase().contains("depth") && searchOptionString.toLowerCase().contains("random") ){
			searchOption = SearchOption.RDFS;
		}else if (searchOptionString.toLowerCase().contains("target")){
			searchOption = SearchOption.CLOSE_TO_TARGET_FIRST;
		}

//		 these options are not yet supported		
//		TAPNQuery.HashTableSize hashTableSizeToSet = null;
//		String hashTableOptionString = getHashTableSize();
//		if (hashTableOptionString.equals("4 MB")){
//			hashTableSizeToSet = HashTableSize.MB_4;
//		}else if (hashTableOptionString.equals("16 MB")){
//			hashTableSizeToSet = HashTableSize.MB_16;
//		}else if (hashTableOptionString.equals("64 MB")){
//			hashTableSizeToSet = HashTableSize.MB_64;
//		}else if (hashTableOptionString.equals("256 MB")){
//			hashTableSizeToSet = HashTableSize.MB_256;
//		}else if (hashTableOptionString.equals("512 MB")){
//			hashTableSizeToSet = HashTableSize.MB_512;
//		}
//
//		TAPNQuery.ExtrapolationOption extrapolationOptionToSet = null;
//		String extrapolationOptionString = getExtrapolationOption();
//		if (extrapolationOptionString.toLowerCase().equals("automatic")){
//			extrapolationOptionToSet = ExtrapolationOption.AUTOMATIC;
//		}else if (extrapolationOptionString.toLowerCase().equals("use difference extrapolation")){
//			extrapolationOptionToSet = ExtrapolationOption.DIFF;
//		}else if (extrapolationOptionString.toLowerCase().equals("use location based extrapolation")){
//			extrapolationOptionToSet = ExtrapolationOption.LOCAL;
//		}else if (extrapolationOptionString.toLowerCase().equals("use lower/upper extrapolation")){
//			extrapolationOptionToSet = ExtrapolationOption.LOW_UP;
//		}
//		 		
		TAPNQuery.ReductionOption reductionOptionToSet = null;
		String reductionOptionString = ""+reductionOption.getSelectedItem();
		boolean symmetry = symmetryReduction.isSelected();

		if (reductionOptionString.equals(name_NAIVE) && !symmetry){
			reductionOptionToSet = ReductionOption.NAIVE;
		}else if(reductionOptionString.equals(name_NAIVE) && symmetry){
			reductionOptionToSet = ReductionOption.NAIVE_UPPAAL_SYM;
		}else if (reductionOptionString.equals(name_ADVNOSYM) && !symmetry){
			reductionOptionToSet = ReductionOption.ADV_NOSYM;
		}else if (reductionOptionString.equals(name_ADVNOSYM) && symmetry){
			reductionOptionToSet = ReductionOption.ADV_UPPAAL_SYM;
		}else if(reductionOptionString.equals(name_BROADCAST) && !symmetry){
			reductionOptionToSet = ReductionOption.BROADCAST_STANDARD;
		}else if(reductionOptionString.equals(name_BROADCAST) && symmetry){
			reductionOptionToSet = ReductionOption.BROADCAST_SYM;
		}else if(reductionOptionString.equals(name_BROADCASTDEG2) && !symmetry){
			reductionOptionToSet = ReductionOption.BROADCAST_DEG2;
		}else if(reductionOptionString.equals(name_BROADCASTDEG2) && symmetry){
			reductionOptionToSet = ReductionOption.BROADCAST_DEG2_SYM;
		}
		//		else if (reductionOptionString.equals(name_NAIVESYM)){
		//			reductionOptionToSet = ReductionOption.NAIVE_UPPAAL_SYM;
		//		}else if (reductionOptionString.equals(name_ADVSYM)){
		//			reductionOptionToSet = ReductionOption.ADV_UPPAAL_SYM;
		//		}else if(reductionOptionString.equals(name_INHIBSTANDARD)){
		//			reductionOptionToSet = ReductionOption.INHIB_TO_PRIO_STANDARD;
		//		}else if(reductionOptionString.equals(name_INHIBSYM)){
		//			reductionOptionToSet = ReductionOption.INHIB_TO_PRIO_SYM;
		//		}else if(reductionOptionString.equals(name_BROADCASTSYM)){
		//			reductionOptionToSet = ReductionOption.BROADCAST_SYM;
		//		}else if(reductionOptionString.equals(name_BROADCASTDEG2SYM)){
		//			reductionOptionToSet = ReductionOption.BROADCAST_DEG2_SYM;
		//		}else if(reductionOptionString.equals(name_ADVBROADCASTSYM)){
		//			reductionOptionToSet = ReductionOption.ADV_BROADCAST_SYM;
		//		}else if(reductionOptionString.equals(name_OPTBROADCASTSYM)){
		//			reductionOptionToSet = ReductionOption.OPT_BROADCAST_SYM;
		//		}else if(reductionOptionString.equals(name_SUPERBROADCASTSYM)){
		//			reductionOptionToSet = ReductionOption.SUPER_BROADCAST_SYM;
		//		}else if(reductionOptionString.equals(name_BROADCASTDEG2)){
		//			reductionOptionToSet = ReductionOption.BROADCAST_DEG2;
		//		}else if(reductionOptionString.equals(name_OPTBROADCAST)){
		//			reductionOptionToSet = ReductionOption.OPT_BROADCAST;
		//		}else if(reductionOptionString.equals(name_SUPERBROADCAST)){
		//			reductionOptionToSet = ReductionOption.SUPER_BROADCAST;
		//		}

//		return new TAPNQuery(name, capacity, query, traceOption, searchOption, reductionOptionToSet, hashTableSizeToSetnull, extrapolationOptionToSet null);

	}

	private int getCapacity(){
		return (Integer) ((JSpinner)capacityPanel.getComponent(1)).getValue();
	}

	private String getQueryComment() {
		return ((JTextField)namePanel.getComponent(1)).getText();
	}

	private String getTraceOptions() {
		String toReturn = null;
		for (Object radioButton : traceOptions.getComponents()){
			if( (radioButton instanceof JRadioButton)){
				if ( ((JRadioButton)radioButton).isSelected() ){
					toReturn = ((JRadioButton)radioButton).getText();
					break;
				}
			}
		}
		return toReturn;
	}

	private String getSearchOptions() {
		String toReturn = null;
		for (Object radioButton : searchOptions.getComponents()){
			if( (radioButton instanceof JRadioButton)){
				if ( ((JRadioButton)radioButton).isSelected() ){

					toReturn = ((JRadioButton)radioButton).getText();
					break;
				}
			}
		}
		return toReturn;
	}

	private String composeQuery() {
		String toReturn = "";

		if (existsDiamond.isSelected()){
			toReturn = "E<>";
		}else if (existsBox.isSelected()){
			toReturn = "E[]";
		}else if (forAllDiamond.isSelected()){
			toReturn = "A<>";
		}else if (forAllBox.isSelected()){
			toReturn = "A[]";
		}

		for (int i=0; i < conjunctionGroups.size(); i++){
			toReturn = toReturn + "(";
			for (int j=0; j < conjunctionGroups.get(i).size(); j++){

				toReturn = toReturn + "( " + ((JComboBox)conjunctionGroups.get(i).get(j).getComponent(0)).getSelectedItem() + " ";
				if (((String)((JComboBox)conjunctionGroups.get(i).get(j).getComponent(1)).getSelectedItem()).equals("=")){
					toReturn = toReturn + "== ";	
				}else {
					toReturn = toReturn + ((JComboBox)conjunctionGroups.get(i).get(j).getComponent(1)).getSelectedItem() + " ";
				}
				toReturn = toReturn + ((JSpinner)conjunctionGroups.get(i).get(j).getComponent(2)).getValue() + " ";
				if(j < conjunctionGroups.get(i).size()-1 ){
					toReturn = toReturn + ") && ";
				}else{
					toReturn = toReturn + ")";
				}
			}
			if (i < conjunctionGroups.size()-1){
				toReturn = toReturn + ") || ";
			}else {
				toReturn = toReturn + ")";
			}
		}

		return toReturn;
	}

	private void exit(){
		myRootPane.getParent().setVisible(false);
	}

	private void addNewDisjunction() {
		JPanel disjunctionGroup = newDisjunction();
		disjunctionGroups.add(disjunctionGroup);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = disjunctionsUsed;
		gbc.anchor = GridBagConstraints.WEST;	
		markingSpecificsPanel.add(disjunctionGroup, gbc);

		conjunctionsUsed.add(new Integer(0));
		disjunctionsUsed++;

		me.pack();
	}

	private void removeDisjunction(int number){
		JPanel disjunctionGroupToRemove = disjunctionGroups.get(number+1);
		disjunctionGroupToRemove.setVisible(false);
		disjunctionGroups.remove(disjunctionGroupToRemove);
		conjunctionGroups.remove(number+1);
		disjunctionGroupToRemove.removeNotify();
		disjunctionGroups.trimToSize();

		me.pack();
	}

	private void createNewConjunction(int inDisjunction){

		JPanel conjunctionGroup = newConjunction(new Integer(inDisjunction));

		conjunctionGroups.get(inDisjunction).add(conjunctionGroup);

		conjunctionsUsed.set(inDisjunction, conjunctionsUsed.get(inDisjunction) + 1);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = conjunctionsUsed.get(inDisjunction);
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		((JPanel)markingSpecificsPanel.getComponent(inDisjunction)).add(conjunctionGroup, gbc);

		me.pack();
	}
	private void removeConjunction(JPanel conjunctionGroupToRemove){		
		conjunctionGroupToRemove.setVisible(false);
		for (ArrayList<JPanel> disjunctionGroup : conjunctionGroups){
			if (disjunctionGroup.contains(conjunctionGroupToRemove)){
				disjunctionGroup.remove(conjunctionGroupToRemove);
				conjunctionGroupToRemove.removeNotify();
				disjunctionGroup.trimToSize();

				me.pack();
			}
		}
	}

	private JPanel newDisjunction(){
		JPanel disjunctionGroup = new JPanel(new GridBagLayout());

		JPanel conjunctionGroup = newConjunction(disjunctionsUsed);

		ArrayList<JPanel> conjunctionToInsert = new ArrayList<JPanel>();
		conjunctionToInsert.add(conjunctionGroup);
		conjunctionGroups.add(conjunctionToInsert);

		GridBagConstraints gbc = new GridBagConstraints();
		disjunctionGroup.add(conjunctionGroup, gbc);

		final JButton orButton = new JButton("OR");

		orButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent evt) {
						for (int i=0; i < disjunctionGroups.size(); i++) {
							//component 1 is a button, with "OR" on it
							if (disjunctionGroups.get(i).getComponent(1) == orButton){

								if ( ! orButton.isSelected() ){
									orButton.setSelected(true);
									//add a new disjunctionGroup.
									addNewDisjunction();
									break;
								}else {
									removeDisjunction(i);
									if ( i < disjunctionGroups.size()-1){
										break;
									}else {
										orButton.setSelected(false);
										break;
									}
								}
							}
						}
					}
				});

		//XXX - hacks
		gbc.gridy = 10;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		disjunctionGroup.add(orButton, gbc);

		return disjunctionGroup;
	}

	private JPanel newConjunction(final int disjunctionIndex){

		final JPanel conjunctionGroup = new JPanel(new FlowLayout());

		String[] relationalSymbols= {"=","<=","<",">=",">"};
		int currentValue = 0;
		int min = 0;
		int step = 1;		

		String[] places = new String[datalayer.getPlaces().length];
		for (int i=0; i< places.length; i++){
			places[i] = datalayer.getPlaces()[i].getName();
		}
		JComboBox placesBox = new JComboBox(new DefaultComboBoxModel(places));
		conjunctionGroup.add( placesBox );

		JComboBox relationalBox = new JComboBox(new DefaultComboBoxModel(relationalSymbols));
		conjunctionGroup.add(relationalBox);

		JSpinner placeMarking = new JSpinner(new SpinnerNumberModel(currentValue, min, Integer.MAX_VALUE, step));
		placeMarking.setMaximumSize(new Dimension(50,30));
		placeMarking.setMinimumSize(new Dimension(50,30));
		placeMarking.setPreferredSize(new Dimension(50,30));
		//		placeMarking.setVisible(visibility);

		conjunctionGroup.add(placeMarking);

		final JButton andButton = new JButton("AND");
		//		andButton.setVisible(visibility);

		andActionListenerMap.put(conjunctionGroup, 

				new ActionListener() {
			final int thisDisjunctionIndex = disjunctionIndex;
			final JPanel thisConjunctionGroup = conjunctionGroup;

			public void actionPerformed(ActionEvent evt) {

				JButton and = (JButton) thisConjunctionGroup.getComponent(3);

				if ( ! and.isSelected() ){
					and.setSelected(true);
					//add a new conjunction in the orGroupIndex'th disjunction.
					createNewConjunction(thisDisjunctionIndex);
				}else {
					removeConjunction( nextConjunctionGroup(thisConjunctionGroup) );
					if ( conjunctionGroups.get(thisDisjunctionIndex).indexOf(thisConjunctionGroup)+1 == conjunctionGroups.get(thisDisjunctionIndex).size() ){
						and.setSelected(false);
					}
				}								
			}
		}

		);

		andButton.addActionListener(andActionListenerMap.get(conjunctionGroup));

		conjunctionGroup.add(andButton);

		return conjunctionGroup;
	}


	protected JPanel nextConjunctionGroup(JPanel thisConjunctionGroup) {
		for (ArrayList<JPanel> disjunctionGroup : conjunctionGroups){
			if (disjunctionGroup.contains(thisConjunctionGroup)){
				return disjunctionGroup.get( disjunctionGroup.indexOf(thisConjunctionGroup)+1 );
			}
		}
		return null;
	}

	public static TAPNQuery ShowUppaalQueryDialogue(QueryDialogueOption option, TAPNQuery queryToRepresent){
		EscapableDialog guiDialog = 
			new EscapableDialog(CreateGui.getApp(), Pipe.getProgramName(), true);

		Container contentPane = guiDialog.getContentPane();

		// 1 Set layout
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));      

		// 2 Add query editor
		QueryDialogue queryDialogue = new QueryDialogue(guiDialog, CreateGui.getModel(), option, queryToRepresent); 
		contentPane.add( queryDialogue );

		guiDialog.setResizable(false);     

		// Make window fit contents' preferred size
		guiDialog.pack();

		// Move window to the middle of the screen
		guiDialog.setLocationRelativeTo(null);
		guiDialog.setVisible(true);

		return  queryDialogue.getQuery();
	}


	public String getQuantificationSelection() {
		if (existsDiamond.isSelected()){
			return "E<>";
		}else if (existsBox.isSelected()){
			return "E[]";
		}else if (forAllDiamond.isSelected()){
			return "A<>";
		}else if (forAllBox.isSelected()){
			return "A[]";
		} else {
			return "";
		}
	}

	private void quantificationRadioButtonChanged(ActionEvent arg0) {
		// 
		if (getQuantificationSelection().equals("E[]") || getQuantificationSelection().equals("A<>")) {
			disableLivenessReductionOptions();
		} else {
			enableAllReductionOptions();
		}

	}
	*/
}