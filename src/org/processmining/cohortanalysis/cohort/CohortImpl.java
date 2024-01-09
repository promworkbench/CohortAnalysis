package org.processmining.cohortanalysis.cohort;

import java.util.Collection;
import java.util.Collections;

import org.processmining.cohortanalysis.feature.Feature;

public class CohortImpl implements Cohort {

	private double distance;
	private Collection<Feature> features;
	private int size;
	private int diversityRank;

	public CohortImpl(Collection<Feature> features, double similarity, int size) {
		this.distance = similarity;
		this.features = features;
		this.size = size;
	}

	public int compareTo(Cohort o) {
		return Double.compare(o.getDistance(), getDistance());
	}

	@Override
	public double getDistance() {
		return distance;
	}

	@Override
	public Collection<Feature> getFeatures() {
		return Collections.unmodifiableCollection(features);
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public int getDiversityRank() {
		return diversityRank;
	}

	@Override
	public void setDiversityRank(int diversityRank) {
		this.diversityRank = diversityRank;
	}

}