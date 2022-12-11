package vn.com.ngn.site.views.tasklist.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import vn.com.ngn.site.views.tasklist.TaskSubBlockLayout;

public class SubTaskListComponent extends TaskInfoComponent{
	public SubTaskListComponent(JsonArray jsonArray) {
		this.jsonArray = jsonArray;

		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		
		for(JsonElement jsonTask : jsonArray) {
			this.add(new TaskSubBlockLayout(jsonTask.getAsJsonObject()));
		}
		
		this.setPadding(false);
	}
}
