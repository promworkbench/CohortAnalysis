package org.processmining.cohortanalysis.feature;

import java.util.Collection;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.ProMCanceller;

public interface FeatureFactory {

	public AttributeFeatureMap getFeatures(XLog log, Collection<String> excludeTraceAttributes, double sizeThreshold,
			ProMCanceller canceller);

}
