package vn.com.ngn.site.util.service;

import java.io.IOException;

import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import vn.com.ngn.site.model.TaskFilterModel;
import vn.com.ngn.site.util.ClientInfoUitl;
import vn.com.ngn.site.util.SessionUtil;

public class ReportServiceUtil extends ServiceUtil{
	public static JsonObject getReport(TaskFilterModel modelFilter) throws IOException {
		String rqUrl = url+"/website/report/task-list?"+modelFilter.createQueryString();
		
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
