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
			
			// X??c th???c t??? username v?? password.
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			
			// N???u kh??ng x???y ra exception t???c l?? th??ng tin h???p l???
			CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();
			
			User user=customUser.getUser();
			
			/* N???u user l?? administrator th?? kh??ng ???????c ph??p */
			if(user.username.equalsIgnoreCase("administrator")) {
				responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setMessage("T??i kho???n kh??ng ???????c ph??p truy c???p");
				return responseCMS.build();
			}
			
			/* N???u user kh??ng n???m trong organization n??o th?? tr??? v??? l???i */
	        if(user.organizations.size()==0) {
	        	responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setMessage("T??i kho???n kh??ng n???m trong t??? ch???c n??o");
				return responseCMS.build();
	        }
	        
	        /* N???u user ch??a ???????c k??ch ho???t */
	        if(user.active==false) {
	        	responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setMessage("T??i kho???n kh??ng ???????c k??ch ho???t");
				return responseCMS.build();
	        }
	        
			if(user.active) {
		        // Set th??ng tin authentication v??o Security Context
		        SecurityContextHolder.getContext().setAuthentication(authentication);
	
		        /* Write log cho login */
		        try {
		        	logRequestService.writeLogLogin(request, user);
				} catch (Exception e) {}
		        
		        /* Tr??? v??? jwt cho ng?????i d??ng. */
		        Date jwt_expiryDate=tokenProvider.generateExpiryDate();
		        String jwt_login = tokenProvider.generateToken((CustomUserDetails) authentication.getPrincipal(), jwt_expiryDate);
		        String jwt_refresh=user.createRefreshToken();
				
		        /* L??u refresh token */
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
		        
				/* N???u user kh??ng n???m trong organization n??o th?? tr??? v??? l???i */
		        if(organizations.size()==0) {
		        	responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
					responseCMS.setMessage("T??i kho???n kh??ng n???m trong t??? ch???c n??o");
					return responseCMS.build();
		        }
		        
		        for (Organization organization : organizations) {
		        	Document document=new Document();
		        	document.append("id", organization.getId());
		        	document.append("name", organization.name);
		        	document.append("leaderName", organization.leaderName);
		        	
		        	/* Th??ng tin t??i kho???n m??? r???ng */
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
	        		
					/* Th??ng tin c??c permission */
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
		        	
					/* L???y danh s??ch l??nh ?????o m?? user n??y h??? tr??? */
		        	List<UserOrganization> leadersTask=new ArrayList<UserOrganization>();
		        	List<UserOrganization> leaders=user.leaders;
		        	for (UserOrganization leader : leaders) {
						if(leader.organizationId.equalsIgnoreCase(organization.getId())) {
							leadersTask.add(leader);
						}
					}
		        	document.append("leadersTask", leadersTask);
		        	
					/* L???y danh s??ch c??n b??? m?? user n??y ???? giao h??? tr??? */
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
		        
		        /* Th??ng b??o n???u ????ng nh???p b???ng web*/
		        LogRequestClientRequest logRequestUserInfo=null;
		        try {
					logRequestUserInfo=new Gson().fromJson(request.getHeader("UserInfo"), LogRequestClientRequest.class);
					notifyLoginWeb(logRequestUserInfo, user);
				} catch (Exception e) {
					log.debug(e.getMessage());
					e.printStackTrace();
				}
				/* End th??ng b??o */
				
				/* ????ng nh???p sai */
		        user.loginFail=0;
		        user.lastDateLogin=new Date();
		        if(logRequestUserInfo!=null && logRequestUserInfo.ipaddress!=null) {
		        	user.lastIPLogin=logRequestUserInfo.ipaddress.toString();
		        }
		        userRepository.save(user);
				/* End ????ng nh???p sai */
		        
		        responseCMS.setStatus(HttpStatus.OK);
				responseCMS.setResult(result);
				return responseCMS.build();
			}
			
			/* Ghi nh???n login th???t b???i */
			notifyLoginFail(userLogin);
			
			log.debug("Forbidden");
			responseCMS.setStatus(HttpStatus.FORBIDDEN);
			responseCMS.setMessage(LogMessages.FORBIDDEN.getMessage());
			return responseCMS.build();
		} catch (Exception e) {
			/* Ghi nh???n login th???t b???i */
			notifyLoginFail(userLogin);
			
			e.printStackTrace();
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.BAD_REQUEST);
			responseCMS.setMessage("Username v?? m???t kh???u kh??ng kh???p");
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
				taskNotify.content="T??i kho???n ???????c ????ng nh???p tr??n web l??c "+DateTimeUtil.getDatetimeFormat().format(new Date());
				taskNotify.viewed=false;
				
				if(taskNotify.creator.validNotify()) {
					/* Th??ng b??o tr??n firebase */
					String topic = "giaoviecvptw_";
					String title = taskNotify.creator.fullName+" ???? " +taskNotify.title.toLowerCase();
					String content = taskNotify.content;
					
					Map<String,String> data = new HashMap<String,String>();
					data.put("action", taskNotify.action);
					
					taskNotifyRepository.save(taskNotify);
					
					/* Th??ng b??o tr??n firebase */
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
				
				/* Th??ng b??o app n???u v?????t qu?? s??? l???n cho ph??p */
				if(userLogin.loginFail>=loginFailOverNumber) {
					UserOrganization userCreator=new UserOrganization();
			        userCreator.userId=userLogin.getId();
			        userCreator.fullName=userLogin.fullName;
					
					Notify taskNotify=new Notify();
					taskNotify.creator=userCreator;
					taskNotify.receiver=userCreator;
					taskNotify.action=NotifyCaption.LoginFail.getAction();
					taskNotify.title=NotifyCaption.LoginFail.getTitle();
					taskNotify.content="????ng nh???p th???t b???i l???n "+userLogin.loginFail+", l??c "+DateTimeUtil.getDatetimeFormat().format(new Date());
					taskNotify.viewed=false;
					
					if(taskNotify.creator.validNotify()) {
						/* Th??ng b??o tr??n firebase */
						String topic = "giaoviecvptw_";
						String title = taskNotify.creator.fullName+" ???? " +taskNotify.title.toLowerCase();
						String content = taskNotify.content;
						
						Map<String,String> data = new HashMap<String,String>();
						data.put("action", taskNotify.action);
						
						taskNotifyRepository.save(taskNotify);
						
						/* Th??ng b??o tr??n firebase */
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
			
			/* Ki???m tra userId */
			User userUpdate=user.getUser();
			
			/* Check password Old */
			if(userRepositoryCustom.checkPassword(userUpdate.getId(), userChangePassword.passwordOld)==false) {
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage("M???t kh???u c?? kh??ng ????ng");
				return responseCMS.build();
			}
			
			/* C???p nh???t m???t kh???u m???i */
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
			/* Ki???m tra userId v?? organizationId */
			User userRequest=null;
			try {
				userRequest=userRepository.findById(new ObjectId(userId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("userId kh??ng t???n t???i trong h??? th???ng");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Ki???m tra organizationId */
			Organization currentOrganization=null;
			try {
				currentOrganization=organizationRepository.findById(new ObjectId(organizationId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("organizationId kh??ng t???n t???i trong h??? th???ng");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Ki???m tra organizationId c?? userId kh??ng */
			boolean exists=false;
			for(UserOrganizationExpand item:userRequest.getOrganizations()) {
				if(item.getOrganizationId().equals(currentOrganization.getId())) {
					exists=true;
				}
			}
			
			if(exists==false) {
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("userId v?? organizationId kh??ng thu???c nhau");
				return responseCMS.build();
			}
			
			Document result=new Document();
			
			/* L???y danh s??ch vai tr?? trong t??? ch???c c???a user */
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
			
			/* L???y c??c user c??ng t??? ch???c */
			UserFilter userFilter=new UserFilter();
			userFilter.keySearch=keyword;
			userFilter.organizationIds.add(currentOrganization.getId());
			List<User> users=userRepositoryCustom.findAll(userFilter, 0, 0);
			List<Document> resultConvertUsers=new ArrayList<Document>();
			for (User item : users) {
				/* Kh??ng ph???i t??i kho???n request */
				//if(userRequest.getId().equalsIgnoreCase(item.getId())==false) {
					/* N???u l?? t??i kho???n truongdonvi*/
					if(truongdonvi) {
						/* L???y t???t c??? */
						
						/* Ki???m tra user c?? vai tr?? nh???n vi???c kh??ng */
						Document dataUser=convertAssignee(item, currentOrganization.getId());
						if(dataUser!=null) {
							resultConvertUsers.add(dataUser);
						}
					}
					/* N???u l?? tk photruongdonvi */
					else if(photruongdonvi) {
						/* Lo???i b??? tk truongdonvi ra */
						organizationRoles=organizationRoleRepositoryCustom.getRolesOrganizationUser(currentOrganization.getId(), item.getId());
						boolean _truongdonvi=false;
						for (OrganizationRole organizationRole : organizationRoles) {
							if(organizationRole.permissionKeys.contains(Permission.truongdonvi.name())) {
								_truongdonvi=true;
								break;
							}
						}
						
						if(_truongdonvi==false) {
							/* Ki???m tra user c?? vai tr?? nh???n vi???c kh??ng */
							Document dataUser=convertAssignee(item, currentOrganization.getId());
							if(dataUser!=null) {
								resultConvertUsers.add(dataUser);
							}
						}
					}
					/* Ng?????c l???i l?? chuy??n vi??n */
					else {
						/* Lo???i tk truongdonvi va photruongdonvi */
						organizationRoles=organizationRoleRepositoryCustom.getRolesOrganizationUser(currentOrganization.getId(), item.getId());
						boolean _photruongdonvi=false;
						for (OrganizationRole organizationRole : organizationRoles) {
							if(organizationRole.permissionKeys.contains(Permission.truongdonvi.name()) || organizationRole.permissionKeys.contains(Permission.photruongdonvi.name())) {
								_photruongdonvi=true;
								break;
							}
						}
						
						if(_photruongdonvi==false) {
							/* Ki???m tra user c?? vai tr?? nh???n vi???c kh??ng */
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
			
			/* L???y c??c user subOrganization 1 c???p*/
			LinkedList<Document> iSubOrganization=new LinkedList<Document>();
			
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.parentId=currentOrganization.getId();
        	organizationFilter.keySearch=keyword;
        	
			List<Organization> subOrganizationList=organizationRepositoryCustom.findAll(organizationFilter, 0, 0);
			for (Organization organization : subOrganizationList) {
				
				/* L???y danh s??ch ng?????i d??ng trong organization */
				UserFilter _userFilter=new UserFilter();
				_userFilter.keySearch=keyword;
				_userFilter.organizationIds.add(organization.getId());
				List<User> _users=userRepositoryCustom.findAll(_userFilter, 0, 0);
				List<Document> _resultConvertUsers=new ArrayList<Document>();
				for (User item : _users) {
					/* Ki???m tra user c?? vai tr?? nh???n vi???c kh??ng */
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
			
			/* Ki???m tra userId */
			User userUpdate=user.getUser();
			  
			/* L??u d??? li???u */
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
		/* Thay v?? l???y vai tr??, th?? l???y ch???c v??? c???a user trong ????n v??? ???? 2022-10-14 */
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
			
			/* L??y ch???c v??? thay cho vai tr?? */
			if(organizationId.equalsIgnoreCase(item.getOrganizationId()) && item.jobTitle!=null) {
				document.put("jobTitle", item.jobTitle);
			}
		}
		document.append("organizations", organizations);
		
		return document;
	}
	
	protected Document convertAssignee(User user, String organizationId) {
		/* N???u tk kh??ng ???????c k??ch ho???t th?? kh??ng l???y */
		if(!user.active) {
			return null;
		}
		
		/*Ki???m tra user c?? h???p l??? v???i vai tr?? kh??ng? C??? th??? c?? vai tr?? khongnhanviec kh??ng*/
		List<OrganizationRole> organizationRoles=organizationRoleRepositoryCustom.getRolesOrganizationUser(organizationId, user.getId());
		boolean khongnhanviec=false;
		for (OrganizationRole organizationRole : organizationRoles) {
			if(organizationRole.permissionKeys.contains(Permission.khongnhanviec.name())) {
				khongnhanviec=true;
				break;
			}
		}
		
		/* N???u tk c?? vai tr?? kh??ng nh???n vi???c th?? kh??ng l???y */
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
		/* Thay v?? l???y vai tr??, th?? l???y ch???c v??? c???a user trong ????n v??? ???? 2022-10-14 */
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
			
			/* L??y ch???c v??? thay cho vai tr?? */
			if(organizationId.equalsIgnoreCase(item.getOrganizationId()) && item.jobTitle!=null) {
				document.put("jobTitle", item.jobTitle);
			}
		}
		document.append("organizations", organizations);
		
		return document;
	}
}
