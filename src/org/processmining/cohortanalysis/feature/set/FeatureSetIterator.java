package org.processmining.cohortanalysis.feature.set;

public interface FeatureSetIterator extends Cloneable {

	/**
	 * May return null if the iterator ran out by another thread. Do not edit
	 * the returned array value.
	 */
	public int[] next();

	/**
	 * 
	 * @return true when there -might- still be another feature set. If false,
	 *         no other feature set will be returned.
	 */
	public boolean hasNext();

	/**
	 * Creates a dependable copy: iterates over the same things.
	 */
	public FeatureSetIterator clone();

}