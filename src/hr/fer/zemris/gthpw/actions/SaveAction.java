package hr.fer.zemris.gthpw.actions;

import java.awt.event.ActionEvent;
import java.nio.file.Path;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.AnnotatingDocImageModelAdapter;
import hr.fer.zemris.gthpw.ground_truth.GroundTruthXMLSaver;

public class SaveAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private JFrame frame;
	private AnnotatingDocImageModel model;
	private ExitAction exit;
	
	private GroundTruthXMLSaver saver;
	
	public SaveAction(String name, JFrame frame, AnnotatingDocImageModel model, ExitAction exit) {
		super(name);
		this.frame = frame;
		this.model = model;
		this.exit = exit;
		saver = new GroundTruthXMLSaver(model);
		
		setEnabled(false);
		
		model.addListener(new AnnotatingDocImageModelAdapter() {
			@Override
			public void docImageLoaded(ChangeEvent e) {
				setEnabled(true);
			}
		});
	}
		
	@Override
	public void actionPerformed(ActionEvent e) {
		Path path = model.getDocImage().getFilePath();
		
		String imageFileString = path.toString();
		String groundTruthFileString = imageFileString.substring(0, imageFileString.lastIndexOf('.') + 1) + "xml";
		
		try {
			saver.saveToXML(groundTruthFileString);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(frame, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		exit.setChanged(false);
	}
	
}
