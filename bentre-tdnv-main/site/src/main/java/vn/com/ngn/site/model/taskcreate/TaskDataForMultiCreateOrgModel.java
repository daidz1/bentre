package vn.com.ngn.site.model.taskcreate;

import java.util.List;

public class TaskDataForMultiCreateOrgModel {
	private TaskInfoCreateModel modelTaskInfo;
	private TaskAssigneeOrgModel modelOrgAssignee;
	private List<TaskAssigneeOrgModel> listOrgSupport;
	
	public TaskInfoCreateModel getModelTaskInfo() {
		return modelTaskInfo;
	}
	public void setModelTaskInfo(TaskInfoCreateModel modelTaskInfo) {
		this.modelTaskInfo = modelTaskInfo;
	}
	public TaskAssigneeOrgModel getModelOrgAssignee() {
		return modelOrgAssignee;
	}
	public void setModelOrgAssignee(TaskAssigneeOrgModel modelOrgAssignee) {
		this.modelOrgAssignee = modelOrgAssignee;
	}
	public List<TaskAssigneeOrgModel> getListOrgSupport() {
		return listOrgSupport;
	}
	public void setListOrgSupport(List<TaskAssigneeOrgModel> listOrgSupport) {
		this.listOrgSupport = listOrgSupport;
	}
	

}
