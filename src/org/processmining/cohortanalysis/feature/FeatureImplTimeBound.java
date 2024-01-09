package org.processmining.cohortanalysis.feature;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;

import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;

public class FeatureImplTimeBound extends FeatureAbstract {

	private final long bound;

	public FeatureImplTimeBound(Attribute attribute, long bound) {
		super(attribute);
		assert !attribute.isVirtual();
		this.bound = bound;
	}

	@Override
	public String getDescriptionSelector() {
		return "&lt; " + FeatureImplTimeBound.displayTime(bound);
	}

	@Override
	public boolean includes(XTrace trace) {
		long time = attribute.getTime(trace);
		if (time == Long.MIN_VALUE) {
			return false;
		}
		return time <= bound;
	}

	public static String displayTime(double ms) {
		double seconds = ms / 1000;
		double minutes = seconds / 60;
		double hours = minutes / 60;
		double days = hours / 24;

		return ms + "ms (" + days + " days)";
	}

	public static TLongList gatherValues(XLog log, Attribute attribute) {
		TLongList result = new TLongArrayList();
		for (XTrace trace : log) {
			long time = attribute.getTime(trace);
			if (time != Long.MIN_VALUE) {
				result.add(time);
			}
		}
		return result;
	}

	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (bound ^ (bound >>> 32));
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
		FeatureImplTimeBound other = (FeatureImplTimeBound) obj;
		if (bound != other.bound) {
			return false;
		}
		return true;
	}

}
