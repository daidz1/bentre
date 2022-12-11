package ws.core.resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ws.core.enums.LogMessages;
import ws.core.model.KhachHang;
import ws.core.service.QLKHService;
import ws.core.util.ResponseCMS;

@RestController
@RequestMapping("/api")
public class QLKHController {
	private Logger log = LogManager.getLogger(QLKHController.class);
	
	@Autowired
	protected QLKHService qlkhService;
	
	@GetMapping("/qlkh/get/{makh}")
	public Object getOrganization(@PathVariable(name = "makh", required = true) String makh) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			KhachHang khachHang=null;
			try {
				khachHang=qlkhService.get(makh);
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("makh không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertKhachHang(khachHang));
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	protected Document convertKhachHang(KhachHang khachHang) {
		Document document=new Document();
		document.append("makhachhang", khachHang.makhachhang);
		document.append("tenkhachhang", khachHang.tenkhachhang);
		document.append("apiurl", khachHang.apiurl);
		document.append("activeurl", khachHang.activeurl);
		return document;
	}
	
	
	
}
