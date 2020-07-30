package hr.fer.zemris.gthpw.actions;

import java.awt.event.ActionEvent;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.AnnotatingDocImageModelAdapter;

public class ShowAction extends AbstractAction {
	
	private static final long serialVersionUID = 1L;

	private Consumer<Boolean> consumer;
	
	public ShowAction(String name, AnnotatingDocImageModel model, Consumer<Boolean> consumer) {
		super(name);
		this.consumer = Objects.requireNonNull(consumer);
		
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
		consumer.accept(((JToggleButton) e.getSource()).isSelected());
	}

}
