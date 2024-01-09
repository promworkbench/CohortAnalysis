package org.processmining.cohortanalysis.cohort;

import java.lang.ref.WeakReference;
import java.util.List;

import org.deckfour.xes.model.XLog;

public interface Cohorts extends List<Cohort> {

	public int getMinimumCohortSize();

	public WeakReference<XLog> getLog();

}