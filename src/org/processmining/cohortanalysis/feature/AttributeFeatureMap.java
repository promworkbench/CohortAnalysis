package org.processmining.cohortanalysis.feature;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.processmining.plugins.inductiveminer2.attributes.Attribute;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class AttributeFeatureMap {

	private final Attribute[] index2attribute;
	private final TObjectIntMap<Attribute> attribute2index;
	private final List<List<Feature>> attribute2features;

	public AttributeFeatureMap(Collection<Attribute> attributes) {
		index2attribute = new Attribute[attributes.size()];
		attribute2index = new TObjectIntHashMap<Attribute>(attributes.size(), 0.5f, -1);
		attribute2features = new ArrayList<>();
		int i = 0;
		for (Iterator<Attribute> it = attributes.iterator(); it.hasNext();) {
			index2attribute[i] = it.next();
			attribute2index.put(index2attribute[i], i);
			attribute2features.add(new ArrayList<Feature>());
			i++;
		}
	}

	public int attribute2index(Attribute attribute) {
		return attribute2index.get(attribute);
	}

	public Attribute index2attribute(int attributeIndex) {
		return index2attribute[attributeIndex];
	}

	public void addFeature(FeatureAbstract feature) {
		Attribute attribute = feature.getAttribute();
		int attributeIndex = attribute2index.get(attribute);
		attribute2features.get(attributeIndex).add(feature);
	}

	public int getNumberOfFeatures(int attributeIndex) {
		return attribute2features.get(attributeIndex).size();
	}

	public int getMaxNumberOfFeatures() {
		int max = Integer.MIN_VALUE;
		for (List<Feature> features : attribute2features) {
			max = Math.max(max, features.size());
		}
		return max;
	}

	public BigInteger getTotalNumberOfFeatureSets() {
		BigInteger product = BigInteger.ONE;
		for (List<Feature> features : attribute2features) {
			product = product.multiply(BigInteger.valueOf(features.size() + 1));
		}
		return product.subtract(BigInteger.ONE);
	}

	public int getNumberOfAttributes() {
		return index2attribute.length;
	}

	/**
	 * 
	 * @param attributeIndex
	 *            indexed from 0
	 * @param featureIndex
	 *            indexed from 0
	 * @return
	 */
	public Feature getFeature(int attributeIndex, int featureIndex) {
		return attribute2features.get(attributeIndex).get(featureIndex);
	}

	public String toString() {
		StringBuilder result = new StringBuilder();

		for (int attributeIndex = 0; attributeIndex < getNumberOfAttributes(); attributeIndex++) {
			for (int featureIndex = 0; featureIndex < getNumberOfFeatures(attributeIndex); featureIndex++) {
				Feature feature = getFeature(attributeIndex, featureIndex);
				result.append(feature.getDescriptionField());
				result.append(" ");
				result.append(feature.getDescriptionSelector());
				result.append(", ");
			}
			result.append("\n");
		}

		return result.toString();
	}
}