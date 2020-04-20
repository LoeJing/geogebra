package org.geogebra.web.html5.safeimage;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * DOMPurify adapter
 *
 * @author laszlo
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class DOMPurify {

	/**
	 *
	 * @return the sanitized content.
	 */
	public static native String sanitize(String dirty);
}
