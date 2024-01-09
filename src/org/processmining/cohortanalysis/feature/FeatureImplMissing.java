package org.processmining.cohortanalysis.feature;

import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public class FeatureImplMissing extends FeatureAbstract {

	private final int value = 0;

	public FeatureImplMissing(Attribute attribute) {
		super(attribute);
		assert !attribute.isVirtual();
	}

	@Override
	public String getDescriptionSelector() {
		return "is missing";
	}

	@Override
	public boolean includes(XTrace trace) {
		return !trace.getAttributes().containsKey(attribute.getName());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
		return result;
	}

}