package app.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import app.Log;
import app.Translate;

public class ControllerViewApp extends WindowAdapter implements ActionListener, ChangeListener, Runnable {

	private ViewApp viewApp;
	private Thread threadTraining;

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
		else if (source.equals(viewApp.getBtnPathDesiredOutput()))
			getPathDesiredOutput();
		else if (source.equals(viewApp.getBtnPathFileTraining()))
			getPathFileTraining();
		else if (source.equals(viewApp.getBtnPathResults()))
			getPathResults();
	}

	public File fileChooser() {
		JFileChooser file = new JFileChooser();
		file.showDialog(viewApp, Translate.get("GUI_OPEN"));
		return file.getSelectedFile();
	}

	public void getPathResults() {
		File file = fileChooser();
		if (file != null) {
			viewApp.getPathResults().setText(file.getAbsolutePath());
		}
	}

	public void getPathFileTraining() {
		File file = fileChooser();
		if (file != null) {
			viewApp.getPathFileTraining().setText(file.getAbsolutePath());
		}
	}

	public void getPathDesiredOutput() {
		File file = fileChooser();
		if (file != null) {
			viewApp.getPathDesiredOutput().setText(file.getAbsolutePath());
		}
	}

	public void training() {

		if (viewApp.getPathDesiredOutput().getText().trim().isEmpty() || viewApp.getPathFileTraining().getText().trim().isEmpty() || viewApp.getPathResults().getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(viewApp, Translate.get("GUI_PATHEMPTY"), "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		viewApp.getBtnPathDesiredOutput().setEnabled(false);
		viewApp.getBtnPathFileTraining().setEnabled(false);
		viewApp.getBtnPathResults().setEnabled(false);
		viewApp.getBtnTraining().setEnabled(false);
		viewApp.getPathDesiredOutput().setEnabled(false);
		viewApp.getPathFileTraining().setEnabled(false);
		viewApp.getPathResults().setEnabled(false);

		threadTraining = new Thread(this);
		threadTraining.start();
	}

	@Override
	public void stateChanged(ChangeEvent e) {

	}

	@Override
	public void run() {

	}

}
