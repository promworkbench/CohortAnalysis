package org.processmining.cohortanalysis.feature.set;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.ArrayUtils;
import org.processmining.cohortanalysis.feature.AttributeFeatureMap;

/**
 * An iterator over feature sets. Cloned iterators influence each other and each
 * can be used in its own thread to let all threads together iterate over all
 * feature sets.
 * 
 * @author sander
 *
 */
public class FeatureSetIteratorImpl implements FeatureSetIterator {

	protected AtomicLong globalFeatureSetNumber = new AtomicLong(0);
	protected long localFeatureSetNumber = 0;
	protected int[] current;
	protected AttributeFeatureMap attributeMap;
	protected final int maxFeatureSetSize;

	public FeatureSetIteratorImpl(AttributeFeatureMap attributeMap, int maxFeatureSetSize) {
		current = FeatureSet.getEmpty(attributeMap.getNumberOfAttributes());
		this.attributeMap = attributeMap;
		this.maxFeatureSetSize = maxFeatureSetSize;
	}

	public int[] next() {
		long newLocalFeatureSetNumber = globalFeatureSetNumber.incrementAndGet();

		while (localFeatureSetNumber < newLocalFeatureSetNumber) {
			FeatureSet.next(attributeMap, current, maxFeatureSetSize);
			localFeatureSetNumber++;
		}

		if (current == null || current[0] == -2) {
			return null;
		}

		//System.out.println(localFeatureSetNumber + ArrayUtils.toString(current));

		return current;
	}

	public boolean hasNext() {
		return current != null && current.length > 0 && current[0] != -2;
	}

	public FeatureSetIteratorImpl clone() {
		try {
			FeatureSetIteratorImpl result = (FeatureSetIteratorImpl) super.clone();
			result.globalFeatureSetNumber = globalFeatureSetNumber;
			result.localFeatureSetNumber = localFeatureSetNumber;
			result.current = ArrayUtils.clone(current);
			result.attributeMap = attributeMap;
			return result;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}