package org.processmining.cohortanalysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.NonSquareMatrixException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.processmining.cohortanalysis.cohort.Cohort;
import org.processmining.cohortanalysis.cohort.Cohorts;
import org.processmining.cohortanalysis.cohort.CohortsImpl;
import org.processmining.cohortanalysis.feature.Feature;
import org.processmining.cohortanalysis.feature.Features2String;
import org.processmining.cohortanalysis.parameters.CohortAnalysisParameters;
import org.processmining.framework.plugin.ProMCanceller;

import gnu.trove.set.hash.THashSet;

public class Diversify {
	/**
	 * Set top results in the cohorts.
	 * 
	 * @param cohorts
	 * @param topSize
	 */
	public static void diversify(CohortsImpl cohorts, CohortAnalysisParameters parameters, ProMCanceller canceller)
			throws NullArgumentException, SingularMatrixException, NonSquareMatrixException {
		CohortAnalysis.debug(parameters, "diversifying");

		//perform basic truncation
		List<Cohort> truncatedCohorts = truncateCohorts(cohorts, parameters.getDiversifyThreshold());

		while (truncatedCohorts.size() > 1) {
			//initialise
			RealMatrix A = createMatrix(truncatedCohorts);
			RealMatrix Ap = MatrixUtils.inverse(A);

			//get minimum-contributing cohort
			int mIndex = getMinContributionIndex(Ap);
			truncatedCohorts.get(mIndex).setDiversityRank(truncatedCohorts.size());

			//delete that cohort
			truncatedCohorts.remove(mIndex);
		}

		truncatedCohorts.get(0).setDiversityRank(1);
	}

	/**
	 * Strategy: take all cohorts within 20% of the highest value.
	 */
	public static List<Cohort> truncateCohorts(Cohorts cohorts, double diversifyThreshold) {
		double threshold = cohorts.get(0).getDistance() * diversifyThreshold;

		List<Cohort> result = new ArrayList<>();
		for (Cohort cohort : cohorts) {
			if (cohort.getDistance() >= threshold) {
				result.add(cohort);
			} else {
				return result;
			}
		}
		return result;
	}

	private static int getMinContributionIndex(RealMatrix Ap) {
		double min = Double.MAX_VALUE;
		int column = -1;

		for (int indexInMatrix = 0; indexInMatrix < Ap.getColumnDimension(); indexInMatrix++) {
			double contribution = getContribution(Ap, indexInMatrix);
			if (contribution < min) {
				min = contribution;
				column = indexInMatrix;
			}
		}

		return column;
	}

	private static double getContribution(RealMatrix Ap, int indexInMatrix) {
		double c = Ap.getEntry(indexInMatrix, indexInMatrix);
		double sum = 0;
		{
			for (int row = 0; row < Ap.getRowDimension(); row++) {
				sum += Ap.getEntry(row, indexInMatrix);
			}
		}

		return (1 / c) * sum * sum;
	}

	private static RealMatrix createMatrix(List<Cohort> cohorts) {
		System.out.println("");
		RealMatrix A = MatrixUtils.createRealMatrix(cohorts.size(), cohorts.size());
		for (int i = 0; i < cohorts.size(); i++) {
			A.setEntry(i, i, Math.exp(0));
			for (int j = i + 1; j < cohorts.size(); j++) {
				double d = getDistance(cohorts.get(i), cohorts.get(j));
				System.out.println(Features2String.toString(cohorts.get(i).getFeatures()) + " vs. "
						+ Features2String.toString(cohorts.get(j).getFeatures()) + " " + d);
				A.setEntry(i, j, Math.exp(d));
			}
		}
		return A;
	}

	private static double getDistance(Cohort cohortA, Cohort cohortB) {
		Collection<Feature> featuresA = cohortA.getFeatures();
		Collection<Feature> featuresB = cohortB.getFeatures();

		int U;
		{
			Set<String> Us = new THashSet<>();
			for (Feature feature : featuresA) {
				Us.add(feature.getDescriptionField());
			}
			for (Feature feature : featuresB) {
				Us.add(feature.getDescriptionField());
			}
			U = Us.size();
		}

		int D = 0;
		int E = 0;
		{
			for (Feature featureA : featuresA) {
				Feature featureB = containsAttribute(featuresB, featureA);
				if (featureB == null) {
					//attribute not present
				} else if (featureA.getDescriptionSelector().equals(featureB.getDescriptionSelector())) {
					//attribute equal, value range equal
					E++;
				} else {
					//attribute equal, value range different
					D++;
				}
			}
		}

		return ((U - 0.5 * D) - E) / U;
	}

	private static Feature containsAttribute(Collection<Feature> features, Feature feature) {
		for (Feature featureA : features) {
			if (featureA.getDescriptionField().equals(feature.getDescriptionField())) {
				return featureA;
			}
		}
		return null;
	}
}