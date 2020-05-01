package org.geogebra.web.html5.safeimage;

import com.google.gwt.user.client.Element;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.StringUtil;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;

public class ConvertToCanvas implements ImagePreprocessor {
	private final Canvas canvas;

	public ConvertToCanvas() {
		canvas = Canvas.createIfSupported();
	}

	@Override
	public boolean match(FileExtensions extension) {
		return extension.isAllowedImage()
				&& !extension.equals(FileExtensions.SVG);
	}

	@Override
	public void process(final ImageFile imageFile, final SafeImageProvider provider) {
		final Image image = new Image();
		//TODO: fix on cleanup events
		addLoadEventListener(image.getElement(), new Runnable() {
			@Override
			public void run() {
				drawImageToCanvas(image);
				String fileName = StringUtil.changeFileExtension(imageFile.getFileName(),
						FileExtensions.PNG);

				provider.onReady(new ImageFile(fileName, canvas.toDataUrl()));
			}
		});
		image.setUrl(imageFile.getContent());
	}

	private native void addLoadEventListener(Element element, Runnable runnable) /*-{
		element.addEventListener('load', function() {
		    runnable.@java.lang.Runnable::run()();
		});
	}-*/;

	private void drawImageToCanvas(Image image)  {
		canvas.setCoordinateSpaceWidth(image.getWidth());
		canvas.setCoordinateSpaceHeight(image.getHeight());
		canvas.getContext2d().drawImage(ImageElement.as(image.getElement()), 0, 0);
	}
}
