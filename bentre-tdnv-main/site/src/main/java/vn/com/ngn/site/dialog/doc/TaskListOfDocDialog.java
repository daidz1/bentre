package vn.com.ngn.site.dialog.doc;

import vn.com.ngn.site.dialog.DialogTemplate;
import vn.com.ngn.site.views.doclist.component.TaskListOfDocComponent;

public class TaskListOfDocDialog extends DialogTemplate{
	private String docId;
	public TaskListOfDocDialog(String docId) {
		this.docId = docId;
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		
		caption.setText("Danh sách nhiệm vụ của văn bản");
		
		vMain.add(new TaskListOfDocComponent(docId));
		
		this.setWidth("1000px");
	}

	@Override
	public void configComponent() {
		super.configComponent();

	}
}
