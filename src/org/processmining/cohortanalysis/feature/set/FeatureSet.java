package org.processmining.cohortanalysis.feature.set;

import org.processmining.cohortanalysis.feature.AttributeFeatureMap;
import org.python.bouncycastle.util.Arrays;

public class FeatureSet {
	public static int[] getEmpty(int numberOfAttributes) {
		int[] result = new int[numberOfAttributes];
		Arrays.fill(result, -1);
		return result;
	}

	public static long getPossibilities(AttributeFeatureMap map) {
		long result = 1;
		for (int attribute = 0; attribute < map.getNumberOfAttributes(); attribute++) {
			result *= map.getNumberOfFeatures(attribute) + 1;
		}
		return result;
	}

	public static boolean containsAttribute(int[] featureSetIndex, int attribute) {
		return featureSetIndex[attribute] != -1;
	}

	public static int size(int[] featureSetIndex) {
		int result = 0;
		for (int attributeIndex = 0; attributeIndex < featureSetIndex.length; attributeIndex++) {
			if (featureSetIndex[attributeIndex] > -1) {
				result++;
			}
		}
		return result;
	}

	public static int[] clone(int[] featureSetIndex) {
		return Arrays.clone(featureSetIndex);
	}

	public static int[] add(int[] featureSetIndex, int attribute, int feature) {
		int[] result = clone(featureSetIndex);
		result[attribute] = feature;
		return result;
	}

	/**
	 * Traverse the feature sets (in place). Returns -2 in every cell when all
	 * have been traversed.
	 * 
	 * @param map
	 * @param current
	 * @param maxFeatureSetSize
	 */
	public static void next(AttributeFeatureMap map, int[] current, int maxFeatureSetSize) {
		if (current == null || current[0] == -2) {
			return;
		}
		int numberOfAttributes = map.getNumberOfAttributes();

		for (int attributeIndex = 0; attributeIndex < numberOfAttributes; attributeIndex++) {
			int feature = getFeature(current, attributeIndex);
			if (feature < map.getNumberOfFeatures(attributeIndex) - 1 && //we can still add a feature in this attribute 
					(size(current) < maxFeatureSetSize || //the maximum of attributes has not been reached
							(size(current) == maxFeatureSetSize && current[attributeIndex] != -1))) { //we are at the maximum but the current attribute is already in
				//increase the featureIndex by one.
				//that is, add attributeBase 
				//featureSetIndex considers attribute attributeIndex
				current[attributeIndex]++;
				return;
			} else {
				//move up one slot
				current[attributeIndex] = -1;
			}
		}

		//running over: done
		Arrays.fill(current, -2);
		return;
	}

	public static int getFeature(int[] featureSetIndex, int attributeIndex) {
		return featureSetIndex[attributeIndex];
	}
}