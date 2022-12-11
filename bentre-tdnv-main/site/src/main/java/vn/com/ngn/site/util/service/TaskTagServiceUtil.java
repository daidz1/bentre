package vn.com.ngn.site.util.service;

import java.io.IOException;
import java.util.List;

import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import vn.com.ngn.site.util.ClientInfoUitl;
import vn.com.ngn.site.util.SessionUtil;

public class TaskTagServiceUtil extends ServiceUtil{
	@SuppressWarnings("deprecation")
	public static JsonObject createTag(String tagName) throws IOException {
		String rqUrl = url+"/website/tag/create";

		OkHttpClient client = new OkHttpClient();

		JsonObject jsonBodyRequest = new JsonObject();

		jsonBodyRequest.addProperty("name", tagName);

		JsonObject jsonCreator = new JsonObject();
		jsonCreator.addProperty("userId", SessionUtil.getUserId());
		jsonCreator.addProperty("fullName", SessionUtil.getUser().getFullname());
		jsonCreator.addProperty("organizationId", SessionUtil.getOrgId());
		jsonCreator.addProperty("organizationName", SessionUtil.getOrg().getName());

		jsonBodyRequest.add("creator", jsonCreator);

		RequestBody body = RequestBody.create(mediaTypeJson,jsonBodyRequest.toString());

		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+SessionUtil.getToken())
				.addHeader("UserInfo", ClientInfoUitl.getUserInfo().toString())
				.url(rqUrl)
				.post(body)
				.build();

		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		return jsonObject;
	}

	@SuppressWarnings("deprecation")
	public static JsonObject updateTag(String id,String tagName) throws IOException {
		String rqUrl = url+"/website/tag/edit/"+id;

		OkHttpClient client = new OkHttpClient();

		JsonObject jsonBodyRequest = new JsonObject();

		jsonBodyRequest.addProperty("name", tagName);

		JsonObject jsonCreator = new JsonObject();
		jsonCreator.addProperty("userId", SessionUtil.getUserId());
		jsonCreator.addProperty("fullName", SessionUtil.getUser().getFullname());
		jsonCreator.addProperty("organizationId", SessionUtil.getOrgId());
		jsonCreator.addProperty("organizationName", SessionUtil.getOrg().getName());
		jsonBodyRequest.add("creator", jsonCreator);

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

	public static JsonObject deleteTag(String idTag) throws IOException {
		String rqUrl = url+"/website/tag/delete/"+idTag;

		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+SessionUtil.getToken())
				.addHeader("UserInfo", ClientInfoUitl.getUserInfo().toString())
				.url(rqUrl)
				.delete()
				.build();

		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		return jsonObject;
	}

	public static JsonObject setTag(String taskId, String tagId) throws IOException {
		String rqUrl = url+"/website/task/set-tag/"+taskId+"?tagId="+tagId;

		JsonObject jsonBodyRequest = new JsonObject();

		RequestBody body = RequestBody.create(mediaTypeJson,jsonBodyRequest.toString());

		OkHttpClient client = new OkHttpClient();
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

	public static JsonObject unsetTag(String taskId, List<String> listTagId, String userId,String orgId) throws IOException {
		String tagId = null;
		if(listTagId.size()>0) {
			tagId = listTagId.toString().replace("[", "").replace("]", "");
		}
		String rqUrl = url+"/website/task/unset-tag/"+taskId+"?tagId="+tagId+"&userId="+userId+"&organizationId="+orgId;
		JsonObject jsonBodyRequest = new JsonObject();

		RequestBody body = RequestBody.create(mediaTypeJson,jsonBodyRequest.toString());

		OkHttpClient client = new OkHttpClient();
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

	public static JsonObject getTagList() throws IOException {
		String rqUrl = url+"/website/tag/list?skip=0&limit=0&userId="+SessionUtil.getUserId()+"&organizationId="+SessionUtil.getOrgId();
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

	public static JsonObject getTagList(String idTask,String idUser, String idOrg) throws IOException {
		String rqUrl = url+"/website/task/get-tag/"+idTask+"?userId="+idUser+"&organizationId="+idOrg;
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
