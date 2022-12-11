package ws.core.module.ioffice;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("unused")
public class ParserXMLToBean {
	private VanbanModel vanbanModel = new VanbanModel();
	private List<VanbanDKModel> modelVanBanDinhKem = new ArrayList<VanbanDKModel>();

	private DocumentBuilderFactory dbFactory;
	private DocumentBuilder dBuilder;
	private Document doc;
	private Element rootElement;

	private TransformerFactory transformerFactory;
	private Transformer transformer;
	private DOMSource source;
	private StreamResult result;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

	public ParserXMLToBean(Document document) {
		this.doc = document;
		try {
			initXML();
			getVanBanPhatHanhFromXML();
			if(isHasAttachment())
				getVanBanDinhKemFromXML();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getVanBanPhatHanhFromXML() throws Exception{
		//Document id
		int DocumentId = Integer.parseInt(rootElement.getElementsByTagName("edXML:DocumentId").item(0).getTextContent().trim());

		//id va ten don vi phat hanh
		String idDonViPhatHanh = rootElement.getElementsByTagName("edXML:OrganId").getLength() > 0 ? rootElement.getElementsByTagName("edXML:OrganId").item(0).getTextContent().trim() : "";
		String tenDonViPhatHanh = rootElement.getElementsByTagName("edXML:OrganName").getLength() > 0 ? rootElement.getElementsByTagName("edXML:OrganName").item(0).getTextContent().trim() : "UBND Tỉnh Bến Tre";

		//so di va ky hieu
		String soDi = rootElement.getElementsByTagName("edXML:CodeNumber").getLength() > 0 ? rootElement.getElementsByTagName("edXML:CodeNumber").item(0).getTextContent().trim() : "0";
		String kyHieu = rootElement.getElementsByTagName("edXML:CodeNotation").getLength() > 0 ? rootElement.getElementsByTagName("edXML:CodeNotation").item(0).getTextContent().trim() : "NA";

		//noi phat hanh va ngay phat hanh
		Date ngayPhatHanh =rootElement.getElementsByTagName("edXML:PromulgationDate").getLength() > 0 ? sdf.parse(rootElement.getElementsByTagName("edXML:PromulgationDate").item(0).getTextContent().trim()) : new Date();

		//loai van ban va ten van ban
		//Node nodeDocumentType = rootElement.getElementsByTagName("edXML:DocumentType").item(0);
		int loaiVanBan = rootElement.getElementsByTagName("edXML:Type").getLength() > 0 ? !rootElement.getElementsByTagName("edXML:Type").item(0).getTextContent().trim().isEmpty() ? Integer.parseInt(rootElement.getElementsByTagName("edXML:Type").item(0).getTextContent().trim()) : 0 : 0;
		String tenVanBan = rootElement.getElementsByTagName("edXML:TypeName").getLength() > 0 ? rootElement.getElementsByTagName("edXML:TypeName").item(0).getTextContent().trim() : "";

		//trich yeu
		String trichYeu = rootElement.getElementsByTagName("edXML:Subject").getLength() > 0 ? rootElement.getElementsByTagName("edXML:Subject").item(0).getTextContent().trim() : "";

		//trich yeu
		String noiDung = rootElement.getElementsByTagName("edXML:Content").getLength() > 0 ? rootElement.getElementsByTagName("edXML:Content").item(0).getTextContent().trim() : "";

		//quyen han, chuc vu, ho ten nguoi ky
		String chucVuNguoiKy = rootElement.getElementsByTagName("edXML:Position").getLength() > 0 ? rootElement.getElementsByTagName("edXML:Position").item(0).getTextContent().trim() : "";
		String hoTenNguoiKy = rootElement.getElementsByTagName("edXML:FullName").getLength() > 0 ? rootElement.getElementsByTagName("edXML:FullName").item(0).getTextContent().trim() : "";

		//han xu ly
		Date hanXuLy = rootElement.getElementsByTagName("edXML:DueDate").getLength() > 0 ? sdf.parse(rootElement.getElementsByTagName("edXML:DueDate").item(0).getTextContent().trim()) : null;

		//noi nhan
		String noiNhan = "";
		NodeList nodeListToPlace = rootElement.getElementsByTagName("edXML:ToPlaces").item(0).getChildNodes();
		for(int i = 0;i<nodeListToPlace.getLength();i++){
			Node node = nodeListToPlace.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String textTmp = nodeListToPlace.item(i).getTextContent().trim().trim();
				if(!textTmp.isEmpty())
					noiNhan+=textTmp+",";
			}
		}
		if(noiNhan.contains(","))
			noiNhan = noiNhan.substring(0,noiNhan.length()-1);

		// other info node
		int doKhan = rootElement.getElementsByTagName("edXML:Priority").getLength() > 0 ? !rootElement.getElementsByTagName("edXML:Priority").item(0).getTextContent().trim().isEmpty() ? Integer.parseInt(rootElement.getElementsByTagName("edXML:Priority").item(0).getTextContent().trim()) : 0 : 0;
		int soLuongBanPhatHanh = rootElement.getElementsByTagName("edXML:PromulgationAmount").getLength() > 0 ? !rootElement.getElementsByTagName("edXML:PromulgationAmount").item(0).getTextContent().trim().isEmpty() ? Integer.parseInt(rootElement.getElementsByTagName("edXML:PromulgationAmount").item(0).getTextContent().trim()) : 1 : 1;
		int soTrangCuaVanBan = rootElement.getElementsByTagName("edXML:PageAmount").getLength() > 0 ? !rootElement.getElementsByTagName("edXML:PageAmount").item(0).getTextContent().trim().isEmpty() ? Integer.parseInt(rootElement.getElementsByTagName("edXML:PageAmount").item(0).getTextContent().trim()) : 1 : 1;

		// set value cho model
		vanbanModel.setIdIOffice(DocumentId+"");
		vanbanModel.setIdDonvibanhanh(-1);
		vanbanModel.setTenDonvibanhanh(tenDonViPhatHanh);
		vanbanModel.setSoHieu(soDi);
		vanbanModel.setKyHieu(kyHieu);
		vanbanModel.setNgayBanhanh(ngayPhatHanh);
		vanbanModel.setIdLoaivanban(-1);
		vanbanModel.setTenLoaivanban(tenVanBan);
		vanbanModel.setTrichYeu(trichYeu);
		vanbanModel.setNoiDung(noiDung);
		vanbanModel.setIdChucvuNguoiky(-1);
		vanbanModel.setTenChucVuNguoiky(chucVuNguoiKy);
		vanbanModel.setIdNguoiky(-1);
		vanbanModel.setTenNguoiky(hoTenNguoiKy);
		vanbanModel.setHanXuly(hanXuLy);
		vanbanModel.setNoiNhan(noiNhan);
		vanbanModel.setDoKhan(doKhan);
		vanbanModel.setSoLuongBanPhatHanh(soLuongBanPhatHanh);
		vanbanModel.setSoTrangCuaVanBan(soTrangCuaVanBan);
	}

	private void getVanBanDinhKemFromXML() throws Exception{
		NodeList nodeAttachment = rootElement.getElementsByTagName("AttachmentEncoded").item(0).getChildNodes();
		for(int i = 0; i<nodeAttachment.getLength();i++){
			if(nodeAttachment.item(i).getNodeName().equalsIgnoreCase("Attachment")){
				Node nNode = nodeAttachment.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					VanbanDKModel modelVanBanDinhKem=new VanbanDKModel();
					NodeList nodeAttachmentField = nNode.getChildNodes(); 
					for(int j=0;j<nodeAttachmentField.getLength();j++){
						Node node = nodeAttachmentField.item(j);
						if (node.getNodeType() == Node.ELEMENT_NODE) {
							switch (node.getNodeName()) {
								case "ContentType":
									modelVanBanDinhKem.setLoaiDinhKem(node.getTextContent().trim());
									break;
								case "ContentId":
									modelVanBanDinhKem.setIdDinhKem(!node.getTextContent().trim().isEmpty()?Integer.parseInt(node.getTextContent().trim()):0);
									break;
								case "Description":
									modelVanBanDinhKem.setMieuTaDinhKem(node.getTextContent().trim());
									break;
								case "ContentTransferEncoded":
									byte[] decoded = Base64.getMimeDecoder().decode(node.getTextContent().getBytes(StandardCharsets.UTF_8));
									modelVanBanDinhKem.setNoiDungDinhKem((decoded));
									break;
								case "AttachmentName":
									modelVanBanDinhKem.setTenDinhKem(node.getTextContent().trim());
									break;
							}
						}
					}
					int DocumentId = Integer.parseInt(rootElement.getElementsByTagName("edXML:DocumentId").item(0).getTextContent().trim());
					modelVanBanDinhKem.setIdVanbanIOffice(DocumentId);
					this.modelVanBanDinhKem.add(modelVanBanDinhKem);
				}
			}
		}
	}
	
	public boolean isHasAttachment(){
		int length = rootElement.getElementsByTagName("Attachment").getLength();
		if(length>0)
			return true;
		return false;
	}

	private void initXML() throws Exception{
		dbFactory = DocumentBuilderFactory.newInstance();
		dBuilder = dbFactory.newDocumentBuilder();
		transformerFactory = TransformerFactory.newInstance();
		transformer = transformerFactory.newTransformer();

		rootElement = doc.getDocumentElement();
	}

	public VanbanModel getVanbanModel() {
		return vanbanModel;
	}

	public List<VanbanDKModel> getModelVanBanDinhKem() {
		return modelVanBanDinhKem;
	}
	
	public byte[] deflate(byte[] data) throws IOException, DataFormatException {
	    Inflater inflater = new Inflater();
	    inflater.setInput(data);
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
	    byte[] buffer = new byte[1024];
	    while (!inflater.finished()) {
	        int count = inflater.inflate(buffer);
	        outputStream.write(buffer, 0, count);
	    }
	    outputStream.close();
	    byte[] output = outputStream.toByteArray();
	    return output;
	}
	
	public byte[] decompressByteArray(byte[] bytes){
        ByteArrayOutputStream baos = null;
        Inflater iflr = new Inflater();
        iflr.setInput(bytes);
        baos = new ByteArrayOutputStream();
        byte[] tmp = new byte[4*1024];
        try{
            while(!iflr.finished()){
                int size = iflr.inflate(tmp);
                baos.write(tmp, 0, size);
            }
        } catch (Exception ex){
             
        } finally {
            try{
                if(baos != null) baos.close();
            } catch(Exception ex){}
        }
         
        return baos.toByteArray();
    }
}
