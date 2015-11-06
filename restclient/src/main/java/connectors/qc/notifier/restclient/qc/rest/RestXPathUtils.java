/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient.qc.rest;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import connectors.qc.notifier.restclient.commons.StringUtils;

public final class RestXPathUtils {

	private static final String SINGLE_ENTITY = "Entity/Fields/Field";
	private static final String MULTI_ENTITY = "Entities/Entity/Fields/Field";
	private static final String MULTI_RELATED_ENTITY = "Entities/Entity/RelatedEntities/Relation/Entity/Fields/Field";

	// no object instantiation for this class
	private RestXPathUtils() {
	}

	public static List<Map<String, String>> toEntities(String xml) {

		Document document = getDocument(xml);

		List<Map<String, String>> ret = new ArrayList<Map<String, String>>();
		NodeList entities = document.getElementsByTagName("Entity");
		for (int i = 0; i < entities.getLength(); i++) {
			Map<String, String> currEntity = new HashMap<String, String>();
			NodeList fields = ((Element) entities.item(i))
					.getElementsByTagName("Field");
			for (int j = 0; j < fields.getLength(); j++) {
				Node item = fields.item(j);
				currEntity.put(item.getAttributes().item(0).getNodeValue(),
						getFieldValue(item));
			}
			ret.add(currEntity);
		}

		return ret;
	}

	public static String getAttributeValueByXPath(String xml, String attrName,
			String xpath) {

		String ret = StringUtils.EMPTY_STRING;

		NodeList nodes = getChildNodes(xml, xpath);
		for (int i = 0; i < nodes.getLength(); i++) {
			Node currNode = nodes.item(i);
			String attr;
			try {
				attr = getNecessaryAttribute(currNode, "Name");
			} catch (Exception cause) {
				throw new RestException(cause);
			}
			if (attr.equals(attrName)) {
				ret = getFieldValue(currNode);
				break;
			}
		}

		return ret;
	}

	public static String getAttributeValue(String xml, String attrName) {

		return getAttributeValueByXPath(xml, attrName, MULTI_ENTITY);
	}

	public static String getRelatedAttributeValue(String xml, String attrName) {

		return getAttributeValueByXPath(xml, attrName, MULTI_RELATED_ENTITY);
	}

	public static String getAttributeValueSingle(String xml, String attrName) {

		return getAttributeValueByXPath(xml, attrName, SINGLE_ENTITY);
	}

	public static String getQCExceptionElementText(String xml, String nodeName) {

		Document doc = getDocument(xml);
		NodeList nodes = doc.getElementsByTagName(nodeName);

		// if element nodeName found return text else return full exception
		String error = xml.substring(xml.indexOf("?>") + "?>".length());

		if (nodes.getLength() == 1) {
			Node currNode = nodes.item(0);
			if (currNode != null) {
				error = currNode.getTextContent();
			}
		}

		return error;
	}

	public static List<String> getAttributeValueByXPathForAll(String xml,
			String attrName, String xpath) {

		List<String> list = new ArrayList<String>();

		NodeList nodes = getChildNodes(xml, xpath);
		for (int i = 0; i < nodes.getLength(); i++) {
			Node currNode = nodes.item(i);
			String attr;
			try {
				attr = getNecessaryAttribute(currNode, "Name");
			} catch (Exception cause) {
				throw new RestException(cause);
			}
			if (attr.equals(attrName)) {
				list.add(getFieldValue(currNode));
			}
		}

		return list;
	}

	public static List<String> getAttributeValueForAll(String xml,
			String attrName) {

		return getAttributeValueByXPathForAll(xml, attrName, MULTI_ENTITY);
	}

	public static List<String> getRelatedAttributeValueForAll(String xml,
			String attrName) {

		return getAttributeValueByXPathForAll(xml, attrName,
				MULTI_RELATED_ENTITY);
	}

	private static String getFieldValue(Node node) {

		String ret = null;
		Node child = node.getFirstChild();
		if (child != null) {
			Node child2 = child.getFirstChild();
			if (child2 != null) {
				ret = child2.getNodeValue();
			}
		}

		return ret;
	}

	private static NodeList getChildNodes(String xml, String xpath) {

		NodeList ret = null;
		try {
			Document document = getDocument(xml);
			XPathFactory factory = XPathFactory.newInstance();
			XPathExpression expression = factory.newXPath().compile(xpath);
			ret = (NodeList) expression.evaluate(document,
					XPathConstants.NODESET);
		} catch (Exception cause) {
			throw new RestException(cause);
		}

		return ret;
	}

	private static String getNecessaryAttribute(Node node, String attributeName) {

		if (!node.hasAttributes()) {
			return null;
		}
		Node attr = node.getAttributes().getNamedItem(attributeName);
		if (attr == null) {
			throw new RestException(String.format(
					"Error parsing XML, missing mandatory attribute '%s'",
					attributeName));
		}
		String ret = attr.getNodeValue();
		if (StringUtils.isNotDefined(ret)) {
			throw new RestException(
					String.format(
							"Error parsing XML, mandatory attribute '%s' cannot be empty",
							attributeName));
		}

		return ret;
	}

	private static Document getDocument(String xml) {

		Document ret = null;
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			InputSource inputSource = new InputSource();
			inputSource.setCharacterStream(new StringReader(xml));
			ret = builder.parse(inputSource);
		} catch (Exception cause) {
			throw new RestException(cause);
		}

		return ret;
	}

	public static int getResultEntitiesCount(String xml) {

		int count = 0;
		NodeList nodes = getChildNodes(xml, "Entities");

		if (nodes.getLength() == 1) {
			Node node = nodes.item(0).getAttributes()
					.getNamedItem("TotalResults");
			count = Integer.parseInt(node.getNodeValue());
		}
		return count;
	}
}
