package org.geogebra.web.web.gui.applet;

import java.util.ArrayList;

import javax.swing.SwingConstants;

import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.debug.LoggerW;
import org.geogebra.web.html5.util.keyboard.UpdateKeyBoardListener;
import org.geogebra.web.html5.util.keyboard.VirtualKeyboardW;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.HeaderPanelDeck;
import org.geogebra.web.web.gui.MyHeaderPanel;
import org.geogebra.web.web.gui.app.GGWMenuBar;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.app.ShowKeyboardButton;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.gui.layout.DockGlassPaneW;
import org.geogebra.web.web.gui.layout.DockManagerW;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.layout.panels.AlgebraDockPanelW;
import org.geogebra.web.web.gui.view.algebra.AlgebraViewW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Frame for applets with GUI
 *
 */
public class GeoGebraFrameBoth extends GeoGebraFrameW implements
		HeaderPanelDeck, UpdateKeyBoardListener {

	private AppletFactory factory;
	private DockGlassPaneW glass;
	private GGWToolBar ggwToolBar = null;
	private GGWMenuBar ggwMenuBar;
	private boolean keyboardVisibilityChanging;
	private final SimplePanel kbButtonSpace = new SimplePanel();

	/**
	 * @param factory
	 *            factory for applets (2D or 3D)
	 * @param laf
	 *            look and feel
	 */
	public GeoGebraFrameBoth(AppletFactory factory, GLookAndFeel laf) {
		super(laf);
		this.factory = factory;
		kbButtonSpace.addStyleName("kbButtonSpace");
		this.add(kbButtonSpace);
	}

	@Override
	protected AppW createApplication(ArticleElement article,
			GLookAndFeelI laf) {
		AppW application = factory.getApplet(article, this, laf);
		getArticleMap().put(article.getId(), application);
		
		if (app!= null && app.has(Feature.SHOW_ONE_KEYBOARD_BUTTON_IN_FRAME)){
			kbButtonSpace.addStyleName("kbButtonSpace");
			this.add(kbButtonSpace);
		}
		
		this.glass = new DockGlassPaneW(new GDimensionW(
				article.getDataParamWidth(), article.getDataParamHeight()));
		this.add(glass);
		return application;
	}


	/**
	 * Main entry points called by geogebra.web.Web.startGeoGebra()
	 * 
	 * @param geoGebraMobileTags
	 *            list of &lt;article&gt; elements of the web page
	 * @param factory
	 *            applet factory
	 * @param laf
	 *            look and feel
	 */
	public static void main(ArrayList<ArticleElement> geoGebraMobileTags,
			AppletFactory factory, GLookAndFeel laf) {

		for (final ArticleElement articleElement : geoGebraMobileTags) {
			final GeoGebraFrameW inst = new GeoGebraFrameBoth(factory, laf);
			inst.ae = articleElement;
			LoggerW.startLogger(inst.ae);
			inst.createSplash(articleElement);
			RootPanel.get(articleElement.getId()).add(inst);
		}
		if (geoGebraMobileTags.isEmpty()) {
			return;
		}

		if (geoGebraMobileTags.size() > 0) {
		// // now we can create dummy elements before & after each applet
		// // with tabindex 10000, for ticket #5158
		// tackleFirstDummy(geoGebraMobileTags.get(0));
		//
		//
			tackleLastDummy(geoGebraMobileTags
					.get(geoGebraMobileTags.size() - 1));
		// // programFocusEvent(firstDummy, lastDummy);
		}
	}

	/**
	 * @param el
	 *            html element to render into
	 * @param factory
	 *            applet factory
	 * @param laf
	 *            look and feel
	 * @param clb
	 *            call this after rendering
	 */
	public static void renderArticleElement(Element el, AppletFactory factory,
			GLookAndFeel laf, JavaScriptObject clb) {

		GeoGebraFrameW.renderArticleElementWithFrame(el, new GeoGebraFrameBoth(
				factory, laf), clb);

		GeoGebraFrameW.reCheckForDummies(el);
	}

	/**
	 * @return glass pane for view moving
	 */
	public DockGlassPaneW getGlassPane() {
		return this.glass;
	}

	/**
	 * Attach glass pane to frame
	 */
	public void attachGlass() {
		if (this.glass != null) {
			this.add(glass);
		}
	}

	private boolean[] childVisible = new boolean[0];
	private boolean keyboardShowing = false;
	private ShowKeyboardButton showKeyboardButton;
	private int keyboardHeight;
	private DockPanelW dockPanelKB;
	private ShowKeyboardButton keyboardButton;

	@Override
	public void showBrowser(HeaderPanel bg) {
		keyBoardNeeded(false, null);
		GeoGebraFrameW frameLayout = this;
		final int count = frameLayout.getWidgetCount();
		final int oldHeight = this.getOffsetHeight();
		final int oldWidth = this.getOffsetWidth();
		childVisible = new boolean[count];
		for (int i = 0; i < count; i++) {
			childVisible[i] = frameLayout.getWidget(i).isVisible();
			frameLayout.getWidget(i).setVisible(false);
		}
		frameLayout.add(bg);
		bg.setHeight(oldHeight + "px");
		bg.setWidth(oldWidth + "px");
		bg.onResize();
		bg.setVisible(true);

		if (bg instanceof MyHeaderPanel) {
			((MyHeaderPanel) bg).setFrame(this);
		}
		// frameLayout.forceLayout();

	}

	@Override
	public void hideBrowser(MyHeaderPanel bg) {
		GeoGebraFrameW frameLayout = this;
		frameLayout.remove(bg);
		final int count = frameLayout.getWidgetCount();
		for (int i = 0; i < count; i++) {
			if (childVisible.length > i) {
				frameLayout.getWidget(i).setVisible(childVisible[i]);
			}
		}
		// frameLayout.setLayout(app);
		// frameLayout.forceLayout();
		app.updateViewSizes();

	}

	@Override
	public void doShowKeyBoard(final boolean show,
			MathKeyboardListener textField) {

		if (app.has(Feature.FIX_KEYBOARD_POSITION) && (keyboardVisibilityChanging)) {
			return;
		}
		if (this.keyboardShowing == show) {
			app.getGuiManager().setOnScreenKeyboardTextField(textField);
			return;
		}

		GuiManagerInterfaceW gm = app.getGuiManager();
		if (gm != null) {
			gm.onScreenEditingEnded();
		}

		// this.mainPanel.clear();

		if (show) {
			keyboardVisibilityChanging = true;
			app.hideMenu();
			app.persistWidthAndHeight();
			addKeyboard(textField);
		} else {
			keyboardVisibilityChanging = true;
			app.persistWidthAndHeight();
			showKeyboardButton(textField);
			removeKeyboard(textField);
			keyboardVisibilityChanging = false;
		}

		// this.mainPanel.add(this.dockPanel);

		Timer timer = new Timer() {
			@Override
			public void run() {
				// onResize();
				// dockPanel.onResize();
				scrollToInputField();
			}
		};
		timer.schedule(0);
	}

	private void removeKeyboard(MathKeyboardListener textField) {
		final VirtualKeyboardW keyBoard = app.getGuiManager()
				.getOnScreenKeyboard(textField, this);
		this.keyboardShowing = false;
		app.addToHeight(keyboardHeight);
		this.remove(keyBoard);
		app.updateCenterPanel(true);
		// TODO too expensive
		app.updateViewSizes();
		keyBoard.resetKeyboardState();
	}

	/**
	 * Show keyboard and connect it to textField
	 * 
	 * @param textField
	 *            keyboard listener
	 */
	void addKeyboard(final MathKeyboardListener textField) {
		final VirtualKeyboardW keyBoard = app.getGuiManager()
				.getOnScreenKeyboard(textField, this);
		this.keyboardShowing = true;

		keyBoard.show();
		keyBoard.setVisible(false);
		if (app.has(Feature.KEYBOARD_BEHAVIOUR)) {
			app.addAsAutoHidePartnerForPopups(keyBoard.asWidget().getElement());
		}
		CancelEventTimer.keyboardSetVisible();
		// this.mainPanel.addSouth(keyBoard, keyBoard.getOffsetHeight());
		this.add(keyBoard);

		app.invokeLater(new Runnable() {

			@Override
			public void run() {
				final boolean showPerspectivesPopup = app
						.isPerspectivesPopupVisible();
				onKeyboardAdded(keyBoard);
				if (showPerspectivesPopup) {
					app.showPerspectivesPopup();
				}
				if(app.has(Feature.KEYBOARD_BEHAVIOUR)){
					if(textField!=null){
						textField.setFocus(true, true);
					}
				}
			}
		});
	}

	// @Override
	// public void showInputField() {
	// Timer timer = new Timer() {
	// @Override
	// public void run() {
	// scrollToInputField();
	// }
	// };
	// timer.schedule(0);
	// }
	/**
	 * Callback for keyboard; takes care of resizing
	 * 
	 * @param keyBoard
	 *            keyboard
	 */
	protected void onKeyboardAdded(final VirtualKeyboardW keyBoard) {
		keyboardHeight = keyBoard.getOffsetHeight();
		app.addToHeight(-keyboardHeight);
		app.updateCenterPanel(true);
		// TODO maybe too expensive?
		app.updateViewSizes();
		GeoGebraFrameBoth.this.add(keyBoard);
		keyBoard.setVisible(true);
		if (showKeyboardButton != null) {
			showKeyboardButton.hide();
		}
		app.centerAndResizePopups();
		keyboardVisibilityChanging = false;

	}

	/**
	 * Scroll to the input-field, if the input-field is in the algebraView.
	 */
	void scrollToInputField() {
		if (app.showAlgebraInput()
				&& app.getInputPosition() == InputPosition.algebraView) {
			AlgebraDockPanelW dp = (AlgebraDockPanelW) (app.getGuiManager()
					.getLayout().getDockManager().getPanel(App.VIEW_ALGEBRA));
			if (app.has(Feature.AV_SINGLE_TAP_EDIT)) {
			dp.scrollToActiveItem();
			} else {
				dp.scrollToBottom();
			}
		}
	}

	@Override
	public void showKeyBoard(boolean show, MathKeyboardListener textField,
			boolean forceShow) {
		if (forceShow) {
			doShowKeyBoard(show, textField);
		} else {
			keyBoardNeeded(show, textField);
		}
	}

	@Override
	public void keyBoardNeeded(boolean show, MathKeyboardListener textField) {
		if (app.has(Feature.FIX_KEYBOARD_POSITION) && this.keyboardVisibilityChanging) {
			return;
		}
		if (app.getLAF().isTablet()
				|| keyboardShowing // if keyboard is already
									// showing, we don't have
									// to handle the showKeyboardButton
				|| app.getGuiManager().getOnScreenKeyboard(textField, this)
						.shouldBeShown()) {
			doShowKeyBoard(show, textField);
		} else {
			showKeyboardButton(textField);
		}

	}

	private void showKeyboardButton(MathKeyboardListener textField) {
		if (!appNeedsKeyboard()) {
			return;
		}
		if (showKeyboardButton == null) {
			DockManagerW dm = (DockManagerW) app.getGuiManager().getLayout()
					.getDockManager();
			dockPanelKB = dm.getPanelForKeyboard();

			if (dockPanelKB != null) {
				showKeyboardButton = new ShowKeyboardButton(this, dm,
						dockPanelKB, app);
				dockPanelKB.setKeyBoardButton(showKeyboardButton);
			}

		}

		if (showKeyboardButton != null) {
			if(app.has(Feature.SHOW_KEYBOARD_BUTTON_IN_EVERY_VIEW)){
				if(app.has(Feature.SHOW_ONE_KEYBOARD_BUTTON_IN_FRAME)){
					this.setKeyboardButton(showKeyboardButton);
				} else {
					DockManagerW dm = (DockManagerW) app.getGuiManager().getLayout()
							.getDockManager();
					DockPanelW newDockPanelKB = dm.getPanelForKeyboard();
					if(dockPanelKB != newDockPanelKB){
						dockPanelKB.setKeyBoardButton(null);
						showKeyboardButton.removeFromParent();
						dockPanelKB = newDockPanelKB;
						showKeyboardButton = new ShowKeyboardButton(this, dm,
								dockPanelKB, app);
						dockPanelKB.setKeyBoardButton(showKeyboardButton);
					}
				}
			}
			showKeyboardButton.show(app.isKeyboardNeeded(), textField);
		}
	}

	private void setKeyboardButton(ShowKeyboardButton button){
		//this.keyboardButton = button;
		//kbButtonSpace.add(button);
		this.add(showKeyboardButton);
	}
	
	private boolean appNeedsKeyboard() {
		return (app.showAlgebraInput() && app.getInputPosition() == InputPosition.algebraView)
				|| (app.showView(App.VIEW_CAS));
	}



	/**
	 * @param show
	 *            whether to show keyboard button
	 */
	public void showKeyboardButton(boolean show) {
		if (showKeyboardButton == null) {
			return;
		}
		if (show) {
			showKeyboardButton.setVisible(true);
		} else {
			showKeyboardButton.hide();
		}
	}

	/**
	 * Make sure keyboard visibility corresponds to both app.isKeyboardNeeded()
	 * and appNeedsKeyboard() TODO rename one of those functions
	 */
	public void refreshKeyboard() {
		if (keyboardShowing) {
			final VirtualKeyboardW keyBoard = app.getGuiManager()
					.getOnScreenKeyboard(null, this);
			if (app.isKeyboardNeeded()) {
				add(keyBoard);
			} else {
				removeKeyboard(null);
				if (this.showKeyboardButton != null) {
					this.showKeyboardButton.hide();
				}
			}
		} else {
			if (app != null && app.isKeyboardNeeded() && appNeedsKeyboard()) {
				if (!app.isStartedWithFile()) {
					keyboardShowing = true;
					app.invokeLater(new Runnable() {

						@Override
						public void run() {
							app.persistWidthAndHeight();
							addKeyboard(null);
							app.getGuiManager().focusScheduled(false, false,
									false);
							new Timer() {

								@Override
								public void run() {
									DockManagerW dm = (DockManagerW) app
											.getGuiManager().getLayout()
											.getDockManager();
									MathKeyboardListener ml = dm
											.getPanelForKeyboard()
											.getKeyboardListener();
									((GuiManagerW) app.getGuiManager())
											.setOnScreenKeyboardTextField(ml);
									ml.setFocus(true, true);
									ml.ensureEditing();
									if (app.getGuiManager().hasAlgebraView()) {
										AlgebraViewW av = (AlgebraViewW) app
												.getAlgebraView();
										av.setDefaultUserWidth();
									}

								}
							}.schedule(500);

						}
					});
				} else {
					this.showKeyboardButton(null);
					app.getGuiManager().getOnScreenKeyboard(null, this)
							.showOnFocus();
				}

			} else if (app != null && app.isKeyboardNeeded()) {
				this.showKeyboardButton(true);
			}

			else if (app != null && !app.isKeyboardNeeded()
					&& this.showKeyboardButton != null) {
				this.showKeyboardButton.hide();
			}
		}
	}

	@Override
	public boolean isKeyboardShowing() {
		return this.keyboardShowing;
	}

	@Override
	public void showKeyboardOnFocus() {
		if (app != null) {
			this.app.getGuiManager().getOnScreenKeyboard(null, this)
					.showOnFocus();
		}
	}

	@Override
	public void updateKeyboardHeight() {
		// TODO update of height
	}

	@Override
	public double getKeyboardHeight() {
		return keyboardShowing ? keyboardHeight : 0;
	}

	/**
	 * Adds menu; if toolbar is missing also add it
	 * 
	 * @param app1
	 *            application
	 */
	public void attachMenubar(AppW app1) {
		if (ggwToolBar == null) {
			ggwToolBar = new GGWToolBar();
			ggwToolBar.init(app1);
			insert(ggwToolBar, 0);
		}
		ggwToolBar.attachMenubar();
	}

	/**
	 * Adds toolbar
	 * 
	 * @param app1
	 *            application
	 */
	public void attachToolbar(AppW app1) {
		// reusing old toolbar is probably a good decision
		if (ggwToolBar == null) {
			ggwToolBar = new GGWToolBar();
			ggwToolBar.init(app1);
		} else {
			ggwToolBar.updateClassname(app1.getToolbarPosition());
		}
		if (app1.getToolbarPosition() == SwingConstants.SOUTH) {
			add(ggwToolBar);
		} else {
			insert(ggwToolBar, 0);
		}
	}

	@Override
	public GGWToolBar getToolbar() {
		return ggwToolBar;
	}

	@Override
	public void setMenuHeight(boolean linearInputbar) {
		// TODO in app mode we need to change menu height when inputbar is
		// visible
	}

	/**
	 * @param app1
	 *            application
	 * @return menubar
	 */
	public GGWMenuBar getMenuBar(AppW app1) {
		if (ggwMenuBar == null) {
			ggwMenuBar = new GGWMenuBar();
			((GuiManagerW) app1.getGuiManager()).getObjectPool()
					.setGgwMenubar(
					ggwMenuBar);
		}
		return ggwMenuBar;
	}

	/**
	 * Close all popups and if event was not from menu, also close menu
	 * 
	 * @param event
	 *            browser event
	 */
	public void closePopupsAndMaybeMenu(NativeEvent event) {
		// app.closePopups(); TODO
		if (app.isMenuShowing()
				&& !Dom.eventTargetsElement(event, ggwMenuBar.getElement())
				&& !Dom.eventTargetsElement(event,
						getToolbar().getOpenMenuButtonElement())
				&& !getGlassPane().isDragInProgress()) {
			app.toggleMenu();
		}
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (app == null || !app.getUseFullGui()) {
			return;
		}
		final int eventType = DOM.eventGetType(event);
		if (eventType == Event.ONMOUSEDOWN || eventType == Event.ONTOUCHSTART) {
			closePopupsAndMaybeMenu(event);
		}
	}

	public void attachKeyboardButton() {
		if(showKeyboardButton != null){
			add(this.showKeyboardButton);
		}
		
	}
}