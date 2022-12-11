package ws.core.resource;

import java.util.ArrayList;
import java.util.Arrays;
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
import ws.core.model.OrganizationRole;
import ws.core.model.User;
import ws.core.model.filter.UserFilter;
import ws.core.model.request.ReqOrganizationRoleDelete;
import ws.core.model.request.ReqOrganizationRoleEdit;
import ws.core.model.request.ReqOrganizationRoleImportUsers;
import ws.core.repository.OrganizationRepository;
import ws.core.repository.OrganizationRoleRepository;
import ws.core.repository.OrganizationRoleRepositoryCustom;
import ws.core.repository.UserRepositoryCustom;
import ws.core.security.CustomUserDetails;
import ws.core.util.ResponseCMS;

@RestController
@RequestMapping("/admin")
public class OrganizationRoleControllerAdmin {
	private Logger log = LogManager.getLogger(OrganizationRoleControllerAdmin.class);
	
	@Autowired
	protected OrganizationRoleRepository organizationRoleRepository;
	
	@Autowired
	protected OrganizationRoleRepositoryCustom organizationRoleRepositoryCustom;
	
	@Autowired
	protected OrganizationRepository organizationRepository;
	
	@Autowired
	protected UserRepositoryCustom userRepositoryCustom;
	
	@Autowired
	protected UserControllerAdmin userControllerAdmin;
	
	@GetMapping("/organization-role/{organizationId}")
	public Object getRolesOrganiation(@PathVariable(name = "organizationId", required = true) String organizationId, @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			int total=organizationRoleRepositoryCustom.countRolesOrganization(organizationId, keyword);
			List<OrganizationRole> roles=organizationRoleRepositoryCustom.getRolesOrganization(organizationId, keyword);
			List<Document> results=new ArrayList<Document>();
			for (OrganizationRole item : roles) {
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
			return responseCMS.build();
		}
	}
	
	@GetMapping("/organization-role/get/{roleId}")
	public Object getRoleOrganization(@PathVariable(name = "roleId", required = true) String roleId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			OrganizationRole role=null;
			try {
				role=organizationRoleRepository.findById(new ObjectId(roleId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("roleId không tồn tại trong hệ thống");
				return responseCMS.build();
			}
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertRole(role));
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			return responseCMS.build();
		}
	}
	
	@PostMapping("/organization-role/create")
	public Object createOrganizationRole(@RequestBody @Valid OrganizationRole organizationRoleCreate){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			CustomUserDetails user = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			/* Kiểm tra organizationId */
			try {
				organizationRepository.findById(new ObjectId(organizationRoleCreate.organizationId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("Tố chức không tồn tại trong hệ thống");
				return responseCMS.build();
			}
			
			/* Lưu data */
			try {
				organizationRoleCreate.creatorId=user.getUser().getId();
				organizationRoleCreate.creatorName=user.getUser().fullName;
				organizationRoleCreate=organizationRoleRepository.save(organizationRoleCreate);
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage("Tên vai trò đã tồn tại trong tổ chức");
				return responseCMS.build();
			}
			
			responseCMS.setStatus(HttpStatus.CREATED);
			responseCMS.setResult(convertRole(organizationRoleCreate));
			return responseCMS.build();
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			return responseCMS.build();
		} 
	}
	
	@PutMapping("/organization-role/edit")
	public Object editOrganization(@RequestBody @Valid ReqOrganizationRoleEdit organizationRoleEdit){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra organizationId */
			try {
				organizationRepository.findById(new ObjectId(organizationRoleEdit.organizationId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("Tố chức không tồn tại trong hệ thống");
				return responseCMS.build();
			}
			
			/* Kiểm tra roleId */
			OrganizationRole roleUpdate=null;
			try {
				roleUpdate=organizationRoleRepository.findById(new ObjectId(organizationRoleEdit.id)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage(LogMessages.NOT_FOUND.getMessage());
				return responseCMS.build();
			}
			
			/* Kiểm tra roleId và organizationId có khớp không */
			if(!roleUpdate.organizationId.equalsIgnoreCase(organizationRoleEdit.organizationId)) {
				responseCMS.setStatus(HttpStatus.BAD_REQUEST);
				responseCMS.setResult("Dữ liệu cập nhật không hợp lệ");
				return responseCMS.build();
			}
			
			/* Cập nhật data */
			try {
				roleUpdate.updatedTime=organizationRoleEdit.updatedTime;
				roleUpdate.name=organizationRoleEdit.name;
				roleUpdate.description=organizationRoleEdit.description;
				roleUpdate.permissionKeys=organizationRoleEdit.permissionKeys;
				roleUpdate.userIds=organizationRoleEdit.userIds;
				roleUpdate=organizationRoleRepository.save(roleUpdate);
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage(LogMessages.CONFLICT.getMessage());
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
			return responseCMS.build();
		}
	}
	
	@DeleteMapping("/organization-role/delete")
	public Object deleteOrganizationRole(@RequestBody @Valid ReqOrganizationRoleDelete organizationRoleDelete) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra articleId */
			OrganizationRole roleDelete=null;
			try {
				roleDelete=organizationRoleRepository.findById(new ObjectId(organizationRoleDelete.id)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage(LogMessages.NOT_FOUND.getMessage());
				return responseCMS.build();
			}
			
			/* Kiểm tra roleId và organizationId có khớp không */
			if(!roleDelete.organizationId.equalsIgnoreCase(organizationRoleDelete.organizationId)) {
				responseCMS.setStatus(HttpStatus.BAD_REQUEST);
				responseCMS.setResult("Dữ liệu cập nhật không hợp lệ");
				return responseCMS.build();
			}
			
			/* Xoá article */
			organizationRoleRepository.delete(roleDelete);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult("Xóa vai trò trong tổ chức thành công");
			return responseCMS.build();
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			return responseCMS.build();
		}
	}
	
	
	/* ------------------- Organization-Role-User -------------------- */
	@GetMapping("/organization-role/get-users-in-role/{roleId}")
	public Object getUsersInRoleOrganization(@PathVariable(name = "roleId", required = true) String roleId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			OrganizationRole role=null;
			try {
				role=organizationRoleRepository.findById(new ObjectId(roleId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("roleId không tồn tại trong hệ thống");
				return responseCMS.build();
			}
			
			if(role.userIds.size()==0) {
				responseCMS.setStatus(HttpStatus.OK);
				responseCMS.setTotal(0);
				responseCMS.setResult(new ArrayList<>());
				return responseCMS.build();
			}
			
			/* Tìm những user trong organization-role */
			UserFilter userFilter=new UserFilter();
			userFilter.userIds=role.userIds;
			List<User> usersInRole=userRepositoryCustom.findAll(userFilter, 0, 0);
			List<Document> usersResult=new ArrayList<Document>();
			for (User user : usersInRole) {
				usersResult.add(userControllerAdmin.convertUser(user));
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setTotal(usersResult.size());
			responseCMS.setResult(usersResult);
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			return responseCMS.build();
		}
	}
	
	@GetMapping("/organization-role/get-users-not-in-role/{roleId}")
	public Object getUsersNotInRoleOrganization(@PathVariable(name = "roleId", required = true) String roleId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			OrganizationRole role=null;
			try {
				role=organizationRoleRepository.findById(new ObjectId(roleId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("roleId không tồn tại trong hệ thống");
				return responseCMS.build();
			}
			
			/* Tìm những user nằm trong organization, nhưng chưa có trong role */
			UserFilter userFilter=new UserFilter();
			userFilter.excludeUserIds=role.userIds;
			userFilter.organizationIds=Arrays.asList(role.organizationId);
			List<User> usersNotInRole=userRepositoryCustom.findAll(userFilter, 0, 0);
			List<Document> usersResult=new ArrayList<Document>();
			for (User user : usersNotInRole) {
				usersResult.add(userControllerAdmin.convertUser(user));
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setTotal(usersResult.size());
			responseCMS.setResult(usersResult);
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			return responseCMS.build();
		}
	}
	
	@PutMapping("/organization-role/import-users")
	public Object importUser(@RequestBody @Valid ReqOrganizationRoleImportUsers reqOrganizationRoleImportUsers){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra roleId */
			OrganizationRole organizationRole=null;
			try {
				organizationRole=organizationRoleRepository.findById(new ObjectId(reqOrganizationRoleImportUsers.roleId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.BAD_REQUEST);
				responseCMS.setMessage("Vai trò gán người dùng không tồn tại");
				return responseCMS.build();
			}
			
			/* Cập nhật data userIds cho role*/
			if(reqOrganizationRoleImportUsers.userIds.size()>0) {
				for (String userId : reqOrganizationRoleImportUsers.userIds) {
					if(organizationRole.userIds.contains(userId)==false) {
						organizationRole.userIds.add(userId);
					}
				}
				organizationRole.updatedTime=reqOrganizationRoleImportUsers.updatedTime;
				organizationRoleRepository.save(organizationRole);
			}
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("Thêm người dùng vào vai trò thành công");
			return responseCMS.build();
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			return responseCMS.build();
		}
	}
	
	@DeleteMapping("/organization-role/export-users")
	public Object exportUser(@RequestBody @Valid ReqOrganizationRoleImportUsers reqOrganizationRoleImportUsers) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra roleId */
			OrganizationRole organizationRole=null;
			try {
				organizationRole=organizationRoleRepository.findById(new ObjectId(reqOrganizationRoleImportUsers.roleId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.BAD_REQUEST);
				responseCMS.setMessage("Vai trò gán người dùng không tồn tại");
				return responseCMS.build();
			}
			
			/* Cập nhật data userIds cho role*/
			if(reqOrganizationRoleImportUsers.userIds.size()>0) {
				organizationRole.userIds.removeAll(reqOrganizationRoleImportUsers.userIds);
				organizationRole.updatedTime=reqOrganizationRoleImportUsers.updatedTime;
				organizationRoleRepository.save(organizationRole);
			}
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("Bỏ người dùng ra khỏi tổ chức thành công");
			return responseCMS.build();
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			return responseCMS.build();
		}
	}
	
	protected Document convertRole(OrganizationRole role) {
		Document document=new Document();
		document.append("createdTime", role.getCreatedTime());
		document.append("updatedTime", role.getUpdatedTime());
		document.append("id", role.getId());
		document.append("name", role.name);
		document.append("description", role.description);
		document.append("creatorId", role.creatorId);
		document.append("creatorName", role.creatorName);
		document.append("permissionKeys", role.permissionKeys);
		document.append("userIds", role.userIds);
		document.append("organizationId", role.organizationId);
		return document;
	}
	
}
