package org.processmining.cohortanalysis.feature;

import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public class FeatureImplLiteral extends FeatureAbstract {

	private final String value;

	public FeatureImplLiteral(Attribute attribute, String value) {
		super(attribute);
		assert !attribute.isVirtual();
		this.value = value;
	}

	public String getDescriptionSelector() {
		return "= " + value;
	}

	public boolean includes(XTrace trace) {
		String value = attribute.getLiteral(trace);
		return value != null && this.value.equals(value);
	}

	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		FeatureImplLiteral other = (FeatureImplLiteral) obj;
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

}
