package org.processmining.cohortanalysis.feature;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.TLongList;

public class FeatureFactoryBasic extends FeatureFactoryAbstract {

	protected void processAttribute(Attribute attribute, XLog log, AttributeFeatureMap result,
			long minimumNumberOfTraces, long maximumNumberOfTraces) {
		if (attribute.isLiteral()) {
			processLiteral(attribute, log, result, minimumNumberOfTraces, maximumNumberOfTraces);
		} else if (attribute.isNumeric()) {
			//split on the median
			TDoubleList values = FeatureImplNumericBounded.gatherValues(log, attribute);
			double median = median(values.toArray());

			addFeature(result, new FeatureImplNumericBounded(attribute, median), log, minimumNumberOfTraces,
					maximumNumberOfTraces);

			if (!attribute.isVirtual()) {
				addFeature(result, new FeatureImplMissing(attribute), log, minimumNumberOfTraces,
						maximumNumberOfTraces);
			}
		} else if (attribute.isTime()) {
			//split on the median
			TLongList values = FeatureImplTimeBound.gatherValues(log, attribute);
			long median = (long) median(values.toArray());

			addFeature(result, new FeatureImplTimeBound(attribute, median), log, minimumNumberOfTraces,
					maximumNumberOfTraces);

			if (!attribute.isVirtual()) {
				addFeature(result, new FeatureImplMissing(attribute), log, minimumNumberOfTraces,
						maximumNumberOfTraces);
			}
		} else if (attribute.isDuration()) {
			//split on the median
			TLongList values = FeatureImplDuration.gatherValues(log, attribute);
			long median = (long) median(values.toArray());

			addFeature(result, new FeatureImplDuration(attribute, median), log, minimumNumberOfTraces,
					maximumNumberOfTraces);

			if (!attribute.isVirtual()) {
				addFeature(result, new FeatureImplMissing(attribute), log, minimumNumberOfTraces,
						maximumNumberOfTraces);
			}
		} else {
			System.out.println(attribute + " not supported");
		}
	}

	private void processLiteral(Attribute attribute, XLog log, AttributeFeatureMap result, long minimumNumberOfTraces,
			long maximumNumberOfTraces) {
		if (attribute.getStringValues().size() != log.size()) { //prevent to add unique attribute values 
			for (String value : attribute.getStringValues()) {
				addFeature(result, new FeatureImplLiteral(attribute, value), log, minimumNumberOfTraces,
						maximumNumberOfTraces);
			}

			if (!attribute.isVirtual()) {
				addFeature(result, new FeatureImplMissing(attribute), log, minimumNumberOfTraces,
						maximumNumberOfTraces);
			}
		}
	}

	public static double median(double[] values) {
		values = values.clone();
		if (values.length % 2 == 1) {
			return quickSelect(values, 0, values.length - 1, values.length / 2);
		} else {
			return (quickSelect(values, 0, values.length - 1, values.length / 2 - 1)
					+ quickSelect(values, 0, values.length - 1, values.length / 2)) / 2.0;
		}
	}

	public static double median(long[] values) {
		values = values.clone();
		if (values.length % 2 == 1) {
			return quickSelect(values, 0, values.length - 1, values.length / 2);
		} else {
			return (quickSelect(values, 0, values.length - 1, values.length / 2 - 1)
					+ quickSelect(values, 0, values.length - 1, values.length / 2)) / 2.0;
		}
	}

	private static double quickSelect(double[] arr, int left, int right, int k) {
		while (true) {
			if (k >= 0 && k <= right - left + 1) {
				int pos = randomPartition(arr, left, right);
				if (pos - left == k) {
					return arr[pos];
				}
				if (pos - left > k) {
					right = pos - 1;
					//return quickSelect(arr, left, pos - 1, k);
				} else {
					k = k - pos + left - 1;
					left = pos + 1;
					//return quickSelect(arr, pos + 1, right, k - pos + left - 1);
				}
			} else {
				return 0;
			}
		}
	}

	private static long quickSelect(long[] arr, int left, int right, int k) {
		while (true) {
			if (k >= 0 && k <= right - left + 1) {
				int pos = randomPartition(arr, left, right);
				if (pos - left == k) {
					return arr[pos];
				}
				if (pos - left > k) {
					right = pos - 1;
					//return quickSelect(arr, left, pos - 1, k);
				} else {
					k = k - pos + left - 1;
					left = pos + 1;
					//return quickSelect(arr, pos + 1, right, k - pos + left - 1);
				}
			} else {
				return 0;
			}
		}
	}

	public static int partitionIterative(double[] arr, int left, int right) {
		double pivot = arr[right];
		int i = left;
		for (int j = left; j <= right - 1; j++) {
			if (arr[j] <= pivot) {
				swap(arr, i, j);
				i++;
			}
		}
		swap(arr, i, right);
		return i;
	}

	private static int partitionIterative(long[] arr, int left, int right) {
		long pivot = arr[right];
		int i = left;
		for (int j = left; j <= right - 1; j++) {
			if (arr[j] <= pivot) {
				swap(arr, i, j);
				i++;
			}
		}
		swap(arr, i, right);
		return i;
	}

	private static void swap(double[] arr, int n1, int n2) {
		double temp = arr[n2];
		arr[n2] = arr[n1];
		arr[n1] = temp;
	}

	private static void swap(long[] arr, int n1, int n2) {
		long temp = arr[n2];
		arr[n2] = arr[n1];
		arr[n1] = temp;
	}

	private static int randomPartition(double[] arr, int left, int right) {
		int n = right - left + 1;
		int pivot = (int) (Math.random()) * n;
		swap(arr, left + pivot, right);
		return partitionIterative(arr, left, right);
	}

	private static int randomPartition(long[] arr, int left, int right) {
		int n = right - left + 1;
		int pivot = (int) (Math.random()) * n;
		swap(arr, left + pivot, right);
		return partitionIterative(arr, left, right);
	}
}
