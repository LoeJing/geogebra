package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianBoundingBoxHandler;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.RemoveNeeded;
import org.geogebra.common.euclidian.text.InlineTextController;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineText;

/**
 * Class that handles drawing inline text elements.
 */
public class DrawInlineText extends Drawable implements RemoveNeeded, DrawWidget {

	private static final int padding = 8;
	private GeoInlineText text;
	private InlineTextController textController;
	private BoundingBox boundingBox;

	/**
	 * Create a new DrawInlineText instance.
	 *
	 * @param view view
	 * @param text geo element
	 */
	public DrawInlineText(EuclidianView view, GeoInlineText text) {
		super(view, text);
		this.text = text;
		this.textController = view.createInlineTextController();
		createEditor();
		update();
	}

	private void createEditor() {
		if (textController != null) {
			textController.create();
		}
	}

	@Override
	public void update() {
		GPoint2D point = text.getLocation();

		if (textController != null) {
			textController.setLocation(view.toScreenCoordX(point.getX()) + padding,
					view.toScreenCoordY(point.getY()) + padding);
			textController.setHeight(text.getHeight() - 2 * padding);
			textController.setWidth(text.getWidth() - 2 * padding);
		}

		getBoundingBox().setRectangle(getBounds());
	}

	public void toBackground() {
		textController.toBackground();
	}

	public void toForeground() {
		textController.toForeground();
	}

	@Override
	public GRectangle getBounds() {
		return AwtFactory.getPrototype().newRectangle(getLeft(), getTop(), getWidth(), getHeight());
	}

	@Override
	public void draw(GGraphics2D g2) {
		// drawing is left to the editor implementation (Carota)
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		return getBounds().contains(x, y);
	}

	@Override
	public boolean isInside(GRectangle rect) {
		return rect.contains(getBounds());
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public BoundingBox getBoundingBox() {
		if (boundingBox == null) {
			boundingBox = createBoundingBox(false, false);
			boundingBox.setRectangle(getBounds());
		}
		boundingBox.updateFrom(geo);
		return boundingBox;
	}

	@Override
	public void remove() {
		if (textController != null) {
			textController.discard();
		}
	}

	@Override
	public void setWidth(int newWidth) {
		text.setWidth(newWidth);
		if (textController != null) {
			textController.setWidth(newWidth);
		}
	}

	@Override
	public void setHeight(int newHeight) {
		text.setHeight(newHeight);
		if (textController != null) {
			textController.setHeight(newHeight);
		}
	}

	@Override
	public int getLeft() {
		GPoint2D point = text.getLocation();
		return view.toScreenCoordX(point.getX());
	}

	@Override
	public int getTop() {
		GPoint2D point = text.getLocation();
		return view.toScreenCoordY(point.getY());
	}

	@Override
	public void setAbsoluteScreenLoc(int x, int y) {
		// Not implemented
	}

	@Override
	public double getOriginalRatio() {
		return 0;
	}

	@Override
	public int getWidth() {
		return text.getWidth();
	}

	@Override
	public int getHeight() {
		return text.getHeight();
	}

	@Override
	public void resetRatio() {
		// Not implemented
	}

	@Override
	public boolean isFixedRatio() {
		return false;
	}

	@Override
	public void updateByBoundingBoxResize(GPoint2D point, EuclidianBoundingBoxHandler handler) {
		// Not implemented
	}
}