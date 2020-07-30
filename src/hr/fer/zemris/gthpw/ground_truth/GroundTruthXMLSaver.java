package hr.fer.zemris.gthpw.ground_truth;

import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.ImageSegment;

public class GroundTruthXMLSaver {

	private AnnotatingDocImageModel model;

	public GroundTruthXMLSaver(AnnotatingDocImageModel model) {
		super();
		this.model = Objects.requireNonNull(model);
	}

	public void saveToXML(String xml) throws ParserConfigurationException, TransformerFactoryConfigurationError, FileNotFoundException, TransformerException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		DocumentBuilder db = dbf.newDocumentBuilder();
		Document dom = db.newDocument();
		Element rootElem = dom.createElement("ground_truth");
		dom.appendChild(rootElem);

		for (int lineIndex = 0; lineIndex < model.numberOfTextLines(); lineIndex++) {
			Element lineElem = dom.createElement("line");
			rootElem.appendChild(lineElem);

			for (int wordIndex = 0; wordIndex < model.numberOfWordsInLine(lineIndex); wordIndex++) {

				Element wordElem = dom.createElement("word");
				lineElem.appendChild(wordElem);

				List<ImageSegment> word = model.getWordInLine(wordIndex, lineIndex);

				for (int charIndex = 0; charIndex < word.size(); charIndex++) {

					Element charElem = dom.createElement("char");
					wordElem.appendChild(charElem);
					
					
					Element labelElem = dom.createElement("label");
					labelElem.appendChild(dom.createTextNode("" + model.getSegmentLabel(word.get(charIndex))));
					charElem.appendChild(labelElem);
					

					Element bbElem = dom.createElement("bounding_box");
					charElem.appendChild(bbElem);

					Rectangle bb = word.get(charIndex).getMinBoundingBox();

					Element xElem = dom.createElement("x");
					xElem.appendChild(dom.createTextNode(String.valueOf(bb.x)));
					bbElem.appendChild(xElem);

					Element yElem = dom.createElement("y");
					yElem.appendChild(dom.createTextNode(String.valueOf(bb.y)));
					bbElem.appendChild(yElem);

					Element widthElem = dom.createElement("width");
					widthElem.appendChild(dom.createTextNode(String.valueOf(bb.width)));
					bbElem.appendChild(widthElem);

					Element heightElem = dom.createElement("height");
					heightElem.appendChild(dom.createTextNode(String.valueOf(bb.height)));
					bbElem.appendChild(heightElem);

				}

			}

		}
		
		Transformer tr = TransformerFactory.newInstance().newTransformer();
		tr.setOutputProperty(OutputKeys.INDENT, "yes");
		tr.setOutputProperty(OutputKeys.METHOD, "xml");
		tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(xml)));

	}

}
