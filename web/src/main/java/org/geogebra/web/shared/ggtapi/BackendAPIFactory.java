package org.geogebra.web.shared.ggtapi;

import org.geogebra.common.main.Feature;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeAPI;
import org.geogebra.common.move.ggtapi.models.MarvlService;
import org.geogebra.common.move.ggtapi.models.MaterialRestAPI;
import org.geogebra.common.move.ggtapi.models.MowService;
import org.geogebra.common.move.ggtapi.operations.BackendAPI;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ArticleElementInterface;
import org.geogebra.web.shared.ggtapi.models.GeoGebraTubeAPIW;

/**
 * Class to provide the right backend API
 * for the given application.
 *
 * @author laszlo
 */
public class BackendAPIFactory {

	private AppW app;
	private ArticleElementInterface articleElement;
	private BackendAPI api = null;

	/**
	 *
	 * @param app The application.
	 */
	public BackendAPIFactory(AppW app) {
		this.app = app;
		articleElement = app.getArticleElement();
	}

	/**
	 *
	 * @return the backend API suitable for the applicaion.
	 */
	public BackendAPI get() {
		createApiIfNeeded();
		api.setClient(app.getClientInfo());
		return this.api;
	}

	private void createApiIfNeeded() {
		if (api != null) {
			return;
		}
		api = app.isMebis() ? newMaterialRestAPI() : newTubeAPI();
	}

	public BackendAPI newMaterialRestAPI() {
		String backendURL = articleElement.getParamBackendURL();

		if (StringUtil.empty(backendURL)) {
			return new MaterialRestAPI(GeoGebraTubeAPI.marvlUrl, new MarvlService());
		} else {
			return new MaterialRestAPI(backendURL, new MowService());
		}
	}

	private GeoGebraTubeAPIW newTubeAPI() {
		return new GeoGebraTubeAPIW(app.getClientInfo(),
				app.has(Feature.TUBE_BETA),
				articleElement);
	}
}
