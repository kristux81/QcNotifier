package connectors.qc.notifier.batchGen.parsers;

import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public interface IXmlParser {

	/**
	 * Parse XML to Nodelist and return it
	 * 
	 * @return
	 */
	public NodeList getChildNodes();

	/**
	 * get values of given attribute in all elements of nodelist
	 * 
	 * @param nodes
	 * @param attrName
	 * @return list of attribute values 
	 */
	public List<String> getValueList(NodeList nodes, String attrName);

	/**
	 * Get value from attribute or text from leaf element 
	 * @param node
	 * @param attributeName
	 * @return
	 */
	public String getNodeValue(Node node, String attributeName);

}
