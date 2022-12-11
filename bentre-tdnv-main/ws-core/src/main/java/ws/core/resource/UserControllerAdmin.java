package ws.core.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ws.core.enums.LogMessages;
import ws.core.model.Media;
import ws.core.model.Organization;
import ws.core.model.OrganizationRole;
import ws.core.model.User;
import ws.core.model.UserOrganization;
import ws.core.model.UserToOrganization;
import ws.core.model.embeded.UserOrganizationExpand;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.filter.OrganizationRoleFilter;
import ws.core.model.filter.UserFilter;
import ws.core.model.object.UserImportRaw;
import ws.core.model.request.ReqOrganizationAssistantImportLeaders;
import ws.core.model.request.ReqUploadFile;
import ws.core.model.request.ReqUserChangePassword;
import ws.core.model.request.ReqUserEditAdmin;
import ws.core.model.request.ReqUserOrganizationExpandEdit;
import ws.core.model.request.ReqUserResetPassword;
import ws.core.repository.MediaRepository;
import ws.core.repository.OrganizationRepository;
import ws.core.repository.OrganizationRoleRepository;
import ws.core.repository.OrganizationRoleRepositoryCustom;
import ws.core.repository.UserRepository;
import ws.core.repository.UserRepositoryCustom;
import ws.core.repository.imp.OrganizationRepositoryCustomImp;
import ws.core.security.CustomUserDetails;
import ws.core.security.JwtTokenProvider;
import ws.core.service.LogRequestService;
import ws.core.service.MediaService;
import ws.core.service.OrganizationService;
import ws.core.service.UserImportService;
import ws.core.service.UserService;
import ws.core.util.ResponseCMS;

@RestController
@RequestMapping("/admin")
public class UserControllerAdmin {
	private Logger log = LogManager.getLogger(UserControllerAdmin.class);
	
	@Autowired 
	protected AuthenticationManager authenticationManager;

	@Autowired
	protected JwtTokenProvider tokenProvider;
	
	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	protected UserRepositoryCustom userRepositoryCustom;
	
	@Autowired
    protected PasswordEncoder passwordEncoder;
	
	@Autowired
	protected OrganizationRepository organizationRepository;
	
	@Autowired
	protected OrganizationRepositoryCustomImp organizationRepositoryCustom;
	
	@Autowired
	protected OrganizationRoleRepository organizationRoleRepository;
	
	@Autowired
	protected OrganizationRoleRepositoryCustom organizationRoleRepositoryCustom;
	
	@Autowired
	protected OrganizationService organizationService;
	
	@Autowired
	protected UserService userService;
	
	@Autowired
	protected LogRequestService logRequestService;
	
	@Autowired
	protected MediaRepository mediaRepository;
	
	@Autowired
	protected MediaService mediaService;
	
	@Autowired
	protected UserImportService userImportService;
	
	@PostMapping("/login")
	public Object login(HttpServletRequest request, @RequestBody Document dataBody) {
		ResponseCMS responseCMS=new ResponseCMS();
		
		if(!dataBody.containsKey("username") && !dataBody.containsKey("password")) {
			responseCMS.setStatus(HttpStatus.BAD_REQUEST);
			responseCMS.setMessage("username and password does not empty");
			return responseCMS.build();
		}
		
		try {
			String username=dataBody.getString("username");
			String password=dataBody.getString("password");
			
			// Xác thực từ username và password.
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			
			// Nếu không xảy ra exception tức là thông tin hợp lệ
			CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();
			
			User user = customUser.getUser();
			
			if(user.active) {
		        // Set thông tin authentication vào Security Context
		        SecurityContextHolder.getContext().setAuthentication(authentication);
	
				/* Write log cho login */
		        try {
		        	logRequestService.writeLogLogin(request, user);
				} catch (Exception e) {}
		        
				/* Trả về jwt cho người dùng. */
		        Date jwt_expiryDate=tokenProvider.generateExpiryDate();
		        String jwt_login = tokenProvider.generateToken((CustomUserDetails) authentication.getPrincipal(), jwt_expiryDate);
		        String jwt_refresh=user.createRefreshToken();
				
		        /* Lưu refresh token */
		        userRepository.save(user);
		        
		        Document result=new Document();
		        result.put("id", user.getId());
		        result.put("email", user.getEmail());
		        result.put("fullName", user.getFullName());
		        result.put("username", user.getUsername());
		        result.put("jobTitle", user.getJobTitle());
		        result.put("phone", user.getPhone());
		        result.put("leaders", user.getLeaders());
		        result.put("accountDomino", user.getAccountDomino());
		        
		        if(user.getUsername().equals("administrator")) {
		        	result.put("superadmin", true);
		        }else {
		        	List<Document> docOrgnizations=new ArrayList<Document>();
		        	
		        	OrganizationFilter organizationFilter=new OrganizationFilter();
		        	List<ObjectId> objectIds=new ArrayList<ObjectId>();
		        	for(UserOrganizationExpand item:user.getOrganizations()) {
		        		objectIds.add(new ObjectId(item.getOrganizationId()));
		        	}
		        	organizationFilter._ids=objectIds;
		        	
		        	List<Organization> organizations=organizationRepositoryCustom.findAll(organizationFilter, 0, 0);
		        	for (Organization organization : organizations) {
		        		Document document=new Document();
		        		document.append("id", organization.getId());
		        		document.append("name", organization.name);
		        		document.append("leaderName", organization.leaderName);
		        		
		        		/* Thông tin tài khoản mở rộng */
		        		Document userExpand=new Document();
		        		for(UserOrganizationExpand item:user.getOrganizations()) {
		        			if(item.getOrganizationId().equals(organization.getId())) {
		        				userExpand.append("organizationId", item.organizationId);
		        				userExpand.append("organizationName", item.organizationName);
		        				userExpand.append("userId", user.getId());
		        				userExpand.append("fullName", user.getFullName());
		        				userExpand.append("accountIOffice", item.accountIOffice);
		        				userExpand.append("jobTitle", item.jobTitle);
		        				userExpand.append("numberOrder", item.numberOrder);
		        				break;
		        			}
		        		}
		        		document.append("userExpand", userExpand);
		        		
						/* Thông tin danh sách permission */
		        		List<OrganizationRole> rolesOrganizationUser=organizationRoleRepositoryCustom.getRolesOrganizationUser(organization.getId(), user.getId());
		        		List<Document> permissionList=new ArrayList<Document>();
		        		for (OrganizationRole organizationRole : rolesOrganizationUser) {
							Document role=new Document();
							role.append("id", organizationRole.getId());
							role.append("name", organizationRole.getName());
							role.append("description", organizationRole.getDescription());
							role.append("permissionKeys", organizationRole.getPermissionKeys());
							permissionList.add(role);
						}
		        		document.append("roles", permissionList);
		        		
		        		docOrgnizations.add(document);
					}
		        	result.put("organizations", docOrgnizations);
		        }
		        result.put("loginToken", jwt_login);
		        result.put("expiryToken", jwt_expiryDate.getTime());
		        result.put("refeshToken", jwt_refresh);
		        
		        responseCMS.setStatus(HttpStatus.OK);
				responseCMS.setResult(result);
				return responseCMS.build();
			}
			
			log.debug("Forbidden");
			responseCMS.setStatus(HttpStatus.FORBIDDEN);
			responseCMS.setMessage(LogMessages.FORBIDDEN.getMessage());
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.BAD_REQUEST);
			responseCMS.setMessage("Username and password does not match");
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	@PutMapping("/user/change-password/{userId}")
	public Object changePassword(@PathVariable(name = "userId", required = true) String userId, @RequestBody @Valid ReqUserChangePassword userChangePassword){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra userId */
			User userUpdate=null;
			try {
				userUpdate=userRepository.findById(new ObjectId(userId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage(LogMessages.NOT_FOUND.getMessage());
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Check password Old */
			if(userRepositoryCustom.checkPassword(userId, userChangePassword.passwordOld)==false) {
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage("Mật khẩu cũ không đúng");
				return responseCMS.build();
			}
			
			/* Cập nhật mật khẩu mới */
			userUpdate.updatedTime=userChangePassword.updatedTime;
			userUpdate.password=passwordEncoder.encode(userChangePassword.passwordNew);
			userUpdate=userRepository.save(userUpdate);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertUser(userUpdate));
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
	
	@PutMapping("/user/reset-password/{userId}")
	public Object resetPassword(@PathVariable(name = "userId", required = true) String userId, @RequestBody @Valid ReqUserResetPassword userResetPassword){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra userId */
			User userUpdate=null;
			try {
				userUpdate=userRepository.findById(new ObjectId(userId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage(LogMessages.NOT_FOUND.getMessage());
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Cập nhật mật khẩu mới */
			userUpdate.updatedTime=userResetPassword.updatedTime;
			userUpdate.password=passwordEncoder.encode(userResetPassword.passwordNew);
			userUpdate=userRepository.save(userUpdate);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertUser(userUpdate));
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
	
	@GetMapping("/user/count")
	public Object getCount(
			@RequestParam(name = "excludeOrganizationId", required = false) String excludeOrganizationId,
			@RequestParam(name = "organization-empty", required = false, defaultValue = "") String organizationEmpty,
			@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			UserFilter userFilter=new UserFilter();
			userFilter.keySearch=keyword;
			userFilter.organizationEmpty=organizationEmpty;
			if(excludeOrganizationId!=null) {
				userFilter.excludeOrganizationIds=Arrays.asList(excludeOrganizationId);
			}
			
			int total=userRepositoryCustom.countAll(userFilter);
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setTotal(total);
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
	
	@GetMapping("/user/list")
	public Object getList(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "excludeOrganizationId", required = false) String excludeOrganizationId,
			@RequestParam(name = "organization-empty", required = false, defaultValue = "") String organizationEmpty,
			@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			UserFilter userFilter=new UserFilter();
			userFilter.keySearch=keyword;
			userFilter.organizationEmpty=organizationEmpty;
			if(excludeOrganizationId!=null) {
				userFilter.excludeOrganizationIds=Arrays.asList(excludeOrganizationId);
			}
			
			int total=userRepositoryCustom.countAll(userFilter);
			List<User> users=userRepositoryCustom.findAll(userFilter, skip, limit);
			List<Document> results=new ArrayList<Document>();
			for (User item : users) {
				results.add(convertUser(item));
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setTotal(total);
			responseCMS.setResult(results);
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
	
	@GetMapping("/user/count/{organizationId}")
	public Object getCountByOrganizationId(
			@PathVariable(name = "organizationId", required = true) String organizationId, 
			@RequestParam(name = "keyword", required = false) String keyword) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			UserFilter userFilter=new UserFilter();
			userFilter.keySearch=keyword;
			userFilter.organizationIds=Arrays.asList(organizationId);
			int total=userRepositoryCustom.countAll(userFilter);
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setTotal(total);
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
	
	@GetMapping("/user/list/{organizationId}")
	public Object getListByOrganizationId(
			@PathVariable(name = "organizationId", required = true) String organizationId, 
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "keyword", required = false) String keyword) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			UserFilter userFilter=new UserFilter();
			userFilter.keySearch=keyword;
			userFilter.organizationIds=Arrays.asList(organizationId);
			int total=userRepositoryCustom.countAll(userFilter);
			List<User> users=userRepositoryCustom.findAll(userFilter, skip, limit);
			List<Document> results=new ArrayList<Document>();
			for (User item : users) {
				results.add(convertUser(item));
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setTotal(total);
			responseCMS.setResult(results);
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
	
	@GetMapping("/user/list-leader/{organizationId}")
	public Object getListLeaderByOrganizationId(
			@PathVariable(name = "organizationId", required = true) String organizationId, 
			@RequestParam(name = "skip", required = true) int skip,
			@RequestParam(name = "limit", required = true) int limit) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/*
			 * Lấy danh sách người dùng trong đơn vị có permission là truongdonvi hoăc
			 * photruongdonvi
			 */
			OrganizationRoleFilter organizationRoleFilter=new OrganizationRoleFilter();
			organizationRoleFilter.organizationIds=Arrays.asList(organizationId);
			organizationRoleFilter.permissionKeys=Arrays.asList("truongdonvi","photruongdonvi");
			List<OrganizationRole> organizationRoles=organizationRoleRepositoryCustom.findAll(organizationRoleFilter, 0, 0);
			List<String> userIds=new ArrayList<String>();
			for (OrganizationRole organizationRole : organizationRoles) {
				userIds.addAll(organizationRole.userIds);
			}
			
			UserFilter userFilter=new UserFilter();
			userFilter.organizationIds=Arrays.asList(organizationId);
			userFilter.userIds=userIds;
			
			int total=userRepositoryCustom.countAll(userFilter);
			List<User> users=userRepositoryCustom.findAll(userFilter, skip, limit);
			List<Document> results=new ArrayList<Document>();
			for (User item : users) {
				results.add(convertUser(item));
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setTotal(total);
			responseCMS.setResult(results);
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
	
	@GetMapping("/user/get/{userId}")
	public Object getUser(@PathVariable(name = "userId", required = true) String userId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			User user=null;
			try {
				user=userRepository.findById(new ObjectId(userId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult(LogMessages.NOT_FOUND.getMessage());
				return responseCMS.build();
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertUser(user));
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	@PostMapping("/user/create")
	public Object createUser(@RequestBody @Valid User createUser){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			CustomUserDetails user = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			/* Check valid trước khi lưu (thực ra là để show lỗi chi tiết) */
			try {
				userService.validForCreate(createUser);
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage(e.getMessage());
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Lưu data */
			try {
				createUser.creatorId=user.getUser().getId();
				createUser.creatorName=user.getUser().fullName;
				createUser.password=passwordEncoder.encode(createUser.password);
				createUser.activeCode=RandomStringUtils.randomAlphanumeric(8).toUpperCase();
				createUser=userRepository.save(createUser);
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage(e.getMessage());
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			responseCMS.setStatus(HttpStatus.CREATED);
			responseCMS.setResult(convertUser(createUser));
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
	
	@PutMapping("/user/edit/{userId}")
	public Object updateUser(
			@PathVariable(name = "userId", required = true) String userId, 
			@RequestBody @Valid ReqUserEditAdmin userEdit){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra userId */
			User userUpdate=null;
			try {
				userUpdate=userRepository.findById(new ObjectId(userId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage(LogMessages.NOT_FOUND.getMessage());
				return responseCMS.build();
			}
			
			/* Lưu dữ liệu */
			userUpdate.updatedTime=userEdit.updatedTime;
			userUpdate.username=userEdit.username;
			userUpdate.email=userEdit.email;
			userUpdate.phone=userEdit.phone;
			userUpdate.fullName=userEdit.fullName;
			userUpdate.jobTitle=userEdit.jobTitle;
			userUpdate.active=userEdit.active;
			userUpdate.accountDomino=userEdit.accountDomino;
			userUpdate.activeCode=userEdit.activeCode;
			userUpdate=userRepository.save(userUpdate);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertUser(userUpdate));
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
	
	@PutMapping("/user/edit-expand/{userId}")
	public Object updateUserExpand(
			@PathVariable(name = "userId", required = true) String userId, 
			@RequestBody @Valid ReqUserOrganizationExpandEdit reqUserOrganizationExpandEdit){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra userId */
			User userUpdate=null;
			try {
				userUpdate=userRepository.findById(new ObjectId(userId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("userId ["+userId+"] không tồn tại trong hệ thống");
				return responseCMS.build();
			}
			
			/* Kiểm tra organization */
			Organization organizationUpdate=null;
			try {
				organizationUpdate=organizationRepository.findById(new ObjectId(reqUserOrganizationExpandEdit.organizationId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("organizationId ["+reqUserOrganizationExpandEdit.organizationId+"] không tồn tại trong hệ thống");
				return responseCMS.build();
			}
			
			/* Kiểm tra tổ chức và tài khoản có hợp lệ không */
			boolean exists=false;
			for(UserOrganizationExpand item: userUpdate.getOrganizations()) {
				if(item.getOrganizationId().equals(organizationUpdate.getId())) {
					item.updatedTime=new Date();
					item.accountIOffice=reqUserOrganizationExpandEdit.accountIOffice;
					item.jobTitle=reqUserOrganizationExpandEdit.jobTitle;
					item.numberOrder=reqUserOrganizationExpandEdit.numberOrder;
					exists=true;
					break;
				}
			}
			
			if(exists==false){
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("Tài khoản không nằm trong tổ chức này");
				return responseCMS.build();
			}
			
			/* Lưu lại */
			userRepository.save(userUpdate);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("Cập nhật thành công");
			responseCMS.setResult(convertUser(userUpdate));
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
	
	@DeleteMapping("/user/delete/{userId}")
	public Object deleteUser(@PathVariable(name = "userId", required = true) String userId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra userId */
			User userDelete=null;
			try {
				userDelete=userRepository.findById(new ObjectId(userId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("userId không tồn tại");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Kiểm tra user có đang bị ràng buộc không thì mới cho xóa? */
			
			
			/* Xoá article */
			userRepository.delete(userDelete);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("Đã xóa người dùng thành công");
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
	
	
	@PutMapping("/user/add-in-organization")
	public Object addUserInOrganization(@RequestBody @Valid UserToOrganization userToOrganization){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra userId */
			User user=null;
			try {
				user=userRepository.findById(new ObjectId(userToOrganization.userId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.BAD_REQUEST);
				responseCMS.setMessage("userId không tồn tại");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Kiểm tra organizationId */
			Organization organization=null;
			try {
				organization=organizationRepository.findById(new ObjectId(userToOrganization.organizationId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.BAD_REQUEST);
				responseCMS.setMessage("organizationId không tồn tại");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Kiểm tra tồn tại chưa */
			boolean exists=false;
			for(UserOrganizationExpand item:user.getOrganizations()) {
				if(item.getOrganizationId().equals(organization.getId())) {
					exists=true;
				}
			}
			
			if(exists) {
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage("Người dùng ["+user.fullName+"] đã tồn tại trong tổ chức ["+organization.name+"]");
				return responseCMS.build();
			}
			
			/* Cập nhật cho organizationIds của user */
			UserOrganizationExpand userOrganizationExpand=new UserOrganizationExpand();
			userOrganizationExpand.setOrganizationId(organization.getId());
			userOrganizationExpand.setOrganizationName(organization.getName());
			
			user.getOrganizations().add(userOrganizationExpand);
			userRepository.save(user);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("Thêm người dùng ["+user.fullName+"] vào trong tổ chức ["+organization.name+"] thành công");
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
	
	@DeleteMapping("/user/remove-in-organization")
	public Object removeUserInOrganization(@RequestBody @Valid UserToOrganization userToOrganization) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra userId */
			User user=null;
			try {
				user=userRepository.findById(new ObjectId(userToOrganization.userId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.BAD_REQUEST);
				responseCMS.setMessage("userId không tồn tại");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Kiểm tra organizationId */
			Organization organization=null;
			try {
				organization=organizationRepository.findById(new ObjectId(userToOrganization.organizationId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.BAD_REQUEST);
				responseCMS.setMessage("organizationId không tồn tại");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Cập nhật cho organizationIds của user */
			for(UserOrganizationExpand item:user.getOrganizations()) {
				if(item.getOrganizationId().equals(organization.getId())) {
					user.getOrganizations().remove(item);
					userRepository.save(user);
					
					/* Bỏ ra khỏi vai trò của tổ chức */
					OrganizationRoleFilter organizationRoleFilter=new OrganizationRoleFilter();
					organizationRoleFilter.organizationIds=Arrays.asList(organization.getId());
					List<OrganizationRole> organizationRoles=organizationRoleRepositoryCustom.findAll(organizationRoleFilter, 0, 0);
					for (OrganizationRole organizationRole : organizationRoles) {
						if(organizationRole.userIds.contains(user.getId())) {
							organizationRole.userIds.remove(user.getId());
							organizationRoleRepository.save(organizationRole);
						}
					}
					
					responseCMS.setStatus(HttpStatus.OK);
					responseCMS.setMessage("Xóa người dùng ["+user.fullName+"] trong tổ chức ["+organization.name+"] thành công");
					return responseCMS.build();
				}
			}
			
			
			responseCMS.setStatus(HttpStatus.BAD_REQUEST);
			responseCMS.setMessage("Người dùng ["+user.fullName+"] không tồn tại trong tổ chức ["+organization.name+"]");
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
	
	
	@PutMapping("/user/set-leaders")
	public Object importLeaders(@RequestBody @Valid ReqOrganizationAssistantImportLeaders reqOrganizationAssistantImportLeaders){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra organization */
			Organization organization=null;
			try {
				organization=organizationRepository.findById(new ObjectId(reqOrganizationAssistantImportLeaders.organizationId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.BAD_REQUEST);
				responseCMS.setMessage("Tổ chức không tồn tại");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			User userAssistant=null;
			try {
				userAssistant=userRepository.findById(new ObjectId(reqOrganizationAssistantImportLeaders.userId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.BAD_REQUEST);
				responseCMS.setMessage("userId ["+reqOrganizationAssistantImportLeaders.userId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Kiểm tra userId với organization có chung không */
			boolean exists=false;
			for(UserOrganizationExpand item:userAssistant.getOrganizations()) {
				if(item.getOrganizationId().equals(organization.getId())) {
					exists=true;
				}
			}
			
			if(exists==false) {
				responseCMS.setStatus(HttpStatus.BAD_REQUEST);
				responseCMS.setMessage("userId ["+userAssistant.getId()+"] không tồn tại trong tổ chức ["+organization.getId()+"] hệ thống");
				return responseCMS.build();
			}
			
			List<UserOrganization> waitInsert=new ArrayList<UserOrganization>();
			
			/* Gán lãnh đạo cho cán bộ */
			if(reqOrganizationAssistantImportLeaders.leaderIds.size()>0) {
				for (String leaderId : reqOrganizationAssistantImportLeaders.leaderIds) {
					User leader=null;
					try {
						leader=userRepository.findById(new ObjectId(leaderId)).get();
					} catch (Exception e) {
						e.printStackTrace();
						log.debug(e.getMessage());
						responseCMS.setStatus(HttpStatus.BAD_REQUEST);
						responseCMS.setMessage("leaderId ["+leaderId+"] không tồn tại trong hệ thống");
						responseCMS.setError(e.getMessage());
						return responseCMS.build();
					}
					
					/* Kiểm tra leader có trong organization không */
					boolean check=false;
					for(UserOrganizationExpand item:leader.getOrganizations()) {
						if(item.getOrganizationId().equals(organization.getId())) {
							check=true;
						}
					}
					
					if(check==false) {
						responseCMS.setStatus(HttpStatus.BAD_REQUEST);
						responseCMS.setMessage("leaderId ["+leaderId+"] không tồn tại trong tổ chức ["+organization.getId()+"] hệ thống");
						return responseCMS.build();
					}
					
					UserOrganization userOrganization=new UserOrganization();
					userOrganization.userId=leader.getId();
					userOrganization.fullName=leader.fullName;
					userOrganization.organizationId=organization.getId();
					userOrganization.organizationName=organization.name;
					waitInsert.add(userOrganization);
				}
			}
			
			/* Kiểm tra sự tồn tại */
			/*for(UserOrganization userOrganization:userAssistant.leaders) {
				for (UserOrganization _userOrganization : waitInsert) {
					if(_userOrganization.userId.equalsIgnoreCase(userOrganization.userId) && _userOrganization.organizationId.equalsIgnoreCase(userOrganization.organizationId)) {
						waitInsert.remove(_userOrganization);
						break;
					}
				}
			}*/
			
			/* Lưu vào DB */
			userAssistant.leaders.clear();
			if(waitInsert.size()>0) {
				userAssistant.leaders.addAll(waitInsert);
			}
			userRepository.save(userAssistant);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage(convertUser(userAssistant));
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
	
//	@DeleteMapping("/user/export-leaders")
//	public Object exportLeaders(@RequestBody @Valid ReqOrganizationAssistantImportLeaders reqOrganizationAssistantImportLeaders) {
//		ResponseCMS responseCMS=new ResponseCMS();
//		try {
//			/* Kiểm tra roleId */
//			Organization organization=null;
//			try {
//				organization=organizationRepository.findById(new ObjectId(reqOrganizationAssistantImportLeaders.organizationId)).get();
//			} catch (Exception e) {
//				e.printStackTrace();
//				log.debug(e.getMessage());
//				responseCMS.setStatus(HttpStatus.BAD_REQUEST);
//				responseCMS.setMessage("Tổ chức ["+reqOrganizationAssistantImportLeaders.organizationId+"] không tồn tại");
//				return responseCMS.build();
//			}
//			
//			User userAssistant=null;
//			try {
//				userAssistant=userRepository.findById(new ObjectId(reqOrganizationAssistantImportLeaders.userId)).get();
//			} catch (Exception e) {
//				e.printStackTrace();
//				log.debug(e.getMessage());
//				responseCMS.setStatus(HttpStatus.BAD_REQUEST);
//				responseCMS.setMessage("userId ["+reqOrganizationAssistantImportLeaders.userId+"] không tồn tại trong hệ thống");
//				return responseCMS.build();
//			}
//			
//			/* Kiểm tra assistant có trong organization không */
//			if(userAssistant.organizationIds.contains(organization.getId())==false) {
//				responseCMS.setStatus(HttpStatus.BAD_REQUEST);
//				responseCMS.setMessage("userId ["+reqOrganizationAssistantImportLeaders.userId+"] không tồn tại trong tổ chức ["+organization.getId()+"] hệ thống");
//				return responseCMS.build();
//			}
//			
//			/* Remove assistant với các leaderIds */
//			if(reqOrganizationAssistantImportLeaders.leaderIds.size()>0 && userAssistant.leaders.size()>0) {
//				for(String leaderId:reqOrganizationAssistantImportLeaders.leaderIds) {
//					for(UserOrganization _userOrganization:userAssistant.leaders) {
//						/* Nếu có tồn tại */
//						if(_userOrganization.userId.equalsIgnoreCase(leaderId) && _userOrganization.organizationId.equalsIgnoreCase(organization.getId())) {
//							userAssistant.leaders.remove(_userOrganization);
//							break;
//						}
//					}
//				}
//			} else {
//				responseCMS.setStatus(HttpStatus.BAD_REQUEST);
//				responseCMS.setMessage("leaderIds đang rỗng, không có leader nào được bỏ ra");
//				return responseCMS.build();
//			}
//			
//			/* Cập nhật DB */
//			userRepository.save(userAssistant);
//			
//			responseCMS.setStatus(HttpStatus.OK);
//			responseCMS.setMessage(convertUser(userAssistant));
//			return responseCMS.build();
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.debug(e.getMessage());
//			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
//			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
//			return responseCMS.build();
//		}
//	}
	
	@PostMapping(value = "/user/import-excel", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public Object importUserFromExcel(HttpServletRequest request,
			@ModelAttribute("myUploadForm") @Validated ReqUploadFile reqUploadFile) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			User creator=userRequest.getUser();
			
			Media media=mediaService.storeMedia(request, reqUploadFile);
			media.setOwnerId(userRequest.getUser().getId());
			media=mediaRepository.save(media);
			
			List<UserImportRaw> userImportRaws = userImportService.readFileFromExcel(media.getPath());
			/* Chạy tạo đơn vị từ root đến child */
			for (UserImportRaw userImportRaw : userImportRaws) {
				System.out.println(userImportRaw.toString());
				
				if(userImportRaw.isValid()) {
					LinkedList<String> orgNames = userImportService.getOrganizationNameOrderByRootList(userImportRaw.getOrganizationUnit());
					String parentId=null;
					String organizationName=null;
					
					/* Lặp qua các tên từ ROOT đến child */
					for (String orgName : orgNames) {
						System.out.println("+ Tổ chức: "+orgName);
						
						/* Tìm tên đã tồn tại chưa trong cùng 1 đơn vị (parent và name) */
						OrganizationFilter organizationFilter=new OrganizationFilter();
						organizationFilter.parentId=parentId;
						organizationFilter.name=orgName;
						
						Optional<Organization> orgCheck=organizationRepositoryCustom.findOne(organizationFilter);
						/* Nếu đã tồn tại */
						if(orgCheck.isPresent()) {
							parentId=orgCheck.get().getId();
						}
						/* Nếu chưa tồn tại */
						else {
							Organization organizationNew=new Organization();
							organizationNew.name=orgName;
							organizationNew.creatorId=creator.getId();
							organizationNew.creatorName=creator.getFullName();
							organizationNew.parentId=parentId;
							organizationService.getPath(organizationNew);
							organizationNew=organizationRepository.save(organizationNew);
							
							parentId=organizationNew.getId();
						}
						
						organizationName=orgName;
					}
					
					userImportRaw.setOrganizationId(parentId);
					userImportRaw.setOrganizationName(organizationName);
				}
			}
			
			/* Chạy tạo người dùng khi đã tạo đơn vị đầy đủ */
			for (UserImportRaw userImportRaw : userImportRaws) {
				if(userImportRaw.isValid() && userImportRaw.getOrganizationId()!=null) {
					/* Tìm username đã có tồn tại chưa */
					Optional<User> userCheck = userRepository.findByUsername(userImportRaw.getUsername());
					if(userCheck.isPresent()) {
						userImportRaw.setResult(false);
						userImportRaw.setStatus("Tài khoản ["+userImportRaw.getUsername()+"] đã tồn tại");
					}else {
						User userNew=new User();
						userNew.active=true;
						userNew.setPassword(passwordEncoder.encode("abc123"));
						userNew.setUsername(userImportRaw.getUsername());
						userNew.setFullName(userImportRaw.getFullname());
						userNew.setEmail(userImportRaw.getEmailAddress());
						userNew.setJobTitle(userImportRaw.getJobTitle());
						userNew.setPhone(userImportRaw.getOfficePhone());
						userNew.setCreatorId(creator.getId());
						userNew.setCreatorName(creator.getFullName());
						
						UserOrganizationExpand userOrganizationExpand=new UserOrganizationExpand();
						userOrganizationExpand.setOrganizationId(userImportRaw.getOrganizationId());
						userOrganizationExpand.setOrganizationName(userImportRaw.getOrganizationName());
						userOrganizationExpand.setJobTitle(userImportRaw.getJobTitle());
						userOrganizationExpand.setNumberOrder(1);
						userNew.getOrganizations().add(userOrganizationExpand);
						
						userNew=userRepository.save(userNew);
						
						userImportRaw.setResult(true);
						userImportRaw.setStatus("Thêm thành công");
					}
				} else {
					userImportRaw.setResult(false);
					userImportRaw.setStatus("Không đủ thông tin");
				}
			}
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("Upload success");
			responseCMS.setResult(userImportRaws);
			return responseCMS.build();
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage("Upload fail");
			return responseCMS.build();
		}
	}
	
	protected Document convertUser(User user) {
		Document document=new Document();
		document.append("createdTime", user.getCreatedTime());
		document.append("updatedTime", user.getUpdatedTime());
		document.append("id", user.getId());
		document.append("username", user.username);
		document.append("email", user.email);
		document.append("phone", user.phone);
		document.append("fullName", user.fullName);
		document.append("creatorId", user.creatorId);
		document.append("creatorName", user.creatorName);
		document.append("lastDateLogin", user.lastDateLogin);
		document.append("lastIPLogin", user.lastIPLogin);
		document.append("active", user.active);
		
		List<Document> organizations=new ArrayList<Document>();
		for(UserOrganizationExpand item:user.organizations) {
			Document userExpand=new Document();
			userExpand.append("organizationId", item.organizationId);
			userExpand.append("organizationName", item.organizationName);
			userExpand.append("accountIOffice", item.accountIOffice);
			userExpand.append("jobTitle", item.jobTitle);
			userExpand.append("numberOrder", item.numberOrder);
			
			List<Document> roles=new ArrayList<Document>();
			OrganizationRoleFilter organizationRoleFilter=new OrganizationRoleFilter();
			organizationRoleFilter.userIds=Arrays.asList(user.getId());
			organizationRoleFilter.organizationIds=Arrays.asList(item.getOrganizationId());
			List<OrganizationRole> organizationRoleList = organizationRoleRepositoryCustom.findAll(organizationRoleFilter, 0, 0);
			for (OrganizationRole organizationRole : organizationRoleList) {
				roles.add(convertOrganizationRole(organizationRole));
			}
			userExpand.append("roles", roles);
			
			organizations.add(userExpand);
		}
		document.append("organizations", organizations);
		
		document.append("leaders", user.leaders);
		document.append("accountDomino", user.accountDomino);
		document.append("activeCode", user.activeCode);
		return document;
	}
	
	protected Document convertOrganizationRole(OrganizationRole role) {
		Document document=new Document();
		document.append("id", role.getId());
		document.append("name", role.name);
		document.append("description", role.description);
		document.append("permissionKeys", role.permissionKeys);
		return document;
	}
}
