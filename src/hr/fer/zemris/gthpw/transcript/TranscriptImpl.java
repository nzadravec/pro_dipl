package hr.fer.zemris.gthpw.transcript;

public class TranscriptImpl implements Transcript {
	
	private String[][] textLines;
	
	public TranscriptImpl(String[][] textLines) {
		this.textLines = textLines;
	}
	
	public int numberOfLines() {
		return textLines.length;
	}
	
	public String[] getWordsAtLine(int index) {
		return textLines[index];
	}

}
