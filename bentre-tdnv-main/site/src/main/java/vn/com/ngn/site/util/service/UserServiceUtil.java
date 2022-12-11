package vn.com.ngn.site.util.service;

import java.io.IOException;

import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import vn.com.ngn.site.enums.LogTypeEnum;
import vn.com.ngn.site.model.UserModel;
import vn.com.ngn.site.util.ClientInfoUitl;
import vn.com.ngn.site.util.SessionUtil;

public class UserServiceUtil extends ServiceUtil{
	
	@SuppressWarnings("deprecation")
	public static JsonObject login(String username, String password) throws IOException {
		String rqUrl = url+"/website/login";
		
		OkHttpClient client = new OkHttpClient();
		
		JsonObject jsonBodyRequest = new JsonObject();
		jsonBodyRequest.addProperty("username", username);
		jsonBodyRequest.addProperty("password", password);

		RequestBody body = RequestBody.create(mediaTypeJson,jsonBodyRequest.toString());
				
		Request request = new Request.Builder()
				.addHeader("UserInfo", ClientInfoUitl.getUserInfo().toString())
	                    .url(rqUrl)
	                    .post(body)
	                    .build();
		
		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		 
		return jsonObject;
	}
	
	@SuppressWarnings("deprecation")
	public static JsonObject changeUserInfo(UserModel modelUser) throws IOException {
		String rqUrl = url+"/website/user/edit";
		
		OkHttpClient client = new OkHttpClient();
		
		JsonObject jsonBodyRequest = new JsonObject();
		jsonBodyRequest.addProperty("username", modelUser.getUsername());
		jsonBodyRequest.addProperty("email", modelUser.getEmail());
		jsonBodyRequest.addProperty("fullName", modelUser.getFullname());
		jsonBodyRequest.addProperty("active", true);

		RequestBody body = RequestBody.create(mediaTypeJson,jsonBodyRequest.toString());
				
		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+SessionUtil.getToken())
				.addHeader("UserInfo", ClientInfoUitl.getUserInfo().toString())
	                    .url(rqUrl)
	                    .put(body)
	                    .build();
		
		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		
		return jsonObject;
	}
	
	@SuppressWarnings("deprecation")
	public static JsonObject updateDisplayConfig(JsonObject jsonConfig) throws IOException {
		String rqUrl = url+"/website/user/edit";
		
		OkHttpClient client = new OkHttpClient();
		
		JsonObject jsonBodyRequest = new JsonObject();
		jsonBodyRequest.addProperty("username", SessionUtil.getUser().getUsername());
		jsonBodyRequest.addProperty("email", SessionUtil.getUser().getEmail());
		jsonBodyRequest.addProperty("fullName", SessionUtil.getUser().getFullname());
		jsonBodyRequest.addProperty("active", true);
		jsonBodyRequest.add("config", jsonConfig);

		RequestBody body = RequestBody.create(mediaTypeJson,jsonBodyRequest.toString());
				
		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+SessionUtil.getToken())
				.addHeader("UserInfo", ClientInfoUitl.getUserInfo().toString())
	                    .url(rqUrl)
	                    .put(body)
	                    .build();
		
		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		
		return jsonObject;
	}
	
	@SuppressWarnings("deprecation")
	public static JsonObject changePassword(String oldPw, String newPw) throws IOException {
		String rqUrl = url+"/website/user/change-password";
		
		OkHttpClient client = new OkHttpClient();
		
		JsonObject jsonBodyRequest = new JsonObject();
		jsonBodyRequest.addProperty("passwordOld", oldPw);
		jsonBodyRequest.addProperty("passwordNew", newPw);

		RequestBody body = RequestBody.create(mediaTypeJson,jsonBodyRequest.toString());
				
		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+SessionUtil.getToken())
				.addHeader("UserInfo", ClientInfoUitl.getUserInfo().toString())
	                    .url(rqUrl)
	                    .put(body)
	                    .build();
		
		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		
		return jsonObject;
	}
	
	public static JsonObject getLog(int skip,int limit, long fromDate, long toDate,LogTypeEnum action) throws IOException {
		String rqUrl = url+"/website/log-request/list?skip="+skip+"&limit="+limit+"&fromDate="+fromDate+"&toDate="+toDate+"&action="+action;
		
		OkHttpClient client = new OkHttpClient();
				
		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+SessionUtil.getToken())
				.addHeader("UserInfo", ClientInfoUitl.getUserInfo().toString())
	                    .url(rqUrl)
	                    .get()
	                    .build();
		
		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		
		return jsonObject;
	}
}
