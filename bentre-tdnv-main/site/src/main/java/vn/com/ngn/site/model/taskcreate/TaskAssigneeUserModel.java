package vn.com.ngn.site.model.taskcreate;

public class TaskAssigneeUserModel {
	private String idUser;
	private String fullName;
	private String idOrg;
	private String orgName;
	private String jobTitle;
	
	public String getIdUser() {
		return idUser;
	}
	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getIdOrg() {
		return idOrg;
	}
	public void setIdOrg(String idOrg) {
		this.idOrg = idOrg;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getJobTitle() {
		return jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
}
