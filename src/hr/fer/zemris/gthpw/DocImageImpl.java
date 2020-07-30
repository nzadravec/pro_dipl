package hr.fer.zemris.gthpw;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Objects;

import hr.fer.zemris.gthpw.image.binary.BinaryImage;
import hr.fer.zemris.gthpw.transcript.Transcript;

public class DocImageImpl implements DocImage {
	
	private Path filePath;
	private BufferedImage image;
	private BinaryImage binaryImage;
	private Transcript transcript;
	
	public DocImageImpl(Path filePath, BufferedImage image, BinaryImage binaryImage) {
		this(filePath, image, binaryImage, null);
	}
	
	public DocImageImpl(Path filePath, BufferedImage image, BinaryImage binaryImage, Transcript transcript) {
		this.filePath = Objects.requireNonNull(filePath);
		this.image = Objects.requireNonNull(image);
		this.binaryImage = Objects.requireNonNull(binaryImage);
		this.transcript = transcript;
	}

	@Override
	public BufferedImage getImage() {
		return image;
	}
	
	@Override
	public BinaryImage getBinaryImage() {
		return binaryImage;
	}

	@Override
	public Transcript getTranscript() {
		return transcript;
	}

	@Override
	public Path getFilePath() {
		return filePath;
	}

}
