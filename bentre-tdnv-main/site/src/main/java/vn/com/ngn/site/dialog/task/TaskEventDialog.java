package vn.com.ngn.site.dialog.task;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import vn.com.ngn.site.dialog.DialogTemplate;
import vn.com.ngn.site.util.service.TaskServiceUtil;
import vn.com.ngn.site.views.tasklist.component.TaskEventComponent;

public class TaskEventDialog extends DialogTemplate{
	private String taskId;
	
	public TaskEventDialog(String taskId) {
		this.taskId = taskId;
		
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		super.buildLayout();
		caption.setText("Nhật ký của nhiệm vụ");
		try {
			JsonObject jsonResponseGet = TaskServiceUtil.getTaskDetail(taskId);
			
			if(jsonResponseGet.get("status").getAsInt()==200) {
				JsonArray jsonArray = jsonResponseGet.getAsJsonObject("result").get("events").getAsJsonArray();
				
				TaskEventComponent comComment = new TaskEventComponent(taskId,jsonArray);
				
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
