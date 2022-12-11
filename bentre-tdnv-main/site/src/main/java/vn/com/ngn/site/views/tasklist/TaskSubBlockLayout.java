package vn.com.ngn.site.views.tasklist;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.dialog.task.TaskAttachmentDialog;
import vn.com.ngn.site.dialog.task.TaskCommentDialog;
import vn.com.ngn.site.dialog.task.TaskDetailDialog;
import vn.com.ngn.site.dialog.task.TaskEventDialog;
import vn.com.ngn.site.dialog.task.TaskProgressDialog;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.UIUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;
import vn.com.ngn.site.views.tasklist.component.TaskActionComponent;
import vn.com.ngn.site.views.tasklist.component.TaskDateComponent;
import vn.com.ngn.site.views.tasklist.component.TaskSupportComponent;
import vn.com.ngn.site.views.tasklist.component.TaskTitleComponent;
import vn.com.ngn.site.views.tasklist.component.UserAssignInfoComponent;

public class TaskSubBlockLayout extends VerticalLayout implements LayoutInterface{
	private JsonObject jsonTask;
	
	private TaskActionComponent comTaskAction;
	
	public TaskSubBlockLayout(JsonObject jsonTask) {
		this.jsonTask = jsonTask;
		
		buildLayout();
//		configComponent();
	}

	@Override
	public void buildLayout() {
		//this.setPadding(false);
		this.setSpacing(false);
		this.setWidthFull();
		this.addClassName("task-block");
		
		this.getStyle().set("font-size", "13px");
		
		rebuild();
	}

	@Override
	public void configComponent() {
		comTaskAction.gethProgress().addClickListener(e->{
			TaskProgressDialog dialogProgress = new TaskProgressDialog(jsonTask.get("id").getAsString());
			
			dialogProgress.open();
		});
		
		comTaskAction.gethAttachment().addClickListener(e->{
			TaskAttachmentDialog dialogAttachment = new TaskAttachmentDialog(jsonTask.get("id").getAsString());
			
			dialogAttachment.open();
		});
		
		comTaskAction.gethComment().addClickListener(e->{
			TaskCommentDialog dialogCommnet = new TaskCommentDialog(jsonTask.get("id").getAsString());
			
			dialogCommnet.open();
		});
		
		comTaskAction.gethEvent().addClickListener(e->{
			TaskEventDialog dialogEvent = new TaskEventDialog(jsonTask.get("id").getAsString());
			
			dialogEvent.open();
		});
		
		comTaskAction.gethDetail().addClickListener(e->{
			try {
				JsonObject jsonResponse = TaskServiceUtil.getTaskDetail(jsonTask.get("id").getAsString());
				
				if(jsonResponse.get("status").getAsInt()==200) {
					TaskDetailDialog dialogTask = new TaskDetailDialog(jsonResponse.getAsJsonObject("result"), null, null);
					
					dialogTask.open();
					
					dialogTask.addOpenedChangeListener(eClose->{
						if(!eClose.isOpened()) {
							this.jsonTask = dialogTask.getTaskDetail().getJsonTask();
							
							rebuild();
							
							if(dialogTask.getTaskDetail().isChange()) {
								UIUtil.getMainView().updateCountMenu(SessionUtil.getUserId(), SessionUtil.getOrgId(),SessionUtil.getYear(),SessionUtil.getToken());
							}
						}
					});
					
					dialogTask.getTaskDetail().getBtnTriggerDelete().addClickListener(eDelete->{
						UIUtil.getMainView().updateCountMenu(SessionUtil.getUserId(), SessionUtil.getOrgId(),SessionUtil.getYear(),SessionUtil.getToken());

						this.setVisible(false);
					});
				} else {
					System.out.println(jsonResponse.get("message").getAsString());
					NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
			}
		});
	}
	
	private void rebuild() {
		this.removeAll();
		
		JsonObject jsonUser = jsonTask.get("assignee").getAsJsonObject();
		
		UserAssignInfoComponent comUserAssginee = new UserAssignInfoComponent(jsonUser,null,null);
		this.add(comUserAssginee);
		
		TaskTitleComponent comTaskTitle = new TaskTitleComponent(jsonTask);
		this.add(comTaskTitle);
		
//		TaskContentComponent comTaskContent = new TaskContentComponent(jsonTask);
//		this.add(comTaskContent);
		
		JsonArray jsonUserSupport = jsonTask.get("followersTask").getAsJsonArray();
		TaskSupportComponent comUserSupport = new TaskSupportComponent(jsonUserSupport);
		this.add(comUserSupport);
		
		TaskDateComponent comTaskDate = new TaskDateComponent(jsonTask,null,null);
		this.add(comTaskDate);
		
		comTaskAction = new TaskActionComponent(jsonTask,null,true);
		this.add(comTaskAction);
		
		//comTaskAction.gethComment().setVisible(false);
		
		configComponent();
	}
}
