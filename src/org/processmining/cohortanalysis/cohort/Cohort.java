package org.processmining.cohortanalysis.cohort;

import java.util.Collection;

import org.processmining.cohortanalysis.feature.Feature;

public interface Cohort extends Comparable<Cohort> {
	public double getDistance();

	public int getSize();

	public Collection<Feature> getFeatures();

	public int getDiversityRank();

	public void setDiversityRank(int diversityRank);
}