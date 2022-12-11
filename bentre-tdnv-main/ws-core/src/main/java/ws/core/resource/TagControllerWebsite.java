package ws.core.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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
import ws.core.model.Tag;
import ws.core.model.UserOrganization;
import ws.core.model.filter.SkipLimitFilter;
import ws.core.model.filter.TagFilter;
import ws.core.model.request.ReqTagCreate;
import ws.core.model.request.ReqTagEdit;
import ws.core.repository.OrganizationRepository;
import ws.core.repository.TagRepository;
import ws.core.repository.TagRepositoryCustom;
import ws.core.repository.TaskRepository;
import ws.core.repository.TaskRepositoryCustom;
import ws.core.repository.UserRepository;
import ws.core.repository.UserRepositoryCustom;
import ws.core.repository.imp.OrganizationRepositoryCustomImp;
import ws.core.util.ResponseCMS;

@RestController
@RequestMapping("/website")
public class TagControllerWebsite {

	private Logger log = LogManager.getLogger(TaskControllerWebsite.class);

	@Autowired
	protected TagRepository tagRepository;

	@Autowired
	protected TagRepositoryCustom tagRepositoryCustom;
	
	@Autowired
	protected TaskRepository taskRepository;

	@Autowired
	protected TaskRepositoryCustom taskRepositoryCustom;

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected UserRepositoryCustom userRepositoryCustom;

	@Autowired
	protected OrganizationRepository organizationRepository;

	@Autowired
	protected OrganizationRepositoryCustomImp organizationRepositoryCustom;

	@GetMapping("/tag/count")
	public Object getCount(
			@RequestParam(name = "userId", required = true) String userId, 
			@RequestParam(name = "organizationId", required = true) String organizationId,
			@RequestParam(name = "fromDate", required = false, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = false, defaultValue = "0") long toDate, 
			@RequestParam(name = "keyword", required = false) String keyword) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			UserOrganization creator=new UserOrganization();
			creator.userId=userId;
			creator.organizationId=organizationId;
			
			TagFilter tagFilter=new TagFilter();
			tagFilter.fromDate=fromDate;
			tagFilter.toDate=toDate;
			tagFilter.keySearch=keyword;
			tagFilter.creator=creator;
			
			long total=tagRepositoryCustom.countAll(tagFilter);
			
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
	
	@GetMapping("/tag/list")
	public Object getList(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "userId", required = true) String userId, 
			@RequestParam(name = "organizationId", required = true) String organizationId,
			@RequestParam(name = "fromDate", required = false, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = false, defaultValue = "0") long toDate, 
			@RequestParam(name = "keyword", required = false) String keyword) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			UserOrganization creator=new UserOrganization();
			creator.userId=userId;
			creator.organizationId=organizationId;
			
			TagFilter tagFilter=new TagFilter();
			tagFilter.fromDate=fromDate;
			tagFilter.toDate=toDate;
			tagFilter.keySearch=keyword;
			tagFilter.creator=creator;
			tagFilter.skipLimitFilter=new SkipLimitFilter(skip, limit);
			
			long total=tagRepositoryCustom.countAll(tagFilter);
			List<Tag> tags=tagRepositoryCustom.findAll(tagFilter);
			List<Document> results=new ArrayList<Document>();
			for (Tag item : tags) {
				results.add(convertTag(item));
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
	
	@GetMapping("/tag/get/{tagId}")
	public Object getTag(@PathVariable(name = "tagId", required = true) String tagId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			Tag tag=null;
			try {
				tag=tagRepository.findById(new ObjectId(tagId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("tagId ["+tagId+"] không tồn tại trong hệ thống");
				return responseCMS.build();
			}
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertTag(tag));
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	@PostMapping("/tag/create")
	public Object tagCreate(@RequestBody @Valid ReqTagCreate reqTagCreate){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			Tag tagCreate=new Tag();
			tagCreate.createdTime=new Date();
			tagCreate.updatedTime=new Date();
			tagCreate.name=reqTagCreate.name;
			tagCreate.color=reqTagCreate.color;
			tagCreate.creator=reqTagCreate.creator;
			
			try {
				tagCreate=tagRepository.save(tagCreate);
			} catch (DuplicateKeyException e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage(e.getMessage());
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			responseCMS.setStatus(HttpStatus.CREATED);
			responseCMS.setResult(convertTag(tagCreate));
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
	
	@PutMapping("/tag/edit/{tagId}")
	public Object tagEdit(
			@PathVariable(name = "tagId", required = true) String tagId,
			@RequestBody @Valid ReqTagEdit reqTagEdit){
		ResponseCMS responseCMS=new ResponseCMS();
		try {
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
			
			tagUpdate.updatedTime=new Date();
			tagUpdate.name=reqTagEdit.name;
			tagUpdate.color=reqTagEdit.color;
			
			try {
				tagUpdate=tagRepository.save(tagUpdate);
			} catch (DuplicateKeyException e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.CONFLICT);
				responseCMS.setMessage(e.getMessage());
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertTag(tagUpdate));
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
	
	@DeleteMapping("/tag/delete/{tagId}")
	public Object deleteTask(@PathVariable(name = "tagId", required = true) String tagId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			Tag tagDelete=null;
			try {
				tagDelete=tagRepository.findById(new ObjectId(tagId)).get();
			} catch (Exception e) {
				e.printStackTrace();
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setMessage("tagId ["+tagId+"] không tồn tại trong hệ thống");
				responseCMS.setError(e.getMessage());
				return responseCMS.build();
			}

			/* Xoá task */
			tagRepository.delete(tagDelete);

			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("Xóa thành công");
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
	
	protected Document convertTag(Tag tag) {
		Document document=new Document();
		document.append("createdTime", tag.getCreatedTime());
		document.append("updatedTime", tag.getUpdatedTime());
		document.append("id", tag.getId());
		document.append("name", tag.name);
		document.append("creator", tag.creator);
		document.append("taskIds", tag.taskIds);
		return document;
	}
}
