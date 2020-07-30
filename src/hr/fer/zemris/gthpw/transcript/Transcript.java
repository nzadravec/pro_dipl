package hr.fer.zemris.gthpw.transcript;

public interface Transcript {

	int numberOfLines();
	
	String[] getWordsAtLine(int index);
	
}
