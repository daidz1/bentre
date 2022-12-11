package vn.com.ngn.site.model.taskcreate;

import java.time.LocalDateTime;
import java.util.List;

import vn.com.ngn.site.model.UploadModuleDataWithDescriptionModel;

public class TaskInfoCreateModel {
	private String title;
	private String description;
	private int priority;
	private LocalDateTime createtime;
	private LocalDateTime endTime;
	private List<UploadModuleDataWithDescriptionModel> listFileUpload;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public LocalDateTime getCreatetime() {
		return createtime;
	}
	public void setCreatetime(LocalDateTime createtime) {
		this.createtime = createtime;
	}
	public LocalDateTime getEndTime() {
		return endTime;
	}
	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
	public List<UploadModuleDataWithDescriptionModel> getListFileUpload() {
		return listFileUpload;
	}
	public void setListFileUpload(List<UploadModuleDataWithDescriptionModel> listFileUpload) {
		this.listFileUpload = listFileUpload;
	}
}
