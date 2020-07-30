package hr.fer.zemris.gthpw.transcript;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TranscriptUtil {

	public static Transcript loadTranscript(String transcriptFileString) throws IOException {
		List<String> textlineList = new ArrayList<>();
		Files.lines(Paths.get(transcriptFileString)).forEach(l -> textlineList.add(l));
		
		String[][] textLines = new String[textlineList.size()][];
		for(int i = 0; i < textlineList.size(); i++) {
			String line = textlineList.get(i);
			StringBuilder sb = new StringBuilder();
			for(int j = 0; j < line.length(); j++) {
				char c = line.charAt(j);
				if(Character.isAlphabetic(c) || Character.isWhitespace(c)) {
					sb.append(c);
				}
			}
			line = sb.toString();
			line.toUpperCase();
			textLines[i] = line.split(" ");
		}
		
		return new TranscriptImpl(textLines);
	}
	
}
