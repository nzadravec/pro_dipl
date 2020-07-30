package hr.fer.zemris.gthpw.ground_truth;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import hr.fer.zemris.gthpw.AnnotatingDocImageModel;
import hr.fer.zemris.gthpw.ImageSegment;
import hr.fer.zemris.gthpw.image.binary.BinaryImage;

public class GroundTruthXMLReader {

	private AnnotatingDocImageModel model;

	public GroundTruthXMLReader(AnnotatingDocImageModel model) {
		super();
		this.model = Objects.requireNonNull(model);
	}

	public void readXML(String xml) throws ParserConfigurationException, SAXException, IOException {

		BinaryImage binaryImage = model.getDocImage().getBinaryImage();
		
		Document dom;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		DocumentBuilder db = dbf.newDocumentBuilder();
		dom = db.parse(xml);
		
		Element doc = dom.getDocumentElement();

		NodeList lineNodeList = doc.getElementsByTagName("line");
		for(int lineIndex = 0; lineIndex < lineNodeList.getLength(); lineIndex++) {
			
			Node lineNode = lineNodeList.item(lineIndex);
			if(lineNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			
			List<ImageSegment> line = new ArrayList<>();
			
			List<List<ImageSegment>> words = new ArrayList<>();
			List<ImageSegment> word = new ArrayList<>();
			
			Element lineElem = (Element) lineNode;
			
			NodeList wordNodeList = lineElem.getElementsByTagName("word");
			
			for(int wordIndex = 0; wordIndex < wordNodeList.getLength(); wordIndex++) {
				
				Node wordNode = wordNodeList.item(wordIndex);
				if(wordNode.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				
				Element wordElem = (Element) wordNode;
				
				NodeList charNodeList = wordElem.getElementsByTagName("char");
				
				for(int charIndex = 0; charIndex < charNodeList.getLength(); charIndex++) {
					
					Node charNode = charNodeList.item(charIndex);
					if(charNode.getNodeType() != Node.ELEMENT_NODE) {
						continue;
					}
					
					Element charElem = (Element) charNode;
					
					ImageSegment segment = null;
					
					NodeList bbNodeList = charElem.getElementsByTagName("bounding_box");
					for(int bbIndex = 0; bbIndex < bbNodeList.getLength(); bbIndex++) {
						
						Node bbNode = bbNodeList.item(bbIndex);
						if(bbNode.getNodeType() != Node.ELEMENT_NODE) {
							continue;
						}
						
						Element bbElem = (Element) bbNode;
						
						Rectangle boundingBox = new Rectangle();
						
						NodeList bbPropsNodeList = bbElem.getChildNodes();
						for(int bbPropsIndex = 0; bbPropsIndex < bbPropsNodeList.getLength(); bbPropsIndex++) {
							
							Node bbPropNode = bbPropsNodeList.item(bbPropsIndex);
							if(bbPropNode.getNodeType() != Node.ELEMENT_NODE) {
								continue;
							}
							
							NodeList nodeList = bbPropNode.getChildNodes();
							for(int index = 0; index < nodeList.getLength(); index++) {
								Node node = nodeList.item(index);
								if(node.getNodeType() != Node.TEXT_NODE) {
									continue;
								}
								
								switch (bbPropNode.getNodeName()) {
								case "x":
									boundingBox.x = Integer.parseInt(node.getNodeValue());
									break;
								case "y":
									boundingBox.y = Integer.parseInt(node.getNodeValue());
									break;
								case "width":
									boundingBox.width = Integer.parseInt(node.getNodeValue());
									break;
								case "height":
									boundingBox.height = Integer.parseInt(node.getNodeValue());
									break;
								default:
									break;
								}
							}
						}
						
						Collection<Point> pixels = new ArrayList<>();
						for (int y = 0; y < boundingBox.height; y++) {
							for (int x = 0; x < boundingBox.width; x++) {
								if (binaryImage.getValueAt(boundingBox.x + x, boundingBox.y + y)) {
									pixels.add(new Point(boundingBox.x + x, boundingBox.y + y));
								}
							}
						}
						
						if(pixels.isEmpty()) {
							return;
						}
						
						segment = new ImageSegment(pixels);
						model.addSegment(segment);
						
						line.add(segment);
						word.add(segment);
						
						break;
					}
					
					if(segment == null) {
						continue;
					}
					
					NodeList labelNodeList = charElem.getElementsByTagName("label");
					Outer:
					for(int labelIndex = 0; labelIndex < labelNodeList.getLength(); labelIndex++) {
						Node labelNode = labelNodeList.item(labelIndex);
						if(labelNode.getNodeType() != Node.ELEMENT_NODE) {
							continue;
						}
						
						NodeList nodeList = labelNode.getChildNodes();
						for(int index = 0; index < nodeList.getLength(); index++) {
							Node node = nodeList.item(index);
							if(node.getNodeType() != Node.TEXT_NODE) {
								continue;
							}
							
							String labelString = node.getNodeValue();
							if(labelString.length() != 1) {
								continue;
							}
							
							Character label = labelString.charAt(0);
							model.setSegmentLabel(segment, label);
							break Outer;
						}
					}
					
				}
				
				words.add(word);
				word = new ArrayList<>();
			}
			
			model.addTextLine(line);
			model.addWords(words, lineIndex);
			words.clear();
			line.clear();
		}
		
	}

}
