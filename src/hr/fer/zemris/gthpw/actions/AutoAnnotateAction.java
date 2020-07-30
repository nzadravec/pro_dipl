package hr.fer.zemris.gthpw.actions;

import javax.swing.AbstractAction;
import javax.swing.event.ChangeEvent;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.AnnotatingDocImageModelAdapter;

public abstract class AutoAnnotateAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	
	protected AnnotatingDocImageModel model;

	public AutoAnnotateAction(String name, AnnotatingDocImageModel model) {
		super(name);
		this.model = model;
		
		setEnabled(false);
		
		model.addListener(new AnnotatingDocImageModelAdapter() {
			@Override
			public void docImageLoaded(ChangeEvent e) {
				setEnabled(true);
			}
		});
	}
	
}
