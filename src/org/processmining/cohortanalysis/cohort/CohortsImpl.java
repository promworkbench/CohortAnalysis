package org.processmining.cohortanalysis.cohort;

import java.lang.ref.WeakReference;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.deckfour.xes.model.XLog;

public class CohortsImpl extends CopyOnWriteArrayList<Cohort> implements Cohorts {

	private AtomicInteger minimumCohortSize;

	private WeakReference<XLog> log;

	public CohortsImpl(XLog log, int minimumCohortSize) {
		this.minimumCohortSize = new AtomicInteger(minimumCohortSize);
		this.log = new WeakReference<>(log);
	}

	private static final long serialVersionUID = -3729850895994629564L;

	public int getMinimumCohortSize() {
		return minimumCohortSize.get();
	}

	public void setMinimumCohortSize(int minimumCohortSize) {
		this.minimumCohortSize.set(minimumCohortSize);
	}

	public WeakReference<XLog> getLog() {
		return log;
	}

}