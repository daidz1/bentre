package vn.com.ngn.site.views.doclist.component;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;
import vn.com.ngn.site.views.tasklist.TaskBlockLayout;

@SuppressWarnings("serial")
public class TaskListOfDocComponent extends DocInfoComponent{
	public TaskListOfDocComponent(String docId) {
		this.stringValue = docId;
		
		buildLayout();
		configComponent();
		
		try {
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
	}
	
	@Override
	public void configComponent() {
		super.configComponent();
	}
	
	public void loadData() throws IOException {
		this.removeAll();
		
		JsonObject jsonResponse = TaskServiceUtil.getTaskListByDocId(stringValue);

		if(jsonResponse.get("status").getAsInt()==200) {
			JsonArray jsonTaskList = jsonResponse.get("result").getAsJsonArray();

			for(JsonElement jsonTask : jsonTaskList) {
				this.add(new TaskBlockLayout(jsonTask.getAsJsonObject(),null,null,this.getClass()));
			}
		} else {
			System.out.println(jsonResponse);
			NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại", NotificationTypeEnum.ERROR);
		}
	}
}
