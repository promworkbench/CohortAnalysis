package org.processmining.cohortanalysis.parameters;

import java.util.Set;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.cohortanalysis.feature.FeatureFactory;

public interface CohortAnalysisParameters {

	/**
	 * The size that sub-logs should minimally have in order to be considered
	 * (as a fraction of the log's size).
	 * 
	 * @return
	 */
	public double getSizeThreshold();

	/**
	 * The number of times a random sample is performed and measured. The
	 * results are scaled accordingly.
	 * 
	 * @return the number of times, or <= 0 if no randomisation is to be
	 *         performed.
	 */
	public int getScaleRandom();

	public XEventClassifier getClassifier();

	public Set<String> getExcludeTraceAttributes();

	public boolean isDebug();

	public FeatureFactory getFeatureFactory();

	public int getMaxFeatureSetSize();

	/**
	 * When diversifying the results, only consider cohorts with a distance at
	 * least x * the highest distance. If 1, no diversification is performed.
	 * 
	 * @return
	 */
	public double getDiversifyThreshold();

	/**
	 * The number of threads that is to be used. Return -1 for a default value,
	 * based on the available cpu cores. Three thread pools of this size will be
	 * created, however not all will be in use all the time.
	 * 
	 * @return
	 */
	public int getNumberOfThreads();

}