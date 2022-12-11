package vn.com.ngn.site.util;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.util.component.NotificationUtil;

public class ReadXMLUtil {
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder;
	Document document;

	XPath xpath = XPathFactory.newInstance().newXPath();
	
	String xmlPath;
	InputStream xmlInputStream;
	
	public ReadXMLUtil(String xmlPath) {
		this.xmlPath = xmlPath;
	}
	
	public ReadXMLUtil(InputStream xmlInputStream) {
		this.xmlInputStream = xmlInputStream;
	}
	
	public void getXMLByPath() throws Exception {
		File fXmlFile = new File(xmlPath);
		if(fXmlFile.exists()) {
			dBuilder = dbFactory.newDocumentBuilder();
			document = dBuilder.parse(fXmlFile);
			document.getDocumentElement().normalize();
		} else {
			NotificationUtil.showNotifi("Template file doesn't exist", NotificationTypeEnum.ERROR);
		}
	}
	
	public void getXMLByInputStream() throws Exception
	{
		dBuilder = dbFactory.newDocumentBuilder();
		document = dBuilder.parse(xmlInputStream);
		document.getDocumentElement().normalize();
	}
	
	public Element getElementByAttribute(Element eleParent,String eleName,String attrName,String value) throws XPathExpressionException {
		Element element = (Element) xpath.evaluate(".//"+eleName+"[@"+attrName+"='"+value+"']",eleParent, XPathConstants.NODE);
		return element;
	}
	
	public NodeList getNodeListByAttribute(Element eleParent,String eleName,String attrName,String value) throws XPathExpressionException {
		NodeList nodeList = (NodeList) xpath.evaluate("//"+eleName+"[@"+attrName+"='"+value+"']", eleParent!=null ? eleParent : document, XPathConstants.NODESET);
	
		return nodeList;
	}
	
	public void saveXMLByPath() throws Exception
	{
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();

		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,"yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");

		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new File(xmlPath));
		transformer.transform(source, result);
	}

	public Document getDocument() {
		return document;
	}
	public XPath getXpath() {
		return xpath;
	}
}
