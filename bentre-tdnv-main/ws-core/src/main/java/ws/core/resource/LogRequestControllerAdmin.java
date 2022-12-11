package ws.core.resource;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import ws.core.enums.LogMessages;
import ws.core.model.LogRequest;
import ws.core.model.filter.LogRequestFilter;
import ws.core.repository.LogRequestRepository;
import ws.core.repository.LogRequestRepositoryCustom;
import ws.core.util.ResponseCMS;

@RestController
@RequestMapping("/admin")
@Tag(name = "Nhật ký - Người dùng")
public class LogRequestControllerAdmin {

	private Logger log = LogManager.getLogger(TaskControllerWebsite.class);

	@Autowired
	protected LogRequestRepository logRequestRepository;

	@Autowired
	protected LogRequestRepositoryCustom logRequestRepositoryCustom;
	
	@GetMapping("/log-request/list")
	@Operation(summary = "Danh sách", description = "Danh sách nhật ký")
	public Object findAll(
			@RequestParam(name = "skip", required = true) @Parameter(example = "0", required = true, description = "Vị trí lấy") int skip, 
			@RequestParam(name = "limit", required = true) @Parameter(example = "10", required = true, description = "Số lượng lấy") int limit, 
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") @Parameter(example = "0", required = true, description = "Từ ngày") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") @Parameter(example = "0", required = true, description = "Đến ngày") long toDate, 
			@RequestParam(name = "keyword", required = false) @Parameter(required = false, description = "Từ khóa") String keyword) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			LogRequestFilter logRequestFilter=new LogRequestFilter();
			logRequestFilter.fromDate=fromDate;
			logRequestFilter.toDate=toDate;
			logRequestFilter.keySearch=keyword;
			logRequestFilter.action=LogRequest.Action.Login.getKey();
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
	@Operation(summary = "Chi tiết", description = "Chi tiết nhật ký")
	public Object getDetail(@PathVariable(name = "logRequestId", required = true) String logRequestId) {
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
