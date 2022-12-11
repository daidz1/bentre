package ws.core.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ws.core.enums.LogMessages;
import ws.core.model.LogRequest;
import ws.core.model.filter.LogRequestFilter;
import ws.core.repository.LogRequestRepository;
import ws.core.repository.LogRequestRepositoryCustom;
import ws.core.security.CustomUserDetails;
import ws.core.util.ResponseCMS;

@RestController
@RequestMapping("/website")
public class LogRequestControllerWebsite {

	private Logger log = LogManager.getLogger(TaskControllerWebsite.class);

	@Autowired
	protected LogRequestRepository logRequestRepository;

	@Autowired
	protected LogRequestRepositoryCustom logRequestRepositoryCustom;
	
	@GetMapping("/log-request/count")
	public Object countAll(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "action", required = false) String action,
			@RequestParam(name = "keyword", required = false) String keyword) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			LogRequestFilter logRequestFilter=new LogRequestFilter();
			logRequestFilter.fromDate=fromDate;
			logRequestFilter.toDate=toDate;
			logRequestFilter.userIdRequest=userRequest.getUser().getId();
			logRequestFilter.keySearch=keyword;
			logRequestFilter.action=action;
			logRequestFilter.access=LogRequest.Access.Website.getKey();
			
			long total=logRequestRepositoryCustom.countAll(logRequestFilter);
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
	
	@GetMapping("/log-request/list")
	public Object findAll(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "action", required = false) String action,
			@RequestParam(name = "keyword", required = false) String keyword) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			LogRequestFilter logRequestFilter=new LogRequestFilter();
			logRequestFilter.fromDate=fromDate;
			logRequestFilter.toDate=toDate;
			logRequestFilter.userIdRequest=userRequest.getUser().getId();
			logRequestFilter.keySearch=keyword;
			logRequestFilter.action=action;
			logRequestFilter.access=LogRequest.Access.Website.getKey();
			
			long total=logRequestRepositoryCustom.countAll(logRequestFilter);
			List<LogRequest> logRequestList=logRequestRepositoryCustom.findAll(logRequestFilter, skip, limit);
			List<Document> result=new ArrayList<Document>();
			for (LogRequest logRequest : logRequestList) {
				result.add(convert(logRequest));
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setTotal(total);
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
	
	@GetMapping("/log-request/get/{logRequestId}")
	public Object getDoc(@PathVariable(name = "logRequestId", required = true) String logRequestId) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			LogRequest logRequest=null;
			try {
				logRequest=logRequestRepository.findById(new ObjectId(logRequestId)).get();
			} catch (Exception e) {
				log.debug(e.getMessage());
				responseCMS.setStatus(HttpStatus.NOT_FOUND);
				responseCMS.setResult("logRequestId không tồn tại trong hệ thống");
				return responseCMS.build();
			}
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convert(logRequest));
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setError(e.getMessage());
			return responseCMS.build();
		}
	}
	
	/*---------------------------------- convert --------------------------------*/
	protected Document convert(LogRequest logRequest) {
		Document document=new Document();
		document.append("createdTime", logRequest.getCreatedTime());
		document.append("id", logRequest.getId());
		document.append("access", logRequest.access);
		document.append("action", logRequest.action);
		document.append("addremote", logRequest.addremote);
		document.append("method", logRequest.method);
		document.append("protocol", logRequest.protocol);
		document.append("requestURL", logRequest.requestURL);
		document.append("requestQuery", logRequest.requestQuery);
		document.append("clientRequest", logRequest.clientRequest);
		document.append("userRequest", logRequest.userRequest);
		return document;
	}
}
