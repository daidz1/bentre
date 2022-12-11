package vn.com.ngn.site.model;

import java.util.ArrayList;
import java.util.List;

public class OrganizationModel {
	private String id;
	private String name;
	private String leaderName;
	private List<RoleModel> roles = new ArrayList<RoleModel>();
	private List<SimpleUserModel> leadersTask = new ArrayList<SimpleUserModel>();
	private List<SimpleUserModel> assistantsTask = new ArrayList<SimpleUserModel>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLeaderName() {
		return leaderName;
	}
	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}
	public List<RoleModel> getRoles() {
		return roles;
	}
	public void setRoles(List<RoleModel> roles) {
		this.roles = roles;
	}
	public List<SimpleUserModel> getLeadersTask() {
		return leadersTask;
	}
	public void setLeadersTask(List<SimpleUserModel> leadersTask) {
		this.leadersTask = leadersTask;
	}
	public List<SimpleUserModel> getAssistantsTask() {
		return assistantsTask;
	}
	public void setAssistantsTask(List<SimpleUserModel> assistantsTask) {
		this.assistantsTask = assistantsTask;
	}
}
