package vn.com.ngn.site.util.service;

import java.io.File;
import java.io.IOException;

import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import vn.com.ngn.site.model.DocFilterModel;
import vn.com.ngn.site.util.ClientInfoUitl;
import vn.com.ngn.site.util.SessionUtil;

public class DocServiceUtil extends ServiceUtil {
	//Dzung code
	@SuppressWarnings("deprecation")
	public static JsonObject newDocument(JsonObject jsonDoc) throws IOException {
		String rqUrl = url+"/website/doc/new";
		System.out.println("=====newDocument=====");
		System.out.println(rqUrl);
		System.out.println(jsonDoc);
		OkHttpClient client = new OkHttpClient();

		RequestBody body = RequestBody.create(mediaTypeJson,jsonDoc.toString());
		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+SessionUtil.getToken())
				.url(rqUrl)
				.post(body)
				.build();

		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		return jsonObject;
	}

	public static JsonObject uploadAttachment(String docId, JsonObject attachment, File file) throws IOException {
		String rqUrl = url+"/website/doc/add-attachment/"+docId;
		System.out.println("=====uploadAttachment=====");
		System.out.println(rqUrl);

		String fileType = attachment.get("fileType").getAsString();
		String fileName = attachment.get("fileName").getAsString();

		OkHttpClient client = new OkHttpClient();

		@SuppressWarnings("deprecation")
		RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
		.addFormDataPart("file", fileName, 
				RequestBody.create(MediaType.parse("application/octet-stream"),file))
		
		.addFormDataPart("fileType",fileType)
		.addFormDataPart("fileName",fileName)
		.build();

		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+SessionUtil.getToken())
				.url(rqUrl)
				.post(body)
				.build();
		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		return jsonObject;
	}
	
	@SuppressWarnings("deprecation")
	public static JsonObject editDocument(JsonObject jsonDoc) throws IOException {
		String rqUrl = url+"/website/doc/edit/"+jsonDoc.get("id").getAsString();
		System.out.println("=====editDocument=====");
		System.out.println(rqUrl);
		System.out.println(jsonDoc);
		OkHttpClient client = new OkHttpClient();

		RequestBody body = RequestBody.create(mediaTypeJson,jsonDoc.toString());
		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+SessionUtil.getToken())
				.url(rqUrl)
				.post(body)
				.build();

		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		return jsonObject;
	}
	//ennd Dzung code

	@SuppressWarnings("deprecation")
	public static JsonObject createDocument(JsonObject jsonDoc,String token,String userInfo) throws IOException {
		String rqUrl = url+"/website/doc/create";

		OkHttpClient client = new OkHttpClient();

		RequestBody body = RequestBody.create(mediaTypeJson,jsonDoc.toString());
		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+token)
				.addHeader("UserInfo", userInfo)
				.url(rqUrl)
				.post(body)
				.build();

		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		return jsonObject;
	}

	@SuppressWarnings("deprecation")
	public static JsonObject createDocument(JsonObject jsonDoc) throws IOException {
		String rqUrl = url+"/website/doc/create";

		OkHttpClient client = new OkHttpClient();

		RequestBody body = RequestBody.create(mediaTypeJson,jsonDoc.toString());
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

	public static JsonObject getCountDocList(DocFilterModel modelFilter) throws IOException {
		String rqUrl = url+"/website/doc/count?"+modelFilter.createQueryString();

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

	public static JsonObject getDocList(DocFilterModel modelFilter) throws IOException {
		System.out.println("=====getDocList=====");
		String rqUrl = url+"/website/doc/list?"+modelFilter.createQueryString();
		System.out.println(rqUrl);
		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+SessionUtil.getToken())
				.addHeader("UserInfo", ClientInfoUitl.getUserInfo().toString())
				.url(rqUrl)
				.get()
				.build();

		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		System.out.println(jsonObject);
		return jsonObject;
	}

	public static JsonObject getDocDetail(String docId) throws IOException {
		System.out.println("=====getDocDetail=====");
		String rqUrl = url+"/website/doc/get/"+docId;
		System.out.println(rqUrl);
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

	public static JsonObject getAttachmentList(String docId) throws IOException {
		String rqUrl = url+"/website/doc/attachment/get/"+docId;

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

	public static JsonObject getAttachmentContent(String path) throws IOException {
		String rqUrl = url+"/website/doc/attachment/path/"+path;

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
