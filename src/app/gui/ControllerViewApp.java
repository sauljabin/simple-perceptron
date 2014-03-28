/**
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 *		SAUL PIÑA - SAULJP07@GMAIL.COM
 *		2014
 */

package app.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import app.Log;
import app.Translate;
import app.perceptron.SimplePerceptron;
import app.util.UtilFileText;

public class ControllerViewApp extends WindowAdapter implements ActionListener, ChangeListener, Runnable {

	private ViewApp viewApp;
	private Thread threadTraining;
	private SimplePerceptron perceptron;

	public ControllerViewApp() {
		viewApp = new ViewApp();
		viewApp.setController(this);
		Log.setLogTextArea(viewApp.getTarConsole());
	}

	@Override
	public void windowClosing(WindowEvent e) {
		close();
	}

	public void close() {
		viewApp.dispose();
		System.exit(0);
	}

	public void about() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ViewAbout();
			}
		});
	}

	public void showConfig() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ViewConfig();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source.equals(viewApp.getMenuItemClose()))
			close();
		else if (source.equals(viewApp.getMenuItemAbout()))
			about();
		else if (source.equals(viewApp.getMenuItemShowConfig()))
			showConfig();
		else if (source.equals(viewApp.getBtnClose()))
			close();
		else if (source.equals(viewApp.getBtnTraining()))
			training();
		else if (source.equals(viewApp.getBtnPathFileTraining()))
			getPathFileTraining();
		else if (source.equals(viewApp.getBtnPathResults()))
			getPathResults();
		else if (source.equals(viewApp.getBtnPathTestValues()))
			getPathTestValues();
	}

	public File fileChooser(String path) {
		JFileChooser file = new JFileChooser(new File(path));
		file.showDialog(viewApp, Translate.get("GUI_OPEN"));
		return file.getSelectedFile();
	}

	public void getPathResults() {
		String path = viewApp.getPathResults().getText();
		if (path.trim().isEmpty())
			path = ".";
		File file = fileChooser(path);
		if (file != null) {
			viewApp.getPathResults().setText(file.getAbsolutePath());
		}
	}

	public void getPathFileTraining() {
		String path = viewApp.getPathFileTraining().getText();
		if (path.trim().isEmpty())
			path = ".";
		File file = fileChooser(path);
		if (file != null) {
			viewApp.getPathFileTraining().setText(file.getAbsolutePath());
		}
	}

	private void getPathTestValues() {
		String path = viewApp.getPathTestValues().getText();
		if (path.trim().isEmpty())
			path = ".";
		File file = fileChooser(path);
		if (file != null) {
			viewApp.getPathTestValues().setText(file.getAbsolutePath());
		}

	}

	public void training() {

		if (viewApp.getTxtName().getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(viewApp, Translate.get("ERROR_NAMEEMPTY"), Translate.get("GUI_ERROR"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (viewApp.getPathTestValues().getText().trim().isEmpty() || viewApp.getPathFileTraining().getText().trim().isEmpty() || viewApp.getPathResults().getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(viewApp, Translate.get("ERROR_PATHEMPTY"), Translate.get("GUI_ERROR"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		viewApp.getBtnPathFileTraining().setEnabled(false);
		viewApp.getBtnPathResults().setEnabled(false);
		viewApp.getBtnTraining().setEnabled(false);
		viewApp.getPathFileTraining().setEnabled(false);
		viewApp.getPathResults().setEnabled(false);

		threadTraining = new Thread(this);
		threadTraining.start();
	}

	public void enable() {
		viewApp.getBtnPathFileTraining().setEnabled(true);
		viewApp.getBtnPathResults().setEnabled(true);
		viewApp.getBtnTraining().setEnabled(true);
		viewApp.getPathFileTraining().setEnabled(true);
		viewApp.getPathResults().setEnabled(true);
	}

	@Override
	public void stateChanged(ChangeEvent e) {

	}

	@Override
	public void run() {
		UtilFileText uft = new UtilFileText();
		String pathFileTraining = viewApp.getPathFileTraining().getText();
		String pathTestValues = viewApp.getPathTestValues().getText();
		String pathResults = viewApp.getPathResults().getText();
		String name = viewApp.getTxtName().getText();
		double learningFactor = (double) viewApp.getSpiFactor().getValue();
		int maxPeriods = (int) viewApp.getSpiMaxPeriods().getValue();
		List<String> trainingValuesList;
		List<String> testValuesList;
		int[][] trainingValues;
		int[] desiredOutput;
		int[][] testValues;

		try {
			trainingValuesList = uft.readFileToList(pathFileTraining);
			trainingValues = new int[trainingValuesList.size()][];
			desiredOutput = new int[trainingValuesList.size()];
		} catch (IOException e) {
			enable();
			Log.error(ControllerViewApp.class, Translate.get("ERROR_LOADTRAININGVALUES"), e);
			return;
		}

		for (int i = 0; i < trainingValuesList.size(); i++) {
			String s = trainingValuesList.get(i);

			String[] string = s.split(";");

			String[] inputString = string[0].split(",");
			int[] input = new int[inputString.length];

			String[] outputString = string[1].split(",");

			for (int j = 0; j < input.length; j++) {
				input[j] = Integer.parseInt(inputString[j]);
			}

			trainingValues[i] = input;
			desiredOutput[i] = Integer.parseInt(outputString[0]);
		}

		try {
			testValuesList = uft.readFileToList(pathTestValues);
			testValues = new int[testValuesList.size()][];
		} catch (IOException e) {
			enable();
			Log.error(ControllerViewApp.class, Translate.get("ERROR_LOADTESTVALUES"), e);
			return;
		}

		for (int i = 0; i < testValuesList.size(); i++) {
			String s = testValuesList.get(i);

			String[] inputString = s.split(",");
			int[] input = new int[inputString.length];
			for (int j = 0; j < input.length; j++) {
				input[j] = Integer.parseInt(inputString[j]);
			}
			testValues[i] = input;
		}

		Log.info(ControllerViewApp.class, Translate.get("INFO_INITTRAINING"));
		perceptron = new SimplePerceptron(learningFactor, maxPeriods);
		perceptron.training(trainingValues, desiredOutput);
		Log.info(ControllerViewApp.class, Translate.get("INFO_FINISHTRAINING"));

		String print = Translate.get("GUI_NAME") + ": " + name;
		print += "\n\n" + Translate.get("GUI_FACTOR") + "\t" + Translate.get("GUI_PERIODS");
		print += "\n" + perceptron.getLearningFactor() + "\t" + perceptron.getPeriods();
		print += "\n\n" + Translate.get("GUI_PERIODS") + "\t" + Translate.get("GUI_ERROR");

		for (int i = 0; i < perceptron.getErrors().size(); i++) {
			print += "\n" + i;
			print += "\t" + perceptron.getErrors().get(i);
		}

		print += "\n\n" + Translate.get("GUI_TESTVALUES") + "\n";
		print += Translate.get("GUI_INPUT") + "\t" + Translate.get("GUI_OUTPUT");

		for (int i = 0; i < testValues.length; i++) {
			print += "\n";
			for (int j = 0; j < testValues[i].length; j++) {
				print += testValues[i][j];
			}
			print += "\t" + perceptron.output(testValues[i]);
		}

		Log.info(ControllerViewApp.class, Translate.get("INFO_SAVERESULTS"));
		try {
			uft.writeFile(pathResults, print);
		} catch (Exception e) {
			Log.error(ControllerViewApp.class, Translate.get("ERROR_SAVERESULTS"), e);
		}

		ViewGraphic viewG = new ViewGraphic(name);

		for (int i = 0; i < perceptron.getErrors().size(); i++) {
			viewG.addPoint(i, perceptron.getErrors().get(i));
		}
		try {
			viewG.exportImage(viewApp.getPathResults().getText() + ".png");
		} catch (IOException e) {
			Log.error(ControllerViewApp.class, Translate.get("ERROR_SAVERESULTSIMAGE"), e);
		}

		enable();
	}

}
