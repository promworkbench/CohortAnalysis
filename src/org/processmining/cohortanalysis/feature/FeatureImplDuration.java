package org.processmining.cohortanalysis.feature;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;

import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;

public class FeatureImplDuration extends FeatureAbstract {

	private final double bound;

	public FeatureImplDuration(Attribute attribute, double bound) {
		super(attribute);
		this.bound = bound;
	}

	public String getDescriptionSelector() {
		return "&lt; " + FeatureImplTimeBound.displayTime(bound);
	}

	@Override
	public boolean includes(XTrace trace) {
		long time = attribute.getDuration(trace);
		if (time == Long.MIN_VALUE) {
			return false;
		}
		return time <= bound;
	}

	public static TLongList gatherValues(XLog log, Attribute attribute) {
		TLongList result = new TLongArrayList();
		for (XTrace trace : log) {
			long time = attribute.getDuration(trace);
			if (time != Long.MIN_VALUE) {
				result.add(time);
			}
		}
		return result;
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
		FeatureImplDuration other = (FeatureImplDuration) obj;
		if (Double.doubleToLongBits(bound) != Double.doubleToLongBits(other.bound)) {
			return false;
		}
		return true;
	}

}
