package org.processmining.cohortanalysis.feature;

import java.util.Arrays;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public class FeatureImplNumberOfEvents extends FeatureAbstract {

	private final double bound;

	public FeatureImplNumberOfEvents(Attribute attribute, double bound) {
		super(attribute);
		this.bound = bound;
	}

	public String getDescriptionSelector() {
		return "&lt; " + bound;
	}

	public boolean includes(XTrace trace) {
		return trace.size() < bound;
	}

	public static double getMedianNumberOfEvents(XLog log) {
		long[] values = new long[log.size()];
		for (int trace = 0; trace < log.size(); trace++) {
			values[trace] = log.get(trace).size();
		}

		Arrays.sort(values);
		if (values.length % 2 == 1) {
			return values[values.length / 2];
		} else {
			return (values[values.length / 2] + values[values.length / 2 + 1]) / 2.0;
		}
	}

	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(bound);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FeatureImplNumberOfEvents other = (FeatureImplNumberOfEvents) obj;
		if (Double.doubleToLongBits(bound) != Double.doubleToLongBits(other.bound)) {
			return false;
		}
		return true;
	}
}