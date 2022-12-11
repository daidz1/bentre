package vn.com.ngn.site.util.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import vn.com.ngn.site.model.CustomPairModel;
import vn.com.ngn.site.model.TaskFilterModel;
import vn.com.ngn.site.model.UploadModuleDataWithDescriptionModel;
import vn.com.ngn.site.model.taskcreate.TaskAssigneeOrgModel;
import vn.com.ngn.site.model.taskcreate.TaskAssigneeUserModel;
import vn.com.ngn.site.model.taskcreate.TaskInfoCreateModel;
import vn.com.ngn.site.util.ClientInfoUitl;
import vn.com.ngn.site.util.GeneralUtil;
import vn.com.ngn.site.util.LocalDateUtil;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.views.doclist.component.DocAttachmentComponent;

public class TaskServiceUtil extends ServiceUtil{
	@SuppressWarnings("deprecation")
	public static JsonObject createProgresss(String taskId,int percent, String explain ,List<UploadModuleDataWithDescriptionModel> listAttachment) throws IOException {
		System.out.println("=====createProgresss=====");
		String rqUrl = url+"/website/task/process/post";
		System.out.println(rqUrl);
		OkHttpClient client = new OkHttpClient();
		
		JsonObject jsonBodyRequest = new JsonObject();
		
		jsonBodyRequest.addProperty("taskId", taskId);
		
		//owner task
		JsonObject jsonOwnerTask = new JsonObject();
		jsonOwnerTask.addProperty("userId", SessionUtil.getUserId());
		jsonOwnerTask.addProperty("fullName", SessionUtil.getUser().getFullname());
		jsonOwnerTask.addProperty("organizationId", SessionUtil.getOrgId());
		jsonOwnerTask.addProperty("organizationName", SessionUtil.getOrg().getName());
		
		jsonBodyRequest.add("creator", jsonOwnerTask);
		
		jsonBodyRequest.addProperty("percent", percent);
		jsonBodyRequest.addProperty("explain", explain);
		
		//attachment
		JsonArray jsonArrAttachmemnt = new JsonArray();
//		for(UploadModuleDataWithDescriptionModel modelFile : listAttachment) {
//			JsonObject jsonAttachment = new JsonObject();
//			
//			jsonAttachment.add("creator", jsonOwnerTask);
//			jsonAttachment.addProperty("description", modelFile.getDescription());
//			jsonAttachment.addProperty("fileType", modelFile.getFileType());
//			jsonAttachment.addProperty("fileName", modelFile.getFileName());
//			jsonAttachment.addProperty("fileBase64", GeneralUtil.inputStreamToBase64String(modelFile.getInputStream()));
//			
//			jsonArrAttachmemnt.add(jsonAttachment);
//		}
		jsonBodyRequest.add("addAttachments", jsonArrAttachmemnt);
		
		RequestBody body = RequestBody.create(mediaTypeJson,jsonBodyRequest.toString());
		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+SessionUtil.getToken())
				.addHeader("UserInfo", ClientInfoUitl.getUserInfo().toString())
	                    .url(rqUrl)
	                    .post(body)
	                    .build();
		
		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		System.out.println(jsonObject);
		
		//Dzung code
		//if has attachment do upload file
		if(jsonObject.get("status").getAsInt()==201) {
			JsonObject result = jsonObject.get("result").getAsJsonObject();
			String progressId = result.get("id").getAsString();
			for(UploadModuleDataWithDescriptionModel modelFile : listAttachment) {

				File file = new File(modelFile.getFileName());
				try {
					FileUtils.copyInputStreamToFile(modelFile.getInputStream(), file);
					JsonObject result_1 =uploadProgressAttachment(
							taskId,
							progressId,
							SessionUtil.getUserId(),
							SessionUtil.getUser().getFullname(),
							SessionUtil.getOrgId(),
							SessionUtil.getOrg().getName(),
							file,
							modelFile.getDescription()
							);
					FileUtils.forceDelete(file);
					System.out.println(result_1);
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		//end Dzung code
		
		return jsonObject;
	}
	
	@SuppressWarnings("deprecation")
	public static JsonObject createComment(String taskId,String message, String parentId,List<UploadModuleDataWithDescriptionModel> listAttachment) throws IOException {
		System.out.println("=====createComment=====");
		String rqUrl = url+"/website/task/comment/post";
		System.out.println(rqUrl);
		
		OkHttpClient client = new OkHttpClient();
		
		JsonObject jsonBodyRequest = new JsonObject();
		
		jsonBodyRequest.addProperty("taskId", taskId);
		
		//owner task
		JsonObject jsonOwnerTask = new JsonObject();
		jsonOwnerTask.addProperty("userId", SessionUtil.getUserId());
		jsonOwnerTask.addProperty("fullName", SessionUtil.getUser().getFullname());
		jsonOwnerTask.addProperty("organizationId", SessionUtil.getOrgId());
		jsonOwnerTask.addProperty("organizationName", SessionUtil.getOrg().getName());
		
		jsonBodyRequest.add("creator", jsonOwnerTask);
		
		jsonBodyRequest.addProperty("message", message);
		jsonBodyRequest.addProperty("parentId", parentId);
		
		//attachment
		JsonArray jsonArrAttachmemnt = new JsonArray();
//		for(UploadModuleDataWithDescriptionModel modelFile : listAttachment) {
//			JsonObject jsonAttachment = new JsonObject();
//			
//			jsonAttachment.add("creator", jsonOwnerTask);
//			jsonAttachment.addProperty("description", modelFile.getDescription());
//			jsonAttachment.addProperty("fileType", modelFile.getFileType());
//			jsonAttachment.addProperty("fileName", modelFile.getFileName());
//			jsonAttachment.addProperty("fileBase64", GeneralUtil.inputStreamToBase64String(modelFile.getInputStream()));
//			
//			jsonArrAttachmemnt.add(jsonAttachment);
//		}
		jsonBodyRequest.add("addAttachments", jsonArrAttachmemnt);
		
		
		RequestBody body = RequestBody.create(mediaTypeJson,jsonBodyRequest.toString());
				
		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+SessionUtil.getToken())
				.addHeader("UserInfo", ClientInfoUitl.getUserInfo().toString())
	                    .url(rqUrl)
	                    .post(body)
	                    .build();
		
		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		System.out.println(jsonObject);
		//Dzung code
		//if has attachment do upload file
		if(jsonObject.get("status").getAsInt()==201) {
			JsonObject result = jsonObject.get("result").getAsJsonObject();
			String commentId = result.get("id").getAsString();
			for(UploadModuleDataWithDescriptionModel modelFile : listAttachment) {

				File file = new File(modelFile.getFileName());
				try {
					FileUtils.copyInputStreamToFile(modelFile.getInputStream(), file);
					JsonObject result_1 =uploadCommentAttachment(
							taskId,
							commentId,
							SessionUtil.getUserId(),
							SessionUtil.getUser().getFullname(),
							SessionUtil.getOrgId(),
							SessionUtil.getOrg().getName(),
							file,
							modelFile.getDescription()
							);
					FileUtils.forceDelete(file);
					System.out.println(result_1);
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		//end Dzung code
		return jsonObject;
	}
	
	@SuppressWarnings({"deprecation"})
	public static JsonObject createUserGroup(String name, String description,TaskAssigneeUserModel modelUserCreator, TaskAssigneeUserModel modelUserAssignee,List<TaskAssigneeUserModel> listUserSupport) throws IOException {
		String rqUrl = url+"/website/group-usertask/create";
		
		OkHttpClient client = new OkHttpClient();
		
		JsonObject jsonBodyRequest = new JsonObject();
		
		//task info
		jsonBodyRequest.addProperty("name", name);
		jsonBodyRequest.addProperty("description", description);
		
		//owner task
		JsonObject jsonOwnerTask = new JsonObject();
		jsonOwnerTask.addProperty("userId", modelUserCreator.getIdUser());
		jsonOwnerTask.addProperty("fullName", modelUserCreator.getFullName());
		jsonOwnerTask.addProperty("organizationId", modelUserCreator.getIdOrg());
		jsonOwnerTask.addProperty("organizationName", modelUserCreator.getOrgName());
		
		jsonBodyRequest.add("creator", jsonOwnerTask);
		
		//assignee task
		JsonObject jsonAssigneeTask = new JsonObject();
		jsonAssigneeTask.addProperty("userId", modelUserAssignee.getIdUser());
		jsonAssigneeTask.addProperty("fullName", modelUserAssignee.getFullName());
		jsonAssigneeTask.addProperty("organizationId", modelUserAssignee.getIdOrg());
		jsonAssigneeTask.addProperty("organizationName", modelUserAssignee.getOrgName());
		
		jsonBodyRequest.add("assigneeTask", jsonAssigneeTask);
		jsonBodyRequest.addProperty("assignmentType", "User");
		
		//support task
		JsonArray jsonArrSupport = new JsonArray();
		for(TaskAssigneeUserModel modelUserSp : listUserSupport) {
			JsonObject jsonSpTask = new JsonObject();
			jsonSpTask.addProperty("userId", modelUserSp.getIdUser());
			jsonSpTask.addProperty("fullName", modelUserSp.getFullName());
			jsonSpTask.addProperty("organizationId", modelUserSp.getIdOrg());
			jsonSpTask.addProperty("organizationName", modelUserSp.getOrgName());
			
			jsonArrSupport.add(jsonSpTask);
		}
		jsonBodyRequest.add("followersTask", jsonArrSupport);

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
	
	@SuppressWarnings({"deprecation"})
	public static JsonObject createOrgGroup(String name, String description,TaskAssigneeUserModel modelUserCreator, TaskAssigneeOrgModel modelOrgAssignee,List<TaskAssigneeOrgModel> listOrgSupport) throws IOException {
		String rqUrl = url+"/website/group-usertask/create";
		
		OkHttpClient client = new OkHttpClient();
		
		JsonObject jsonBodyRequest = new JsonObject();
		
		//task info
		jsonBodyRequest.addProperty("name", name);
		jsonBodyRequest.addProperty("description", description);
		
		//owner task
		JsonObject jsonOwnerTask = new JsonObject();
		jsonOwnerTask.addProperty("userId", modelUserCreator.getIdUser());
		jsonOwnerTask.addProperty("fullName", modelUserCreator.getFullName());
		jsonOwnerTask.addProperty("organizationId", modelUserCreator.getIdOrg());
		jsonOwnerTask.addProperty("organizationName", modelUserCreator.getOrgName());
		
		jsonBodyRequest.add("creator", jsonOwnerTask);
		
		//assignee task
		JsonObject jsonAssigneeTask = new JsonObject();
		jsonAssigneeTask.addProperty("userId", "");
		jsonAssigneeTask.addProperty("fullName", "");
		jsonAssigneeTask.addProperty("organizationId", modelOrgAssignee.getOrgId());
		jsonAssigneeTask.addProperty("organizationName", modelOrgAssignee.getOrgName());
		
		jsonBodyRequest.add("assigneeTask", jsonAssigneeTask);
		jsonBodyRequest.addProperty("assignmentType", "Organization");
		
		//support task
		JsonArray jsonArrSupport = new JsonArray();
		for(TaskAssigneeOrgModel modelOrgSp : listOrgSupport) {
			JsonObject jsonSpTask = new JsonObject();
			jsonSpTask.addProperty("userId", "");
			jsonSpTask.addProperty("fullName", "");
			jsonSpTask.addProperty("organizationId", modelOrgSp.getOrgId());
			jsonSpTask.addProperty("organizationName",modelOrgSp.getOrgName());
			
			jsonArrSupport.add(jsonSpTask);
		}
		jsonBodyRequest.add("followersTask", jsonArrSupport);
		

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
	public static JsonObject createTask(String parentId,String docId,TaskInfoCreateModel modelTaskInfo,TaskAssigneeUserModel modelUserOwner, TaskAssigneeUserModel modelUserAssistant, TaskAssigneeUserModel modelUserAssignee,List<TaskAssigneeUserModel> listUserSupport) throws IOException {
		String rqUrl = url+"/website/task/create";
		System.out.println("=====createTask=====");
		System.out.println(rqUrl);
		OkHttpClient client = new OkHttpClient();
		
		JsonObject jsonBodyRequest = new JsonObject();
		
		if(parentId!=null) {
			jsonBodyRequest.addProperty("parentId",parentId);
		}
		if(docId!=null) {
			jsonBodyRequest.addProperty("docId", docId);
		}

		//task info
		jsonBodyRequest.addProperty("title", modelTaskInfo.getTitle());
		jsonBodyRequest.addProperty("description", modelTaskInfo.getDescription());
		jsonBodyRequest.addProperty("priority", modelTaskInfo.getPriority());
		jsonBodyRequest.addProperty("endTime",modelTaskInfo.getEndTime()!=null ? LocalDateUtil.localDateTimeToLong(modelTaskInfo.getEndTime()) : 0);
		
		//owner task
		JsonObject jsonOwnerTask = new JsonObject();
		jsonOwnerTask.addProperty("userId", modelUserOwner.getIdUser());
		jsonOwnerTask.addProperty("fullName", modelUserOwner.getFullName());
		jsonOwnerTask.addProperty("organizationId", modelUserOwner.getIdOrg());
		jsonOwnerTask.addProperty("organizationName", modelUserOwner.getOrgName());
		
		jsonBodyRequest.add("ownerTask", jsonOwnerTask);
		
		//assistant task
		if(modelUserAssistant!=null) {
			JsonObject jsonAssistTask = new JsonObject();
			jsonAssistTask.addProperty("userId", modelUserAssistant.getIdUser());
			jsonAssistTask.addProperty("fullName", modelUserAssistant.getFullName());
			jsonAssistTask.addProperty("organizationId", modelUserAssistant.getIdOrg());
			jsonAssistTask.addProperty("organizationName", modelUserAssistant.getOrgName());
			
			jsonBodyRequest.add("assistantTask", jsonAssistTask);
		}
		
		//assignee task
		JsonObject jsonAssigneeTask = new JsonObject();
		jsonAssigneeTask.addProperty("userId", modelUserAssignee.getIdUser());
		jsonAssigneeTask.addProperty("fullName", modelUserAssignee.getFullName());
		jsonAssigneeTask.addProperty("organizationId", modelUserAssignee.getIdOrg());
		jsonAssigneeTask.addProperty("organizationName", modelUserAssignee.getOrgName());
		
		jsonBodyRequest.add("assigneeTask", jsonAssigneeTask);
		
		//support task
		JsonArray jsonArrSupport = new JsonArray();
		for(TaskAssigneeUserModel modelUserSp : listUserSupport) {
			JsonObject jsonSpTask = new JsonObject();
			jsonSpTask.addProperty("userId", modelUserSp.getIdUser());
			jsonSpTask.addProperty("fullName", modelUserSp.getFullName());
			jsonSpTask.addProperty("organizationId", modelUserSp.getIdOrg());
			jsonSpTask.addProperty("organizationName", modelUserSp.getOrgName());
			
			jsonArrSupport.add(jsonSpTask);
		}
		jsonBodyRequest.add("addFollowersTask", jsonArrSupport);

		//attachment
		JsonArray jsonArrAttachmemnt = new JsonArray();
//		for(UploadModuleDataWithDescriptionModel modelFile : modelTaskInfo.getListFileUpload()) {
//			JsonObject jsonAttachment = new JsonObject();
//			
//			jsonAttachment.add("creator", jsonOwnerTask);
//			jsonAttachment.addProperty("description", modelFile.getDescription());
//			jsonAttachment.addProperty("fileType", modelFile.getFileType());
//			jsonAttachment.addProperty("fileName", modelFile.getFileName());
//			jsonAttachment.addProperty("fileBase64", GeneralUtil.inputStreamToBase64String(modelFile.getInputStream()));
//			
//			jsonArrAttachmemnt.add(jsonAttachment);
//		}
		jsonBodyRequest.add("addAttachments", jsonArrAttachmemnt);
		
		//assignmentType
		jsonBodyRequest.addProperty("assignmentType", "User");
		System.out.println("jsonBodyRequest: "+jsonBodyRequest);
		RequestBody body = RequestBody.create(mediaTypeJson,jsonBodyRequest.toString());
		
		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+SessionUtil.getToken())
				.addHeader("UserInfo", ClientInfoUitl.getUserInfo().toString())
	                    .url(rqUrl)
	                    .post(body)
	                    .build();
		
		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		
		//Dzung code
		//if has attachment do upload file
		if(jsonObject.get("status").getAsInt()==201) {
			JsonObject result = jsonObject.get("result").getAsJsonObject();
			String taskId = result.get("id").getAsString();
			for(UploadModuleDataWithDescriptionModel modelFile : modelTaskInfo.getListFileUpload()) {
				
				File file = new File(modelFile.getFileName());
				try {
					FileUtils.copyInputStreamToFile(modelFile.getInputStream(), file);
					JsonObject result_1 =uploadTaskAttachment(
							taskId,
							modelUserOwner.getIdUser(),
							modelUserOwner.getFullName(),
							modelUserOwner.getIdOrg(),
							modelUserOwner.getOrgName(),
							file,
							modelFile.getDescription()
							);
					FileUtils.forceDelete(file);
					System.out.println(result_1);
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
		//end Dzung code
		
		return jsonObject;
	
	}
	

	public static JsonObject createOrgTask(String parentId,String docId,TaskInfoCreateModel modelTaskInfo,TaskAssigneeUserModel modelUserOwner, TaskAssigneeUserModel modelUserAssistant, TaskAssigneeOrgModel modelOrgAssignee,List<TaskAssigneeOrgModel> listOrgSupport) throws IOException {
		String rqUrl = url+"/website/task/create";
		System.out.println("=====createOrgTask=====");
		System.out.println(rqUrl);
		
		OkHttpClient client = new OkHttpClient();
		
		JsonObject jsonBodyRequest = new JsonObject();
		
		if(parentId!=null) {
			jsonBodyRequest.addProperty("parentId",parentId);
		}
		if(docId!=null) {
			jsonBodyRequest.addProperty("docId", docId);
		}

		//task info
		jsonBodyRequest.addProperty("title", modelTaskInfo.getTitle());
		jsonBodyRequest.addProperty("description", modelTaskInfo.getDescription());
		jsonBodyRequest.addProperty("priority", modelTaskInfo.getPriority());
		jsonBodyRequest.addProperty("endTime",modelTaskInfo.getEndTime()!=null ? LocalDateUtil.localDateTimeToLong(modelTaskInfo.getEndTime()) : 0);
		
		//owner task
		JsonObject jsonOwnerTask = new JsonObject();
		jsonOwnerTask.addProperty("userId", modelUserOwner.getIdUser());
		jsonOwnerTask.addProperty("fullName", modelUserOwner.getFullName());
		jsonOwnerTask.addProperty("organizationId", modelUserOwner.getIdOrg());
		jsonOwnerTask.addProperty("organizationName", modelUserOwner.getOrgName());
		
		jsonBodyRequest.add("ownerTask", jsonOwnerTask);
		
		//assistant task
		if(modelUserAssistant!=null) {
			JsonObject jsonAssistTask = new JsonObject();
			jsonAssistTask.addProperty("userId", modelUserAssistant.getIdUser());
			jsonAssistTask.addProperty("fullName", modelUserAssistant.getFullName());
			jsonAssistTask.addProperty("organizationId", modelUserAssistant.getIdOrg());
			jsonAssistTask.addProperty("organizationName", modelUserAssistant.getOrgName());
			
			jsonBodyRequest.add("assistantTask", jsonAssistTask);
		}
		
		//assignee task
		JsonObject jsonAssigneeTask = new JsonObject();
//		jsonAssigneeTask.addProperty("userId", "null");
//		jsonAssigneeTask.addProperty("fullName", "null");
		jsonAssigneeTask.addProperty("organizationId", modelOrgAssignee.getOrgId());
		jsonAssigneeTask.addProperty("organizationName", modelOrgAssignee.getOrgName());
		
		jsonBodyRequest.add("assigneeTask", jsonAssigneeTask);
		
		//support task
		JsonArray jsonArrSupport = new JsonArray();
		for(TaskAssigneeOrgModel modelOrgSp : listOrgSupport) {
			JsonObject jsonSpTask = new JsonObject();
//			jsonSpTask.addProperty("userId", "");
//			jsonSpTask.addProperty("fullName", "");
			jsonSpTask.addProperty("organizationId", modelOrgSp.getOrgId());
			jsonSpTask.addProperty("organizationName", modelOrgSp.getOrgName());
			
			jsonArrSupport.add(jsonSpTask);
		}
		jsonBodyRequest.add("addFollowersTask", jsonArrSupport);

		//attachment
		JsonArray jsonArrAttachmemnt = new JsonArray();
//		for(UploadModuleDataWithDescriptionModel modelFile : modelTaskInfo.getListFileUpload()) {
//			JsonObject jsonAttachment = new JsonObject();
//			
//			jsonAttachment.add("creator", jsonOwnerTask);
//			jsonAttachment.addProperty("description", modelFile.getDescription());
//			jsonAttachment.addProperty("fileType", modelFile.getFileType());
//			jsonAttachment.addProperty("fileName", modelFile.getFileName());
//			jsonAttachment.addProperty("fileBase64", GeneralUtil.inputStreamToBase64String(modelFile.getInputStream()));
//			
//			jsonArrAttachmemnt.add(jsonAttachment);
//		}
		jsonBodyRequest.add("addAttachments", jsonArrAttachmemnt);
		
		//assignmentType
		jsonBodyRequest.addProperty("assignmentType", "Organization");
		System.out.println(jsonBodyRequest.toString());
		RequestBody body = RequestBody.create(mediaTypeJson,jsonBodyRequest.toString());
				
		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+SessionUtil.getToken())
				.addHeader("UserInfo", ClientInfoUitl.getUserInfo().toString())
	                    .url(rqUrl)
	                    .post(body)
	                    .build();
		
		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		//Dzung code
		//if has attachment do upload file
		if(jsonObject.get("status").getAsInt()==201) {
			JsonObject result = jsonObject.get("result").getAsJsonObject();
			String taskId = result.get("id").getAsString();
			for(UploadModuleDataWithDescriptionModel modelFile : modelTaskInfo.getListFileUpload()) {

				File file = new File(modelFile.getFileName());
				try {
					FileUtils.copyInputStreamToFile(modelFile.getInputStream(), file);
					JsonObject result_1 =uploadTaskAttachment(
							taskId,
							modelUserOwner.getIdUser(),
							modelUserOwner.getFullName(),
							modelUserOwner.getIdOrg(),
							modelUserOwner.getOrgName(),
							file,
							modelFile.getDescription()
							);
					FileUtils.forceDelete(file);
					System.out.println(result_1);
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		//end Dzung code
		
		return jsonObject;
	}
	
	@SuppressWarnings("deprecation")
	public static JsonObject updateUserGroup(String groupId, String name, String description,TaskAssigneeUserModel modelUserCreator, TaskAssigneeUserModel modelUserAssignee,List<TaskAssigneeUserModel> listUserSupport) throws IOException {
		String rqUrl = url+"/website/group-usertask/edit/"+groupId;
		
		OkHttpClient client = new OkHttpClient();
		
		JsonObject jsonBodyRequest = new JsonObject();
		
		//task info
		jsonBodyRequest.addProperty("name", name);
		jsonBodyRequest.addProperty("description", description);
		
		//owner task
		JsonObject jsonOwnerTask = new JsonObject();
		jsonOwnerTask.addProperty("userId", modelUserCreator.getIdUser());
		jsonOwnerTask.addProperty("fullName", modelUserCreator.getFullName());
		jsonOwnerTask.addProperty("organizationId", modelUserCreator.getIdOrg());
		jsonOwnerTask.addProperty("organizationName", modelUserCreator.getOrgName());
		
		jsonBodyRequest.add("creator", jsonOwnerTask);
		
		//assignee task
		JsonObject jsonAssigneeTask = new JsonObject();
		jsonAssigneeTask.addProperty("userId", modelUserAssignee.getIdUser());
		jsonAssigneeTask.addProperty("fullName", modelUserAssignee.getFullName());
		jsonAssigneeTask.addProperty("organizationId", modelUserAssignee.getIdOrg());
		jsonAssigneeTask.addProperty("organizationName", modelUserAssignee.getOrgName());
		
		jsonBodyRequest.add("assigneeTask", jsonAssigneeTask);
		jsonBodyRequest.addProperty("assignmentType", "User");
		
		//support task
		JsonArray jsonArrSupport = new JsonArray();
		for(TaskAssigneeUserModel modelUserSp : listUserSupport) {
			JsonObject jsonSpTask = new JsonObject();
			jsonSpTask.addProperty("userId", modelUserSp.getIdUser());
			jsonSpTask.addProperty("fullName", modelUserSp.getFullName());
			jsonSpTask.addProperty("organizationId", modelUserSp.getIdOrg());
			jsonSpTask.addProperty("organizationName", modelUserSp.getOrgName());
			
			jsonArrSupport.add(jsonSpTask);
		}
		jsonBodyRequest.add("followersTask", jsonArrSupport);

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
	public static JsonObject updateOrgGroup(String groupId, String name, String description,TaskAssigneeUserModel modelUserCreator, TaskAssigneeOrgModel modelOrgAssignee,List<TaskAssigneeOrgModel> listOrgSupport) throws IOException {
		String rqUrl = url+"/website/group-usertask/edit/"+groupId;
		
		OkHttpClient client = new OkHttpClient();
		
		JsonObject jsonBodyRequest = new JsonObject();
		
		//task info
		jsonBodyRequest.addProperty("name", name);
		jsonBodyRequest.addProperty("description", description);
		
		//owner task
		JsonObject jsonOwnerTask = new JsonObject();
		jsonOwnerTask.addProperty("userId", modelUserCreator.getIdUser());
		jsonOwnerTask.addProperty("fullName", modelUserCreator.getFullName());
		jsonOwnerTask.addProperty("organizationId", modelUserCreator.getIdOrg());
		jsonOwnerTask.addProperty("organizationName", modelUserCreator.getOrgName());
		
		jsonBodyRequest.add("creator", jsonOwnerTask);
		
		//assignee task
		JsonObject jsonAssigneeTask = new JsonObject();
		jsonAssigneeTask.addProperty("userId", "");
		jsonAssigneeTask.addProperty("fullName", "");
		jsonAssigneeTask.addProperty("organizationId", modelOrgAssignee.getOrgId());
		jsonAssigneeTask.addProperty("organizationName", modelOrgAssignee.getOrgName());
		
		jsonBodyRequest.add("assigneeTask", jsonAssigneeTask);
		
		//support task
		JsonArray jsonArrSupport = new JsonArray();
		for(TaskAssigneeOrgModel modelOrgSp : listOrgSupport) {
			JsonObject jsonSpTask = new JsonObject();
			jsonSpTask.addProperty("userId", "");
			jsonSpTask.addProperty("fullName", "");
			jsonSpTask.addProperty("organizationId", modelOrgSp.getOrgId());
			jsonSpTask.addProperty("organizationName", modelOrgSp.getOrgName());
			
			jsonArrSupport.add(jsonSpTask);
		}
		jsonBodyRequest.add("followersTask", jsonArrSupport);
		jsonBodyRequest.addProperty("assignmentType", "Organization");

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
	public static JsonObject updateTask(String taskId,TaskInfoCreateModel modelTaskInfo, TaskAssigneeUserModel modelUserAssignee, List<CustomPairModel<String, String>> listSupportDelete,List<TaskAssigneeUserModel> listSupportAdd) throws IOException {
		String rqUrl = url+"/website/task/edit/"+taskId;
		
		OkHttpClient client = new OkHttpClient();
		
		JsonObject jsonBodyRequest = new JsonObject();
		
		//task info
		jsonBodyRequest.addProperty("title", modelTaskInfo.getTitle());
		jsonBodyRequest.addProperty("description", modelTaskInfo.getDescription());
		jsonBodyRequest.addProperty("priority", modelTaskInfo.getPriority());
		jsonBodyRequest.addProperty("endTime",modelTaskInfo.getEndTime()!=null ? LocalDateUtil.localDateTimeToLong(modelTaskInfo.getEndTime()) : 0);
	
		if(modelUserAssignee!=null) {
			JsonObject jsonAssigneeTask = new JsonObject();
			jsonAssigneeTask.addProperty("userId", modelUserAssignee.getIdUser());
			jsonAssigneeTask.addProperty("fullName", modelUserAssignee.getFullName());
			jsonAssigneeTask.addProperty("organizationId", modelUserAssignee.getIdOrg());
			jsonAssigneeTask.addProperty("organizationName", modelUserAssignee.getOrgName());
			
			jsonBodyRequest.add("assigneeTask", jsonAssigneeTask);
		}
		
		//support task
		JsonArray jsonArrSupport = new JsonArray();
		for(TaskAssigneeUserModel modelUserSp : listSupportAdd) {
			JsonObject jsonSpTask = new JsonObject();
			jsonSpTask.addProperty("userId", modelUserSp.getIdUser());
			jsonSpTask.addProperty("fullName", modelUserSp.getFullName());
			jsonSpTask.addProperty("organizationId", modelUserSp.getIdOrg());
			jsonSpTask.addProperty("organizationName", modelUserSp.getOrgName());
			
			jsonArrSupport.add(jsonSpTask);
		}
		jsonBodyRequest.add("addFollowersTask", jsonArrSupport);
		
		//support task delete
		JsonArray jsonArrDeleteSupport = new JsonArray();
		for(CustomPairModel<String, String> modelUserSp : listSupportDelete) {
			JsonObject jsonSpTask = new JsonObject();
			jsonSpTask.addProperty("userId", modelUserSp.getKey());
			jsonSpTask.addProperty("organizationId", modelUserSp.getValue());
			
			jsonArrDeleteSupport.add(jsonSpTask);
		}
		jsonBodyRequest.add("deleteFollowersTask", jsonArrDeleteSupport);
		//attachment
		JsonObject jsonOwnerTask = new JsonObject();
		jsonOwnerTask.addProperty("userId", SessionUtil.getUserId());
		jsonOwnerTask.addProperty("fullName", SessionUtil.getUser().getFullname());
		jsonOwnerTask.addProperty("organizationId", SessionUtil.getOrgId());
		jsonOwnerTask.addProperty("organizationName", SessionUtil.getOrg().getName());
		
		JsonArray jsonArrAttachmemnt = new JsonArray();
//		for(UploadModuleDataWithDescriptionModel modelFile : modelTaskInfo.getListFileUpload()) {
//			JsonObject jsonAttachment = new JsonObject();
//			
//			jsonAttachment.add("creator", jsonOwnerTask);
//			jsonAttachment.addProperty("description", modelFile.getDescription());
//			jsonAttachment.addProperty("fileType", modelFile.getFileType());
//			jsonAttachment.addProperty("fileName", modelFile.getFileName());
//			jsonAttachment.addProperty("fileBase64", GeneralUtil.inputStreamToBase64String(modelFile.getInputStream()));
//			
//			jsonArrAttachmemnt.add(jsonAttachment);
//		}
		jsonBodyRequest.add("addAttachments", jsonArrAttachmemnt);
		
		RequestBody body = RequestBody.create(mediaTypeJson,jsonBodyRequest.toString());
				
		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+SessionUtil.getToken())
				.addHeader("UserInfo", ClientInfoUitl.getUserInfo().toString())
	                    .url(rqUrl)
	                    .put(body)
	                    .build();
		
		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		//Dzung code
		//if has attachment do upload file
		if(jsonObject.get("status").getAsInt()==201) {
			for(UploadModuleDataWithDescriptionModel modelFile : modelTaskInfo.getListFileUpload()) {

				File file = new File(modelFile.getFileName());
				try {
					FileUtils.copyInputStreamToFile(modelFile.getInputStream(), file);
					JsonObject result_1 =uploadTaskAttachment(
							taskId,
							SessionUtil.getUserId(),
							SessionUtil.getUser().getFullname(),
							SessionUtil.getOrgId(),
							SessionUtil.getOrg().getName(),
							file,
							modelFile.getDescription()
							);
					FileUtils.forceDelete(file);
					System.out.println(result_1);
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		//end Dzung code
		return jsonObject;
	}
	
	//Dzung code
	public static JsonObject updateOrgTask(String taskId,TaskInfoCreateModel modelTaskInfo, TaskAssigneeOrgModel taskAssigneeOrgModel, List<CustomPairModel<String, String>> listSupportDelete,List<TaskAssigneeOrgModel> listSupportAdd) throws IOException {
		String rqUrl = url+"/website/task/edit/"+taskId;
		System.out.println("===update orgTask===");
		System.out.println(rqUrl);
		OkHttpClient client = new OkHttpClient();
		
		JsonObject jsonBodyRequest = new JsonObject();
		
		//task info
		jsonBodyRequest.addProperty("title", modelTaskInfo.getTitle());
		jsonBodyRequest.addProperty("description", modelTaskInfo.getDescription());
		jsonBodyRequest.addProperty("priority", modelTaskInfo.getPriority());
		jsonBodyRequest.addProperty("endTime",modelTaskInfo.getEndTime()!=null ? LocalDateUtil.localDateTimeToLong(modelTaskInfo.getEndTime()) : 0);
	
		if(taskAssigneeOrgModel!=null) {
			JsonObject jsonAssigneeTask = new JsonObject();
			jsonAssigneeTask.addProperty("organizationId", taskAssigneeOrgModel.getOrgId());
			jsonAssigneeTask.addProperty("organizationName", taskAssigneeOrgModel.getOrgName());
			
			jsonBodyRequest.add("assigneeTask", jsonAssigneeTask);
		}
		
		//support task
		JsonArray jsonArrSupport = new JsonArray();
		System.out.println("===List org support===");
		for(TaskAssigneeOrgModel modelOrgSp : listSupportAdd) {
			JsonObject jsonSpTask = new JsonObject();
			
			jsonSpTask.addProperty("organizationId", modelOrgSp.getOrgId());
			jsonSpTask.addProperty("organizationName", modelOrgSp.getOrgName());
			
			jsonArrSupport.add(jsonSpTask);
			System.out.println("organizationId: "+modelOrgSp.getOrgId());
			System.out.println("organizationName: "+modelOrgSp.getOrgName());
			

			
		}
		jsonBodyRequest.add("addFollowersTask", jsonArrSupport);
		
		//support task delete
		System.out.println("===List org support to delete===");
		JsonArray jsonArrDeleteSupport = new JsonArray();
		for(CustomPairModel<String, String> modelOrgSp : listSupportDelete) {
			JsonObject jsonSpTask = new JsonObject();
			jsonSpTask.addProperty("organizationId", modelOrgSp.getKey());
			jsonSpTask.addProperty("organizationName", modelOrgSp.getValue());
			
			jsonArrDeleteSupport.add(jsonSpTask);
			System.out.println("organizationId: "+modelOrgSp.getKey());
			System.out.println("organizationName: "+modelOrgSp.getValue());
		}
		jsonBodyRequest.add("deleteFollowersTask", jsonArrDeleteSupport);
		//attachment
		JsonObject jsonOwnerTask = new JsonObject();
		jsonOwnerTask.addProperty("userId", SessionUtil.getUserId());
		jsonOwnerTask.addProperty("fullName", SessionUtil.getUser().getFullname());
		jsonOwnerTask.addProperty("organizationId", SessionUtil.getOrgId());
		jsonOwnerTask.addProperty("organizationName", SessionUtil.getOrg().getName());
		
		JsonArray jsonArrAttachmemnt = new JsonArray();
//		for(UploadModuleDataWithDescriptionModel modelFile : modelTaskInfo.getListFileUpload()) {
//			JsonObject jsonAttachment = new JsonObject();
//			
//			jsonAttachment.add("creator", jsonOwnerTask);
//			jsonAttachment.addProperty("description", modelFile.getDescription());
//			jsonAttachment.addProperty("fileType", modelFile.getFileType());
//			jsonAttachment.addProperty("fileName", modelFile.getFileName());
//			jsonAttachment.addProperty("fileBase64", GeneralUtil.inputStreamToBase64String(modelFile.getInputStream()));
//			
//			jsonArrAttachmemnt.add(jsonAttachment);
//		}
		jsonBodyRequest.add("addAttachments", jsonArrAttachmemnt);
		System.out.println(jsonBodyRequest.toString());
		
		RequestBody body = RequestBody.create(mediaTypeJson,jsonBodyRequest.toString());
				
		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+SessionUtil.getToken())
				.addHeader("UserInfo", ClientInfoUitl.getUserInfo().toString())
	                    .url(rqUrl)
	                    .put(body)
	                    .build();
		
		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		//Dzung code
		//if has attachment do upload file
		if(jsonObject.get("status").getAsInt()==201) {
			for(UploadModuleDataWithDescriptionModel modelFile : modelTaskInfo.getListFileUpload()) {

				File file = new File(modelFile.getFileName());
				try {
					FileUtils.copyInputStreamToFile(modelFile.getInputStream(), file);
					JsonObject result_1 =uploadTaskAttachment(
							taskId,
							SessionUtil.getUserId(),
							SessionUtil.getUser().getFullname(),
							SessionUtil.getOrgId(),
							SessionUtil.getOrg().getName(),
							file,
							modelFile.getDescription()
							);
					FileUtils.forceDelete(file);
					System.out.println(result_1);
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		//end Dzung code
		return jsonObject;
	}
	
	
	public static JsonObject setTaskAssignee(String taskId, TaskAssigneeUserModel assigneeUser, TaskAssigneeUserModel assigneeBy ) throws IOException {
		String rqUrl = url+"/website/task/set-assignee-user/"+taskId;
		System.out.println("===setTaskAssignee===");
		System.out.println(rqUrl);
		OkHttpClient client = new OkHttpClient();
		
		JsonObject jsonBodyRequest = new JsonObject();
		jsonBodyRequest.addProperty("userId", assigneeUser.getIdUser());
		jsonBodyRequest.addProperty("fullName", assigneeUser.getFullName());
		JsonObject jsonAssigneeBy = new JsonObject();
		jsonAssigneeBy.addProperty("userId", assigneeBy.getIdUser());
		jsonAssigneeBy.addProperty("fullName", assigneeBy.getFullName());
		jsonAssigneeBy.addProperty("organizationId", assigneeBy.getIdOrg());
		jsonAssigneeBy.addProperty("organizationName", assigneeBy.getOrgName());
		jsonBodyRequest.add("assignmentBy", jsonAssigneeBy);
		
		System.out.println(jsonBodyRequest.toString());
		RequestBody body = RequestBody.create(mediaTypeJson,jsonBodyRequest.toString());
		
		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+SessionUtil.getToken())
				.addHeader("UserInfo", ClientInfoUitl.getUserInfo().toString())
	                    .url(rqUrl)
	                    .put(body)
	                    .build();
		
		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		System.out.println(jsonObject);
		return jsonObject;
		
	}
	public static JsonObject unSetTaskAssignee(String taskId, TaskAssigneeUserModel unAssigneeBy, String reason ) throws IOException {
		String rqUrl = url+"/website/task/unset-assignee-user/"+taskId;
		System.out.println("===unSetTaskAssignee===");
		System.out.println(rqUrl);
		OkHttpClient client = new OkHttpClient();
		//reason
		JsonObject jsonBodyRequest = new JsonObject();
		jsonBodyRequest.addProperty("reason", reason);
		//unAssigneeBy
		JsonObject jsonUnAssigneeBy = new JsonObject();
		jsonUnAssigneeBy.addProperty("userId", unAssigneeBy.getIdUser());
		jsonUnAssigneeBy.addProperty("fullName", unAssigneeBy.getFullName());
		jsonUnAssigneeBy.addProperty("organizationId", unAssigneeBy.getIdOrg());
		jsonUnAssigneeBy.addProperty("organizationName", unAssigneeBy.getOrgName());
		
		jsonBodyRequest.add("unassignmentBy", jsonUnAssigneeBy);
		
		System.out.println(jsonBodyRequest.toString());
		RequestBody body = RequestBody.create(mediaTypeJson,jsonBodyRequest.toString());
		
		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+SessionUtil.getToken())
				.addHeader("UserInfo", ClientInfoUitl.getUserInfo().toString())
	                    .url(rqUrl)
	                    .put(body)
	                    .build();
		
		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		System.out.println(jsonObject);
		return jsonObject;
		
	}
	public static JsonObject setTaskSupport(String taskId, TaskAssigneeUserModel supportUser, TaskAssigneeUserModel assigneeBy ) throws IOException {
		String rqUrl = url+"/website/task/set-follow-user/"+taskId;
		System.out.println("===setTaskFollow===");
		System.out.println(rqUrl);
		OkHttpClient client = new OkHttpClient();
		
		JsonObject jsonBodyRequest = new JsonObject();
		jsonBodyRequest.addProperty("userId", supportUser.getIdUser());
		jsonBodyRequest.addProperty("fullName", supportUser.getFullName());
		jsonBodyRequest.addProperty("organizationId", supportUser.getIdOrg());
		jsonBodyRequest.addProperty("organizationName", supportUser.getOrgName());
		
		JsonObject jsonAssigneeBy = new JsonObject();
		jsonAssigneeBy.addProperty("userId", assigneeBy.getIdUser());
		jsonAssigneeBy.addProperty("fullName", assigneeBy.getFullName());
		jsonAssigneeBy.addProperty("organizationId", assigneeBy.getIdOrg());
		jsonAssigneeBy.addProperty("organizationName", assigneeBy.getOrgName());
		jsonBodyRequest.add("assignmentBy", jsonAssigneeBy);
		
		System.out.println(jsonBodyRequest.toString());
		RequestBody body = RequestBody.create(mediaTypeJson,jsonBodyRequest.toString());
		
		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+SessionUtil.getToken())
				.addHeader("UserInfo", ClientInfoUitl.getUserInfo().toString())
	                    .url(rqUrl)
	                    .put(body)
	                    .build();
		
		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		System.out.println(jsonObject);
		return jsonObject;
		
	}
	public static JsonObject unSetTaskSupport(String taskId, TaskAssigneeUserModel unAssigneeBy, String reason ) throws IOException {
		String rqUrl = url+"/website/task/unset-follow-user/"+taskId;
		System.out.println("===unSetTaskFollow===");
		System.out.println(rqUrl);
		OkHttpClient client = new OkHttpClient();
		//reason
		JsonObject jsonBodyRequest = new JsonObject();
		jsonBodyRequest.addProperty("organizationId", unAssigneeBy.getIdOrg());
		jsonBodyRequest.addProperty("organizationName",unAssigneeBy.getOrgName());
		
		jsonBodyRequest.addProperty("reason", reason);
		//unAssigneeBy
		JsonObject jsonUnAssigneeBy = new JsonObject();
		jsonUnAssigneeBy.addProperty("userId", unAssigneeBy.getIdUser());
		jsonUnAssigneeBy.addProperty("fullName", unAssigneeBy.getFullName());
		jsonUnAssigneeBy.addProperty("organizationId", unAssigneeBy.getIdOrg());
		jsonUnAssigneeBy.addProperty("organizationName", unAssigneeBy.getOrgName());
		
		jsonBodyRequest.add("unassignmentBy", jsonUnAssigneeBy);
		
		System.out.println(jsonBodyRequest.toString());
		RequestBody body = RequestBody.create(mediaTypeJson,jsonBodyRequest.toString());
		
		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+SessionUtil.getToken())
				.addHeader("UserInfo", ClientInfoUitl.getUserInfo().toString())
	                    .url(rqUrl)
	                    .put(body)
	                    .build();
		
		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		System.out.println(jsonObject);
		return jsonObject;
		
	}
	//end Dzung code
	@SuppressWarnings("deprecation")
	public static JsonObject completeTask(String taskId,String userId, String orgId) throws IOException {
		String rqUrl = url+"/website/task/complete/"+taskId;
		
		OkHttpClient client = new OkHttpClient();
		
		JsonObject jsonBodyRequest = new JsonObject();
		jsonBodyRequest.addProperty("userId", userId);
		jsonBodyRequest.addProperty("organizationId", orgId);
		
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
	public static JsonObject setViewdNotify(String notifyId) throws IOException {
		String rqUrl = url+"/website/feature/notify/viewed/"+notifyId;
		
		OkHttpClient client = new OkHttpClient();
		
		JsonObject jsonBodyRequest = new JsonObject();
		
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
	public static JsonObject setViewdAllNotify(String userId, String orgId) throws IOException {
		String rqUrl = url+"/website/feature/notify/mark-all?userId="+userId+"&organizationId="+orgId;
		
		OkHttpClient client = new OkHttpClient();
		
		JsonObject jsonBodyRequest = new JsonObject();
		
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
	public static JsonObject redoTask(String taskId,String userId, String orgId,String reason) throws IOException {
		String rqUrl = url+"/website/task/redo/"+taskId;
		
		OkHttpClient client = new OkHttpClient();
		
		JsonObject jsonBodyRequest = new JsonObject();
		jsonBodyRequest.addProperty("userId", userId);
		jsonBodyRequest.addProperty("organizationId", orgId);
		jsonBodyRequest.addProperty("reason", reason);
		
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
	public static JsonObject ratingTask(String taskId,int star, String comment) throws IOException {
		String rqUrl = url+"/website/task/rating/"+taskId;
		
		OkHttpClient client = new OkHttpClient();
		
		JsonObject jsonBodyRequest = new JsonObject();
		
		jsonBodyRequest.addProperty("star", star);
		jsonBodyRequest.addProperty("comment", comment);
		JsonObject jsonOwnerTask = new JsonObject();
		jsonOwnerTask.addProperty("userId", SessionUtil.getUserId());
		jsonOwnerTask.addProperty("fullName", SessionUtil.getUser().getFullname());
		jsonOwnerTask.addProperty("organizationId", SessionUtil.getOrgId());
		jsonOwnerTask.addProperty("organizationName", SessionUtil.getOrg().getName());
		
		jsonBodyRequest.add("creator", jsonOwnerTask);
		
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
	
	public static JsonObject deleteUserGroup(String groupId) throws IOException {
		String rqUrl = url+"/website/group-usertask/delete/"+groupId;
		
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
	
	public static JsonObject deleteTask(String taskId) throws IOException {
		String rqUrl = url+"/website/task/delete/"+taskId;
		
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
	
	public static JsonObject getCountTaskList(TaskFilterModel modelFilter) throws IOException {
		String rqUrl = url+"/website/task/count?"+modelFilter.createQueryString();
		
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
	
	public static JsonObject getCountDashboard(String userId, String orgId,int year, int itemCount) throws IOException {
		long startTime = LocalDateUtil.localDateTimeToLong(LocalDateUtil.getStartOfTheYear(year));
		long endTime = LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(year));
		String rqUrl = url+"/website/feature/dashboard?userId="+userId+"&organizationId="+orgId+"&topUser="+itemCount+"&fromDate="+startTime+"&toDate="+endTime;
		
		OkHttpClient client = new OkHttpClient();
				
		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+SessionUtil.getToken())
				.addHeader("UserInfo", ClientInfoUitl.getUserInfo().toString())
	                    .url(rqUrl)
	                    .get()
	                    .build();
		 
		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		System.out.println("Task Dashboard: "+ClientInfoUitl.getUserInfo());
		return jsonObject;
	}
	
	public static JsonObject getCountAllTask(String userId, String orgId, int year,String token) throws IOException {
		long startTime = LocalDateUtil.localDateTimeToLong(LocalDateUtil.getStartOfTheYear(year));
		long endTime = LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(year));
		
		String rqUrl = url+"/website/feature/menucount?userId="+userId+"&organizationId="+orgId+"&fromDate="+startTime+"&toDate="+endTime;
		System.out.println("===getCountAllTask==");
		System.out.println("rqUrl: "+rqUrl);
		OkHttpClient client = new OkHttpClient();
				
		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+token)
				.addHeader("UserInfo", ClientInfoUitl.getUserInfo().toString())
	                    .url(rqUrl)
	                    .get()
	                    .build();
		
		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		
		return jsonObject;
	}
	
	public static JsonObject getCountUserNoitify(String userId,String orgId) throws IOException {
		String rqUrl = url+"/website/feature/notify/count?userId="+userId+"&organizationId="+orgId;
		
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
	
	public static JsonObject getTaskSumByDate(TaskFilterModel modelFilter) throws IOException {
		String rqUrl = url+"/website/task/sumdate?"+modelFilter.createQueryString();
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
	
	public static JsonObject getTaskListTop(TaskFilterModel modelFilter) throws IOException {
		String rqUrl = url+"/website/task/list/top?"+modelFilter.createQueryString();
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
	
	public static JsonObject getTaskList(TaskFilterModel modelFilter) throws IOException {
		String rqUrl = url+"/website/task/list?"+modelFilter.createQueryString();
		System.out.println("===getTaskList url: "+rqUrl+"===");
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
	
	public static JsonObject getTaskListByDocId(String docId) throws IOException {
		String rqUrl = url+"/website/task/list/"+docId;
		
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
	
	public static JsonObject getSubTaskList(String taskId) throws IOException {
		String rqUrl = url+"/website/task/get/subtask/"+taskId;
		
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
	
	public static JsonObject getTaskDetail(String taskId,String token,String userId,String orgId) throws IOException {
		String rqUrl = url+"/website/task/get/"+taskId+"?userId="+userId+"&organizationId="+orgId;
		System.out.println("===getTaskDetail===");
		System.out.println(rqUrl);
		OkHttpClient client = new OkHttpClient();
				
		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+token)
				.addHeader("UserInfo", ClientInfoUitl.getUserInfo().toString())
	                    .url(rqUrl)
	                    .get()
	                    .build();
		
		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		
		return jsonObject;
	}
	
	public static JsonObject getTaskDetail(String taskId) throws IOException {
		return getTaskDetail(taskId,SessionUtil.getToken(),SessionUtil.getUserId(),SessionUtil.getOrgId());
	}
	
	public static JsonObject getAssigneeList(String userId, String orgId) throws IOException {
		String rqUrl = url+"/website/user/list/assignee?userId="+userId+"&organizationId="+orgId;
		System.out.println("======getAssigneeList======");
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
	
	public static JsonObject getUserFilterList(TaskFilterModel modelFilter) throws IOException {
		String rqUrl = url+"/website/task/list/usertask?"+modelFilter.createQueryString();
		
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
	
	public static JsonObject getUserGroupList(String keyword, int skip, int limit,String userId,String organizationId, String assignmentType) throws IOException {
		String rqUrl = url+"/website/group-usertask/list?"
				+ "userId="+userId
				+"&organizationId="+ organizationId
				+"&assignmentType="+assignmentType
				+"&skip="+skip+"&limit="+limit+"&keyword="+keyword;
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
	
	public static JsonObject getTaskTree(String taskId) throws IOException {
		String rqUrl = url+"/website/task/tree/"+taskId;
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
	
	public static JsonObject getProgressList(String taskId) throws IOException {
		String rqUrl = url+"/website/task/process/get/"+taskId;
		
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
	
	public static JsonObject getCommentList(String taskId) throws IOException {
		return getCommentList(taskId,SessionUtil.getToken());
	}
	public static JsonObject getCommentList(String taskId, String token) throws IOException {
		String rqUrl = url+"/website/task/comment/get/"+taskId;
		
		OkHttpClient client = new OkHttpClient();
				
		Request request = new Request.Builder()
				.addHeader("Authorization", "Bearer "+token)
				.addHeader("UserInfo", ClientInfoUitl.getUserInfo().toString())
	                    .url(rqUrl)
	                    .get()
	                    .build();
		
		ResponseBody responseBody = client.newCall(request).execute().body();
		JsonObject jsonObject = gson.fromJson(responseBody.string(), JsonObject.class);
		
		return jsonObject;
	}
	
	public static JsonObject getAttachmentList(String taskId) throws IOException {
		String rqUrl = url+"/website/task/attachment/get/"+taskId;
		
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
		String rqUrl = url+"/website/task/attachment/path?path="+path;
		
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
	
	public static JsonObject getUserNoitify(int skip,int limit, String userId,String orgId,String type) throws IOException {
		String rqUrl = url+"/website/feature/notify/list?skip="+skip+"&limit="+limit+"&userId="+userId+"&organizationId="+orgId;
		if(type!=null)
			rqUrl+="&categorykey="+type;
		
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
	
	
	public static JsonObject uploadTaskAttachment (
			String taskId, 
			String userId,
			String fullName,
			String organizationId,
			String organizationName,
			File file,
			String description
			) throws IOException{
		String rqUrl = url+"/website/task/attachment/add/"+taskId;
		System.out.println("=====uploadAttachment=====");
		System.out.println(rqUrl);
		OkHttpClient client = new OkHttpClient();
		@SuppressWarnings("deprecation")
		RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
		.addFormDataPart("file", file.getName(), 
				RequestBody.create(MediaType.parse("application/octet-stream"),file))
		
		.addFormDataPart("userId",userId)
		.addFormDataPart("fullName",fullName)
		.addFormDataPart("organizationId",organizationId)
		.addFormDataPart("organizationName",organizationName)
		.addFormDataPart("description",description)
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
	
	public static JsonObject uploadCommentAttachment (
			String taskId,
			String commentId,
			String userId,
			String fullName,
			String organizationId,
			String organizationName,
			File file,
			String description
			) throws IOException{
		String rqUrl = url+"/website/task/comment/post/add-attachment/"+taskId+"/"+commentId;
		System.out.println("=====uploadCommentAttachment=====");
		System.out.println(rqUrl);
		OkHttpClient client = new OkHttpClient();
		@SuppressWarnings("deprecation")
		RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
		.addFormDataPart("file", file.getName(), 
				RequestBody.create(MediaType.parse("application/octet-stream"),file))
		
		.addFormDataPart("userId",userId)
		.addFormDataPart("fullName",fullName)
		.addFormDataPart("organizationId",organizationId)
		.addFormDataPart("organizationName",organizationName)
		.addFormDataPart("description",description)
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
	
	public static JsonObject uploadProgressAttachment (
			String taskId,
			String processId,
			String userId,
			String fullName,
			String organizationId,
			String organizationName,
			File file,
			String description
			) throws IOException{
		String rqUrl = url+"/website/task/process/post/add-attachment/"+taskId+"/"+processId;
		System.out.println("=====uploadProgressAttachment=====");
		System.out.println(rqUrl);
		OkHttpClient client = new OkHttpClient();
		@SuppressWarnings("deprecation")
		RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
		.addFormDataPart("file", file.getName(), 
				RequestBody.create(MediaType.parse("application/octet-stream"),file))
		
		.addFormDataPart("userId",userId)
		.addFormDataPart("fullName",fullName)
		.addFormDataPart("organizationId",organizationId)
		.addFormDataPart("organizationName",organizationName)
		.addFormDataPart("description",description)
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
	
}
