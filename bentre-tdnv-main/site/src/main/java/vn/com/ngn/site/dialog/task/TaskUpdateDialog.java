package vn.com.ngn.site.dialog.task;

import com.vaadin.flow.component.Component;

import vn.com.ngn.site.dialog.DialogTemplate;
import vn.com.ngn.site.views.taskcreate.SingleTaskCreateLayout;
import vn.com.ngn.site.views.taskcreate.SingleTaskCreateOrgLayout;

public class TaskUpdateDialog extends DialogTemplate{
	private String taskId;
	private Component layoutTask;
	
	public TaskUpdateDialog(String taskId,  String assignmentType) {
		
		this.taskId = taskId;
		if(assignmentType.equalsIgnoreCase("organization")) {
			layoutTask = new SingleTaskCreateOrgLayout(taskId,null);
			
		}else {
			layoutTask = new SingleTaskCreateLayout(taskId,null);
		}
		
		
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		caption.setText("Cập nhật thông tin nhiệm vụ");
		
		vMain.add(layoutTask);
		
		this.setWidth("80%");
	}

	@Override
	public void configComponent() {
		super.configComponent();
	}

	public Component getLayoutTask() {
		return layoutTask;
	}
}
