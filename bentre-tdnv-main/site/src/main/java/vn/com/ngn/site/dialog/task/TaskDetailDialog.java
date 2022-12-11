	package vn.com.ngn.site.dialog.task;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

import vn.com.ngn.site.dialog.DialogTemplate;
import vn.com.ngn.site.enums.PermissionEnum;
import vn.com.ngn.site.model.TaskDetailStateForUser;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.views.tasklist.component.TaskDetailComponent;
import vn.com.ngn.site.views.tasklist.component.TaskEventComponent;
import vn.com.ngn.site.views.tasklist.component.TaskTreeComponent;

@CssImport(value="/themes/site/components/dialog.css" , themeFor = "vaadin-dialog-overlay")
public class TaskDetailDialog extends DialogTemplate{
	private JsonObject jsonTask = new JsonObject();
	private String eType;
	private String eStatus;
	
	private Tab tabDetail = new Tab(VaadinIcon.FILE_TABLE.create(), new Span("Chi tiết nhiệm vụ"));
	private Tab tabEvent = new Tab(VaadinIcon.FILE_PROCESS.create(), new Span( "Nhật ký nhiệm vụ"));
	private Tab tabTree = new Tab(VaadinIcon.FILE_TREE.create(),new Span( "Tiến trình"));
	
	private Tabs tabs = new Tabs(tabDetail, tabTree, tabEvent);
	private VerticalLayout vTabDisplay = new VerticalLayout();
	
	private TaskDetailComponent taskDetail;
	private TaskEventComponent taskEvent;
	private TaskTreeComponent taskTree;
	
	private Map<Tab, Component> tabsToPages = new HashMap<>();

	public TaskDetailDialog(JsonObject jsonTask,String eType, String eStatus) {
		this.jsonTask = jsonTask;
		this.eType = eType;
		this.eStatus = eStatus;
		
		taskDetail = new TaskDetailComponent(jsonTask, eType, eStatus);
		taskEvent = new TaskEventComponent(jsonTask.get("id").getAsString(),jsonTask.getAsJsonArray("events"));
		taskTree = new TaskTreeComponent(jsonTask.get("id").getAsString());
		
		buildLayout();
		configComponent();
		setStateOfTask();
		System.out.println(jsonTask.get("id").getAsString());
	}

	@Override
	public void buildLayout() {
		super.buildLayout();
		caption.setText("Thông tin nhiệm vụ");
		
		tabsToPages.put(tabDetail, taskDetail);
		tabsToPages.put(tabEvent, taskEvent);
		tabsToPages.put(tabTree, taskTree);
		
		taskEvent.setVisible(false);
		taskTree.setVisible(false);
		
		vTabDisplay.add(taskDetail,taskEvent,taskTree);
		
		vTabDisplay.setPadding(false);
		
		vMain.add(tabs);
		vMain.add(vTabDisplay);
		
		tabs.setWidthFull();
		
		this.setSizeFull();
	}

	@Override
	public void configComponent() {
		super.configComponent();
		
		tabs.addSelectedChangeListener(event -> {
		    tabsToPages.values().forEach(page -> page.setVisible(false));
		    Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
		    if(selectedPage.equals(taskEvent)) {
		    	try {
					taskEvent.reload();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    } else if(selectedPage.equals(taskTree)) {
		    	taskTree.loadData();
		    }
		    selectedPage.setVisible(true);
		});
		
		taskDetail.getBtnTriggerDelete().addClickListener(e->{
			this.close();
		});
	}

	public TaskDetailComponent getTaskDetail() {
		return taskDetail;
	}
	
	public void setStateOfTask() {
		TaskDetailStateForUser state = new TaskDetailStateForUser(SessionUtil.getUserId(),jsonTask);
		if(!state.isOwner() && !state.isAssignee()) {
			tabTree.setVisible(false);
		}
	}
}
