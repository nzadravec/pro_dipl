package hr.fer.zemris.gthpw.annotating_tools;

import java.awt.event.ActionEvent;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.event.ChangeEvent;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.AnnotatingDocImageModelAdapter;

public abstract class AbstractAnnotatingTool extends AbstractAction implements AnnotatingTool {

	private static final long serialVersionUID = 1L;
	
	private ActivatedAnnotatingTool tool;
	
	protected AnnotatingDocImageModel model;
	
	public AbstractAnnotatingTool(String name, ActivatedAnnotatingTool tool, AnnotatingDocImageModel model) {
		super(name);
		this.tool = Objects.requireNonNull(tool);
		this.model = Objects.requireNonNull(model);
		
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
		tool.setAnnotatingTool(this);
	}
	
}
