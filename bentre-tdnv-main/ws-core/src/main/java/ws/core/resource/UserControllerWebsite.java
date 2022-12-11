package ws.core.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import ws.core.enums.LogMessages;
import ws.core.enums.NotifyCaption;
import ws.core.enums.Permission;
import ws.core.model.LogRequestClientRequest;
import ws.core.model.Notify;
import ws.core.model.Organization;
import ws.core.model.OrganizationRole;
import ws.core.model.User;
import ws.core.model.UserOrganization;
import ws.core.model.embeded.UserOrganizationExpand;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.filter.UserFilter;
import ws.core.model.request.ReqUserChangePassword;
import ws.core.model.request.ReqUserEditWebsite;
import ws.core.model.request.ReqUserLogin;
import ws.core.repository.NotifyRepository;
import ws.core.repository.OrganizationRepository;
import ws.core.repository.OrganizationRoleRepository;
import ws.core.repository.OrganizationRoleRepositoryCustom;
import ws.core.repository.UserRepository;
import ws.core.repository.UserRepositoryCustom;
import ws.core.repository.imp.OrganizationRepositoryCustomImp;
import ws.core.security.CustomUserDetails;
import ws.core.security.JwtTokenProvider;
import ws.core.service.FirebaseService;
import ws.core.service.LogRequestService;
import ws.core.service.OrganizationService;
import ws.core.service.UserService;
import ws.core.util.DateTimeUtil;
import ws.core.util.ResponseCMS;

@RestController
@RequestMapping("/website")
public class UserControllerWebsite {
	private Logger log = LogManager.getLogger(UserControllerWebsite.class);
	
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
	protected FirebaseService firebaseService;
	
	@Autowired
	protected NotifyRepository taskNotifyRepository;
	
	@Autowired
	protected LogRequestService logRequestService;
	
	@Value("${loging.fail.notify.over-number}")
	protected int loginFailOverNumber;
	
	@PostMapping("/login")
	public Object login(HttpServletRequest request, @RequestBody @Valid ReqUserLogin reqUserLogin) {
		ResponseCMS responseCMS=new ResponseCMS();
		
		User userLogin=null;
		try {
			String username=reqUserLogin.username;
			String password=reqUserLogin.password;
			
			try {
				userLogin=userService.getUserByUsername(username);
			} catch (Exception e) {}
			
			// Xác thực từ username và password.
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			
			// Nếu không xảy ra exception tức là thông tin hợp lệ
			CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();
			
			User user=customUser.getUser();
			
			/* Nếu user là administrator thì không được phép */
			if(user.username.equalsIgnoreCase("administrator")) {
				responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setMessage("Tài khoản không được phép truy cập");
				return responseCMS.build();
			}
			
			/* Nếu user không nằm trong organization nào thì trả về lỗi */
	        if(user.organizations.size()==0) {
	        	responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setMessage("Tài khoản không nằm trong tổ chức nào");
				return responseCMS.build();
	        }
	        
	        /* Nếu user chưa được kích hoạt */
	        if(user.active==false) {
	        	responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setMessage("Tài khoản không được kích hoạt");
				return responseCMS.build();
	        }
	        
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
		        result.put("email", user.email);
		        result.put("phone", user.phone);
		        result.put("username", user.username);
		        result.put("fullName", user.fullName);
		        result.put("jobTitle", "");
		        result.put("accountDomino", user.accountDomino);
		        result.put("config", user.config);
		        
		        List<Document> docOrgnizations=new ArrayList<Document>();
		        
		        OrganizationFilter organizationFilter=new OrganizationFilter();
		        List<ObjectId> objectIds=new ArrayList<ObjectId>();
	        	for(UserOrganizationExpand item:user.getOrganizations()) {
	        		objectIds.add(new ObjectId(item.getOrganizationId()));
	        	}
	        	organizationFilter._ids=objectIds;
		        List<Organization> organizations=organizationRepositoryCustom.findAll(organizationFilter, 0, 0);
		        
				/* Nếu user không nằm trong organization nào thì trả về lỗi */
		        if(organizations.size()==0) {
		        	responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
					responseCMS.setMessage("Tài khoản không nằm trong tổ chức nào");
					return responseCMS.build();
		        }
		        
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
	        				userExpand.append("accountIOffice", item.accountIOffice);
	        				userExpand.append("jobTitle", item.jobTitle);
	        				userExpand.append("numberOrder", item.numberOrder);
	        				break;
	        			}
	        		}
	        		document.append("userExpand", userExpand);
	        		
					/* Thông tin các permission */
		        	List<OrganizationRole> rolesOrganizationUser=organizationRoleRepositoryCustom.getRolesOrganizationUser(organization.getId(), user.getId());
		        	List<Document> permissionList=new ArrayList<Document>();
		        	for (OrganizationRole organizationRole : rolesOrganizationUser) {
		        		Document role=new Document();
		        		role.append("id", organizationRole.getId());
		        		role.append("name", organizationRole.name);
		        		role.append("description", organizationRole.description);
		        		role.append("permissionKeys", organizationRole.permissionKeys);
		        		permissionList.add(role);
		        	}
		        	document.append("roles", permissionList);
		        	
					/* Lấy danh sách lãnh đạo mà user này hỗ trợ */
		        	List<UserOrganization> leadersTask=new ArrayList<UserOrganization>();
		        	List<UserOrganization> leaders=user.leaders;
		        	for (UserOrganization leader : leaders) {
						if(leader.organizationId.equalsIgnoreCase(organization.getId())) {
							leadersTask.add(leader);
						}
					}
		        	document.append("leadersTask", leadersTask);
		        	
					/* Lấy danh sách cán bộ mà user này đã giao hỗ trợ */
		        	List<UserOrganization> assistantsTask=new ArrayList<UserOrganization>();
		        	UserFilter userFilter=new UserFilter();
		        	userFilter.leader=new UserOrganization();
		        	userFilter.leader.userId=user.getId();
		        	userFilter.leader.organizationId=organization.getId();
		        	List<User> findAssistants=userRepositoryCustom.findAll(userFilter, 0, 0);
		        	
		        	for (User assistant : findAssistants) {
		        		for (UserOrganization leader : assistant.leaders) {
							if(leader.userId.equalsIgnoreCase(user.getId()) && leader.organizationId.equalsIgnoreCase(organization.getId())) {
								UserOrganization assistantTask=new UserOrganization();
								assistantTask.userId=assistant.getId();
								assistantTask.fullName=assistant.fullName;
								assistantTask.organizationId=organization.getId();
								assistantTask.organizationName=organization.name;
								assistantsTask.add(assistantTask);
								break;
							}
						}
					}
		        	document.append("assistantsTask", assistantsTask);
		        	
		        	docOrgnizations.add(document);
		        }
		        result.put("organizations", docOrgnizations);
		        
		        result.put("loginToken", jwt_login);
		        result.put("expiryToken", jwt_expiryDate.getTime());
		        result.put("refeshToken", jwt_refresh);
		        
		        /* Thông báo nếu đăng nhập bằng web*/
		        LogRequestClientRequest logRequestUserInfo=null;
		        try {
					logRequestUserInfo=new Gson().fromJson(request.getHeader("UserInfo"), LogRequestClientRequest.class);
					notifyLoginWeb(logRequestUserInfo, user);
				} catch (Exception e) {
					log.debug(e.getMessage());
					e.printStackTrace();
				}
				/* End thông báo */
				
				/* Đăng nhập sai */
		        user.loginFail=0;
		        user.lastDateLogin=new Date();
		        if(logRequestUserInfo!=null && logRequestUserInfo.ipaddress!=null) {
		        	user.lastIPLogin=logRequestUserInfo.ipaddress.toString();
		        }
		        userRepository.save(user);
				/* End đăng nhập sai */
		        
		        responseCMS.setStatus(HttpStatus.OK);
				responseCMS.setResult(result);
				return responseCMS.build();
			}
			
			/* Ghi nhận login thất bại */
			notifyLoginFail(userLogin);
			
			log.debug("Forbidden");
			responseCMS.setStatus(HttpStatus.FORBIDDEN);
			responseCMS.setMessage(LogMessages.FORBIDDEN.getMessage());
			return responseCMS.build();
		} catch (Exception e) {
			/* Ghi nhận login thất bại */
			notifyLoginFail(userLogin);
			
			e.printStackTrace();
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.BAD_REQUEST);
			responseCMS.setMessage("Username và mật khẩu không khớp");
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	private void notifyLoginWeb(LogRequestClientRequest logRequestUserInfo, User user) {
		try {
			if(logRequestUserInfo!=null && logRequestUserInfo.remote.equals("web")){
		        UserOrganization userCreator=new UserOrganization();
		        userCreator.userId=user.getId();
		        userCreator.fullName=user.fullName;
				
				Notify taskNotify=new Notify();
				taskNotify.creator=userCreator;
				taskNotify.receiver=userCreator;
				taskNotify.action=NotifyCaption.LoginWeb.getAction();
				taskNotify.title=NotifyCaption.LoginWeb.getTitle();
				taskNotify.content="Tài khoản được đăng nhập trên web lúc "+DateTimeUtil.getDatetimeFormat().format(new Date());
				taskNotify.viewed=false;
				
				if(taskNotify.creator.validNotify()) {
					/* Thông báo trên firebase */
					String topic = "giaoviecvptw_";
					String title = taskNotify.creator.fullName+" đã " +taskNotify.title.toLowerCase();
					String content = taskNotify.content;
					
					Map<String,String> data = new HashMap<String,String>();
					data.put("action", taskNotify.action);
					
					taskNotifyRepository.save(taskNotify);
					
					/* Thông báo trên firebase */
					try {
						topic = "giaoviecvptw_"+taskNotify.receiver.userId;
						firebaseService.sendToTopic(topic,title,content, data);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void notifyLoginFail(User userLogin) {
		try {
			if(userLogin!=null) {
				userLogin.loginFail=userLogin.loginFail+1;
				userRepository.save(userLogin);
				
				/* Thông báo app nếu vượt quá số lần cho phép */
				if(userLogin.loginFail>=loginFailOverNumber) {
					UserOrganization userCreator=new UserOrganization();
			        userCreator.userId=userLogin.getId();
			        userCreator.fullName=userLogin.fullName;
					
					Notify taskNotify=new Notify();
					taskNotify.creator=userCreator;
					taskNotify.receiver=userCreator;
					taskNotify.action=NotifyCaption.LoginFail.getAction();
					taskNotify.title=NotifyCaption.LoginFail.getTitle();
					taskNotify.content="Đăng nhập thất bại lần "+userLogin.loginFail+", lúc "+DateTimeUtil.getDatetimeFormat().format(new Date());
					taskNotify.viewed=false;
					
					if(taskNotify.creator.validNotify()) {
						/* Thông báo trên firebase */
						String topic = "giaoviecvptw_";
						String title = taskNotify.creator.fullName+" đã " +taskNotify.title.toLowerCase();
						String content = taskNotify.content;
						
						Map<String,String> data = new HashMap<String,String>();
						data.put("action", taskNotify.action);
						
						taskNotifyRepository.save(taskNotify);
						
						/* Thông báo trên firebase */
						try {
							topic = "giaoviecvptw_"+taskNotify.receiver.userId;
							firebaseService.sendToTopic(topic,title,content, data);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@PutMapping("/user/change-password")
	public Object changePassword(@RequestBody @Valid ReqUserChangePassword userChangePassword){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			CustomUserDetails user = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			/* Kiểm tra userId */
			User userUpdate=user.getUser();
			
			/* Check password Old */
			if(userRepositoryCustom.checkPassword(userUpdate.getId(), userChangePassword.passwordOld)==false) {
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage("Mật khẩu cũ không đúng");
				return responseCMS.build();
			}
			
			/* Cập nhật mật khẩu mới */
			userUpdate.updatedTime=userChangePassword.updatedTime;
			userUpdate.password=passwordEncoder.encode(userChangePassword.passwordNew);
			userUpdate=userRepository.save(userUpdate);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage(LogMessages.UPDATE_SUCCESS.getMessage());
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
			@RequestParam(name = "keySearch", required = false, defaultValue = "") String keySearch) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			UserFilter userFilter=new UserFilter();
			userFilter.keySearch=keySearch;
			userFilter.organizationIds=Arrays.asList(organizationId);
			int total=userRepositoryCustom.countAll(userFilter);
			List<User> users=userRepositoryCustom.findAll(userFilter, skip, limit);
			List<Document> results=new ArrayList<Document>();
			for (User item : users) {
				results.add(convertUser(item, organizationId));
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
	
	@GetMapping("/user/list/assignee")
	public Object getListAssignee(
			@RequestParam(name = "userId", required = true) String userId, 
			@RequestParam(name = "organizationId", required = true) String organizationId, 
			@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra userId và organizationId */
			User userRequest=null;
			try {
				userRequest=userRepository.findById(new ObjectId(userId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("userId không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Kiểm tra organizationId */
			Organization currentOrganization=null;
			try {
				currentOrganization=organizationRepository.findById(new ObjectId(organizationId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("organizationId không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Kiểm tra organizationId có userId không */
			boolean exists=false;
			for(UserOrganizationExpand item:userRequest.getOrganizations()) {
				if(item.getOrganizationId().equals(currentOrganization.getId())) {
					exists=true;
				}
			}
			
			if(exists==false) {
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("userId và organizationId không thuộc nhau");
				return responseCMS.build();
			}
			
			Document result=new Document();
			
			/* Lấy danh sách vai trò trong tổ chức của user */
			List<OrganizationRole> organizationRoles=organizationRoleRepositoryCustom.getRolesOrganizationUser(organizationId, userId);
			boolean truongdonvi=false, photruongdonvi=false;
			for (OrganizationRole organizationRole : organizationRoles) {
				if(organizationRole.permissionKeys.contains(Permission.truongdonvi.name())) {
					truongdonvi=true;
					break;
				}else if(organizationRole.permissionKeys.contains(Permission.photruongdonvi.name())) {
					photruongdonvi=true;
				}
			}
			
			/* Lấy các user cùng tổ chức */
			UserFilter userFilter=new UserFilter();
			userFilter.keySearch=keyword;
			userFilter.organizationIds.add(currentOrganization.getId());
			List<User> users=userRepositoryCustom.findAll(userFilter, 0, 0);
			List<Document> resultConvertUsers=new ArrayList<Document>();
			for (User item : users) {
				/* Không phải tài khoản request */
				//if(userRequest.getId().equalsIgnoreCase(item.getId())==false) {
					/* Nếu là tài khoản truongdonvi*/
					if(truongdonvi) {
						/* Lấy tất cả */
						
						/* Kiểm tra user có vai trò nhận việc không */
						Document dataUser=convertAssignee(item, currentOrganization.getId());
						if(dataUser!=null) {
							resultConvertUsers.add(dataUser);
						}
					}
					/* Nếu là tk photruongdonvi */
					else if(photruongdonvi) {
						/* Loại bỏ tk truongdonvi ra */
						organizationRoles=organizationRoleRepositoryCustom.getRolesOrganizationUser(currentOrganization.getId(), item.getId());
						boolean _truongdonvi=false;
						for (OrganizationRole organizationRole : organizationRoles) {
							if(organizationRole.permissionKeys.contains(Permission.truongdonvi.name())) {
								_truongdonvi=true;
								break;
							}
						}
						
						if(_truongdonvi==false) {
							/* Kiểm tra user có vai trò nhận việc không */
							Document dataUser=convertAssignee(item, currentOrganization.getId());
							if(dataUser!=null) {
								resultConvertUsers.add(dataUser);
							}
						}
					}
					/* Ngược lại là chuyên viên */
					else {
						/* Loại tk truongdonvi va photruongdonvi */
						organizationRoles=organizationRoleRepositoryCustom.getRolesOrganizationUser(currentOrganization.getId(), item.getId());
						boolean _photruongdonvi=false;
						for (OrganizationRole organizationRole : organizationRoles) {
							if(organizationRole.permissionKeys.contains(Permission.truongdonvi.name()) || organizationRole.permissionKeys.contains(Permission.photruongdonvi.name())) {
								_photruongdonvi=true;
								break;
							}
						}
						
						if(_photruongdonvi==false) {
							/* Kiểm tra user có vai trò nhận việc không */
							Document dataUser=convertAssignee(item, currentOrganization.getId());
							if(dataUser!=null) {
								resultConvertUsers.add(dataUser);
							}
						}
					}
				//}
			}
			Document iCurrentOrganzation=new Document();
			iCurrentOrganzation.append("organization", convertOrganization(currentOrganization));
			iCurrentOrganzation.append("users", resultConvertUsers);
			result.append("currentOrganzation", iCurrentOrganzation);
			
			/* Lấy các user subOrganization 1 cấp*/
			LinkedList<Document> iSubOrganization=new LinkedList<Document>();
			
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.parentId=currentOrganization.getId();
        	organizationFilter.keySearch=keyword;
        	
			List<Organization> subOrganizationList=organizationRepositoryCustom.findAll(organizationFilter, 0, 0);
			for (Organization organization : subOrganizationList) {
				
				/* Lấy danh sách người dùng trong organization */
				UserFilter _userFilter=new UserFilter();
				_userFilter.keySearch=keyword;
				_userFilter.organizationIds.add(organization.getId());
				List<User> _users=userRepositoryCustom.findAll(_userFilter, 0, 0);
				List<Document> _resultConvertUsers=new ArrayList<Document>();
				for (User item : _users) {
					/* Kiểm tra user có vai trò nhận việc không */
					Document dataUser=convertAssignee(item, organization.getId());
					if(dataUser!=null) {
						_resultConvertUsers.add(dataUser);
					}
				}
				
				Document iSubOrganzation=new Document();
				iSubOrganzation.append("organization", convertOrganization(organization));
				iSubOrganzation.append("users", _resultConvertUsers);
				
				iSubOrganization.add(iSubOrganzation);
			}
			result.append("subOrganzation", iSubOrganization);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(result);
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
	
	@GetMapping("/user/get")
	public Object getUser(
			@RequestParam(name = "userId", required = true) String userId,
			@RequestParam(name = "organizationId", required = true) String organizationId) {
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
			responseCMS.setResult(convertUser(user, organizationId));
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	@PutMapping("/user/edit")
	public Object updateUser(@RequestBody @Valid ReqUserEditWebsite userEdit){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			CustomUserDetails user = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			/* Kiểm tra userId */
			User userUpdate=user.getUser();
			  
			/* Lưu dữ liệu */
			userUpdate.updatedTime=userEdit.updatedTime;
			userUpdate.username=userEdit.username;
			userUpdate.email=userEdit.email;
			userUpdate.phone=userEdit.phone;
			userUpdate.fullName=userEdit.fullName;
			
			if(userEdit.config!=null) {
				userUpdate.config=userEdit.config;
			}
			userUpdate=userRepository.save(userUpdate);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage(LogMessages.UPDATE_SUCCESS.getMessage());
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
		document.append("id", organization.getId());
		document.append("name", organization.name);
		document.append("description", organization.description);
		return document;
	}
	
	protected Document convertUser(User user, String organizationId) {
		Document document=new Document();
		document.append("createdTime", user.getCreatedTime());
		document.append("updatedTime", user.getUpdatedTime());
		document.append("id", user.getId());
		document.append("username", user.username);
		document.append("email", user.email);
		document.append("phone", user.phone);
		document.append("fullName", user.fullName);
		/* Thay vì lấy vai trò, thì lấy chức vụ của user trong đơn vị đó 2022-10-14 */
		//document.append("jobTitle", organizationRoleRepositoryCustom.getRolesOrganizationUserString(organizationId, user.getId()));
		document.append("jobTitle", "");
		document.append("lastDateLogin", user.lastDateLogin);
		document.append("lastIPLogin", user.lastIPLogin);
		document.append("active", user.active);
		document.append("accountDomino", user.accountDomino);
		document.append("config", user.config);
		
		List<Document> organizations=new ArrayList<Document>();
		for(UserOrganizationExpand item:user.organizations) {
			Document userExpand=new Document();
			userExpand.append("organizationId", item.organizationId);
			userExpand.append("organizationName", item.organizationName);
			userExpand.append("accountIOffice", item.accountIOffice);
			userExpand.append("jobTitle", item.jobTitle);
			userExpand.append("numberOrder", item.numberOrder);
			organizations.add(userExpand);
			
			/* Lây chức vụ thay cho vai trò */
			if(organizationId.equalsIgnoreCase(item.getOrganizationId()) && item.jobTitle!=null) {
				document.put("jobTitle", item.jobTitle);
			}
		}
		document.append("organizations", organizations);
		
		return document;
	}
	
	protected Document convertAssignee(User user, String organizationId) {
		/* Nếu tk không được kích hoạt thì không lấy */
		if(!user.active) {
			return null;
		}
		
		/*Kiểm tra user có hợp lệ với vai trò không? Cụ thể có vai trò khongnhanviec không*/
		List<OrganizationRole> organizationRoles=organizationRoleRepositoryCustom.getRolesOrganizationUser(organizationId, user.getId());
		boolean khongnhanviec=false;
		for (OrganizationRole organizationRole : organizationRoles) {
			if(organizationRole.permissionKeys.contains(Permission.khongnhanviec.name())) {
				khongnhanviec=true;
				break;
			}
		}
		
		/* Nếu tk có vai trò không nhận việc thì không lấy */
		if(khongnhanviec) {
			return null;
		}
		
		Document document=new Document();
		document.append("createdTime", user.getCreatedTime());
		document.append("updatedTime", user.getUpdatedTime());
		document.append("id", user.getId());
		document.append("username", user.username);
		document.append("email", user.email);
		document.append("phone", user.phone);
		document.append("fullName", user.fullName);
		/* Thay vì lấy vai trò, thì lấy chức vụ của user trong đơn vị đó 2022-10-14 */
		//document.append("jobTitle", organizationRoleRepositoryCustom.getRolesOrganizationUserString(organizationId, user.getId()));
		document.append("jobTitle", "");
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
			organizations.add(userExpand);
			
			/* Lây chức vụ thay cho vai trò */
			if(organizationId.equalsIgnoreCase(item.getOrganizationId()) && item.jobTitle!=null) {
				document.put("jobTitle", item.jobTitle);
			}
		}
		document.append("organizations", organizations);
		
		return document;
	}
}
