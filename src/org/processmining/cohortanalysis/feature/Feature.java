package org.processmining.cohortanalysis.feature;

import org.deckfour.xes.model.XTrace;

public interface Feature {

	public String getDescriptionField();

	public String getDescriptionSelector();

	public boolean includes(XTrace trace);

}