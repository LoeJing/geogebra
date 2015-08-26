package org.geogebra.web.html5.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.DrawEquation;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.factories.Factory;
import org.geogebra.common.factories.SwingFactory;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.menubar.MenuInterface;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.kernel.AnimationManager;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.UndoManager;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.barycentric.AlgoCubicSwitch;
import org.geogebra.common.kernel.barycentric.AlgoKimberlingWeights;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.AlgoCubicSwitchInterface;
import org.geogebra.common.main.AlgoCubicSwitchParams;
import org.geogebra.common.main.AlgoKimberlingWeightsInterface;
import org.geogebra.common.main.AlgoKimberlingWeightsParams;
import org.geogebra.common.main.App;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.FontManager;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.common.move.events.BaseEventPool;
import org.geogebra.common.move.events.NativeEventAttacher;
import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.Provider;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.operations.Network;
import org.geogebra.common.move.operations.NetworkOperation;
import org.geogebra.common.move.views.OfflineView;
import org.geogebra.common.plugin.ScriptManager;
import org.geogebra.common.plugin.SensorLogger;
import org.geogebra.common.sound.SoundManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.CopyPaste;
import org.geogebra.common.util.Language;
import org.geogebra.common.util.MD5EncrypterGWTImpl;
import org.geogebra.common.util.NormalizerMinimal;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.euclidian.EuclidianControllerW;
import org.geogebra.web.html5.euclidian.EuclidianPanelWAbstract;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.MouseTouchGestureControllerW;
import org.geogebra.web.html5.gui.AlgebraInput;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.LoadingApplication;
import org.geogebra.web.html5.gui.ToolBarInterface;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.ViewsChangedListener;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.io.ConstructionException;
import org.geogebra.web.html5.io.MyXMLioW;
import org.geogebra.web.html5.javax.swing.GOptionPaneW;
import org.geogebra.web.html5.js.JavaScriptInjector;
import org.geogebra.web.html5.kernel.AnimationManagerW;
import org.geogebra.web.html5.kernel.UndoManagerW;
import org.geogebra.web.html5.kernel.commands.CommandDispatcherW;
import org.geogebra.web.html5.move.googledrive.GoogleDriveOperation;
import org.geogebra.web.html5.sound.SoundManagerW;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.DynamicScriptElement;
import org.geogebra.web.html5.util.ImageManagerW;
import org.geogebra.web.html5.util.MyDictionary;
import org.geogebra.web.html5.util.ScriptLoadCallback;
import org.geogebra.web.html5.util.SpreadsheetTableModelW;
import org.geogebra.web.html5.util.UUIDW;
import org.geogebra.web.html5.util.View;
import org.geogebra.web.plugin.WebsocketLogger;
import org.geogebra.web.web.gui.view.algebra.RadioButtonTreeItem;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageMap;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class AppW extends App implements SetLabels {
	public static final String STORAGE_MACRO_KEY = "storedMacro";
	public static final String STORAGE_MACRO_ARCHIVE = "macroArchive";
	public static final String DEFAULT_APPLET_ID = "ggbApplet";


	private DrawEquationWeb drawEquation;

	private NormalizerMinimal normalizerMinimal;
	private GgbAPIW ggbapi;
	private final LocalizationW loc;
	private ImageManagerW imageManager;
	private HashMap<String, String> currentFile = null;
	private LinkedList<Map<String, String>> fileList = new LinkedList<Map<String, String>>();
	// random id to identify ggb files
	// eg so that GeoGebraTube can notice it's a version of the same file
	private String uniqueId = UUIDW.randomUUID().toString();
	private int localID = -1;
	private long syncStamp;
	protected GoogleDriveOperation googleDriveOperation;

	public static final String LOCALE_PARAMETER = "locale";

	private FontManagerW fontManager;
	private SpreadsheetTableModelW tableModel;
	private SoundManagerW soundManager;
	protected DialogManager dialogManager = null;

	protected FileManagerI fm;
	private Material activeMaterial;

	protected final ArticleElement articleElement;
	private String ORIGINAL_BODY_CLASSNAME = "";

	protected EuclidianPanelWAbstract euclidianViewPanel;
	protected Canvas canvas;

	private final GLookAndFeelI laf;

	private ArrayList<Widget> popups = new ArrayList<Widget>();
	private static boolean justClosedPopup = false;
	// protected GeoGebraFrame frame = null;
	private ErrorHandler errorHandler;
	private GlobalKeyDispatcherW globalKeyDispatcher;

	// when losing focus, remembering it so that ENTER can give focus back
	public static Element lastActiveElement = null;
	// but not in case of anything important in any app has focus,
	// we shall set it to true in each of those cases, e.g. AV input bar too !!!
	public static boolean anyAppHasFocus = true;

	/**
	 * @param ae
	 *            {@link ArticleElement}
	 * @param dimension
	 *            int
	 * @param laf
	 *            {@link GLookAndFeelI}
	 */
	protected AppW(ArticleElement ae, int dimension, GLookAndFeelI laf) {
		super();
		setPrerelease(ae.getDataParamPrerelease());
		this.loc = new LocalizationW(dimension);
		this.articleElement = ae;
		this.laf = laf;

		getTimerSystem();
		this.showInputTop = InputPositon.algebraView;
		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				windowResized();
			}
		});

	}

	protected final void windowResized() {
		for (MouseTouchGestureControllerW mtg : this.euclidianHandlers) {
			mtg.calculateEnvironment();
		}
		if (this.getGuiManager() != null) {
			getGuiManager().setPixelRatio(getPixelRatio());
		}

	}

	@Override
	public final String getUniqueId() {
		return uniqueId;
	}

	@Override
	public final void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	@Override
	public final void resetUniqueId() {
		uniqueId = UUIDW.randomUUID().toString();
		setTubeId(0);
	}

	/**
	 * @return id of local saved file
	 */
	public int getLocalID() {
		return this.localID;
	}

	/**
	 * sets ID of local saved file
	 * 
	 * @param id
	 *            int
	 */
	public void setLocalID(int id) {
		this.localID = id;
	}

	@Override
	public final DrawEquation getDrawEquation() {
		if (drawEquation == null) {
			drawEquation = new DrawEquationWeb();
		}

		return drawEquation;
	}

	@Override
	public final SoundManager getSoundManager() {
		if (soundManager == null) {
			soundManager = new SoundManagerW(this);
		}
		return soundManager;
	}

	@Override
	public org.geogebra.web.html5.main.GgbAPIW getGgbApi() {
		if (ggbapi == null) {
			ggbapi = new org.geogebra.web.html5.main.GgbAPIW(this);
		}
		return ggbapi;
	}

	/**
	 * @return {@link Canvas}
	 */
	public Canvas getCanvas() {
		return canvas;
	}

	@Override
	public final StringType getPreferredFormulaRenderingType() {
		return StringType.LATEX;
	}

	@Override
	public final NormalizerMinimal getNormalizer() {
		if (normalizerMinimal == null) {
			normalizerMinimal = new NormalizerMinimal();
		}

		return normalizerMinimal;
	}

	@Override
	public final SwingFactory getSwingFactory() {
		return SwingFactory.getPrototype();
	}

	/**
	 * inits factories
	 */
	protected void initFactories() {
		org.geogebra.common.factories.FormatFactory.prototype = new org.geogebra.web.html5.factories.FormatFactoryW();
		org.geogebra.common.factories.AwtFactory.prototype = new org.geogebra.web.html5.factories.AwtFactoryW();
		org.geogebra.common.euclidian.EuclidianStatic.prototype = new org.geogebra.web.html5.euclidian.EuclidianStaticW();
		org.geogebra.common.factories.SwingFactory
		        .setPrototype(new org.geogebra.web.html5.factories.SwingFactoryW());
		org.geogebra.common.util.StringUtil.prototype = new org.geogebra.common.util.StringUtil();
		org.geogebra.common.factories.CASFactory
		        .setPrototype(new org.geogebra.web.html5.factories.CASFactoryW());
		if (!is3D()) {
			org.geogebra.common.util.CopyPaste.INSTANCE = new CopyPaste();
		}
	}

	protected void afterCoreObjectsInited() {
	} // TODO: abstract?

	@Override
	final public GlobalKeyDispatcherW getGlobalKeyDispatcher() {
		if (globalKeyDispatcher == null) {
			globalKeyDispatcher = newGlobalKeyDispatcher();
		}
		return globalKeyDispatcher;
	}

	/**
	 * @return a new instance of {@link GlobalKeyDispatcherW}
	 */
	private GlobalKeyDispatcherW newGlobalKeyDispatcher() {
		return new GlobalKeyDispatcherW(this);
	}

	@Override
	public EuclidianViewW getEuclidianView1() {
		return (EuclidianViewW) euclidianView;
	}

	private TimerSystemW timers;

	public TimerSystemW getTimerSystem() {
		if (timers == null) {
			timers = new TimerSystemW(this);
		}
		return timers;
	}

	public void syncAppletPanelSize(int width, int height, int evNo) {
		// TODO Auto-generated method stub

	}

	@Override
	public ScriptManager getScriptManager() {
		if (scriptManager == null) {
			scriptManager = new ScriptManagerW(this);
		}
		return scriptManager;
	}

	// ================================================
	// NATIVE JS
	// ================================================

	public native void evalScriptNative(String script) /*-{
		$wnd.eval(script);
	}-*/;

	public native void callNativeJavaScript(String funcname) /*-{
		if ($wnd[funcname]) {
			$wnd[funcname]();
		}
	}-*/;

	public native void callNativeJavaScript(String funcname, String arg) /*-{
		if ($wnd[funcname]) {
			$wnd[funcname](arg);
		}
	}-*/;

	public native void callNativeJavaScriptMultiArg(String funcname,
	        JavaScriptObject arg) /*-{
		if ($wnd[funcname]) {
			$wnd[funcname](arg);
		}
	}-*/;

	@Override
	public void callAppletJavaScript(String fun, Object[] args) {
		if (args == null || args.length == 0) {
			callNativeJavaScript(fun);
		} else if (args.length == 1) {
			App.debug("calling function: " + fun + "(" + args[0].toString()
			        + ")");
			callNativeJavaScript(fun, args[0].toString());
		} else {
			JsArrayString jsStrings = (JsArrayString) JsArrayString
			        .createArray();
			for (Object obj : args) {
				jsStrings.push(obj.toString());
			}
			callNativeJavaScriptMultiArg(fun, jsStrings);
		}

	}

	private MyXMLioW xmlio;
	private boolean toolLoadedFromStorage;
	private Storage storage;

	@Override
	public boolean loadXML(String xml) throws Exception {
		getXMLio().processXMLString(xml, true, false);
		return true;
	}

	@Override
	public MyXMLioW getXMLio() {
		if (xmlio == null) {
			xmlio = createXMLio(kernel.getConstruction());
		}
		return xmlio;
	}

	@Override
	public MyXMLioW createXMLio(Construction cons) {
		return new MyXMLioW(cons.getKernel(), cons);
	}

	void doSetLanguage(String lang) {
		resetCommandDictionary();

		((LocalizationW) getLocalization()).setLanguage(lang);

		// make sure digits are updated in all numbers
		getKernel().updateConstructionLanguage();

		// update display & Input Bar Dictionary etc
		setLabels();

		// inputField.setDictionary(getCommandDictionary());

		examWelcome();
	}

	public final void setLanguage(final String browserLang) {
		if (browserLang != null && browserLang.equals(loc.getLanguage())) {
			setLabels();
			return;
		}

		if (browserLang == null || "".equals(browserLang)) {

			App.error("language being set to empty string");
			setLanguage("en");
			return;
		}
		final String lang = Language
		        .getClosestGWTSupportedLanguage(browserLang);
		App.debug("setting language to:" + lang + ", browser lang:"
		        + browserLang);


		if (Browser.supportsSessionStorage() && loadPropertiesFromStorage(lang)) {
			App.debug("properties loaded from local storage");
			doSetLanguage(lang);
		} else {
			// load keys (into a JavaScript <script> tag)
			DynamicScriptElement script = (DynamicScriptElement) Document.get()
			        .createScriptElement();
			script.setSrc(GWT.getModuleBaseURL() + "js/properties_keys_" + lang
			        + ".js");
			script.addLoadHandler(new ScriptLoadCallback() {

				@Override
				public void onLoad() {
					// force reload
					doSetLanguage(lang);
					if (Browser.supportsSessionStorage()) {
						savePropertiesToStorage(lang);
					}
				}

			});
			Document.get().getBody().appendChild(script);

		}
	}

	private native boolean loadPropertiesFromStorage(String lang) /*-{
		var storedTranslation = {};
		if ($wnd.localStorage && $wnd.localStorage.translation) {
			try {
				storedTranslation = JSON.parse(localStorage.translation);
			} catch (e) {
				console.log(e.message);
			}
		}
		if (storedTranslation && storedTranslation[lang]) {
			$wnd["__GGB__keysVar"] = {};
			$wnd["__GGB__keysVar"][lang] = storedTranslation[lang];
			return true;
		}
		return false;
	}-*/;

	/**
	 * Saves properties loaded from external JSON to localStorage
	 * 
	 * @param lang
	 *            language
	 */
	native void savePropertiesToStorage(String lang) /*-{
		var storedTranslation = {};
		if ($wnd.localStorage && $wnd["__GGB__keysVar"]
				&& $wnd["__GGB__keysVar"][lang]) {
			var obj = {};
			obj[lang] = $wnd.__GGB__keysVar[lang];
			$wnd.localStorage.translation = JSON.stringify(obj);
		}
	}-*/;

	/**
	 * @param language
	 *            language ISO code
	 * @param country
	 *            country or country_variant
	 */
	public void setLanguage(String language, String country) {

		if (language == null || "".equals(language)) {
			Log.warn("error calling setLanguage(), setting to English (US): "
			        + language + "_" + country);
			setLanguage("en");
			return;
		}

		if (country == null || "".equals(country)) {
			setLanguage(language);
			return;
		}
		this.setLanguage(language + "_" + country);
	}

	@Override
	public Localization getLocalization() {
		return loc;
	}

	/**
	 * Translates localized command name into internal TODO check whether this
	 * differs from translateCommand somehow and either document it or remove
	 * this method
	 * 
	 * @param cmd
	 *            localized command name
	 * @return internal command name
	 */
	@Override
	final public String getInternalCommand(String cmd) {
		initTranslatedCommands();
		String s;
		String cmdLower = StringUtil.toLowerCase(cmd);
		Commands[] values = Commands.values();
		if (revTranslateCommandTable.isEmpty()) {// we should clear this cache
												 // on language change!
			for (Commands c : values) {// and fill it now if needed
				s = Commands.englishToInternal(c).name();

				// make sure that when si[] is typed in script, it's changed to
				// Si[] etc
				String lowerCaseCmd = StringUtil.toLowerCase(getLocalization()
				        .getCommand(s));
				revTranslateCommandTable.put(lowerCaseCmd, s);
			}
		}
		return revTranslateCommandTable.get(cmdLower);
		// return null;
	}

	HashMap<String, String> revTranslateCommandTable = new HashMap<String, String>();

	@Override
	protected void fillCommandDict() {
		super.fillCommandDict();
		revTranslateCommandTable.clear();
	}

	/**
	 * This method checks if the command is stored in the command properties
	 * file as a key or a value.
	 * 
	 * @param command
	 *            : a value that should be in the command properties files (part
	 *            of Internationalization)
	 * @return the value "command" after verifying its existence.
	 */
	@Override
	final public String getReverseCommand(String command) {

		if (loc.getLanguage() == null) {
			// keys not loaded yet
			return command;
		}

		return super.getReverseCommand(command);
	}

	@Override
	public String getEnglishCommand(String pageName) {
		loc.initCommand();
		// String ret = commandConstants
		// .getString(crossReferencingPropertiesKeys(pageName));
		// if (ret != null)
		// return ret;
		return pageName;
	}

	public void loadGgbFile(HashMap<String, String> archiveContent)
	        throws Exception {
		loadFile(archiveContent);
	}

	/**
	 * @param dataUrl
	 *            the data url to load the ggb file
	 */
	public void loadGgbFileAsBase64Again(String dataUrl) {
		prepareReloadGgbFile();
		View view = new View(null, this);
		view.processBase64String(dataUrl);
	}

	public void loadGgbFileAsBinaryAgain(JavaScriptObject binary) {
		prepareReloadGgbFile();
		View view = new View(null, this);
		view.processBinaryString(binary);
	}

	private void prepareReloadGgbFile() {
		((DrawEquationWeb) getDrawEquation())
		        .deleteLaTeXes((EuclidianViewW) getActiveEuclidianView());
		getImageManager().reset();
	}

	private void loadFile(HashMap<String, String> archiveContent)
	        throws Exception {
		beforeLoadFile();

		HashMap<String, String> archive = (HashMap<String, String>) archiveContent
		        .clone();

		// Handling of construction and macro file
		String construction = archive.remove(MyXMLio.XML_FILE);
		String macros = archive.remove(MyXMLio.XML_FILE_MACRO);
		String defaults2d = archive.remove(MyXMLio.XML_FILE_DEFAULTS_2D);
		String defaults3d = null;
		if (is3D()) {
			defaults3d = archive.remove(MyXMLio.XML_FILE_DEFAULTS_3D);
		}
		String libraryJS = archive.remove(MyXMLio.JAVASCRIPT_FILE);

		// Construction (required)
		if (construction == null && macros == null) {
			throw new ConstructionException(
			        "File is corrupt: No GeoGebra data found");
		}

		if (construction != null) {
			// ggb file: remove all macros from kernel before processing
			kernel.removeAllMacros();
		}

		// Macros (optional)
		// moved after the images are loaded, because otherwise
		// exception might come for macros which use images
		//if (macros != null) {
		//	// macros = DataUtil.utf8Decode(macros);
		//	// //DataUtil.utf8Decode(macros);
		//	getXMLio().processXMLString(macros, true, true);
		//}

		// Library JavaScript (optional)
		if (libraryJS == null) { // TODO: && !isGGTfile)
			kernel.resetLibraryJavaScript();
		} else {
			kernel.setLibraryJavaScript(libraryJS);
		}

		if (archive.entrySet() != null) {
			for (Entry<String, String> entry : archive.entrySet()) {
				maybeProcessImage(entry.getKey(), entry.getValue());
			}
		}

		if (construction == null) {
			if (macros != null) {
				getXMLio().processXMLString(macros, true, true);
			}
			setCurrentFile(archiveContent);
			afterLoadFileAppOrNot();
			if (!hasMacroToRestore()) {
				getGuiManager().refreshCustomToolsInToolBar();
			}
			getGuiManager().updateToolbar();
			return;
		}

		if (!getImageManager().hasImages()) {
			// Process Construction
			// construction =
			// DataUtil.utf8Decode(construction);//DataUtil.utf8Decode(construction);

			// Before opening the file,
			// hide navigation bar for construction steps if visible.
			// (Don't do this for ggt files.)
			setHideConstructionProtocolNavigation();

			if (macros != null) {
				// App.debug("start processing macros: "+System.currentTimeMillis());
				getXMLio().processXMLString(macros, true, true);
				// App.debug("end processing macros: "+System.currentTimeMillis());
			}

			// App.debug("start processing" + System.currentTimeMillis());
			getXMLio().processXMLString(construction, true, false);



			// App.debug("end processing" + System.currentTimeMillis());
			// defaults (optional)
			if (defaults2d != null) {
				getXMLio().processXMLString(defaults2d, false, true);
			}
			if (defaults3d != null) {
				getXMLio().processXMLString(defaults3d, false, true);
			}
			setCurrentFile(archiveContent);
			afterLoadFileAppOrNot();
		} else {
			// on images do nothing here: wait for callback when images loaded.
			getImageManager().triggerImageLoading(
			/* DataUtil.utf8Decode( */construction/*
												 * )/*DataUtil.utf8Decode
												 * (construction)
												 */, defaults2d, defaults3d,
					macros,
					getXMLio(), this);
			setCurrentFile(archiveContent);

		}

	}

	public void beforeLoadFile() {
		startCollectingRepaints();
		// make sure the image manager will not wait for images from the *old*
		// file
		if (this.getImageManager() != null) {
			this.getImageManager().reset();
		}
		getEuclidianView1().setReIniting(true);
		if (hasEuclidianView2EitherShowingOrNot(1)) {
			getEuclidianView2(1).setReIniting(true);
		}
	}

	public void setCurrentFile(HashMap<String, String> file) {
		if (currentFile == file) {
			return;
		}

		currentFile = file;
		if (currentFile != null) {
			addToFileList(currentFile);
		}

		// if (!isIniting() && isUsingFullGui()) {
		// updateTitle();
		// getGuiManager().updateMenuWindow();
		// }
	}

	public void addToFileList(Map<String, String> file) {
		if (file == null) {
			return;
		}
		// add or move fileName to front of list
		fileList.remove(file);
		fileList.addFirst(file);
	}

	public Map<String, String> getFromFileList(int i) {
		if (fileList.size() > i) {
			return fileList.get(i);
		}
		return null;
	}

	public int getFileListSize() {
		return fileList.size();
	}

	public HashMap<String, String> getCurrentFile() {
		return currentFile;
	}

	@Override
	public void reset() {
		if (currentFile != null) {
			try {
				loadGgbFile(currentFile);
			} catch (Exception e) {
				clearConstruction();
			}
		} else {
			clearConstruction();
		}
	}

	private static final ArrayList<String> IMAGE_EXTENSIONS = new ArrayList<String>();
	static {
		IMAGE_EXTENSIONS.add("bmp");
		IMAGE_EXTENSIONS.add("gif");
		IMAGE_EXTENSIONS.add("jpg");
		IMAGE_EXTENSIONS.add("jpeg");
		IMAGE_EXTENSIONS.add("png");
		IMAGE_EXTENSIONS.add("svg");
	}

	private void maybeProcessImage(String filename, String content) {
		String fn = filename.toLowerCase();
		if (fn.equals(MyXMLio.XML_FILE_THUMBNAIL)) {
			return; // Ignore thumbnail
		}

		int index = fn.lastIndexOf('.');
		if (index == -1) {
			return; // Ignore files without extension
		}

		String ext = fn.substring(index + 1).toLowerCase();
		if (!IMAGE_EXTENSIONS.contains(ext)) {
			return; // Ignore non image files
		}

		// for file names e.g. /geogebra/main/nav_play.png in GeoButtons
		// App.debug("filename2 = " + filename);
		// App.debug("ext2 = " + ext);

		if ("svg".equals(ext)) {
			// IE11/12 seems to require SVG to be base64 encoded
			addExternalImage(filename, "data:image/svg+xml;base64,"
			        + encodeBase64String(content));
		} else {
			addExternalImage(filename, content);
		}
	}

	/*
	 * String -> String only
	 */
	public native String encodeBase64String(String s) /*-{
		return $wnd.btoa(s);
	}-*/;

	/*
	 * String -> String only
	 */
	public native String decodeBase64String(String s) /*-{
		return $wnd.atob(s);
	}-*/;

	public void addExternalImage(String filename, String src) {
		getImageManager().addExternalImage(filename, src);
	}

	@Override
	public ImageManagerW getImageManager() {
		return imageManager;
	}

	protected void initImageManager() {
		imageManager = new ImageManagerW();
	}

	@Override
	public final void setXML(String xml, boolean clearAll) {
		if (clearAll) {
			setCurrentFile(null);
		}

		try {
			// make sure objects are displayed in the correct View
			setActiveView(App.VIEW_EUCLIDIAN);
			getXMLio().processXMLString(xml, clearAll, false);
		} catch (MyError err) {
			err.printStackTrace();
			showError(err);
		} catch (Exception e) {
			e.printStackTrace();
			showError("LoadFileFailed");
		}
	}

	@Override
	public boolean clearConstruction() {
		// if (isSaved() || saveCurrentFile()) {
		kernel.clearConstruction(true);

		kernel.initUndoInfo();
		resetMaxLayerUsed();
		setCurrentFile(null);
		setMoveMode();

		DrawEquationWeb dew = (DrawEquationWeb) getDrawEquation();
		dew.deleteLaTeXes(getEuclidianView1());
		if (hasEuclidianView2EitherShowingOrNot(1)) {
			dew.deleteLaTeXes(getEuclidianView2(1));
		}
		return true;

		// }
		// return false;
	}

	@Override
	public final MyImage getExternalImageAdapter(String fileName, int width,
	        int height) {
		ImageElement im = getImageManager().getExternalImage(fileName);
		if (im == null)
			return null;
		if (width != 0 && height != 0) {
			im.setWidth(width);
			im.setHeight(height);
		}
		return new MyImageW(im, fileName.toLowerCase().endsWith(".svg"));
	}

	@Override
	public final AnimationManager newAnimationManager(Kernel kernel2) {
		return new AnimationManagerW(kernel2);
	}

	@Override
	public final UndoManager getUndoManager(Construction cons) {
		return new UndoManagerW(cons);
	}

	@Override
	public final GeoElementGraphicsAdapter newGeoElementGraphicsAdapter() {
		return new org.geogebra.web.html5.kernel.GeoElementGraphicsAdapterW(this);
	}

	@Override
	public final void runScripts(GeoElement geo1, String string) {
		geo1.runClickScripts(string);
	}

	@Override
	public final CASFactory getCASFactory() {
		return CASFactory.getPrototype();
	}

	@Override
	public void fileNew() {

		// clear all
		// triggers the "do you want to save" dialog
		// so must be called first
		if (!clearConstruction()) {
			return;
		}

		clearInputBar();


		resetUniqueId();

		setLocalID(-1);
		resetActiveMaterial();

		if (getGoogleDriveOperation() != null) {
			getGoogleDriveOperation().resetStorageInfo();
		}

	}

	/**
	 * @param allMacroXML
	 * @param macro
	 *            Macro need to be stored.
	 * @param writeBack
	 *            Is it a new one or a modification.
	 */
	public void storeMacro(Macro macro, boolean writeBack) {
		createStorage();
		if (storage == null) {
			return;
		}

		String b64 = getGgbApi().getMacrosBase64();

		storage.setItem(STORAGE_MACRO_ARCHIVE, b64);

		storage.setItem(STORAGE_MACRO_KEY, macro.getToolName());

		if (writeBack) {
			return;
		}

		// Storage.addStorageEventHandler(new StorageEvent.Handler() {
		//
		// public void onStorageChange(StorageEvent event) {
		// if (STORAGE_MACRO_KEY.equals(event.getKey())) {
		// App.debug("[STORAGE] '" + STORAGE_MACRO_KEY
		// + "' has changed.");
		// }
		// }
		// });

	}

	protected void createStorage() {
		if (storage == null) {
			storage = Storage.getSessionStorageIfSupported();
		}

	}

	protected boolean hasMacroToRestore() {
		createStorage();
		if (storage != null) {
			StorageMap map = new StorageMap(storage);
			if (map.containsKey(STORAGE_MACRO_ARCHIVE)) {
				return true;
			}
		}

		return false;

	}

	protected void restoreMacro() {

		createStorage();
		if (storage != null) {
			StorageMap map = new StorageMap(storage);
			if (map.containsKey(STORAGE_MACRO_ARCHIVE)) {
				getKernel().removeAllMacros();
				String b64 = storage.getItem(STORAGE_MACRO_ARCHIVE);
				getGgbApi().setBase64(b64);
			}
		}

	}

	protected boolean openMacroFromStorage() {
		createStorage();

		if (storage != null) {
			StorageMap map = new StorageMap(storage);
			if (map.containsKey(STORAGE_MACRO_KEY)) {
				String macroName = storage.getItem(STORAGE_MACRO_KEY);
				try {
					// App.debug("[STORAGE] restoring macro " + macroName);
					openMacro(macroName);
					Window.setTitle(macroName);
					setToolLoadedFromStorage(true);
					return true;

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * sets the timestamp of last synchronization with ggbTube
	 * 
	 * @param syncStamp
	 *            long
	 */
	public void setSyncStamp(long syncStamp) {
		this.syncStamp = syncStamp;
	}

	/**
	 * @return timestamp of last synchronization with ggbTube
	 */
	public long getSyncStamp() {
		return this.syncStamp;
	}

	/**
	 * @return GoogleDriveOperation
	 */
	public GoogleDriveOperation getGoogleDriveOperation() {
		return googleDriveOperation;
	}

	/**
	 * Opens the ggb or ggt file
	 * 
	 * @param fileToHandle
	 * @param callback
	 * @return returns true, if fileToHandle is ggb or ggt file, otherwise
	 *         returns false. Note that If the function returns true, it's don't
	 *         mean, that the file opening was successful, and the opening
	 *         finished already.
	 */
	public native boolean openFile(JavaScriptObject fileToHandle,
	        JavaScriptObject callback) /*-{
		var ggbRegEx = /\.(ggb|ggt|csv|off)$/i;
		if (!fileToHandle.name.toLowerCase().match(ggbRegEx))
			return false;

		var appl = this;
		var reader = new FileReader();
		reader.onloadend = function(ev) {
			if (reader.readyState === reader.DONE) {
				var fileStr = reader.result;
				if (fileToHandle.name.toLowerCase().match(/\.(ggb|ggt)$/i)) {

					appl.@org.geogebra.web.html5.main.AppW::loadGgbFileAsBase64Again(Ljava/lang/String;)(fileStr);
				}
				if (fileToHandle.name.toLowerCase().match(/\.(csv)$/i)) {
					appl.@org.geogebra.web.html5.main.AppW::openCSV(Ljava/lang/String;)(atob(fileStr.substring(fileStr.indexOf(",")+1)));
				}
				if (fileToHandle.name.toLowerCase().match(/\.(off)$/i)) {
					appl.@org.geogebra.web.html5.main.AppW::openOFF(Ljava/lang/String;)(atob(fileStr.substring(fileStr.indexOf(",")+1)));
				}
				if (callback != null)
					callback();
			}
		};
		reader.readAsDataURL(fileToHandle);
		return true;
	}-*/;

	/**
	 * NEVER CALLED??
	 */
	public void addFileLoadListener(FileLoadListener f) {
		this.fileLoadListeners.add(f);
	}

	private ArrayList<FileLoadListener> fileLoadListeners = new ArrayList<FileLoadListener>();

	public final void notifyFileLoaded() {
		for (FileLoadListener listener : fileLoadListeners) {
			listener.onFileLoad();
		}

	}

	@Override
	public double getMillisecondTime() {
		return getMillisecondTimeNative();
	}

	private native double getMillisecondTimeNative() /*-{
		if ($wnd.performance) {
			return $wnd.performance.now();
		}

		// for IE9
		return new Date().getTime();
	}-*/;

	@Override
	public void copyBase64ToClipboard() {
		String str = getGgbApi().getBase64();
		if (isChromeWebApp()) {
			copyBase64ToClipboardChromeWebAppCase(str);
		} else {
			// this usually opens a Window.prompt
			copyBase64NonWebApp(str);
		}
	}

	@Override
	public void copyFullHTML5ExportToClipboard() {
		String str = getFullHTML5ExportString();
		if (isChromeWebApp()) {
			copyBase64ToClipboardChromeWebAppCase(str);
		} else {
			// this usually opens a Window.prompt
			copyBase64NonWebApp(str);
		}
	}

	public native void copyBase64NonWebApp(String str) /*-{
		var userAgent = $wnd.navigator.userAgent.toLowerCase();
		if ((userAgent.indexOf('msie') > -1)
				|| (userAgent.indexOf('trident') > -1)) {
			// It is a good question what shall we do in Internet Explorer?
			// Security settings may block clipboard, new browser tabs, window.prompt, alert
			// Use a custom alert! but this does not seem to work either

			//this.@org.geogebra.web.html5.main.GlobalKeyDispatcherW::showConfirmDialog(Ljava/lang/String;)(str);
			// alternative, better than nothing, but not always working
			//if ($wnd.clipboardData) {
			//	$wnd.clipboardData.setData('Text', str);
			//}

			// then just do the same as in other cases, for now
			if ($wnd.prompt) {
				$wnd.prompt('Base64', str);
			} else {
				this.@org.geogebra.web.html5.main.AppW::showConfirmDialog(Ljava/lang/String;Ljava/lang/String;)("Base64", str);
			}
		} else {
			// otherwise, we should do the following:
			if ($wnd.prompt) {
				$wnd.prompt('Base64', str);
			} else {
				this.@org.geogebra.web.html5.main.AppW::showConfirmDialog(Ljava/lang/String;Ljava/lang/String;)("Base64", str);
			}
		}
	}-*/;

	public void copyBase64ToClipboardChromeWebAppCase(String str) {
		// This should do nothing in webSimple!
	}

	public void showConfirmDialog(String title, String mess) {
		// This should do nothing in webSimple!
	}

	public static native boolean isChromeWebApp() /*-{
		if ($doc.isChromeWebapp()) {
			return true;
		}
		return false;
	}-*/;

	public void openMaterial(String s, Runnable onError) {
		// TODO Auto-generated method stub

	}

	private NetworkOperation networkOperation;

	/*
	 * True if showing the "alpha" in Input Boxes is allowed. (we can hide the
	 * symbol buttons with data-param-allowSymbolTable parameter)
	 */
	private boolean allowSymbolTables = true;

	/**
	 * @return OfflineOperation event flow
	 */
	public NetworkOperation getNetworkOperation() {
		return networkOperation;
	}

	protected void initNetworkEventFlow() {

		Network network = new Network() {

			private native boolean checkOnlineState() /*-{
		return $wnd.navigator.onLine;
	}-*/;

			@Override
			public boolean onLine() {
				return checkOnlineState();
			}
		};

		NativeEventAttacher attacher = new NativeEventAttacher() {

			private native void nativeAttach(String t, BaseEventPool ep) /*-{
		$wnd.addEventListener(t, function() {
			ep.@org.geogebra.common.move.events.BaseEventPool::trigger()();
		});
		$doc.addEventListener(t, function() {
			ep.@org.geogebra.common.move.events.BaseEventPool::trigger()();
		});
	}-*/;

			@Override
			public void attach(String type, BaseEventPool eventPool) {
				nativeAttach(type, eventPool);
			}
		};

		networkOperation = new NetworkOperation(network);
		BaseEventPool offlineEventPool = new BaseEventPool(networkOperation,
		        false);
		attacher.attach("offline", offlineEventPool);
		BaseEventPool onlineEventPool = new BaseEventPool(networkOperation,
		        true);
		attacher.attach("online", onlineEventPool);
		OfflineView ov = new OfflineView();
		networkOperation.setView(ov);
	}

	public void setAllowSymbolTables(boolean allowST) {
		allowSymbolTables = allowST;
	}

	/*
	 * Return true, if alpha buttons may be visible in input boxes.
	 */
	public boolean isAllowedSymbolTables() {
		return allowSymbolTables;
	}

	private boolean allowStyleBar = true;

	public void setAllowStyleBar(boolean flag) {
		allowStyleBar = flag;
	}

	public boolean isStyleBarAllowed() {
		return allowStyleBar;
	}

	@Override
	public AlgoKimberlingWeightsInterface getAlgoKimberlingWeights() {
		if (kimberlingw != null) {
			return kimberlingw;
		}

		GWT.runAsync(new RunAsyncCallback() {
			@Override
			public void onSuccess() {
				kimberlingw = new AlgoKimberlingWeights();
				setKimberlingWeightFunction(kimberlingw);
				getKernel().updateConstruction();
			}

			@Override
			public void onFailure(Throwable reason) {
				App.debug("AlgoKimberlingWeights loading failure");
			}
		});
		return kimberlingw;
	}

	public native void setKimberlingWeightFunction(
	        AlgoKimberlingWeightsInterface kimberlingw) /*-{
		$wnd.geogebraKimberlingWeight = function(obj) {
			return kimberlingw.@org.geogebra.common.main.AlgoKimberlingWeightsInterface::weight(Lorg/geogebra/common/main/AlgoKimberlingWeightsParams;)(obj);
		}
	}-*/;

	@Override
	public native double kimberlingWeight(AlgoKimberlingWeightsParams kparams) /*-{

		if ($wnd.geogebraKimberlingWeight) {
			return $wnd.geogebraKimberlingWeight(kparams);
		}

		// should not execute!
		return 0;

	}-*/;

	@Override
	public AlgoCubicSwitchInterface getAlgoCubicSwitch() {
		if (cubicw != null) {
			return cubicw;
		}

		GWT.runAsync(new RunAsyncCallback() {
			@Override
			public void onSuccess() {
				cubicw = new AlgoCubicSwitch();
				setCubicSwitchFunction(cubicw);
				getKernel().updateConstruction();
			}

			@Override
			public void onFailure(Throwable reason) {
				App.debug("AlgoKimberlingWeights loading failure");
			}
		});
		return cubicw;
	}

	public native void setCubicSwitchFunction(AlgoCubicSwitchInterface cubicw) /*-{
		$wnd.geogebraCubicSwitch = function(obj) {
			return cubicw.@org.geogebra.common.main.AlgoCubicSwitchInterface::getEquation(Lorg/geogebra/common/main/AlgoCubicSwitchParams;)(obj);
		}
	}-*/;

	@Override
	public native String cubicSwitch(AlgoCubicSwitchParams kparams) /*-{

		if ($wnd.geogebraCubicSwitch) {
			return $wnd.geogebraCubicSwitch(kparams);
		}

		// should not execute!
		return 0;

	}-*/;

	@Override
	public CommandDispatcher getCommandDispatcher(Kernel k) {
		return new CommandDispatcherW(k);
	}

	/**
	 * @param viewId
	 * @return the plotpanel euclidianview
	 */
	public EuclidianViewW getPlotPanelEuclidianView(int viewId) {
		if (getGuiManager() == null) {
			return null;
		}
		return (EuclidianViewW) getGuiManager().getPlotPanelView(viewId);
	}

	public boolean isPlotPanelEuclidianView(int viewID) {
		if (getGuiManager() == null) {
			return false;
		}
		return getGuiManager().getPlotPanelView(viewID) != null;
	}

	public void imageDropHappened(String imgFileName, String fileStr,
			String fileStr2, GeoPoint loc1) {
		imageDropHappened(imgFileName, fileStr, fileStr2, loc1, 0, 0);
	}

	/**
	 * Loads an image and puts it on the canvas (this happens on webcam input)
	 * On drag&drop or insert from URL this would be called too, but that would
	 * set security exceptions
	 * 
	 * @param url
	 *            - the data url of the image
	 * @param clientx
	 *            - desired position on the canvas (x) - unused
	 * @param clienty
	 *            - desired position on the canvas (y) - unused
	 */
	public void urlDropHappened(String url, int clientx, int clienty) {

		// Filename is temporarily set until a better solution is found
		// TODO: image file name should be reset after the file data is
		// available

		MD5EncrypterGWTImpl md5e = new MD5EncrypterGWTImpl();
		String zip_directory = md5e.encrypt(url);

		// with dummy extension, maybe gif or jpg in real
		String imgFileName = zip_directory + ".png";

		String fn = imgFileName;
		int index = imgFileName.lastIndexOf('/');
		if (index != -1) {
			fn = fn.substring(index + 1, fn.length()); // filename without
		}
		// path
		fn = org.geogebra.common.util.Util.processFilename(fn);

		// filename will be of form
		// "a04c62e6a065b47476607ac815d022cc\liar.gif"
		imgFileName = zip_directory + '/' + fn;

		doDropHappened(imgFileName, url, null, 0, 0);
		this.insertImageCallback.run();
	}

	/**
	 * Loads an image and puts it on the canvas (this happens by drag & drop)
	 * 
	 * @param imgFileName
	 *            - the file name of the image
	 * @param fileStr
	 *            - the image data url
	 * @param fileStr2
	 *            - the image binary string
	 * @param loc
	 */
	public void imageDropHappened(String imgFileName, String fileStr,
			String fileStr2, GeoPoint loc1, int width, int height) {

		MD5EncrypterGWTImpl md5e = new MD5EncrypterGWTImpl();
		String zip_directory = md5e.encrypt(fileStr2);

		String fn = imgFileName;
		int index = imgFileName.lastIndexOf('/');
		if (index != -1) {
			fn = fn.substring(index + 1, fn.length()); // filename without
		}
		// path
		fn = org.geogebra.common.util.Util.processFilename(fn);

		// filename will be of form
		// "a04c62e6a065b47476607ac815d022cc\liar.gif"
		imgFileName = zip_directory + '/' + fn;

		doDropHappened(imgFileName, fileStr, loc1, width, height);
	}

	private void doDropHappened(String imgFileName, String fileStr,
	        GeoPoint loc, int width, int height) {

		Construction cons = getKernel().getConstruction();
		EuclidianViewInterfaceCommon ev = getActiveEuclidianView();
		getImageManager().addExternalImage(imgFileName, fileStr);
		GeoImage geoImage = new GeoImage(cons);
		getImageManager().triggerSingleImageLoading(imgFileName, geoImage);
		geoImage.setImageFileName(imgFileName, width, height);

		if (loc == null) {
			double cx = ev.getXmin() + (ev.getXmax() - ev.getXmin()) / 4;
			double cy = ev.getYmin() + (ev.getYmax() - ev.getYmin()) / 4;
			GeoPoint gsp = new GeoPoint(cons, cx, cy, 1);
			gsp.setLabel(null);
			gsp.setLabelVisible(false);
			gsp.update();
			geoImage.setCorner(gsp, 0);

			cx = ev.getXmax() - (ev.getXmax() - ev.getXmin()) / 4;
			GeoPoint gsp2 = new GeoPoint(cons, cx, cy, 1);
			gsp2.setLabel(null);
			gsp2.setLabelVisible(false);
			gsp2.update();
			geoImage.setCorner(gsp2, 1);
		} else {
			geoImage.setCorner(loc, 0);
			GeoPoint point = new GeoPoint(cons);
			geoImage.calculateCornerPoint(point, 2);
			geoImage.setCorner(point, 1);
			point.setLabel(null);
		}

		geoImage.setLabel(null);
		GeoImage.updateInstances(this);

		// these things are done in Desktop GuiManager.loadImage too
		GeoElement[] geos = { geoImage };
		getActiveEuclidianView().getEuclidianController().clearSelections();
		getActiveEuclidianView().getEuclidianController()
		        .memorizeJustCreatedGeos(geos);
		setDefaultCursor();
	}

	/**
	 * Opens the image file
	 * 
	 * @param fileToHandle
	 * @param callback
	 * @return returns true, if fileToHandle image file, otherwise return false.
	 *         Note that If the function returns true, it's don't mean, that the
	 *         file opening was successful, and the opening finished already.
	 */
	public native boolean openFileAsImage(JavaScriptObject fileToHandle,
	        JavaScriptObject callback) /*-{
		var imageRegEx = /\.(png|jpg|jpeg|gif|bmp)$/i;
		if (!fileToHandle.name.toLowerCase().match(imageRegEx))
			return false;

		var appl = this;
		var reader = new FileReader();
		reader.onloadend = function(ev) {
			if (reader.readyState === reader.DONE) {
				var fileStr = reader.result;
				var fileName = fileToHandle.name;
				appl.@org.geogebra.web.html5.main.AppW::imageDropHappened(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lorg/geogebra/common/kernel/geos/GeoPoint;)(fileName, fileStr, fileStr, null);
				if (callback != null) {
					callback();
				}
			}
		};
		reader.readAsDataURL(fileToHandle);
		return true;
	}-*/;

	/**
	 * @return the id of the articleelement
	 */
	public String getArticleId() {
		return articleElement.getId();
	}

	/**
	 * @param articleid
	 *            the article id added by scriptManager
	 * 
	 *            this method is called by scriptmanager after ggbOnInit
	 */
	public static native void appletOnLoad(String articleid) /*-{
		if (typeof $wnd.ggbAppletOnLoad === "function") {
			$wnd.ggbAppletOnLoad(articleid);
		}
	}-*/;

	/**
	 * Pops up a welcome message for the exam mode.
	 */
	public void examWelcome() {

		if (isExam()) {
			String[] optionNames = { getMenu("StartExam") };
			GOptionPaneW.INSTANCE.showOptionDialog(this,
			        getMenu("WelcomeExam"), getMenu("GeoGebraExam"),
			        GOptionPane.CUSTOM_OPTION, GOptionPane.INFORMATION_MESSAGE,
			        null, optionNames, new AsyncOperation() {
				        @Override
				        public void callback(Object obj) {
					        DivElement divID = (DivElement) Document.get()
					                .getElementById("timer");
					        divID.setPropertyBoolean("started", true);
					        Date date = new Date();
					        final long start = date.getTime();
					        // We need to set seconds, otherwise it does not fit
					        // into int.
					        divID.setPropertyInt("start", (int) (start / 1000));
				        }
			        });
		}
	}

	@Override
	public void setActiveView(int evID) {
		if (getGuiManager() != null) {
			getGuiManager().setActiveView(evID);
		}
	}

	public final ClientInfo getClientInfo() {
		ClientInfo clientInfo = new ClientInfo();
		clientInfo.setModel(getLoginOperation().getModel());
		clientInfo.setLanguage(getLocalization().getLanguage());
		clientInfo.setWidth((int) getWidth());
		clientInfo.setHeight((int) getHeight());
		clientInfo.setType(getClientType());
		clientInfo.setId(getClientID());
		return clientInfo;
	}

	/**
	 * Initializes the user authentication
	 */
	public void initSignInEventFlow(LogInOperation op, boolean mayLogIn) {

		// Initialize the signIn operation
		loginOperation = op;
		if (getNetworkOperation().isOnline()) {
			if (this.getLAF() != null && this.getLAF().externalDriveSupported()) {
				initGoogleDriveEventFlow();
			}
			if (mayLogIn) {
			loginOperation.performTokenLogin();
			}
		} else {
			loginOperation.startOffline();
		}
	}

	protected void initGoogleDriveEventFlow() {
		// overriden in AppW
	}

	private ArrayList<ViewsChangedListener> viewsChangedListener = new ArrayList<ViewsChangedListener>();
	private GDimension preferredSize;

	public void addViewsChangedListener(ViewsChangedListener l) {
		viewsChangedListener.add(l);
	}

	public void fireViewsChangedEvent() {
		for (ViewsChangedListener l : viewsChangedListener) {
			l.onViewsChanged();
		}
	}

	@Override
	public FontManager getFontManager() {
		return fontManager;
	}

	/**
	 * Initializes the application, seeds factory prototypes, creates Kernel and
	 * MyXMLIO
	 * 
	 */
	protected void initCommonObjects() {
		initFactories();
		org.geogebra.common.factories.UtilFactory.prototype = new org.geogebra.web.html5.factories.UtilFactoryW();
		org.geogebra.common.factories.Factory
		        .setPrototype(new org.geogebra.web.html5.factories.FactoryW());
		// App.initializeSingularWS();

		// neded to not overwrite anything already exists
		ORIGINAL_BODY_CLASSNAME = RootPanel.getBodyElement().getClassName();

		// Online - Offline event handling begins here
		initNetworkEventFlow();
	}

	/**
	 * 
	 * @param this_app
	 *            application
	 * @return a kernel
	 */
	protected Kernel newKernel(App this_app) {
		return new Kernel(this_app);
	}

	/**
	 * Initializes Kernel, EuclidianView, EuclidianSettings, etc..
	 * 
	 * @param undoActive
	 * @param this_app
	 */
	protected void initCoreObjects(final boolean undoActive, final App this_app) {
		kernel = newKernel(this_app);

		// init settings
		settings = companion.newSettings();
		SpreadsheetSettings.MAX_SPREADSHEET_ROWS_VISIBLE = 200;
		SpreadsheetSettings.MAX_SPREADSHEET_COLUMNS_VISIBLE = 200;
		myXMLio = new MyXMLioW(kernel, kernel.getConstruction());

		fontManager = new FontManagerW();
		setFontSize(16);
		initEuclidianViews();

		initImageManager();

		setFontSize(16);
		// setLabelDragsEnabled(false);

		// make sure undo allowed
		hasFullPermissions = true;

		getScriptManager();// .ggbOnInit();//this is not called here because we
		// have to delay it
		// until the canvas is first drawn

		setUndoActive(undoActive);
		registerFileDropHandlers(getFrameElement());
	}

	/**
	 * Register file drop handlers for the canvas of this application
	 */
	native void registerFileDropHandlers(Element ce) /*-{

		var appl = this;
		var frameElement = ce;

		if (frameElement) {
			frameElement.addEventListener("dragover", function(e) {
				e.preventDefault();
				e.stopPropagation();
				frameElement.style.borderColor = "#ff0000";
			}, false);
			frameElement.addEventListener("dragenter", function(e) {
				e.preventDefault();
				e.stopPropagation();
			}, false);
			frameElement
					.addEventListener(
							"drop",
							function(e) {
								e.preventDefault();
								e.stopPropagation();
								frameElement.style.borderColor = "#000000";
								var dt = e.dataTransfer;
								if (dt.files.length) {
									var fileToHandle = dt.files[0];

									//at first this tries to open the fileToHandle as image,
									//if fileToHandle not an image, this will try to open as ggb or ggt.
									if (!appl.@org.geogebra.web.html5.main.AppW::openFileAsImage(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(fileToHandle, null)) {
										appl.@org.geogebra.web.html5.main.AppW::openFile(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(fileToHandle, null);
									}

								} else {
									// This would raise security exceptions later - see ticket #2301
									//var gdat = dt.getData("URL");
									//if (gdat && gdat != " ") {
									//	var coordx = e.offsetX ? e.offsetX : e.layerX;
									//	var coordy = e.offsetY ? e.offsetY : e.layerY;
									//	appl.@org.geogebra.web.html5.main.AppW::urlDropHappened(Ljava/lang/String;II)(gdat, coordx, coordy);
									//}
								}
							}, false);
		}
		$doc.body.addEventListener("dragover", function(e) {
			e.preventDefault();
			e.stopPropagation();
			if (frameElement)
				frameElement.style.borderColor = "#000000";
		}, false);
		$doc.body.addEventListener("drop", function(e) {
			e.preventDefault();
			e.stopPropagation();
		}, false);
	}-*/;

	/**
	 * @return preferred size
	 */
	public GDimension getPreferredSize() {
		if (preferredSize == null) {
			return new GDimensionW(800, 600);
		}
		return preferredSize;
	}

	@Override
	public void setPreferredSize(org.geogebra.common.awt.GDimension size) {
		preferredSize = size;
	}

	public Element getFrameElement() {
		// App.debug("getFrameElement() returns null, should be overridden by subclasses");
		return null;
	}

	@Override
	public void setWaitCursor() {
		if (getDialogManager() instanceof LoadingApplication) {
			((LoadingApplication) getDialogManager()).showLoadingAnimation();
		}
		RootPanel.get().setStyleName(ORIGINAL_BODY_CLASSNAME);
		RootPanel.get().addStyleName("cursor_wait");
	}

	@Override
	public void setDefaultCursor() {
		if (getDialogManager() instanceof LoadingApplication) {
			((LoadingApplication) getDialogManager()).hideLoadingAnimation();
		}
		RootPanel.get().setStyleName(ORIGINAL_BODY_CLASSNAME);
	}

	public void resetCursor() {
		RootPanel.get().setStyleName(ORIGINAL_BODY_CLASSNAME);
	}

	private void updateContentPane(boolean updateComponentTreeUI) {
		if (initing) {
			return;
		}

		addMacroCommands();

		// used in AppWapplet
		buildApplicationPanel();

		fontManager.setFontSize(getGUIFontSize());

		// update sizes
		euclidianView.updateSize();

		// update layout
		if (updateComponentTreeUI) {
			updateTreeUI();
		}

		// reset mode and focus
		set1rstMode();

		if (euclidianView.isShowing()) {
			requestFocusInWindow();
		}
	}

	/**
	 * Updates the GUI of the main component.
	 */
	public void updateContentPane() {
		updateContentPane(true);
	}

	protected void requestFocusInWindow() {
		if (!articleElement.preventFocus()) {
			euclidianView.requestFocusInWindow();
		}
	}

	public GLookAndFeelI getLAF() {
		return laf;
	}

	@Override
	public SpreadsheetTableModel getSpreadsheetTableModel() {
		if (tableModel == null) {
			tableModel = new SpreadsheetTableModelW(this, SPREADSHEET_INI_ROWS,
			        SPREADSHEET_INI_COLS);
		}
		return tableModel;
	}

	protected abstract void updateTreeUI();

	public void buildApplicationPanel() {
	}

	public void appSplashCanNowHide() {
		// not sure we need this in web applets
		// (not application mode)

		// allow eg ?command=A=(1,1);B=(2,2) in URL
		String cmd = com.google.gwt.user.client.Window.Location
		        .getParameter("command");

		if (cmd != null) {

			App.debug("exectuing commands: " + cmd);

			String[] cmds = cmd.split(";");
			for (int i = 0; i < cmds.length; i++) {
				getKernel().getAlgebraProcessor()
				        .processAlgebraCommandNoExceptionsOrErrors(cmds[i],
				                false);
			}
		}
	}

	/**
	 * Called from GuiManager, implementation depends on subclass
	 * 
	 * @return toolbar object
	 */
	public ToolBarInterface getToolbar() {
		return null;
	}

	// methods used just from AppWapplet (and AppWsimple)
	public void focusLost(org.geogebra.common.kernel.View w, Element el) {
		// other things are handled in subclasses of AppW
		// anyAppHasFocus = false;
		if (el != null) {
			lastActiveElement = el;
		}
	}

	public void focusGained(org.geogebra.common.kernel.View w, Element el) {
		// this is used through the super keyword
		// anyAppHasFocus = true;
		if (el != null) {
			lastActiveElement = el;
		}
	}

	public void setCustomToolBar() {
	}

	/**
	 * initializes the google drive event flow
	 */

	public boolean onlyGraphicsViewShowing() {
		if (!isUsingFullGui() || getGuiManager() == null) {
			return true;
		}

		return getGuiManager().getLayout().isOnlyVisible(App.VIEW_EUCLIDIAN);
	}

	@Override
	public boolean isUsingFullGui() {
		return useFullGui;
	}

	@Override
	public GuiManagerInterfaceW getGuiManager() {
		return null;
	}

	// ========================================================
	// Getters/Setters
	// ========================================================

	@Override
	public boolean isHTML5Applet() {
		return true;
	}

	@Override
	public String getVersionString() {
		return super.getVersionString() + "-HTML5";
	}

	public ArticleElement getArticleElement() {
		return articleElement;
	}

	@Override
	public boolean isApplet() {
		return !articleElement.getDataParamApp();
	}

	public Material getActiveMaterial() {
		return this.activeMaterial;
	}

	public void setActiveMaterial(Material mat) {
		this.activeMaterial = mat;
	}

	private void resetActiveMaterial() {
		this.activeMaterial = null;
	}

	@Override
	protected EuclidianView newEuclidianView(boolean[] showAxes,
	        boolean showGrid) {

		return euclidianView = newEuclidianView(euclidianViewPanel,
		        euclidianController, showAxes, showGrid, 1, getSettings()
		                .getEuclidian(1));
	}

	/**
	 * 
	 * @param evPanel
	 * @param ec
	 * @param showAxes
	 * @param showGrid
	 * @param id
	 * @param settings
	 * @return new euclidian view
	 */
	public EuclidianViewW newEuclidianView(EuclidianPanelWAbstract evPanel,
	        EuclidianController ec, boolean[] showAxes, boolean showGrid,
	        int id, EuclidianSettings settings) {
		return new EuclidianViewW(evPanel, ec, showAxes, showGrid, id, settings);
	}

	@Override
	public EuclidianController newEuclidianController(Kernel kernel) {
		return new EuclidianControllerW(kernel);

	}

	@Override
	public DialogManager getDialogManager() {
		return dialogManager;
	}

	@Override
	public Factory getFactory() {
		return Factory.getPrototype();
	}

	public void restoreCurrentUndoInfo() {
		if (isUndoActive()) {
			kernel.restoreCurrentUndoInfo();
			// isSaved = false;
		}
	}

	// ===================================================
	// Views
	// ===================================================

	public EuclidianPanelWAbstract getEuclidianViewpanel() {
		return euclidianViewPanel;
	}

	@Override
	public boolean hasEuclidianView2EitherShowingOrNot(int idx) {
		return (getGuiManager() != null)
		        && getGuiManager().hasEuclidianView2EitherShowingOrNot(idx);
	}

	@Override
	public boolean hasEuclidianView2(int idx) {
		return (getGuiManager() != null)
		        && getGuiManager().hasEuclidianView2(idx);
	}

	@Override
	public EuclidianViewW getEuclidianView2(int idx) {

		if (getGuiManager() == null)
			return null;

		return (EuclidianViewW) getGuiManager().getEuclidianView2(idx);
	}

	@Override
	public EuclidianViewInterfaceCommon getActiveEuclidianView() {
		if (getGuiManager() == null) {
			return getEuclidianView1();
		}
		return getGuiManager().getActiveEuclidianView();
	}

	@Override
	public boolean isShowingEuclidianView2(int idx) {
		return (getGuiManager() != null)
		        && getGuiManager().hasEuclidianView2(idx)
		        && getGuiManager().getEuclidianView2(idx).isShowing();
	}

	@Override
	public EuclidianViewW createEuclidianView() {
		return (EuclidianViewW) this.euclidianView;
	}

	@Override
	public AlgebraView getAlgebraView() {
		if (getGuiManager() == null) {
			return null;
		}
		return getGuiManager().getAlgebraView();
	}

	@Override
	public boolean showView(int view) {
		if (getGuiManager() == null) {
			return (view == App.VIEW_EUCLIDIAN);
		}
		return getGuiManager().showView(view);
	}

	protected void attachViews() {

		if (getGuiManager() == null)
			return;

		if (getGuiManager().hasAlgebraView()
				&& !getGuiManager().getAlgebraView().isAttached())
			getGuiManager().attachView(VIEW_ALGEBRA);

		if (needsSpreadsheetTableModel())
			getSpreadsheetTableModel();// its constructor calls attachView as a
		// side-effect
		// Attached only on first click
		// getGuiManager().attachView(VIEW_PROPERTIES);
	}

	// ========================================================
	// Languages
	// ========================================================

	private static ArrayList<String> supportedLanguages = null;

	/**
	 * @return ArrayList of languages suitable for GWT, eg "en", "de_AT"
	 */
	/*
	 * public static ArrayList<String> getSupportedLanguages() {
	 * 
	 * if (supportedLanguages != null) { return supportedLanguages; }
	 * 
	 * supportedLanguages = new ArrayList<String>();
	 * 
	 * Language[] languages = Language.values();
	 * 
	 * for (int i = 0; i < languages.length; i++) {
	 * 
	 * Language language = languages[i];
	 * 
	 * if (language.fullyTranslated || this.isPrerelease()) {
	 * supportedLanguages.add(language.localeGWT); } }
	 * 
	 * return supportedLanguages;
	 * 
	 * }
	 */

	/**
	 * Checks for GeoGebraLangUI in URL, then in cookie, then checks browser
	 * language
	 */
	public String getLanguageFromCookie() {
		String lCookieValue = Location.getParameter("GeoGebraLangUI");
		if (lCookieValue == null || lCookieValue.length() == 0) {
			lCookieValue = Cookies.getCookie("GeoGebraLangUI");
		}
		if (lCookieValue == null) {
			lCookieValue = Browser.navigatorLanguage();
		}
		return lCookieValue;
	}

	@Override
	public void setLabels() {
		if (initing) {
			return;
		}
		if (getGuiManager() != null) {
			getGuiManager().setLabels();
		}
		// if (rbplain != null) {
		kernel.updateLocalAxesNames();
		kernel.setViewsLabels();
		// }
		updateCommandDictionary();
	}

	@Override
	public boolean letRedefine() {
		// AbstractApplication.debug("implementation needed"); // TODO
		// Auto-generated
		return true;
	}

	MyDictionary commandDictionary = null;

	private MyDictionary getCommandDict() {
		if (commandDictionary == null) {
			try {
				// commandDictionary =
				// Dictionary.getDictionary("__GGB__dictionary_"+language);
				commandDictionary = MyDictionary.getDictionary("command",
				        getLocalization().getLanguage());
			} catch (MissingResourceException e) {
				// commandDictionary =
				// Dictionary.getDictionary("__GGB__dictionary_en");
				commandDictionary = MyDictionary.getDictionary("command", "en");
				Log.error("Missing Dictionary "
				        + getLocalization().getLanguage());
			}
		}

		return commandDictionary;

	}

	@Override
	public String getCountryFromGeoIP() {
		// currently only needed in Desktop
		return null;
	}

	// ============================================
	// IMAGES
	// ============================================

	private String createImageSrc(String ext, String base64) {
		String dataUrl = "data:image/" + ext + ";base64," + base64;
		return dataUrl;
	}

	public ImageElement getRefreshViewImage() {
		// don't need to load gui jar as reset image is in main jar
		ImageElement imgE = getImageManager().getInternalImage(
		        GuiResourcesSimple.INSTANCE.viewRefresh());
		attachNativeLoadHandler(imgE);
		return imgE;
	}

	public ImageElement getPlayImage() {
		// don't need to load gui jar as reset image is in main jar
		return getImageManager().getInternalImage(
		        GuiResourcesSimple.INSTANCE.navPlay());
	}

	public ImageElement getPauseImage() {
		// don't need to load gui jar as reset image is in main jar
		return getImageManager().getInternalImage(
		        GuiResourcesSimple.INSTANCE.navPause());
	}

	// ============================================
	// XML
	// ============================================

	@Override
	protected int getWindowWidth() {
		if (getWidth() > 0) {
			return (int) getWidth();
		} else {
			return 800;
		}
	}

	@Override
	protected int getWindowHeight() {
		if (getHeight() > 0) {
			return (int) getHeight();
		} else {
			return 600;
		}
	}

	@Override
	protected void getLayoutXML(StringBuilder sb, boolean asPreference) {

		if (getGuiManager() == null) {
			initGuiManager();
		}
		if (getGuiManager() != null) {
			getGuiManager().getLayout().getXml(sb, asPreference);
		}
	}

	// ============================================
	// FONTS
	// ============================================

	@Override
	public GFont getPlainFontCommon() {
		return new org.geogebra.web.html5.awt.GFontW("normal");
	}

	// ============================================
	// CURSORS
	// ============================================

	@Override
	public void updateUI() {
		// App.debug("updateUI: implementation needed for GUI"); // TODO
	}

	// ========================================
	// EXPORT & GEOTUBE
	// ========================================


	public final void copyEVtoClipboard(EuclidianViewW ev) {
		String image = ev.getExportImageDataUrl(3, true);
		String title = ev.getApplication().getKernel().getConstruction()
				.getTitle();
		title = "".equals(title) ? "GeoGebraImage" : title;
		getFileManager().exportImage(image, title);
	}

	@Override
	public void copyGraphicsViewToClipboard() {
		App.debug("unimplemented");
	}

	// ========================================
	// MISC
	// ========================================

	/**
	 * Clear selection
	 * 
	 * @param repaint
	 *            whether all views need repainting afterwards
	 */
	/*
	 * @Override public void clearSelectedGeos(boolean repaint) { // if
	 * (getUseFullGui()) ? if (useFullAppGui) ((AlgebraViewW)
	 * getAlgebraView()).clearSelection(); super.clearSelectedGeos(repaint); }
	 */

	@Override
	public GeoElementSelectionListener getCurrentSelectionListener() {
		// TODO Auto-generated method stub
		return null;
	}

	public void showLoadingAnimation(boolean go) {
		// showSplashImageOnCanvas();

	}

	@Override
	public void showURLinBrowser(final String pageUrl) {
		Window.open(pageUrl, "_blank", "");
		debug("opening: " + pageUrl);

		// assume showURLinBrowserWaiterFixedDelay is called before
	}



	@Override
	public void initGuiManager() {
		// this should not be called from AppWsimple!
		// this method should be overridden in
		// AppWapplet and AppWapplication!
	}

	@Override
	public void exitAll() {
		App.debug("unimplemented");
	}

	@Override
	public void addMenuItem(MenuInterface parentMenu, String filename,
	        String name, boolean asHtml, MenuInterface subMenu) {
		addMenuItem((MenuBar) parentMenu, filename, name, asHtml, subMenu);
	}

	private void addMenuItem(MenuBar parentMenu, String filename, String name,
	        boolean asHtml, MenuInterface subMenu) {

		if (subMenu instanceof MenuBar)
			((MenuBar) subMenu).addStyleName("GeoGebraMenuBar");

		// ideally, GMenuBar's addItem will execute,
		// as this method is called from nowhere else
		parentMenu.addItem(
		        getGuiManager().getMenuBarHtml(filename, name, true), true,
		        (MenuBar) subMenu);
	}

	/**
	 * This is used for LaTeXes in GeoGebraWeb (DrawText, DrawEquationWeb)
	 */
	@Override
	public void scheduleUpdateConstruction() {

		// set up a scheduler in case 0.5 seconds would not be enough for the
		// computer
		Scheduler.get().scheduleDeferred(sucCallback);
	}

	Timer timeruc = new Timer() {
		@Override
		public void run() {
			boolean force = kernel.getForceUpdatingBoundingBox();
			kernel.setForceUpdatingBoundingBox(true);
			kernel.getConstruction().updateConstructionLaTeX();
			kernel.notifyRepaint();
			kernel.setForceUpdatingBoundingBox(force);
		}
	};

	Scheduler.ScheduledCommand sucCallback = new Scheduler.ScheduledCommand() {
		@Override
		public void execute() {
			// 0.5 seconds is good for the user and maybe for the computer
			// too
			timeruc.schedule(500);
		}
	};
	private Runnable closeBroserCallback;
	private Runnable insertImageCallback;

	@Override
	public void createNewWindow() {
		// TODO implement it ?
	}

	public boolean menubarRestricted() {
		return true;
	}

	public String getDataParamId() {
		return getArticleElement().getDataParamId();

	}

	protected void resetCommandDictionary() {
		this.commandDictionary = null;

	}

	public abstract void afterLoadFileAppOrNot();

	/**
	 * Returns the tool name and tool help text for the given tool as an HTML
	 * text that is useful for tooltips.
	 * 
	 * @param mode
	 *            : tool ID
	 */
	@Override
	public String getToolTooltipHTML(int mode) {

		// TODO: fix this code copied from desktop
		// if getLocalization().getTooltipLocale() != null) {
		// getLocalization().setTooltipFlag();
		// }

		StringBuilder sbTooltip = new StringBuilder();
		sbTooltip.append("<html><b>");
		sbTooltip.append(StringUtil.toHTMLString(getToolName(mode)));
		sbTooltip.append("</b><br>");
		sbTooltip.append(StringUtil.toHTMLString(getToolHelp(mode)));
		sbTooltip.append("</html>");

		getLocalization().clearTooltipFlag();

		return sbTooltip.toString();

	}

	public void recalculateEnvironments() {

		if (getGuiManager() != null) {
			getGuiManager().recalculateEnvironments();

		}
		if (getEuclidianView1() != null) {
			getEuclidianView1().getEuclidianController().calculateEnvironment();
		}

	}

	@Override
	public void updateViewSizes() {
		// TODO Auto-generated method stub
	}

	public void registerPopup(Widget widget) {
		popups.add(widget);
	}

	public void closePopups() {
		justClosedPopup = false;
		for (Widget widget : popups) {
			justClosedPopup = true;
			widget.setVisible(false);
		}
		ToolTipManagerW.hideAllToolTips();
		popups.clear();

		if (getKernel().getApplication().has(Feature.AV_EXTENSIONS)) {
			RadioButtonTreeItem.closeMinMaxPanel();
		}
	}

	public boolean wasPopupJustClosed() {
		return justClosedPopup;
	}

	public void unregisterPopup(Widget widget) {
		popups.remove(widget);
	}

	public String getClientType() {
		return getLAF().getType();
	}

	public String getClientID() {
		return getArticleElement().getDataClientID();
	}

	public boolean isShowToolbar() {
		if (this.articleElement == null) {
			return false;
		}
		return this.articleElement.getDataParamShowToolBar(false)
		        || this.articleElement.getDataParamApp();
	}

	public int getWidthForSplitPanel(int fallback) {
		int ret = getAppletWidth() - 2; // 2: border

		// if it is not 0, there will be some scaling later
		if (ret <= 0) {
			ret = fallback;

			// empirical hack to make room for the toolbar always
			if (showToolBar() && ret < 598)
				ret = 598; // 2: border
			// maybe this has to be put outside the "if"?
		}
		return ret;
	}

	public int getHeightForSplitPanel(int fallback) {
		int windowHeight = getAppletHeight() - 2; // 2: border
		// but we want to know the available height for the rootPane
		// so we either use the above as a heuristic,
		// or we should substract the height(s) of
		// toolbar, menubar, and input bar;
		// heuristics come from GeoGebraAppFrame
		if (showAlgebraInput()
		        && getInputPosition() != InputPositon.algebraView) {
			windowHeight -= GLookAndFeelI.COMMAND_LINE_HEIGHT;
		}
		if (showToolBar()) {
			windowHeight -= GLookAndFeelI.TOOLBAR_HEIGHT;
		}
		// menubar height is always 0
		if (windowHeight <= 0)
			windowHeight = fallback;
		return windowHeight;
	}

	protected void initUndoInfoSilent() {
		getScriptManager().disableListeners();
		kernel.initUndoInfo();
		getScriptManager().enableListeners();
	}

	@Override
	public boolean supportsView(int viewID) {
		if (viewID == App.VIEW_CAS && !getLAF().isSmart()) {
			if (!Browser.supportsJsCas()) {
				return false;
			}
		}

		if (viewID == App.VIEW_CAS) {
			return !getArticleElement().getDataParamNoCAS();
		}

		return viewID != App.VIEW_EUCLIDIAN3D;
	}

	@Override
	public abstract void set1rstMode();

	@Override
	public int getGUIFontSize() {
		return 14;
	}

	public void showStartScreen() {
		if (showStartScreenNative(getLocalization().getLanguage(), this
		        .getLoginOperation().getModel().loadLastUser())) {
			Element fr = this.getFrameElement();
			if (fr.getParentElement() != null) {
				fr = fr.getParentElement();
			}
			if (fr.getParentElement() != null) {
				fr = fr.getParentElement();
			}
			fr.setId("appletContainer");
			fr.getStyle().setVisibility(Visibility.HIDDEN);
		}
	}

	private native boolean showStartScreenNative(String language, String user) /*-{
		if ($wnd.showStartScreen) {
			return $wnd.showStartScreen(language, user);
		}
		return false;
	}-*/;

	public void updateToolBar() {
		if (!showToolBar || isIniting()) {
			return;
		}

		if (getGuiManager() != null) {
			getGuiManager().updateToolbar();
		}

		set1rstMode();
	}

	@Override
	public void updateApplicationLayout() {
		App.debug("updateApplicationLayout: Implementation needed...");
	}

	public void setShowInputHelpPanel(boolean b) {
		App.debug("setShowInputHelpPanel: Implementation needed...");
	}

	@Override
	public void setShowToolBar(boolean toolbar, boolean help) {
		if (toolbar) {
			JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE
			        .propertiesKeysJS());
		}
		super.setShowToolBar(toolbar, help);
	}

	// methods used just from AppWapplication
	public int getOWidth() {
		return 0;
	}

	public int getOHeight() {
		return 0;
	}

	public Object getGlassPane() {
		return null;
	}

	public void doOnResize() {
	}

	public void loadURL_GGB(String ggb) {
	}

	public String getAppletId() {
		return articleElement.getDataParamId();
	}

	public HasAppletProperties getAppletFrame() {
		// Should be implemented in subclasses
		return null;
	}

	/**
	 * @return whether the focus was lost
	 */
	private static native Element nativeLoseFocus(Element element) /*-{
		var active = $doc.activeElement;
		if (active
				&& ((active === element) || (active
						.compareDocumentPosition(element) & $wnd.Node.DOCUMENT_POSITION_CONTAINS))) {
			active.blur();
			return active;
		}
		return null;
	}-*/;

	@Override
	public void loseFocus() {
		// probably this is called on ESC, so the reverse
		// should happen on ENTER
		Element ret = nativeLoseFocus(articleElement);
		if (ret != null) {
			lastActiveElement = ret;
			anyAppHasFocus = false;
			getGlobalKeyDispatcher().InFocus = false;
		}
	}

	/**
	 * @return whether we can focus on ENTER
	 */
	private static native boolean nativeGiveFocusBack() /*-{
		var active = $doc.activeElement;
		if (active && (active !== $doc.body)) {
			if ($wnd.$ggbQuery) {
				// have jQuery, do the other checks
				var act = $wnd.$ggbQuery(active);
				if (act.is(".geogebraweb-dummy-invisible")) {
					// actually, ESC focuses this, does not blur!
					return true;
				}
				// this shall execute the default ENTER action on
				// that element, to be independent (e.g. click on links!)
				// OR if it is part of GeoGebra, then we shall also not
				// support selecting GeoGebra again by ENTER
				//return false; // behold the other return false;
			}
			// not doing more checks, don't do any action, which is safe
			return false;
		}
		// blurred, probably, so it's safe to focus on ENTER
		return true;
	}-*/;

	/**
	 * Let's say this is the pair of loseFocus, so that only loseFocus can lose
	 * focus from ALL applets officially (i.e. "ESC"), and from each part of
	 * each applet (e.g. input bar, Graphics view, etc), while only
	 * giveFocusBack can give focus back to an applet removed by the loseFocus
	 * method - to avoid hidden bugs.
	 * 
	 * What if focus is received by some other method than ENTER (pair of ESC)?
	 * I think let's allow it, but if ENTER comes next, then we should adjust
	 * our knowledge about it (otherwise, it should have been watched in the
	 * entire codebase, which is probably worse, for there are possibilities of
	 * errors). This way just these two methods shall be checked.
	 */
	public static void giveFocusBack() {
		if (anyAppHasFocus) {
			// here we are sure that ENTER should not do anything
			return;
		}

		// update for the variable in this case, must be made anyway
		// just it is a question whether this shall also mean a focus?
		// BUT only when nativeGiveFocusBack is changed, and this
		// variable also filled perfectly
		// anyAppHasFocus = true;

		// here we could insert static aggregates of relevant
		// variables like getGlobalKeyDispatcher().InFocus
		// ... but what if e.g. the input bar has focus?

		// then we can easily check for $doc.activeElement,
		// whether it means blur=OK
		if (nativeGiveFocusBack()) {
			if (lastActiveElement != null) {
				anyAppHasFocus = true;
				lastActiveElement.focus();
			}
		}
	}

	@Override
	public boolean isScreenshotGenerator() {
		return this.articleElement.getDataParamScreenshotGenerator();
	}

	// ========================================================
	// INITIALIZING
	// ========================================================

	@Override
	public void setScrollToShow(boolean b) {
		if (getGuiManager() != null) {
			getGuiManager().setScrollToShow(b);
		}
	}

	/**
	 * Overwritten for applets, full app and for touch
	 * 
	 * @return {@link FileManagerI}
	 */
	public FileManagerI getFileManager() {
		return null;
	}

	// public ToolTipManagerW getToolTipManager(){
	// if(toolTipManager == null){
	// toolTipManager = new ToolTipManagerW(this);
	// }
	// return toolTipManager;
	// }

	// ========================================================
	// Undo/Redo
	// ========================================================

	@Override
	public void setUndoActive(boolean flag) {
		// don't allow undo when running with restricted permissions
		/*
		 * if (flag && !hasFullPermissions) { flag = false; }
		 */

		if (kernel.isUndoActive() == flag) {
			return;
		}

		kernel.setUndoActive(flag);
		if (flag) {
			kernel.initUndoInfo();
		}

		if (getGuiManager() != null) {
			getGuiManager().updateActions();
		}

		// isSaved = true;
	}

	@Override
	public final void storeUndoInfo() {
		if (isUndoActive()) {
			kernel.storeUndoInfo();
			setUnsaved();
		}
	}

	// ========================================================
	// FILE HANDLING
	// ========================================================

	protected void clearInputBar() {
		if (isUsingFullGui() && showAlgebraInput() && getGuiManager() != null) {
			AlgebraInput ai = (getGuiManager().getAlgebraInput());
			if (ai != null) {
				ai.setText("");
			}
		}
	}

	// ================================================
	// ERROR HANDLING
	// ================================================

	@Override
	public void showCommandError(final String command, final String message) {
		// TODO
		App.debug("TODO later: make sure splash screen not showing");

		String title = GeoGebraConstants.APPLICATION_NAME + " - "
		        + getLocalization().getError("Error");

		String[] optionNames = { getLocalization().getPlain("OK"),
		        getLocalization().getPlain("ShowOnlineHelp") };

		GOptionPaneW.INSTANCE.showOptionDialog(this, message, title,
		        GOptionPane.CUSTOM_OPTION, GOptionPane.ERROR_MESSAGE, null,
		        optionNames, new AsyncOperation() {
			        @Override
			        public void callback(Object obj) {
				        String[] dialogResult = (String[]) obj;
				        if ("1".equals(dialogResult[0])) {
					        if (getGuiManager() != null) {
						        getGuiManager().openCommandHelp(command);
					        }
				        }
			        }
		        });

	}

	@Override
	public void showError(String key, String error) {
		showErrorDialog(getLocalization().getError(key) + ":\n" + error);
	}

	public void showMessage(final String message) {
		GOptionPaneW.INSTANCE.showConfirmDialog(null, message,
		        GeoGebraConstants.APPLICATION_NAME + " - " + getMenu("Info"),
		        GOptionPane.OK_CANCEL_OPTION, GOptionPane.INFORMATION_MESSAGE,
		        null);
	}

	@Override
	public void showErrorDialog(final String msg) {
		if (!isErrorDialogsActive()) {
			return;
		}
		if (this.getErrorHandler() != null) {
			this.getErrorHandler().showError(msg);
			return;
		}
		String title = GeoGebraConstants.APPLICATION_NAME + " - "
		        + getLocalization().getError("Error");

		GOptionPaneW.INSTANCE.showConfirmDialog(this, msg, title,
		        GOptionPane.DEFAULT_OPTION, GOptionPane.ERROR_MESSAGE, null);
	}

	private ErrorHandler getErrorHandler() {
		return this.errorHandler;
	}

	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	/**
	 * get translations for the onScreenKeyboard-buttons
	 * 
	 * @param key
	 *            String to translate
	 * @param section
	 *            "lowerCase" or "shiftDown"
	 * @param language
	 *            String
	 * @return String for keyboardButton
	 */
	public String getKey(String key, String section, String language) {
		return this.loc.getKey(key, section, language);
	}

	@Override
	public void showError(String s) {
		showErrorDialog(s);
	}

	@Override
	public boolean freeMemoryIsCritical() {
		// can't access available memory etc from JavaScript
		return false;
	}

	@Override
	public long freeMemory() {
		return 0;
	}

	@Override
	public void evalJavaScript(App app, String script, String arg) {

		// TODO: maybe use sandbox?

		String ggbApplet = getDataParamId();

		script = "document.ggbApplet= document." + ggbApplet
		        + "; ggbApplet = document." + ggbApplet + ";" + script;

		// script = "ggbApplet = document.ggbApplet;"+script;

		// add eg arg="A"; to start
		if (arg != null) {
			script = "arg=\"" + arg + "\";" + script;
		}
		evalScriptNative(script);
	}

	public static int getAbsoluteLeft(Element element) {
		return element.getAbsoluteLeft();
	}

	public static int getAbsoluteRight(Element element) {
		return element.getAbsoluteRight();
	}

	public static int getAbsoluteTop(Element element) {
		return element.getAbsoluteTop();
	}

	public static int getAbsoluteBottom(Element element) {
		return element.getAbsoluteBottom();
	}

	public static native void removeDefaultContextMenu(Element element) /*-{

		function eventOnElement(e) {

			x1 = @org.geogebra.web.html5.main.AppW::getAbsoluteLeft(Lcom/google/gwt/dom/client/Element;)(element);
			x2 = @org.geogebra.web.html5.main.AppW::getAbsoluteRight(Lcom/google/gwt/dom/client/Element;)(element);
			y1 = @org.geogebra.web.html5.main.AppW::getAbsoluteTop(Lcom/google/gwt/dom/client/Element;)(element);
			y2 = @org.geogebra.web.html5.main.AppW::getAbsoluteBottom(Lcom/google/gwt/dom/client/Element;)(element);

			if ((e.pageX < x1) || (e.pageX > x2) || (e.pageY < y1)
					|| (e.pageY > y2)) {
				return false;
			}
			return true;
		}

		if ($doc.addEventListener) {
			element.addEventListener("MSHoldVisual", function(e) {
				e.preventDefault();
			}, false);
			$doc.addEventListener('contextmenu', function(e) {
				if (eventOnElement(e))
					e.preventDefault();
			}, false);
		} else {
			$doc.attachEvent('oncontextmenu', function() {
				if (eventOnElement(e))
					$wnd.event.returnValue = false;
			});
		}
	}-*/;

	public static native void removeDefaultContextMenu() /*-{

		if ($doc.addEventListener) {
			$doc.addEventListener('contextmenu', function(e) {
				e.preventDefault();
			}, false);
			$doc.addEventListener("MSHoldVisual", function(e) {
				e.preventDefault();
			}, false);

		} else {
			$doc.attachEvent('oncontextmenu', function() {
				$wnd.event.returnValue = false;
			});
		}
	}-*/;

	public native String getNativeEmailSet() /*-{
		if ($wnd.GGW_appengine) {
			return $wnd.GGW_appengine.USER_EMAIL;
		} else
			return "";
	}-*/;

	public void attachNativeLoadHandler(ImageElement img) {
		addNativeLoadHandler(img, (EuclidianView) getActiveEuclidianView());
	}

	private native void addNativeLoadHandler(ImageElement img,
	        EuclidianView view) /*-{
		img
				.addEventListener(
						"load",
						function() {
							view.@org.geogebra.web.html5.euclidian.EuclidianViewW::updateBackground()();
						});
	}-*/;

	public static native void console(JavaScriptObject dataAsJSO) /*-{
		@org.geogebra.common.main.App::debug(Ljava/lang/String;)(dataAsJSO);
	}-*/;

	public static native void nativeConsole(JavaScriptObject object) /*-{
		$wnd.console.log(object);
	}-*/;

	// ============================================
	// LAYOUT & GUI UPDATES
	// ============================================

	@Override
	public boolean showAlgebraInput() {
		return showAlgebraInput;
	}

	@Override
	public double getWidth() {
		if (getFrameElement() == null)
			return 0;
		return getFrameElement().getOffsetWidth();
	}

	@Override
	public double getHeight() {
		if (getFrameElement() == null)
			return 0;
		return getFrameElement().getOffsetHeight();
	}

	@Override
	public void updateMenubar() {
		// getGuiManager().updateMenubar();
		App.debug("AppW.updateMenubar() - implementation needed - just finishing"); // TODO
		// Auto-generated
	}

	@Override
	public void updateStyleBars() {

		if (!isUsingFullGui() || isIniting()) {
			return;
		}

		if (getEuclidianView1().hasStyleBar()) {
			getEuclidianView1().getStyleBar().updateStyleBar();
		}

		if (hasEuclidianView2(1) && getEuclidianView2(1).hasStyleBar()) {
			getEuclidianView2(1).getStyleBar().updateStyleBar();
		}
	}

	public static Widget getRootComponent(AppW app) {

		// This is just used from tooltipManager yet
		if (app.getGuiManager() == null)
			return null;

		return app.getGuiManager().getRootComponent();
	}

	@Override
	public void updateCenterPanel(boolean updateUI) {
	}

	public Widget getSplitLayoutPanel() {
		if (getGuiManager() == null)
			return null;
		if (getGuiManager().getLayout() == null)
			return null;
		return getGuiManager().getRootComponent();
	}

	/**
	 * @param ggwGraphicsViewWidth
	 * 
	 *            Resets the width of the Canvas converning the Width of its
	 *            wrapper (splitlayoutpanel center)
	 */
	public void ggwGraphicsViewDimChanged(int width, int height) {
		// App.debug("dim changed" + getSettings().getEuclidian(1));
		getSettings().getEuclidian(1).setPreferredSize(
		        org.geogebra.common.factories.AwtFactory.prototype.newDimension(
		                width, height));

		// simple setting temp.
		appCanvasHeight = height;
		appCanvasWidth = width;
		// App.debug("syn size");
		getEuclidianView1().synCanvasSize();
		getEuclidianView1().doRepaint2();
		stopCollectingRepaints();
	}

	/**
	 * Resets the width of the Canvas converning the Width of its wrapper
	 * (splitlayoutpanel center)
	 *
	 * @param width
	 *            , height
	 */
	public void ggwGraphicsView2DimChanged(int width, int height) {
		getSettings().getEuclidian(2).setPreferredSize(
		        org.geogebra.common.factories.AwtFactory.prototype.newDimension(
		                width, height));

		// simple setting temp.
		// appCanvasHeight = height;
		// appCanvasWidth = width;

		getEuclidianView2(1).synCanvasSize();
		getEuclidianView2(1).doRepaint2();
		stopCollectingRepaints();
	}

	@Override
	public void ensureTimerRunning() {
		this.getTimerSystem().ensureRunning();

	}

	public void showLanguageGUI() {
		showBrowser(getLanguageGUI());
	}

	@Override
	public void showCustomizeToolbarGUI() {
		showBrowser(getCustomizeToolbarGUI());
	}

	protected HeaderPanel getCustomizeToolbarGUI() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Overwritten for AppWapplet/AppWapplication
	 * 
	 * @param bg
	 */
	public void showBrowser(HeaderPanel bg) {
		// TODO
	}

	/**
	 * Overwritten for AppWapplet/AppWapplication
	 */
	public HeaderPanel getLanguageGUI() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * shows the on-screen keyboard (or e.g. a show-keyboard-button)
	 */
	public void showKeyboard(MathKeyboardListener textField) {
		// Overwritten in subclass - nothing to do here
	}

	public void updateKeyboardHeight() {
		// Overwritten in subclass - nothing to do here
	}

	/**
	 * shows the on-screen keyboard (or e.g. a show-keyboard-button)
	 */
	public void showKeyboard(MathKeyboardListener textField, boolean forceShow) {
		// Overwritten in subclass - nothing to do here
	}

	/**
	 * update the on-screen keyboard
	 * 
	 * @param field
	 *            after the update the input of the keyboard is written into
	 *            this field
	 */
	public void updateKeyBoardField(
	        @SuppressWarnings("unused") MathKeyboardListener field) {
		// Overwritten in subclass - nothing to do here
	}

	/**
	 * hide the on-screen keyboard (if it is visible)
	 */
	public void hideKeyboard() {
		// Overwritten in subclass - nothing to do here
	}

	public boolean isOffline() {
		return !getNetworkOperation().isOnline();
	}

	public boolean isToolLoadedFromStorage() {
		return toolLoadedFromStorage;
	}

	public void setToolLoadedFromStorage(boolean toolLoadedFromStorage) {
		this.toolLoadedFromStorage = toolLoadedFromStorage;
	}

	@Override
	public boolean isExam() {
		return getLAF() != null && getLAF().isExam();
	}

	public void setCloseBrowserCallback(Runnable runnable) {
		this.closeBroserCallback = runnable;

	}

	public void onBrowserClose() {
		if (this.closeBroserCallback != null) {
			this.closeBroserCallback.run();
			this.closeBroserCallback = null;
		}

	}

	public void addInsertImageCallback(Runnable runnable) {
		this.insertImageCallback = runnable;
	}

	public boolean isMenuShowing() {
		return false;
	}

	public void addToHeight(int i) {
		// for applets with keyboard only
	}

	WebsocketLogger webSocketLogger = null;
	private boolean keyboardNeeded;
	private String externalPath;

	public SensorLogger getSensorLogger() {
		if (webSocketLogger == null) {
			webSocketLogger = new WebsocketLogger(getKernel());
		}
		return webSocketLogger;
	}

	public void setKeyboardNeeded(boolean b) {
		this.keyboardNeeded = b;
	}

	public boolean isKeyboardNeeded() {
		return keyboardNeeded;
	}

	public static native void download(String url, String title) /*-{

		if ($wnd.navigator.msSaveBlob) {
			//works for chrome and internet explorer
			var image = document.createElement('img');
			image.src = image;

			$wnd.navigator.msSaveBlob(image, title);
		} else {
			//works for firefox
			var a = $doc.createElement("a");
			$doc.body.appendChild(a);
			a.style = "display: none";
			a.href = url;
			a.download = title;
			a.click();
		}

	}-*/;

	public void showStartTooltip() {
		// probably needed in full version only
	}

	public double getAbsLeft() {
		return this.getFrameElement().getAbsoluteLeft();
	}

	public double getAbsTop() {
		return this.getFrameElement().getAbsoluteTop();
	}

	public boolean enableFileFeatures() {
		return this.articleElement.getDataParamEnableFileFeatures();
	}

	public void setPrerelease(String prereleaseStr) {
		this.canary = false;
		this.prerelease = false;

		if ("canary".equals(prereleaseStr)) {
			canary = true;
			prerelease = true;
		} else if ("true".equals(prereleaseStr)) {
			this.prerelease = true;

		}

	}

	public void hideMenu() {
		// for applets with menubar
	}

	public void setExternalPath(String path) {
		this.externalPath = path;
		String title = "";
		if (getKernel() != null && getKernel().getConstruction() != null
				&& getKernel().getConstruction().getTitle() == null
				|| "".equals(getKernel().getConstruction().getTitle())) {
			int lastSlash = Math.max(path.lastIndexOf('/'),
					path.lastIndexOf('\\'));
			title = path.substring(lastSlash + 1).replace(".ggb", "");
			getKernel().getConstruction().setTitle(title);
		}
		getFileManager().setFileProvider(Provider.LOCAL);
	}

	public void checkSaved(Runnable runnable) {
		// TODO Auto-generated method stub

	}

	public void openCSV(String base64) {
		// TODO Auto-generated method stub

	}

	protected void updateNavigationBars() {
		if (showConstProtNavigationNeedsUpdate == null) {
			return;
		}

		for (int key : showConstProtNavigationNeedsUpdate.keySet()) {
			getGuiManager().getLayout().getDockManager().getPanel(key)
					.updateNavigationBar();
		}

	}

	@Override
	public boolean useShaders() {
		return true;
	}

	public void openOFF(String response) {
		// only makes sense in 3D

	}

	public static native String decode(String base64)/*-{
		return atob(base64);
	}-*/;

	public float getPixelRatio() {
		if (!has(Feature.RETINA)) {
			return 1;
		}
		return Browser.getPixelRatio()
				* (float) articleElement.getDataParamScale();
	}

	private ArrayList<MouseTouchGestureControllerW> euclidianHandlers = new ArrayList<MouseTouchGestureControllerW>();

	public void addWindowResizeListener(MouseTouchGestureControllerW mtg) {
		this.euclidianHandlers.add(mtg);
	}

	public boolean showToolBarHelp() {
		return getArticleElement().getDataParamShowToolBarHelp(true);
	}

	public Panel getPanel() {
		return RootPanel.get();
	}

}
