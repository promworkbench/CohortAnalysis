package org.processmining.cohortanalysis.plugins;

import javax.swing.JComponent;

import org.processmining.cohortanalysis.cohort.Cohort;
import org.processmining.cohortanalysis.cohort.Cohorts;
import org.processmining.cohortanalysis.feature.Features2String;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.contexts.util.HtmlPanel;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.HTMLToString;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;

public class CohortsVisualisationPlugin {

	public static final int maxDiversityRank = 5;

	@Plugin(name = "Cohorts visualisation", returnLabels = { "Cohorts visualization" }, returnTypes = {
			JComponent.class }, parameterLabels = { "Cohorts" }, userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Visualise process tree", requiredParameterLabels = { 0 })
	public HtmlPanel fancy(PluginContext context, Cohorts cohorts) throws UnknownTreeNodeException {
		return new HtmlPanel(visualise(cohorts).toHTMLString(true));
	}

	public static HTMLToString visualise(final Cohorts cohorts) {
		return new HTMLToString() {
			public String toHTMLString(boolean includeHTMLTags) {
				StringBuilder result2 = new StringBuilder();
				result2.append("<table>");
				result2.append("<tr><td><b>Rank</b></td>" //
						+ "<td><b>Cohort defined by</b></td>" //
						+ "<td><b>Cohort size</b></td>" //
						+ "<td><b>Distance with rest of the log<br>(corrected for log variance)</b></td></tr>");

				//selected diverse results
				{
					result2.append("<tr>");
					result2.append("<td colspan=4>");
					result2.append("Selected diverse cohorts:");
					result2.append("</td>");
					result2.append("</tr>");
					for (int diversityRank = 1; diversityRank <= maxDiversityRank; diversityRank++) {
						int i = 1;
						for (Cohort cohort : cohorts) {
							if (diversityRank == cohort.getDiversityRank()) {
								result2.append("<tr>");
								result2.append("<td>");
								result2.append(i);
								result2.append("</td>");
								result2.append("<td>");
								result2.append(Features2String.toString(cohort.getFeatures()));
								result2.append("</td>");
								result2.append("<td>");
								result2.append(cohort.getSize());
								result2.append("</td>");
								result2.append("<td>");
								result2.append(cohort.getDistance());
								result2.append("</td>");
								result2.append("</tr>");
							}
							i++;
						}
					}
				}
				
				result2.append("<tr><td> </td></tr>");

				//all results
				{
					result2.append("<tr>");
					result2.append("<td colspan=4>");
					result2.append("All cohorts:");
					result2.append("</td>");
					result2.append("</tr>");
					int i = 1;
					for (Cohort cohort : cohorts) {
						result2.append("<tr>");
						result2.append("<td>");
						result2.append(i);
						result2.append("</td>");
						result2.append("<td>");
						result2.append(Features2String.toString(cohort.getFeatures()));
						result2.append("</td>");
						result2.append("<td>");
						result2.append(cohort.getSize());
						result2.append("</td>");
						result2.append("<td>");
						result2.append(cohort.getDistance());
						result2.append("</td>");
						result2.append("</tr>");
						i++;
					}
				}

				result2.append("</table>");

				result2.append("<br>");
				result2.append("Only cohorts of at least " + Math.round(cohorts.getMinimumCohortSize())
						+ " traces have been considered.");
				return result2.toString();
			}
		};
	}
}
