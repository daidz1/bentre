package ws.core.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ws.core.enums.DocCategory;
import ws.core.enums.LogMessages;
import ws.core.enums.NotifyCaption;
import ws.core.enums.Permission;
import ws.core.enums.TaskAssignmentStatus;
import ws.core.enums.TaskAssignmentType;
import ws.core.enums.TaskCategory;
import ws.core.enums.TaskSubCategory;
import ws.core.model.Notify;
import ws.core.model.Task;
import ws.core.model.UserOrganization;
import ws.core.model.UserOrganizationCreator;
import ws.core.model.UserTaskCount;
import ws.core.model.filter.DocFilter;
import ws.core.model.filter.NotifyFilter;
import ws.core.model.filter.TaskFilter;
import ws.core.repository.DocRepositoryCustom;
import ws.core.repository.NotifyRepository;
import ws.core.repository.NotifyRepositoryCustom;
import ws.core.repository.OrganizationRepository;
import ws.core.repository.OrganizationRoleRepository;
import ws.core.repository.OrganizationRoleRepositoryCustom;
import ws.core.repository.TaskRepository;
import ws.core.repository.TaskRepositoryCustom;
import ws.core.repository.UserRepository;
import ws.core.repository.UserRepositoryCustom;
import ws.core.repository.imp.OrganizationRepositoryCustomImp;
import ws.core.service.OrganizationRoleService;
import ws.core.service.OrganizationService;
import ws.core.service.TaskAttachmentService;
import ws.core.service.TaskService;
import ws.core.util.DateTimeUtil;
import ws.core.util.ResponseCMS;

@RestController
@RequestMapping("/website")
public class FeatureControllerWebsite {
	private Logger log = LogManager.getLogger(FeatureControllerWebsite.class);
	
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
	protected NotifyRepository taskNotifyRepository;
	
	@Autowired
	protected NotifyRepositoryCustom taskNotifyRepositoryCustom;
	
	@Autowired
	protected OrganizationService organizationService;
	
	@Autowired
	protected TaskService taskService;
	
	@Autowired
	protected TaskAttachmentService taskAttachmentService;
	
	@Autowired
	protected DocRepositoryCustom docRepositoryCustom;
	
	@Autowired 
	protected OrganizationRoleService organizationRoleService;
	
	@GetMapping("/feature/menucount")
	public Object getMenuCount(
			@RequestParam(name = "userId", required = true) String userId, 
			@RequestParam(name = "organizationId", required = true) String organizationId,
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Đếm văn bản */
			UserOrganizationCreator creatorDoc=new UserOrganizationCreator();
			creatorDoc.userId=userId;
			creatorDoc.organizationId=organizationId;
			
			DocFilter docFilter=new DocFilter();
			docFilter.fromDate=fromDate;
			docFilter.toDate=toDate;
			
			Document vanban=new Document();
			docFilter.docCreator=creatorDoc;
			
			docFilter.docCategory=DocCategory.CVDEN.getKey();
			vanban.append("vanbanden", docRepositoryCustom.countAll(docFilter));
			
			docFilter.docCategory=DocCategory.CVDI.getKey();
			vanban.append("vanbandi", docRepositoryCustom.countAll(docFilter));
			
//			if(user.getUser().accountDomino!=null) {
//				docFilter.creatorDoc=creatorDoc;
//				
//				docFilter.docCategory=DocCategory.CVDEN.getKey();
//				vanban.append("vanbanden", docRepositoryCustom.countAll(docFilter));
//				
//				docFilter.docCategory=DocCategory.CVDI.getKey();
//				vanban.append("vanbandi", docRepositoryCustom.countAll(docFilter));
//			}else {
//				vanban.append("vanbanden", 0);
//				vanban.append("vanbandi", 0);
//			}
			
			docFilter.docCreator.userId=null;
			docFilter.docCategory=DocCategory.CVDEN.getKey();
			vanban.append("tatcavanbanden", docRepositoryCustom.countAll(docFilter));
			
			docFilter.docCategory=DocCategory.CVDI.getKey();
			vanban.append("tatcavanbandi", docRepositoryCustom.countAll(docFilter));
			/* End đếm văn bản */
			
			
			/*Đếm nhiệm vụ*/
			UserOrganization userTask=new UserOrganization();
			userTask.userId=userId;
			userTask.organizationId=organizationId;
			
			/* Nếu tài khoản có permission xemnhiemvudonvi thì cho coi toàn bộ */
			boolean xemnhiemvudonvi=organizationRoleService.hasRole(organizationId, userId, Permission.xemnhiemvudonvi.name());
			if(xemnhiemvudonvi) {
				userTask.userId=null;
			}
			
			/* Count đã giao */
			TaskFilter taskFillterDagiao=new TaskFilter();
			taskFillterDagiao.userTask=userTask;
			taskFillterDagiao.fromDate=fromDate;
			taskFillterDagiao.toDate=toDate;
			taskFillterDagiao.taskCategory=TaskCategory.DAGIAO;
			
			Document dagiao=new Document();
			dagiao.append("dahoanthanh", getDashboardDHT(taskFillterDagiao));
			dagiao.append("chuahoanthanh", getDashboardCHT(taskFillterDagiao));
			dagiao.append("nhiemvudonvi", getDashboardTaskOrganization(taskFillterDagiao));
			dagiao.append("chuathuchien", getDashboardCTH(taskFillterDagiao));
			
			/* Count được giao */
			TaskFilter taskFillterDuocgiao=new TaskFilter();
			taskFillterDuocgiao.userTask=userTask;
			taskFillterDuocgiao.fromDate=fromDate;
			taskFillterDuocgiao.toDate=toDate;
			taskFillterDuocgiao.taskCategory=TaskCategory.DUOCGIAO;
			
			Document duocgiao=new Document();
			duocgiao.append("dahoanthanh", getDashboardDHT(taskFillterDuocgiao));
			duocgiao.append("chuahoanthanh", getDashboardCHT(taskFillterDuocgiao));
			duocgiao.append("nhiemvudonvi", getDashboardTaskOrganization(taskFillterDuocgiao));
			duocgiao.append("chuathuchien", getDashboardCTH(taskFillterDuocgiao));
			
			/* Count theo dõi */
			TaskFilter taskFillterTheodoi=new TaskFilter();
			taskFillterTheodoi.userTask=userTask;
			taskFillterTheodoi.fromDate=fromDate;
			taskFillterTheodoi.toDate=toDate;
			taskFillterTheodoi.taskCategory=TaskCategory.THEODOI;
			
			Document theodoi=new Document();
			theodoi.append("dahoanthanh", getDashboardDHT(taskFillterTheodoi));
			theodoi.append("chuahoanthanh", getDashboardCHT(taskFillterTheodoi));
			theodoi.append("nhiemvudonvi", getDashboardTaskOrganization(taskFillterTheodoi));
			theodoi.append("chuathuchien", getDashboardCTH(taskFillterTheodoi));
			
			/* Count giao việc thay */
			TaskFilter taskFillterGiaoviecthay=new TaskFilter();
			taskFillterGiaoviecthay.userTask=userTask;
			taskFillterGiaoviecthay.fromDate=fromDate;
			taskFillterGiaoviecthay.toDate=toDate;
			taskFillterGiaoviecthay.taskCategory=TaskCategory.GIAOVIECTHAY;
			
			Document giaoviecthay=new Document();
			giaoviecthay.append("dahoanthanh", getDashboardDHT(taskFillterGiaoviecthay));
			giaoviecthay.append("chuahoanthanh", getDashboardCHT(taskFillterGiaoviecthay));
			giaoviecthay.append("nhiemvudonvi", getDashboardTaskOrganization(taskFillterGiaoviecthay));
			giaoviecthay.append("chuathuchien", getDashboardCTH(taskFillterGiaoviecthay));
			
			/* Count theo dõi thay */
			TaskFilter taskFillterTheodoithay=new TaskFilter();
			taskFillterTheodoithay.userTask=userTask;
			taskFillterTheodoithay.fromDate=fromDate;
			taskFillterTheodoithay.toDate=toDate;
			taskFillterTheodoithay.taskCategory=TaskCategory.THEODOITHAY;
			
			Document theodoithay=new Document();
			theodoithay.append("dahoanthanh", getDashboardDHT(taskFillterTheodoithay));
			theodoithay.append("chuahoanthanh", getDashboardCHT(taskFillterTheodoithay));
			theodoithay.append("nhiemvudonvi", getDashboardTaskOrganization(taskFillterTheodoithay));
			theodoithay.append("chuathuchien", getDashboardCTH(taskFillterTheodoithay));
			
			Document results=new Document();
			results.append("vanban", vanban);
			results.append("dagiao", dagiao);
			results.append("duocgiao", duocgiao);
			results.append("theodoi", theodoi);
			results.append("giaoviecthay", giaoviecthay);
			results.append("theodoithay", theodoithay);
			
			responseCMS.setStatus(HttpStatus.OK);
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
	
	@GetMapping("/feature/dashboard")
	public Object getDashboard(
			@RequestParam(name = "topUser", required = true, defaultValue = "10") int topUser,
			@RequestParam(name = "userId", required = true) String userId, 
			@RequestParam(name = "organizationId", required = true) String organizationId,
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate) {
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
			
			/* Đã giao */
			TaskFilter taskFilterDagiao=new TaskFilter();
			taskFilterDagiao.userTask=userTask;
			taskFilterDagiao.fromDate=fromDate;
			taskFilterDagiao.toDate=toDate;
			taskFilterDagiao.taskCategory=TaskCategory.DAGIAO;
			
			Document dagiao_dahoanthanh=new Document();
			dagiao_dahoanthanh.append("sum", getDashboardDHT(taskFilterDagiao));
			
			taskFilterDagiao.taskSubCategory=TaskSubCategory.DAHOANTHANH;
			List<UserTaskCount> topUserDagiao_DHT=taskRepositoryCustom.getAssigneeList(taskFilterDagiao);
			if(topUser>topUserDagiao_DHT.size() && topUserDagiao_DHT.size()>topUser) {
				dagiao_dahoanthanh.append("top", topUserDagiao_DHT.subList(0, topUser));
			}else {
				dagiao_dahoanthanh.append("top", topUserDagiao_DHT);
			}
			
			Document dagiao_chuahoanthanh=new Document();
			dagiao_chuahoanthanh.append("sum", getDashboardCHT(taskFilterDagiao));
			
			taskFilterDagiao.taskSubCategory=TaskSubCategory.CHUAHOANTHANH;
			List<UserTaskCount> topUserDagiao_CHT=taskRepositoryCustom.getAssigneeList(taskFilterDagiao);
			if(topUser>topUserDagiao_CHT.size() && topUserDagiao_CHT.size()>topUser) {
				dagiao_chuahoanthanh.append("top", topUserDagiao_CHT.subList(0, topUser));
			}else {
				dagiao_chuahoanthanh.append("top", topUserDagiao_CHT);
			}
			
			Document dagiao=new Document();
			dagiao.append("dahoanthanh", dagiao_dahoanthanh);
			dagiao.append("chuahoanthanh", dagiao_chuahoanthanh);
			
			/* Được giao */
			TaskFilter taskFilterDuocgiao=new TaskFilter();
			taskFilterDuocgiao.userTask=userTask;
			taskFilterDuocgiao.fromDate=fromDate;
			taskFilterDuocgiao.toDate=toDate;
			taskFilterDuocgiao.taskCategory=TaskCategory.DUOCGIAO;
			
			Document duocgiao_dahoanthanh=new Document();
			duocgiao_dahoanthanh.append("sum", getDashboardDHT(taskFilterDuocgiao));
			
			taskFilterDuocgiao.taskSubCategory=TaskSubCategory.DAHOANTHANH;
			List<UserTaskCount> topUserDuocgiao_DHT=taskRepositoryCustom.getOwnerList(taskFilterDuocgiao);
			if(topUser>topUserDuocgiao_DHT.size() && topUserDuocgiao_DHT.size()>topUser) {
				duocgiao_dahoanthanh.append("top", topUserDuocgiao_DHT.subList(0, topUser));
			}else {
				duocgiao_dahoanthanh.append("top", topUserDuocgiao_DHT);
			}
			duocgiao_dahoanthanh.append("top", topUserDuocgiao_DHT);
			
			Document duocgiao_chuahoanthanh=new Document();
			duocgiao_chuahoanthanh.append("sum", getDashboardCHT(taskFilterDuocgiao));
			
			taskFilterDuocgiao.taskSubCategory=TaskSubCategory.CHUAHOANTHANH;
			List<UserTaskCount> topUserDuocgiao_CHT=taskRepositoryCustom.getOwnerList(taskFilterDuocgiao);
			if(topUser>topUserDuocgiao_CHT.size() && topUserDuocgiao_CHT.size()>topUser) {
				duocgiao_chuahoanthanh.append("top", topUserDuocgiao_CHT.subList(0, topUser));
			}else {
				duocgiao_chuahoanthanh.append("top", topUserDuocgiao_CHT);
			}
			
			Document duocgiao=new Document();
			duocgiao.append("dahoanthanh", duocgiao_dahoanthanh);
			duocgiao.append("chuahoanthanh", duocgiao_chuahoanthanh);
			
			/* Theo dõi */
			TaskFilter taskFilterTheodoi=new TaskFilter();
			taskFilterTheodoi.userTask=userTask;
			taskFilterTheodoi.fromDate=fromDate;
			taskFilterTheodoi.toDate=toDate;
			taskFilterTheodoi.taskCategory=TaskCategory.THEODOI;
			
			Document theodoi_dahoanthanh=new Document();
			theodoi_dahoanthanh.append("sum", getDashboardDHT(taskFilterTheodoi));
			
			taskFilterTheodoi.taskSubCategory=TaskSubCategory.DAHOANTHANH;
			List<UserTaskCount> topUserTheoDoi_DHT=taskRepositoryCustom.getAssigneeList(taskFilterTheodoi);
			if(topUser>topUserTheoDoi_DHT.size() && topUserTheoDoi_DHT.size()>topUser) {
				theodoi_dahoanthanh.append("top", topUserTheoDoi_DHT.subList(0, topUser));
			}else {
				theodoi_dahoanthanh.append("top", topUserTheoDoi_DHT);
			}
			
			Document theodoi_chuahoanthanh=new Document();
			theodoi_chuahoanthanh.append("sum", getDashboardCHT(taskFilterTheodoi));
			
			taskFilterTheodoi.taskSubCategory=TaskSubCategory.CHUAHOANTHANH;
			List<UserTaskCount> topUserTheoDoi_CHT=taskRepositoryCustom.getAssigneeList(taskFilterTheodoi);
			if(topUser>topUserTheoDoi_CHT.size() && topUserTheoDoi_CHT.size()>topUser) {
				theodoi_chuahoanthanh.append("top", topUserTheoDoi_CHT.subList(0, topUser));
			}else {
				theodoi_chuahoanthanh.append("top", topUserTheoDoi_CHT);
			}
			
			Document theodoi=new Document();
			theodoi.append("dahoanthanh", theodoi_dahoanthanh);
			theodoi.append("chuahoanthanh", theodoi_chuahoanthanh);
			
			/*------------------------------*/
			Document results=new Document();
			results.append("dagiao", dagiao);
			results.append("duocgiao", duocgiao);
			results.append("theodoi", theodoi);
			
			responseCMS.setStatus(HttpStatus.OK);
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
	
	@GetMapping("/feature/notify/count")
	public Object getTaskNotifyCount(
			@RequestParam(name = "userId", required = true) String userId, 
			@RequestParam(name = "organizationId", required = true) String organizationId,
			@RequestParam(name = "ignoreLoginWeb", required = false) String ignoreLoginWeb,
			@RequestParam(name = "categorykey", required = false) String categorykey,
			@RequestParam(name = "year", required = false, defaultValue = "0") int year) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			List<String> includeActions=new ArrayList<String>();
			List<String> excludeActions=new ArrayList<String>();
			
			if(ignoreLoginWeb!=null && Boolean.parseBoolean(ignoreLoginWeb)) {
				excludeActions.add(NotifyCaption.LoginWeb.getAction());
			}
			
			NotifyFilter notifyFilter=new NotifyFilter();
			notifyFilter.receiver.userId=userId;
			notifyFilter.receiver.organizationId=organizationId;
			notifyFilter.excludeActions=excludeActions;
			notifyFilter.includeActions=includeActions;
			if(year==0) {
				int currentYear=DateTimeUtil.getYearAttmoment();
				notifyFilter.fromDate=DateTimeUtil.getDateStartOfYear(currentYear).getTime();
				notifyFilter.toDate=DateTimeUtil.getDateEndOfYear(currentYear).getTime();
			}else if(year<0) {
				notifyFilter.fromDate=0;
				notifyFilter.toDate=0;
			}else {
				int currentYear=year;
				notifyFilter.fromDate=DateTimeUtil.getDateStartOfYear(currentYear).getTime();
				notifyFilter.toDate=DateTimeUtil.getDateEndOfYear(currentYear).getTime();
			}
			
			if(categorykey!=null && !categorykey.isEmpty()) {
				TaskCategory taskCategory=TaskCategory.getTaskCategory(categorykey);
				if(taskCategory==null) {
					responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
					responseCMS.setResult("categorykey không được chấp nhận");
					return responseCMS.build();
				}
				notifyFilter.taskCategory=taskCategory;
			}
			
			int countAll=taskNotifyRepositoryCustom.countAll(notifyFilter);
			
			notifyFilter.viewed="false";
			int unview=taskNotifyRepositoryCustom.countAll(notifyFilter);
			
			Document result=new Document();
			result.append("total", countAll);
			result.append("unview", unview);
			result.append("viewed", countAll-unview);
			
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
	
	@GetMapping("/feature/notify/list")
	public Object getTaskNotifyList(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "userId", required = true) String userId, 
			@RequestParam(name = "organizationId", required = true) String organizationId,
			@RequestParam(name = "ignoreLoginWeb", required = false) String ignoreLoginWeb,
			@RequestParam(name = "categorykey", required = false) String categorykey,
			@RequestParam(name = "year", required = false, defaultValue = "0") int year) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			List<String> includeActions=new ArrayList<String>();
			List<String> excludeActions=new ArrayList<String>();
			
			if(ignoreLoginWeb!=null && Boolean.parseBoolean(ignoreLoginWeb)) {
				excludeActions.add(NotifyCaption.LoginWeb.getAction());
			}
			
			NotifyFilter notifyFilter=new NotifyFilter();
			notifyFilter.receiver.userId=userId;
			notifyFilter.receiver.organizationId=organizationId;
			notifyFilter.excludeActions=excludeActions;
			notifyFilter.includeActions=includeActions;
			if(year==0) {
				int currentYear=DateTimeUtil.getYearAttmoment();
				notifyFilter.fromDate=DateTimeUtil.getDateStartOfYear(currentYear).getTime();
				notifyFilter.toDate=DateTimeUtil.getDateEndOfYear(currentYear).getTime();
			}else if(year<0) {
				notifyFilter.fromDate=0;
				notifyFilter.toDate=0;
			}else {
				int currentYear=year;
				notifyFilter.fromDate=DateTimeUtil.getDateStartOfYear(currentYear).getTime();
				notifyFilter.toDate=DateTimeUtil.getDateEndOfYear(currentYear).getTime();
			}
			
			if(categorykey!=null && !categorykey.isEmpty()) {
				TaskCategory taskCategory=TaskCategory.getTaskCategory(categorykey);
				if(taskCategory==null) {
					responseCMS.setStatus(HttpStatus.BAD_GATEWAY);
					responseCMS.setResult("categorykey không được chấp nhận");
					return responseCMS.build();
				}
				notifyFilter.taskCategory=taskCategory;
			}
			
			int countAll=taskNotifyRepositoryCustom.countAll(notifyFilter);
			List<Notify> taskNotifys=taskNotifyRepositoryCustom.findAll(notifyFilter, skip, limit);
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setTotal(countAll);
			responseCMS.setResult(taskNotifys);
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
	
	@PutMapping("/feature/notify/viewed/{notifyId}")
	public Object setTaskNotifyList(@PathVariable(name = "notifyId", required = true) String notifyId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			Notify taskNotify=null;
			try {
				taskNotify=taskNotifyRepository.findById(new ObjectId(notifyId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("notifyId không tồn tại trong hệ thống");
				return responseCMS.build();
			}
			
			taskNotify.viewed=true;
			taskNotify=taskNotifyRepository.save(taskNotify);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(taskNotify);
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	@PutMapping("/feature/notify/mark-all")
	public Object setMarkAllNotifyList(
			@RequestParam(name = "userId", required = true) String userId, 
			@RequestParam(name = "organizationId", required = true) String organizationId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			NotifyFilter notifyFilter=new NotifyFilter();
			notifyFilter.receiver.userId=userId;
			notifyFilter.receiver.organizationId=organizationId;
			
			taskNotifyRepositoryCustom.setMarkAll(notifyFilter);
			responseCMS.setStatus(HttpStatus.OK);
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	private Document getDashboardCHT(TaskFilter taskFilter) {
		Document result=new Document();
		taskFilter.taskSubCategory=TaskSubCategory.CHUAHOANTHANH_TRONGHAN;
		int tronghan=taskRepositoryCustom.countAll(taskFilter);
		result.append("tronghan", tronghan);
		
		taskFilter.taskSubCategory=TaskSubCategory.CHUAHOANTHANH_KHONGHAN;
		int khonghan=taskRepositoryCustom.countAll(taskFilter);
		result.append("khonghan", khonghan);
		
		taskFilter.taskSubCategory=TaskSubCategory.CHUAHOANTHANH_QUAHAN;
		int quahan=taskRepositoryCustom.countAll(taskFilter);
		result.append("quahan", quahan);
		
		return result;
	}
	
	
	private Document getDashboardDHT(TaskFilter taskFilter) {
		Document result=new Document();
		taskFilter.taskSubCategory=TaskSubCategory.DAHOANTHANH_TRONGHAN;
		int tronghan=taskRepositoryCustom.countAll(taskFilter);
		result.append("tronghan", tronghan);
		
		taskFilter.taskSubCategory=TaskSubCategory.DAHOANTHANH_KHONGHAN;
		int khonghan=taskRepositoryCustom.countAll(taskFilter);
		result.append("khonghan", khonghan);
		
		taskFilter.taskSubCategory=TaskSubCategory.DAHOANTHANH_QUAHAN;
		int quahan=taskRepositoryCustom.countAll(taskFilter);
		result.append("quahan", quahan);
		
		return result;
	}
	
	private int getDashboardCTH(TaskFilter taskFilter) {
		taskFilter.taskAssignmentType=null;
		taskFilter.taskAssignmentStatus=null;
		taskFilter.taskSubCategory=TaskSubCategory.CHUATHUCHIEN;
		
		int cth=taskRepositoryCustom.countAll(taskFilter);
		return cth;
	}
	
	private Document getDashboardTaskOrganization(TaskFilter taskFilter) {
		taskFilter.taskSubCategory=null;
		taskFilter.taskAssignmentType=TaskAssignmentType.Organization;
		
		Document result=new Document();
		taskFilter.taskAssignmentStatus=TaskAssignmentStatus.CHUAPHAN_CANBO;
		result.append("chuaphancanbo", taskRepositoryCustom.countAll(taskFilter));
		
		taskFilter.taskAssignmentStatus=TaskAssignmentStatus.DAPHAN_CANBO;
		result.append("daphancanbo", taskRepositoryCustom.countAll(taskFilter));
		
		return result;
	}
	
	protected Document convertTask(Task task) {
		return convertTask(task, false);
	}
	
	protected Document convertTask(Task task, boolean detail) {
		Document document=new Document();
		document.append("createdTime", task.getCreatedTime());
		document.append("updatedTime", task.getUpdatedTime());
		document.append("id", task.getId());
		document.append("owner", task.ownerTask);
		document.append("assignee", task.assigneeTask);
		document.append("followersTask", task.followersTask);
		document.append("title", task.title);
		document.append("description", task.description);
		document.append("endTime", task.getEndTime());
		document.append("completedTime", task.getCompletedTime());
		document.append("parentId", task.parentId);
		document.append("comments", task.comments);
		document.append("events", task.events);
		document.append("processes", task.processes);
		
		if(detail) {
			document.append("attachments", task.attachments);
		}
		return document;
	}
	
}
