package vn.com.ngn.site.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import vn.com.ngn.site.util.SessionUtil;

public class TaskDetailStateForUser {
	private boolean isOwner;
	private boolean isAssistant;
	private boolean isAssignee;
	private boolean isSupport;
	
	private boolean isCompleted;
	
	private int progress;
	
	public TaskDetailStateForUser(String userId, JsonObject jsonTask) {
		String idOwner = jsonTask.getAsJsonObject("owner").get("userId").getAsString();
		String idAssistant = jsonTask.has("assistantTask") && !jsonTask.get("assistantTask").isJsonNull() ? jsonTask.getAsJsonObject("assistantTask").get("userId").getAsString() : null;
//		String idAssignee = jsonTask.getAsJsonObject("assignee").get("userId").getAsString();
		String idAssignee = jsonTask.getAsJsonObject("assignee").get("userId").isJsonNull()?"":jsonTask.getAsJsonObject("assignee").get("userId").getAsString();
		List<String> listSupportId = new ArrayList<String>();
		
		progress = jsonTask.getAsJsonArray("processes").size()>0?jsonTask.getAsJsonArray("processes").get(0).getAsJsonObject().get("percent").getAsInt() : 0;
		
		for(JsonElement jsonEle : jsonTask.getAsJsonArray("followersTask")) {
			if(jsonEle.getAsJsonObject().get("userId").isJsonNull()==false) {
				listSupportId.add(jsonEle.getAsJsonObject().get("userId").getAsString());
			}
		}
		
		if(userId.equals(idOwner)) {
			isOwner = true;
		} else if(userId.equals(idAssistant)) {
			isAssistant = true;
		} else if(userId.equals(idAssignee)) {
			isAssignee = true;
		} else if(listSupportId.contains(userId)) {
			isSupport = true;
		}
		
		long completeTime = jsonTask.get("completedTime").getAsLong();
		if(completeTime>0) {
			isCompleted = true;
		}
	}

	public boolean isOwner() {
		return isOwner;
	}
	public boolean isAssignee() {
		return isAssignee;
	}
	public boolean isAssistant() {
		return isAssistant;
	}
	public boolean isSupport() {
		return isSupport;
	}
	public boolean isCompleted() {
		return isCompleted;
	}
	public int getProgress() {
		return progress;
	}
}
