package org.processmining.cohortanalysis.feature;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

public class FeatureImplNumericBounded extends FeatureAbstract {

	private final double bound;

	public FeatureImplNumericBounded(Attribute attribute, double bound) {
		super(attribute);
		assert !attribute.isVirtual();
		this.bound = bound;
	}

	@Override
	public String getDescriptionSelector() {
		return "&lt; " + bound;
	}

	@Override
	public boolean includes(XTrace trace) {
		double value = attribute.getNumeric(trace);
		if (value == -Double.MAX_VALUE) {
			return false;
		}
		return value <= bound;
	}

	public static TDoubleList gatherValues(XLog log, Attribute attribute) {
		TDoubleList result = new TDoubleArrayList();
		for (XTrace trace : log) {
			double value = attribute.getNumeric(trace);
			if (value != -Double.MAX_VALUE) {
				result.add(value);
			}
		}
		return result;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(bound);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeatureImplNumericBounded other = (FeatureImplNumericBounded) obj;
		if (Double.doubleToLongBits(bound) != Double.doubleToLongBits(other.bound))
			return false;
		return true;
	}

}