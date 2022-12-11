package vn.com.ngn.site.model;

import vn.com.ngn.site.enums.TaskStatusEnum;

public class TaskFilterModel {
	private int skip;
	private int limit;
	private long formdate;
	private long todate;
	private String userid;
	private String organizationId;
	private String categorykey;
	private String subcategorykey;
	private String keyword;
	private int priority;
	private String owners;
	private String assignees;
	private String followers;
	private String assistants;
	//Dzung code
	private String assignmentType;
	private String assignmentStatus;
	//end Dzung code
	public String createQueryString() {
		String queryString = "skip="+skip
				+ "&limit="+limit
				+ "&userId="+userid
				+ "&organizationId="+organizationId
				+ "&fromDate="+formdate
				+ "&toDate="+todate;
		
		if(categorykey!=null) {
			queryString+="&categorykey="+categorykey;
		}
		if(subcategorykey!=null && !subcategorykey.equals(TaskStatusEnum.TATCA.getKey())) {
			queryString+="&subcategorykey="+subcategorykey;
		}
		if(keyword!=null && !keyword.isEmpty()) {
			queryString+="&keyword="+keyword;
		}
		if(priority!=0) {
			queryString+="&priority="+priority;
		}
		
		if(owners!=null) {
			queryString+="&findOwners="+owners;
		}
		if(assignees!=null) {
			queryString+="&findAssignees="+assignees;
		}
		if(followers!=null) {
			queryString+="&findFollowers="+followers;
		}
		if(assistants!=null) {
			queryString+="&findAssistants="+assistants;
		}
		//Dzung code
		if(assignmentType!=null) {
			queryString+="&assignmentType="+assignmentType;
		}
		if(assignmentStatus!=null) {
			queryString+="&assignmentStatus="+assignmentStatus;
		}
		//end Dzung code
		return queryString;
	}
	
	public int getSkip() {
		return skip;
	}
	public void setSkip(int skip) {
		this.skip = skip;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public long getFormdate() {
		return formdate;
	}
	public void setFormdate(long formdate) {
		this.formdate = formdate;
	}
	public long getTodate() {
		return todate;
	}
	public void setTodate(long todate) {
		this.todate = todate;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
	public String getCategorykey() {
		return categorykey;
	}
	public void setCategorykey(String categorykey) {
		this.categorykey = categorykey;
	}
	public String getSubcategorykey() {
		return subcategorykey;
	}
	public void setSubcategorykey(String subcategorykey) {
		this.subcategorykey = subcategorykey;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public String getOwners() {
		return owners;
	}
	public void setOwners(String owners) {
		this.owners = owners;
	}
	public String getAssignees() {
		return assignees;
	}
	public void setAssignees(String assignees) {
		this.assignees = assignees;
	}
	public String getFollowers() {
		return followers;
	}
	public void setFollowers(String followers) {
		this.followers = followers;
	}
	public String getAssistants() {
		return assistants;
	}
	public void setAssistants(String assistants) {
		this.assistants = assistants;
	}

	public String getAssignmentType() {
		return assignmentType;
	}

	public void setAssignmentType(String assignmentType) {
		this.assignmentType = assignmentType;
	}

	public String getAssignmentStatus() {
		return assignmentStatus;
	}

	public void setAssignmentStatus(String assignmentStatus) {
		this.assignmentStatus = assignmentStatus;
	}
	
	
}
