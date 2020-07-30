package hr.fer.zemris.gthpw.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.AnnotatingDocImageModelListener;

public class ExitAction extends AbstractAction {
	
	private static final long serialVersionUID = 1L;
	
	private JFrame frame;
	private AnnotatingDocImageModel model;
	
	private boolean docImageLoaded;
	private boolean changed;
	
	public ExitAction(String name, JFrame frame, AnnotatingDocImageModel model) {
		super(name);
		this.frame = frame;
		this.model = model;
		
		this.model.addListener(new AnnotatingDocImageModelListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				changed = true;
			}

			@Override
			public void docImageLoaded(ChangeEvent e) {
				docImageLoaded = true;
			}
		});
	}
	
	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(!docImageLoaded || !changed) {
			System.exit(0);
		}
		
		int decision = JOptionPane.showConfirmDialog(
			    frame,
			    "If you don't save, changes will be lost.",
			    "Save before closing?",
			    JOptionPane.YES_NO_CANCEL_OPTION);
		
		if(decision == JOptionPane.YES_OPTION) {
			new SaveAction(null, frame, model, this).actionPerformed(e);
		} else if(decision == JOptionPane.NO_OPTION) {
			// don't save
		} else {
			return;
		}
		
		System.exit(0);
	}

}
