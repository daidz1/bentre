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
import ws.core.model.GroupUserTask;
import ws.core.model.UserOrganization;
import ws.core.model.filter.GroupUserTaskFilter;
import ws.core.model.request.ReqGroupUserTask;
import ws.core.repository.GroupUserTaskRepository;
import ws.core.repository.GroupUserTaskRepositoryCustom;
import ws.core.util.ResponseCMS;

@RestController
@RequestMapping("/website")
public class GroupUserTaskControllerWebsite {
	private Logger log = LogManager.getLogger(GroupUserTaskControllerWebsite.class);
	
	@Autowired
	protected GroupUserTaskRepository groupUserTaskRepository;
	
	@Autowired
	protected GroupUserTaskRepositoryCustom groupUserTaskRepositoryCustom;
	
	@GetMapping("/group-usertask/list")
	public Object list(
			@RequestParam(name = "userId", required = true) String userId, 
			@RequestParam(name = "organizationId", required = true) String organizationId,
			@RequestParam(name = "skip", required = true) int skip,
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
			@RequestParam(name = "findAssignees", required = false, defaultValue = "") String findAssignees,
			@RequestParam(name = "findFollowers", required = false, defaultValue = "") String findFollowers,
			@RequestParam(name = "assignmentType", required = false) String assignmentType) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			GroupUserTaskFilter groupUserTaskFilter=new GroupUserTaskFilter();
			groupUserTaskFilter.creator=new UserOrganization();
			groupUserTaskFilter.creator.userId=userId;
			groupUserTaskFilter.creator.organizationId=organizationId;
			groupUserTaskFilter.keySearch=keyword;
			groupUserTaskFilter.findAssignees=findAssignees;
			groupUserTaskFilter.findFollowers=findFollowers;
			groupUserTaskFilter.assignmentType=assignmentType;
			
			long total=groupUserTaskRepositoryCustom.countAll(groupUserTaskFilter);
			List<GroupUserTask> groupUserTasks=groupUserTaskRepositoryCustom.findAll(groupUserTaskFilter, skip, limit);
			List<Document> results=new ArrayList<Document>();
			for (GroupUserTask item : groupUserTasks) {
				results.add(convertGroupUserTask(item));
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
	
	@GetMapping("/group-usertask/get/{groupUserTaskId}")
	public Object get(@PathVariable(name = "groupUserTaskId", required = true) String groupUserTaskId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			GroupUserTask groupUserTask=null;
			try {
				groupUserTask=groupUserTaskRepository.findById(new ObjectId(groupUserTaskId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("groupUserTaskId ["+groupUserTaskId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertGroupUserTask(groupUserTask));
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	@PostMapping("/group-usertask/create")
	public Object create(@RequestBody @Valid ReqGroupUserTask reqGroupUserTask){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			GroupUserTask groupUserTask=new GroupUserTask();
			/* Lưu data */
			try {
				groupUserTask.name=reqGroupUserTask.name;
				groupUserTask.description=reqGroupUserTask.description;
				groupUserTask.creator=reqGroupUserTask.creator;
				groupUserTask.assigneeTask=reqGroupUserTask.assigneeTask;
				groupUserTask.followersTask=reqGroupUserTask.followersTask;
				groupUserTask.sortBy=reqGroupUserTask.sortBy;
				groupUserTask.assignmentType=reqGroupUserTask.assignmentType;
				groupUserTask=groupUserTaskRepository.save(groupUserTask);
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage("Dữ liệu bị xung đột");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			responseCMS.setStatus(HttpStatus.CREATED);
			responseCMS.setResult(convertGroupUserTask(groupUserTask));
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
	
	@PutMapping("/group-usertask/edit/{groupUserTaskId}")
	public Object edit(
			@PathVariable(name = "groupUserTaskId", required = true) String groupUserTaskId, 
			@RequestBody @Valid ReqGroupUserTask reqGroupUserTask){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra userId */
			GroupUserTask groupUserTask=null;
			try {
				groupUserTask=groupUserTaskRepository.findById(new ObjectId(groupUserTaskId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("groupUserTaskId ["+groupUserTaskId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Cập nhật data */
			try {
				groupUserTask.name=reqGroupUserTask.name;
				groupUserTask.description=reqGroupUserTask.description;
				groupUserTask.creator=reqGroupUserTask.creator;
				groupUserTask.assigneeTask=reqGroupUserTask.assigneeTask;
				groupUserTask.followersTask=reqGroupUserTask.followersTask;
				groupUserTask.sortBy=reqGroupUserTask.sortBy;
				groupUserTask.assignmentType=reqGroupUserTask.assignmentType;
				groupUserTask=groupUserTaskRepository.save(groupUserTask);
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage("Dữ liệu cập nhật bị lỗi, hoặc xung đột dữ liệu");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertGroupUserTask(groupUserTask));
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
	
	@DeleteMapping("/group-usertask/delete/{groupUserTaskId}")
	public Object delete(@PathVariable(name = "groupUserTaskId", required = true) String groupUserTaskId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			/* Kiểm tra articleId */
			GroupUserTask groupUserTaskDelete=null;
			try {
				groupUserTaskDelete=groupUserTaskRepository.findById(new ObjectId(groupUserTaskId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("groupUserTaskId ["+groupUserTaskId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}
			
			/* Xoá article */
			groupUserTaskRepository.delete(groupUserTaskDelete);
			
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
	
	protected Document convertGroupUserTask(GroupUserTask groupUserTask) {
		Document document=new Document();
		document.append("createdTime", groupUserTask.getCreatedTime());
		document.append("updatedTime", groupUserTask.getUpdatedTime());
		document.append("id", groupUserTask.getId());
		document.append("name", groupUserTask.name);
		document.append("description", groupUserTask.description);
		document.append("creator", groupUserTask.creator);
		document.append("assigneeTask", groupUserTask.assigneeTask);
		document.append("followersTask", groupUserTask.followersTask);
		document.append("sortBy", groupUserTask.sortBy);
		document.append("assignmentType", groupUserTask.assignmentType);
		return document;
	}
}
