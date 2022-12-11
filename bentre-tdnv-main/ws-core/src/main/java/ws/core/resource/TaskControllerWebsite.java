package ws.core.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import ws.core.enums.NotifyCaption;
import ws.core.enums.Permission;
import ws.core.enums.TaskAssignmentStatus;
import ws.core.enums.TaskAssignmentType;
import ws.core.enums.TaskCategory;
import ws.core.enums.TaskPriority;
import ws.core.enums.TaskSubCategory;
import ws.core.model.Attachment;
import ws.core.model.Notify;
import ws.core.model.Organization;
import ws.core.model.Tag;
import ws.core.model.Task;
import ws.core.model.TaskAttachment;
import ws.core.model.TaskComment;
import ws.core.model.TaskEvent;
import ws.core.model.TaskProcess;
import ws.core.model.TaskRating;
import ws.core.model.TaskRemind;
import ws.core.model.User;
import ws.core.model.UserOrganization;
import ws.core.model.UserTaskCount;
import ws.core.model.UserTaskId;
import ws.core.model.embeded.UserOrganizationExpand;
import ws.core.model.fields.TaskSum;
import ws.core.model.filter.TagFilter;
import ws.core.model.filter.TaskFilter;
import ws.core.model.request.ReqTaskAccept;
import ws.core.model.request.ReqTaskAttachFile;
import ws.core.model.request.ReqTaskCommentCreate;
import ws.core.model.request.ReqTaskComplete;
import ws.core.model.request.ReqTaskCreate;
import ws.core.model.request.ReqTaskEdit;
import ws.core.model.request.ReqTaskProcessCreate;
import ws.core.model.request.ReqTaskRatingEdit;
import ws.core.model.request.ReqTaskReDo;
import ws.core.model.request.ReqTaskRemindCreate;
import ws.core.model.request.ReqTaskSetAssigneeUser;
import ws.core.model.request.ReqTaskSetFollowUser;
import ws.core.model.request.ReqTaskUnSetAssigneeUser;
import ws.core.model.request.ReqTaskUnSetFollowUser;
import ws.core.repository.NotifyRepository;
import ws.core.repository.NotifyRepositoryCustom;
import ws.core.repository.OrganizationRepository;
import ws.core.repository.OrganizationRoleRepository;
import ws.core.repository.OrganizationRoleRepositoryCustom;
import ws.core.repository.TagRepository;
import ws.core.repository.TagRepositoryCustom;
import ws.core.repository.TaskRepository;
import ws.core.repository.TaskRepositoryCustom;
import ws.core.repository.UserRepository;
import ws.core.repository.UserRepositoryCustom;
import ws.core.repository.imp.OrganizationRepositoryCustomImp;
import ws.core.security.CustomUserDetails;
import ws.core.service.FirebaseService;
import ws.core.service.OrganizationRoleService;
import ws.core.service.OrganizationService;
import ws.core.service.TaskAttachmentService;
import ws.core.service.TaskService;
import ws.core.util.DateTimeUtil;
import ws.core.util.ResponseCMS;

// TODO: Auto-generated Javadoc
/**
 * The Class TaskControllerWebsite.
 */
@RestController
@RequestMapping("/website")
public class TaskControllerWebsite {
	
	/** The log. */
	private Logger log = LogManager.getLogger(TaskControllerWebsite.class);

	/** The task repository. */
	@Autowired
	protected TaskRepository taskRepository;

	/** The task repository custom. */
	@Autowired
	protected TaskRepositoryCustom taskRepositoryCustom;

	/** The user repository. */
	@Autowired
	protected UserRepository userRepository;

	/** The user repository custom. */
	@Autowired
	protected UserRepositoryCustom userRepositoryCustom;

	/** The password encoder. */
	@Autowired
	protected PasswordEncoder passwordEncoder;

	/** The organization repository. */
	@Autowired
	protected OrganizationRepository organizationRepository;

	/** The organization repository custom. */
	@Autowired
	protected OrganizationRepositoryCustomImp organizationRepositoryCustom;

	/** The organization role repository. */
	@Autowired
	protected OrganizationRoleRepository organizationRoleRepository;

	/** The organization role repository custom. */
	@Autowired
	protected OrganizationRoleRepositoryCustom organizationRoleRepositoryCustom;

	/** The task notify repository. */
	@Autowired
	protected NotifyRepository taskNotifyRepository;
	
	/** The task notify repository custom. */
	@Autowired
	protected NotifyRepositoryCustom taskNotifyRepositoryCustom;
	
	/** The organization service. */
	@Autowired
	protected OrganizationService organizationService;

	/** The task service. */
	@Autowired
	protected TaskService taskService;

	/** The task attachment service. */
	@Autowired
	protected TaskAttachmentService taskAttachmentService;

	/** The firebase service. */
	@Autowired
	protected FirebaseService firebaseService;
	
	@Autowired
	protected TagRepository tagRepository;

	@Autowired
	protected TagRepositoryCustom tagRepositoryCustom;
	
	@Autowired
	protected TagControllerWebsite tagControllerWebsite;
	
	@Autowired
	protected OrganizationRoleService organizationRoleService;
	
	/**
	 * Gets the list.
	 *
	 * @param skip the skip
	 * @param limit the limit
	 * @param userId the user id
	 * @param organizationId the organization id
	 * @param fromDate the from date
	 * @param toDate the to date
	 * @param categorykey the categorykey
	 * @param subcategorykey the subcategorykey
	 * @param keyword the keyword
	 * @param priority the priority
	 * @param findOwners the find owners
	 * @param findAssistants the find assistants
	 * @param findAssignees the find assignees
	 * @param findFollowers the find followers
	 * @return the list
	 */
	@GetMapping("/task/list")
	public Object getList(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "userId", required = true) String userId, 
			@RequestParam(name = "organizationId", required = true) String organizationId, 
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "categorykey", required = true, defaultValue = "") String categorykey, 
			@RequestParam(name = "subcategorykey", required = false, defaultValue = "") String subcategorykey,
			@RequestParam(name = "assignmentType", required = false) String assignmentType,
			@RequestParam(name = "assignmentStatus", required = false) String assignmentStatus,
			@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
			@RequestParam(name = "priority", required = false, defaultValue = "0") int priority,
			@RequestParam(name = "findOwners", required = false, defaultValue = "") String findOwners,
			@RequestParam(name = "findAssistants", required = false, defaultValue = "") String findAssistants,
			@RequestParam(name = "findAssignees", required = false, defaultValue = "") String findAssignees,
			@RequestParam(name = "findFollowers", required = false, defaultValue = "") String findFollowers) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			UserOrganization userTask=new UserOrganization();
			userTask.userId=userId;
			userTask.organizationId=organizationId;

			/* Nếu tài khoản có permission xemnhiemvudonvi thì cho coi toàn bộ */
			boolean xemnhiemvudonvi=organizationRoleService.hasRole(organizationId, userId, Permission.xemnhiemvudonvi.name());
			if(xemnhiemvudonvi) {
				userTask.userId=null;
			}
			
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
			
			/* Kiểm tra categorykey */
			if(StringUtils.isEmpty(categorykey)==false) {
				if(EnumUtils.isValidEnumIgnoreCase(TaskCategory.class, categorykey)==false) {
					responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
					responseCMS.setMessage("categorykey không được chấp nhận");
					return responseCMS.build();
				}
				TaskCategory taskCategory=EnumUtils.getEnumIgnoreCase(TaskCategory.class, categorykey);
				taskFilter.taskCategory=taskCategory;
			}

			/* Kiểm tra subcategorykey */
			if(StringUtils.isEmpty(subcategorykey)==false) {
				if(EnumUtils.isValidEnumIgnoreCase(TaskSubCategory.class, subcategorykey)==false) {
					responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
					responseCMS.setMessage("subcategorykey không được chấp nhận");
					return responseCMS.build();
				}
				TaskSubCategory taskSubCategory=EnumUtils.getEnumIgnoreCase(TaskSubCategory.class, subcategorykey);
				taskFilter.taskSubCategory=taskSubCategory;
			}

			/* Kiểm tra assignmentType */
			if(StringUtils.isEmpty(assignmentType)==false) {
				if(EnumUtils.isValidEnumIgnoreCase(TaskAssignmentType.class, assignmentType)==false) {
					responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
					responseCMS.setMessage("assignmentType không được chấp nhận");
					return responseCMS.build();
				}
				TaskAssignmentType taskAssignmentType=EnumUtils.getEnumIgnoreCase(TaskAssignmentType.class, assignmentType);
				taskFilter.taskAssignmentType=taskAssignmentType;
				
				/* CHÚ Ý: Bỏ field userId nếu là tìm theo assignmentType=Organization */
				if(taskAssignmentType==TaskAssignmentType.Organization) {
					userTask.userId=null;
				}
			}
			
			/* Kiểm tra taskAssignmentStatus */
			if(StringUtils.isEmpty(assignmentStatus)==false) {
				if(EnumUtils.isValidEnumIgnoreCase(TaskAssignmentStatus.class, assignmentStatus)==false) {
					responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
					responseCMS.setMessage("taskAssignmentStatus không được chấp nhận");
					return responseCMS.build();
				}
				TaskAssignmentStatus taskAssignmentStatus=EnumUtils.getEnumIgnoreCase(TaskAssignmentStatus.class, assignmentStatus);
				taskFilter.taskAssignmentStatus=taskAssignmentStatus;
			}
			
			int total=taskRepositoryCustom.countAll(taskFilter);
			List<Task> tasks=taskRepositoryCustom.findAll(taskFilter, skip, limit);
			List<Document> results=new ArrayList<Document>();
			for (Task item : tasks) {
				results.add(convertTaskList(item, userTask));
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
	
	/**
	 * Gets the list by doc id.
	 *
	 * @param docId the doc id
	 * @return the list by doc id
	 */
	@GetMapping("/task/list/{docId}")
	public Object getListByDocId(@PathVariable(name = "docId", required = true) String docId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			TaskFilter taskFilter=new TaskFilter();
			taskFilter.docId=docId;
			
			int total=taskRepositoryCustom.countAll(taskFilter);
			List<Task> tasks=taskRepositoryCustom.findAll(taskFilter, 0, 0);
			List<Document> results=new ArrayList<Document>();
			for (Task item : tasks) {
				results.add(convertTaskList(item, null));
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
	
	/**
	 * Gets the list user task.
	 *
	 * @param userId the user id
	 * @param organizationId the organization id
	 * @param fromDate the from date
	 * @param toDate the to date
	 * @param categorykey the categorykey
	 * @param subcategorykey the subcategorykey
	 * @param keyword the keyword
	 * @param priority the priority
	 * @return the list user task
	 */
	@GetMapping("/task/list/usertask")
	public Object getListUserTask(
			@RequestParam(name = "userId", required = true) String userId, 
			@RequestParam(name = "organizationId", required = true) String organizationId, 
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "categorykey", required = true, defaultValue = "") String categorykey, 
			@RequestParam(name = "subcategorykey", required = false, defaultValue = "") String subcategorykey,
			@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
			@RequestParam(name = "priority", required = false, defaultValue = "0") int priority) {
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
			
			/* Kiểm tra categorykey */
			TaskCategory taskCategory=null;
			if(categorykey!=null && !categorykey.isEmpty()) {
				taskCategory=TaskCategory.getTaskCategory(categorykey);
				if(taskCategory==null) {
					responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
					responseCMS.setResult("categorykey không được chấp nhận");
					return responseCMS.build();
				}
				taskFilter.taskCategory=taskCategory;
			}else {
//				responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
//				responseCMS.setResult("categorykey không được rỗng");
//				return responseCMS.build();
			}

			/* Kiểm tra subcategorykey */
			if(subcategorykey!=null && !subcategorykey.isEmpty()) {
				TaskSubCategory taskSubCategory=TaskSubCategory.getTaskSubCategory(subcategorykey);
				if(taskSubCategory==null) {
					responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
					responseCMS.setResult("taskSubCategory không được chấp nhận");
					return responseCMS.build();
				}
				taskFilter.taskSubCategory=taskSubCategory;
			}
			
			Document result = new Document();
			if(taskCategory!=null) {
				switch (taskCategory) {
					case DAGIAO:{
						List<UserTaskCount> userTaskAssignees=taskRepositoryCustom.getAssigneeList(taskFilter);
						List<UserTaskCount> userTaskSuports=taskRepositoryCustom.getSuportList(taskFilter);
						result.append("nguoixuly", userTaskAssignees);
						result.append("nguoihotro", userTaskSuports);
						break;
					}
					case DUOCGIAO: {
						List<UserTaskCount> userTaskOwners=taskRepositoryCustom.getOwnerList(taskFilter);
						List<UserTaskCount> userTaskSuports=taskRepositoryCustom.getSuportList(taskFilter);
						result.append("nguoigiao", userTaskOwners);
						result.append("nguoihotro", userTaskSuports);
						break;
					}
					case THEODOI:{
						List<UserTaskCount> userTaskOwners=taskRepositoryCustom.getOwnerList(taskFilter);
						List<UserTaskCount> userTaskAssignees=taskRepositoryCustom.getAssigneeList(taskFilter);
						result.append("nguoigiao", userTaskOwners);
						result.append("nguoixuly", userTaskAssignees);
						break;
					}
					case GIAOVIECTHAY:{
						List<UserTaskCount> userTaskAssignees=taskRepositoryCustom.getAssigneeList(taskFilter);
						List<UserTaskCount> userTaskSuports=taskRepositoryCustom.getSuportList(taskFilter);
						result.append("nguoixuly", userTaskAssignees);
						result.append("nguoihotro", userTaskSuports);
						break;
					}
					case THEODOITHAY:{
						List<UserTaskCount> userTaskAssignees=taskRepositoryCustom.getAssigneeList(taskFilter);
						List<UserTaskCount> userTaskSuports=taskRepositoryCustom.getSuportList(taskFilter);
						result.append("nguoixuly", userTaskAssignees);
						result.append("nguoihotro", userTaskSuports);
						break;
					}
				}
			}else {
				List<UserTaskCount> userTaskOwners=taskRepositoryCustom.getOwnerList(taskFilter);
				List<UserTaskCount> userTaskAssignees=taskRepositoryCustom.getAssigneeList(taskFilter);
				List<UserTaskCount> userTaskSuports=taskRepositoryCustom.getSuportList(taskFilter);
				result.append("nguoigiao", userTaskOwners);
				result.append("nguoixuly", userTaskAssignees);
				result.append("nguoihotro", userTaskSuports);
			}
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
	
	/**
	 * Count.
	 *
	 * @param userId the user id
	 * @param organizationId the organization id
	 * @param fromDate the from date
	 * @param toDate the to date
	 * @param categorykey the categorykey
	 * @param subcategorykey the subcategorykey
	 * @param keyword the keyword
	 * @param priority the priority
	 * @param findOwners the find owners
	 * @param findAssistants the find assistants
	 * @param findAssignees the find assignees
	 * @param findFollowers the find followers
	 * @return the object
	 */
	@GetMapping("/task/count")
	public Object count(
			@RequestParam(name = "userId", required = true) String userId, 
			@RequestParam(name = "organizationId", required = true) String organizationId, 
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "categorykey", required = true, defaultValue = "") String categorykey, 
			@RequestParam(name = "subcategorykey", required = false, defaultValue = "") String subcategorykey,
			@RequestParam(name = "assignmentType", required = false) String assignmentType,
			@RequestParam(name = "assignmentStatus", required = false) String assignmentStatus,
			@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
			@RequestParam(name = "priority", required = false, defaultValue = "0") int priority,
			@RequestParam(name = "findOwners", required = false, defaultValue = "") String findOwners,
			@RequestParam(name = "findAssistants", required = false, defaultValue = "") String findAssistants,
			@RequestParam(name = "findAssignees", required = false, defaultValue = "") String findAssignees,
			@RequestParam(name = "findFollowers", required = false, defaultValue = "") String findFollowers) {
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
			
			/* Kiểm tra categorykey */
			if(StringUtils.isEmpty(categorykey)==false) {
				if(EnumUtils.isValidEnumIgnoreCase(TaskCategory.class, categorykey)==false) {
					responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
					responseCMS.setMessage("categorykey không được chấp nhận");
					return responseCMS.build();
				}
				TaskCategory taskCategory=EnumUtils.getEnumIgnoreCase(TaskCategory.class, categorykey);
				taskFilter.taskCategory=taskCategory;
			}

			/* Kiểm tra subcategorykey */
			if(StringUtils.isEmpty(subcategorykey)==false) {
				if(EnumUtils.isValidEnumIgnoreCase(TaskSubCategory.class, subcategorykey)==false) {
					responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
					responseCMS.setMessage("subcategorykey không được chấp nhận");
					return responseCMS.build();
				}
				TaskSubCategory taskSubCategory=EnumUtils.getEnumIgnoreCase(TaskSubCategory.class, subcategorykey);
				taskFilter.taskSubCategory=taskSubCategory;
			}

			/* Kiểm tra assignmentType */
			if(StringUtils.isEmpty(assignmentType)==false) {
				if(EnumUtils.isValidEnumIgnoreCase(TaskAssignmentType.class, assignmentType)==false) {
					responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
					responseCMS.setMessage("assignmentType không được chấp nhận");
					return responseCMS.build();
				}
				TaskAssignmentType taskAssignmentType=EnumUtils.getEnumIgnoreCase(TaskAssignmentType.class, assignmentType);
				taskFilter.taskAssignmentType=taskAssignmentType;
				
				/* CHÚ Ý: Bỏ field userId nếu là tìm theo assignmentType=Organization */
				if(taskAssignmentType==TaskAssignmentType.Organization) {
					userTask.userId=null;
				}
			}
			
			/* Kiểm tra taskAssignmentStatus */
			if(StringUtils.isEmpty(assignmentStatus)==false) {
				if(EnumUtils.isValidEnumIgnoreCase(TaskAssignmentStatus.class, assignmentStatus)==false) {
					responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
					responseCMS.setMessage("taskAssignmentStatus không được chấp nhận");
					return responseCMS.build();
				}
				TaskAssignmentStatus taskAssignmentStatus=EnumUtils.getEnumIgnoreCase(TaskAssignmentStatus.class, assignmentStatus);
				taskFilter.taskAssignmentStatus=taskAssignmentStatus;
			}

			int total=taskRepositoryCustom.countAll(taskFilter);
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
	
	/**
	 * Gets the sub tasks.
	 *
	 * @param taskParentId the task parent id
	 * @return the sub tasks
	 */
	@GetMapping("/task/get/subtask/{taskParentId}")
	public Object getSubTasks(@PathVariable(name = "taskParentId", required = true) String taskParentId){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			if(ObjectId.isValid(taskParentId)==false) {
				responseCMS.setStatus(HttpStatus.BAD_REQUEST);
				responseCMS.setResult("taskParentId không hợp lệ");
				return responseCMS.build();
			}
			
			TaskFilter taskFilter=new TaskFilter();
			taskFilter.parentId=taskParentId;
			
			int total=taskRepositoryCustom.countAll(taskFilter);
			List<Task> tasks=taskRepositoryCustom.findAll(taskFilter, 0, 0);
			List<Document> results=new ArrayList<Document>();
			for (Task item : tasks) {
				results.add(convertTaskList(item, null));
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
	
	/**
	 * Gets the task.
	 *
	 * @param taskId the task id
	 * @return the task
	 */
	@GetMapping("/task/get/{taskId}")
	public Object getTask(
			@PathVariable(name = "taskId", required = true) String taskId,
			@RequestParam(name = "userId", required = false) String userId, 
			@RequestParam(name = "organizationId", required = false) String organizationId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			UserOrganization userTask=new UserOrganization();
			userTask.userId=userId;
			userTask.organizationId=organizationId;
			
			Task task=null;
			try {
				task=taskRepository.findById(new ObjectId(taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("taskId không tồn tại trong hệ thống");
				return responseCMS.build();
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertTaskDetail(task, userTask));
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

	/**
	 * Creates the task.
	 *
	 * @param reqTaskCreate the task create
	 * @return the object
	 */
	@PostMapping("/task/create")
	public Object createTask(@RequestBody @Valid ReqTaskCreate reqTaskCreate){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			/* Check valid trước khi lưu (thực ra là để show lỗi chi tiết) */
			try {
				taskService.validForCreate(reqTaskCreate);
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage(e.getMessage());
				return responseCMS.build();
			}

			/* Kiểm tra assignmentType */
			if(EnumUtils.isValidEnumIgnoreCase(TaskAssignmentType.class, reqTaskCreate.assignmentType)==false) {
				responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
				responseCMS.setMessage("assignmentType không được chấp nhận");
				return responseCMS.build();
			}
			TaskAssignmentType taskAssignmentType=EnumUtils.getEnumIgnoreCase(TaskAssignmentType.class, reqTaskCreate.assignmentType);
			reqTaskCreate.assignmentType=taskAssignmentType.getKey();
			
			/* Lưu data */
			Task task=new Task();
			try {
				/* Gán loại giao nhiệm vụ (mặc định luôn là giao User) */
				task.assignmentType=reqTaskCreate.assignmentType;
				
				/* -------------------- Ghi log ---------------------- */
				TaskEvent taskEvent=new TaskEvent();
				taskEvent.creator=reqTaskCreate.ownerTask;
				taskEvent.title="Tạo và giao nhiệm vụ";
				taskEvent.descriptions.put("Tiêu đề", reqTaskCreate.title);
				taskEvent.descriptions.put("Nội dung", reqTaskCreate.description);
				
				if(task.isAsssigmentTypeUser()) {
					taskEvent.descriptions.put("Người xử lý", reqTaskCreate.assigneeTask.getText());
				}else if(task.isAsssigmentTypeOrganization()) {
					taskEvent.descriptions.put("Đơn vị xử lý", reqTaskCreate.assigneeTask.getText());
				}
				
				if(reqTaskCreate.addFollowersTask.size()>0) {
					List<String> listFullName=new ArrayList<String>();
					for(UserOrganization userTask : reqTaskCreate.addFollowersTask) {
						listFullName.add(userTask.getText());
					}
					
					if(task.isAsssigmentTypeUser()) {
						taskEvent.descriptions.put("Người theo dõi", String.join(", ", listFullName));
					}else if(task.isAsssigmentTypeOrganization()) {
						taskEvent.descriptions.put("Đơn vị theo dõi", String.join(", ", listFullName));
					}
				}
				taskEvent.descriptions.put("Độ khẩn", TaskPriority.getName(reqTaskCreate.priority));
				if(reqTaskCreate.endTime>0) {
					taskEvent.descriptions.put("Hạn xử lý", DateTimeUtil.getDatetimeFormat().format(reqTaskCreate.endTime));
				}else {
					taskEvent.descriptions.put("Hạn xử lý", "Không hạn");
				}
				if(reqTaskCreate.addAttachments.size()>0) {
					List<String> listAttachmentName=new ArrayList<String>();
					for (Attachment attachment : reqTaskCreate.addAttachments) {
						listAttachmentName.add(attachment.fileName);
					}
					taskEvent.descriptions.put("Đính kèm", String.join(", ", listAttachmentName));
				}
				taskEvent.action=NotifyCaption.TaoNhiemVuMoi.getAction();
				task.events.add(0, taskEvent);
				/* ---------------------- End ghi log ------------------*/

				/* ----------------- Ghi data cho task ---------------- */
				
				/* Kiểm tra owner */
				try {
					reqTaskCreate.ownerTask.validOrganizationFields();
				} catch (Exception e) {
					responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
					responseCMS.setMessage("ownerTask không hợp lệ");
					responseCMS.setResult(reqTaskCreate.ownerTask);
					return responseCMS.build();
				}
				task.ownerTask=reqTaskCreate.ownerTask;
				
				if(reqTaskCreate.assistantTask!=null) {
					/* Kiểm tra thông tin người giao thay */
					try {
						reqTaskCreate.assistantTask.validOrganizationFields();
					} catch (Exception e) {
						responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
						responseCMS.setMessage("assistantTask không hợp lệ");
						responseCMS.setResult(reqTaskCreate.assistantTask);
						return responseCMS.build();
					}
					task.assistantTask=reqTaskCreate.assistantTask;
				}
				
				/* Kiểm tra assignee */
				if(taskAssignmentType==TaskAssignmentType.Organization) {
					try {
						reqTaskCreate.assigneeTask.validOrganizationFields();
					} catch (Exception e) {
						responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
						responseCMS.setMessage("assigneeTask không hợp lệ");
						responseCMS.setResult(reqTaskCreate.assigneeTask);
						return responseCMS.build();
					}
					reqTaskCreate.assigneeTask.userId=null;
					reqTaskCreate.assigneeTask.fullName=null;
				} 
				/* Nếu giao cho cá nhân */
				else {
					try {
						reqTaskCreate.assigneeTask.validUserOrganizationFields();
					} catch (Exception e) {
						responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
						responseCMS.setMessage("assigneeTask không hợp lệ");
						responseCMS.setResult(reqTaskCreate.assigneeTask);
						return responseCMS.build();
					}
				}
				task.assigneeTask=reqTaskCreate.assigneeTask;
				
				/* Nếu có người theo dõi */
				if(reqTaskCreate.addFollowersTask.size()>0) {
					/* Nếu giao cho đơn vị */
					if(taskAssignmentType==TaskAssignmentType.Organization) {
						for(UserOrganization followerUser:reqTaskCreate.addFollowersTask) {
							try {
								followerUser.validOrganizationFields();
							} catch (Exception e) {
								responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
								responseCMS.setMessage("addFollowerTask thông tin organization không hợp lệ");
								responseCMS.setResult(followerUser);
								return responseCMS.build();
							}
							followerUser.userId=null;
							followerUser.fullName=null;
						}
					}
					/* Nếu giao cho cá nhân */
					else {
						for(UserOrganization followerUser:reqTaskCreate.addFollowersTask) {
							try {
								followerUser.validUserOrganizationFields();
							} catch (Exception e) {
								responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
								responseCMS.setMessage("addFollowerTask thông tin user-organization không hợp lệ");
								responseCMS.setResult(followerUser);
								return responseCMS.build();
							}
						}
					}
				}
				task.followersTask=reqTaskCreate.addFollowersTask;
				
				task.title=reqTaskCreate.title;
				task.description=reqTaskCreate.description;
				task.priority=reqTaskCreate.priority;
				if(reqTaskCreate.endTime>0) {
					task.endTime=new Date(reqTaskCreate.endTime);
				}else {
					task.endTime=null;
				}
				task.parentId=reqTaskCreate.parentId;
				task.docId=reqTaskCreate.docId;

				/* Nếu có đính kèm khi tạo */
				if(reqTaskCreate.addAttachments.size()>0) {
					LinkedList<TaskAttachment> attachmentsTask=new LinkedList<TaskAttachment>();
					for (Attachment attachment : reqTaskCreate.addAttachments) {
						try {
							TaskAttachment taskAttachment=taskAttachmentService.storeMedia(attachment.fileName, attachment.fileType, attachment.fileBase64);
							taskAttachment.creator=attachment.creator;
							taskAttachment.description=attachment.description;
							attachmentsTask.add(taskAttachment);
						} catch (Exception e) {
							e.printStackTrace();
							log.debug(e.getMessage());
							responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
							responseCMS.setMessage("Đính kèm ["+attachment.fileName+"] bị lỗi tệp, vui lòng thử lại file khác");
							return responseCMS.build();
						}
					}
					task.attachments.addAll(attachmentsTask);
					/* Sau khi lưu đính kèm thành công */
				}
				
				
				
				/* Lưu task vào DB */
				task=taskRepository.save(task);
				
				/* Nếu là subtask thì ++ countSubTask cho task parent */
				if(task.parentId!=null && task.parentId.isEmpty()==false) {
					taskService.plusSubTask(task.parentId);
				}
				
				/* Thông báo */
				Notify taskNotify=new Notify();
				taskNotify.taskId=task.getId();
				taskNotify.creator=task.ownerTask;
				taskNotify.action=NotifyCaption.TaoNhiemVuMoi.getAction();
				taskNotify.title=NotifyCaption.TaoNhiemVuMoi.getTitle();
				taskNotify.content=task.title;
				taskNotify.viewed=false;
				
				createTaskNotify(taskNotify, task, userRequest.getUser());
				/* End thông báo */
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage(e.getMessage());
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			responseCMS.setStatus(HttpStatus.CREATED);
			responseCMS.setResult(convertTaskDetail(task, null));
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

	/**
	 * Edits the task.
	 *
	 * @param taskId the task id
	 * @param task_Edit the task edit
	 * @return the object
	 */
	@PutMapping("/task/edit/{taskId}")
	public Object editTask(
			@PathVariable(name = "taskId", required = true) String taskId, 
			@RequestBody @Valid ReqTaskEdit task_Edit){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			/* Kiểm tra taskId có tồn tại không */
			Task taskUpdate=null;
			UserOrganization oldAssigneeTask=null;
			UserOrganization newAssigneeTask=null;
			try {
				taskUpdate=taskRepository.findById(new ObjectId(taskId)).get();
				oldAssigneeTask=taskUpdate.assigneeTask;
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("taskId ["+taskId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			/* Thoát, nếu đã hoàn thành */
			if(taskUpdate.completedTime!=null) {
				responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setResult("Lỗi, nhiệm vụ đã được hoàn thành, không thể cập nhật nhiệm vụ");
				return responseCMS.build();
			}
			
			/* Cập nhật data */
			try {
				/* -------------------- Ghi log ---------------------- */
				TaskEvent taskEvent=new TaskEvent();
				taskEvent.creator=taskUpdate.ownerTask;
				taskEvent.title="Cập nhật nhiệm vụ";

				if(taskUpdate.title.equalsIgnoreCase(task_Edit.title)==false) {
					taskEvent.descriptions.put("Tiêu đề", task_Edit.title);
				}

				if(taskUpdate.description.equalsIgnoreCase(task_Edit.description)==false) {
					taskEvent.descriptions.put("Nội dung", task_Edit.description);
				}

				if(task_Edit.assigneeTask!=null && task_Edit.assigneeTask.validBasic() && !taskUpdate.assigneeTask.compareTo(task_Edit.assigneeTask)) {
					if(taskUpdate.isAsssigmentTypeUser()) {
						taskEvent.descriptions.put("Cập nhật người xử lý", task_Edit.assigneeTask.getText());
					}else if(taskUpdate.isAsssigmentTypeOrganization()) {
						taskEvent.descriptions.put("Cập nhật đơn vị xử lý", task_Edit.assigneeTask.getText());
					}
				}
				
				if(task_Edit.addFollowersTask.size()>0) {
					List<String> listFullName=new ArrayList<String>();
					for(UserOrganization userTask : task_Edit.addFollowersTask) {
						listFullName.add(userTask.getText());
					}
					
					if(taskUpdate.isAsssigmentTypeUser()) {
						taskEvent.descriptions.put("Thêm người theo dõi", String.join(", ", listFullName));
					}else if(taskUpdate.isAsssigmentTypeOrganization()) {
						taskEvent.descriptions.put("Thêm đơn vị theo dõi", String.join(", ", listFullName));
					}
				}

				if(task_Edit.deleteFollowersTask.size()>0 && taskUpdate.followersTask.size()>0) {
					List<String> listFullName=new ArrayList<String>();
					for(UserTaskId userTaskDelete : task_Edit.deleteFollowersTask) {
						for(UserOrganization userTask : taskUpdate.followersTask) {
							if(taskUpdate.isAsssigmentTypeUser()) {
								if(userTask.userId.equalsIgnoreCase(userTaskDelete.userId) && userTask.organizationId.equalsIgnoreCase(userTaskDelete.organizationId)) {
									listFullName.add(userTask.getText());
									break;
								}
							}else if(taskUpdate.isAsssigmentTypeOrganization()) {
								if(userTask.organizationId.equalsIgnoreCase(userTaskDelete.organizationId)) {
									listFullName.add(userTask.getText());
									break;
								}
							}
						}
					}
					if(listFullName.size()>0) {
						if(taskUpdate.isAsssigmentTypeUser()) {
							taskEvent.descriptions.put("Bỏ người theo dõi", String.join(", ", listFullName));
						}else if(taskUpdate.isAsssigmentTypeOrganization()) {
							taskEvent.descriptions.put("Bỏ đơn vị theo dõi", String.join(", ", listFullName));
						}
					}
				}

				if(taskUpdate.priority!=task_Edit.priority) {
					taskEvent.descriptions.put("Độ khẩn", TaskPriority.getName(task_Edit.priority));
				}

				if(taskUpdate.endTime==null && task_Edit.endTime>0) {
					taskEvent.descriptions.put("Hạn xử lý", DateTimeUtil.getDatetimeFormat().format(task_Edit.endTime));
				}else if(taskUpdate.endTime!=null && task_Edit.endTime==0) {
					taskEvent.descriptions.put("Hạn xử lý", "Không hạn");
				}

				if(task_Edit.addAttachments.size()>0) {
					List<String> listAttachmentName=new ArrayList<String>();
					for (Attachment attachment : task_Edit.addAttachments) {
						listAttachmentName.add(attachment.fileName);
					}
					taskEvent.descriptions.put("Thêm đính kèm", String.join(", ", listAttachmentName));
				}

				if(task_Edit.deleteAttachments.size()>0 && taskUpdate.attachments.size()>0) {
					List<String> listAttachmentName=new ArrayList<String>();
					for (String idAttachmentDelete : task_Edit.deleteAttachments) {
						for (TaskAttachment taskAttachment : taskUpdate.attachments) {
							if(taskAttachment.getId().equalsIgnoreCase(idAttachmentDelete)) {
								listAttachmentName.add(taskAttachment.fileName);
								break;
							}
						}
					}
					if(listAttachmentName.size()>0) {
						taskEvent.descriptions.put("Xóa đính kèm", String.join(", ", listAttachmentName));
					}
				}
				taskEvent.action=NotifyCaption.CapNhatNhiemVu.getAction();
				
				taskUpdate.events.add(0, taskEvent);
				/* --------------------- End ghi log ------------------------*/

				/* Cập nhật người xử lý */
				if(task_Edit.assigneeTask!=null && task_Edit.assigneeTask.validBasic() && !taskUpdate.assigneeTask.compareTo(task_Edit.assigneeTask)) {
					newAssigneeTask=task_Edit.assigneeTask;
					
					taskUpdate.assigneeTask.userId=task_Edit.assigneeTask.userId;
					taskUpdate.assigneeTask.fullName=task_Edit.assigneeTask.fullName;
					taskUpdate.assigneeTask.organizationId=task_Edit.assigneeTask.organizationId;
					taskUpdate.assigneeTask.organizationName=task_Edit.assigneeTask.organizationName;
				}
				
				/* Thêm người theo dõi */
				if(task_Edit.addFollowersTask.size()>0) {
					taskUpdate.followersTask.addAll(task_Edit.addFollowersTask);
				}

				/* Xóa người theo dõi nếu có */
				if(task_Edit.deleteFollowersTask.size()>0 && taskUpdate.followersTask.size()>0) {
					for(UserTaskId userTaskDelete : task_Edit.deleteFollowersTask) {
						for(UserOrganization followerTask : taskUpdate.followersTask) {
							/* Xóa trường hợp giao cá nhân */
							if(userTaskDelete.userId!=null && followerTask.userId.equalsIgnoreCase(userTaskDelete.userId) && followerTask.organizationId.equalsIgnoreCase(userTaskDelete.organizationId)) {
								taskUpdate.followersTask.remove(followerTask);
								break;
							}
							
							/* Xóa trường hợp giao đơn vị */
							if(userTaskDelete.userId==null && followerTask.organizationId.equalsIgnoreCase(userTaskDelete.organizationId)) {
								taskUpdate.followersTask.remove(followerTask);
								break;
							}
						}
					}
				}

				taskUpdate.title=task_Edit.title;
				taskUpdate.description=task_Edit.description;
				taskUpdate.priority=task_Edit.priority;
				if(task_Edit.endTime>0) {
					taskUpdate.endTime=new Date(task_Edit.endTime);
				}else {
					taskUpdate.endTime=null;
				}
				
				/* Nếu có đính kèm khi cập nhật */
				if(task_Edit.addAttachments.size()>0) {
					LinkedList<TaskAttachment> attachmentsTask=new LinkedList<TaskAttachment>();
					for (Attachment attachment : task_Edit.addAttachments) {
						try {
							TaskAttachment taskAttachment=taskAttachmentService.storeMedia(attachment.fileName, attachment.fileType, attachment.fileBase64);
							taskAttachment.creator=attachment.creator;
							taskAttachment.description=attachment.description;
							attachmentsTask.add(taskAttachment);
						} catch (Exception e) {
							e.printStackTrace();
							log.debug(e.getMessage());
							responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
							responseCMS.setMessage("Đính kèm ["+attachment.fileName+"] bị lỗi tệp, vui lòng thử lại file khác");
							return responseCMS.build();
						}
					}
					taskUpdate.attachments.addAll(0, attachmentsTask);
					/* Sau khi lưu đính kèm thành công */
				}

				/* Xóa đính kèm nếu có */
				if(task_Edit.deleteAttachments.size()>0 && taskUpdate.attachments.size()>0) {
					/* Duyệt từng attachment cần xóa */
					for (String idAttachmentDelete : task_Edit.deleteAttachments) {
						for(TaskAttachment attachment: taskUpdate.attachments) {
							if(attachment.getId().equalsIgnoreCase(idAttachmentDelete)) {
								taskUpdate.attachments.remove(attachment);
								taskAttachmentService.delete(attachment);
								break;
							}
						}
					}
				}

				/* Save task vào DB */
				taskUpdate=taskRepository.save(taskUpdate);
				
				/* Thông báo */
				Notify taskNotify=new Notify();
				taskNotify.taskId=taskUpdate.getId();
				taskNotify.action=NotifyCaption.CapNhatNhiemVu.getAction();
				taskNotify.title=NotifyCaption.CapNhatNhiemVu.getTitle();
				taskNotify.content=taskUpdate.title;
				taskNotify.viewed=false;
				
				createTaskNotify(taskNotify, taskUpdate, userRequest.getUser());
				if(taskUpdate.assignmentType.equalsIgnoreCase(TaskAssignmentType.Organization.getKey())==false && newAssigneeTask!=null) {
					/* Thêm thông báo riêng cho thay đổi chủ trì ở task này */
					createTaskNotifyForChangeAssignee(taskUpdate, userRequest.getUser(), oldAssigneeTask, newAssigneeTask);
					
					/* Thay đổi và thông báo ở subtask */
					changeAssignee(taskUpdate, oldAssigneeTask, newAssigneeTask);
				}
				
				/* End thông báo */
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage(e.getMessage());
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertTaskDetail(taskUpdate, null));
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

	@PutMapping("/task/accept/{taskId}")
	public Object acceptTask(@PathVariable(name = "taskId", required = true) String taskId,
			@RequestBody @Valid ReqTaskAccept reqTaskAccept) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			/* Kiểm tra taskId */
			Task taskComplete=null;
			try {
				taskComplete=taskRepository.findById(new ObjectId(taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("taskId không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Thoát, nếu đã hoàn thành */
			if(taskComplete.completedTime!=null && taskComplete.completedTime.getTime()>0) {
				responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setResult("Lỗi, nhiệm vụ đã được hoàn thành trước đó");
				return responseCMS.build();
			}
			
			/* -------------------- Ghi log ---------------------- */
			UserOrganization userCreator=new UserOrganization();
			
			User userAction=null;
			try {
				userAction=userRepository.findById(new ObjectId(reqTaskAccept.userId)).get();
				userCreator.userId=userAction.getId();
				userCreator.fullName=userAction.fullName;
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("userId không tồn tại trong hệ thống");
				return responseCMS.build();
			}
			
			Organization organizationAction=null;
			try {
				organizationAction=organizationRepository.findById(new ObjectId(reqTaskAccept.organizationId)).get();
				userCreator.organizationId=organizationAction.getId();
				userCreator.organizationName=organizationAction.name;
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("organizationId không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			TaskEvent taskEvent=new TaskEvent();
			taskEvent.creator=userCreator;
			taskEvent.title="Bắt đầu thực hiện nhiệm vụ";
			taskEvent.action=NotifyCaption.BatDauThucHienNhiemVu.getAction();
			
			taskComplete.events.add(0, taskEvent);
			/* End ghi log */
			
			/* Cập nhật task (DB)*/
			taskComplete.acceptedTime=new Date();
			
			taskComplete=taskRepository.save(taskComplete);
			
			/* Thông báo */
			Notify taskNotify=new Notify();
			taskNotify.taskId=taskComplete.getId();
			taskNotify.action=NotifyCaption.BatDauThucHienNhiemVu.getAction();
			taskNotify.title=NotifyCaption.BatDauThucHienNhiemVu.getTitle();
			taskNotify.content=taskComplete.title;
			taskNotify.viewed=false;
			
			createTaskNotify(taskNotify, taskComplete, userRequest.getUser());
			/* End thông báo */
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertTaskDetail(taskComplete, null));
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	/**
	 * Complete task.
	 *
	 * @param taskId the task id
	 * @param reqTaskComplete the req task complete
	 * @return the object
	 */
	@PutMapping("/task/complete/{taskId}")
	public Object completeTask(@PathVariable(name = "taskId", required = true) String taskId,
			@RequestBody @Valid ReqTaskComplete reqTaskComplete) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			/* Kiểm tra taskId */
			Task taskComplete=null;
			try {
				taskComplete=taskRepository.findById(new ObjectId(taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("taskId không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Thoát, nếu đã hoàn thành */
			if(taskComplete.completedTime!=null && taskComplete.completedTime.getTime()>0) {
				responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setResult("Lỗi, nhiệm vụ đã được hoàn thành trước đó");
				return responseCMS.build();
			}
			
			/* -------------------- Ghi log ---------------------- */
			UserOrganization userCreator=new UserOrganization();
			
			User userAction=null;
			try {
				userAction=userRepository.findById(new ObjectId(reqTaskComplete.userId)).get();
				userCreator.userId=userAction.getId();
				userCreator.fullName=userAction.fullName;
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("userId không tồn tại trong hệ thống");
				return responseCMS.build();
			}
			
			Organization organizationAction=null;
			try {
				organizationAction=organizationRepository.findById(new ObjectId(reqTaskComplete.organizationId)).get();
				userCreator.organizationId=organizationAction.getId();
				userCreator.organizationName=organizationAction.name;
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("organizationId không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			TaskEvent taskEvent=new TaskEvent();
			taskEvent.creator=userCreator;
			taskEvent.title="Hoàn thành nhiệm vụ nhiệm vụ";
			taskEvent.action=NotifyCaption.HoanThanhNhiemVu.getAction();
			
			taskComplete.events.add(0, taskEvent);
			/* End ghi log */
			
			/* Cập nhật task (DB)*/
			taskComplete.completedTime=new Date();
			
			taskComplete=taskRepository.save(taskComplete);
			
			/* Thông báo */
			Notify taskNotify=new Notify();
			taskNotify.taskId=taskComplete.getId();
			taskNotify.action=NotifyCaption.HoanThanhNhiemVu.getAction();
			taskNotify.title=NotifyCaption.HoanThanhNhiemVu.getTitle();
			taskNotify.content=taskComplete.title;
			taskNotify.viewed=false;
			
			createTaskNotify(taskNotify, taskComplete, userRequest.getUser());
			/* End thông báo */
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertTaskDetail(taskComplete, null));
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	/**
	 * Redo task.
	 *
	 * @param taskId the task id
	 * @param reqTaskReDo the req task re do
	 * @return the object
	 */
	@PutMapping("/task/redo/{taskId}")
	public Object redoTask(@PathVariable(name = "taskId", required = true) String taskId,
			@RequestBody @Valid ReqTaskReDo reqTaskReDo) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			Task taskRedo=null;
			try {
				taskRedo=taskRepository.findById(new ObjectId(taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("taskId không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Thoát, nếu chưa hoàn thành */
			if(taskRedo.completedTime==null) {
				responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setResult("Lỗi, nhiệm vụ chưa được hoàn thành, vẫn có thể tiếp tục thực hiện");
				return responseCMS.build();
			}
			
			/* -------------------- Ghi log ---------------------- */
			UserOrganization userCreator=new UserOrganization();
			
			User userAction=null;
			try {
				userAction=userRepository.findById(new ObjectId(reqTaskReDo.userId)).get();
				userCreator.userId=userAction.getId();
				userCreator.fullName=userAction.fullName;
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("userId không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			Organization organizationAction=null;
			try {
				organizationAction=organizationRepository.findById(new ObjectId(reqTaskReDo.organizationId)).get();
				userCreator.organizationId=organizationAction.getId();
				userCreator.organizationName=organizationAction.name;
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("organizationId không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			TaskEvent taskEvent=new TaskEvent();
			taskEvent.creator=userCreator;
			taskEvent.title="Triệu hồi nhiệm vụ";
			taskEvent.descriptions.put("Lý do", reqTaskReDo.reason);
			taskEvent.action=NotifyCaption.TrieuHoiNhiemVu.getAction();
			
			taskRedo.events.add(0, taskEvent);
			/* End log */
			
			/* Cập nhật */
			taskRedo.completedTime=null;
			
			taskRedo=taskRepository.save(taskRedo);
			
			/* Thông báo */
			Notify taskNotify=new Notify();
			taskNotify.taskId=taskRedo.getId();
			taskNotify.action=NotifyCaption.TrieuHoiNhiemVu.getAction();
			taskNotify.title=NotifyCaption.TrieuHoiNhiemVu.getTitle();
			taskNotify.content=taskRedo.title;
			taskNotify.viewed=false;
			
			createTaskNotify(taskNotify, taskRedo, userRequest.getUser());
			/* End thông báo */
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertTaskDetail(taskRedo, null));
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	/**
	 * Gets the tree task.
	 *
	 * @param taskId the task id
	 * @return the tree task
	 */
	@GetMapping("/task/tree/{taskId}")
	public Object getTreeTask(@PathVariable(name = "taskId", required = true) String taskId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			Task task=null;
			try {
				task=taskRepository.findById(new ObjectId(taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("taskId không tồn tại trong hệ thống");
				return responseCMS.build();
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(treeTasks(task));
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
	
	/**
	 * Delete task.
	 *
	 * @param taskId the task id
	 * @return the object
	 */
	@DeleteMapping("/task/delete/{taskId}")
	public Object deleteTask(@PathVariable(name = "taskId", required = true) String taskId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			/* Kiểm tra articleId */
			Task taskDelete=null;
			try {
				taskDelete=taskRepository.findById(new ObjectId(taskId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("taskId không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			/* Xoá task */
			taskRepository.delete(taskDelete);

			/* Nếu là subtask thì -- countSubTask cho task parent */
			if(taskDelete.parentId!=null && taskDelete.parentId.isEmpty()==false) {
				taskService.plusSubTask(taskDelete.parentId);
			}
			
			/* Kiểm tra và xóa các đính kèm liên quan */
			try {
				if(taskDelete.attachments.size()>0) {
					for(TaskAttachment attachment: taskDelete.attachments) {
						taskAttachmentService.delete(attachment);
					}
				}

				if(taskDelete.processes.size()>0) {
					for(TaskProcess taskProcess: taskDelete.processes) {
						if(taskProcess.attachments.size()>0) {
							for(TaskAttachment attachment: taskProcess.attachments) {
								taskAttachmentService.delete(attachment);
							}
						}
					}
				}

				if(taskDelete.comments.size()>0) {
					for(TaskComment taskComment: taskDelete.comments) {
						if(taskComment.attachments.size()>0) {
							for(TaskAttachment attachment: taskComment.attachments) {
								taskAttachmentService.delete(attachment);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
			}

			/* Thông báo */
			Notify taskNotify=new Notify();
			taskNotify.taskId=taskDelete.getId();
			taskNotify.action=NotifyCaption.XoaNhiemVu.getAction();
			taskNotify.title=NotifyCaption.XoaNhiemVu.getTitle();
			taskNotify.content=taskDelete.title;
			taskNotify.viewed=false;
			taskNotify.active=false;
			
			createTaskNotify(taskNotify, taskDelete, userRequest.getUser());
			/* End thông báo */
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("Đã xóa nhiệm vụ chỉ đạo ["+taskDelete.title+"] thành công");
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

	/*------------------------------ Task Comment --------------------------*/

	/**
	 * Task comment post.
	 *
	 * @param taskComment_Post the task comment post
	 * @return the object
	 */
	@PostMapping("/task/comment/post")
	public Object taskCommentPost(@RequestBody @Valid ReqTaskCommentCreate taskComment_Post){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			/* Kiểm tra taskId có tồn tại không */
			Task taskUpdate=null;
			try {
				taskUpdate=taskRepository.findById(new ObjectId(taskComment_Post.taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("taskId ["+taskComment_Post.taskId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			/* Lưu data cho comment */
			TaskComment taskCommentCreate=new TaskComment();
			try {
				/* Ghi log */
				TaskEvent taskEvent=new TaskEvent();
				taskEvent.creator=taskComment_Post.creator;
				taskEvent.title="Ý kiến chỉ đạo";
				taskEvent.descriptions.put("Nội dung", taskComment_Post.message);
				if(taskComment_Post.addAttachments.size()>0) {
					List<String> listAttachmentName=new ArrayList<String>();
					for (Attachment attachment : taskComment_Post.addAttachments) {
						listAttachmentName.add(attachment.fileName);
					}
					taskEvent.descriptions.put("Đính kèm", String.join(", ", listAttachmentName));
				}
				taskEvent.action=NotifyCaption.YKienVaPhanHoi.getAction();
				
				taskUpdate.events.add(0, taskEvent);
				/* End ghi log */

				/* Ghi data cho task comment */
				taskCommentCreate.creator=taskComment_Post.creator;
				taskCommentCreate.message=taskComment_Post.message;
				taskCommentCreate.parentId=taskComment_Post.parentId;

				/* Nếu có đính kèm khi tạo */
				if(taskComment_Post.addAttachments.size()>0) {
					LinkedList<TaskAttachment> attachmentsTask=new LinkedList<TaskAttachment>();
					for (Attachment attachment : taskComment_Post.addAttachments) {
						try {
							TaskAttachment taskAttachment=taskAttachmentService.storeMedia(attachment.fileName, attachment.fileType, attachment.fileBase64);
							taskAttachment.creator=attachment.creator;
							taskAttachment.description=attachment.description;
							attachmentsTask.add(taskAttachment);
						} catch (Exception e) {
							e.printStackTrace();
							log.debug(e.getMessage());
							responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
							responseCMS.setMessage("Đính kèm ["+attachment.fileName+"] bị lỗi tệp, vui lòng thử lại file khác");
							return responseCMS.build();
						}
					}
					taskCommentCreate.attachments.addAll(attachmentsTask);
				}

				/* Cập nhật list comment cho task */
				if(taskComment_Post.parentId!=null && taskComment_Post.parentId.isEmpty()==false && taskComment_Post.parentId.length()==24) {
					LinkedList<TaskComment> taskCommentList=taskUpdate.comments;
					for (TaskComment taskCommentParent : taskCommentList) {
						if(taskCommentParent.getId().equalsIgnoreCase(taskComment_Post.parentId)) {
							taskCommentParent.replies.add(taskCommentCreate);
							break;
						}
					}
					taskUpdate.comments=taskCommentList;
				}else {
					taskUpdate.comments.add(0, taskCommentCreate);
				}
				
				/* Save comment task vào DB */
				taskUpdate=taskRepository.save(taskUpdate);
				
				/* Thông báo */
				Notify taskNotify=new Notify();
				taskNotify.creator=taskComment_Post.creator;
				taskNotify.taskId=taskUpdate.getId();
				taskNotify.action=NotifyCaption.YKienVaPhanHoi.getAction();
				taskNotify.title=NotifyCaption.YKienVaPhanHoi.getTitle();
				taskNotify.content=taskUpdate.title;
				taskNotify.viewed=false;
				
				createTaskNotify(taskNotify, taskUpdate, userRequest.getUser());
				/* End thông báo */
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage(e.getMessage());
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			responseCMS.setStatus(HttpStatus.CREATED);
			responseCMS.setResult(taskCommentCreate);
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

	@PostMapping("/task/comment/post/add-attachment/{taskId}/{commentId}")
	public Object taskCommentPostAddAttachment(HttpServletRequest request,
			@PathVariable(name = "taskId", required = true) String taskId,
			@PathVariable(name = "commentId", required = true) String commentId,
			@ModelAttribute("myUploadForm") @Valid ReqTaskAttachFile reqAttachment) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			Task task=null;
			try {
				task=taskRepository.findById(new ObjectId(taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("taskId ["+taskId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Tìm processId cần thêm đính kèm */
			TaskComment taskComment=null;
			for (TaskComment item : task.comments) {
				if(item.getId().equals(commentId)) {
					taskComment=item;
					break;
				}
			}
			
			if(taskComment==null) {
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("commentId ["+commentId+"] không tồn tại trong hệ thống");
				return responseCMS.build();
			}
			
			TaskAttachment taskAttachment =null;
			try {
				taskAttachment = taskAttachmentService.storeMedia(reqAttachment);

				UserOrganization creator=new UserOrganization();
				creator.userId=reqAttachment.getUserId();
				creator.fullName=reqAttachment.getUserId();
				creator.organizationId=reqAttachment.getOrganizationId();
				creator.organizationName=reqAttachment.getOrganizationName();
				
				taskAttachment.creator=creator;
				taskAttachment.description=reqAttachment.getDescription();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setMessage(e.getMessage());
				return responseCMS.build();
			}
			
			/* Lưu lại */
			for (TaskComment item : task.comments) {
				if(item.getId().equals(commentId)) {
					item.attachments.add(taskAttachment);
					break;
				}
			}
			taskRepository.save(task);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("Thêm đính kèm thành công");
			responseCMS.setResult(taskAttachment);
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
	
	/**
	 * Gets the task comment.
	 *
	 * @param taskId the task id
	 * @return the task comment
	 */
	@GetMapping("/task/comment/get/{taskId}")
	public Object getTaskComment(@PathVariable(name = "taskId", required = true) String taskId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			Task task=null;
			try {
				task=taskRepository.findById(new ObjectId(taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("taskId không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(task.comments);
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	
	/*------------------------------ Task Remind --------------------------*/

	/**
	 * Task remind post.
	 *
	 * @param reqTaskRemindCreate the task remind post
	 * @return the object
	 */
	@PostMapping("/task/remind/post")
	public Object taskRemindPost(@RequestBody @Valid ReqTaskRemindCreate reqTaskRemindCreate){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			/* Kiểm tra taskId có tồn tại không */
			Task taskUpdate=null;
			try {
				taskUpdate=taskRepository.findById(new ObjectId(reqTaskRemindCreate.taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("taskId ["+reqTaskRemindCreate.taskId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			/* Lưu data cho comment */
			TaskRemind taskRemindCreate=new TaskRemind();
			
			/* Ghi log */
			TaskEvent taskEvent=new TaskEvent();
			taskEvent.creator=reqTaskRemindCreate.creator;
			taskEvent.title=NotifyCaption.NhacNhoNhiemVu.getTitle();
			taskEvent.descriptions.put("Nội dung", reqTaskRemindCreate.message);
			taskEvent.action=NotifyCaption.NhacNhoNhiemVu.getAction();
			
			taskUpdate.events.add(0, taskEvent);
			/* End ghi log */

			/* Ghi data cho task comment */
			taskRemindCreate.creator=reqTaskRemindCreate.creator;
			taskRemindCreate.message=reqTaskRemindCreate.message;

			/* Cập nhật list comment cho task */
			taskUpdate.reminds.add(0, taskRemindCreate);
			
			/* Save comment task vào DB */
			taskUpdate=taskRepository.save(taskUpdate);
			
			/* Thông báo */
			Notify taskNotify=new Notify();
			taskNotify.creator=reqTaskRemindCreate.creator;
			taskNotify.taskId=taskUpdate.getId();
			taskNotify.action=NotifyCaption.NhacNhoNhiemVu.getAction();
			taskNotify.title=NotifyCaption.NhacNhoNhiemVu.getTitle();
			taskNotify.content=taskUpdate.title;
			taskNotify.viewed=false;
			
			createTaskNotify(taskNotify, taskUpdate, userRequest.getUser());
			/* End thông báo */

			responseCMS.setStatus(HttpStatus.CREATED);
			responseCMS.setResult(convertTaskDetail(taskUpdate, null));
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

	/**
	 * Gets the task remind.
	 *
	 * @param taskId the task id
	 * @return the task remind
	 */
	@GetMapping("/task/remind/get/{taskId}")
	public Object getTaskRemind(@PathVariable(name = "taskId", required = true) String taskId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			Task task=null;
			try {
				task=taskRepository.findById(new ObjectId(taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("taskId không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(task.reminds);
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	/*------------------------------ Task Process --------------------------*/

	/**
	 * Task process post.
	 *
	 * @param taskProcess_Post the task process post
	 * @return the object
	 */
	@PostMapping("/task/process/post")
	public Object taskProcessPost(@RequestBody @Valid ReqTaskProcessCreate taskProcess_Post){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			/* Kiểm tra taskId có tồn tại không */
			Task taskUpdate=null;
			try {
				taskUpdate=taskRepository.findById(new ObjectId(taskProcess_Post.taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("taskId ["+taskProcess_Post.taskId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			/* Lưu data cho process */
			TaskProcess taskProcessCreate=new TaskProcess();
			try {
				/* Ghi log */
				TaskEvent taskEvent=new TaskEvent();
				taskEvent.creator=taskProcess_Post.creator;
				taskEvent.title="Báo cáo tiến độ";
				taskEvent.descriptions.put("Tiến độ",taskProcess_Post.percent+"%");
				taskEvent.descriptions.put("Diễn giải",taskProcess_Post.explain);
				if(taskProcess_Post.addAttachments.size()>0) {
					List<String> listAttachmentName=new ArrayList<String>();
					for (Attachment attachment : taskProcess_Post.addAttachments) {
						listAttachmentName.add(attachment.fileName);
					}
					taskEvent.descriptions.put("Đính kèm", String.join(", ", listAttachmentName));
				}
				taskEvent.action=NotifyCaption.CapNhatTienDo.getAction();
				
				taskUpdate.events.add(0, taskEvent);
				/* End ghi log */

				/* Ghi data cho task process */
				taskProcessCreate.creator=taskProcess_Post.creator;
				taskProcessCreate.explain=taskProcess_Post.explain;
				taskProcessCreate.percent=taskProcess_Post.percent;

				/* Nếu có đính kèm khi tạo */
				if(taskProcess_Post.addAttachments.size()>0) {
					LinkedList<TaskAttachment> attachments=new LinkedList<TaskAttachment>();
					for (Attachment attachment : taskProcess_Post.addAttachments) {
						try {
							TaskAttachment taskAttachment=taskAttachmentService.storeMedia(attachment.fileName, attachment.fileType, attachment.fileBase64);
							taskAttachment.creator=attachment.creator;
							taskAttachment.description=attachment.description;
							attachments.add(taskAttachment);
						} catch (Exception e) {
							e.printStackTrace();
							log.debug(e.getMessage());
							responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
							responseCMS.setMessage("Đính kèm ["+attachment.fileName+"] bị lỗi tệp, vui lòng thử lại file khác");
							return responseCMS.build();
						}
					}
					taskProcessCreate.attachments.addAll(attachments);
				}

				/* Cập nhật list process cho task */
				taskUpdate.processes.add(0, taskProcessCreate);

				/* Save process task vào DB */
				taskUpdate=taskRepository.save(taskUpdate);
				
				/* Thông báo */
				Notify taskNotify=new Notify();
				taskNotify.creator=taskProcess_Post.creator;
				taskNotify.taskId=taskUpdate.getId();
				taskNotify.action=NotifyCaption.CapNhatTienDo.getAction();
				taskNotify.title=NotifyCaption.CapNhatTienDo.getTitle();
				taskNotify.content=taskUpdate.title;
				taskNotify.viewed=false;
				
				createTaskNotify(taskNotify, taskUpdate, userRequest.getUser());
				/* End thông báo */
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage(e.getMessage());
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			responseCMS.setStatus(HttpStatus.CREATED);
			responseCMS.setResult(taskProcessCreate);
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

	@PostMapping("/task/process/post/add-attachment/{taskId}/{processId}")
	public Object taskProcessPostAddAttachment(HttpServletRequest request,
			@PathVariable(name = "taskId", required = true) String taskId,
			@PathVariable(name = "processId", required = true) String processId,
			@ModelAttribute("myUploadForm") @Valid ReqTaskAttachFile reqTaskAttachFile) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			Task task=null;
			try {
				task=taskRepository.findById(new ObjectId(taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("taskId ["+taskId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Tìm processId cần thêm đính kèm */
			TaskProcess taskProcess=null;
			for (TaskProcess item : task.processes) {
				if(item.getId().equals(processId)) {
					taskProcess=item;
					break;
				}
			}
			
			if(taskProcess==null) {
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("processId ["+processId+"] không tồn tại trong hệ thống");
				return responseCMS.build();
			}
			
			TaskAttachment taskAttachment =null;
			try {
				taskAttachment = taskAttachmentService.storeMedia(reqTaskAttachFile);

				UserOrganization creator=new UserOrganization();
				creator.userId=reqTaskAttachFile.getUserId();
				creator.fullName=reqTaskAttachFile.getUserId();
				creator.organizationId=reqTaskAttachFile.getOrganizationId();
				creator.organizationName=reqTaskAttachFile.getOrganizationName();
				
				taskAttachment.creator=creator;
				taskAttachment.description=reqTaskAttachFile.getDescription();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setMessage(e.getMessage());
				return responseCMS.build();
			}
			
			/* Lưu lại */
			for (TaskProcess item : task.processes) {
				if(item.getId().equals(processId)) {
					item.attachments.add(taskAttachment);
					break;
				}
			}
			taskRepository.save(task);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("Thêm đính kèm thành công");
			responseCMS.setResult(taskAttachment);
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
	
	/**
	 * Gets the task process.
	 *
	 * @param taskId the task id
	 * @return the task process
	 */
	@GetMapping("/task/process/get/{taskId}")
	public Object getTaskProcess(@PathVariable(name = "taskId", required = true) String taskId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			Task task=null;
			try {
				task=taskRepository.findById(new ObjectId(taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("taskId không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(task.processes);
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	/**
	 * Gets the task attachments.
	 *
	 * @param taskId the task id
	 * @return the task attachments
	 */
	/*------------------------------ Task Attachment --------------------------*/
	@GetMapping("/task/attachment/get/{taskId}")
	public Object getTaskAttachments(@PathVariable(name = "taskId", required = true) String taskId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			Task task=null;
			try {
				task=taskRepository.findById(new ObjectId(taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("taskId không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(task.attachments);
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	/**
	 * Gets the task attachment.
	 *
	 * @param path the path
	 * @return the task attachment
	 */
	@GetMapping("/task/attachment/path")
	public Object getTaskAttachment(@RequestParam(name = "path", required = true) String path) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			System.out.println("path: "+path);
			byte[] base64Encode=taskAttachmentService.getFilePath(path);
			if(base64Encode==null) {
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("path không tồn tại trong hệ thống");
				return responseCMS.build();
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(base64Encode);
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	@PostMapping(value="/task/attachment/add/{taskId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public Object addTaskAttachment(HttpServletRequest request,
			@PathVariable(name = "taskId", required = true) String taskId,
			@ModelAttribute("myUploadForm") @Valid ReqTaskAttachFile reqTaskAttachFile) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			Task task=null;
			try {
				task=taskRepository.findById(new ObjectId(taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("taskId ["+taskId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			TaskAttachment taskAttachment=null;
			try {
				taskAttachment = taskAttachmentService.storeMedia(reqTaskAttachFile);
				
				UserOrganization creator=new UserOrganization();
				creator.userId=reqTaskAttachFile.getUserId();
				creator.fullName=reqTaskAttachFile.getFullName();
				creator.organizationId=reqTaskAttachFile.getOrganizationId();
				creator.organizationName=reqTaskAttachFile.getOrganizationName();
				
				taskAttachment.creator=creator;
				taskAttachment.description=reqTaskAttachFile.getDescription();
				task.attachments.add(taskAttachment);
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setMessage("Đính kèm ["+reqTaskAttachFile.getFile().getOriginalFilename()+"] bị lỗi tệp, vui lòng thử lại file khác");
				return responseCMS.build();
			}
			
			/* Lưu lại */
			taskRepository.save(task);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("Thêm đính kèm thành công");
			responseCMS.setResult(taskAttachment);
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	/**
	 * Task rating put.
	 *
	 * @param taskId the task id
	 * @param reqTaskRating the req task rating
	 * @return the object
	 */
	/*---------------------------------- task rating -----------------------------*/
	@PutMapping("/task/rating/{taskId}")
	public Object taskRatingPut(
			@PathVariable(name = "taskId", required = true) String taskId,
			@RequestBody @Valid ReqTaskRatingEdit reqTaskRating){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			/* Kiểm tra taskId có tồn tại không */
			Task taskUpdate=null;
			try {
				taskUpdate=taskRepository.findById(new ObjectId(taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("taskId ["+taskId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Kiểm tra task đã được hoàn thành hay chưa mới cho đánh giá */
			if(taskUpdate.completedTime==null) {
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("taskId ["+taskId+"] chưa hoàn thành, bạn không thể đánh giá được");
				return responseCMS.build();
			}
	
			/* Lưu data cho process */
			try {
				/* Ghi log */
				TaskEvent taskEvent=new TaskEvent();
				taskEvent.creator=reqTaskRating.creator;
				taskEvent.title="Đánh giá nhiệm vụ";
				taskEvent.descriptions.put("Đánh giá",reqTaskRating.star+" sao");
				taskEvent.descriptions.put("Ghi chú",reqTaskRating.comment);
				taskEvent.action=NotifyCaption.DanhGiaNhiemVu.getAction();
				
				taskUpdate.events.add(0, taskEvent);
				/* End ghi log */
	
				/* Ghi data cho task rating */
				TaskRating taskRating=new TaskRating();
				taskRating.creator=reqTaskRating.creator;
				taskRating.star=reqTaskRating.star;
				taskRating.comment=reqTaskRating.comment;
	
				/* Cập nhật rating */
				taskUpdate.rating=new TaskRating();
				taskUpdate.rating=taskRating;
	
				/* Save task vào DB */
				taskUpdate=taskRepository.save(taskUpdate);
				
				/* Thông báo */
				Notify taskNotify=new Notify();
				taskNotify.creator=reqTaskRating.creator;
				taskNotify.taskId=taskUpdate.getId();
				taskNotify.action=NotifyCaption.DanhGiaNhiemVu.getAction();
				taskNotify.title=NotifyCaption.DanhGiaNhiemVu.getTitle();
				taskNotify.content=taskUpdate.title;
				taskNotify.viewed=false;
				
				createTaskNotify(taskNotify, taskUpdate, userRequest.getUser());
				/* End thông báo */
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage(e.getMessage());
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
	
			responseCMS.setStatus(HttpStatus.CREATED);
			responseCMS.setResult(convertTaskDetail(taskUpdate, null));
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

	/**
	 * Gets the report sum.
	 *
	 * @param limit the limit
	 * @param userId the user id
	 * @param organizationId the organization id
	 * @param categorykey the categorykey
	 * @param findOwners the find owners
	 * @param findAssistants the find assistants
	 * @param findAssignees the find assignees
	 * @param findFollowers the find followers
	 * @return the report sum
	 */
	@GetMapping("/task/sumdate")
	public Object getReportSum(
			@RequestParam(name = "limit", required = true) long limit, 
			@RequestParam(name = "userId", required = true) String userId, 
			@RequestParam(name = "organizationId", required = true) String organizationId, 
			@RequestParam(name = "categorykey", required = true) String categorykey, 
			@RequestParam(name = "findOwners", required = false, defaultValue = "") String findOwners,
			@RequestParam(name = "findAssistants", required = false, defaultValue = "") String findAssistants,
			@RequestParam(name = "findAssignees", required = false, defaultValue = "") String findAssignees,
			@RequestParam(name = "findFollowers", required = false, defaultValue = "") String findFollowers) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			if(limit>32) {
				responseCMS.setStatus(HttpStatus.BAD_REQUEST);
				responseCMS.setMessage("Khoảng cách thời gian quá rộng, tối đa "+31+" ngày");
				return responseCMS.build();
			}
			Date now=new Date();
			
			Date from=DateTimeUtil.getDateStartOfDay(DateTimeUtil.backDate(now, (int)limit-1));
			Date to=DateTimeUtil.endDate(now);
			long fromDate=from.getTime();
			long toDate=to.getTime();
			
			UserOrganization userTask=new UserOrganization();
			userTask.userId=userId;
			userTask.organizationId=organizationId;
	
			TaskFilter taskFilter=new TaskFilter();
			taskFilter.userTask=userTask;
			taskFilter.fromDate=fromDate;
			taskFilter.toDate=toDate;
			taskFilter.findOwners=findOwners;
			taskFilter.findAssistants=findAssistants;
			taskFilter.findAssignees=findAssignees;
			taskFilter.findFollowers=findFollowers;
			
			/* Kiểm tra categorykey */
			if(categorykey!=null && !categorykey.isEmpty()) {
				TaskCategory taskCategory=TaskCategory.getTaskCategory(categorykey);
				if(taskCategory==null) {
					responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
					responseCMS.setResult("categorykey không được chấp nhận");
					return responseCMS.build();
				}
				taskFilter.taskCategory=taskCategory;
			}
	
			List<Date> dates=DateTimeUtil.getDatesBetween(new Date(fromDate), new Date(toDate));
			LinkedHashMap<String, TaskSum> keyValues=new LinkedHashMap<String, TaskSum>();
			for (Date date : dates) {
				TaskSum dataInit=new TaskSum();
				dataInit.dateTime=DateTimeUtil.getDateFormatToSQL().format(date);
				dataInit.taskNumber=0;
				dataInit.taskCompleted=0;
				keyValues.put(DateTimeUtil.getDateFormatToSQL().format(date), dataInit);
			}
	
			/* Lấy nv chưa hoàn thành + đã hoàn thành */
			List<Task> tasks=taskRepositoryCustom.findAll(taskFilter, 0, 0);
			for (Task item : tasks) {
				String key=DateTimeUtil.getDateFormatToSQL().format(item.getCreatedTime());
				
				if(keyValues.containsKey(key)) {
					TaskSum dataReal=keyValues.get(key);
					dataReal.taskNumber++;
					keyValues.put(key, dataReal);
				}
			}
			
			/* Lấy nv đã hoàn thành */
			taskFilter.completedFromDate=fromDate;
			taskFilter.completedToDate=toDate;
			taskFilter.taskSubCategory=TaskSubCategory.DAHOANTHANH;
			tasks=taskRepositoryCustom.findAll(taskFilter, 0, 0);
			for (Task item : tasks) {
				String key=DateTimeUtil.getDateFormatToSQL().format(item.getCompletedTime());
				
				if(keyValues.containsKey(key)) {
					TaskSum dataReal=keyValues.get(key);
					dataReal.taskCompleted++;
					keyValues.put(key, dataReal);
				}
			}
			
			LinkedList<TaskSum> result=new LinkedList<TaskSum>();
			for(Map.Entry<String, TaskSum> entry:keyValues.entrySet()) {
				result.add(entry.getValue());
			}
			
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

	/**
	 * Gets the report top.
	 *
	 * @param limit the limit
	 * @param userId the user id
	 * @param organizationId the organization id
	 * @param categorykey the categorykey
	 * @param priority the priority
	 * @param findOwners the find owners
	 * @param findAssistants the find assistants
	 * @param findAssignees the find assignees
	 * @param findFollowers the find followers
	 * @return the report top
	 */
	@GetMapping("/task/list/top")
	public Object getReportTop(
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "userId", required = true) String userId, 
			@RequestParam(name = "organizationId", required = true) String organizationId, 
			@RequestParam(name = "categorykey", required = true, defaultValue = "") String categorykey, 
			@RequestParam(name = "priority", required = false, defaultValue = "0") int priority,
			@RequestParam(name = "findOwners", required = false, defaultValue = "") String findOwners,
			@RequestParam(name = "findAssistants", required = false, defaultValue = "") String findAssistants,
			@RequestParam(name = "findAssignees", required = false, defaultValue = "") String findAssignees,
			@RequestParam(name = "findFollowers", required = false, defaultValue = "") String findFollowers) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			if(limit>32) {
				responseCMS.setStatus(HttpStatus.BAD_REQUEST);
				responseCMS.setMessage("Top tối đa "+31+" nhiệm vụ");
				return responseCMS.build();
			}
			
			UserOrganization userTask=new UserOrganization();
			userTask.userId=userId;
			userTask.organizationId=organizationId;

			TaskFilter taskFilter=new TaskFilter();
			taskFilter.userTask=userTask;
			taskFilter.priority=priority;
			taskFilter.findOwners=findOwners;
			taskFilter.findAssistants=findAssistants;
			taskFilter.findAssignees=findAssignees;
			taskFilter.findFollowers=findFollowers;
			
			/* Kiểm tra categorykey */
			if(categorykey!=null && !categorykey.isEmpty()) {
				TaskCategory taskCategory=TaskCategory.getTaskCategory(categorykey);
				if(taskCategory==null) {
					responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
					responseCMS.setResult("categorykey không được chấp nhận");
					return responseCMS.build();
				}
				taskFilter.taskCategory=taskCategory;
			}
			taskFilter.taskSubCategory=TaskSubCategory.CHUAHOANTHANH;
			
			List<Task> tasks=taskRepositoryCustom.getListTop(taskFilter, 0, limit);
			List<Document> results=new ArrayList<Document>();
			for (Task item : tasks) {
				results.add(convertTaskList(item, null));
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setTotal(tasks.size());
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
	
	@GetMapping("/task/get-tag/{taskId}")
	public Object taskGetTags(
			@PathVariable(name = "taskId", required = true) String taskId,
			@RequestParam(name = "userId", required = true) String userId, 
			@RequestParam(name = "organizationId", required = true) String organizationId){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra taskId có tồn tại không */
			Task taskCheck=null;
			try {
				taskCheck=taskRepository.findById(new ObjectId(taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("taskId ["+taskId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			UserOrganization userTask=new UserOrganization();
			userTask.userId=userId;
			userTask.organizationId=organizationId;
			
			TagFilter tagFilter=new TagFilter();
			tagFilter.creator=userTask;
			tagFilter.taskIds=Arrays.asList(taskCheck.getId());
			
			List<Document> result=new ArrayList<Document>();
			List<Tag> tags=tagRepositoryCustom.findAll(tagFilter);
			for (Tag tag : tags) {
				result.add(tagControllerWebsite.convertTag(tag));
			}
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(result);
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
	
	@PutMapping("/task/set-tag/{taskId}")
	public Object taskSetTag(
			@PathVariable(name = "taskId", required = true) String taskId,
			@RequestParam(name = "tagId", required = true) String tagId){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra taskId có tồn tại không */
			Task taskUpdate=null;
			try {
				taskUpdate=taskRepository.findById(new ObjectId(taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("taskId ["+taskId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			Tag tagUpdate=null;
			try {
				tagUpdate=tagRepository.findById(new ObjectId(tagId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("tagId ["+tagId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			if(tagUpdate.taskIds!=null && tagUpdate.taskIds.contains(taskUpdate.getId())==false) {
				tagUpdate.taskIds.add(taskUpdate.getId());
				tagRepository.save(tagUpdate);
			}
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("Cập nhật thành công");
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
	
	
	@PutMapping("/task/unset-tag/{taskId}")
	public Object taskUnsetTag(
			@PathVariable(name = "taskId", required = true) String taskId,
			@RequestParam(name = "tagId", required = false) String tagId,
			@RequestParam(name = "userId", required = true) String userId, 
			@RequestParam(name = "organizationId", required = true) String organizationId){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra taskId có tồn tại không */
			Task taskCheck=null;
			try {
				taskCheck=taskRepository.findById(new ObjectId(taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("taskId ["+taskId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			UserOrganization userTask=new UserOrganization();
			userTask.userId=userId;
			userTask.organizationId=organizationId;
			
			TagFilter tagFilter=new TagFilter();
			tagFilter.creator=userTask;
			tagFilter.taskIds=Arrays.asList(taskCheck.getId());
			if(tagId!=null) {
				tagFilter._id=tagId;
			}
			
			List<Tag> tagUpdate=tagRepositoryCustom.findAll(tagFilter);
			for (Tag tag : tagUpdate) {
				tag.taskIds.remove(taskCheck.getId());
				tagRepository.save(tag);
			}
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("Cập nhật thành công");
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
	
	@PutMapping("/task/set-assignee-user/{taskId}")
	public Object taskSetAssigneeUser(
			@PathVariable(name = "taskId", required = true) String taskId,
			@RequestBody @Valid ReqTaskSetAssigneeUser reqTaskSetAssigneeUser){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra taskId có tồn tại không */
			Task taskUpdate=null;
			try {
				taskUpdate=taskRepository.findById(new ObjectId(taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("taskId ["+taskId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			if(taskUpdate.assignmentType.equalsIgnoreCase(TaskAssignmentType.User.getKey())) {
				responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setMessage("Đây là nhiệm vụ cá nhân, không thể gán cán bộ");
				return responseCMS.build();
			}
			
			User user=null;
			try {
				user=userRepository.findById(new ObjectId(reqTaskSetAssigneeUser.userId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("userId ["+reqTaskSetAssigneeUser.userId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Kiểm tra user có nằm cùng tổ chức không */
			boolean exists=false;
			for(UserOrganizationExpand item:user.getOrganizations()) {
				if(item.getOrganizationId().equals(taskUpdate.assigneeTask.organizationId)) {
					exists=true;
				}
			}
			
			if(exists==false) {
				responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setMessage("userId ["+reqTaskSetAssigneeUser.userId+"] không nằm trong cùng đơn vị/cơ quan");
				return responseCMS.build();
			}
			
			/********************************************************/
			UserOrganization assignementBy=new UserOrganization();
			User userAction=null;
			try {
				userAction=userRepository.findById(new ObjectId(reqTaskSetAssigneeUser.assignmentBy.userId)).get();
				assignementBy.userId=userAction.getId();
				assignementBy.fullName=userAction.fullName;
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("assignmentBy.userId ["+reqTaskSetAssigneeUser.assignmentBy.userId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			Organization organizationAction=null;
			try {
				organizationAction=organizationRepository.findById(new ObjectId(reqTaskSetAssigneeUser.assignmentBy.organizationId)).get();
				assignementBy.organizationId=organizationAction.getId();
				assignementBy.organizationName=organizationAction.name;
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("assignmentBy.organizationId ["+reqTaskSetAssigneeUser.assignmentBy.organizationId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			taskUpdate.assigneeTask.userId=user.getId();
			taskUpdate.assigneeTask.fullName=user.getFullName();
			
			TaskEvent taskEvent=new TaskEvent();
			taskEvent.creator=assignementBy;
			taskEvent.title="Đơn vị xử lý: phân nhiệm vụ cho cán bộ";
			taskEvent.descriptions.put("Lý do", assignementBy.getOrganizationName()+" đã phân cán bộ xử lý nhiệm vụ đơn vị cho ["+user.getFullName()+"]");
			taskEvent.action=NotifyCaption.PhanNhiemVuDonVi.getAction();
			
			taskUpdate.events.add(0, taskEvent);
			
			/* Thông báo */
			Notify taskNotify=new Notify();
			taskNotify.taskId=taskUpdate.getId();
			taskNotify.action=NotifyCaption.PhanNhiemVuDonVi.getAction();
			taskNotify.title=NotifyCaption.PhanNhiemVuDonVi.getTitle();
			taskNotify.content=taskUpdate.title;
			taskNotify.viewed=false;
			taskNotify.creator=assignementBy;
			
			UserOrganization assigneeTask=taskUpdate.assigneeTask;
			Thread thread=new Thread(new Runnable() {
				@Override
				public void run() {
					/* Danh sách các người có thể nhận thông báo */
					List<UserOrganization> userTasks=new ArrayList<UserOrganization>();
					userTasks.add(assigneeTask);
					
					/* Thông báo */
					if(taskNotify.creator.validAll() && userTasks.size()>0) {
						
						/* Kiểm tra lại thông tin người nhận, và loại bỏ nếu trùng hoặc là creator */
						for(int i=0;i<userTasks.size();i++) {
							UserOrganization userTask=userTasks.get(i);
							if(taskNotify.creator.compareTo(userTask)) {
								userTasks.remove(userTask);
							}
						}
						
						/* Thông báo trên firebase */
						String title = taskNotify.creator.fullName+" đã " +taskNotify.title.toLowerCase();
						String content = taskNotify.content;
						Map<String,String> data = new HashMap<String,String>();
						data.put("taskId", taskNotify.taskId);
						data.put("action", taskNotify.action);
						
						/* Thông báo cho người còn lại */
						for(UserOrganization userTask:userTasks) {
							taskNotify.reNewId();
							taskNotify.receiver=userTask;
							taskNotifyRepository.save(taskNotify);
							
							/* Thông báo trên firebase */
							try {
								String topic = "giaoviecvptw_"+taskNotify.receiver.userId;
								firebaseService.sendToTopic(topic,title,content, data);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					/* End thông báo */
					
				}
			});
			thread.start();
			/* End thông báo */
			
			taskRepository.save(taskUpdate);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("Cập nhật thành công");
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
	
	@PutMapping("/task/unset-assignee-user/{taskId}")
	public Object taskUnSetAssigneeUser(
			@PathVariable(name = "taskId", required = true) String taskId,
			@RequestBody @Valid ReqTaskUnSetAssigneeUser reqTaskUnSetAssigneeUser){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra taskId có tồn tại không */
			Task taskUpdate=null;
			try {
				taskUpdate=taskRepository.findById(new ObjectId(taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("taskId ["+taskId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			if(taskUpdate.assignmentType.equalsIgnoreCase(TaskAssignmentType.User.getKey())) {
				responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setMessage("Đây là nhiệm vụ cá nhân, không thể hủy gán cán bộ xử lý");
				return responseCMS.build();
			}else if(taskUpdate.assigneeTask.validUser()==false) {
				responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setMessage("Đơn vị đã hủy gán cán bộ xử lý trước đó");
				return responseCMS.build();
			}
			
			UserOrganization assignmentBy=new UserOrganization();
			User userAction=null;
			try {
				userAction=userRepository.findById(new ObjectId(reqTaskUnSetAssigneeUser.unassignmentBy.userId)).get();
				assignmentBy.userId=userAction.getId();
				assignmentBy.fullName=userAction.fullName;
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("unassignmentBy.userId ["+reqTaskUnSetAssigneeUser.unassignmentBy.userId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			Organization organizationAction=null;
			try {
				organizationAction=organizationRepository.findById(new ObjectId(reqTaskUnSetAssigneeUser.unassignmentBy.organizationId)).get();
				assignmentBy.organizationId=organizationAction.getId();
				assignmentBy.organizationName=organizationAction.name;
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("unassignmentBy.organizationId ["+reqTaskUnSetAssigneeUser.unassignmentBy.organizationId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			String fullNameOld=taskUpdate.assigneeTask.getFullName();
			
			UserOrganization assigneeTaskOld=taskUpdate.assigneeTask;
			taskUpdate.assigneeTask.userId=null;
			taskUpdate.assigneeTask.fullName=null;
			
			TaskEvent taskEvent=new TaskEvent();
			taskEvent.creator=assignmentBy;
			taskEvent.title="Đơn vị xử lý: hủy phân cán bộ xử lý";
			taskEvent.descriptions.put("Mô tả", assignmentBy.getOrganizationName()+" đã hủy phân cán bộ xử lý nhiệm vụ đơn vị cho ["+fullNameOld+"]");
			taskEvent.descriptions.put("Lý do", reqTaskUnSetAssigneeUser.getReason());
			taskEvent.action=NotifyCaption.HuyPhanNhiemVuDonVi.getAction();
			
			taskUpdate.events.add(0, taskEvent);
			
			/* Thông báo */
			Notify taskNotify=new Notify();
			taskNotify.taskId=taskUpdate.getId();
			taskNotify.action=NotifyCaption.HuyPhanNhiemVuDonVi.getAction();
			taskNotify.title=NotifyCaption.HuyPhanNhiemVuDonVi.getTitle();
			taskNotify.content=taskUpdate.title;
			taskNotify.viewed=false;
			taskNotify.creator=assignmentBy;
			
			
			Thread thread=new Thread(new Runnable() {
				@Override
				public void run() {
					/* Danh sách các người có thể nhận thông báo */
					List<UserOrganization> userTasks=new ArrayList<UserOrganization>();
					userTasks.add(assigneeTaskOld);
					
					/* Thông báo */
					if(taskNotify.creator.validAll() && userTasks.size()>0) {
						
						/* Kiểm tra lại thông tin người nhận, và loại bỏ nếu trùng hoặc là creator */
						for(int i=0;i<userTasks.size();i++) {
							UserOrganization userTask=userTasks.get(i);
							if(taskNotify.creator.compareTo(userTask)) {
								userTasks.remove(userTask);
							}
						}
						
						/* Thông báo trên firebase */
						String title = taskNotify.creator.fullName+" đã " +taskNotify.title.toLowerCase();
						String content = taskNotify.content;
						Map<String,String> data = new HashMap<String,String>();
						data.put("taskId", taskNotify.taskId);
						data.put("action", taskNotify.action);
						
						/* Thông báo cho người còn lại */
						for(UserOrganization userTask:userTasks) {
							taskNotify.reNewId();
							taskNotify.receiver=userTask;
							taskNotifyRepository.save(taskNotify);
							
							/* Thông báo trên firebase */
							try {
								String topic = "giaoviecvptw_"+taskNotify.receiver.userId;
								firebaseService.sendToTopic(topic,title,content, data);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					/* End thông báo */
					
				}
			});
			thread.start();
			/* End thông báo */
			
			taskRepository.save(taskUpdate);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("Cập nhật thành công");
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
	
	@PutMapping("/task/set-follow-user/{taskId}")
	public Object taskSetFollowUser(
			@PathVariable(name = "taskId", required = true) String taskId,
			@RequestBody @Valid ReqTaskSetFollowUser reqTaskSetFollowUser){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra taskId có tồn tại không */
			Task taskUpdate=null;
			try {
				taskUpdate=taskRepository.findById(new ObjectId(taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("taskId ["+taskId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			if(taskUpdate.assignmentType.equalsIgnoreCase(TaskAssignmentType.User.getKey())) {
				responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setMessage("Đây là nhiệm vụ cá nhân, không thể gán cán bộ");
				return responseCMS.build();
			}
			
			User user=null;
			try {
				user=userRepository.findById(new ObjectId(reqTaskSetFollowUser.userId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("userId ["+reqTaskSetFollowUser.userId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			Organization organization=null;
			try {
				organization=organizationRepository.findById(new ObjectId(reqTaskSetFollowUser.organizationId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("organizationId ["+reqTaskSetFollowUser.organizationId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Kiểm tra organizationId cho followers */
			boolean check=false;
			for(UserOrganization userOrganization:taskUpdate.followersTask) {
				if(userOrganization.organizationId.equals(reqTaskSetFollowUser.organizationId)) {
					check=true;
					break;
				}
			}
			
			if(check==false) {
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("organizationId ["+reqTaskSetFollowUser.organizationId+"] không tồn tại trong nhiệm vụ");
				return responseCMS.build();
			}
			
			/* Kiểm tra user có nằm cùng tổ chức không */
			boolean exists=false;
			for(UserOrganizationExpand item:user.getOrganizations()) {
				if(item.getOrganizationId().equals(reqTaskSetFollowUser.organizationId)) {
					exists=true;
				}
			}
			
			if(exists==false) {
				responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setMessage("userId ["+reqTaskSetFollowUser.userId+"] không nằm trong cùng đơn vị/cơ quan organizationId ["+reqTaskSetFollowUser.organizationId+"]");
				return responseCMS.build();
			}
			
			/********************************************************/
			UserOrganization assignementBy=new UserOrganization();
			User userAction=null;
			try {
				userAction=userRepository.findById(new ObjectId(reqTaskSetFollowUser.assignmentBy.userId)).get();
				assignementBy.userId=userAction.getId();
				assignementBy.fullName=userAction.fullName;
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("assignmentBy.userId ["+reqTaskSetFollowUser.assignmentBy.userId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			Organization organizationAction=null;
			try {
				organizationAction=organizationRepository.findById(new ObjectId(reqTaskSetFollowUser.assignmentBy.organizationId)).get();
				assignementBy.organizationId=organizationAction.getId();
				assignementBy.organizationName=organizationAction.name;
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("assignmentBy.organizationId ["+reqTaskSetFollowUser.assignmentBy.organizationId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Tìm và cập nhật UserOrganization cho followers */
			for(UserOrganization userOrganization:taskUpdate.followersTask) {
				if(userOrganization.organizationId.equals(organization.getId())) {
					userOrganization.userId=user.getId();
					userOrganization.fullName=user.getFullName();
					break;
				}
			}
			
			TaskEvent taskEvent=new TaskEvent();
			taskEvent.creator=assignementBy;
			taskEvent.title="Đơn vị theo dõi (hỗ trợ): phân nhiệm vụ cho cán bộ";
			taskEvent.descriptions.put("Lý do", assignementBy.getOrganizationName()+" đã phân cán bộ xử lý nhiệm vụ đơn vị cho ["+user.getFullName()+"]");
			taskEvent.action=NotifyCaption.PhanNhiemVuDonVi.getAction();
			
			taskUpdate.events.add(0, taskEvent);
			
			taskRepository.save(taskUpdate);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("Cập nhật thành công");
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
	
	@PutMapping("/task/unset-follow-user/{taskId}")
	public Object taskUnSetFollowUser(
			@PathVariable(name = "taskId", required = true) String taskId,
			@RequestBody @Valid ReqTaskUnSetFollowUser reqTaskUnSetFollowUser){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra taskId có tồn tại không */
			Task taskUpdate=null;
			try {
				taskUpdate=taskRepository.findById(new ObjectId(taskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("taskId ["+taskId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			/* Check nếu không phải là nhiệm vụ giao loại organization không */
			if(taskUpdate.assignmentType.equalsIgnoreCase(TaskAssignmentType.Organization.getKey())==false) {
				responseCMS.setStatus(HttpStatus.NOT_ACCEPTABLE);
				responseCMS.setMessage("Đây là nhiệm vụ cá nhân, không thể hủy gán cán bộ xử lý");
				return responseCMS.build();
			}
			
			/* Kiểm tra organizationId */
			Organization organization=null;
			try {
				organization=organizationRepository.findById(new ObjectId(reqTaskUnSetFollowUser.organizationId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("organizationId ["+reqTaskUnSetFollowUser.organizationId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Khởi tạo đối tượng assignmentBy */
			UserOrganization assignmentBy=new UserOrganization();
			
			/* Kiểm tra userId thực hiện yêu cầu */
			User userAction=null;
			try {
				userAction=userRepository.findById(new ObjectId(reqTaskUnSetFollowUser.unassignmentBy.userId)).get();
				assignmentBy.userId=userAction.getId();
				assignmentBy.fullName=userAction.fullName;
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("unassignmentBy.userId ["+reqTaskUnSetFollowUser.unassignmentBy.userId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			/* Kiểm tra organizationId thực hiện yêu cầu */
			Organization organizationAction=null;
			try {
				organizationAction=organizationRepository.findById(new ObjectId(reqTaskUnSetFollowUser.unassignmentBy.organizationId)).get();
				assignmentBy.organizationId=organizationAction.getId();
				assignmentBy.organizationName=organizationAction.name;
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("unassignmentBy.organizationId ["+reqTaskUnSetFollowUser.unassignmentBy.organizationId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Kiểm tra organizationId có trong danh sách theo dõi không */
			UserOrganization followTaskOld=null;
			for(UserOrganization userOrganization:taskUpdate.followersTask) {
				if(userOrganization.getOrganizationId().equals(organization.getId())) {
					userOrganization.userId=null;
					userOrganization.fullName=null;
					followTaskOld=userOrganization;
					break;
				}
			}
			
			/* Kiểm tra có tồn tại đơn vị cần hủy không */
			if(followTaskOld==null) {
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("organizationId ["+reqTaskUnSetFollowUser.organizationId+"] không tồn tại trong nhiệm vụ");
				return responseCMS.build();
			}
			
			/* Tên cán bộ được gán trước đó, để lưu lại nhật ký */
			String fullNameOld=followTaskOld.getFullName();
			
			TaskEvent taskEvent=new TaskEvent();
			taskEvent.creator=assignmentBy;
			taskEvent.title="Đơn vị theo dõi (hỗ trợ): hủy phân cán bộ xử lý";
			taskEvent.descriptions.put("Mô tả", assignmentBy.getOrganizationName()+" đã hủy phân cán bộ xử lý nhiệm vụ đơn vị cho ["+fullNameOld+"]");
			taskEvent.descriptions.put("Lý do", reqTaskUnSetFollowUser.getReason());
			taskEvent.action=NotifyCaption.HuyPhanNhiemVuDonVi.getAction();
			
			taskUpdate.events.add(0, taskEvent);
			
			taskRepository.save(taskUpdate);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("Cập nhật thành công");
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
	
	/**
	 * Change assignee.
	 *
	 * @param currentTask the current task
	 * @param oldAssigneeTask the old assignee task
	 * @param newAssigneeTask the new assignee task
	 */
	protected void changeAssignee(Task currentTask, UserOrganization oldAssigneeTask, UserOrganization newAssigneeTask) {
		/* Nếu có subtask */
		if(currentTask.parentId!=null) {
			TaskFilter taskFilter=new TaskFilter();
			taskFilter.parentId=currentTask.getId();
			List<Task> tasks=taskRepositoryCustom.findAll(taskFilter, 0, 0);
			for (Task subTaskUpdate : tasks) {
				/* Cập nhật data */
				try {
					/* -------------------- Ghi log ---------------------- */
					TaskEvent taskEvent=new TaskEvent();
					taskEvent.creator=null;
					taskEvent.title="Cập nhật người giao";
					taskEvent.descriptions.put("Miêu tả", "Cán bộ "+currentTask.ownerTask.fullName+" đã thay đổi người giao từ "+subTaskUpdate.ownerTask.fullName+" sang "+newAssigneeTask.fullName);
					taskEvent.action=NotifyCaption.CapNhatNhiemVu.getAction();
					
					subTaskUpdate.events.add(0, taskEvent);
					/* --------------------- End ghi log ------------------------*/

					UserOrganization oldOwnerTask=subTaskUpdate.ownerTask;
					UserOrganization newOwnerTask=newAssigneeTask;
					
					/* Thay đổi owner sang assignee mới */
					subTaskUpdate.ownerTask=newAssigneeTask;
					
					/* Save task vào DB */
					subTaskUpdate=taskRepository.save(subTaskUpdate);
					
					/* Thông báo */
					createSubTaskNotifyForChangeAssignee(currentTask, subTaskUpdate, oldOwnerTask, newOwnerTask);
					/* End thông báo */
				} catch (Exception e) {
					e.printStackTrace();
					log.debug(e.getMessage());
				}
			}
		}
	}
	
	/**
	 * Creates the task notify.
	 *
	 * @param taskNotify the task notify
	 * @param task the task
	 * @param userRequest the user request
	 */
	protected void createTaskNotify(Notify taskNotify, Task task, User userRequest) {
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				/* Danh sách các người có thể nhận thông báo */
				List<UserOrganization> userTasks=new ArrayList<UserOrganization>();
				userTasks.add(task.ownerTask);
				userTasks.add(task.assigneeTask);
				userTasks.addAll(task.followersTask);
				
				/* Tìm người tạo nếu chưa được gán */
				if(taskNotify.creator.validAll()==false) {
					for(UserOrganization userTask:userTasks) {
						if(userTask.userId.equalsIgnoreCase(userRequest.getId())) {
							taskNotify.creator=userTask;
						}
					}
				}
				
				
				/* Thông báo */
				if(taskNotify.creator.validAll() && userTasks.size()>0) {
					
					/* Kiểm tra lại thông tin người nhận, và loại bỏ nếu trùng hoặc là creator */
					for(int i=0;i<userTasks.size();i++) {
						UserOrganization userTask=userTasks.get(i);
						if(taskNotify.creator.compareTo(userTask)) {
							userTasks.remove(userTask);
						}
					}
					
					/* Thông báo trên firebase */
					String title = taskNotify.creator.fullName+" đã " +taskNotify.title.toLowerCase();
					String content = taskNotify.content;
					Map<String,String> data = new HashMap<String,String>();
					data.put("taskId", taskNotify.taskId);
					data.put("action", taskNotify.action);
					
					/* Thông báo cho người còn lại */
					for(UserOrganization userTask:userTasks) {
						taskNotify.reNewId();
						taskNotify.receiver=userTask;
						taskNotifyRepository.save(taskNotify);
						
						/* Thông báo trên firebase */
						try {
							String topic = "giaoviecvptw_"+taskNotify.receiver.userId;
							firebaseService.sendToTopic(topic,title,content, data);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				/* End thông báo */
				
			}
		});
		thread.start();
	}
	
	/**
	 * Creates the task notify for change assignee.
	 *
	 * @param task the task
	 * @param userRequest the user request
	 * @param oldAssigneeTask the old assignee task
	 * @param newAssigneeTask the new assignee task
	 */
	protected void createTaskNotifyForChangeAssignee(Task task, User userRequest, UserOrganization oldAssigneeTask, UserOrganization newAssigneeTask) {
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				Notify taskNotify=new Notify();
				taskNotify.creator=task.ownerTask;
				taskNotify.taskId=task.getId();
				taskNotify.action=NotifyCaption.ThayDoiChuTri.getAction();
				taskNotify.viewed=false;
				
				/* Trích tóm tắt */
				String titleSumary=task.title;
				if(titleSumary.length()>50) {
					titleSumary=titleSumary.substring(0, 50)+"...";
				}
				
				/* ---------------------- Thông báo cho assignee cũ ------------------------------------*/
				String oldOwnerContent="Nhiệm vụ ["+titleSumary+"] được thay đổi sang "+newAssigneeTask.getText()+" chủ trì xử lý";
				List<UserOrganization> userTasks=new ArrayList<UserOrganization>();
				userTasks.add(oldAssigneeTask);
				
				String title = NotifyCaption.ThayDoiChuTri.getTitle().toLowerCase();
				String content = oldOwnerContent;
				Map<String,String> data = new HashMap<String,String>();
				data.put("taskId", taskNotify.taskId);
				data.put("action", taskNotify.action);
				
				taskNotify.title=title;
				taskNotify.content=content;
				for(UserOrganization userTask:userTasks) {
					taskNotify.reNewId();
					taskNotify.receiver=userTask;
					taskNotifyRepository.save(taskNotify);
					
					try {
						String topic = "giaoviecvptw_"+taskNotify.receiver.userId;
						firebaseService.sendToTopic(topic,title,content, data);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				/* ---------------------- Thông báo cho assignee mới -----------------------------------*/
				String newOwnerContent="Nhiệm vụ ["+titleSumary+"] đã được thay đổi sang "+newAssigneeTask.getText()+" để chủ trì xử lý";
				userTasks=new ArrayList<UserOrganization>();
				userTasks.add(newAssigneeTask);
				
				title = NotifyCaption.ThayDoiChuTri.getTitle().toLowerCase();
				content = newOwnerContent;
				data = new HashMap<String,String>();
				data.put("taskId", taskNotify.taskId);
				data.put("action", taskNotify.action);
				
				taskNotify.title=title;
				taskNotify.content=content;
				for(UserOrganization userTask:userTasks) {
					taskNotify.reNewId();
					taskNotify.receiver=userTask;
					taskNotifyRepository.save(taskNotify);
					
					try {
						String topic = "giaoviecvptw_"+taskNotify.receiver.userId;
						firebaseService.sendToTopic(topic,title,content, data);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				
				/* ------------------- Thông báo cho followers --------------------------*/
				if(task.followersTask.size()>0) {
					String currentAssigneeAndFollowersContent="Nhiệm vụ ["+titleSumary+"] được thay đổi sang cán bộ "+newAssigneeTask.fullName+" chủ trì xử lý";
					userTasks=new ArrayList<UserOrganization>();
					userTasks.addAll(task.followersTask);
					
					title = NotifyCaption.ThayDoiChuTri.getTitle().toLowerCase();
					content = currentAssigneeAndFollowersContent;
					data = new HashMap<String,String>();
					data.put("taskId", taskNotify.taskId);
					data.put("action", taskNotify.action);
					
					taskNotify.title=title;
					taskNotify.content=content;
					for(UserOrganization userTask:userTasks) {
						taskNotify.reNewId();
						taskNotify.receiver=userTask;
						taskNotifyRepository.save(taskNotify);
						
						try {
							String topic = "giaoviecvptw_"+taskNotify.receiver.userId;
							firebaseService.sendToTopic(topic,title,content, data);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		thread.start();
	}
	
	/**
	 * Creates the sub task notify for change assignee.
	 *
	 * @param currentTask the current task
	 * @param subTask the sub task
	 * @param oldOwnerTask the old owner task
	 * @param newOwnerTask the new owner task
	 */
	protected void createSubTaskNotifyForChangeAssignee(Task currentTask, Task subTask, UserOrganization oldOwnerTask, UserOrganization newOwnerTask) {
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				Notify taskNotify=new Notify();
				taskNotify.creator=currentTask.ownerTask;
				taskNotify.taskId=subTask.getId();
				taskNotify.action=NotifyCaption.ThayDoiNguoiGiao.getAction();
				taskNotify.viewed=false;
				
				/* Trích tóm tắt */
				String titleSumary=subTask.title;
				if(titleSumary.length()>50) {
					titleSumary=titleSumary.substring(0, 50)+"...";
				}
				
				/* ---------------------- Thông báo cho owner cũ ------------------------------------*/
				String oldOwnerContent="Nhiệm vụ ["+titleSumary+"] của cán bộ được thay đổi sang cán bộ "+newOwnerTask.fullName+" quản lý";
				List<UserOrganization> userTasks=new ArrayList<UserOrganization>();
				userTasks.add(oldOwnerTask);
				
				String title = NotifyCaption.ThayDoiNguoiGiao.getTitle().toLowerCase();
				String content = oldOwnerContent;
				Map<String,String> data = new HashMap<String,String>();
				data.put("taskId", taskNotify.taskId);
				data.put("action", taskNotify.action);
				
				taskNotify.title=title;
				taskNotify.content=content;
				for(UserOrganization userTask:userTasks) {
					taskNotify.reNewId();
					taskNotify.receiver=userTask;
					taskNotifyRepository.save(taskNotify);
					
					try {
						String topic = "giaoviecvptw_"+taskNotify.receiver.userId;
						firebaseService.sendToTopic(topic,title,content, data);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				/* ---------------------- Thông báo cho owner mới -----------------------------------*/
				String newOwnerContent="Nhiệm vụ ["+titleSumary+"] đã được thay đổi sang cán bộ để quản lý";
				userTasks=new ArrayList<UserOrganization>();
				userTasks.add(newOwnerTask);
				
				title = NotifyCaption.ThayDoiNguoiGiao.getTitle().toLowerCase();
				content = newOwnerContent;
				data = new HashMap<String,String>();
				data.put("taskId", taskNotify.taskId);
				data.put("action", taskNotify.action);
				
				taskNotify.title=title;
				taskNotify.content=content;
				for(UserOrganization userTask:userTasks) {
					taskNotify.reNewId();
					taskNotify.receiver=userTask;
					taskNotifyRepository.save(taskNotify);
					
					try {
						String topic = "giaoviecvptw_"+taskNotify.receiver.userId;
						firebaseService.sendToTopic(topic,title,content, data);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				
				/* ------------------- Thông báo cho assignee và followers --------------------------*/
				String currentAssigneeAndFollowersContent="Nhiệm vụ ["+titleSumary+"] được thay đổi sang cán bộ "+newOwnerTask.fullName+" để quản lý";
				userTasks=new ArrayList<UserOrganization>();
				userTasks.add(subTask.assigneeTask);
				userTasks.addAll(subTask.followersTask);
				
				title = NotifyCaption.ThayDoiNguoiGiao.getTitle().toLowerCase();
				content = currentAssigneeAndFollowersContent;
				data = new HashMap<String,String>();
				data.put("taskId", taskNotify.taskId);
				data.put("action", taskNotify.action);
				
				taskNotify.title=title;
				taskNotify.content=content;
				for(UserOrganization userTask:userTasks) {
					taskNotify.reNewId();
					taskNotify.receiver=userTask;
					taskNotifyRepository.save(taskNotify);
					
					try {
						String topic = "giaoviecvptw_"+taskNotify.receiver.userId;
						firebaseService.sendToTopic(topic,title,content, data);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}
		});
		thread.start();
	}
	
	/**
	 * Convert task list.
	 *
	 * @param task the task
	 * @return the document
	 */
	/*---------------------------------- convert --------------------------------*/
	protected Document convertTaskList(Task task, UserOrganization userTask) {
		Document document=new Document();
		document.append("createdTime", task.getCreatedTime());
		document.append("id", task.getId());
		document.append("owner", task.ownerTask);
		document.append("assignee", task.assigneeTask);
		document.append("assistantTask", task.assistantTask);
		document.append("followersTask", task.followersTask);
		document.append("title", task.title);
		document.append("description", task.description);
		document.append("priority", task.priority);
		document.append("priorityName", TaskPriority.getName(task.priority));
		document.append("endTime", task.getEndTime());
		document.append("completedTime", task.getCompletedTime());
		document.append("acceptedTime", task.getAcceptedTime());
		document.append("parentId", task.parentId);
		document.append("countSubTask", task.countSubTask);
		
		int countComment=0;
		List<TaskComment> taskComments=task.comments;
		for (TaskComment taskComment : taskComments) {
			countComment++;
			countComment+=taskComment.replies.size();
		}
		document.append("comments", countComment);
		document.append("reminds", task.reminds.size());
		document.append("events", task.events.size());
		if(task.processes.size()>0) {
			document.append("processes", task.processes.getFirst().percent);
		}else {
			document.append("processes", 0);
		}
		document.append("attachments", task.attachments.size());
		document.append("rating", task.rating);
		document.append("assignmentType", task.assignmentType);
		
		List<Document> tagsData=new ArrayList<Document>();
		if(userTask!=null) {
			try {
				TagFilter tagFilter=new TagFilter();
				tagFilter.creator=userTask;
				tagFilter.taskIds=Arrays.asList(task.getId());
				
				List<Tag> tags=tagRepositoryCustom.findAll(tagFilter);
				for (Tag tag : tags) {
					Document tagData=new Document();
					tagData.append("id", tag.getId());
					tagData.append("name", tag.name);
					
					tagsData.add(tagData);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		document.append("tags", tagsData);
		
		return document;
	}

	/**
	 * Convert task detail.
	 *
	 * @param task the task
	 * @return the document
	 */
	protected Document convertTaskDetail(Task task, UserOrganization userTask) {
		Document document=new Document();
		document.append("createdTime", task.getCreatedTime());
		document.append("updatedTime", task.getUpdatedTime());
		document.append("id", task.getId());
		document.append("owner", task.ownerTask);
		document.append("assignee", task.assigneeTask);
		document.append("assistantTask", task.assistantTask);
		document.append("followersTask", task.followersTask);
		document.append("title", task.title);
		document.append("description", task.description);
		document.append("priority", task.priority);
		document.append("priorityName", TaskPriority.getName(task.priority));
		document.append("endTime", task.getEndTime());
		document.append("completedTime", task.getCompletedTime());
		document.append("acceptedTime", task.getAcceptedTime());
		document.append("parentId", task.parentId);
		document.append("countSubTask", task.countSubTask);
		document.append("comments", task.comments);
		document.append("reminds", task.reminds);
		document.append("events", task.events);
		document.append("processes", task.processes);
		document.append("attachments", task.attachments);
		document.append("rating", task.rating);
		document.append("assignmentType", task.assignmentType);
		
		/* Get tags */
		List<Document> tagsData=new ArrayList<Document>();
		if(userTask!=null && userTask.validIds()) {
			try {
				TagFilter tagFilter=new TagFilter();
				tagFilter.creator=userTask;
				tagFilter.taskIds=Arrays.asList(task.getId());
				
				List<Tag> tags=tagRepositoryCustom.findAll(tagFilter);
				for (Tag tag : tags) {
					Document tagData=new Document();
					tagData.append("id", tag.getId());
					tagData.append("name", tag.name);
					
					tagsData.add(tagData);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		document.append("tags", tagsData);
		
		/* subTasks */
		try {
			List<Document> subList=new ArrayList<Document>();
			if(task.countSubTask>0) {
				TaskFilter taskFilter=new TaskFilter();
				taskFilter.parentId=task.getId();
				List<Task> subTasks=taskRepositoryCustom.findAll(taskFilter, 0, 0);
				for (Task subTask : subTasks) {
					subList.add(convertTaskDetail(subTask, userTask));
				}
			}
			document.append("subTasks", subList);
		} catch (Exception e) {
			document.append("subTasks", new ArrayList<>());
		}
		
		/* treeTasks */
		try {
			document.append("treeTasks", new ArrayList<>());
		} catch (Exception e) {
			document.append("treeTasks", new ArrayList<>());
		}
		
		return document;
	}
	
	/**
	 * Reverse event.
	 *
	 * @param _taskEvents the task events
	 * @return the linked list
	 */
	@SuppressWarnings({ "rawtypes" })
	protected LinkedList<TaskEvent> reverseEvent(LinkedList<TaskEvent> _taskEvents){
		LinkedList<TaskEvent> reverseData=new LinkedList<>();
		for (Iterator iterator = _taskEvents.iterator(); iterator.hasNext();) {
			TaskEvent taskEvent = (TaskEvent) iterator.next();
			reverseData.add(0, taskEvent);
		}
		return reverseData;
	}
	
	/**
	 * Tree tasks.
	 *
	 * @param task the task
	 * @return the document
	 */
	protected Document treeTasks(Task task) {
		Document document = convertTaskList(task, null);
		
		List<Document> subList=new ArrayList<Document>();
		if(task.countSubTask>0) {
			TaskFilter taskFilter=new TaskFilter();
			taskFilter.parentId=task.getId();
			List<Task> subTasks=taskRepositoryCustom.findAll(taskFilter, 0, 0);
			for (Task subTask : subTasks) {
				subList.add(treeTasks(subTask));
			}
		}
		document.append("subTasks", subList);
		return document;
	}
}
