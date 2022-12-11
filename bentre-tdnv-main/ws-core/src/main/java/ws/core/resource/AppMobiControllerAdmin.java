package ws.core.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ws.core.enums.LogMessages;
import ws.core.model.AppMobi;
import ws.core.model.filter.AppMobiFilter;
import ws.core.repository.AppMobiRepository;
import ws.core.repository.AppMobiRepositoryCustom;
import ws.core.util.ResponseCMS;

@RestController
@RequestMapping("/admin")
public class AppMobiControllerAdmin {

	private Logger log = LogManager.getLogger(AppMobiControllerAdmin.class);

	@Autowired
	protected AppMobiRepository appMobiRepository;

	@Autowired
	protected AppMobiRepositoryCustom appMobiRepositoryCustom;
	
	@GetMapping("/app-user-device/count")
	public Object getCount(
			@RequestParam(name = "fromDate", required = false, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = false, defaultValue = "0") long toDate, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "active", required = false) String active) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			AppMobiFilter appMobiFilter=new AppMobiFilter();
			appMobiFilter.fromDate=fromDate;
			appMobiFilter.toDate=toDate;
			appMobiFilter.keySearch=keyword;
			appMobiFilter.active=active;
			
			int total=appMobiRepositoryCustom.countAll(appMobiFilter);
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setTotal(total);
			return responseCMS.build();
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setResult(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	@GetMapping("/app-user-device/list")
	public Object getList(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "fromDate", required = false, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = false, defaultValue = "0") long toDate, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "active", required = false) String active) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			AppMobiFilter appMobiFilter=new AppMobiFilter();
			appMobiFilter.fromDate=fromDate;
			appMobiFilter.toDate=toDate;
			appMobiFilter.keySearch=keyword;
			appMobiFilter.active=active;
			
			int total=appMobiRepositoryCustom.countAll(appMobiFilter);
			List<AppMobi> appMobies=appMobiRepositoryCustom.findAll(appMobiFilter, skip, limit);
			List<Document> results=new ArrayList<Document>();
			for (AppMobi item : appMobies) {
				results.add(convertAppMobi(item));
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setTotal(total);
			responseCMS.setResult(results);
			return responseCMS.build();
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setResult(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	@GetMapping("/app-user-device/get/{id}")
	public Object getDoc(@PathVariable(name = "id", required = true) String id) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			AppMobi appMobi=null;
			try {
				appMobi=appMobiRepository.findById(new ObjectId(id)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("id không tồn tại trong hệ thống");
				return responseCMS.build();
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertAppMobi(appMobi));
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	@PutMapping("/app-user-device/set-active/{id}")
	public Object setActive(@PathVariable(name = "id", required = true) String id,
			@RequestParam(name = "active", required = true) boolean active) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra có tồn tại không */
			AppMobi appMobi=null;
			
			try {
				appMobi=appMobiRepository.findById(new ObjectId(id)).get();
			} catch (Exception e) {
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("id ["+id+"] không tồn tại");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			appMobi.active=active;
			appMobiRepository.save(appMobi);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertAppMobi(appMobi));
			return responseCMS.build();
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	@DeleteMapping("/app-user-device/delete/{id}")
	public Object deleteTask(@PathVariable(name = "id", required = true) String id) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra articleId */
			AppMobi appMobiDelete=null;
			try {
				appMobiDelete=appMobiRepository.findById(new ObjectId(id)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("id không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			/* Xoá task */
			appMobiRepository.delete(appMobiDelete);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("Đã xóa ["+appMobiDelete.getId()+"] thành công");
			return responseCMS.build();
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	/*---------------------------------- convert --------------------------------*/
	protected Document convertAppMobi(AppMobi appMobi) {
		Document document=new Document();
		document.append("createdTime", appMobi.getCreatedTime());
		document.append("updatedTime", appMobi.getUpdatedTime());
		document.append("id", appMobi.getId());
		document.append("userId", appMobi.userId);
		document.append("deviceId", appMobi.deviceId);
		document.append("username", appMobi.username);
		document.append("fullName", appMobi.fullName);
		document.append("deviceName", appMobi.deviceName);
		document.append("longitute", appMobi.longitute);
		document.append("lagitute", appMobi.lagitute);
		document.append("active", appMobi.active);
		return document;
	}
}
