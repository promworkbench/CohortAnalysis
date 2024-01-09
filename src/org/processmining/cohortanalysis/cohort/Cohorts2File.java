package org.processmining.cohortanalysis.cohort;

import org.processmining.cohortanalysis.feature.Features2String;
import org.processmining.cohortanalysis.plugins.CohortsVisualisationPlugin;

public class Cohorts2File {
	public static String convert(Cohorts cohorts) {
		StringBuilder result = new StringBuilder();
		//count selected diverse results
		int count = 0;
		{
			for (int diversityRank = 1; diversityRank <= CohortsVisualisationPlugin.maxDiversityRank; diversityRank++) {
				for (Cohort cohort : cohorts) {
					if (diversityRank == cohort.getDiversityRank()) {
						count++;
					}
				}
			}
			result.append(count + " selected cohorts\n");
		}

		//selected diverse results
		{
			for (int diversityRank = 1; diversityRank <= CohortsVisualisationPlugin.maxDiversityRank; diversityRank++) {
				for (Cohort cohort : cohorts) {
					if (diversityRank == cohort.getDiversityRank()) {
						result.append(cohort.getSize() + " cohort size\n");
						result.append(cohort.getDistance() + " distance\n");
						result.append(Features2String.toString(cohort.getFeatures()) + "\n");
					}
				}
			}
		}

		result.append(cohorts.size() + " all cohorts\n");

		//all results
		{
			for (Cohort cohort : cohorts) {
				result.append(cohort.getSize() + " cohort size\n");
				result.append(cohort.getDistance() + " distance\n");
				result.append(Features2String.toString(cohort.getFeatures()) + "\n");
			}
		}

		result.append("Only cohorts of at least " + Math.round(cohorts.getMinimumCohortSize())
				+ " traces have been considered.");
		return result.toString();
	}
}