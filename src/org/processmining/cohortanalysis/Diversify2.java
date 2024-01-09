package org.processmining.cohortanalysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.linear.NonSquareMatrixException;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.processmining.cohortanalysis.cohort.Cohort;
import org.processmining.cohortanalysis.cohort.Cohorts;
import org.processmining.cohortanalysis.cohort.CohortsImpl;
import org.processmining.cohortanalysis.feature.Feature;
import org.processmining.cohortanalysis.parameters.CohortAnalysisParameters;
import org.processmining.framework.plugin.ProMCanceller;

public class Diversify2 {

	public static final double threshold = 0.05;

	public static void diversify(CohortsImpl cohorts, CohortAnalysisParameters parameters, ProMCanceller canceller)
			throws NullArgumentException, SingularMatrixException, NonSquareMatrixException {
		CohortAnalysis.debug(parameters, "diversifying");

		if (cohorts.size() > 0) {
			double maxDistance = cohorts.get(0).getDistance();

			//add all length-one cohorts
			for (Cohort cohort : cohorts) {
				if (cohort.getDistance() > maxDistance * parameters.getDiversifyThreshold()) {
					if (cohort.getFeatures().size() == 1) {
						cohort.setDiversityRank(1);
					} else {
						double parentDistance = getMaxParentDistance(cohorts, cohort);
						if (cohort.getDistance() > parentDistance * (1 + threshold)) {
							cohort.setDiversityRank(cohort.getFeatures().size());
						}
					}
				}
			}
		}
	}

	public static double getMaxParentDistance(Cohorts cohorts, Cohort cohort) {
		double maxParent = -Double.MAX_VALUE;
		for (Cohort parent : getParents(cohorts, cohort)) {
			if (parent != null) {
				maxParent = Math.max(maxParent, parent.getDistance());
			}
		}
		return maxParent;
	}

	public static List<Cohort> getParents(Cohorts cohorts, Cohort cohort) {
		List<Cohort> result = new ArrayList<>();
		for (Feature feature : cohort.getFeatures()) {
			List<Feature> newList = new ArrayList<>(cohort.getFeatures());
			newList.remove(feature);
			Cohort parent = findCohortWithFeatures(cohorts, newList);
			result.add(parent);
		}
		return result;
	}

	public static Cohort findCohortWithFeatures(Cohorts cohorts, Collection<Feature> features) {
		for (Cohort cohort : cohorts) {
			if (cohort.getFeatures().containsAll(features) && features.containsAll(cohort.getFeatures())) {
				return cohort;
			}
		}
		return null;
	}
}