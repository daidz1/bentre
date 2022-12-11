package vn.com.ngn.site.dialog.task;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import vn.com.ngn.site.dialog.DialogTemplate;
import vn.com.ngn.site.util.service.TaskServiceUtil;
import vn.com.ngn.site.views.tasklist.component.TaskAttachmentComponent;

public class TaskAttachmentDialog extends DialogTemplate{
	private String taskId;
	
	public TaskAttachmentDialog(String taskId) {
		this.taskId = taskId;
		
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		super.buildLayout();
		caption.setText("Danh sác đính kèm của nhiệm vụ");
		try {
			JsonObject jsonResponseGet = TaskServiceUtil.getAttachmentList(taskId);
			
			if(jsonResponseGet.get("status").getAsInt()==200) {
				JsonArray jsonArray = jsonResponseGet.get("result").getAsJsonArray();
				
				TaskAttachmentComponent comComment = new TaskAttachmentComponent(jsonArray);
				
				vMain.add(comComment);
			} else {
				System.out.println(jsonResponseGet);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.setWidth("80%");
		//this.setMinHeight("500px");
	}

	@Override
	public void configComponent() {
		super.configComponent();
	}
}
