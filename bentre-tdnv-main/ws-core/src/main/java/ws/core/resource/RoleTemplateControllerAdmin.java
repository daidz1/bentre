package ws.core.resource;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ws.core.enums.LogMessages;
import ws.core.model.RoleTemplate;
import ws.core.model.request.ReqRoleTemplateEdit;
import ws.core.repository.RoleTemplateRepository;
import ws.core.repository.RoleTemplateRepositoryCustom;
import ws.core.security.CustomUserDetails;
import ws.core.util.ResponseCMS;

@RestController
@RequestMapping("/admin")
public class RoleTemplateControllerAdmin {
	private Logger log = LogManager.getLogger(RoleTemplateControllerAdmin.class);
	
	@Autowired
	protected RoleTemplateRepository roleTemplateRepository;
	
	@Autowired
	protected RoleTemplateRepositoryCustom roleTemplateRepositoryCustom;
	
	@GetMapping("/role-template/list")
	public Object list(@RequestParam(name = "skip", required = true) int skip, @RequestParam(name = "limit", required = true) int limit, @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			int total=roleTemplateRepositoryCustom.countAll(keyword);
			List<RoleTemplate> roles=roleTemplateRepositoryCustom.findAll(skip, limit, keyword);
			List<Document> results=new ArrayList<Document>();
			for (RoleTemplate item : roles) {
				results.add(convertRole(item));
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
	
	@GetMapping("/role-template/get/{roleTemplateId}")
	public Object get(@PathVariable(name = "roleTemplateId", required = true) String roleTemplateId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			RoleTemplate role=null;
			try {
				role=roleTemplateRepository.findById(new ObjectId(roleTemplateId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("Vai trò mẫu không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertRole(role));
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	@PostMapping("/role-template/create")
	public Object create(@RequestBody @Valid RoleTemplate roleCreate){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			CustomUserDetails user = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			/* Lưu data */
			try {
				roleCreate.creatorId=user.getUser().getId();
				roleCreate.creatorName=user.getUser().fullName;
				roleCreate=roleTemplateRepository.save(roleCreate);
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage("Dữ liệu bị xung đột");
				return responseCMS.build();
			}
			
			responseCMS.setStatus(HttpStatus.CREATED);
			responseCMS.setResult(convertRole(roleCreate));
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
	
	@PutMapping("/role-template/edit/{roleTemplateId}")
	public Object edit(@PathVariable(name = "roleTemplateId", required = true) String roleTemplateId, @RequestBody @Valid ReqRoleTemplateEdit roleTemplateEdit){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra userId */
			RoleTemplate roleUpdate=null;
			try {
				roleUpdate=roleTemplateRepository.findById(new ObjectId(roleTemplateId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage(LogMessages.NOT_FOUND.getMessage());
				return responseCMS.build();
			}
			
			/* Cập nhật data */
			try {
				roleUpdate.updatedTime=roleTemplateEdit.updatedTime;
				roleUpdate.name=roleTemplateEdit.name;
				roleUpdate.description=roleTemplateEdit.description;
				roleUpdate.permissionKeys=roleTemplateEdit.permissionKeys;
				roleUpdate=roleTemplateRepository.save(roleUpdate);
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage("Dữ liệu cập nhật bị lỗi, hoặc xung đột dữ liệu");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertRole(roleUpdate));
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
	
	@DeleteMapping("/role-template/delete/{roleTemplateId}")
	public Object delete(@PathVariable(name = "roleTemplateId", required = true) String roleTemplateId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra articleId */
			RoleTemplate roleDelete=null;
			try {
				roleDelete=roleTemplateRepository.findById(new ObjectId(roleTemplateId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("Vai trò mẫu không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Xoá article */
			roleTemplateRepository.delete(roleDelete);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult("Đã xóa thành công");
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
	
	protected Document convertRole(RoleTemplate role) {
		Document document=new Document();
		document.append("createdTime", role.getCreatedTime());
		document.append("updatedTime", role.getUpdatedTime());
		document.append("id", role.getId());
		document.append("name", role.name);
		document.append("description", role.description);
		document.append("creatorId", role.creatorId);
		document.append("creatorName", role.creatorName);
		document.append("permissionKeys", role.permissionKeys);
		return document;
	}
	
	
	
}
