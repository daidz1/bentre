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
import ws.core.model.Organization;
import ws.core.model.OrganizationRole;
import ws.core.model.User;
import ws.core.model.embeded.UserOrganizationExpand;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.filter.OrganizationRoleFilter;
import ws.core.model.filter.UserFilter;
import ws.core.model.request.ReqOrganizationEdit;
import ws.core.model.request.ReqOrganizationImportUsers;
import ws.core.repository.OrganizationRepository;
import ws.core.repository.OrganizationRepositoryCustom;
import ws.core.repository.OrganizationRoleRepository;
import ws.core.repository.OrganizationRoleRepositoryCustom;
import ws.core.repository.UserRepository;
import ws.core.repository.UserRepositoryCustom;
import ws.core.security.CustomUserDetails;
import ws.core.service.OrganizationService;
import ws.core.util.ResponseCMS;

@RestController
@RequestMapping("/admin")
public class OrganizationControllerAdmin {
	private Logger log = LogManager.getLogger(OrganizationControllerAdmin.class);
	
	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	protected UserRepositoryCustom userRepositoryCustom;
	
	@Autowired
	protected OrganizationRepository organizationRepository;
	
	@Autowired
	protected OrganizationRepositoryCustom organizationRepositoryCustom;
	
	@Autowired
	protected OrganizationRoleRepositoryCustom organizationRoleRepositoryCustom;
	
	@Autowired
	protected OrganizationService organizationService;
	
	@Autowired
	protected OrganizationRoleRepository organizationRoleRepository;
	
	@GetMapping("/organization/list")
	public Object getList(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "parentid", required = false, defaultValue = "") String parentId,
			@RequestParam(name = "keyword", required = false) String keyword) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.parentId=parentId;
			organizationFilter.keySearch=keyword;
			
			long total=organizationRepositoryCustom.countAll(organizationFilter);
			List<Organization> users=organizationRepositoryCustom.findAll(organizationFilter, skip, limit);
			List<Document> results=new ArrayList<Document>();
			for (Organization item : users) {
				results.add(convertOrganization(item));
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
	
	@GetMapping("/organization/count")
	public Object getCount(
			@RequestParam(name = "parentid", required = false, defaultValue = "") String parentId,
			@RequestParam(name = "keyword", required = false) String keyword) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.parentId=parentId;
			organizationFilter.keySearch=keyword;
			
			long total=organizationRepositoryCustom.countAll(organizationFilter);
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
	
	@GetMapping("/organization/get/{organizationId}")
	public Object getOrganization(@PathVariable(name = "organizationId", required = true) String organizationId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			Organization organization=null;
			try {
				organization=organizationRepository.findById(new ObjectId(organizationId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("T??? ch???c kh??ng t???n t???i trong h??? th???ng");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertOrganization(organization));
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	@PostMapping("/organization/create")
	public Object createOrganization(@RequestBody @Valid Organization createOrganization){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			CustomUserDetails user = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			/* Ki???m tra n???u c?? parentId */
			if(createOrganization.parentId!=null && !createOrganization.parentId.isEmpty()) {
				try {
					organizationRepository.findById(new ObjectId(createOrganization.parentId)).get();
				} catch (Exception e) {
					e.printStackTrace();
					responseCMS.setStatus(HttpStatus.BAD_REQUEST);
					responseCMS.setMessage("T??? ch???c c???p cha kh??ng t???n t???i");
					responseCMS.setError(e.getMessage());
					return responseCMS.build();
				}
			}
			
			/* Get path organzation */
			String path="";
			try {
				path=organizationService.getPath(createOrganization);
			} catch (Exception e) {
				e.printStackTrace();
				responseCMS.setStatus(HttpStatus.BAD_REQUEST);
				responseCMS.setMessage(e.getMessage());
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* L??u data */
			try {
				createOrganization.creatorId=user.getUser().getId();
				createOrganization.creatorName=user.getUser().fullName;
				createOrganization.path=path;
				createOrganization=organizationRepository.save(createOrganization);
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage(e.getMessage());
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			responseCMS.setStatus(HttpStatus.CREATED);
			responseCMS.setResult(convertOrganization(createOrganization));
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
	
	@PutMapping("/organization/edit/{organizationId}")
	public Object editOrganization(@PathVariable(name = "organizationId", required = true) String organizationId, @RequestBody @Valid ReqOrganizationEdit organizationEdit){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Ki???m tra organizationId */
			Organization organizationUpdate=null;
			try {
				organizationUpdate=organizationRepository.findById(new ObjectId(organizationId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage(LogMessages.NOT_FOUND.getMessage());
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Ki???m tra n???u c?? parentId */
			if(organizationEdit.parentId!=null && !organizationEdit.parentId.isEmpty()) {
				try {
					organizationRepository.findById(new ObjectId(organizationEdit.parentId)).get();
				} catch (Exception e) {
					e.printStackTrace();
					responseCMS.setStatus(HttpStatus.BAD_REQUEST);
					responseCMS.setMessage("parentId not exists");
					responseCMS.setError(e.getMessage());
					return responseCMS.build();
				}
			}
			
			/* Get path organzation */
			String path="";
			try {
				organizationUpdate.parentId=organizationEdit.parentId;
				path=organizationService.getPath(organizationUpdate);
			} catch (Exception e) {
				e.printStackTrace();
				responseCMS.setStatus(HttpStatus.BAD_REQUEST);
				responseCMS.setMessage(e.getMessage());
				return responseCMS.build();
			}
			
			/* C???p nh???t data */
			try {
				organizationUpdate.updatedTime=organizationEdit.updatedTime;
				organizationUpdate.name=organizationEdit.name;
				organizationUpdate.description=organizationEdit.description;
				organizationUpdate.description=organizationEdit.description;
				organizationUpdate.path=path;
				organizationUpdate.active=organizationEdit.active;
				organizationUpdate.numberOrder=organizationEdit.numberOrder;
				organizationUpdate=organizationRepository.save(organizationUpdate);
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage(LogMessages.CONFLICT.getMessage());
				return responseCMS.build();
			}
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertOrganization(organizationUpdate));
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
	
	@DeleteMapping("/organization/delete/{organizationId}")
	public Object deleteOrganization(@PathVariable(name = "organizationId", required = true) String organizationId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Ki???m tra organizationId */
			Organization organizationDelete=null;
			try {
				organizationDelete=organizationRepository.findById(new ObjectId(organizationId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("T??? ch???c kh??ng t???n t???i");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Ki???m tra c??n ph??ng ban con thu???c organizationId kh??ng? */
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.parentId=organizationId;
			if(organizationRepositoryCustom.countAll(organizationFilter)>0) {
				responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setMessage("Kh??ng th??? x??a ????n v??? n??y, v?? c??n ????n v??? con ??ang tr???c thu???c");
				return responseCMS.build();
			}
			
			/* Ki???m tra ????n v??? c??n ng?????i d??ng kh??ng */
			UserFilter userFilter=new UserFilter();
			userFilter.organizationIds.add(organizationId);
			if(userRepositoryCustom.countAll(userFilter)>0) {
				responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setMessage("Kh??ng th??? x??a ????n v??? n??y, v?? c??n ng?????i d??ng ??ang ho???t ?????ng");
				return responseCMS.build();
			}
			
			/* Xo?? article */
			organizationRepository.delete(organizationDelete);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult("X??a t??? ch???c ["+organizationDelete.name+"] th??nh c??ng");
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
	
	@PutMapping("/organization/import-users")
	public Object importUser(@RequestBody @Valid ReqOrganizationImportUsers reqOrganizationImportUsers){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Ki???m tra organizationId */
			Organization organization=null;
			try {
				organization=organizationRepository.findById(new ObjectId(reqOrganizationImportUsers.organizationId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.BAD_REQUEST);
				responseCMS.setMessage("T??? ch???c g??n ng?????i d??ng kh??ng t???n t???i");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* C???p nh???t data cho t???ng user*/
			if(reqOrganizationImportUsers.userIds.size()>0) {
				try {
					UserFilter userFilter = new UserFilter();
					userFilter.userIds=reqOrganizationImportUsers.userIds;
					
					/* L???p danh s??ch c??c user ???????c th??m v??o t??? ch???c */
					List<User> users=userRepositoryCustom.findAll(userFilter, 0, 0);
					System.out.println("Size: "+users.size());
					for (User user : users) {
						boolean exists=false;
						
						System.out.println("User: "+user.getFullName());
						/* L???p qua c??c ????n v??? c???a t???ng user ???? t???n t???i */
						for(UserOrganizationExpand item:user.getOrganizations()) {
							if(item.getOrganizationId().equals(organization.getId())) {
								System.out.println("C?? t???n t??i: "+item.getOrganizationId()+"/"+item.getOrganizationName());
								exists=true;
								break;
							}
						}
						
						/* N???u ch??a t???n t???i -> th??m v?? */
						if(exists==false) {
							UserOrganizationExpand userOrganizationExpand=new UserOrganizationExpand();
							userOrganizationExpand.setOrganizationId(organization.getId());
							userOrganizationExpand.setOrganizationName(organization.getName());
							
							user.getOrganizations().add(userOrganizationExpand);
							userRepository.save(user);
							
							System.out.println("G??n t??? ch???c cho ng?????i d??ng th??nh c??ng: "+user.getFullName());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.debug(e.getMessage());
				}
			}
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("Th??m ng?????i d??ng v??o t??? ch???c th??nh c??ng");
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
	
	@DeleteMapping("/organization/export-users")
	public Object exportUser(@RequestBody @Valid ReqOrganizationImportUsers reqOrganizationImportUsers) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Ki???m tra organizationId */
			Organization organization=null;
			try {
				organization=organizationRepository.findById(new ObjectId(reqOrganizationImportUsers.organizationId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.BAD_REQUEST);
				responseCMS.setMessage("T??? ch???c g??n ng?????i d??ng kh??ng t???n t???i");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* C???p nh???t data cho t???ng user*/
			if(reqOrganizationImportUsers.userIds.size()>0) {
				try {
					UserFilter userFilter = new UserFilter();
					userFilter.userIds=reqOrganizationImportUsers.userIds;
					userFilter.organizationIds=Arrays.asList(organization.getId());
					
					/* L???p danh s??ch c??c user ???????c th??m v??o t??? ch???c */
					List<User> users=userRepositoryCustom.findAll(userFilter, 0, 0);
					for (User user : users) {
						
						/* Khai b??o ph???n t??? x??a */
						UserOrganizationExpand userOrganizationExpand = null;
						
						/* L???p qua c??c ????n v??? c???a t???ng user ???? t???n t???i */
						for(UserOrganizationExpand item:user.getOrganizations()) {
							if(item.getOrganizationId().equals(organization.getId())) {
								userOrganizationExpand=item;
								break;
							}
						}
						
						/* N???u t???n t???i b??? ????n v??? ???? ra kh???i user ?????y */
						if(userOrganizationExpand!=null) {
							user.getOrganizations().remove(userOrganizationExpand);
							userRepository.save(user);
						}
						
						/* Lo???i b??? c??c vai tr?? trong ????n v??? m?? user ?????y ??ang thu???c v??? */
						OrganizationRoleFilter organizationRoleFilter=new OrganizationRoleFilter();
						organizationRoleFilter.organizationIds=Arrays.asList(organization.getId());
						organizationRoleFilter.userIds=Arrays.asList(user.getId());
						List<OrganizationRole> organizationRoles=organizationRoleRepositoryCustom.findAll(organizationRoleFilter, 0, 0);
						for (OrganizationRole organizationRole : organizationRoles) {
							if(organizationRole.userIds.contains(user.getId())) {
								organizationRole.userIds.remove(user.getId());
								organizationRoleRepository.save(organizationRole);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.debug(e.getMessage());
				}
			}
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("B??? ng?????i d??ng ra kh???i t??? ch???c th??nh c??ng");
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
	
	
	@PutMapping("/organization/set-leader/{organizationId}")
	public Object setLeaderOrganization(@PathVariable(name = "organizationId", required = true) String organizationId, @RequestParam(name = "userId", required = true) String userId){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Ki???m tra organizationId */
			Organization organizationUpdate=null;
			try {
				organizationUpdate=organizationRepository.findById(new ObjectId(organizationId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("T??? ch???c kh??ng t???n t???i");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Ki???m tra organizationId */
			User user=null;
			try {
				user=userRepository.findById(new ObjectId(userId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("L??nh ?????o kh??ng t???n t???i");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* C???p nh???t leader */
			organizationUpdate.leaderId=user.getId();
			organizationUpdate.leaderName=user.fullName;
			organizationUpdate=organizationRepository.save(organizationUpdate);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertOrganization(organizationUpdate));
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
	
	
	@DeleteMapping("/organization/unset-leader/{organizationId}")
	public Object unsetLeaderOrganization(@PathVariable(name = "organizationId", required = true) String organizationId){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Ki???m tra organizationId */
			Organization organizationUpdate=null;
			try {
				organizationUpdate=organizationRepository.findById(new ObjectId(organizationId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("T??? ch???c kh??ng t???n t???i");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* C???p nh???t leader */
			organizationUpdate.leaderId=null;
			organizationUpdate.leaderName=null;
			organizationUpdate=organizationRepository.save(organizationUpdate);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertOrganization(organizationUpdate));
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
	
	protected Document convertOrganization(Organization organization) {
		Document document=new Document();
		document.append("createdTime", organization.getCreatedTime());
		document.append("updatedTime", organization.getUpdatedTime());
		document.append("id", organization.getId());
		document.append("name", organization.name);
		document.append("description", organization.description);
		document.append("creatorId", organization.creatorId);
		document.append("creatorName", organization.creatorName);
		document.append("leaderId", organization.leaderId);
		document.append("leaderName", organization.leaderName);
		document.append("path", organization.path);
		document.append("parentId", organization.parentId);
		document.append("active", organization.active);
		document.append("numberOrder", organization.numberOrder);
		
		/* countSubOrg */
		try {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.parentId=organization.getId();
			
			document.append("countSubOrg",organizationRepositoryCustom.countAll(organizationFilter));
		} catch (Exception e) {
			document.append("countSubOrg",0);
		}
		
		/* countUser */
		try {
			UserFilter userFilter=new UserFilter();
			userFilter.organizationIds=Arrays.asList(organization.getId());
			document.append("countUser",userRepositoryCustom.countAll(userFilter));
		} catch (Exception e) {
			document.append("countUser",0);
		}
		
		/* countRole */
		try {
			OrganizationRoleFilter organizationRoleFilter=new OrganizationRoleFilter();
			organizationRoleFilter.organizationIds=Arrays.asList(organization.getId());
			document.append("countRole",organizationRoleRepositoryCustom.countAll(organizationRoleFilter));
		} catch (Exception e) {
			document.append("countRole",0);
		}
		return document;
	}
}
