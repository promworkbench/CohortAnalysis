package org.processmining.cohortanalysis.parameters;

import java.util.Set;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.cohortanalysis.feature.FeatureFactory;

public abstract class CohortAnalysisParametersAbstract implements CohortAnalysisParameters {
	private double sizeThreshold;
	private int scaleRandom;
	private XEventClassifier classifier;
	private Set<String> excludeTraceAttributes;
	private boolean debug;
	private FeatureFactory featureFactory;
	private int maxFeatureSetSize;
	private double diversifyThreshold;
	private int threads;

	public CohortAnalysisParametersAbstract(double sizeThreshold, int scaleRandom, XEventClassifier classifier,
			Set<String> excludeTraceAttributes, boolean debug, FeatureFactory featureFactory, int maxFeatureSetSize,
			double diversifyThreshold, int threads) {
		this.sizeThreshold = sizeThreshold;
		this.scaleRandom = scaleRandom;
		this.classifier = classifier;
		this.excludeTraceAttributes = excludeTraceAttributes;
		this.debug = debug;
		this.featureFactory = featureFactory;
		this.maxFeatureSetSize = maxFeatureSetSize;
		this.diversifyThreshold = diversifyThreshold;
		this.threads = threads;
	}

	@Override
	public int getScaleRandom() {
		return scaleRandom;
	}

	public void setScaleRandom(int scaleRandom) {
		this.scaleRandom = scaleRandom;
	}

	@Override
	public double getSizeThreshold() {
		return sizeThreshold;
	}

	public void setSizeThreshold(double sizeThreshold) {
		this.sizeThreshold = sizeThreshold;
	}

	@Override
	public XEventClassifier getClassifier() {
		return classifier;
	}

	public void setClassifier(XEventClassifier classifier) {
		this.classifier = classifier;
	}

	@Override
	public Set<String> getExcludeTraceAttributes() {
		return excludeTraceAttributes;
	}

	public void setExcludeTraceAttributes(Set<String> excludeTraceAttributes) {
		this.excludeTraceAttributes = excludeTraceAttributes;
	}

	@Override
	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Override
	public FeatureFactory getFeatureFactory() {
		return featureFactory;
	}

	public void setFeatureFactory(FeatureFactory featureFactory) {
		this.featureFactory = featureFactory;
	}

	@Override
	public int getMaxFeatureSetSize() {
		return maxFeatureSetSize;
	}

	public void setMaxFeatureSetSize(int maxFeatureSetSize) {
		this.maxFeatureSetSize = maxFeatureSetSize;
	}

	@Override
	public double getDiversifyThreshold() {
		return diversifyThreshold;
	}

	public void setDiversifyThreshold(double diversify) {
		this.diversifyThreshold = diversify;
	}

	public int getNumberOfThreads() {
		return threads;
	}

	public void setNumberOfThreads(int threads) {
		this.threads = threads;
	}

}