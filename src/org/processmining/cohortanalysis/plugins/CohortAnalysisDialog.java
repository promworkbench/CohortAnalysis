package org.processmining.cohortanalysis.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deckfour.xes.model.XLog;
import org.processmining.cohortanalysis.parameters.CohortAnalysisParametersAbstract;
import org.processmining.cohortanalysis.parameters.CohortAnalysisParametersDefault;
import org.processmining.plugins.InductiveMiner.ClassifierChooser;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class CohortAnalysisDialog extends JPanel {

	private static final long serialVersionUID = -1097548788215530060L;

	public static final int leftColumnWidth = 200;
	public static final int columnMargin = 20;
	public static final int rowHeight = 40;

	private CohortAnalysisParametersAbstract parameters = new CohortAnalysisParametersDefault();

	private final SpringLayout layout;

	public CohortAnalysisDialog(XLog log) {
		SlickerFactory factory = SlickerFactory.instance();

		layout = new SpringLayout();
		setLayout(layout);

		//first group
		final ClassifierChooser classifiers;
		{
			JLabel classifierLabel = factory.createLabel("Event classifier");
			add(classifierLabel);
			layout.putConstraint(SpringLayout.NORTH, classifierLabel, 5, SpringLayout.NORTH, this);
			layout.putConstraint(SpringLayout.EAST, classifierLabel, leftColumnWidth, SpringLayout.WEST, this);

			classifiers = new ClassifierChooser(log);
			add(classifiers);
			layout.putConstraint(SpringLayout.WEST, classifiers, columnMargin, SpringLayout.EAST, classifierLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, classifiers, 0, SpringLayout.VERTICAL_CENTER,
					classifierLabel);

			classifiers.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					parameters.setClassifier(classifiers.getSelectedClassifier());
				}
			});
			parameters.setClassifier(classifiers.getSelectedClassifier());
		}

		//second group
		final JSlider sliderMass;
		{
			JLabel classifierLabel = factory.createLabel("Maximum features");
			add(classifierLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, classifierLabel, rowHeight, SpringLayout.VERTICAL_CENTER,
					classifiers);
			layout.putConstraint(SpringLayout.EAST, classifierLabel, leftColumnWidth, SpringLayout.WEST, this);

			sliderMass = factory.createSlider(SwingConstants.HORIZONTAL);
			sliderMass.setMinimum(1);
			sliderMass.setMaximum(5);
			sliderMass.setValue(parameters.getMaxFeatureSetSize());
			add(sliderMass);
			layout.putConstraint(SpringLayout.WEST, sliderMass, columnMargin, SpringLayout.EAST, classifierLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, sliderMass, 0, SpringLayout.VERTICAL_CENTER,
					classifierLabel);

			final JLabel firstNoiseValue = factory.createLabel(sliderMass.getValue() + "");
			add(firstNoiseValue);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, firstNoiseValue, 0, SpringLayout.VERTICAL_CENTER,
					classifierLabel);
			layout.putConstraint(SpringLayout.WEST, firstNoiseValue, 5, SpringLayout.EAST, sliderMass);

			sliderMass.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					parameters.setMaxFeatureSetSize(sliderMass.getValue());
					firstNoiseValue.setText(sliderMass.getValue() + "");
				}
			});
		}

		//third group
		{
			JLabel threadedLabel = factory.createLabel("Multithreaded");
			add(threadedLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, threadedLabel, rowHeight * 2,
					SpringLayout.VERTICAL_CENTER, sliderMass);
			layout.putConstraint(SpringLayout.EAST, threadedLabel, leftColumnWidth, SpringLayout.WEST, this);

			final JCheckBox threaded = factory.createCheckBox("", parameters.getNumberOfThreads() > 1);
			add(threaded);
			layout.putConstraint(SpringLayout.WEST, threaded, columnMargin, SpringLayout.EAST, threadedLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, threaded, 0, SpringLayout.VERTICAL_CENTER,
					threadedLabel);

			threaded.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					if (threaded.isSelected()) {
						parameters.setNumberOfThreads(CohortAnalysisParametersDefault.defaultThreads);
					} else {
						parameters.setNumberOfThreads(1);
					}
				}
			});
		}
	}

	public CohortAnalysisParametersAbstract getParameters() {
		return parameters;
	}
}