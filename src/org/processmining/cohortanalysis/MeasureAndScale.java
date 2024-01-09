package org.processmining.cohortanalysis;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.deckfour.xes.model.XLog;
import org.processmining.cohortanalysis.parameters.CohortAnalysisParameters;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogLog;
import org.processmining.earthmoversstochasticconformancechecking.plugins.EarthMoversStochasticConformancePlugin;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignmentsLogLog;
import org.processmining.framework.plugin.ProMCanceller;

public class MeasureAndScale {
	public static double getSimilarity(XLog fullLog, final XLog log1, final XLog log2, RandomSplitCache cache,
			final EMSCParametersLogLog emscParameters, final CohortAnalysisParameters parameters, ExecutorService measuresExecutor,
			ExecutorService randomMeasuresExecutor, final ProMCanceller canceller)
			throws InterruptedException, ExecutionException {

		Future<Double> similarityJob = measuresExecutor.submit(new Callable<Double>() {
			public Double call() throws Exception {
				//compute distance
				try {
					CohortAnalysis.debug(parameters, " compute distance (" + log1.size() + " vs " + log2.size() + ")");

					StochasticTraceAlignmentsLogLog p = EarthMoversStochasticConformancePlugin.measureLogLog(log1, log2,
							emscParameters, canceller);
					if (p == null || canceller.isCancelled()) {
						return -Double.MAX_VALUE;
					}

					CohortAnalysis.debug(parameters,
							" done compute distance (" + log1.size() + " vs " + log2.size() + ")");

					return p.getSimilarity();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return -Double.MAX_VALUE;
				}
			}
		});

		double randomAverage = cache.getRandomSplitDifference(log1, canceller, measuresExecutor,
				randomMeasuresExecutor);

		if (randomAverage == -Double.MAX_VALUE || canceller.isCancelled()) {
			return -Double.MAX_VALUE;
		}

		double similarity = similarityJob.get();

		if (similarity == -Double.MAX_VALUE || canceller.isCancelled()) {
			return -Double.MAX_VALUE;
		}

		double scaled = similarity / (randomAverage);

		return scaled;
	}
}