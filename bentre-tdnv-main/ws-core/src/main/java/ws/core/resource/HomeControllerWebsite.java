package ws.core.resource;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ws.core.enums.TaskAssignmentType;
import ws.core.model.Task;
import ws.core.model.User;
import ws.core.model.request.ReqCheckValidSample;
import ws.core.mysql.DatabaseUtil;
import ws.core.mysql.SyncOrganizationService;
import ws.core.mysql.SyncRoleService;
import ws.core.mysql.SyncUserService;
import ws.core.repository.LogRequestRepository;
import ws.core.repository.TaskRepository;
import ws.core.repository.UserRepository;
import ws.core.util.ResponseCMS;

@RestController
public class HomeControllerWebsite {

	@Autowired
	protected LogRequestRepository logRequestRepository;
	
	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	private SyncOrganizationService syncOrganizationService;
	
	@Autowired
	private SyncUserService syncUserService;
	
	@Autowired
	private SyncRoleService syncRoleService;
	
	@Autowired
	private TaskRepository taskRepository;
	
	@GetMapping("/")
	public Object home() {
		return "Welcome to Core API";
	}
	
	@GetMapping("/check-api")
	public Object checkapi() {
		try {
			logRequestRepository.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			return "false";
		}
		return "true";
	}
	
	@GetMapping("/sync-mysql/{process}")
	public Object sync(@PathVariable(name = "process", required = true) String process) {
		if(process.equals("get-list-user-mysql")) {
			DatabaseUtil.getListUsers(null);
			return "Done process ["+process+"]";
		}else if(process.equals("get-list-organization-mysql")) {
			DatabaseUtil.getListOrganizations(null);
			return "Done process ["+process+"]";
		}else if(process.equals("get-list-suborganization-mysql")) {
			DatabaseUtil.getListSubOrganizations(20705);
			return "Done process ["+process+"]";
		}else if(process.equals("get-list-user-mongodb")) {
			List<User> users=userRepository.findAll();
			for (User user : users) {
				System.out.println("+ Fullname: "+user.fullName);
				System.out.println();
			}
			return "Done process ["+process+"]";
		}else if(process.equals("sync-organization")) {
			try {
				syncOrganizationService.syncOrganization();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(process.equals("sync-user")) {
			try {
				syncUserService.syncUser();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(process.equals("sync-role")) {
			try {
				syncRoleService.syncRole();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
//		else if(process.equals("sync-user-org-expand")) {
//			try {
//				syncUserOrganizationExpand();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		
		
		/* DONG BO MYSQL -> MONGODB
		 * 1. Chay sync org 
		 * 2. Chay sync user (bo user vao org) 
		 * 3. Chay role
		 */
		return "Welcome to Core API";
	}
	
	@GetMapping("/upgrade-data/{process}")
	public Object upgradeData(@PathVariable(name = "process", required = true) String process) {
		if(process.equals("39cf88d38eccc51600813ae238456b0c0eccc51600813ae238456b02841eccc51600813ae238456b0")) {
			List<Task> tasks=taskRepository.findAll();
			for (Task task : tasks) {
				if(StringUtils.isEmpty(task.assignmentType)) {
					task.assignmentType=TaskAssignmentType.User.getKey();
					taskRepository.save(task);
				}
			}
			return "Done upgrade task";
		}
		return "Don't any process";
	}
	
	@PostMapping("/checkValidSubClass")
	public Object checkValidSubClass(@RequestBody @Valid ReqCheckValidSample reqCheckValidSample){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		responseCMS.setStatus(HttpStatus.OK);
		return responseCMS.build();
	}
	
//	private void syncUserOrganizationExpand() {
//		List<User> users=userRepository.findAll();
//		for (User user : users) {
//			LinkedList<UserOrganizationExpand> organizations=new LinkedList<UserOrganizationExpand>();
//			for(String orgId : user.getOrganizationIds()) {
//				try {
//					Organization _org=organizationRepository.findById(new ObjectId(orgId)).get();
//					
//					UserOrganizationExpand userOrganizationExpand=new UserOrganizationExpand();
//					userOrganizationExpand.setOrganzationId(_org.getId());
//					userOrganizationExpand.setOrganzationName(_org.getName());
//					userOrganizationExpand.setNumberOrder(1);
//					userOrganizationExpand.setJobTitle(user.getJobTitle());
//					userOrganizationExpand.setAccountIOffice(user.getAccountDomino());
//					
//					organizations.add(userOrganizationExpand);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//			user.setOrganizations(organizations);
//			userRepository.save(user);
//		}
//	}
}
