package ws.core.resource;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ws.core.enums.LogMessages;
import ws.core.model.Active;
import ws.core.model.User;
import ws.core.repository.UserRepository;
import ws.core.service.ActiveService;
import ws.core.service.FirebaseService;
import ws.core.util.DateTimeUtil;
import ws.core.util.ResponseCMS;

@RestController
@RequestMapping("/api")
public class ActiveController {
	private Logger log = LogManager.getLogger(ActiveController.class);
	
	@Autowired
	protected ActiveService activeService;
	
	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	protected FirebaseService firebaseService;
		
	
	@GetMapping("/active/check")
	public Object checkActiveCode(
			@RequestParam(name = "username", required = true) String username, 
			@RequestParam(name = "activeCode", required = true) String activeCode) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			User user=null;
			try {
				user=userRepository.findByUsername(username).get();
			} catch (Exception e) {
				responseCMS.setStatus(HttpStatus.UNAUTHORIZED);
				responseCMS.setMessage("Không xác thực được");
				return responseCMS.build();
			}
			
			/* 01-03-2022 tạm bỏ check activeCode */
			if(user!=null) {
				/* if(user!=null && user.activeCode.equals(activeCode)) { */
				Active active=activeService.getActive();
				responseCMS.setStatus(HttpStatus.OK);
				responseCMS.setResult(convert(active));
				return responseCMS.build();
			}
			
			responseCMS.setStatus(HttpStatus.UNAUTHORIZED);
			responseCMS.setMessage("Không xác thực được");
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setDebug(e.getMessage());
			return responseCMS.build();
		}
	}
	
	@GetMapping("/active/reset/{username}")
	public Object resetActiveCode(@PathVariable(name = "username", required = true) String username) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			User user=null;
			try {
				user=userRepository.findByUsername(username).get();
			} catch (Exception e) {
				responseCMS.setStatus(HttpStatus.UNAUTHORIZED);
				responseCMS.setMessage("Username không tồn tại");
				return responseCMS.build();
			}
			
			user.activeCode=RandomStringUtils.randomAlphanumeric(8).toUpperCase();
			userRepository.save(user);
			
			/* Thông báo firebase */
			notificationFirebase(user);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setMessage("Reset thành công");
			return responseCMS.build();
		} catch (Exception e) {
			log.debug(e.getMessage());
			responseCMS.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			responseCMS.setMessage(LogMessages.INTERNAL_SERVER_ERROR.getMessage());
			responseCMS.setDebug(e.getMessage());
			return responseCMS.build();
		}
	}
	
	private void notificationFirebase(User user) {
		try {
			/* Thông báo trên firebase */
			String topic = "giaoviecvptw_";
			String title = "Tài khoản của bạn vừa được kích hoạt và đăng nhập trên thiết bị mới";
			String content = "Lúc "+DateTimeUtil.getDatetimeFormat().format(new Date());
			
			Map<String,String> data = new HashMap<String,String>();
			try {
				topic = "giaoviecvptw_"+user.getId();
				firebaseService.sendToTopic(topic, title, content, data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected Document convert(Active active) {
		Document document=new Document();
		document.append("apiurl", active.apiurl);
		try {
			document.append("clientcert", activeService.getFileBytesCert(active.clientcert.toString()));
		} catch (Exception e) {
			document.append("clientcert", null);
		}
		
		document.append("clientpass", active.clientpass);
		return document;
	}
	
	
	
}
