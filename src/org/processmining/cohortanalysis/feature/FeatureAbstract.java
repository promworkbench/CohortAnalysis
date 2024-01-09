package org.processmining.cohortanalysis.feature;

import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public abstract class FeatureAbstract implements Feature {

	protected Attribute attribute;

	public FeatureAbstract(Attribute attribute) {
		this.attribute = attribute;
	}

	public String getDescriptionField() {
		return attribute.getName();
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attribute == null) ? 0 : attribute.getName().hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeatureAbstract other = (FeatureAbstract) obj;
		if (attribute == null) {
			if (other.attribute != null)
				return false;
		} else if (!attribute.getName().equals(other.attribute.getName()))
			return false;
		return true;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public String toString() {
		return getDescriptionField() + " " + getDescriptionSelector();
	}

}