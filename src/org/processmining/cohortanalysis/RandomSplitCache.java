package org.processmining.cohortanalysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XLogImpl;
import org.processmining.cohortanalysis.parameters.CohortAnalysisParameters;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogLog;
import org.processmining.earthmoversstochasticconformancechecking.plugins.EarthMoversStochasticConformancePlugin;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignmentsLogLog;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.jobList.JobList;
import org.processmining.plugins.InductiveMiner.jobList.JobListConcurrent;
import org.python.google.common.util.concurrent.AtomicDouble;

/**
 * Idea: keep random partitions for a number of buckets.
 * 
 * @author sander
 *
 */
public class RandomSplitCache {

	private final XLog fullLog;
	private final CohortAnalysisParameters parameters;
	private final EMSCParametersLogLog emscParameters;

	private final static int buckets = 10;

	private final List<Future<Double>> cache;
	private final AtomicBoolean[] cacheComputing;

	public RandomSplitCache(XLog fullLog, CohortAnalysisParameters parameters, EMSCParametersLogLog emscParameters) {
		this.fullLog = fullLog;
		this.parameters = parameters;
		this.emscParameters = emscParameters;

		cache = new ArrayList<>();
		cacheComputing = new AtomicBoolean[buckets];
		for (int i = 0; i < buckets; i++) {
			cache.add(null);
			cacheComputing[i] = new AtomicBoolean(false);
		}
	}

	/**
	 * 
	 * @param subLog
	 * @param canceller
	 * @param measuresExecutor
	 * @return the random split value, or -Double.max if we got cancelled, or
	 *         the computation failed.
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public double getRandomSplitDifference(XLog subLog, final ProMCanceller canceller, ExecutorService measuresExecutor,
			final ExecutorService randomMeasuresExecutor) throws ExecutionException, InterruptedException {
		int subLogSize = Math.min(subLog.size(), fullLog.size() - subLog.size());
		int cacheIndex = getCacheIndex(subLogSize);
		final int cacheSize = getLogSize(cacheIndex);

		while (cache.get(cacheIndex) == null) {
			//the value is not available yet

			if (canceller.isCancelled()) {
				return -Double.MAX_VALUE;
			}

			//see if we need to update it
			boolean weAreUpdating = cacheComputing[cacheIndex].compareAndSet(false, true);
			if (weAreUpdating) {
				//we got the computation, perform it
				cache.set(cacheIndex, measuresExecutor.submit(new Callable<Double>() {
					public Double call() throws Exception {
						return compute(canceller, cacheSize, randomMeasuresExecutor);
					}
				}));
			} else {
				//the future was not available yet, but it is being set-up by another thread.
				//wait and try again
				Thread.sleep(100);
			}
		}

		//the future should be available here
		Future<Double> future = cache.get(cacheIndex);

		return future.get();
	}

	public int getCacheIndex(int logSize) {
		int maxSize = (int) Math.ceil(fullLog.size() / 2.0);
		assert (logSize <= maxSize);
		int bucket = (int) Math.min(Math.floor((logSize / (maxSize * 1.0)) * (buckets + 1)), buckets - 1);
		return bucket;
	}

	public int getLogSize(int cacheIndex) {
		return (int) ((fullLog.size() / 2.0) * ((cacheIndex + 0.5) / buckets));
	}

	private double compute(final ProMCanceller canceller, final int subLogSize,
			final ExecutorService randomMeasuresExecutor) throws ExecutionException {
		JobList pool = new JobListConcurrent(randomMeasuresExecutor);
		final AtomicDouble sum = new AtomicDouble(0);
		final AtomicBoolean error = new AtomicBoolean(false);
		for (int run = 0; run < parameters.getScaleRandom(); run++) {
			final int run2 = run;
			pool.addJob(new Runnable() {
				public void run() {
					Pair<XLog, XLog> t = splitLogRandom(fullLog, subLogSize / (fullLog.size() * 1.0), run2);

					CohortAnalysis.debug(parameters,
							"  random split " + t.getA().size() + " vs " + t.getB().size() + ", planned " + subLogSize);
					try {
						StochasticTraceAlignmentsLogLog q = EarthMoversStochasticConformancePlugin
								.measureLogLog(t.getA(), t.getB(), emscParameters, canceller);

						if (q == null || canceller.isCancelled()) {
							error.set(true);
							return;
						}

						sum.addAndGet(q.getSimilarity());

						CohortAnalysis.debug(parameters, "  done random split " + t.getA().size() + " vs "
								+ t.getB().size() + ", planned " + subLogSize);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}

		pool.join();

		if (error.get()) {
			return -Double.MAX_VALUE;
		}
		return sum.get() / parameters.getScaleRandom();
	}

	public static Pair<XLog, XLog> splitLogRandom(XLog log, double threshold, int randomSeed) {
		Random random = new Random(randomSeed);
		XLog log1 = new XLogImpl(log.getAttributes());
		XLog log2 = new XLogImpl(log.getAttributes());

		for (XTrace trace : log) {
			if (random.nextDouble() < threshold) {
				log1.add(trace);
			} else {
				log2.add(trace);
			}
		}

		return Pair.of(log1, log2);
	}
}