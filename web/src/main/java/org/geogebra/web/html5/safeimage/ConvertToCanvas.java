package org.geogebra.web.html5.safeimage;

import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.StringUtil;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

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
		RootPanel.get().add(image);
		image.setUrl(imageFile.getContent());
		image.addLoadHandler(new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				drawImageToCanvas(image);
				String fileName = StringUtil.changeFileExtension(imageFile.getFileName(),
						FileExtensions.PNG);

				provider.onReady(new ImageFile(fileName, canvas.toDataUrl()));
			}
		});
	}

	private void drawImageToCanvas(Image image)  {
		canvas.setCoordinateSpaceWidth(image.getWidth());
		canvas.setCoordinateSpaceHeight(image.getHeight());
		canvas.getContext2d().drawImage(ImageElement.as(image.getElement()), 0, 0);
	}
}
