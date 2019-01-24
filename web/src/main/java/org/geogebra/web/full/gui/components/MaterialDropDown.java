package org.geogebra.web.full.gui.components;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

/**
 * Popup menu following the Material Design.
 *
 * @author laszlo
 */
public class MaterialDropDown {

	// for checking item position. It is 0 in normal case.
	private static final int OFFSET_X = 0;
	private EuclidianView view;
	private GPopupMenuW menu;
	private int selectedIndex;
	private Widget parent;
	private int itemHeight;

	private enum RelativePosition {
		HIGH, CENTER, LOW
	}

	/**
	 *
	 * @param app        {@link AppW}
	 * @param itemHeight Height of an item in list
	 * @param parent     The widget the dropdown belongs to.
	 */
	public MaterialDropDown(AppW app, int itemHeight, Widget parent) {
		this.itemHeight = itemHeight;
		this.parent = parent;
		this.view = app.getActiveEuclidianView();
		menu = new GPopupMenuW(app);
		menu.getPopupPanel().addStyleName("matMenu");
		menu.getPopupPanel().addStyleName("dropDownPopup");
	}

	/**
	 *
	 * @param item to add
	 */
	public void addItem(AriaMenuItem item) {
		menu.addItem(item);
	}

	/**
	 * @param element
	 *            element where clicks should not collapse the selection
	 */
	public void addAutoHidePartner(Element element) {
		menu.getPopupPanel().addAutoHidePartner(element);
	}

	/**
	 *
	 * @return the index of the selected item.
	 */
	public int getSelectedIndex() {
		return selectedIndex;
	}

	/**
	 * Set item selected at the given index.
	 *
	 * @param index to select.
	 */
	public void setSelectedIndex(int index) {
		this.selectedIndex = index;
	}

	/**
	 * Opens DropDown at the top of the widget positioning selected item at the
	 * center.
	 */
	void show() {
		menu.setMenuShown(true);
		restoreHeight();
		RelativePosition pos = getRelativePosition();
		if (pos == RelativePosition.HIGH) {
			showHigh();
		} else if (pos == RelativePosition.CENTER) {
			showCenter();
		} else if (pos == RelativePosition.LOW) {
			showLow();
		}
	}

	private RelativePosition getRelativePosition() {
		int itemTop = getSelectedItemTop();
		int top = getTop();
		if (itemTop < top) {
			return RelativePosition.HIGH;
		} else if (getAllItemsHeight() - itemTop < getMaxHeight() / 2) {
			return RelativePosition.LOW;
		}
		return RelativePosition.CENTER;
	}

	private void showHigh() {
		menu.showAtPoint(getLeft(), getTop() - getSelectedItemTop());
	}

	private void showCenter() {
		int h2 = getMaxHeight() / 2;
		openAndScrollTo(getTop() - h2, getSelectedItemTop() - h2);
	}

	private void showLow() {
		int itemTop = getSelectedItemTop();
		int top = getTop();
		int h2 = getMaxHeight() / 2;
		int diff = getAllItemsHeight() - itemTop;
		if (diff < h2) {
			if (top < getMaxHeight() + diff) {
				setHeightInPx(top);
			}
			openAndScrollTo(diff, itemTop);
		}
	}

	private int getSelectedItemTop() {
		return getSelectedIndex() * itemHeight;
	}
	private int getLeft() {
		return parent.getAbsoluteLeft() + OFFSET_X;
	}

	private int getTop() {
		return parent.getAbsoluteTop() - parent.getOffsetHeight() / 2 - view.getAbsoluteTop();
	}

	private int getMaxHeight() {
		return view.getHeight() / 2;
	}

	private void openAndScrollTo(int top, int position) {
		menu.showAtPoint(getLeft(), top);
		menu.getPopupPanel().getElement().setScrollTop(position);
	}

	private void setHeightInPx(int height) {
		getStyle().setHeight(height, Unit.PX);
	}

	private void restoreHeight() {
		getStyle().clearHeight();
	}

	private Style getStyle() {
		return menu.getPopupPanel().getElement().getStyle();
	}

	private int getAllItemsHeight() {
		return menu.getComponentCount() * itemHeight;
	}

	/**
	 * @return whether the dropdown is currently open or not
	 */
	public boolean isOpened() {
		return menu.isMenuShown();
	}

	/**
	 * Hide the material dropdown popup
	 */
	public void close() {
		menu.hideMenu();
	}
}