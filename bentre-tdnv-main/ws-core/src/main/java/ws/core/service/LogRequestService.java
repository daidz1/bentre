package ws.core.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import ws.core.model.LogRequest;
import ws.core.model.LogRequestClientRequest;
import ws.core.model.LogRequestUserRequest;
import ws.core.model.User;
import ws.core.model.embeded.UserOrganizationExpand;
import ws.core.repository.LogRequestRepository;
import ws.core.security.JwtTokenProvider;

@Service
public class LogRequestService {

	@Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;
    
    @Autowired
    private LogRequestRepository logRequestRepository;
    
	public void writeLogRequest(HttpServletRequest request, String jwt) {
    	Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				try {
		    		LogRequest logRequest=new LogRequest();
		    		
		    		LogRequestUserRequest logRequestUserRequest=new LogRequestUserRequest();
		    		logRequestUserRequest.token=jwt;
		    		
		    		try {
	    				String username = tokenProvider.getUsernameFromJWT(jwt);
		    			User user=userService.getUserByUsername(username);
		    			logRequestUserRequest.userId=user.getId();
		    			logRequestUserRequest.username=user.username;
		    			logRequestUserRequest.fullName=user.fullName;
		    			logRequestUserRequest.email=user.email;

		    			List<String> organizationIds=new ArrayList<String>();
		    			for(UserOrganizationExpand item:user.getOrganizations()) {
		    				organizationIds.add(item.getOrganizationId());
		    			}
		    			
		    			logRequestUserRequest.organizationIds=String.join(",", organizationIds);
		    			logRequestUserRequest.accountDomino=user.accountDomino;
					} catch (Exception e) {
						e.printStackTrace();
					}
		    		logRequest.userRequest=logRequestUserRequest;
		    		
		    		try {
						LogRequestClientRequest logRequestUserInfo=new Gson().fromJson(request.getHeader("UserInfo"), LogRequestClientRequest.class);
						logRequest.clientRequest=logRequestUserInfo;
					} catch (Exception e) {}
		    		
		    		logRequest.method=request.getMethod().toString();
		    		logRequest.requestURL=request.getRequestURL().toString();
		        	logRequest.addremote=new URI(request.getRequestURL().toString()).getHost();
		        	logRequest.protocol=request.getProtocol().toString();
		        	logRequest.requestQuery=request.getQueryString();
		        	logRequest.action=LogRequest.Action.Request.getKey();
		        	
					/* Nếu là admin/website */
		        	if(request.getRequestURL().toString().contains("/admin")) {
		        		logRequest.access=LogRequest.Access.Admin.getKey();
		        	}else {
		        		logRequest.access=LogRequest.Access.Website.getKey();
		        	}
		        	
		        	logRequestRepository.save(logRequest);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    	thread.start();
    }
	
	public void writeLogLogin(HttpServletRequest request, User user) {
    	Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				try {
		    		LogRequest logRequest=new LogRequest();
		    		
		    		LogRequestUserRequest logRequestUserRequest=new LogRequestUserRequest();
		    		logRequestUserRequest.token=null;
		    		logRequestUserRequest.userId=user.getId();
	    			logRequestUserRequest.username=user.username;
	    			logRequestUserRequest.fullName=user.fullName;
	    			logRequestUserRequest.email=user.email;
	    			
	    			List<String> organizationIds=new ArrayList<String>();
	    			for(UserOrganizationExpand item:user.getOrganizations()) {
	    				organizationIds.add(item.getOrganizationId());
	    			}
	    			
	    			logRequestUserRequest.organizationIds=String.join(",", organizationIds);
	    			logRequestUserRequest.accountDomino=user.accountDomino;
		    		
		    		logRequest.userRequest=logRequestUserRequest;
		    		
		    		try {
						LogRequestClientRequest logRequestUserInfo=new Gson().fromJson(request.getHeader("UserInfo"), LogRequestClientRequest.class);
						logRequest.clientRequest=logRequestUserInfo;
					} catch (Exception e) {}
		    		
		    		logRequest.method=request.getMethod().toString();
		    		logRequest.requestURL=request.getRequestURL().toString();
		        	logRequest.addremote=new URI(request.getRequestURL().toString()).getHost();
		        	logRequest.protocol=request.getProtocol().toString();
		        	logRequest.requestQuery=request.getQueryString();
		        	logRequest.action=LogRequest.Action.Login.getKey();
		        	
					/* Nếu là admin/website */
		        	if(request.getRequestURL().toString().contains("/admin")) {
		        		logRequest.access=LogRequest.Access.Admin.getKey();
		        	}else {
		        		logRequest.access=LogRequest.Access.Website.getKey();
		        	}
		        	
		        	logRequestRepository.save(logRequest);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    	thread.start();
    }
}
