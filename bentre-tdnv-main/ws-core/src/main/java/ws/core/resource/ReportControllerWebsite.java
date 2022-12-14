package ws.core.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ws.core.enums.LogMessages;
import ws.core.enums.Permission;
import ws.core.enums.TaskAssignmentType;
import ws.core.enums.TaskCategory;
import ws.core.enums.TaskPriority;
import ws.core.enums.TaskSubCategory;
import ws.core.model.Organization;
import ws.core.model.OrganizationRole;
import ws.core.model.Task;
import ws.core.model.User;
import ws.core.model.UserOrganization;
import ws.core.model.embeded.UserOrganizationExpand;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.filter.TaskFilter;
import ws.core.model.filter.UserFilter;
import ws.core.repository.OrganizationRepository;
import ws.core.repository.OrganizationRoleRepository;
import ws.core.repository.OrganizationRoleRepositoryCustom;
import ws.core.repository.TaskRepository;
import ws.core.repository.TaskRepositoryCustom;
import ws.core.repository.UserRepository;
import ws.core.repository.UserRepositoryCustom;
import ws.core.repository.imp.OrganizationRepositoryCustomImp;
import ws.core.service.OrganizationService;
import ws.core.service.TaskAttachmentService;
import ws.core.service.TaskService;
import ws.core.util.ResponseCMS;

@RestController
@RequestMapping("/website")
public class ReportControllerWebsite {
	private Logger log = LogManager.getLogger(ReportControllerWebsite.class);

	@Autowired
	protected TaskRepository taskRepository;

	@Autowired
	protected TaskRepositoryCustom taskRepositoryCustom;

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
	protected TaskService taskService;

	@Autowired
	protected TaskAttachmentService taskAttachmentService;

	@GetMapping("/report/task-list")
	public Object getList(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "userId", required = true) String userId, 
			@RequestParam(name = "organizationId", required = true) String organizationId, 
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "categorykey", required = true) String categorykey, 
			@RequestParam(name = "subcategorykey", required = false) String subcategorykey,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "priority", required = false, defaultValue = "0") int priority,
			@RequestParam(name = "findOwners", required = false) String findOwners,
			@RequestParam(name = "findAssistants", required = false) String findAssistants,
			@RequestParam(name = "findAssignees", required = false) String findAssignees,
			@RequestParam(name = "findFollowers", required = false) String findFollowers,
			@RequestParam(name = "assignmentType", required = false) String assignmentType) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			UserOrganization userTask=new UserOrganization();
			userTask.userId=userId;
			userTask.organizationId=organizationId;

			TaskFilter taskFilter=new TaskFilter();
			taskFilter.keySearch=keyword;
			taskFilter.userTask=userTask;
			taskFilter.fromDate=fromDate;
			taskFilter.toDate=toDate;
			taskFilter.priority=priority;
			taskFilter.findOwners=findOwners;
			taskFilter.findAssistants=findAssistants;
			taskFilter.findAssignees=findAssignees;
			taskFilter.findFollowers=findFollowers;
			
			/* Ki???m tra categorykey */
			if(categorykey!=null && !categorykey.isEmpty()) {
				TaskCategory taskCategory=TaskCategory.getTaskCategory(categorykey);
				if(taskCategory==null) {
					responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
					responseCMS.setResult("categorykey kh??ng ???????c ch???p nh???n");
					return responseCMS.build();
				}
				taskFilter.taskCategory=taskCategory;
			}

			/* Ki???m tra subcategorykey */
			if(subcategorykey!=null && !subcategorykey.isEmpty()) {
				TaskSubCategory taskSubCategory=TaskSubCategory.getTaskSubCategory(subcategorykey);
				if(taskSubCategory==null) {
					responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
					responseCMS.setResult("taskSubCategory kh??ng ???????c ch???p nh???n");
					return responseCMS.build();
				}
				taskFilter.taskSubCategory=taskSubCategory;
			}

			/* Ki???m tra assignmentType */
			if(assignmentType!=null) {
				if(!EnumUtils.isValidEnum(TaskAssignmentType.class, assignmentType)) {
					responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
					responseCMS.setResult("assignmentType kh??ng ???????c ch???p nh???n");
					return responseCMS.build();
				}
				taskFilter.taskAssignmentType=EnumUtils.getEnum(TaskAssignmentType.class, assignmentType);
			}
					
			int total=taskRepositoryCustom.countAll(taskFilter);
			List<Task> tasks=taskRepositoryCustom.findAll(taskFilter, skip, limit);
			List<Document> results=new ArrayList<Document>();
			for (Task item : tasks) {
				results.add(convertTaskList(item));
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

	@GetMapping("/report/task-filter")
	public Object getTaskFilter(
			@RequestParam(name = "userId", required = true) String userId, 
			@RequestParam(name = "organizationId", required = true) String organizationId,
			@RequestParam(name = "assignmentType", required = false) String assignmentType) {
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
			
			/* Ki???m tra assignmentType */
			TaskAssignmentType taskAssignmentType=null;
			if(assignmentType!=null) {
				if(!EnumUtils.isValidEnum(TaskAssignmentType.class, assignmentType)) {
					responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
					responseCMS.setResult("assignmentType kh??ng ???????c ch???p nh???n");
					return responseCMS.build();
				}
				taskAssignmentType=EnumUtils.getEnum(TaskAssignmentType.class, assignmentType);
			}
			
			/* Data tr??? v??? */
			Document result=new Document();
			
			/* L???y danh s??ch l??nh ?????o m?? user n??y h??? tr??? */
        	List<Document> leadersTask=new ArrayList<Document>();
        	List<UserOrganization> leaders=userRequest.leaders;
        	for (UserOrganization leader : leaders) {
				if(leader.organizationId.equalsIgnoreCase(currentOrganization.getId())) {
					Document data=new Document();
					data.append("key", leader.getUserId()+"-"+leader.getOrganizationId());
					data.append("value", leader.getFullName()+" - "+leader.getOrganizationName());
					leadersTask.add(data);
				}
			}
        	result.append("leadersTask", leadersTask);
        	
			/* L???y danh s??ch c??n b??? m?? user n??y ???? giao h??? tr??? */
        	List<Document> assistantsTask=new ArrayList<Document>();
        	UserFilter userFilter=new UserFilter();
        	userFilter.leader=new UserOrganization();
        	userFilter.leader.userId=userRequest.getId();
        	userFilter.leader.organizationId=currentOrganization.getId();
        	List<User> findAssistants=userRepositoryCustom.findAll(userFilter, 0, 0);
        	
        	for (User assistant : findAssistants) {
        		for (UserOrganization leader : assistant.leaders) {
					if(leader.userId.equalsIgnoreCase(userRequest.getId()) && leader.organizationId.equalsIgnoreCase(currentOrganization.getId())) {
						Document data=new Document();
						data.append("key", assistant.getId()+"-"+currentOrganization.getId());
						data.append("value", assistant.getFullName()+" - "+currentOrganization.getName());
						assistantsTask.add(data);
						break;
					}
				}
			}
        	result.append("assistantsTask", assistantsTask);
        	
        	result.append("ownersTask", getOwnersTask(userRequest, currentOrganization, taskAssignmentType));
			result.append("assigneesTask", getAssigneesTask(userRequest, currentOrganization, taskAssignmentType));
			
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
	
	private List<Document> getOwnersTask(User userRequest, Organization currentOrganization, TaskAssignmentType taskAssignmentType){
		List<Document> ownersTask=new ArrayList<Document>();
		
		/* L???y c??c user parent c???p tr??n (n???u c??)*/
		if(StringUtils.isEmpty(currentOrganization.getParentId())==false) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter._id=new ObjectId(currentOrganization.getParentId());
			List<Organization> subOrganizationList=organizationRepositoryCustom.findAll(organizationFilter, 0, 0);
			for (Organization organization : subOrganizationList) {
				
				/* L???y th??ng tin organization */
				if(taskAssignmentType==null || (taskAssignmentType!=null && taskAssignmentType==TaskAssignmentType.Organization)) {
					Document dataUser=convertOrganizationOwner(organization);
					if(dataUser!=null) {
						ownersTask.add(dataUser);
					}
				}
				
				/* L???y danh s??ch ng?????i d??ng trong organization */
				if(taskAssignmentType==null || (taskAssignmentType!=null && taskAssignmentType==TaskAssignmentType.User)) {
					UserFilter userSubFilter=new UserFilter();
					userSubFilter.organizationIds.add(organization.getId());
					List<User> usersSub=userRepositoryCustom.findAll(userSubFilter, 0, 0);
					for (User item : usersSub) {
						/* Ki???m tra user c?? vai tr?? giaonhiemvu kh??ng */
						Document dataUser=convertUserOrganizationOwner(item, organization.getId());
						if(dataUser!=null) {
							ownersTask.add(dataUser);
						}
					}
				}
			}
		}
		
		/* L???y th??ng tin currentOrganization cho owner*/
		if(taskAssignmentType==null || (taskAssignmentType!=null && taskAssignmentType==TaskAssignmentType.Organization)) {
			Document dataUser=convertOrganizationOwner(currentOrganization);
			if(dataUser!=null) {
				ownersTask.add(dataUser);
			}
		}
		
		/* L???y user owner c??ng t??? ch???c */
		if(taskAssignmentType==null || (taskAssignmentType!=null && taskAssignmentType==TaskAssignmentType.User)) {
			UserFilter userFilter=new UserFilter();
			userFilter.organizationIds.add(currentOrganization.getId());
			List<User> users=userRepositoryCustom.findAll(userFilter, 0, 0);
			for (User item : users) {
				/* Ki???m tra user c?? vai tr?? giaonhiemvu kh??ng */
				Document dataUser=convertUserOrganizationOwner(item, currentOrganization.getId());
				if(dataUser!=null) {
					ownersTask.add(dataUser);
				}
			}
		}
		
		return ownersTask;
	}
	
	private List<Document> getAssigneesTask(User userRequest, Organization currentOrganization, TaskAssignmentType taskAssignmentType){
		List<Document> assigneesTask=new ArrayList<Document>();
		
		/* L???y danh s??ch vai tr?? trong t??? ch???c c???a user */
		List<OrganizationRole> organizationRoles=organizationRoleRepositoryCustom.getRolesOrganizationUser(currentOrganization.getId(), userRequest.getId());
		boolean truongdonvi=false, photruongdonvi=false;
		for (OrganizationRole organizationRole : organizationRoles) {
			if(organizationRole.permissionKeys.contains(Permission.truongdonvi.name())) {
				truongdonvi=true;
				break;
			}else if(organizationRole.permissionKeys.contains(Permission.photruongdonvi.name())) {
				photruongdonvi=true;
			}
		}
		
		/* L???y th??ng tin currentOrganization cho owner v?? assignee*/
		if(taskAssignmentType==null || (taskAssignmentType!=null && taskAssignmentType==TaskAssignmentType.Organization)) {
			Document dataUser=convertOrganizationAssignee(currentOrganization);
			if(dataUser!=null) {
				assigneesTask.add(dataUser);
			}
		}
		
		/* L???y user assignee */
		if(taskAssignmentType==null || (taskAssignmentType!=null && taskAssignmentType==TaskAssignmentType.User)) {
			UserFilter userFilter=new UserFilter();
			userFilter.organizationIds.add(currentOrganization.getId());
			List<User> users=userRepositoryCustom.findAll(userFilter, 0, 0);
			for (User item : users) {
				/* Ki???m tra user c?? vai tr?? nh???n vi???c kh??ng */
				Document dataUser=convertUserOrganizationAssignee(item, currentOrganization.getId());
				
				/* N???u l?? t??i kho???n truongdonvi*/
				if(truongdonvi) {
					/* L???y t???t c??? */
					if(dataUser!=null) {
						assigneesTask.add(dataUser);
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
						if(dataUser!=null) {
							assigneesTask.add(dataUser);
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
						if(dataUser!=null) {
							assigneesTask.add(dataUser);
						}
					}
				}
			}
		}
		
		/* L???y c??c user subOrganization 1 c???p*/
		OrganizationFilter organizationFilter=new OrganizationFilter();
		organizationFilter.parentId=currentOrganization.getId();
		List<Organization> subOrganizationList=organizationRepositoryCustom.findAll(organizationFilter, 0, 0);
		for (Organization organization : subOrganizationList) {
			
			/* L???y th??ng tin organization */
			if(taskAssignmentType==null || (taskAssignmentType!=null && taskAssignmentType==TaskAssignmentType.Organization)) {
				Document dataUser=convertOrganizationAssignee(organization);
				if(dataUser!=null) {
					assigneesTask.add(dataUser);
				}
			}
			
			/* L???y danh s??ch ng?????i d??ng trong organization */
			if(taskAssignmentType==null || (taskAssignmentType!=null && taskAssignmentType==TaskAssignmentType.User)) {
				UserFilter userSubFilter=new UserFilter();
				userSubFilter.organizationIds.add(organization.getId());
				List<User> usersSub=userRepositoryCustom.findAll(userSubFilter, 0, 0);
				for (User item : usersSub) {
					/* Ki???m tra user c?? vai tr?? nh???n vi???c kh??ng */
					Document dataUser=convertUserOrganizationAssignee(item, organization.getId());
					if(dataUser!=null) {
						assigneesTask.add(dataUser);
					}
				}
			}
		}
		
		return assigneesTask;
	}
	
	/*---------------------------------- convert --------------------------------*/
	protected Document convertTaskList(Task task) {
		Document document=new Document();
		document.append("createdTime", task.getCreatedTime());
		document.append("id", task.getId());
		document.append("owner", task.ownerTask);
		document.append("assignee", task.assigneeTask);
		document.append("followersTask", task.followersTask);
		document.append("title", task.title);
		document.append("description", task.description);
		document.append("priorityName", TaskPriority.getName(task.priority));
		document.append("endTime", task.getEndTime());
		document.append("completedTime", task.getCompletedTime());
		
		if(task.processes.size()>0) {
			Document process=new Document();
			process.append("percent", task.processes.getFirst().percent);
			process.append("explain", task.processes.getFirst().explain);
			document.append("process", process);
		}else {
			document.append("process", null);
		}
		
		return document;
	}
	
	protected Document convertOrganizationOwner(Organization organizationId) {
		Document document=new Document();
		document.append("key", organizationId.getId());
		document.append("value", organizationId.getName());
		return document;
	}
	
	protected Document convertOrganizationAssignee(Organization organizationId) {
		Document document=new Document();
		document.append("key", organizationId.getId());
		document.append("value", organizationId.getName());
		return document;
	}
	
	protected Document convertUserOrganizationOwner(User user, String organizationId) {
		/* N???u tk kh??ng ???????c k??ch ho???t th?? kh??ng l???y */
		if(!user.active) {
			return null;
		}
		
		/*Ki???m tra user c?? h???p l??? v???i vai tr?? kh??ng? C??? th??? c?? vai tr?? giaonhiemvu kh??ng*/
		List<OrganizationRole> organizationRoles=organizationRoleRepositoryCustom.getRolesOrganizationUser(organizationId, user.getId());
		boolean giaonhiemvu=false;
		for (OrganizationRole organizationRole : organizationRoles) {
			if(organizationRole.permissionKeys.contains(Permission.giaonhiemvu.name())) {
				giaonhiemvu=true;
				break;
			}
		}
		
		/* N???u tk c?? vai tr?? kh??ng nh???n vi???c th?? kh??ng l???y */
		if(giaonhiemvu==false) {
			return null;
		}
		
		for(UserOrganizationExpand item:user.organizations) {
			if(organizationId.equalsIgnoreCase(item.getOrganizationId())) {
				String key=user.getId()+"-"+item.getOrganizationId();
				String value=user.getFullName()+" - "+item.getOrganizationName();
				
				Document document=new Document();
				document.append("key", key);
				document.append("value", value);
				
				return document;
			}
		}
		return null;
	}
	
	protected Document convertUserOrganizationAssignee(User user, String organizationId) {
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
		
		for(UserOrganizationExpand item:user.organizations) {
			if(organizationId.equalsIgnoreCase(item.getOrganizationId())) {
				String key=user.getId()+"-"+item.getOrganizationId();
				String value=user.getFullName()+" - "+item.getOrganizationName();
				
				Document document=new Document();
				document.append("key", key);
				document.append("value", value);
				
				return document;
			}
		}
		return null;
	}
}
