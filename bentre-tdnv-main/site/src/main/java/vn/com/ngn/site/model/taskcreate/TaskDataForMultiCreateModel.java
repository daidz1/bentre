package vn.com.ngn.site.model.taskcreate;

import java.util.List;

public class TaskDataForMultiCreateModel {
	private TaskInfoCreateModel modelTaskInfo;
	private TaskAssigneeUserModel modelUserAssignee;
	private List<TaskAssigneeUserModel> listUserSupport;
	
	public TaskInfoCreateModel getModelTaskInfo() {
		return modelTaskInfo;
	}
	public void setModelTaskInfo(TaskInfoCreateModel modelTaskInfo) {
		this.modelTaskInfo = modelTaskInfo;
	}
	public TaskAssigneeUserModel getModelUserAssignee() {
		return modelUserAssignee;
	}
	public void setModelUserAssignee(TaskAssigneeUserModel modelUserAssignee) {
		this.modelUserAssignee = modelUserAssignee;
	}
	public List<TaskAssigneeUserModel> getListUserSupport() {
		return listUserSupport;
	}
	public void setListUserSupport(List<TaskAssigneeUserModel> listUserSupport) {
		this.listUserSupport = listUserSupport;
	}
}
