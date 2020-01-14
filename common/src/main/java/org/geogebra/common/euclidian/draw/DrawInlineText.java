package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianBoundingBoxHandler;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.RemoveNeeded;
import org.geogebra.common.euclidian.TextBoundingBox;
import org.geogebra.common.euclidian.text.InlineTextController;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.util.debug.Log;

/**
 * Class that handles drawing inline text elements.
 */
public class DrawInlineText extends Drawable implements RemoveNeeded, DrawWidget {

	private static final int padding = 8;

	private GeoInlineText text;
	private InlineTextController textController;

	private GAffineTransform directTransform;
	private GAffineTransform inverseTransform;

	private GPoint2D corner0;
	private GPoint2D corner1;
	private GPoint2D corner2;
	private GPoint2D corner3;

	private TextBoundingBox boundingBox;

	/**
	 * Create a new DrawInlineText instance.
	 *
	 * @param view view
	 * @param text geo element
	 */
	public DrawInlineText(EuclidianView view, GeoInlineText text) {
		super(view, text);

		this.text = text;
		this.textController = view.createInlineTextController(text);
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

		double angle = text.getAngle();
		int width = text.getWidth();
		int height = text.getHeight();

		directTransform = AwtFactory.getPrototype().newAffineTransform();
		directTransform.translate(view.toScreenCoordX(point.getX()), view.toScreenCoordY(point.getY()));
		directTransform.rotate(angle);
		directTransform.scale(width, height);

		try {
			inverseTransform = directTransform.createInverse();
		} catch (Exception e) {
			Log.error(e.getMessage());
		}

		corner0 = directTransform.transform(new GPoint2D.Double(0, 0), null);
		corner1 = directTransform.transform(new GPoint2D.Double(1, 0), null);
		corner2 = directTransform.transform(new GPoint2D.Double(1, 1), null);
		corner3 = directTransform.transform(new GPoint2D.Double(0, 1), null);

		if (textController != null) {
			textController.setLocation(view.toScreenCoordX(point.getX()),
					view.toScreenCoordY(point.getY()));
			textController.setHeight(height - 2 * padding);
			textController.setWidth(width - 2 * padding);
			textController.setAngle(angle);
			if (text.updateFontSize()) {
				textController.updateContent();
			}
		}

		if (boundingBox != null) {
			boundingBox.setRectangle(getBounds());
			boundingBox.setTransform(directTransform);
			boundingBox.setAngle(angle);
		}
	}

	/**
	 * Send this to background
	 */
	public void toBackground() {
		if (textController != null) {
			textController.toBackground();
		}
	}

	/**
	 * Send this to foreground
	 * @param x x mouse coordinates in pixels
	 * @param y y mouse coordinates in pixels
	 */
	public void toForeground(int x, int y) {
		if (textController != null) {
			textController.toForeground(x, y);
		}
	}

	@Override
	public GRectangle getBounds() {
		return AwtFactory.getPrototype().newRectangle(getLeft(), getTop(), getWidth(), getHeight());
	}

	@Override
	public TextBoundingBox getBoundingBox() {
		if (boundingBox == null) {
			boundingBox = new TextBoundingBox();
			boundingBox.setColor(view.getApplication().getPrimaryColor());
		}
		boundingBox.updateFrom(geo);
		return boundingBox;
	}

	@Override
	public double getWidthThreshold() {
		return GeoInlineText.DEFAULT_WIDTH;
	}

	@Override
	public double getHeightThreshold() {
		return text.getMinHeight();
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (textController != null) {
			textController.draw(g2, getLeft(), getTop());
		}
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		GPoint2D p = inverseTransform.transform(new GPoint2D.Double(x, y), null);
		return 0 < p.getX() && p.getX() < 1 && 0 < p.getY() && p.getY() < 1;
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
		return (int) Math.min(Math.min(corner0.getX(), corner1.getX()),
				Math.min(corner2.getX(), corner3.getX()));
	}

	@Override
	public int getTop() {
		return (int) Math.min(Math.min(corner0.getY(), corner1.getY()),
				Math.min(corner2.getY(), corner3.getY()));
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
		return (int) (Math.max(Math.max(corner0.getX(), corner1.getX()),
				Math.max(corner2.getX(), corner3.getX()))
				- Math.min(Math.min(corner0.getX(), corner1.getX()),
				Math.min(corner2.getX(), corner3.getX())));
	}

	@Override
	public int getHeight() {
		return (int) (Math.max(Math.max(corner0.getY(), corner1.getY()),
				Math.max(corner2.getY(), corner3.getY()))
				- Math.min(Math.min(corner0.getY(), corner1.getY()),
				Math.min(corner2.getY(), corner3.getY())));
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

	@Override
	public void fromPoints(ArrayList<GPoint2D> points) {
		int newWidth = (int) Math.abs(points.get(1).getX() - points.get(0).getX());
		int newHeight = (int) Math.abs(points.get(1).getY() - points.get(0).getY());

		if (Math.abs(newWidth - getWidth()) > 1 || Math.abs(newHeight - getHeight()) > 1) {
			text.setWidth(newWidth);
			text.setHeight(newHeight);
			text.setLocation(
					AwtFactory.getPrototype().newPoint2D(
							view.toRealWorldCoordX(Math.min(points.get(0).getX(),
									points.get(1).getX())),
							view.toRealWorldCoordY(Math.min(points.get(0).getY(),
									points.get(1).getY()))
					)
			);
		}
	}

	/**
	 * @param key
	 *            formatting option
	 * @param val
	 *            value (String, int or bool, depending on key)
	 */
	public void format(String key, Object val) {
		if (textController != null) {
			textController.format(key, val);
		}
	}

	/**
	 * @param key formatting option name
	 * @param fallback fallback when not set / indeterminate
	 * @param <T> option type
	 * @return formatting option value or fallback
	 */
	public <T> T getFormat(String key, T fallback) {
		if (textController != null) {
			return textController.getFormat(key, fallback);
		}
		return fallback;
	}
}
