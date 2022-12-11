package vn.com.ngn.site.dialog.task;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.dialog.DialogTemplate;
import vn.com.ngn.site.util.service.TaskServiceUtil;
import vn.com.ngn.site.views.tasklist.component.SubTaskListComponent;
import vn.com.ngn.site.views.tasklist.component.TaskCommentComponent;

public class TaskSubTaskDialog extends DialogTemplate{
	private String taskId;
	
	public TaskSubTaskDialog(String taskId) {
		this.taskId = taskId;
		
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		super.buildLayout();
		caption.setText("Danh sách nhiệm vụ con của nhiệm vụ");
		try {
			JsonObject jsonResponse = TaskServiceUtil.getSubTaskList(taskId);
			if(jsonResponse.get("status").getAsInt()==200) {
				JsonArray jsonArrSubTask = jsonResponse.getAsJsonArray("result");
				
				if(jsonArrSubTask.size()>0) {
					SubTaskListComponent subTaskLayout = new SubTaskListComponent(jsonArrSubTask);
					vMain.add(subTaskLayout);
				} else {
					Span span = new Span("Không có nhiệm vụ con nào.");

					span.getStyle().set("margin-left", "11px");
					span.getStyle().set("font-style", "italic");
					span.getStyle().set("color", "7f7c7c");

					this.add(span);
				}
			} else {
				System.out.println(jsonResponse);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.setWidth("80%");
	}

	@Override
	public void configComponent() {
		super.configComponent();
	}
}
