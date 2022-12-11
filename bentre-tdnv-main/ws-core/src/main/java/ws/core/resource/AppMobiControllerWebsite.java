package ws.core.resource;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ws.core.enums.LogMessages;
import ws.core.model.AppMobi;
import ws.core.model.request.ReqAppMobi;
import ws.core.repository.AppMobiRepository;
import ws.core.repository.AppMobiRepositoryCustom;
import ws.core.util.ResponseCMS;

@RestController
@RequestMapping("/website")
public class AppMobiControllerWebsite {

	private Logger log = LogManager.getLogger(AppMobiControllerWebsite.class);

	@Autowired
	protected AppMobiRepository appMobiRepository;

	@Autowired
	protected AppMobiRepositoryCustom appMobiRepositoryCustom;
	
	@PostMapping("/app-user-device/is-active")
	public Object isActive(@RequestBody @Valid ReqAppMobi reqAppMobi) {
		ResponseCMS responseCMS=new ResponseCMS();
		try {
			boolean actived=false;
			
			/* Kiểm tra có tồn tại không */
			AppMobi appMobi=appMobiRepositoryCustom.get(reqAppMobi.userId, reqAppMobi.deviceId);
			if(appMobi!=null) {
				actived=appMobi.active;
				responseCMS.setStatus(HttpStatus.OK);
				responseCMS.setResult(convertAppMobi(actived));
				return responseCMS.build();
			}
			
			appMobi=new AppMobi();
			appMobi.userId=reqAppMobi.userId;
			appMobi.deviceId=reqAppMobi.deviceId;
			appMobi.deviceName=reqAppMobi.deviceName;
			appMobi.username=reqAppMobi.username;
			appMobi.fullName=reqAppMobi.fullName;
			appMobi.longitute=reqAppMobi.longitute;
			appMobi.lagitute=reqAppMobi.lagitute;
			
			/* Enable luôn sau khi đăng nhập (27-02-2022) */
			appMobi.active=true;
			appMobiRepository.save(appMobi);
			
			responseCMS.setStatus(HttpStatus.OK);
			responseCMS.setResult(convertAppMobi(actived));
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
	
	/*---------------------------------- convert --------------------------------*/
	protected Document convertAppMobi(boolean actived) {
		Document document=new Document();
		document.append("active", actived);
		return document;
	}
}
