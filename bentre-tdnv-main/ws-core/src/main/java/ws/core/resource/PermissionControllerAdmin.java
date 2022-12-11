package ws.core.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ws.core.enums.LogMessages;
import ws.core.model.Permission;
import ws.core.repository.PermissionRepository;
import ws.core.repository.PermissionRepositoryCustom;
import ws.core.util.ResponseCMS;

@RestController
@RequestMapping("/admin")
public class PermissionControllerAdmin {
	private Logger log = LogManager.getLogger(PermissionControllerAdmin.class);
	
	@Autowired
	protected PermissionRepository permissionRepository;
	
	@Autowired
	protected PermissionRepositoryCustom permissionRepositoryCustom;
	
	@GetMapping("/permission/list")
	public Object getList(@RequestParam(name = "permissionKeys", required = false, defaultValue = "") String permissionKeys) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			List<Permission> permissions=permissionRepositoryCustom.getList(permissionKeys);
			List<Document> results=new ArrayList<Document>();
			for (Permission item : permissions) {
				results.add(convertPermission(item));
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setTotal(results.size());
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
	
	@GetMapping("/permission/get/{permissionKey}")
	public Object getOrganization(@PathVariable(name = "permissionKey", required = true) String permissionKey) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			Permission permission=null;
			try {
				permission=permissionRepository.findByKey(permissionKey).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("permissionKey không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertPermission(permission));
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	protected Document convertPermission(Permission permission) {
		Document document=new Document();
		document.append("id", permission.getId());
		document.append("key", permission.key);
		document.append("name", permission.name);
		document.append("description", permission.description);
		document.append("orderSort", permission.order);
		
		document.append("groupId", permission.groupId);
		document.append("groupName", permission.groupName);
		document.append("groupSort", permission.groupOrder);
		return document;
	}
	
	
	
}
