package org.processmining.cohortanalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.ArrayUtils;
import org.jscience.mathematics.number.LargeInteger;
import org.processmining.cohortanalysis.feature.AttributeFeatureMap;
import org.processmining.cohortanalysis.feature.Feature;
import org.processmining.cohortanalysis.feature.set.FeatureSet;
import org.processmining.cohortanalysis.feature.set.FeatureSetIterator;
import org.processmining.cohortanalysis.feature.set.FeatureSetIteratorImpl;
import org.processmining.framework.plugin.ProMCanceller;

import gnu.trove.map.hash.TCustomHashMap;
import gnu.trove.strategy.HashingStrategy;

/**
 * Keeps track of a lattice of feature sets. Stores only whether they have been
 * pruned or not.
 * 
 * @author sander
 *
 */
public class LatticeIntArray {
	private Map<int[], AtomicBoolean> featureSetIndex2pruned;
	private final AtomicInteger pruned;
	private final AttributeFeatureMap attributeMap;
	private final int maxFeatureSetSize;

	/**
	 * 
	 * @param attributeMap
	 * @param maxSize
	 *            is ignored for now
	 * @param canceller
	 */
	public LatticeIntArray(AttributeFeatureMap attributeMap, int maxFeatureSetSize, ProMCanceller canceller) {
		this.attributeMap = attributeMap;
		this.pruned = new AtomicInteger(0);
		this.maxFeatureSetSize = maxFeatureSetSize;

		featureSetIndex2pruned = new TCustomHashMap<int[], AtomicBoolean>(new HashingStrategy<int[]>() {
			private static final long serialVersionUID = -2472434954141689664L;

			public int computeHashCode(int[] object) {
				return Arrays.hashCode(object);
			}

			public boolean equals(int[] o1, int[] o2) {
				return Arrays.equals(o1, o2);
			}
		});

		initialiseRecursive(canceller);
	}

	/**
	 * Fill the map with AtomicBooleans.
	 * 
	 * @param attributeMap2
	 * 
	 * @param featureSetIndex
	 * @param canceller
	 */
	private void initialiseRecursive(ProMCanceller canceller) {
		if (canceller.isCancelled()) {
			return;
		}

		for (FeatureSetIterator it = iterator(); it.hasNext();) {
			int[] featureSetIndex = it.next();
			featureSetIndex2pruned.put(ArrayUtils.clone(featureSetIndex), new AtomicBoolean(false));

			if (canceller.isCancelled()) {
				return;
			}
		}
	}

	/**
	 * Prunes all (grand)children of the given feature set, and the set itself,
	 * up to length of the current feature set + sizeLeft. Thread safe.
	 * 
	 * @param featureSetIndex
	 */
	public void pruneRecursive(int[] featureSetIndex) {
		AtomicBoolean isPruned = featureSetIndex2pruned.get(featureSetIndex);
		//System.out.println(" prune " + toString(featureSetIndex));
		if (isPruned != null) {
			boolean changed = isPruned.compareAndSet(false, true);
			if (changed) {
				pruned.incrementAndGet();

				if (FeatureSet.size(featureSetIndex) < maxFeatureSetSize) {
					//recurse
					for (int attributeIndex = 0; attributeIndex < attributeMap
							.getNumberOfAttributes(); attributeIndex++) {
						if (!FeatureSet.containsAttribute(featureSetIndex, attributeIndex)) {
							//featureSetIndex does not consider attribute attributeIndex, add it and recurse

							//add each feature and recurse
							for (int newFeatureIndex = 1; newFeatureIndex <= attributeMap
									.getNumberOfFeatures(attributeIndex); newFeatureIndex++) {
								int[] newFeatureSetIndex = FeatureSet.add(featureSetIndex, attributeIndex,
										newFeatureIndex);
								pruneRecursive(newFeatureSetIndex);
							}
						}
					}
				}
			}
		}
	}

	public FeatureSetIterator iterator() {
		return new FeatureSetIteratorImpl(attributeMap, maxFeatureSetSize);
	}

	protected String toString(int[] featureSetIndex) {
		StringBuilder result = new StringBuilder();
		result.append("[");

		for (int attributeIndex = 0; attributeIndex < attributeMap.getNumberOfAttributes(); attributeIndex++) {
			if (FeatureSet.containsAttribute(featureSetIndex, attributeIndex)) {
				Feature feature = attributeMap.getFeature(attributeIndex,
						FeatureSet.getFeature(featureSetIndex, attributeIndex));
				result.append(feature.getDescriptionField() + " " + feature.getDescriptionSelector() + ", ");
			} else {
				//featureSetIndex does not consider attribute attributeIndex
			}
		}
		result.append("]");
		return result.toString();
	}

	public List<Feature> featureSetIndex2featureSet(int[] featureSetIndex) {
		List<Feature> result = new ArrayList<>();
		for (int attributeIndex = 0; attributeIndex < attributeMap.getNumberOfAttributes(); attributeIndex++) {
			if (FeatureSet.containsAttribute(featureSetIndex, attributeIndex)) {
				result.add(attributeMap.getFeature(attributeIndex,
						FeatureSet.getFeature(featureSetIndex, attributeIndex)));
			}
		}
		return result;
	}

	public boolean isPruned(int[] featureSetIndex) {
		AtomicBoolean isPruned = featureSetIndex2pruned.get(featureSetIndex);
		return isPruned == null || isPruned.get();
	}

	@Deprecated
	/**
	 * Only for testing purposes.
	 * 
	 * @return
	 */
	public int size() {
		return featureSetIndex2pruned.size();
	}

	public static LargeInteger pow(LargeInteger value, int exp) {
		if (exp == 0) {
			return LargeInteger.ONE;
		}
		return value.pow(exp);
	}
}