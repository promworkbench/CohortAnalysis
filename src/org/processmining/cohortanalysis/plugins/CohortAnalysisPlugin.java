package org.processmining.cohortanalysis.plugins;

import java.util.concurrent.ExecutionException;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.cohortanalysis.CohortAnalysis;
import org.processmining.cohortanalysis.Diversify2;
import org.processmining.cohortanalysis.cohort.Cohorts;
import org.processmining.cohortanalysis.cohort.CohortsImpl;
import org.processmining.cohortanalysis.parameters.CohortAnalysisParameters;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;

public class CohortAnalysisPlugin {
	@Plugin(name = "Cohort analysis", returnLabels = { "Cohorts" }, returnTypes = { Cohorts.class }, parameterLabels = {
			"Event log" }, userAccessible = true, help = "Find sub-logs that are the most different in terms of their stochastic language, using Earth-movers' stochastic conformance (EMSC).", level = PluginLevel.Regular)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine a sdfa, dialog", requiredParameterLabels = { 0 })
	public Cohorts convert(final UIPluginContext context, XLog log) throws Exception {

		CohortAnalysisDialog dialog = new CohortAnalysisDialog(log);
		InteractionResult iResult = context.showWizard("Cohort analysis", true, true, dialog);

		if (iResult != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		CohortAnalysisParameters parameters = dialog.getParameters();

		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return context.getProgress().isCancelled();
			}
		};

		Cohorts result = measure(log, parameters, canceller);
		if (result == null) {
			context.getFutureResult(0).cancel(false);
			return null;
		} else {
			return result;
		}
	}

	public static Cohorts measure(XLog log, CohortAnalysisParameters parameters, ProMCanceller canceller)
			throws ExecutionException {
		CohortsImpl result = CohortAnalysis.compute(log, parameters, canceller);

		if (parameters.getDiversifyThreshold() < 1) {
			Diversify2.diversify(result, parameters, canceller);
		}

		return result;
	}
}