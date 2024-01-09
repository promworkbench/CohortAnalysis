package org.processmining.cohortanalysis.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.plugins.inductiveminer2.attributes.AttributesInfo;
import org.processmining.plugins.inductiveminer2.attributes.AttributesInfoImpl;

public abstract class FeatureFactoryAbstract implements FeatureFactory {

	public AttributeFeatureMap getFeatures(XLog log, Collection<String> excludeTraceAttributes, double sizeThreshold,
			ProMCanceller canceller) {
		AttributesInfo info = getAttributes(log);
		List<Attribute> attributes = new ArrayList<>(info.getTraceAttributes());

		//filter unwanted attributes
		for (Iterator<Attribute> it = attributes.iterator(); it.hasNext();) {
			if (excludeTraceAttributes.contains(it.next().getName())) {
				it.remove();
			}
		}

		AttributeFeatureMap result = new AttributeFeatureMap(attributes);

		for (Attribute attribute : attributes) {
			processAttribute(attribute, log, result, Math.round(log.size() * sizeThreshold),
					Math.round(log.size() * (1 - sizeThreshold)));

			if (canceller.isCancelled()) {
				return null;
			}
		}

		return result;
	}

	public AttributesInfo getAttributes(XLog log) {
		return new AttributesInfoImpl(log);
	}

	public static void addFeature(AttributeFeatureMap result, FeatureAbstract feature, XLog log,
			long minimumNumberOfTraces, long maximumNumberOfTraces) {
		result.addFeature(feature);
	}

	protected abstract void processAttribute(Attribute attribute, XLog log, AttributeFeatureMap result,
			long minimumNumberOfTraces, long maximumNumberOfTraces);
}