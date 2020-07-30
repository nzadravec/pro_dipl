package hr.fer.zemris.gthpw.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.AnnotatingDocImageModelAdapter;
import hr.fer.zemris.gthpw.transcript.Transcript;
import hr.fer.zemris.gthpw.transcript.TranscriptUtil;

public class ReloadTranscriptAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	
	private JFrame frame;
	private AnnotatingDocImageModel model;
	
	public ReloadTranscriptAction(String name, JFrame frame, AnnotatingDocImageModel model) {
		super(name);
		this.frame = frame;
		this.model = model;
		
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
		String transcriptFileString = imageFileString.substring(0, imageFileString.lastIndexOf('.') + 1) + "txt";
		Transcript transcript;
		try {
			transcript = TranscriptUtil.loadTranscript(transcriptFileString);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(frame, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		model.setTranscript(transcript);
	}

}
