package vn.com.ngn.site.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import vn.com.ngn.site.model.CustomPairModel;

public class BroadcasterSupportUitl {
	final public static String UPDATEUI = "=UPDATEUI";
	
	final public static String MAINVIEW = "=MAINVIEW";
	final public static String TASKDETAIL = "=TASKDETAIL";
	final public static String COMMENTDIALOG = "=COMMENTDIALOG";
	final public static String PROGRESSDIALOG = "=PROGRESSDIALOG";
	
	public static String createMessageOnTask(JsonObject jsonTask) {
		String message = "";
		
		String userId = SessionUtil.getUserId();
		String orgId = SessionUtil.getOrgId();
		
		JsonObject jsonOwner = jsonTask.getAsJsonObject("owner");
		JsonObject jsonAssignee = jsonTask.getAsJsonObject("assignee");
		JsonObject jsonAssigneeOld = jsonTask.getAsJsonObject("assigneeOld");
		JsonArray jsonArrSupport = jsonTask.getAsJsonArray("followersTask");
		
		
		if(!userId.equals(jsonOwner.get("userId").getAsString()) || !orgId.equals(jsonOwner.get("organizationId").getAsString())) {
			message+=jsonOwner.get("userId").getAsString()+"-"+jsonOwner.get("organizationId").getAsString()+",";
		}
		
		//Dzung code
		if(jsonAssignee.get("userId").isJsonNull()) {
			return message;
		}
		
		//end
		
		
		if(!userId.equals(jsonAssignee.get("userId").getAsString()) || !orgId.equals(jsonAssignee.get("organizationId").getAsString())) {
			message+=jsonAssignee.get("userId").getAsString()+"-"+jsonAssignee.get("organizationId").getAsString()+",";
		}
		if(jsonAssigneeOld!=null) {
			if(!userId.equals(jsonAssigneeOld.get("userId").getAsString()) || !orgId.equals(jsonAssigneeOld.get("organizationId").getAsString())) {
				message+=jsonAssigneeOld.get("userId").getAsString()+"-"+jsonAssigneeOld.get("organizationId").getAsString()+",";
			}
		}
		for(JsonElement jsonEle : jsonArrSupport) {
			JsonObject jsonSupport = jsonEle.getAsJsonObject();
			
			if(!userId.equals(jsonSupport.get("userId").getAsString()) || !orgId.equals(jsonSupport.get("organizationId").getAsString())) {
				message+=jsonSupport.get("userId").getAsString()+"-"+jsonSupport.get("organizationId").getAsString()+",";
			}
		}
		message = message.trim();
		if(!message.isEmpty())
			message = message.substring(0,message.length()-1);
		
		return message;
	}
	
	public static String appendMessageWithOption(String message,String option) {
		message = option+message;
		
		return message;
	}
	
	public static List<CustomPairModel<String, String>> decodeMessageWithOnlyUser(String message){
		List<CustomPairModel<String, String>> listUserPair = new ArrayList<CustomPairModel<String,String>>();

		if(!message.isEmpty()) {
			String[] arrPair = message.split(Pattern.quote(","));
			for (int i = 0; i < arrPair.length; i++) {
				String [] arrUserPair = arrPair[i].split(Pattern.quote("-"));
				listUserPair.add(new CustomPairModel<String, String>(arrUserPair[0],arrUserPair[1]));
			}
		}
		
		return listUserPair;
	}
	
	public static boolean checkHasOption(String message,String option) {
		if(message.contains(option)) {
			return true;
		}
		return false;
	}
	
	public static String removeAllOption(String message) {
		message = message.replace(UPDATEUI, "");
		message = message.replace(MAINVIEW, "");
		message = message.replace(TASKDETAIL, "");
		message = message.replace(COMMENTDIALOG, "");
		message = message.replace(PROGRESSDIALOG, "");
		return message;
	}
}
