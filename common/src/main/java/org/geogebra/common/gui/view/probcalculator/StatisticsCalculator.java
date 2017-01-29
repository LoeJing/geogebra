package org.geogebra.common.gui.view.probcalculator;

import java.util.HashMap;
import java.util.Map.Entry;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

/**
 * @author gabor StatisticCalculator common superclass
 */
public abstract class StatisticsCalculator {

	protected Construction cons;
	protected StatisticsCollection sc;
	protected StatisticsCalculatorProcessor statProcessor;
	protected StatisticsCalculatorHTML statHTML;
	protected Kernel kernel;

	// =========================================
	// Procedures
	// =========================================

	/***/
	public enum Procedure {
		ZMEAN_TEST, ZMEAN2_TEST, TMEAN_TEST, TMEAN2_TEST, ZPROP_TEST, ZPROP2_TEST, ZMEAN_CI, ZMEAN2_CI, TMEAN_CI, TMEAN2_CI, ZPROP_CI, ZPROP2_CI, GOF_TEST, CHISQ_TEST
	}

	protected Procedure selectedProcedure;

	protected HashMap<String, Procedure> mapNameToProcedure;
	protected HashMap<Procedure, String> mapProcedureToName;

	// =========================================
	// Misc
	// =========================================

	public static final String tail_left = "<";
	public static final String tail_right = ">";
	public static final String tail_two = ExpressionNodeConstants.strNOT_EQUAL;

	protected StringBuilder bodyText;

	protected String strMean;
	protected String strSD;
	protected String strSigma;
	protected String strSuccesses;
	protected String strN;
	// protected String strPooled;

	protected double[] s1;
	protected double[] s2;
	protected Localization loc;
	protected App app;

	// =========================================
	// Getters/Setters
	// =========================================

	public StatisticsCalculator(App app) {
		this.loc = app.getLocalization();
		this.app = app;
		cons = app.getKernel().getConstruction();
		kernel = cons.getKernel();
		sc = new StatisticsCollection();
		statProcessor = new StatisticsCalculatorProcessor(app, this, sc);
		statHTML = new StatisticsCalculatorHTML(app, this, sc);

		selectedProcedure = Procedure.ZMEAN_TEST;
	}

	public Procedure getSelectedProcedure() {
		return selectedProcedure;
	}

	/**
	 * Formats a number string using local format settings.
	 * 
	 * @param x
	 * @return
	 */
	public String format(double x) {
		StringTemplate highPrecision;

		if (kernel.useSignificantFigures) {
			highPrecision = StringTemplate.printFigures(StringType.GEOGEBRA,
					kernel.getPrintFigures(), false);
		} else {
			// override the default decimal place if < 4
			int d = kernel.getPrintDecimals() < 4 ? 4
					: cons.getKernel().getPrintDecimals();
			highPrecision = StringTemplate.printDecimals(StringType.GEOGEBRA, d,
					false);
		}
		// get the formatted string
		String result = kernel.format(x, highPrecision);

		return result;
	}

	public HashMap<Procedure, String> getMapProcedureToName() {
		return mapProcedureToName;
	}

	public StatisticsCalculatorProcessor getStatProcessor() {
		return statProcessor;
	}

	public StatisticsCollection getStatististicsCollection() {
		return sc;
	}

	public final void updateResult() {

		updateStatisticCollection();
		statProcessor.doCalculate();

		bodyText = new StringBuilder();
		bodyText.append(statHTML.getStatString());
		updateResultText(bodyText.toString());

		// prevent auto scrolling
		resetCaret();

	}

	protected abstract void updateStatisticCollection();

	protected abstract void resetCaret();

	protected abstract void updateResultText(String string);

	protected void combolabelsPreprocess() {
		if (mapNameToProcedure == null) {
			mapNameToProcedure = new HashMap<String, Procedure>();
		}
		if (mapProcedureToName == null) {
			mapProcedureToName = new HashMap<Procedure, String>();
		}

		mapNameToProcedure.clear();
		mapProcedureToName.clear();

		mapNameToProcedure.put(loc.getMenu("ZMeanTest"), Procedure.ZMEAN_TEST);
		mapNameToProcedure.put(loc.getMenu("ZMeanTest"), Procedure.ZMEAN_TEST);
		mapNameToProcedure.put(loc.getMenu("TMeanTest"), Procedure.TMEAN_TEST);
		mapNameToProcedure.put(loc.getMenu("ZMeanInterval"),
				Procedure.ZMEAN_CI);
		mapNameToProcedure.put(loc.getMenu("TMeanInterval"),
				Procedure.TMEAN_CI);
		mapNameToProcedure.put(loc.getMenu("ZTestDifferenceOfMeans"),
				Procedure.ZMEAN2_TEST);
		mapNameToProcedure.put(loc.getMenu("TTestDifferenceOfMeans"),
				Procedure.TMEAN2_TEST);
		mapNameToProcedure.put(loc.getMenu("ZEstimateDifferenceOfMeans"),
				Procedure.ZMEAN2_CI);
		mapNameToProcedure.put(loc.getMenu("TEstimateDifferenceOfMeans"),
				Procedure.TMEAN2_CI);
		mapNameToProcedure.put(loc.getMenu("ZProportionTest"),
				Procedure.ZPROP_TEST);
		mapNameToProcedure.put(loc.getMenu("ZProportionInterval"),
				Procedure.ZPROP_CI);
		mapNameToProcedure.put(loc.getMenu("ZTestDifferenceOfProportions"),
				Procedure.ZPROP2_TEST);
		mapNameToProcedure.put(loc.getMenu("ZEstimateDifferenceOfProportions"),
				Procedure.ZPROP2_CI);
		mapNameToProcedure.put(loc.getMenu("GoodnessOfFitTest"),
				Procedure.GOF_TEST);
		mapNameToProcedure.put(loc.getMenu("ChiSquaredTest"),
				Procedure.CHISQ_TEST);

		for (Entry<String, Procedure> entry : mapNameToProcedure.entrySet()) {

			this.mapProcedureToName.put(entry.getValue(), entry.getKey());
		}
	}

	protected void setLabelStrings() {

		strMean = loc.getMenu("Mean");
		strSD = loc.getMenu("SampleStandardDeviation.short");
		strSigma = loc.getMenu("StandardDeviation.short");
		strSuccesses = loc.getMenu("Successes");
		strN = loc.getMenu("N");
		// strPooled = loc.getMenu("Pooled");
	}

	public App getApp() {
		return kernel.getApplication();
	}
	
	protected void updateCollectionProcedure() {
		switch (selectedProcedure) {

		default:
			// do nothing
			break;
		case ZMEAN_TEST:
		case ZMEAN_CI:
		case TMEAN_TEST:
		case TMEAN_CI:
			sc.mean = s1[0];
			sc.sd = s1[1];
			sc.n = s1[2];
			break;

		case ZMEAN2_TEST:
		case ZMEAN2_CI:
		case TMEAN2_TEST:
		case TMEAN2_CI:
			sc.mean = s1[0];
			sc.sd = s1[1];
			sc.n = s1[2];
			sc.mean2 = s2[0];
			sc.sd2 = s2[1];
			sc.n2 = s2[2];

			// force the null hypothesis to zero
			// TODO: allow non-zero values
			sc.nullHyp = 0;
			break;

		case ZPROP_TEST:
		case ZPROP_CI:
			sc.count = s1[0];
			sc.n = s1[1];
			break;

		case ZPROP2_TEST:
		case ZPROP2_CI:
			sc.count = s1[0];
			sc.n = s1[1];
			sc.count2 = s2[0];
			sc.n2 = s2[1];

			// force the null hypothesis to zero
			// TODO: allow non-zero values
			sc.nullHyp = 0;
			break;
		}

		sc.validate();

	}


}
