package hr.fer.zemris.gthpw;

import java.awt.image.BufferedImage;
import java.nio.file.Path;

import hr.fer.zemris.gthpw.image.binary.BinaryImage;
import hr.fer.zemris.gthpw.transcript.Transcript;

public interface DocImage {
	
	BufferedImage getImage();
	
	BinaryImage getBinaryImage();

	Transcript getTranscript();
	
	Path getFilePath();
	
}
