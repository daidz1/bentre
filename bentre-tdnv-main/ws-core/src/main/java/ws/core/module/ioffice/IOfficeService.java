package ws.core.module.ioffice;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Service
public class IOfficeService {
	private Logger log = LogManager.getLogger(IOfficeService.class);

	@Value("${ioffice.sync.wsURL}")
	private String wsURL;
	
	@Value("${ioffice.sync.usRoot}")
	private String usRoot;
	
	@Value("${ioffice.sync.psRoot}")
	private String pwRoot;
	
	private String getHeaderSOAP(){
		return 	"<soap:Header>\n"
				+"<AuthUser xmlns=\"http://tempuri.org/\">\n"
				+"<username>"+usRoot+"</username>\n"
				+"<password>"+pwRoot+"</password>\n"
				+"<enc />\n"
				+"</AuthUser>\n"
				+"</soap:Header>\n";
	}
	
	public boolean getFileAttachment(String pathFile, String encodeBase64){
		byte[] decoded = Base64.getDecoder().decode(encodeBase64);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(pathFile);
			fos.write(decoded);
			fos.close();
			return true;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String formatXML(String unformattedXml) {
		try {
			Document document = parseXmlFile(unformattedXml);
			OutputFormat format = new OutputFormat(document);
			format.setIndenting(true);
			format.setIndent(3);
			format.setOmitXMLDeclaration(true);
			Writer out = new StringWriter();
			XMLSerializer serializer = new XMLSerializer(out, format);
			serializer.serialize(document);
			return out.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Document parseXmlFile(String in) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(in));
			return db.parse(is);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Document obtenerDocumentDeByte(byte[] documentoXml) throws Exception {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    return builder.parse(new ByteArrayInputStream(documentoXml));
	}
	
	public int GetCountVBCDDH(String nguoinhan, int trangthai, String tungay, String denngay) throws MalformedURLException, IOException {
		String responseString = "";
		String outputString = "";
		
		URL url = new URL(wsURL);
		URLConnection connection = url.openConnection();
		HttpURLConnection httpConn = (HttpURLConnection) connection;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		String xmlInput = "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
				+ getHeaderSOAP()
				+"<soap:Body>\n"
				+"<GetCountVBCDDH xmlns=\"http://tempuri.org/\">\n"
				+"<nguoinhan>"+nguoinhan+"</nguoinhan>\n"
				+ "<trangthai>"+trangthai+"</trangthai>\n"
				+ "<tungay>"+tungay+"</tungay>\n"
				+"<denngay>"+denngay+"</denngay>\n"
				+"</GetCountVBCDDH>\n"
				+"</soap:Body>\n"
				+"</soap:Envelope>";

		byte[] buffer = new byte[xmlInput.length()];
		buffer = xmlInput.getBytes();
		bout.write(buffer);
		byte[] b = bout.toByteArray();
		String SOAPAction = "http://tempuri.org/GetCountVBCDDH";

		// Set the appropriate HTTP parameters.
		httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
		httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		httpConn.setRequestProperty("SOAPAction", SOAPAction);
		httpConn.setRequestMethod("POST");
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		OutputStream out = httpConn.getOutputStream();
		// Write the content of the request to the outputstream of the HTTP Connection.
		out.write(b);
		out.close();
		// Ready with sending the request.

		// Read the response.
		InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
		BufferedReader in = new BufferedReader(isr);

		// Write the SOAP message response to a String.
		while ((responseString = in.readLine()) != null) {
			outputString = outputString + responseString;
		}

		// Parse the String output to a org.w3c.dom.Document and be able to reach every node with the org.w3c.dom API.
		Document document = parseXmlFile(outputString);
		NodeList nodeLst = document.getElementsByTagName("GetCountVBCDDHResult");
		String countCDDH = nodeLst.item(0).getTextContent();

		return Integer.parseInt(countCDDH);
	}
	
	public PackageIOffice GetVBCDDHNew(String username, int trangthai, String tungay, String denngay) throws Exception {
		// Code to make a webservice HTTP request
		String responseString = "";
		String outputString = "";
		
		URL url = new URL(wsURL);
		URLConnection connection = url.openConnection();
		HttpURLConnection httpConn = (HttpURLConnection) connection;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		String xmlInput = "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
				+ getHeaderSOAP()
				+"<soap:Body>\n"
				+"<GetVBCDDHNew xmlns=\"http://tempuri.org/\">\n"
				+"<username>"+username+"</username>\n"
				+"<trangthai>"+trangthai+"</trangthai>\n"
				+"<tungay>"+tungay+"</tungay>\n"
				+"<denngay>"+denngay+"</denngay>\n"
				+"</GetVBCDDHNew>\n"
				+"</soap:Body>\n"
				+"</soap:Envelope>";

		byte[] buffer = new byte[xmlInput.length()];
		buffer = xmlInput.getBytes();
		bout.write(buffer);
		byte[] b = bout.toByteArray();
		String SOAPAction = "http://tempuri.org/GetVBCDDHNew";
		
		// Set the appropriate HTTP parameters.
		httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
		httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		httpConn.setRequestProperty("SOAPAction", SOAPAction);
		httpConn.setRequestMethod("POST");
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		OutputStream out = httpConn.getOutputStream();
		// Write the content of the request to the outputstream of the HTTP Connection.
		out.write(b);
		out.close();
		// Ready with sending the request.

		System.out.println("Trạng thái lấy về: "+httpConn.getResponseCode());
		
		// Read the response.
		InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
		BufferedReader in = new BufferedReader(isr);

		// Write the SOAP message response to a String.
		while ((responseString = in.readLine()) != null) {
			outputString = outputString + responseString;
		}
		
		// Parse the String output to a org.w3c.dom.Document and be able to reach every node with the org.w3c.dom API.
		Document document = parseXmlFile(outputString);
		NodeList nodeLst = document.getElementsByTagName("GetVBCDDHNewResult");
		Node nNode = nodeLst.item(0);
		Element element = (Element) nNode;
		
		/*Kiểm tra gói tin trả về có nội dung không*/
		if(nodeLst.getLength()==0){
			System.out.println("- Không có gói tin nào mới!");
			return null;
		}
		
		try {
			String string1 = element.getElementsByTagName("string").item(0).getTextContent();

			byte[] decoded = Base64.getDecoder().decode(string1);
			
			document = obtenerDocumentDeByte(decoded);
			
			ParserXMLToBean parserXMLToBean = new ParserXMLToBean(document);
			
			PackageIOffice packageIOffice = new PackageIOffice();
			packageIOffice.setModelVanBanPhatHanh(parserXMLToBean.getVanbanModel());
			packageIOffice.setAttachment(parserXMLToBean.isHasAttachment());
			if(packageIOffice.isAttachment()){
				packageIOffice.setModelVanBanDinhKem(parserXMLToBean.getModelVanBanDinhKem());
			}
			
			String idPackage = element.getElementsByTagName("string").item(1).getTextContent();
			packageIOffice.setIdGoiTin(idPackage);
			packageIOffice.getModelVanBanPhatHanh().setIdGoiTin(idPackage);
			
			return packageIOffice;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean UpdateTTVBCDDH(int idgoitin, String nguoinhan,int trangthai) throws MalformedURLException, IOException {
		log.info("Xoá văn bản IOFFICE, idGoiTin: "+idgoitin+", nguoinhan: "+nguoinhan+", trangthai: "+trangthai);
		// Code to make a webservice HTTP request
		String responseString = "";
		String outputString = "";

		URL url = new URL(wsURL);
		URLConnection connection = url.openConnection();
		HttpURLConnection httpConn = (HttpURLConnection) connection;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		String xmlInput = "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
				+ getHeaderSOAP()
				+"<soap:Body>\n"
				+"<UpdateTTVBCDDH xmlns=\"http://tempuri.org/\">\n"
				+"<idgoitin>"+idgoitin+"</idgoitin>\n"
				+ "<nguoinhan>"+nguoinhan+"</nguoinhan>\n"
				+ "<trangthai>"+trangthai+"</trangthai>\n"
				+"</UpdateTTVBCDDH>\n"
				+"</soap:Body>\n"
				+"</soap:Envelope>";

		byte[] buffer = new byte[xmlInput.length()];
		buffer = xmlInput.getBytes();
		bout.write(buffer);
		byte[] b = bout.toByteArray();
		String SOAPAction = "http://tempuri.org/UpdateTTVBCDDH";

		// Set the appropriate HTTP parameters.
		httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
		httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		httpConn.setRequestProperty("SOAPAction", SOAPAction);
		httpConn.setRequestMethod("POST");
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		OutputStream out = httpConn.getOutputStream();
		// Write the content of the request to the outputstream of the HTTP Connection.
		out.write(b);
		out.close();
		// Ready with sending the request.

		// Read the response.
		InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
		BufferedReader in = new BufferedReader(isr);

		// Write the SOAP message response to a String.
		while ((responseString = in.readLine()) != null) {
			outputString = outputString + responseString;
		}

		// Parse the String output to a org.w3c.dom.Document and be able to reach every node with the org.w3c.dom API.
		Document document = parseXmlFile(outputString);
		NodeList nodeLst = document.getElementsByTagName("UpdateTTVBCDDHResult");
		String response = nodeLst.item(0).getTextContent();
		if(response.equalsIgnoreCase("Complete")) {
			log.info(".... không thành công");
			return true;
		}
		
		log.info(".... thành công");
		return false;
	}
	
	public boolean UpdateTTVBCBByIDGoiTin(int idgoitin, String nguoinhan,int trangthai) throws MalformedURLException, IOException {
		// Code to make a webservice HTTP request
		String responseString = "";
		String outputString = "";

		URL url = new URL(wsURL);
		URLConnection connection = url.openConnection();
		HttpURLConnection httpConn = (HttpURLConnection) connection;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		String xmlInput = "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
				+ getHeaderSOAP()
				+"<soap:Body>\n"
				+"<UpdateTTVBCBByIDGoiTin xmlns=\"http://tempuri.org/\">\n"
				+"<idgoitin>"+idgoitin+"</idgoitin>\n"
				+ "<nguoinhan>"+nguoinhan+"</nguoinhan>\n"
				+ "<trangthai>"+trangthai+"</trangthai>\n"
				+"</UpdateTTVBCBByIDGoiTin>\n"
				+"</soap:Body>\n"
				+"</soap:Envelope>";

		byte[] buffer = new byte[xmlInput.length()];
		buffer = xmlInput.getBytes();
		bout.write(buffer);
		byte[] b = bout.toByteArray();
		String SOAPAction = "http://tempuri.org/UpdateTTVBCBByIDGoiTin";

		// Set the appropriate HTTP parameters.
		httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
		httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		httpConn.setRequestProperty("SOAPAction", SOAPAction);
		httpConn.setRequestMethod("POST");
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		OutputStream out = httpConn.getOutputStream();
		// Write the content of the request to the outputstream of the HTTP Connection.
		out.write(b);
		out.close();
		// Ready with sending the request.

		// Read the response.
		InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
		BufferedReader in = new BufferedReader(isr);

		// Write the SOAP message response to a String.
		while ((responseString = in.readLine()) != null) {
			outputString = outputString + responseString;
		}

		// Parse the String output to a org.w3c.dom.Document and be able to reach every node with the org.w3c.dom API.
		Document document = parseXmlFile(outputString);
		NodeList nodeLst = document.getElementsByTagName("UpdateTTVBCBByIDGoiTinResult");
		String response = nodeLst.item(0).getTextContent();
		if(response=="Complete")
			return true;

		return false;
	}
	
	public String getIPAddress() throws MalformedURLException, IOException {
		// Code to make a webservice HTTP request
		String responseString = "";
		String outputString = "";

		URL url = new URL(wsURL);
		URLConnection connection = url.openConnection();
		HttpURLConnection httpConn = (HttpURLConnection) connection;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		String xmlInput = "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
				+ getHeaderSOAP()
				+"<soap:Body>\n"
				+"<getIPAddress xmlns=\"http://tempuri.org/\"/>\n"
				+"</soap:Body>\n"
				+"</soap:Envelope>";

		byte[] buffer = new byte[xmlInput.length()];
		buffer = xmlInput.getBytes();
		bout.write(buffer);
		byte[] b = bout.toByteArray();
		String SOAPAction = "http://tempuri.org/getIPAddress";

		// Set the appropriate HTTP parameters.
		httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
		httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		httpConn.setRequestProperty("SOAPAction", SOAPAction);
		httpConn.setRequestMethod("POST");
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		OutputStream out = httpConn.getOutputStream();
		// Write the content of the request to the outputstream of the HTTP Connection.
		out.write(b);
		out.close();
		// Ready with sending the request.

		// Read the response.
		InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
		BufferedReader in = new BufferedReader(isr);

		// Write the SOAP message response to a String.
		while ((responseString = in.readLine()) != null) {
			outputString = outputString + responseString;
		}

		// Parse the String output to a org.w3c.dom.Document and be able to reach every node with the org.w3c.dom API.
		Document document = parseXmlFile(outputString);
		NodeList nodeLst = document.getElementsByTagName("getIPAddressResult");
		String response = nodeLst.item(0).getTextContent();
		System.out.println(response);

		return response;
	}
	
	public boolean getauthentication() throws MalformedURLException, IOException {
		// Code to make a webservice HTTP request
		String responseString = "";
		String outputString = "";

		URL url = new URL(wsURL);
		URLConnection connection = url.openConnection();
		HttpURLConnection httpConn = (HttpURLConnection) connection;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		String xmlInput = "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
				+ getHeaderSOAP()
				+"<soap:Body>\n"
				+"<getauthentication xmlns=\"http://tempuri.org/\"/>\n"
				+"</soap:Body>\n"
				+"</soap:Envelope>";

		byte[] buffer = new byte[xmlInput.length()];
		buffer = xmlInput.getBytes();
		bout.write(buffer);
		byte[] b = bout.toByteArray();
		String SOAPAction = "http://tempuri.org/getauthentication";

		System.out.println("xmlInput: "+xmlInput);
		
		// Set the appropriate HTTP parameters.
		httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
		httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		httpConn.setRequestProperty("SOAPAction", SOAPAction);
		httpConn.setRequestMethod("POST");
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		OutputStream out = httpConn.getOutputStream();
		// Write the content of the request to the outputstream of the HTTP Connection.
		out.write(b);
		out.close();
		// Ready with sending the request.

		// Read the response.
		InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
		BufferedReader in = new BufferedReader(isr);

		// Write the SOAP message response to a String.
		while ((responseString = in.readLine()) != null) {
			outputString = outputString + responseString;
		}

		// Parse the String output to a org.w3c.dom.Document and be able to reach every node with the org.w3c.dom API.
		Document document = parseXmlFile(outputString);
		NodeList nodeLst = document.getElementsByTagName("getauthenticationResult");
		String response = nodeLst.item(0).getTextContent();
		
		if(response.equalsIgnoreCase("true"))
			return true;
		return false;
	}
}
