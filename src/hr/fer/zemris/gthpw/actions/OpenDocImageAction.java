package hr.fer.zemris.gthpw.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.DocImage;

public class OpenDocImageAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private JFrame frame;
	private AnnotatingDocImageModel model;
	private ExitAction exit;
	
	public OpenDocImageAction(String name, JFrame frame, AnnotatingDocImageModel model, ExitAction exit) {
		super(name);
		this.frame = frame;
		this.model = model;
		this.exit = exit;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		DocImage docImage = model.getDocImage();
		if(docImage != null && exit.isChanged()) {
			int decision = JOptionPane.showConfirmDialog(
				    frame,
				    "If you don't save, changes will be lost.",
				    "Save before opening new document image?",
				    JOptionPane.YES_NO_CANCEL_OPTION);
			
			if(decision == JOptionPane.YES_OPTION) {
				new SaveAction(null, frame, model, exit).actionPerformed(e);
			} else if(decision == JOptionPane.NO_OPTION) {
				// don't save
			} else {
				return;
			}
		}
		
		JFileChooser fc = new JFileChooser(new File(".\\img"));
		fc.setDialogTitle("Open");
		
		int retVal = fc.showOpenDialog(frame);
		if (retVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		File docImageFile = fc.getSelectedFile();		
		try {
			model.loadDocImage(docImageFile.toPath());
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(frame, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
}
