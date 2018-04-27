package pipe.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dk.aau.cs.gui.TabTransformer;
import dk.aau.cs.model.tapn.*;
import javafx.scene.control.Toggle;
import net.tapaal.Preferences;
import com.sun.jna.Platform;

import net.tapaal.TAPAAL;
import pipe.dataLayer.DataLayer;
import pipe.dataLayer.NetType;
import pipe.dataLayer.NetWriter;
import pipe.dataLayer.TAPNQuery;
import pipe.dataLayer.Template;
import pipe.gui.Pipe.ElementType;
import pipe.gui.action.GuiAction;
import pipe.gui.graphicElements.Arc;
import pipe.gui.graphicElements.ArcPathPoint;
import pipe.gui.graphicElements.PetriNetObject;
import pipe.gui.graphicElements.PlaceTransitionObject;
import pipe.gui.graphicElements.Transition;
import pipe.gui.graphicElements.tapn.TimedPlaceComponent;
import pipe.gui.graphicElements.tapn.TimedTransitionComponent;
import pipe.gui.handler.SpecialMacHandler;
import pipe.gui.undo.ChangeSpacingEdit;
import pipe.gui.widgets.EngineDialogPanel;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.FileBrowser;
import pipe.gui.widgets.NewTAPNPanel;
import pipe.gui.widgets.QueryDialog;
import pipe.gui.widgets.QueryPane;
import pipe.gui.widgets.WorkflowDialog;
import dk.aau.cs.debug.Logger;
import dk.aau.cs.gui.BatchProcessingDialog;
import dk.aau.cs.gui.TabComponent;
import dk.aau.cs.gui.TabContent;
import dk.aau.cs.gui.components.StatisticsPanel;
import dk.aau.cs.gui.undo.Command;
import dk.aau.cs.gui.undo.DeleteQueriesCommand;
import dk.aau.cs.io.LoadedModel;
import dk.aau.cs.io.ModelLoader;
import dk.aau.cs.io.PNMLoader;
import dk.aau.cs.io.ResourceManager;
import dk.aau.cs.io.TimedArcPetriNetNetworkWriter;
import dk.aau.cs.io.TraceImportExport;
import dk.aau.cs.io.queries.SUMOQueryLoader;
import dk.aau.cs.io.queries.XMLQueryLoader;
import dk.aau.cs.model.tapn.simulation.ShortestDelayMode;
import dk.aau.cs.verification.UPPAAL.Verifyta;
import dk.aau.cs.verification.VerifyTAPN.VerifyTAPN;
import dk.aau.cs.verification.VerifyTAPN.VerifyTAPNDiscreteVerification;


public class GuiFrame extends JFrame implements Observer {

	private static final long serialVersionUID = 7509589834941127217L;
	// for zoom combobox and dropdown
	private final String[] zoomExamples = { "40%", "60%", "80%", "100%",
			"120%", "140%", "160%", "180%", "200%", "300%" };
	
	private String frameTitle;
	private GuiFrame appGui;
	private DrawingSurfaceImpl appView;
	private Pipe.ElementType mode, prev_mode; 
	private int newNameCounter = 1;
	private JTabbedPane appTab;
	private StatusBar statusBar;
	private JMenuBar menuBar;
	private JToolBar drawingToolBar;
	private JComboBox<String> zoomComboBox;

	private GuiAction createAction;
	private GuiAction openAction;
	private GuiAction closeAction;
	private GuiAction saveAction;
	private GuiAction saveAsAction;
	private GuiAction exitAction;
	private GuiAction printAction;
	private GuiAction importPNMLAction;
	private GuiAction importSUMOAction;
	private GuiAction importXMLAction;
	private GuiAction exportPNGAction;
	private GuiAction exportPSAction;
	private GuiAction exportToTikZAction;
	private GuiAction exportToPNMLAction;
	private GuiAction exportToXMLAction;
	private GuiAction exportTraceAction;
	private GuiAction importTraceAction;

	private EditAction /* copyAction, cutAction, pasteAction, */undoAction, redoAction;
	private GuiAction toggleGrid;
	private ToolAction netStatisticsAction;
	private ToolAction batchProcessingAction;
	private ToolAction engineSelectionAction;
	private ToolAction verifyAction;
	private ToolAction workflowDialogAction;
	private ToolAction stripTimeDialogAction;
	private ZoomAction zoomOutAction, zoomInAction;

	private GuiAction incSpacingAction;
	private GuiAction decSpacingAction;
	private GuiAction deleteAction;

	private TypeAction annotationAction;
	private TypeAction inhibarcAction;
	private TypeAction transAction;
	private TypeAction tokenAction;
	private TypeAction selectAction;
	private TypeAction deleteTokenAction;
	private TypeAction timedPlaceAction;
	private TypeAction timedArcAction;
	private TypeAction transportArcAction;

	private ViewAction showTokenAgeAction;
	private ViewAction showComponentsAction;
	private ViewAction showQueriesAction;
	private ViewAction showConstantsAction;
	private ViewAction showZeroToInfinityIntervalsAction;
	private ViewAction showEnabledTransitionsAction;
	private ViewAction showDelayEnabledTransitionsAction;
	private ViewAction showToolTipsAction;
	private ViewAction showAdvancedWorkspaceAction;
	private ViewAction showSimpleWorkspaceAction;
	private ViewAction saveWorkSpaceAction;
	private GuiAction showAboutAction;
	private GuiAction showHomepage;
	private GuiAction showAskQuestionAction;
	private GuiAction showReportBugAction;
	private GuiAction showFAQAction;
	private GuiAction checkUpdate;


	private SelectAllAction selectAllAction;

	private JMenuItem statistics;
	private JMenuItem verification;

	public AnimateAction startAction;
	public AnimateAction stepforwardAction;
	public AnimateAction stepbackwardAction;
	private AnimateAction randomAction;
	private AnimateAction randomAnimateAction;
	private AnimateAction timeAction;
	private AnimateAction delayFireAction;
	private AnimateAction prevcomponentAction;
	private AnimateAction nextcomponentAction;

	public enum GUIMode {
		draw, animation, noNet
	}

	private JCheckBoxMenuItem showZeroToInfinityIntervalsCheckBox;
	private JCheckBoxMenuItem showTokenAgeCheckBox;

	private boolean showComponents = true;
	private boolean showConstants = true;
	private boolean showQueries = true;
	private boolean showEnabledTransitions = true;
	private boolean showDelayEnabledTransitions = true;
	private boolean showToolTips = true;

	private GUIMode guiMode = GUIMode.noNet;
	private JMenu importMenu, exportMenu, zoomMenu;


	public boolean isMac(){
		return Platform.isMac();
	}

	//XXX - guess this will break on java 10?
	public int getJRE(){
		return Character.getNumericValue(System.getProperty("java.version").charAt(2));
	}	

	public GuiFrame(String title) {
		// HAK-arrange for frameTitle to be initialized and the default file
		// name to be appended to basic window title

		frameTitle = title;
		setTitle(null);
		try {
			// Set the Look and Feel native for the system.
			setLookAndFeel();
			UIManager.put("OptionPane.informationIcon", ResourceManager.infoIcon());
                        UIManager.put("Slider.paintValue", false);

			// 2010-05-07, Kenneth Yrke Joergensen:
			// If the native look and feel is GTK replace the useless open
			// dialog, with a java-reimplementation.

			if ("GTK look and feel".equals(UIManager.getLookAndFeel().getName())){
				try {
					//Load class to see if its there
					Class.forName("com.google.code.gtkjfilechooser.ui.GtkFileChooserUI", false, this.getClass().getClassLoader());
					UIManager.put("FileChooserUI", "com.google.code.gtkjfilechooser.ui.GtkFileChooserUI");
				} catch (ClassNotFoundException exc){
					Logger.log("Error loading GtkFileChooserUI Look and Feel, using default jvm GTK look and feel instead");
					CreateGui.setUsingGTKFileBrowser(false);
				}

			}


		} catch (Exception exc) {
			Logger.log("Error loading L&F: " + exc);
		}

		if (isMac()){ 
			try {
				new SpecialMacHandler();
			} catch (NoClassDefFoundError e) {
				//Failed loading special mac handler, ignore and run program without MacOS integration
			}
		}

		this.setIconImage(new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(CreateGui.imgPath + "icon.png")).getImage());

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(screenSize.width * 80 / 100, screenSize.height * 80 / 100);
		this.setLocationRelativeTo(null);
		this.setMinimumSize(new Dimension(825, 480));

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		loadPrefrences();

		buildMenus();

		// Status bar...
		statusBar = new StatusBar();
		getContentPane().add(statusBar, BorderLayout.PAGE_END);

		// Build menus
		buildToolbar();

		addWindowListener(new WindowAdapter() {
			// Handler for window closing event
			public void windowClosing(WindowEvent e) {
				exitAction.actionPerformed(null);
			}
		});

		this.setForeground(java.awt.Color.BLACK);

		// Set GUI mode
		setGUIMode(GUIMode.noNet);
	}

	private void loadPrefrences() {
		Preferences prefs = Preferences.getInstance();

		QueryDialog.setAdvancedView(prefs.getAdvancedQueryView());
		TabContent.setEditorModelRoot(prefs.getEditorModelRoot());
		TabContent.setSimulatorModelRoot(prefs.getSimulatorModelRoot());
		showComponents = prefs.getShowComponents();
		showQueries = prefs.getShowQueries();
		showConstants = prefs.getShowConstants();

		showEnabledTransitions = prefs.getShowEnabledTransitions();
		showDelayEnabledTransitions = prefs.getShowDelayEnabledTransitions();
		DelayEnabledTransitionControl.setDefaultDelayMode(prefs.getDelayEnabledTransitionDelayMode());
		DelayEnabledTransitionControl.setDefaultGranularity(prefs.getDelayEnabledTransitionGranularity());
		DelayEnabledTransitionControl.setDefaultIsRandomTransition(prefs.getDelayEnabledTransitionIsRandomTransition());

		showToolTips = prefs.getShowToolTips();

		if(CreateGui.showZeroToInfinityIntervals() != prefs.getShowZeroInfIntervals()){
			CreateGui.toggleShowZeroToInfinityIntervals();
		}

		if(CreateGui.showTokenAge() != prefs.getShowTokenAge()){
			CreateGui.toggleShowTokenAge();
		}

		Dimension dimension = prefs.getWindowSize();
		if(dimension != null){
			this.setSize(dimension);
		}

	}

	private void setLookAndFeel() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		if(UIManager.getLookAndFeel().getName().equals("Windows")){
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					UIManager.getLookAndFeelDefaults().put("List[Selected].textBackground", new Color(57, 105, 138));
					UIManager.getLookAndFeelDefaults().put("List[Selected].textForeground", new Color(255,255,255));
					UIManager.getLookAndFeelDefaults().put("List.background", new Color(255,255,255));
									
					break;
				}
			}
		}

		// Set enter to select focus button rather than default (makes ENTER selection key on all LAFs)
		UIManager.put("Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[]
				{
				"SPACE", "pressed",
				"released SPACE", "released",
				"ENTER", "pressed",
				"released ENTER", "released"
				}));
	}

	/**
	 * This method does build the menus
	 * 
	 * @author Dave Patterson - fixed problem on OSX due to invalid character in
	 *         URI caused by unescaped blank. The code changes one blank
	 *         character if it exists in the string version of the URL. This way
	 *         works safely in both OSX and Windows. I also added a
	 *         printStackTrace if there is an exception caught in the setup for
	 *         the "Example nets" folder.
	 * @author Kenneth Yrke Joergensen <kenneth@yrke.dk>, 2011-06-28
	 * 	       Code cleanup, removed unused parts, Refactored help menu, Fixed 
	 *         loading of Example Nets to work if we create a Jar. 
	 **/
	private void buildMenus() {
		menuBar = new JMenuBar();

		int shortcutkey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		menuBar.add(buildMenuFiles(shortcutkey));
		menuBar.add(buildMenuEdit(shortcutkey));
		menuBar.add(buildMenuView(shortcutkey));
		menuBar.add(buildMenuDraw());
		menuBar.add(buildMenuAnimation());
		menuBar.add(buildMenuTools());
		menuBar.add(buildMenuHelp());

		setJMenuBar(menuBar);

	}

	private JMenu buildMenuEdit(int shortcutkey) {
		/* Edit Menu */
		JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic('E');
		editMenu.add( undoAction = new EditAction("Undo",
				"Undo", KeyStroke.getKeyStroke('Z', shortcutkey)));
		
		
		editMenu.add( redoAction = new EditAction("Redo",
				"Redo", KeyStroke.getKeyStroke('Y', shortcutkey)));
		editMenu.addSeparator();

		editMenu.add( deleteAction = new GuiAction("Delete", "Delete selection", "DELETE") {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// check if queries need to be removed
				ArrayList<PetriNetObject> selection = CreateGui.getView().getSelectionObject().getSelection();
				Iterable<TAPNQuery> queries = ((TabContent) appTab.getSelectedComponent()).queries();
				HashSet<TAPNQuery> queriesToDelete = new HashSet<TAPNQuery>();

				boolean queriesAffected = false;
				for (PetriNetObject pn : selection) {
					if (pn instanceof TimedPlaceComponent) {
						TimedPlaceComponent place = (TimedPlaceComponent)pn;
						if(!place.underlyingPlace().isShared()){
							for (TAPNQuery q : queries) {
								if (q.getProperty().containsAtomicPropositionWithSpecificPlaceInTemplate(((LocalTimedPlace)place.underlyingPlace()).model().name(),place.underlyingPlace().name())) {
									queriesAffected = true;
									queriesToDelete.add(q);
								}
							}
						}
					} else if (pn instanceof TimedTransitionComponent){
						TimedTransitionComponent transition = (TimedTransitionComponent)pn;
						if(!transition.underlyingTransition().isShared()){
							for (TAPNQuery q : queries) {
								if (q.getProperty().containsAtomicPropositionWithSpecificTransitionInTemplate((transition.underlyingTransition()).model().name(),transition.underlyingTransition().name())) {
									queriesAffected = true;
									queriesToDelete.add(q);
								}
							}
						}
					}
				}
				StringBuilder s = new StringBuilder();
				s.append("The following queries are associated with the currently selected objects:\n\n");
				for (TAPNQuery q : queriesToDelete) {
					s.append(q.getName());
					s.append('\n');
				}
				s.append("\nAre you sure you want to remove the current selection and all associated queries?");

				int choice = queriesAffected ? JOptionPane.showConfirmDialog(
						CreateGui.getApp(), s.toString(), "Warning",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
						: JOptionPane.YES_OPTION;

						if (choice == JOptionPane.YES_OPTION) {
							appView.getUndoManager().newEdit(); // new "transaction""
							if (queriesAffected) {
								TabContent currentTab = ((TabContent) CreateGui.getTab().getSelectedComponent());
								for (TAPNQuery q : queriesToDelete) {
									Command cmd = new DeleteQueriesCommand(currentTab, Arrays.asList(q));
									cmd.redo();
									appView.getUndoManager().addEdit(cmd);
								}
							}

							appView.getUndoManager().deleteSelection(appView.getSelectionObject().getSelection());
							appView.getSelectionObject().deleteSelection();
							appView.repaint();
							CreateGui.getCurrentTab().network().buildConstraints();
						}
				
			}
			
		});

		// Bind delete to backspace also
		editMenu.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("BACK_SPACE"), "Delete");
		editMenu.getActionMap().put("Delete", deleteAction);

		editMenu.addSeparator();


		editMenu.add(selectAllAction = new SelectAllAction("Select all", "Select all components", "ctrl A"));
		editMenu.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('A', shortcutkey), "SelectAll");
		editMenu.getActionMap().put("SelectAll", selectAllAction);

		return editMenu;
	}

	private JMenu buildMenuDraw() {
		/* Draw menu */
		JMenu drawMenu = new JMenu("Draw");
		drawMenu.setMnemonic('D');
		drawMenu.add( selectAction = new TypeAction("Select",
				ElementType.SELECT, "Select components (S)", "S", true));
		drawMenu.addSeparator();

		drawMenu.add( timedPlaceAction = new TypeAction("Place",
				ElementType.TAPNPLACE, "Add a place (P)", "P", true));

		drawMenu.add( transAction = new TypeAction("Transition",
				ElementType.TAPNTRANS, "Add a transition (T)", "T", true));

		drawMenu.add( timedArcAction = new TypeAction("Arc",
				ElementType.TAPNARC, "Add an arc (A)", "A", true));

		drawMenu.add( transportArcAction = new TypeAction(
				"Transport arc", ElementType.TRANSPORTARC, "Add a transport arc (R)", "R",
				true));

		drawMenu.add( inhibarcAction = new TypeAction("Inhibitor arc",
				ElementType.TAPNINHIBITOR_ARC, "Add an inhibitor arc (I)", "I", true));

		drawMenu.add(annotationAction = new TypeAction("Annotation",
				ElementType.ANNOTATION, "Add an annotation (N)", "N", true));

		drawMenu.addSeparator();

		drawMenu.add( tokenAction = new TypeAction("Add token",
				ElementType.ADDTOKEN, "Add a token (+)", "typed +", true));

		drawMenu.add( deleteTokenAction = new TypeAction(
				"Delete token", ElementType.DELTOKEN, "Delete a token (-)", "typed -",
				true));
		return drawMenu;
	}

	private JMenu buildMenuView(int shortcutkey) {
		/* ViewMenu */
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic('V');

		zoomMenu = new JMenu("Zoom");
		zoomMenu.setIcon(new ImageIcon(Thread.currentThread()
				.getContextClassLoader().getResource(
						CreateGui.imgPath + "Zoom.png")));
		
		addZoomMenuItems(zoomMenu);

		viewMenu.add( zoomInAction = new ZoomAction("Zoom in",
				"Zoom in by 10% ", KeyStroke.getKeyStroke('J', shortcutkey)));

		viewMenu.add( zoomOutAction = new ZoomAction("Zoom out",
				"Zoom out by 10% ", KeyStroke.getKeyStroke('K', shortcutkey)));
		viewMenu.add(zoomMenu);

		viewMenu.addSeparator();
		
		viewMenu.add(incSpacingAction = new GuiAction("Increase node spacing", "Increase spacing by 20% ",
				KeyStroke.getKeyStroke('U', shortcutkey)) {
			public void actionPerformed(ActionEvent arg0) {
				double factor = 1.25;
				changeSpacing(factor);
				appView.getUndoManager().addNewEdit(new ChangeSpacingEdit(factor));
			}
		});

		viewMenu.add(decSpacingAction = new GuiAction("Decrease node spacing", "Decrease spacing by 20% ",
				KeyStroke.getKeyStroke("shift U")) {
			public void actionPerformed(ActionEvent arg0) {
				double factor = 0.8;
				changeSpacing(factor);
				appView.getUndoManager().addNewEdit(new ChangeSpacingEdit(factor));
			}
		});
		

		
		viewMenu.addSeparator();
		
		viewMenu.add( toggleGrid = new GuiAction("Cycle grid",
				"Change the grid size", "G") {
					public void actionPerformed(ActionEvent arg0) {
						Grid.increment();
						repaint();			
					}		
		});
		

		viewMenu.addSeparator();

		showComponentsAction = new ViewAction("Display components", "Show/hide the list of components.",
				KeyStroke.getKeyStroke('1', shortcutkey), true);
		addCheckboxMenuItem(viewMenu, showComponents, showComponentsAction);

		showQueriesAction = new ViewAction("Display queries", "Show/hide verification queries.",
				KeyStroke.getKeyStroke('2', shortcutkey), true);
		addCheckboxMenuItem(viewMenu, showQueries, showQueriesAction);

		showConstantsAction = new ViewAction("Display constants", "Show/hide global constants.",
				KeyStroke.getKeyStroke('3', shortcutkey), true);
		addCheckboxMenuItem(viewMenu, showConstants, showConstantsAction);

		showEnabledTransitionsAction = new ViewAction("Display enabled transitions",
				"Show/hide the list of enabled transitions", KeyStroke.getKeyStroke('4', shortcutkey), true);
		addCheckboxMenuItem(viewMenu, showEnabledTransitions, showEnabledTransitionsAction);

		showDelayEnabledTransitionsAction = new ViewAction("Display future-enabled transitions",
				"Highlight transitions which can be enabled after a delay", KeyStroke.getKeyStroke('5', shortcutkey),
				true);
		addCheckboxMenuItem(viewMenu, showDelayEnabledTransitions, showDelayEnabledTransitionsAction);

		showZeroToInfinityIntervalsAction = new ViewAction("Display intervals [0,inf)",
				"Show/hide intervals [0,inf) that do not restrict transition firing in any way.",
				KeyStroke.getKeyStroke('6', shortcutkey), true);
		showZeroToInfinityIntervalsCheckBox = addCheckboxMenuItem(viewMenu, CreateGui.showZeroToInfinityIntervals(),
				showZeroToInfinityIntervalsAction);

		showToolTipsAction = new ViewAction("Display tool tips", "Show/hide tool tips when mouse is over an element",
				KeyStroke.getKeyStroke('7', shortcutkey), true);
		addCheckboxMenuItem(viewMenu, showToolTips, showToolTipsAction);

		showTokenAgeAction = new ViewAction("Display token age",
				"Show/hide displaying the token age 0.0 (when hidden the age 0.0 is drawn as a dot)",
				KeyStroke.getKeyStroke('8', shortcutkey), true);
		showTokenAgeCheckBox = addCheckboxMenuItem(viewMenu, CreateGui.showTokenAge(), showTokenAgeAction);

		viewMenu.addSeparator();

		viewMenu.add( showSimpleWorkspaceAction = new ViewAction("Show simple workspace", "Show only the most important panels", false));
		viewMenu.add( showAdvancedWorkspaceAction = new ViewAction("Show advanced workspace", "Show all panels", false));
		viewMenu.add( saveWorkSpaceAction = new ViewAction("Save workspace", "Save the current workspace as the default one", false));
		return viewMenu;
	}

	private JMenu buildMenuAnimation() {
		/* Simulator */
		JMenu animateMenu = new JMenu("Simulator");
		animateMenu.setMnemonic('A');
		animateMenu.add( startAction = new AnimateAction(
				"Simulation mode", ElementType.START, "Toggle simulation mode (M)",
				"M", true));
		
		
		animateMenu.add( stepbackwardAction = new AnimateAction("Step backward",
				ElementType.STEPBACKWARD, "Step backward", "pressed LEFT"));
		animateMenu.add(
				stepforwardAction = new AnimateAction("Step forward",
						ElementType.STEPFORWARD, "Step forward", "pressed RIGHT"));

		animateMenu.add( timeAction = new AnimateAction("Delay one time unit",
				ElementType.TIMEPASS, "Let time pass one time unit", "W"));

		animateMenu.add( delayFireAction = new AnimateAction("Delay and fire",
				ElementType.DELAYFIRE, "Delay and fire selected transition", "F"));

		animateMenu.add( prevcomponentAction = new AnimateAction("Previous component",
				ElementType.PREVCOMPONENT, "Previous component", "pressed UP"));

		animateMenu.add( nextcomponentAction = new AnimateAction("Next component",
				ElementType.NEXTCOMPONENT, "Next component", "pressed DOWN"));

		animateMenu.addSeparator();

		animateMenu.add( exportTraceAction = new GuiAction("Export trace",
				"Export the current trace","") {
					public void actionPerformed(ActionEvent arg0) {
						TraceImportExport.exportTrace();
					}		
		});
		animateMenu.add( importTraceAction = new GuiAction("Import trace",
				"Import trace to simulator",""){
			public void actionPerformed(ActionEvent arg0) {
				TraceImportExport.importTrace();
			}		
		});

		randomAction = new AnimateAction("Random", ElementType.RANDOM,
				"Randomly fire a transition", "typed 5");
		randomAnimateAction = new AnimateAction("Simulate", ElementType.ANIMATE,
				"Randomly fire a number of transitions", "typed 7", true);
		return animateMenu;
	}

	private JMenu buildMenuHelp() {
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');

		helpMenu.add(showHomepage = new GuiAction("Visit TAPAAL home", "Visit the TAPAAL homepage") {
			public void actionPerformed(ActionEvent arg0) {
				showInBrowser("http://www.tapaal.net");
			}
		});

		helpMenu.add(checkUpdate = new GuiAction("Check for updates", "Check if there is a new version of TAPAAL") {
			public void actionPerformed(ActionEvent arg0) {
				pipe.gui.CreateGui.checkForUpdate(true);
			}
		});

		helpMenu.addSeparator();

		helpMenu.add(showFAQAction = new GuiAction("Show FAQ", "See TAPAAL frequently asked questions") {
			public void actionPerformed(ActionEvent arg0) {
				showInBrowser("https://answers.launchpad.net/tapaal/+faqs");
			}
		});
		helpMenu.add(showAskQuestionAction = new GuiAction("Ask a question", "Ask a question about TAPAAL") {
			public void actionPerformed(ActionEvent arg0) {
				showInBrowser("https://answers.launchpad.net/tapaal/+addquestion");
			}
		});
		helpMenu.add(showReportBugAction = new GuiAction("Report bug", "Report a bug in TAPAAL") {
			public void actionPerformed(ActionEvent arg0) {
				showInBrowser("https://bugs.launchpad.net/tapaal/+filebug");
			}
		});

		helpMenu.addSeparator();

		helpMenu.add(showAboutAction = new GuiAction("About", "Show the About menu") {
			public void actionPerformed(ActionEvent arg0) {
				showAbout();
			}
		});
		return helpMenu;
	}

	

	private JMenu buildMenuTools() {
		JMenu toolsMenu = new JMenu("Tools");
		toolsMenu.setMnemonic('t');

		int shortcutkey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		verification = new JMenuItem(verifyAction = new ToolAction("Verify query","Verifies the currently selected query",KeyStroke.getKeyStroke(KeyEvent.VK_M, shortcutkey)));
		verification.setMnemonic('m');
		verification.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent arg0) {
				CreateGui.getCurrentTab().verifySelectedQuery();				
			}
		});
		toolsMenu.add(verification);	
		statistics = new JMenuItem(netStatisticsAction = new ToolAction("Net statistics", "Shows information about the number of transitions, places, arcs, etc.",KeyStroke.getKeyStroke(KeyEvent.VK_I, shortcutkey)));				
		statistics.setMnemonic('i');		
		statistics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				StatisticsPanel.showStatisticsPanel();
			}
		});		
		toolsMenu.add(statistics);		


		//JMenuItem batchProcessing = new JMenuItem("Batch processing");
		JMenuItem batchProcessing = new JMenuItem(batchProcessingAction = new ToolAction("Batch processing", "Batch verification of multiple nets and queries",KeyStroke.getKeyStroke(KeyEvent.VK_B, shortcutkey)));				
		batchProcessing.setMnemonic('b');				
		batchProcessing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(checkForSaveAll()){
					BatchProcessingDialog.showBatchProcessingDialog(new JList(new DefaultListModel()));
				}
			}
		});
		toolsMenu.add(batchProcessing);

		JMenuItem workflowDialog = new JMenuItem(workflowDialogAction = new ToolAction("Workflow analysis", "Analyse net as a TAWFN", KeyStroke.getKeyStroke(KeyEvent.VK_F, shortcutkey)));				
		workflowDialog.setMnemonic('f');
		workflowDialog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WorkflowDialog.showDialog();
			}
		});
		toolsMenu.add(workflowDialog);

		//Stip off timing information
		JMenuItem stripTimeDialog = new JMenuItem(stripTimeDialogAction = new ToolAction("Remove timing information", "Remove all timing information from the net in the active tab and open it as a P/T net in a new tab.", KeyStroke.getKeyStroke(KeyEvent.VK_E, shortcutkey)));
		stripTimeDialog.setMnemonic('e');
		stripTimeDialog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				duplicateTab((TabContent) appTab.getSelectedComponent());
				convertToUntimedTab((TabContent) appTab.getSelectedComponent());
			}
		});
		toolsMenu.add(stripTimeDialog);

		toolsMenu.addSeparator();

		JMenuItem engineSelection = new JMenuItem(engineSelectionAction = new ToolAction("Engine selection", "View and modify the location of verification engines",null));
		engineSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new EngineDialogPanel().showDialog();				
			}
		});
		toolsMenu.add(engineSelection);

		JMenuItem clearPreferences = new JMenuItem("Clear all preferences");
		clearPreferences.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Clear persistent storage
				Preferences.getInstance().clear();
				// Engines reset individually to remove preferences for already setup engines
				Verifyta.reset();
				VerifyTAPN.reset();
				VerifyTAPNDiscreteVerification.reset();
			}
		});
		toolsMenu.add(clearPreferences);

		return toolsMenu;
	}

	private void showAdvancedWorkspace(boolean advanced){
		QueryDialog.setAdvancedView(advanced);
		showComponents(advanced);
		showConstants(advanced);

		//Queries and enabled transitions should always be shown
		showQueries(true);
		showEnabledTransitionsList(true);
		showToolTips(true);
		CreateGui.getCurrentTab().setResizeingDefault();
		if(!CreateGui.showZeroToInfinityIntervals()){
			showZeroToInfinityIntervalsCheckBox.doClick();
		}
		if(!CreateGui.showTokenAge()){
			showTokenAgeCheckBox.doClick();
		}
		//Delay-enabled Transitions
		showDelayEnabledTransitions(advanced);
		DelayEnabledTransitionControl.getInstance().setValue(new BigDecimal("0.1"));
		DelayEnabledTransitionControl.getInstance().setDelayMode(ShortestDelayMode.getInstance());
		DelayEnabledTransitionControl.getInstance().setRandomTransitionMode(false);
	}

	private void saveWorkspace(){
		Preferences prefs = Preferences.getInstance();

		prefs.setAdvancedQueryView(QueryDialog.getAdvancedView());
		prefs.setEditorModelRoot(TabContent.getEditorModelRoot());
		prefs.setSimulatorModelRoot(TabContent.getSimulatorModelRoot());
		prefs.setWindowSize(this.getSize());

		prefs.setShowComponents(showComponents);
		prefs.setShowQueries(showQueries);
		prefs.setShowConstants(showConstants);

		prefs.setShowEnabledTrasitions(showEnabledTransitions);
		prefs.setShowDelayEnabledTransitions(showDelayEnabledTransitions);
		prefs.setShowTokenAge(CreateGui.showTokenAge());
		prefs.setDelayEnabledTransitionDelayMode(DelayEnabledTransitionControl.getDefaultDelayMode());
		prefs.setDelayEnabledTransitionGranularity(DelayEnabledTransitionControl.getDefaultGranularity());
		prefs.setDelayEnabledTransitionIsRandomTransition(DelayEnabledTransitionControl.isRandomTransition());

		JOptionPane.showMessageDialog(this, 
				"The workspace has now been saved into your preferences.\n" 
						+ "It will be used as the initial workspace next time you run the tool.",
						"Workspace Saved", JOptionPane.INFORMATION_MESSAGE);
	}


	private void buildToolbar() {

		//XXX .setRequestFocusEnabled(false), removed "border" around tollbar buttons when selcted/focus
		// https://stackoverflow.com/questions/9361658/disable-jbutton-focus-border and
		//https://stackoverflow.com/questions/20169436/how-to-prevent-toolbar-button-focus-in-java-swing

		// Create the toolbar
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);// Inhibit toolbar floating
		toolBar.setRequestFocusEnabled(false);

		// Basis file operations
		toolBar.add(createAction).setRequestFocusEnabled(false);
		toolBar.add(openAction).setRequestFocusEnabled(false);
		toolBar.add(saveAction).setRequestFocusEnabled(false);
		toolBar.add(saveAsAction).setRequestFocusEnabled(false);

		// Print
		toolBar.addSeparator();
		toolBar.add(printAction).setRequestFocusEnabled(false);

		// Copy/past
		/*
		 * Removed copy/past button toolBar.addSeparator();
		 * toolBar.add(cutAction); toolBar.add(copyAction);
		 * toolBar.add(pasteAction);
		 */

		// Undo/redo
		toolBar.addSeparator();
		toolBar.add(deleteAction).setRequestFocusEnabled(false);
		toolBar.add(undoAction).setRequestFocusEnabled(false);
		toolBar.add(redoAction).setRequestFocusEnabled(false);

		// Zoom
		toolBar.addSeparator();
		toolBar.add(zoomOutAction).setRequestFocusEnabled(false);
		addZoomComboBox(toolBar, new ZoomAction("Zoom",
				"Select zoom percentage ", ""));
		toolBar.add(zoomInAction).setRequestFocusEnabled(false);

		// Modes

		toolBar.addSeparator();
		toolBar.add(toggleGrid).setRequestFocusEnabled(false);

		toolBar.add(new ToggleButton(startAction));

		// Start drawingToolBar
		drawingToolBar = new JToolBar();
		drawingToolBar.setFloatable(false);
		drawingToolBar.addSeparator();
		drawingToolBar.setRequestFocusEnabled(false);

		// Normal arraw
		drawingToolBar.add(new ToggleButton(selectAction));


		// Drawing elements
		drawingToolBar.addSeparator();
		drawingToolBar.add(new ToggleButton(timedPlaceAction));
		drawingToolBar.add(new ToggleButton(transAction));
		drawingToolBar.add(new ToggleButton(timedArcAction));
		drawingToolBar.add(new ToggleButton(transportArcAction));
		drawingToolBar.add(new ToggleButton(inhibarcAction));

		drawingToolBar.add(new ToggleButton(annotationAction));

		// Tokens
		drawingToolBar.addSeparator();
		drawingToolBar.add(new ToggleButton(tokenAction));
		drawingToolBar.add(new ToggleButton(deleteTokenAction));

		// Create panel to put toolbars in
		JPanel toolBarPanel = new JPanel();
		toolBarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		// Add toolbars to pane
		toolBarPanel.add(toolBar);
		toolBarPanel.add(drawingToolBar);

		// Create a toolBarPaneltmp usign broderlayout and a spacer to get
		// toolbar to fill the screen
		JPanel toolBarPaneltmp = new JPanel();
		toolBarPaneltmp.setLayout(new BorderLayout());
		toolBarPaneltmp.add(toolBarPanel, BorderLayout.WEST);
		JToolBar spacer = new JToolBar();
		spacer.addSeparator();
		spacer.setFloatable(false);
		toolBarPaneltmp.add(spacer, BorderLayout.CENTER);

		// Add to GUI
		getContentPane().add(toolBarPaneltmp, BorderLayout.PAGE_START);
	}

	/**
	 * @author Ben Kirby Takes the method of setting up the Zoom menu out of the
	 *         main buildMenus method.
	 * @param zoomMenu
	 *            - the menu to add the submenu to
	 */
	private void addZoomMenuItems(JMenu zoomMenu) {
		for (int i = 0; i <= zoomExamples.length - 1; i++) {
			ZoomAction a = new ZoomAction(zoomExamples[i], "Select zoom percentage", "");

			JMenuItem newItem = new JMenuItem(a);

			zoomMenu.add(newItem);
		}
	}

	/**
	 * @author Ben Kirby Just takes the long-winded method of setting up the
	 *         ComboBox out of the main buildToolbar method. Could be adapted
	 *         for generic addition of comboboxes
	 * @param toolBar
	 *            the JToolBar to add the button to
	 * @param action
	 *            the action that the ZoomComboBox performs
	 */
	private void addZoomComboBox(JToolBar toolBar, Action action) {
		Dimension zoomComboBoxDimension = new Dimension(75, 28);
		zoomComboBox = new JComboBox<String>(zoomExamples);
		zoomComboBox.setEditable(true);
		zoomComboBox.setSelectedItem("100%");
		zoomComboBox.setMaximumRowCount(zoomExamples.length);
		zoomComboBox.setMaximumSize(zoomComboBoxDimension);
		zoomComboBox.setMinimumSize(zoomComboBoxDimension);
		zoomComboBox.setPreferredSize(zoomComboBoxDimension);
		zoomComboBox.setAction(action);
		zoomComboBox.setFocusable(false);
		toolBar.add(zoomComboBox);
	}

	private JCheckBoxMenuItem addCheckboxMenuItem(JMenu menu, boolean selected, Action action) {
		
		JCheckBoxMenuItem checkBoxItem = new JCheckBoxMenuItem();
		
		checkBoxItem.setAction(action);
		checkBoxItem.setSelected(selected);
		JMenuItem item = menu.add(checkBoxItem);
		KeyStroke keystroke = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);

		if (keystroke != null) {
			item.setAccelerator(keystroke);
		}
		return checkBoxItem;
	}

	/**
	 * Sets all buttons to enabled or disabled according to the current GUImode.
	 * 
	 * Reimplementation of old enableGUIActions(bool status)
	 * 
	 * @author Kenneth Yrke Joergensen (kyrke)
	 * */
	private void enableGUIActions() {
		switch (getGUIMode()) {
		case draw:
			enableAllActions(true);
			exportTraceAction.setEnabled(false);
			importTraceAction.setEnabled(false);

			timedPlaceAction.setEnabled(true);
			timedArcAction.setEnabled(true);
			inhibarcAction.setEnabled(true);
			if (!CreateGui.getModel().netType().equals(NetType.UNTIMED)) {
				transportArcAction.setEnabled(true);
			} else {
				transportArcAction.setEnabled(false);
			}

			annotationAction.setEnabled(true);
			transAction.setEnabled(true);
			tokenAction.setEnabled(true);
			deleteAction.setEnabled(true);
			selectAllAction.setEnabled(true);
			selectAction.setEnabled(true);
			deleteTokenAction.setEnabled(true);

			timeAction.setEnabled(false);
			delayFireAction.setEnabled(false);
			stepbackwardAction.setEnabled(false);
			stepforwardAction.setEnabled(false);
			prevcomponentAction.setEnabled(false);
			nextcomponentAction.setEnabled(false);

			deleteAction.setEnabled(true);
			showEnabledTransitionsAction.setEnabled(false);
			showDelayEnabledTransitionsAction.setEnabled(false);

			verifyAction.setEnabled(CreateGui.getCurrentTab().isQueryPossible());

			verifyAction.setEnabled(CreateGui.getCurrentTab().isQueryPossible());

			workflowDialogAction.setEnabled(true);
			stripTimeDialogAction.setEnabled(true);

			// Undo/Redo is enabled based on undo/redo manager
			appView.getUndoManager().setUndoRedoStatus();

			if(CreateGui.getCurrentTab().restoreWorkflowDialog()){
				WorkflowDialog.showDialog();
			}

			break;

		case animation:
			enableAllActions(true);

			timedPlaceAction.setEnabled(false);
			timedArcAction.setEnabled(false);
			inhibarcAction.setEnabled(false);
			transportArcAction.setEnabled(false);

			annotationAction.setEnabled(false);
			transAction.setEnabled(false);
			tokenAction.setEnabled(false);
			deleteAction.setEnabled(false);
			selectAllAction.setEnabled(false);
			selectAction.setEnabled(false);
			deleteTokenAction.setEnabled(false);

			showConstantsAction.setEnabled(false);
			showQueriesAction.setEnabled(false);

			// Only enable this if it is not an untimed net.
			if (CreateGui.getModel().netType() != NetType.UNTIMED) {
				timeAction.setEnabled(true);
			}
			delayFireAction.setEnabled(true);
			stepbackwardAction.setEnabled(true);
			stepforwardAction.setEnabled(true);
			prevcomponentAction.setEnabled(true);
			nextcomponentAction.setEnabled(true);

			deleteAction.setEnabled(false);
			undoAction.setEnabled(false);
			redoAction.setEnabled(false);
			verifyAction.setEnabled(false);

			workflowDialogAction.setEnabled(false);
			stripTimeDialogAction.setEnabled(false);

			// Remove constant highlight
			CreateGui.getCurrentTab().removeConstantHighlights();

			CreateGui.getAnimationController().requestFocusInWindow();

			// Event repeater
			CreateGui.getAnimationController().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "_right_hold");
			CreateGui.getAnimationController().getActionMap().put("_right_hold", stepforwardAction);
			CreateGui.getAnimationController().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "_left_hold");
			CreateGui.getAnimationController().getActionMap().put("_left_hold", stepbackwardAction);
			CreateGui.getAnimationController().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "_up_hold");
			CreateGui.getAnimationController().getActionMap().put("_up_hold", prevcomponentAction);
			CreateGui.getAnimationController().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "_down_hold");
			CreateGui.getAnimationController().getActionMap().put("_down_hold", nextcomponentAction);
			break;
		case noNet:
			exportTraceAction.setEnabled(false);
			importTraceAction.setEnabled(false);
			verifyAction.setEnabled(false);

			timedPlaceAction.setEnabled(false);
			timedArcAction.setEnabled(false);
			inhibarcAction.setEnabled(false);
			transportArcAction.setEnabled(false);

			annotationAction.setEnabled(false);
			transAction.setEnabled(false);
			tokenAction.setEnabled(false);
			deleteAction.setEnabled(false);
			selectAllAction.setEnabled(false);
			selectAction.setEnabled(false);
			deleteTokenAction.setEnabled(false);

			timeAction.setEnabled(false);
			delayFireAction.setEnabled(false);
			stepbackwardAction.setEnabled(false);
			stepforwardAction.setEnabled(false);

			deleteAction.setEnabled(false);
			undoAction.setEnabled(false);
			redoAction.setEnabled(false);

			workflowDialogAction.setEnabled(false);
			stripTimeDialogAction.setEnabled(false);

			enableAllActions(false);
			break;
		}

	}

	/**
	 * Helperfunction for disabeling/enabeling all actions when we are in noNet
	 * GUImode
	 * 
	 * @return
	 */
	private void enableAllActions(boolean enable) {

		// File
		closeAction.setEnabled(enable);

		saveAction.setEnabled(enable);
		saveAsAction.setEnabled(enable);

		exportMenu.setEnabled(enable);
		exportPNGAction.setEnabled(enable);
		exportPSAction.setEnabled(enable);
		exportToTikZAction.setEnabled(enable);
		exportToPNMLAction.setEnabled(enable);
		exportToXMLAction.setEnabled(enable);

		exportTraceAction.setEnabled(enable);
		importTraceAction.setEnabled(enable);

		printAction.setEnabled(enable);

		// View
		zoomInAction.setEnabled(enable);
		zoomOutAction.setEnabled(enable);
		zoomComboBox.setEnabled(enable);
		zoomMenu.setEnabled(enable);

		toggleGrid.setEnabled(enable);

		showComponentsAction.setEnabled(enable);
		showConstantsAction.setEnabled(enable);
		showQueriesAction.setEnabled(enable);
		showZeroToInfinityIntervalsAction.setEnabled(enable);
		showEnabledTransitionsAction.setEnabled(enable);
		showDelayEnabledTransitionsAction.setEnabled(enable);
		showToolTipsAction.setEnabled(enable);
		showTokenAgeAction.setEnabled(enable);
		showAdvancedWorkspaceAction.setEnabled(enable);
		showSimpleWorkspaceAction.setEnabled(enable);
		saveWorkSpaceAction.setEnabled(enable);


		// Simulator
		startAction.setEnabled(enable);

		// Tools
		statistics.setEnabled(enable);

	}

	// set frame objects by array index
	public void setObjects(int index) {
		appView = CreateGui.getDrawingSurface(index);
	}

	// set tabbed pane properties and add change listener that updates tab with
	// linked model and view
	public void setTab() {

		appTab = CreateGui.getTab();
		appTab.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {

				int index = appTab.getSelectedIndex();
				setObjects(index);
				if (appView != null) {
					appView.setVisible(true);
					appView.repaint();
					updateZoomCombo();

					setTitle(appTab.getTitleAt(index));
					setGUIMode(GUIMode.draw);

					// TODO: change this code... it's ugly :)
					if (appGui.getMode() == ElementType.SELECT) {
						appGui.activateSelectAction();
					}

				} else {
					setTitle(null);
				}

			}

		});
		appGui = CreateGui.getApp();
		appView = CreateGui.getView();

	}



	// HAK Method called by netModel object when it changes
	public void update(Observable o, Object obj) {
		if ((mode != ElementType.CREATING) && (!appView.isInAnimationMode())) {
			appView.setNetChanged(true);
		}
	}

	private void showQueries(boolean enable){
		showQueries = enable;
		CreateGui.getCurrentTab().showQueries(enable);

	}
	private void toggleQueries(){
		showQueries(!showQueries);
	}

	private void showConstants(boolean enable){
		showConstants = enable;
		CreateGui.getCurrentTab().showConstantsPanel(enable);

	}
	private void toggleConstants(){
		showConstants(!showConstants);
	}

	private void showToolTips(boolean enable){
		showToolTips = enable;
		Preferences.getInstance().setShowToolTips(showToolTips);

		ToolTipManager.sharedInstance().setEnabled(enable);
		ToolTipManager.sharedInstance().setInitialDelay(400);
		ToolTipManager.sharedInstance().setReshowDelay(800);
		ToolTipManager.sharedInstance().setDismissDelay(60000);
	}
	private void toggleToolTips(){
		showToolTips(!showToolTips);
	}

	boolean isShowingToolTips(){
		return showToolTips;
	}

	private void toggleTokenAge(){
		CreateGui.toggleShowTokenAge();
		Preferences.getInstance().setShowTokenAge(CreateGui.showTokenAge());
		appView.repaintAll();
	}

	private void toggleZeroToInfinityIntervals() {
		CreateGui.toggleShowZeroToInfinityIntervals();
		Preferences.getInstance().setShowZeroInfIntervals(CreateGui.showZeroToInfinityIntervals());
		appView.repaintAll();
	}

	private void showComponents(boolean enable){
		showComponents = enable;
		CreateGui.getCurrentTab().showComponents(enable);

	}
	private void toggleComponents(){
		showComponents(!showComponents);
	}

	private void showEnabledTransitionsList(boolean enable){
		showEnabledTransitions = enable;
		CreateGui.getCurrentTab().showEnabledTransitionsList(enable);

	}
	private void toggleEnabledTransitionsList(){
		showEnabledTransitionsList(!showEnabledTransitions);
	}

	private void showDelayEnabledTransitions(boolean enable){
		showDelayEnabledTransitions = enable;
		CreateGui.getCurrentTab().showDelayEnabledTransitions(enable);
	}
	private void toggleDelayEnabledTransitions(){
		showDelayEnabledTransitions(!showDelayEnabledTransitions);
	}

	private void saveOperation(boolean forceSave){
		saveOperation(appTab.getSelectedIndex(), forceSave);
	}

	private boolean saveOperation(int index, boolean forceSaveAs) {
		File modelFile = CreateGui.getFile(index);
		boolean result;
		if (!forceSaveAs && modelFile != null) { // ordinary save
			saveNet(index, modelFile);
			result = true;
		} else { // save as
			String path;
			if (modelFile != null) {
				path = modelFile.toString();
			} else {
				path = appTab.getTitleAt(index);
			}
			String filename = new FileBrowser(path).saveFile();
			if (filename != null) {
				modelFile = new File(filename);
				saveNet(index, modelFile);
				result = true;
			}else{
				result = false;
			}
		}

		// resize "header" of current tab immediately to fit the length of the
		// model name
		appTab.getTabComponentAt(index).doLayout();
		return result;
	}

	private void saveNet(int index, File outFile) {
		try {
			saveNet(index, outFile, (List<TAPNQuery>) CreateGui.getTab(index).queries());

			CreateGui.setFile(outFile, index);

			CreateGui.getDrawingSurface(index).setNetChanged(false);
			appTab.setTitleAt(index, outFile.getName());
			if(index == appTab.getSelectedIndex()) setTitle(outFile.getName()); // Change the window title
			CreateGui.getDrawingSurface(index).getUndoManager().clear();
			undoAction.setEnabled(false);
			redoAction.setEnabled(false);
		} catch (Exception e) {
			System.err.println(e);
			JOptionPane.showMessageDialog(GuiFrame.this, e.toString(),
					"File Output Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	
	public void saveNet(int index, File outFile, List<TAPNQuery> queries) {
		try {
			TabContent currentTab = CreateGui.getTab(index);
			NetworkMarking currentMarking = null;
			if(getGUIMode().equals(GUIMode.animation)){
				currentMarking = currentTab.network().marking();
				currentTab.network().setMarking(CreateGui.getAnimator().getInitialMarking());
			}

			NetWriter tapnWriter = new TimedArcPetriNetNetworkWriter(
					currentTab.network(),
					currentTab.allTemplates(), 
					queries, 
					currentTab.network().constants()
					);

			tapnWriter.savePNML(outFile);

			if(getGUIMode().equals(GUIMode.animation)){
				currentTab.network().setMarking(currentMarking);
			}
		} catch (Exception e) {
			Logger.log(e);
			JOptionPane.showMessageDialog(GuiFrame.this, e.toString(),
					"File Output Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void createNewTab(String name, NetType netType) {
		int freeSpace = CreateGui.getFreeSpace(netType);

		setObjects(freeSpace);
		CreateGui.getModel(freeSpace).setNetType(netType);

		if (name == null || name.isEmpty()) {
			name = "New Petri net " + (newNameCounter++) + ".xml";
		}

		TabContent tab = CreateGui.getTab(freeSpace);
		appTab.addTab(name, tab);
		appTab.setTabComponentAt(freeSpace, new TabComponent(appTab));
		appTab.setSelectedIndex(freeSpace);

		String templateName = tab.drawingSurface().getNameGenerator().getNewTemplateName();
		Template template = new Template(new TimedArcPetriNet(templateName), new DataLayer(), new Zoomer());
		tab.addTemplate(template);

		tab.setCurrentTemplate(template);

		appView.setNetChanged(false); // Status is unchanged
		appView.updatePreferredSize();

		setTitle(name);// Change the program caption
		appTab.setTitleAt(freeSpace, name);
		selectAction.actionPerformed(null);
	}


	/**
	 * Creates a new tab with the selected file, or a new file if filename==null
	 */
	public void createNewTabFromFile(InputStream file, String namePrefix) {
		int freeSpace = CreateGui.getFreeSpace(NetType.TAPN);
		String name;

		setObjects(freeSpace);
		int currentlySelected = appTab.getSelectedIndex();


		if (namePrefix == null || namePrefix.equals("")) {
			name = "New Petri net " + (newNameCounter++) + ".xml";
		} else {
			name = namePrefix + ".xml";
		}

		TabContent tab = CreateGui.getTab(freeSpace);
		appTab.addTab(name, null, tab, null);
		appTab.setTabComponentAt(freeSpace, new TabComponent(appTab));
		appTab.setSelectedIndex(freeSpace);

		if (file != null) {
			try {
				TabContent currentTab = (TabContent) appTab.getSelectedComponent();
				if (CreateGui.getApp() != null) {
					// Notifies used to indicate new instances.
					CreateGui.getApp().setMode(ElementType.CREATING);
				}

				ModelLoader loader = new ModelLoader(currentTab.drawingSurface());
				LoadedModel loadedModel = loader.load(file);

				currentTab.setNetwork(loadedModel.network(), loadedModel.templates());
				currentTab.setQueries(loadedModel.queries());
				currentTab.setConstants(loadedModel.network().constants());
				currentTab.setupNameGeneratorsFromTemplates(loadedModel.templates());

				currentTab.selectFirstElements();

				if (CreateGui.getApp() != null) {
					CreateGui.getApp().restoreMode();
				}

				CreateGui.setFile(null, freeSpace);
			} catch (Exception e) {
				undoAddTab(currentlySelected);
				JOptionPane.showMessageDialog(GuiFrame.this,
						"TAPAAL encountered an error while loading the file: " + name + "\n\nPossible explanations:\n  - " + e.toString(), 
						"Error loading file: " + name, 
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		appView.setNetChanged(false); // Status is unchanged
		appView.updatePreferredSize();
		setTitle(name);// Change the program caption
		appTab.setTitleAt(freeSpace, name);
		selectAction.actionPerformed(null);
	}

	/**
	 * Creates a new tab with the selected file, or a new file if filename==null
	 */
	public void createNewTabFromFile(File file, boolean loadPNML) {
		int freeSpace = CreateGui.getFreeSpace(NetType.TAPN);
		String name;

		setObjects(freeSpace);
		int currentlySelected = appTab.getSelectedIndex();

		if (file == null) {
			name = "New Petri net " + (newNameCounter++) + ".xml";
		} else {
			name = file.getName();
		}

		TabContent tab = CreateGui.getTab(freeSpace);
		appTab.addTab(name, null, tab, null);
		appTab.setTabComponentAt(freeSpace, new TabComponent(appTab));
		appTab.setSelectedIndex(freeSpace);

		if (file != null) {
			try {
				TabContent currentTab = (TabContent) appTab.getSelectedComponent();
				if (CreateGui.getApp() != null) {
					// Notifies used to indicate new instances.
					CreateGui.getApp().setMode(ElementType.CREATING);
				}

				LoadedModel loadedModel;
				if(loadPNML){
					PNMLoader loader = new PNMLoader(currentTab.drawingSurface());
					loadedModel = loader.load(file);
				} else {
					ModelLoader loader = new ModelLoader(currentTab.drawingSurface());
					loadedModel = loader.load(file);
				}

				currentTab.setNetwork(loadedModel.network(), loadedModel.templates());
				currentTab.setQueries(loadedModel.queries());
				currentTab.setConstants(loadedModel.network().constants());
				currentTab.setupNameGeneratorsFromTemplates(loadedModel.templates());

				currentTab.selectFirstElements();

				if (CreateGui.getApp() != null) {
					CreateGui.getApp().restoreMode();
				}

				if(!loadPNML){
					CreateGui.setFile(file, freeSpace);
				}
			} catch (Exception e) {
				undoAddTab(currentlySelected);
				JOptionPane.showMessageDialog(GuiFrame.this,
						"TAPAAL encountered an error while loading the file: " + name + "\n\nPossible explanations:\n  - " + e.toString(), 
						"Error loading file: " + name, 
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		appView.setNetChanged(false); // Status is unchanged
		appView.updatePreferredSize();
		name = name.replace(".pnml",".xml"); // rename .pnml input file to .xml	
		setTitle(name);// Change the program caption
		appTab.setTitleAt(freeSpace, name);
		selectAction.actionPerformed(null);
	}

	private void duplicateTab(TabContent tabToDuplicate) {
		int index = appTab.indexOfComponent(tabToDuplicate);

		NetWriter tapnWriter = new TimedArcPetriNetNetworkWriter(
				tabToDuplicate.network(),
				tabToDuplicate.allTemplates(),
				tabToDuplicate.queries(),
				tabToDuplicate.network().constants()
		);

		try {
			ByteArrayOutputStream outputStream = tapnWriter.savePNML();
			String composedName = appTab.getTitleAt(index);
			composedName = composedName.replace(".xml", "");
			composedName += "-untimed";
			CreateGui.getApp().createNewTabFromFile(new ByteArrayInputStream(outputStream.toByteArray()), composedName);
		} catch (Exception e1) {
			System.console().printf(e1.getMessage());
		}
	}

	private void convertToUntimedTab(TabContent tab){
		TabTransformer.removeTimingInformation(tab);
		setGUIMode(GuiFrame.GUIMode.draw);
	}

	private void undoAddTab(int currentlySelected) {
		CreateGui.undoGetFreeSpace();
		appTab.removeTabAt(appTab.getTabCount() - 1);
		appTab.setSelectedIndex(currentlySelected);

	}

	/**
	 * If current net has modifications, asks if you want to save and does it if
	 * you want.
	 * 
	 * @return true if handled, false if cancelled
	 */
	private boolean checkForSave() {
		return checkForSave(appTab.getSelectedIndex());
	}

	public boolean checkForSave(int index) {

		if(index < 0) return false;

		if (CreateGui.getDrawingSurface(index).getNetChanged()) {
			int result = JOptionPane.showConfirmDialog(GuiFrame.this,
					"The net has been modified. Save the current net?",
					"Confirm Save Current File",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);

			switch (result) {
			case JOptionPane.YES_OPTION:
				boolean saved = saveOperation(index, false);
				if(!saved) return false;
				break;
			case JOptionPane.CLOSED_OPTION:
			case JOptionPane.CANCEL_OPTION:
				return false;
			}
		}
		return true;
	}

	/**
	 * If current net has modifications, asks if you want to save and does it if
	 * you want.
	 * 
	 * @return true if handled, false if cancelled
	 */
	private boolean checkForSaveAll() {
		// Loop through all tabs and check if they have been saved
		for (int counter = 0; counter < appTab.getTabCount(); counter++) {
			appTab.setSelectedIndex(counter);
			if (!(checkForSave())) {
				return false;
			}
		}
		return true;
	}

	public void setRandomAnimationMode(boolean on) {

		if (!(on)) {
			stepforwardAction.setEnabled(CreateGui.getAnimationHistory().isStepForwardAllowed());
			stepbackwardAction.setEnabled(CreateGui.getAnimationHistory().isStepBackAllowed());

			CreateGui.getAnimationController().setAnimationButtonsEnabled();

		} else {
			stepbackwardAction.setEnabled(false);
			stepforwardAction.setEnabled(false);
		}
		randomAction.setEnabled(!on);
		randomAnimateAction.setSelected(on);
	}

	/**
	 * @deprecated Replaced with setGUIMode
	 * @param on
	 *            enable or disable animation mode
	 */
	public void setAnimationMode(boolean on) {

		if (on) {
			setGUIMode(GUIMode.animation);
		} else {
			setGUIMode(GUIMode.draw);
		}

	}

	/**
	 * Returns the current GUIMode
	 * 
	 * @author Kenneth Yrke Joergensen (kyrke)
	 * @return the current GUIMode
	 */
	public GUIMode getGUIMode() {
		return guiMode;
	}

	/**
	 * Set the current mode of the GUI, and changes possible actions
	 * 
	 * @param mode
	 *            change GUI to this mode
	 * @author Kenneth Yrke Joergensen (kyrke)
	 */
	//TODO
	public void setGUIMode(GUIMode mode) {
		switch (mode) {
		case draw:
			// Enable all draw actions
			startAction.setSelected(false);
			CreateGui.getView().changeAnimationMode(false);

			statusBar.changeText(statusBar.textforDrawing);
			if (this.guiMode.equals(GUIMode.animation)) {
				CreateGui.getAnimator().restoreModel();
				hideComponentWindow();
			}

			CreateGui.switchToEditorComponents();
			showComponents(showComponents);
			showQueries(showQueries);
			showConstants(showConstants);
			showToolTips(showToolTips);

			CreateGui.getView().setBackground(Pipe.ELEMENT_FILL_COLOUR);

			activateSelectAction();
			selectAction.setSelected(true);
			break;
		case animation:
			TabContent tab = (TabContent) appTab.getSelectedComponent();
			CreateGui.getAnimator().setTabContent(tab);
			tab.switchToAnimationComponents(showEnabledTransitions);
			showComponents(showComponents);

			startAction.setSelected(true);
			tab.drawingSurface().changeAnimationMode(true);
			tab.drawingSurface().repaintAll();
			CreateGui.getAnimator().reset(false);
			CreateGui.getAnimator().storeModel();
			CreateGui.getAnimator().highlightEnabledTransitions();
			CreateGui.getAnimator().reportBlockingPlaces();
			CreateGui.getAnimator().setFiringmode("Random");

			statusBar.changeText(statusBar.textforAnimation);
			selectAction.setSelected(false);
			// Set a light blue backgound color for animation mode
			tab.drawingSurface().setBackground(Pipe.ANIMATION_BACKGROUND_COLOR);
			CreateGui.getAnimationController().requestFocusInWindow();
			break;
		case noNet:
			// Disable All Actions
			statusBar.changeText(statusBar.textforNoNet);
			if(CreateGui.appGui != null){
				CreateGui.appGui.setFocusTraversalPolicy(null);
			}
			break;

		default:
			break;
		}
		this.guiMode = mode;
		// Enable actions based on GUI mode
		enableGUIActions();

	}

	private void hideComponentWindow(){
		ArrayList<PetriNetObject> selection = CreateGui.getView().getPNObjects();

		for (PetriNetObject pn : selection) {
			if (pn instanceof TimedPlaceComponent) {
				TimedPlaceComponent place = (TimedPlaceComponent)pn;
				place.showAgeOfTokens(false);
			} else if (pn instanceof TimedTransitionComponent){
				TimedTransitionComponent transition = (TimedTransitionComponent)pn;
				transition.showDInterval(false);
			}
		}
	}

	public void endFastMode(){
		if(timedPlaceAction.isSelected())
			mode=ElementType.TAPNPLACE;
		else if(transAction.isSelected())
			mode=ElementType.TAPNTRANS;
		else
			mode=ElementType.SELECT;
	}

	public void setMode(Pipe.ElementType _mode) {
		// Don't bother unless new mode is different.
		if (mode != _mode) {
			prev_mode = mode;
			mode = _mode;
		}
	}

	public Pipe.ElementType getMode() {
		return mode;
	}

	private void restoreMode() {
		// xxx - This must be refactored when someone findes out excatly what is
		// gowing on
		mode = prev_mode;

		verifyAction.setEnabled(CreateGui.getCurrentTab().isQueryPossible());

		verifyAction.setEnabled(CreateGui.getCurrentTab().isQueryPossible());

		//XXX - why preform null check, is set in constructor?
		if (transAction != null) {
			transAction.setSelected(mode == ElementType.IMMTRANS);
		}

		if (timedArcAction != null)
			timedArcAction.setSelected(mode == ElementType.TAPNARC);

		if (transportArcAction != null)
			transportArcAction.setSelected(mode == ElementType.TRANSPORTARC);

		if (timedPlaceAction != null)
			timedPlaceAction.setSelected(mode == ElementType.TAPNPLACE);

		if (tokenAction != null)
			tokenAction.setSelected(mode == ElementType.ADDTOKEN);

		if (deleteTokenAction != null)
			deleteTokenAction.setSelected(mode == ElementType.DELTOKEN);

		if (selectAction != null)
			selectAction.setSelected(mode == ElementType.SELECT);

		if (annotationAction != null)
			annotationAction.setSelected(mode == ElementType.ANNOTATION);





	}

	public void setTitle(String title) {
		super.setTitle((title == null) ? frameTitle : frameTitle + ": " + title);
	}

	public boolean isEditionAllowed() {
		return getGUIMode() == GUIMode.draw;
	}


	public void setUndoActionEnabled(boolean flag) {
		undoAction.setEnabled(flag);
	}

	public void setRedoActionEnabled(boolean flag) {
		redoAction.setEnabled(flag);
	}

	public void activateSelectAction() {
		// Set selection mode at startup
		setMode(ElementType.SELECT);
		selectAction.actionPerformed(null);
	}

	/**
	 * @author Ben Kirby Remove the listener from the zoomComboBox, so that when
	 *         the box's selected item is updated to keep track of ZoomActions
	 *         called from other sources, a duplicate ZoomAction is not called
	 */
	public void updateZoomCombo() {
		ActionListener zoomComboListener = (zoomComboBox.getActionListeners())[0];
		zoomComboBox.removeActionListener(zoomComboListener);
		zoomComboBox.setSelectedItem(String.valueOf(appView.getZoomController().getPercent()) + "%");
		zoomComboBox.addActionListener(zoomComboListener);
	}
	
	public void changeSpacing(double factor){
		TabContent tabContent = (TabContent) appTab.getSelectedComponent();			
		for(PetriNetObject obj : tabContent.currentTemplate().guiModel().getPetriNetObjects()){
			if(obj instanceof PlaceTransitionObject){
				obj.translate((int) (obj.getLocation().x*factor-obj.getLocation().x), (int) (obj.getLocation().y*factor-obj.getLocation().y));
				
				if(obj instanceof Transition){
					for(Arc arc : ((PlaceTransitionObject) obj).getPreset()){
						for(ArcPathPoint point : arc.getArcPath().getArcPathPoints()){
							point.setPointLocation((float) Math.max(point.getPoint().x*factor, point.getWidth()), (float) Math.max(point.getPoint().y*factor, point.getHeight()));
						}
					}
					for(Arc arc : ((PlaceTransitionObject) obj).getPostset()){
						for(ArcPathPoint point : arc.getArcPath().getArcPathPoints()){
							point.setPointLocation((float) Math.max(point.getPoint().x*factor, point.getWidth()), (float) Math.max(point.getPoint().y*factor, point.getHeight()));
						}
					}
				}
				
				((PlaceTransitionObject) obj).update(true);
			}else{
				obj.setLocation((int) (obj.getLocation().x*factor), (int) (obj.getLocation().y*factor));
			}
		}
		
		tabContent.currentTemplate().guiModel().repaintAll(true);
		appGui.appView.updatePreferredSize();
	}
        
        private boolean canNetBeSavedAndShowMessage() {
                if (CreateGui.getCurrentTab().network().paintNet()) {
                        return true;
                } else {
                        String message = "The net is too big and cannot be saved or exported.";
                        Object[] dialogContent = {message};
                        JOptionPane.showMessageDialog(null, dialogContent, "Large net limitation", JOptionPane.WARNING_MESSAGE);
                }
                return false;
        }

	class AnimateAction extends GuiAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8582324286370859664L;
		private ElementType typeID;
		private AnimationHistoryComponent animBox;

		AnimateAction(String name, ElementType typeID, String tooltip, String keystroke) {
			super(name, tooltip, keystroke);
			this.typeID = typeID;
		}

		AnimateAction(String name, ElementType typeID, String tooltip,
				String keystroke, boolean toggleable) {
			super(name, tooltip, keystroke, toggleable);
			this.typeID = typeID;
		}

		public AnimateAction(String name, ElementType typeID, String tooltip,
				KeyStroke keyStroke) {
			super(name, tooltip, keyStroke);
			this.typeID = typeID;

		}

		public void actionPerformed(ActionEvent ae) {
			if (appView == null) {
				return;
			}

			animBox = CreateGui.getAnimationHistory();

			switch (typeID) {
			case START:
				actionStartAnimation();				
				break;

			case TIMEPASS:
				CreateGui.getAnimator().letTimePass(BigDecimal.ONE);
				CreateGui.getAnimationController().setAnimationButtonsEnabled();
				break;

			case DELAYFIRE:
				CreateGui.getCurrentTab().getTransitionFireingComponent().fireSelectedTransition();
				CreateGui.getAnimationController().setAnimationButtonsEnabled();
				break;

			case STEPFORWARD:
				animBox.stepForward();
				CreateGui.getAnimator().stepForward();
				updateMouseOverInformation();
				CreateGui.getAnimationController().setAnimationButtonsEnabled();
				break;

			case STEPBACKWARD:
				animBox.stepBackwards();
				CreateGui.getAnimator().stepBack();
				updateMouseOverInformation();
				CreateGui.getAnimationController().setAnimationButtonsEnabled();
				break;
			case PREVCOMPONENT:
				CreateGui.getCurrentTab().getTemplateExplorer().selectPrevious();
				break;
			case NEXTCOMPONENT:
				CreateGui.getCurrentTab().getTemplateExplorer().selectNext();
				break;
			default:
				break;
			}
		}

		private void updateMouseOverInformation() {
			// update mouseOverView
			for (pipe.gui.graphicElements.Place p : CreateGui.getModel().getPlaces()) {
				if (((TimedPlaceComponent) p).isAgeOfTokensShown()) {
					((TimedPlaceComponent) p).showAgeOfTokens(true);
				}
			}
			
		}

		private void actionStartAnimation() {
			try {

				if (!appView.isInAnimationMode()) {
					if (CreateGui.getCurrentTab().numberOfActiveTemplates() > 0) {
						CreateGui.getCurrentTab().rememberSelectedTemplate();
						if (CreateGui.getCurrentTab().currentTemplate().isActive()){
							CreateGui.getCurrentTab().setSelectedTemplateWasActive();
						}
						restoreMode();
						PetriNetObject.ignoreSelection(true);
						setAnimationMode(!appView.isInAnimationMode());
						if (CreateGui.getCurrentTab().templateWasActiveBeforeSimulationMode()) {								
							CreateGui.getCurrentTab().restoreSelectedTemplate();
							CreateGui.getCurrentTab().resetSelectedTemplateWasActive();
						}
						else {
							CreateGui.getCurrentTab().selectFirstActiveTemplate();
						}
						//Enable simulator focus traversal policy							
						CreateGui.appGui.setFocusTraversalPolicy(new SimulatorFocusTraversalPolicy());
					} else {
						JOptionPane.showMessageDialog(GuiFrame.this, 
								"You need at least one active template to enter simulation mode",
								"Simulation Mode Error", JOptionPane.ERROR_MESSAGE);
					}

					stepforwardAction.setEnabled(false);
					stepbackwardAction.setEnabled(false);
				} else {
					setMode(typeID);
					PetriNetObject.ignoreSelection(false);
					appView.getSelectionObject().clearSelection();
					setAnimationMode(!appView.isInAnimationMode());
					CreateGui.getCurrentTab().restoreSelectedTemplate();
					//Enable editor focus traversal policy
					CreateGui.appGui.setFocusTraversalPolicy(new EditorFocusTraversalPolicy());
				}
			} catch (Exception e) {
				Logger.log(e);
				JOptionPane.showMessageDialog(GuiFrame.this, e.toString(),
						"Simulation Mode Error", JOptionPane.ERROR_MESSAGE);
				startAction.setSelected(false);
				appView.changeAnimationMode(false);
				throw new RuntimeException(e);
			}

			if(getGUIMode().equals(GUIMode.draw)){
				activateSelectAction();

				// XXX
				// This is a fix for bug #812694 where on mac some menues are gray after
				// changing from simulation mode, when displaying a trace. Showing and 
				// hiding a menu seems to fix this problem 
				JDialog a = new JDialog(CreateGui.appGui, false);
				a.setUndecorated(true);
				a.setVisible(true);
				a.dispose();
			}
		}

	}





	class SelectAllAction extends GuiAction {

		private static final long serialVersionUID = -9223372036854775808L;

		SelectAllAction(String name, String tooltip, String keystroke) {
			super(name, tooltip, keystroke);
		}

		public void actionPerformed(ActionEvent e) {
			CreateGui.getView().getSelectionObject().selectAll();
		}
	}

	class TypeAction extends GuiAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1333311291148756241L;
		private Pipe.ElementType typeID;

		TypeAction(String name, Pipe.ElementType typeID, String tooltip, String keystroke) {
			super(name, tooltip, keystroke);
			this.typeID = typeID;
		}

		TypeAction(String name, Pipe.ElementType typeID, String tooltip, String keystroke,
				boolean toggleable) {
			super(name, tooltip, keystroke, toggleable);
			this.typeID = typeID;
		}

		public void actionPerformed(ActionEvent e) {

			this.setSelected(true);

			// deselect other actions
			if (this != transAction) {
				transAction.setSelected(false);
			}

			if (this != timedArcAction) {
				timedArcAction.setSelected(false);
			}

			if (this != timedPlaceAction) {
				timedPlaceAction.setSelected(false);
			}
			if (this != transportArcAction) {
				transportArcAction.setSelected(false);
			}

			if (this != inhibarcAction) {
				inhibarcAction.setSelected(false);
			}

			if (this != tokenAction) {
				tokenAction.setSelected(false);
			}
			if (this != deleteTokenAction) {
				deleteTokenAction.setSelected(false);
			}

			if (this != selectAction) {
				selectAction.setSelected(false);
			}
			if (this != annotationAction) {
				annotationAction.setSelected(false);
			}

			if (appView == null) {
				return;
			}

			appView.getSelectionObject().disableSelection();

			setMode(typeID);
			statusBar.changeText(typeID);

			if ((typeID != ElementType.ARC) && (appView.createArc != null)) {

				appView.createArc.delete();
				appView.createArc = null;
				appView.repaint();

			}

			if (typeID == ElementType.SELECT) {
				// disable drawing to eliminate possiblity of connecting arc to
				// old coord of moved component
				statusBar.changeText(typeID);
				appView.getSelectionObject().enableSelection();
				appView.setCursorType("arrow");
			} else if (typeID == ElementType.DRAG) {
				appView.setCursorType("move");
			} else {
				appView.setCursorType("crosshair");
			}
		}
		// }

	}



	class ToolAction extends GuiAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8910743226610517225L;

		ToolAction(String name, String tooltip, KeyStroke keyStroke) {
			super(name, tooltip, keyStroke);
		}


		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub

		}
	}

	class ZoomAction extends GuiAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 549331166742882564L;

		ZoomAction(String name, String tooltip, String keystroke) {
			super(name, tooltip, keystroke);
		}
		ZoomAction(String name, String tooltip, KeyStroke keystroke) {
			super(name, tooltip, keystroke);
		}

		public void actionPerformed(ActionEvent e) {
			// This is set to true if a valid zoom action is performed
			boolean didZoom;
			try {
				String actionName = (String) getValue(NAME);
				Zoomer zoomer = appView.getZoomController();
				TabContent tabContent = (TabContent) appTab.getSelectedComponent();
				JViewport thisView = tabContent.drawingSurfaceScrollPane().getViewport();
				String selectedZoomLevel = null;

				/*
				 * Zoom action name overview
				 * Zoom in: the zoom IN icon in panel has been pressed
				 * Zoom out: the zoom OUT icon in panel has been pressed
				 * Zoom: a specific zoom level has been chosen in drop down or in the menu.
				 */
				switch (actionName) {
					case "Zoom in":
						didZoom = zoomer.zoomIn();
						break;
					case "Zoom out":
						didZoom = zoomer.zoomOut();
						break;
					default:
						if (actionName.equals("Zoom")) {
							selectedZoomLevel = (String) zoomComboBox.getSelectedItem();
						}
						if (e.getSource() instanceof JMenuItem) {
							selectedZoomLevel = ((JMenuItem) e.getSource()).getText();
						}

						//parse selected zoom level, and strip of %.
						int newZoomLevel = Integer.parseInt(selectedZoomLevel.replace("%", ""));

						didZoom = zoomer.setZoom(newZoomLevel);
						break;
				}
				if (didZoom) {
					updateZoomCombo();

					double midpointX = Zoomer.getUnzoomedValue(thisView.getViewPosition().x
							+ (thisView.getWidth() * 0.5), zoomer.getPercent());
					double midpointY = Zoomer.getUnzoomedValue(thisView.getViewPosition().y
							+ (thisView.getHeight() * 0.5), zoomer.getPercent());

					java.awt.Point midpoint = new java.awt.Point((int) midpointX, (int) midpointY);

					appView.zoomTo(midpoint);
				}
			} catch (ClassCastException cce) {
				// zoom
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}
	
	

	class ViewAction extends GuiAction {

		private static final long serialVersionUID = -5145846750992454638L;

		ViewAction(String name, String tooltip, String keystroke, boolean toggleable) {
			super(name, tooltip, keystroke, toggleable);
		}

		ViewAction(String name, String tooltip, KeyStroke keystroke, boolean toggleable) {
			super(name, tooltip, keystroke, toggleable);
		}
		
		ViewAction(String name, String tooltip, boolean toggleable) {
			super(name, tooltip, toggleable);
		}


		public void actionPerformed(ActionEvent arg0) {

			if (this == showComponentsAction){
				toggleComponents();
			} else if (this == showQueriesAction){
				toggleQueries();
			} else if (this == showConstantsAction){
				toggleConstants();
			} else if (this == showZeroToInfinityIntervalsAction) {
				toggleZeroToInfinityIntervals();
			} else if (this == showEnabledTransitionsAction) {
				toggleEnabledTransitionsList();
			} else if (this == showDelayEnabledTransitionsAction) {
				toggleDelayEnabledTransitions();
			} else if (this == showToolTipsAction) {
				toggleToolTips();
			} else if (this == showTokenAgeAction) {
				toggleTokenAge();
			} else if (this == showAdvancedWorkspaceAction){
				showAdvancedWorkspace(true);
			} else if (this == showSimpleWorkspaceAction){
				showAdvancedWorkspace(false);
			} else if (this == saveWorkSpaceAction){
				saveWorkspace();
			}
		}

	}
	public void showAbout() {
		StringBuilder buffer = new StringBuilder("About " + TAPAAL.getProgramName());
		buffer.append("\n\n");
		buffer.append("TAPAAL is a tool for editing, simulation and verification of P/T and timed-arc Petri nets.\n");
		buffer.append("The GUI is based on PIPE2: http://pipe2.sourceforge.net/\n\n");
		buffer.append("License information and more is availabe at: www.tapaal.net\n\n");
		buffer.append("Credits\n\n");
		buffer.append("TAPAAL GUI and Translations:\n");
		buffer.append("Mathias Andersen, Sine V. Birch, Jacob Hjort Bundgaard, Joakim Byg, Jakob Dyhr,\nLouise Foshammer, Malte Neve-Graesboell, ");
		buffer.append("Lasse Jacobsen, Morten Jacobsen,\nThomas S. Jacobsen, Jacob J. Jensen, Peter G. Jensen, ");
		buffer.append("Mads Johannsen,\nKenneth Y. Joergensen, Mikael H. Moeller, Christoffer Moesgaard, Niels N. Samuelsen,\nJiri Srba, Mathias G. Soerensen, Jakob H. Taankvist and Peter H. Taankvist\n");
		buffer.append("Aalborg University 2009-2018\n\n");
		buffer.append("TAPAAL Continuous Engine (verifytapn):\n");
		buffer.append("Alexandre David, Lasse Jacobsen, Morten Jacobsen and Jiri Srba\n");
		buffer.append("Aalborg University 2011-2018\n\n");
		buffer.append("TAPAAL Discrete Engine (verifydtapn):\n");
                buffer.append("Mathias Andersen, Peter G. Jensen, Heine G. Larsen, Jiri Srba,\n");
		buffer.append("Mathias G. Soerensen and Jakob H. Taankvist\n");
                buffer.append("Aalborg University 2012-2018\n\n");
		buffer.append("TAPAAL Untimed Engine (verifypn):\n");
                buffer.append("Frederik Meyer Boenneland, Jakob Dyhr, Peter Fogh, ");
                buffer.append("Jonas F. Jensen,\nLasse S. Jensen, Peter G. Jensen, ");
                buffer.append("Tobias S. Jepsen, Mads Johannsen,\nIsabella Kaufmann, ");
                buffer.append("Andreas H. Klostergaard, Soeren M. Nielsen,\nThomas S. Nielsen, Lars K. Oestergaard, ");
                buffer.append("Samuel Pastva and Jiri Srba\n");
                buffer.append("Aalborg University 2014-2018\n\n");


		buffer.append("\n");
		JOptionPane.showMessageDialog(null, buffer.toString(), "About " + TAPAAL.getProgramName(),
				JOptionPane.INFORMATION_MESSAGE, ResourceManager.appIcon());
	}


	private static void openBrowser(URI url){
		//open the default bowser on this page
		try {
			java.awt.Desktop.getDesktop().browse(url);
		} catch (IOException e) {
			Logger.log("Cannot open the browser.");
			JOptionPane.showMessageDialog(null, "There was a problem opening the default web browser \n" +
					"Please open the url in your browser by entering " + url.toString(), 
					"Error opening browser", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public static void showInBrowser(String address) {
		try {
			URI url = new URI(address);
			openBrowser(url);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			Logger.log("Error convering to URL");
			e.printStackTrace();
		}
	}





	public void exit(){
		if (checkForSaveAll()) {
			dispose();
			System.exit(0);
		}
	}

	
	
	private JMenu buildMenuFiles(int shortcutkey) {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');

		createAction = new GuiAction("New", "Create a new Petri net", "ctrl N") {
			public void actionPerformed(ActionEvent arg0) {
				showNewPNDialog();
			}
		};
		fileMenu.add(createAction);

		fileMenu.add(openAction = new GuiAction("Open", "Open", "ctrl O") {
			public void actionPerformed(ActionEvent arg0) {
				File[] files = new FileBrowser(CreateGui.userPath).openFiles();
				for (File f : files) {
					if (f.exists() && f.isFile() && f.canRead()) {
						CreateGui.userPath = f.getParent();
						createNewTabFromFile(f, false);
					}
				}
			}
		});

		fileMenu.add(closeAction = new GuiAction("Close", "Close the current tab", "ctrl W") {
			public void actionPerformed(ActionEvent arg0) {

				if ((appTab.getTabCount() > 0) && checkForSave()) {
					// Set GUI mode to noNet
					setGUIMode(GUIMode.noNet);

					int index = appTab.getSelectedIndex();
					appTab.remove(index);
					CreateGui.removeTab(index);
				}

			}

		});

		fileMenu.addSeparator();

		fileMenu.add(saveAction = new GuiAction("Save", "Save", "ctrl S") {
			public void actionPerformed(ActionEvent arg0) {
				 if (canNetBeSavedAndShowMessage()) {
                     saveOperation(false); 
				 }
			}			
		});
		
		
		fileMenu.add(saveAsAction = new GuiAction("Save as", "Save as...", KeyStroke.getKeyStroke('S', (shortcutkey + InputEvent.SHIFT_MASK))) {
			public void actionPerformed(ActionEvent arg0) {
				if (canNetBeSavedAndShowMessage()) {
                    saveOperation(true); 
				}	
			}
		});

				
		// Import menu
		importMenu = new JMenu("Import");
		importMenu.setIcon(new ImageIcon(
				Thread.currentThread().getContextClassLoader().getResource(CreateGui.imgPath + "Export.png")
		));
		
		importMenu.add(importPNMLAction = new GuiAction("PNML untimed net", "Import an untimed net in the PNML format", KeyStroke.getKeyStroke('X', shortcutkey)) {
			public void actionPerformed(ActionEvent arg0) {
				File[] files = new FileBrowser("Import PNML", "pnml", CreateGui.userPath).openFiles();
				for(File f : files){
					if(f.exists() && f.isFile() && f.canRead()){
						CreateGui.userPath = f.getParent();
						createNewTabFromFile(f, true);
					}
				}
			}
		});
		

		importMenu.add(importSUMOAction = new GuiAction("SUMO queries (.txt)", "Import SUMO queries in a plain text format") {
			public void actionPerformed(ActionEvent arg0) {
				File[] files = new FileBrowser("Import SUMO", "txt", CreateGui.userPath).openFiles();
				for(File f : files){
					if(f.exists() && f.isFile() && f.canRead()){
						CreateGui.userPath = f.getParent();
						SUMOQueryLoader.importQueries(f, CreateGui.getCurrentTab().network());
					}
				}
			}
		});

		importMenu.add(
				importXMLAction = new GuiAction("XML queries (.xml)", "Import MCC queries in XML format", KeyStroke.getKeyStroke('R', shortcutkey)) {
					public void actionPerformed(ActionEvent arg0) {
						File[] files = new FileBrowser("Import XML queries", "xml", CreateGui.userPath).openFiles();
						for(File f : files){
							if(f.exists() && f.isFile() && f.canRead()){
								CreateGui.userPath = f.getParent();
								XMLQueryLoader.importQueries(f, CreateGui.getCurrentTab().network());
							}
						}
					}	
				});
		fileMenu.add(importMenu);

		// Export menu
		exportMenu = new JMenu("Export");
		exportMenu.setIcon(new ImageIcon(
				Thread.currentThread().getContextClassLoader().getResource(CreateGui.imgPath + "Export.png")));
		
		exportMenu.add(exportPNGAction = new GuiAction("PNG", "Export the net to PNG format", KeyStroke.getKeyStroke('G', shortcutkey)) {
			public void actionPerformed(ActionEvent arg0) {
				if (canNetBeSavedAndShowMessage()) {
                    Export.exportGuiView(appView, Export.PNG, null);
				}
			}
		});

		exportMenu.add(exportPSAction = new GuiAction("PostScript", "Export the net to PostScript format", KeyStroke.getKeyStroke('T', shortcutkey)) {
			public void actionPerformed(ActionEvent arg0) {
				if (canNetBeSavedAndShowMessage()) {
                    Export.exportGuiView(appView, Export.POSTSCRIPT, null);
				}
			}
		});


		exportMenu.add(exportToTikZAction = new GuiAction("TikZ", "Export the net to LaTex (TikZ) format", KeyStroke.getKeyStroke('L', shortcutkey)) {
			public void actionPerformed(ActionEvent arg0) {
				if (canNetBeSavedAndShowMessage()) {
                    Export.exportGuiView(appView, Export.TIKZ, appView.getGuiModel());
				}
			}
		});


		exportMenu.add(exportToPNMLAction = new GuiAction("PNML", "Export the net to PNML format", KeyStroke.getKeyStroke('D', shortcutkey)) {
			public void actionPerformed(ActionEvent arg0) {
				if (canNetBeSavedAndShowMessage()) {
                    if(Preferences.getInstance().getShowPNMLWarning()) {
                            JCheckBox showAgain = new JCheckBox("Do not show this warning.");
                            String message = "In the saved PNML all timing information will be lost\n" +
                                    	"and the components in the net will be merged into one big net.";
                            Object[] dialogContent = {message, showAgain};
                            JOptionPane.showMessageDialog(null, dialogContent, 
                                    "PNML loss of information", JOptionPane.WARNING_MESSAGE);
                            Preferences.getInstance().setShowPNMLWarning(!showAgain.isSelected());
                    }
                    Export.exportGuiView(appView, Export.PNML, null);
				}
			}
		});
		
		
		exportMenu.add(exportToXMLAction = new GuiAction("XML Queries", "Export the queries to XML format", KeyStroke.getKeyStroke('H', shortcutkey)) {
			public void actionPerformed(ActionEvent arg0) {
				if (canNetBeSavedAndShowMessage()) {
					Export.exportGuiView(appView, Export.QUERY, null);
				}
			}
		});


		fileMenu.add(exportMenu);

		fileMenu.addSeparator();
		fileMenu.add(printAction = new GuiAction("Print", "Print", KeyStroke.getKeyStroke('P', shortcutkey)) {
			public void actionPerformed(ActionEvent arg0) {
				Export.exportGuiView(appView, Export.PRINTER, null);
			}
		});
		
		fileMenu.addSeparator();

		// Loads example files, retuns null if not found
		String[] nets = loadTestNets();

		// Oliver Haggarty - fixed code here so that if folder contains non
		// .xml file the Example x counter is not incremented when that file
		// is ignored
		if (nets != null && nets.length > 0) {
			JMenu exampleMenu = new JMenu("Example nets");
			exampleMenu.setIcon(new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(CreateGui.imgPath + "Example.png")));
			
			for (String filename : nets) {
				if (filename.toLowerCase().endsWith(".xml")) {
					
					String netname = filename.replace(".xml", "");
					
					GuiAction tmp = new GuiAction(netname, "Open example file \"" + netname + "\"") {
						public void actionPerformed(ActionEvent arg0) {
							InputStream file = Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/Example nets/" + filename);
							createNewTabFromFile(file, filename);
						}
					};
					tmp.putValue(Action.SMALL_ICON, new ImageIcon(Thread.currentThread()
							.getContextClassLoader().getResource(
									CreateGui.imgPath + "Net.png")));
					exampleMenu.add(tmp);
				}
			}
			fileMenu.add(exampleMenu);
			fileMenu.addSeparator();
		}

		fileMenu.add(exitAction = new GuiAction("Exit", "Close the program", KeyStroke.getKeyStroke('Q', shortcutkey)) {
			public void actionPerformed(ActionEvent arg0) {
				exit();
			}
		});

		return fileMenu;
	}

	/**
	 * The function loads the example nets as InputStream from the resources
	 * Notice the check for if we are inside a jar file, as files inside a jar cant
	 * be listed in the normal way.
	 * 
	 * @author Kenneth Yrke Joergensen <kenneth@yrke.dk>, 2011-06-27
	 */
	private String[] loadTestNets() {
		
		
		String[] nets = null;

		try {
			URL dirURL = Thread.currentThread().getContextClassLoader().getResource("resources/Example nets/");
			if (dirURL != null && dirURL.getProtocol().equals("file")) {
				/* A file path: easy enough */
				nets = new File(dirURL.toURI()).list();
			}

			if (dirURL == null) {
				/*
				 * In case of a jar file, we can't actually find a directory. Have to assume the
				 * same jar as clazz.
				 */
				String me = this.getName().replace(".", "/") + ".class";
				dirURL = Thread.currentThread().getContextClassLoader().getResource(me);
			}

			if (dirURL.getProtocol().equals("jar")) {
				/* A JAR path */
				String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf('!')); // strip out only the JAR
																								// file
				JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
				Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
				Set<String> result = new HashSet<String>(); // avoid duplicates in case it is a subdirectory
				while (entries.hasMoreElements()) {
					String name = entries.nextElement().getName();
					if (name.startsWith("resources/Example nets/")) { // filter according to the path
						String entry = name.substring("resources/Example nets/".length());
						int checkSubdir = entry.indexOf('/');
						if (checkSubdir >= 0) {
							// if it is a subdirectory, we just return the directory name
							entry = entry.substring(0, checkSubdir);
						}
						result.add(entry);
					}
				}
				nets = result.toArray(new String[result.size()]);
				jar.close();
			}

			Arrays.sort(nets, new Comparator<String>() {
				public int compare(String one, String two) {

					int toReturn = one.compareTo(two);
					// Special hack to get intro-example first
					if (one.equals("intro-example.xml")) {
						toReturn = -1;
					}
					if (two.equals("intro-example.xml")) {
						toReturn = 1;
					}
					return toReturn;
				}
			});
		} catch (Exception e) {
			Logger.log("Error getting example files:" + e);
			e.printStackTrace();
		}
		return nets;
	}
	

	class EditAction extends GuiAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2402602825981305085L;

		EditAction(String name, String tooltip, String keystroke) {
			super(name, tooltip, keystroke);
		}
		EditAction(String name, String tooltip, KeyStroke keystroke) {
			super(name, tooltip, keystroke);
		}

		public void actionPerformed(ActionEvent e) {

			if (CreateGui.getApp().isEditionAllowed()) {
				/*
				 * if (this == cutAction) { ArrayList selection =
				 * appView.getSelectionObject().getSelection();
				 * appGui.getCopyPasteManager().setUpPaste(selection, appView);
				 * appView.getUndoManager().newEdit(); // new "transaction""
				 * appView.getUndoManager().deleteSelection(selection);
				 * appView.getSelectionObject().deleteSelection();
				 * pasteAction.setEnabled
				 * (appGui.getCopyPasteManager().pasteEnabled()); } else if
				 * (this == copyAction) {
				 * appGui.getCopyPasteManager().setUpPaste(
				 * appView.getSelectionObject().getSelection(), appView);
				 * pasteAction
				 * .setEnabled(appGui.getCopyPasteManager().pasteEnabled()); }
				 * else if (this == pasteAction) {
				 * appView.getSelectionObject().clearSelection();
				 * appGui.getCopyPasteManager().startPaste(appView); } else
				 */if (this == undoAction) {
					 appView.getUndoManager().undo();
					 CreateGui.getCurrentTab().network().buildConstraints();
				 } else if (this == redoAction) {
					 appView.getUndoManager().redo();
					 CreateGui.getCurrentTab().network().buildConstraints();
				 }				 
			}
		}
	}

	/**
	 * A JToggleButton that watches an Action for selection change
	 * 
	 * @author Maxim
	 * 
	 *         Selection must be stored in the action using
	 *         putValue("selected",Boolean);
	 */
	class ToggleButton extends JToggleButton implements PropertyChangeListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5085200741780612997L;

		public ToggleButton(Action a) {
			super(a);
			if (a.getValue(Action.SMALL_ICON) != null) {
				// toggle buttons like to have images *and* text, nasty
				setText(null);
			}
			this.setRequestFocusEnabled(false);
		}

		public void propertyChange(PropertyChangeEvent evt) {
		}

	}



	public void setEnabledStepForwardAction(boolean b) {
		stepforwardAction.setEnabled(b);
	}

	private void showNewPNDialog() {
		// Build interface
		EscapableDialog guiDialog = new EscapableDialog(CreateGui.getApp(),
				"Create a New Petri Net", true);

		Container contentPane = guiDialog.getContentPane();

		// 1 Set layout
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

		// 2 Add Place editor
		contentPane.add(new NewTAPNPanel(guiDialog.getRootPane(), this));

		guiDialog.setResizable(false);

		// Make window fit contents' preferred size
		guiDialog.pack();

		// Move window to the middle of the screen
		guiDialog.setLocationRelativeTo(null);
		guiDialog.setVisible(true);

	}

	public void setEnabledStepBackwardAction(boolean b) {
		stepbackwardAction.setEnabled(b);
	}


	public void setStepShotcutEnabled(boolean enabled){
		if(enabled){
			stepforwardAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("pressed RIGHT"));
			stepbackwardAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("pressed LEFT"));
		} else {
			stepforwardAction.putValue(Action.ACCELERATOR_KEY, null);
			stepbackwardAction.putValue(Action.ACCELERATOR_KEY, null);
		}
	}

	public int getNameCounter() {
		return newNameCounter;
	}

	public void incrementNameCounter() {
		newNameCounter++;
	}

	public String getCurrentTabName(){
		return appTab.getTitleAt(appTab.getSelectedIndex());
	}

	public boolean isShowingDelayEnabledTransitions() {
		return showDelayEnabledTransitions;
	}

}
