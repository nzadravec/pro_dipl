package hr.fer.zemris.gthpw;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public interface AnnotatingDocImageModelListener extends ChangeListener {
	
	void docImageLoaded(ChangeEvent e);
	
}
