package org.geogebra.web.html5.safeimage;

import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.ImageManager;
import org.geogebra.web.html5.Browser;

public class SVGPreprocessor implements ImagePreprocessor {

	@Override
	public boolean match(FileExtensions extension) {
		return FileExtensions.SVG.equals(extension);
	}

	@Override
	public void process(ImageFile imageFile, SafeImageProvider provider) {
		String clean = DOMPurify.sanitize(imageFile.getContent());
		provider.onReady(new ImageFile(imageFile.getFileName(), encodeSVG(clean)));
	}

	private String encodeSVG(String content) {
		return Browser.encodeSVG(ImageManager.fixSVG(content));
	}
}
