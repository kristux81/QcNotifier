/**
 * @author krvsingh
 */
package connectors.qc.notifier.batchGen.parsers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XPathParser implements IXmlParser{

	private String xmlFile;
	private String xPath;

	public void setxPath(String xPath) {
		this.xPath = xPath;
	}

	public XPathParser(String xml) {
		xmlFile = xml;
	}
	
	public XPathParser(String xml, String xpath) {
		xmlFile = xml;
		xPath = xpath;
	}

	/*
	 * override this method to change XML parser
	 */
	public NodeList getChildNodes() throws XmlSchemaException {

		Document document = null;
		try {
			document = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(new File(xmlFile));
		} catch (SAXException e) {
			throw new XmlSchemaException("SAX Parser Exception : ", e);
		} catch (IOException e) {
			throw new XmlSchemaException("File I/O Exception : ", e);
		} catch (ParserConfigurationException e) {
			throw new XmlSchemaException("Internal Exception : ", e);
		}

		XPathExpression expression = null;
		try {
			expression = XPathFactory.newInstance().newXPath().compile(xPath);
		} catch (XPathExpressionException e) {
			throw new XmlSchemaException("Xpath Compilation Exception : ", e);
		}

		try {
			return (NodeList) expression.evaluate(document,
					XPathConstants.NODESET);

		} catch (XPathExpressionException e) {
			throw new XmlSchemaException("Xpath Compilation Exception : ", e);
		}
	}

	
	/*
	 * override this method to fetch data from element instead of attribute
	 * or different xml format
	 */
	public List<String> getValueList(NodeList nodes, String attrName) {

		List<String> list = new ArrayList<String>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node currNode = nodes.item(i);

			try {
				list.add(getNodeValue(currNode, attrName));
			} catch (Exception e) {
				continue;
			}
		}
		return list;
	}

	
	/*
	 * override this method to fetch data from element instead of attribute
	 */
	public String getNodeValue(Node node, String attributeName)
			throws XmlSchemaException {

		if(attributeName == null){
			return node.getTextContent().replaceAll("\\s+", "");	
		}
		
		if (!node.hasAttributes()) {
			throw new XmlSchemaException("Node has no attributes");
		}

		Node attr = node.getAttributes().getNamedItem(attributeName);
		if (attr == null) {
			throw new XmlSchemaException(String.format(
					"Error parsing XML, missing attribute '%s'", attributeName));
		}
		
		String ret = attr.getNodeValue().replaceAll("\\s+", "");
		if (ret == null || ret.length() == 0) {
			throw new XmlSchemaException(
					String.format("Error parsing XML, attribute '%s' is empty",
							attributeName));
		}

		return ret;
	}

}
