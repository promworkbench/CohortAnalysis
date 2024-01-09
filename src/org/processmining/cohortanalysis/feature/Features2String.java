package org.processmining.cohortanalysis.feature;

import java.util.Iterator;

public class Features2String {
	public static String toString(Iterable<Feature> features) {
		StringBuilder result = new StringBuilder();

		for (Iterator<Feature> it = features.iterator(); it.hasNext();) {
			Feature feature = it.next();
			result.append(feature.getDescriptionField());
			result.append(" ");
			result.append(feature.getDescriptionSelector());
			if (it.hasNext()) {
				result.append(", ");
			}
		}

		return result.toString();
	}
}
