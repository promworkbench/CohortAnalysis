package org.processmining.cohortanalysis.parameters;

import java.util.Set;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.cohortanalysis.feature.FeatureFactory;
import org.processmining.cohortanalysis.feature.FeatureFactoryBasic;
import org.processmining.plugins.inductiveminer2.mining.MiningParameters;

import gnu.trove.set.hash.THashSet;

public class CohortAnalysisParametersDefault extends CohortAnalysisParametersAbstract {

	public static final double defaultSizeThreshold = 0.01;
	public static final int defaultScaleRandom = 10;
	public static final XEventClassifier defaultClassifier = MiningParameters.defaultClassifier;
	public static final Set<String> defaultExcluceTraceAttributes = new THashSet<>();
	static {
		defaultExcluceTraceAttributes.add("concept:name");
		defaultExcluceTraceAttributes.add("number of events");
	}
	public static final boolean defaultDebug = false;
	public static final FeatureFactory defaultFeatureFactory = new FeatureFactoryBasic();
	public static final int defaultMaxFeatureSetSize = 2;
	public static final double defaultDiversifyThreshold = 0.7;
	public static final int defaultThreads = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);

	public CohortAnalysisParametersDefault() {
		super(defaultSizeThreshold, defaultScaleRandom, defaultClassifier, defaultExcluceTraceAttributes, defaultDebug,
				defaultFeatureFactory, defaultMaxFeatureSetSize, defaultDiversifyThreshold, defaultThreads);
	}

}
