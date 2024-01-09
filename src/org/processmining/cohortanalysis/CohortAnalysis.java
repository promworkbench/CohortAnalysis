package org.processmining.cohortanalysis;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XLogImpl;
import org.processmining.cohortanalysis.cohort.Cohort;
import org.processmining.cohortanalysis.cohort.CohortImpl;
import org.processmining.cohortanalysis.cohort.Cohorts;
import org.processmining.cohortanalysis.cohort.CohortsImpl;
import org.processmining.cohortanalysis.feature.AttributeFeatureMap;
import org.processmining.cohortanalysis.feature.Feature;
import org.processmining.cohortanalysis.feature.Features2String;
import org.processmining.cohortanalysis.feature.set.FeatureSetIterator;
import org.processmining.cohortanalysis.parameters.CohortAnalysisParameters;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogLog;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogLogAbstract;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogLogDefault;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.jobList.JobList;
import org.processmining.plugins.InductiveMiner.jobList.JobListConcurrent;

public class CohortAnalysis {

	public static CohortsImpl compute(final XLog log, final CohortAnalysisParameters parameters,
			final ProMCanceller canceller) throws ExecutionException {
		//initialise comparison parameters
		final EMSCParametersLogLogAbstract emscParameters = new EMSCParametersLogLogDefault();
		emscParameters.setComputeStochasticTraceAlignments(false);
		emscParameters.setClassifierA(parameters.getClassifier());
		emscParameters.setClassifierB(parameters.getClassifier());

		//initialise result
		final int minSize = getMinSubLogSize(log.size(), parameters);
		final CohortsImpl result = new CohortsImpl(log, minSize);
		final RandomSplitCache cache = new RandomSplitCache(log, parameters, emscParameters);

		//gather attributes
		Set<String> excludeTraceAttributes = parameters.getExcludeTraceAttributes();
		AttributeFeatureMap attributeMap = parameters.getFeatureFactory().getFeatures(log, excludeTraceAttributes,
				parameters.getSizeThreshold(), canceller);

		if (attributeMap == null || canceller.isCancelled()) {
			return null;
		}

		//make lattice
		debug(parameters, "initialise lattice");
		final LatticeIntArray lattice = new LatticeIntArray(attributeMap, parameters.getMaxFeatureSetSize(), canceller);

		if (lattice == null || canceller.isCancelled()) {
			return null;
		}

		//set up threads
		final AtomicBoolean error = new AtomicBoolean(false);
		{
			ExecutorService cohortsExecutor = Executors.newFixedThreadPool(parameters.getNumberOfThreads());
			final ExecutorService measuresExecutor = Executors.newFixedThreadPool(parameters.getNumberOfThreads());
			final ExecutorService randomMeasuresExecutor = Executors
					.newFixedThreadPool(parameters.getNumberOfThreads());
			JobList pool = new JobListConcurrent(cohortsExecutor);
			final FeatureSetIterator superIterator = lattice.iterator();

			for (int t = 0; t < parameters.getNumberOfThreads(); t++) {
				pool.addJob(new Runnable() {
					public void run() {
						FeatureSetIterator threadIterator = superIterator.clone();
						try {
							workerThread(log, threadIterator, lattice, result, parameters, canceller, cache,
									emscParameters, measuresExecutor, randomMeasuresExecutor);
						} catch (InterruptedException e) {
							e.printStackTrace();
							error.set(true);
						} catch (ExecutionException e) {
							e.printStackTrace();
							error.set(true);
						}
					}
				});
			}

			pool.join();
			cohortsExecutor.shutdown();
			measuresExecutor.shutdown();
			randomMeasuresExecutor.shutdown();
		}

		if (error.get() || canceller.isCancelled()) {
			return null;
		}

		Collections.sort(result);
		return result;
	}

	public static int getMinSubLogSize(int logSize, CohortAnalysisParameters parameters) {
		return Math.max(1, (int) Math.floor(parameters.getSizeThreshold() * (logSize)));
	}

	protected static void workerThread(XLog log, FeatureSetIterator it, LatticeIntArray lattice, Cohorts result,
			CohortAnalysisParameters parameters, ProMCanceller canceller, RandomSplitCache cache,
			EMSCParametersLogLog emscParameters, ExecutorService measuresExecutor,
			ExecutorService randomMeasuresExecutor) throws InterruptedException, ExecutionException {
		while (it.hasNext()) {

			if (canceller.isCancelled()) {
				return;
			}

			int[] featureSetIndex = it.next();
			if (featureSetIndex != null && !lattice.isPruned(featureSetIndex)) {
				Cohort cohort = processFeatureSet(log, featureSetIndex, lattice, result, parameters, cache, canceller,
						emscParameters, measuresExecutor, randomMeasuresExecutor);

				if (cohort != null) {
					result.add(cohort);
				}
			}
		}
	}

	protected static Cohort processFeatureSet(XLog log, int[] featureSetIndex, LatticeIntArray lattice, Cohorts result,
			CohortAnalysisParameters parameters, RandomSplitCache cache, ProMCanceller canceller,
			EMSCParametersLogLog emscParameters, ExecutorService measuresExecutor,
			ExecutorService randomMeasuresExecutor) throws InterruptedException, ExecutionException {
		List<Feature> featureSet = lattice.featureSetIndex2featureSet(featureSetIndex);

		//make sub-logs
		Pair<XLog, XLog> logPair = splitLog(log, featureSet);

		if (logPair == null || canceller.isCancelled()) {
			return null;
		}

		XLog log1 = logPair.getA();
		XLog log2 = logPair.getB();

		//check sizes
		if (!checkSizes(log1, log2, parameters)) {
			//This split results in too small sub-logs. Mark as pruned.
			debug(parameters, Features2String.toString(featureSet) + ": sub-log is too small; skipping (" + log1.size()
					+ " vs " + log2.size() + ")");

			lattice.pruneRecursive(featureSetIndex);
			return null;
		}

		if (canceller.isCancelled()) {
			return null;
		}

		debug(parameters, Features2String.toString(featureSet) + ": starting distance measure (" + log1.size() + " vs "
				+ log2.size() + ")");
		double similarity = MeasureAndScale.getSimilarity(log, log1, log2, cache, emscParameters, parameters,
				measuresExecutor, randomMeasuresExecutor, canceller);

		if (similarity == -Double.MAX_VALUE || canceller.isCancelled()) {
			return null;
		}

		double distance = 1 - similarity;

		//add to result
		return new CohortImpl(featureSet, distance, log1.size());
	}

	private static boolean checkSizes(XLog log1, XLog log2, CohortAnalysisParameters parameters) {
		int minSize = getMinSubLogSize(log1.size() + log2.size(), parameters);
		if (log1.size() < minSize) {

			return false;
		}
		if (log2.size() < minSize) {
			return false;
		}
		return true;
	}

	private static Pair<XLog, XLog> splitLog(XLog log, List<Feature> featureSet) {
		XLog log1 = new XLogImpl(log.getAttributes());
		XLog log2 = new XLogImpl(log.getAttributes());

		for (XTrace trace : log) {
			if (includes(trace, featureSet)) {
				log1.add(trace);
			} else {
				log2.add(trace);
			}
		}

		return Pair.of(log1, log2);
	}

	private static boolean includes(XTrace trace, Iterable<Feature> features) {
		for (Feature feature : features) {
			if (!feature.includes(trace)) {
				return false;
			}
		}
		return true;
	}

	public static void debug(CohortAnalysisParameters parameters, Object output) {
		if (parameters.isDebug()) {
			System.out.println(output.toString());
		}
	}
}
