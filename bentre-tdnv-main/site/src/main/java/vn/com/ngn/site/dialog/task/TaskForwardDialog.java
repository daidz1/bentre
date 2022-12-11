package vn.com.ngn.site.dialog.task;

import vn.com.ngn.site.dialog.DialogTemplate;
import vn.com.ngn.site.views.taskcreate.SingleTaskCreateLayout;

public class TaskForwardDialog extends DialogTemplate{
	private String taskId;
	private SingleTaskCreateLayout layoutTask;
	
	public TaskForwardDialog(String taskId) {
		this.taskId = taskId;
		
		layoutTask = new SingleTaskCreateLayout(null,taskId);
		
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		caption.setText("Giao tiếp nhiệm vụ");
		
		vMain.add(layoutTask);
		
		this.setWidth("80%");
	}

	@Override
	public void configComponent() {
		super.configComponent();
	}

	public SingleTaskCreateLayout getLayoutTask() {
		return layoutTask;
	}
}
