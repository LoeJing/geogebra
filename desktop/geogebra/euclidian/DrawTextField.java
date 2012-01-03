/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.euclidian;

import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.RemoveNeeded;
import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.geos.GeoTextField;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.util.Unicode;
import geogebra.gui.inputfield.AutoCompleteTextField;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Checkbox for free GeoBoolean object.
 * 
 * @author Michael
 * @version
 */
public final class DrawTextField extends Drawable implements RemoveNeeded {

	private final GeoTextField geoButton;

	private boolean isVisible;

	boolean hit = false;
	private String oldCaption;

	AutoCompleteTextField textField;
	JLabel label;
	ButtonListener bl;
	Container box = Box.createHorizontalBox();

	public DrawTextField(EuclidianView view, GeoTextField geo) {
		this.view = view;
		this.geoButton = geo;
		this.geo = geo;

		// action listener for checkBox
		bl = new ButtonListener();
		textField = new AutoCompleteTextField(geo.getLength(), view.getApplication());
		textField.showPopupSymbolButton(true);
		textField.setAutoComplete(false);
		textField.enableColoring(false);
		label = new JLabel("Label");
		label.setLabelFor(textField);
		textField.setVisible(true);
		label.setVisible(true);
		textField.addFocusListener(bl);
		label.addMouseListener(bl);
		label.addMouseMotionListener(bl);
		textField.addKeyListener(bl);
		box.add(label);
		box.add(textField);
		view.add(box);

		// Add mouse listeners to textField so that it becomes draggable
		// on a right click. These listeners are registered first to prevent
		// the JTextField listeners from initiating editing.
		/*
		 * MouseListener[] ml = textField.getMouseListeners(); for(int i = 0;
		 * i<ml.length; i++){ textField.removeMouseListener(ml[i]); }
		 * MouseMotionListener[] mml = textField.getMouseMotionListeners();
		 * for(int i = 0; i<mml.length; i++){
		 * textField.removeMouseMotionListener(mml[i]); }
		 * 
		 * textField.addMouseListener(bl); for(int i = 0; i<mml.length; i++){
		 * textField.addMouseMotionListener(mml[i]); }
		 * 
		 * textField.addMouseMotionListener(bl); for(int i = 0; i<ml.length;
		 * i++){ textField.addMouseListener(ml[i]); }
		 */

		update();
	}

	private class ButtonListener implements MouseListener, MouseMotionListener,
			FocusListener, KeyListener {

		private boolean dragging = false;
		private final EuclidianController ec = ((EuclidianView)view).getEuclidianController();

		public ButtonListener() {
			// TODO Auto-generated constructor stub
		}

		/**
		 * Handles click on check box. Changes value of GeoBoolean.
		 */
		@SuppressWarnings("unused")
		public void itemStateChanged(ItemEvent e) {
			// TODO delete?
		}

		public void mouseDragged(MouseEvent e) {

			dragging = true;
			e.translatePoint(box.getX(), box.getY());
			ec.mouseDragged(e);
			((EuclidianView)view).setToolTipText(null);
		}

		public void mouseMoved(MouseEvent e) {

			e.translatePoint(box.getX(), box.getY());
			ec.mouseMoved(e);
			((EuclidianView)view).setToolTipText(null);
		}

		public void mouseClicked(MouseEvent e) {

			if (e.getClickCount() > 1) {
				return;
			}

			e.translatePoint(box.getX(), box.getY());
			ec.mouseClicked(e);
		}

		public void mousePressed(MouseEvent e) {

			// prevent textField editing on right click
			if (Application.isRightClick(e)) {
				e.consume();
			}

			dragging = false;
			e.translatePoint(box.getX(), box.getY());
			ec.mousePressed(e);
		}

		public void mouseReleased(MouseEvent e) {

			// prevent textField editing on right click
			if (Application.isRightClick(e)) {
				e.consume();
			}

			if (!dragging && !e.isMetaDown() && !e.isPopupTrigger()
					&& (view.getMode() == EuclidianConstants.MODE_MOVE)) {
				// handle LEFT CLICK
				// geoBool.setValue(!geoBool.getBoolean());
				// geoBool.updateRepaint();
				// geo.runScript();
				//

				// make sure itemChanged does not change
				// the value back my faking a drag
				dragging = true;
			} else {
				// handle right click and dragging
				e.translatePoint(box.getX(), box.getY());
				ec.mouseReleased(e);
			}

		}

		public void mouseEntered(MouseEvent arg0) {
			hit = true;
			((EuclidianView)view).setToolTipText(null);
			updateText();
		}

		public void mouseExited(MouseEvent arg0) {
			hit = false;
		}

		public void focusGained(FocusEvent e) {
			((EuclidianView)view).getEuclidianController().textfieldHasFocus(true);
			updateText();

		}

		public void focusLost(FocusEvent e) {
			((EuclidianView)view).getEuclidianController().textfieldHasFocus(false);

			GeoElement linkedGeo = geoButton.getLinkedGeo();

			if (linkedGeo != null) {

				String defineText = textField.getText();

				if (linkedGeo.isGeoLine()) {

					// not y=
					// and not Line[A,B]
					if ((defineText.indexOf('=') == -1)
							&& (defineText.indexOf('[') == -1)) {
						// x + 1 changed to
						// y = x + 1
						defineText = "y=" + defineText;
					}

					String prefix = linkedGeo.getLabel() + ":";
					// need a: in front of
					// X = (-0.69, 0) + \lambda (1, -2)
					if (!defineText.startsWith(prefix)) {
						defineText = prefix + defineText;
					}
				} else if (linkedGeo.isGeoText()) {
					defineText = "\"" + defineText + "\"";
				} else if (linkedGeo.isGeoPoint()) {
					if (((GeoPoint2) linkedGeo).toStringMode == AbstractKernel.COORD_COMPLEX) {
						// z=2 doesn't work for complex numbers (parses to
						// GeoNumeric)
						defineText = defineText + "+0" + Unicode.IMAGINARY;
					}
				} else if (linkedGeo instanceof FunctionalNVar) {
					// string like f(x,y)=x^2
					// or f(\theta) = \theta
					defineText = linkedGeo.getLabel() + "("
							+ ((FunctionalNVar) linkedGeo).getVarString()
							+ ")=" + defineText;
				}

				try {
					linkedGeo = ((AbstractKernel) geo.getKernel())
							.getAlgebraProcessor()
							.changeGeoElementNoExceptionHandling(linkedGeo,
									defineText, false, true);
				} catch (Exception e1) {
					geo.getKernel().getApplication().showError(e1.getMessage());
					updateText();
					return;
				}
				geoButton.setLinkedGeo(linkedGeo);

				updateText();

			}

			geo.runScripts(textField.getText());

		}

		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub

		}

		public void keyReleased(KeyEvent e) {
			if (e.getKeyChar() == '\n') {
				// geo.runScripts(textField.getText());

				// this should be enough to trigger script event
				// ie in focusLost
				((EuclidianView)view).requestFocus();
			}

		}

		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub

		}

	}

	void updateText() {

		GeoElement linkedGeo = geoButton.getLinkedGeo();
		if (linkedGeo != null) {

			String text;

			if (linkedGeo.isGeoText()) {
				text = ((GeoText) linkedGeo).getTextString();
			} else {

				// want just a number for eg a=3 but we want variables for eg
				// y=m x + c
				boolean substituteNos = linkedGeo.isGeoNumeric()
						&& linkedGeo.isIndependent();
				text = linkedGeo.getFormulaString(StringType.GEOGEBRA,
						substituteNos);
			}

			if (linkedGeo.isGeoText() && (text.indexOf("\n") > -1)) {
				// replace linefeed with \\n
				while (text.indexOf("\n") > -1) {
					text = text.replaceAll("\n", "\\\\\\\\n");
				}
			}
			if (!textField.getText().equals(text)) { // avoid redraw error
				textField.setText(text);
			}

		}

		geoButton.setText(textField.getText());

	}
	private int oldLength = 0;
	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		// textField.setVisible(isVisible);
		// label.setVisible(isVisible);
		box.setVisible(isVisible);
		int length = geoButton.getLength();
		if(length!=oldLength){
			textField.setColumns(length);
			textField.showPopupSymbolButton(length>8);
		}
		if (!isVisible) {
			return;
		}

		// show hide label by setting text
		if (geo.isLabelVisible()) {
			// get caption to show r
			String caption = geo.getCaption();
			if (!caption.equals(oldCaption)) {
				oldCaption = caption;
				labelDesc = GeoElement.indicesToHTML(caption, true);
			}
			box.setVisible(false); // avoid redraw error
			label.setText(labelDesc);
			box.setVisible(true);
		} else {
			label.setText("");
		}

		int fontSize = view.getFontSize() + geoButton.getFontSize();
		Application app = ((EuclidianView)view).getApplication();

		Font vFont = geogebra.awt.Font.getAwtFont(view.getFont());
		Font font = app.getFontCanDisplay(textField.getText(), false,
				vFont.getStyle(), fontSize);

		textField.setOpaque(true);
		label.setOpaque(false);
		textField.setFont(font);
		label.setFont(font);
		textField.setForeground(geogebra.awt.Color.getAwtColor(geo
				.getObjectColor()));
		label.setForeground(geogebra.awt.Color.getAwtColor(geo.getObjectColor()));
		Color bgCol = geogebra.awt.Color.getAwtColor(geo.getBackgroundColor());
		textField.setBackground(bgCol != null ? bgCol : ((EuclidianView)view).getBackground());

		textField.setFocusable(true);
		textField.setEditable(true);
		updateText();
		// set checkbox state
		// jButton.removeItemListener(bl);
		// jButton.setSelected(geo.getBoolean());
		// jButton.addItemListener(bl);

		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;
		Dimension prefSize = box.getPreferredSize();
		labelRectangle.setBounds(xLabel, yLabel, prefSize.width,
				prefSize.height);
		box.setBounds(geogebra.awt.Rectangle.getAWTRectangle(labelRectangle));
	}

	@SuppressWarnings("unused")
	private void updateLabel() {
		// TODO delete?
		/*
		 * xLabel = geo.labelOffsetX; yLabel = geo.labelOffsetY;
		 * 
		 * labelRectangle.setBounds(xLabel, yLabel, ((textSize == null) ? 0 :
		 * textSize.x), 12);
		 */

	}

	@Override
	final public void draw(geogebra.common.awt.Graphics2D g2) {
		if (isVisible) {
			if (geo.doHighlighting()) {
				label.setOpaque(true);
				label.setBackground(Color.lightGray);

			} else {
				label.setOpaque(false);
			}
		}
	}

	/**
	 * Removes button from view again
	 */
	final public void remove() {
		((EuclidianView)view).remove(box);
	}

	/**
	 * was this object clicked at? (mouse pointer location (x,y) in screen
	 * coords)
	 */
	@Override
	final public boolean hit(int x, int y) {
		return hit;
	}

	@Override
	final public boolean isInside(geogebra.common.awt.Rectangle rect) {
		return rect.contains(labelRectangle);
	}

	/**
	 * Returns false
	 */
	@Override
	public boolean hitLabel(int x, int y) {
		return false;
	}

	@Override
	final public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	final public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	public void setFocus(final String str) {
		textField.requestFocus();
		if (str != null) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					textField.setText(str);
				}
			});
		}
		
	}

}
