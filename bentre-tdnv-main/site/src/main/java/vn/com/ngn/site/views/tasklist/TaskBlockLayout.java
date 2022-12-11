package vn.com.ngn.site.views.tasklist;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.dialog.task.TaskAttachmentDialog;
import vn.com.ngn.site.dialog.task.TaskCommentDialog;
import vn.com.ngn.site.dialog.task.TaskDetailDialog;
import vn.com.ngn.site.dialog.task.TaskEventDialog;
import vn.com.ngn.site.dialog.task.TaskProgressDialog;
import vn.com.ngn.site.dialog.task.TaskSubTaskDialog;
import vn.com.ngn.site.enums.DisplayConfigEnum;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.enums.TaskTypeEnum;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.UIUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;
import vn.com.ngn.site.views.tasklist.component.TaskActionComponent;
import vn.com.ngn.site.views.tasklist.component.TaskContentComponent;
import vn.com.ngn.site.views.tasklist.component.TaskDateComponent;
import vn.com.ngn.site.views.tasklist.component.TaskSupportComponent;
import vn.com.ngn.site.views.tasklist.component.TaskTagComponent;
import vn.com.ngn.site.views.tasklist.component.TaskTitleComponent;
import vn.com.ngn.site.views.tasklist.component.UserAssignInfoComponent;
import vn.com.ngn.site.views.tasklist.component.UserOwnerInfoComponent;

@SuppressWarnings("serial")
public class TaskBlockLayout extends VerticalLayout implements LayoutInterface{
	private JsonObject jsonTask;
	
	private String eType;
	private String eStatus;
	private TaskActionComponent comTaskAction;
	private boolean isDetail = false;
	private Button btnDropdown = new Button(VaadinIcon.ANGLE_RIGHT.create());
	private Class<? extends Component> classCall;
	private Button btnTrigger = new Button();
	
	public TaskBlockLayout(JsonObject jsonTask,String eType, String eStatus,Class<? extends Component> classCall) {
		this.jsonTask = jsonTask;
		this.eType = eType;
		this.eStatus = eStatus;
		this.classCall = classCall;
		
		buildLayout();
	}

	@Override
	public void buildLayout() {
		this.setSpacing(false);
		this.setWidthFull();
		this.addClassName("task-block");
		
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

		comTaskAction.gethSubtask().addClickListener(e->{
			TaskSubTaskDialog dialogSubtask = new TaskSubTaskDialog(jsonTask.get("id").getAsString());
			
			dialogSubtask.open();
		});
		
		comTaskAction.gethEvent().addClickListener(e->{
			TaskEventDialog dialogEvent = new TaskEventDialog(jsonTask.get("id").getAsString());
			
			dialogEvent.open();
		});
		
		comTaskAction.gethDetail().addClickListener(e->{
			try {
				JsonObject jsonResponse = TaskServiceUtil.getTaskDetail(jsonTask.get("id").getAsString());
				
				if(jsonResponse.get("status").getAsInt()==200) {
					TaskDetailDialog dialogTask = new TaskDetailDialog(jsonResponse.getAsJsonObject("result"), eType, eStatus);
					
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
		
		isDetail = true;
		btnDropdown = new Button(VaadinIcon.ANGLE_DOWN.create());
		btnDropdown.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		btnDropdown.getStyle().set("margin-right", "-13px");
		
		HorizontalLayout hUser = new HorizontalLayout(btnDropdown);
		hUser.setWidthFull();
		hUser.setDefaultVerticalComponentAlignment(Alignment.CENTER);

		this.add(hUser);
		if(eType==null || eType == TaskTypeEnum.THEODOI.getKey() || eType == TaskTypeEnum.GIAOVIECTHAY.getKey()) {
			JsonObject jsonUserOwner = jsonTask.get("owner").getAsJsonObject();
			JsonObject jsonUserAssign = jsonTask.get("assignee").getAsJsonObject();
			
			UserOwnerInfoComponent comUserOwner = new UserOwnerInfoComponent(jsonUserOwner,eType,classCall);
			UserAssignInfoComponent comUserAssginee = new UserAssignInfoComponent(jsonUserAssign,eType,classCall);

			comUserOwner.getStyle().set("width", "unset");
			comUserAssginee.getStyle().set("width", "unset");
			
			hUser.add(comUserAssginee,comUserOwner);
		} else if(eType == TaskTypeEnum.DAGIAO.getKey()) {
			JsonObject jsonUser = jsonTask.get("assignee").getAsJsonObject();
			
			UserAssignInfoComponent comUserAssginee = new UserAssignInfoComponent(jsonUser,eType,classCall);
			hUser.add(comUserAssginee);
		} else if(eType == TaskTypeEnum.DUOCGIAO.getKey()) {
			JsonObject jsonUser = jsonTask.get("owner").getAsJsonObject();
			
			UserOwnerInfoComponent comUserOwner = new UserOwnerInfoComponent(jsonUser,eType,classCall);
			hUser.add(comUserOwner);
		} if(eType == TaskTypeEnum.THEODOITHAY.getKey()) {
			JsonObject jsonUserAssist = jsonTask.get("assistantTask").getAsJsonObject();
			JsonObject jsonUserAssign = jsonTask.get("assignee").getAsJsonObject();
			
			UserOwnerInfoComponent comUserAssistant = new UserOwnerInfoComponent(jsonUserAssist,eType,classCall);
			UserAssignInfoComponent comUserAssginee = new UserAssignInfoComponent(jsonUserAssign,eType,classCall);

			comUserAssistant.getStyle().set("width", "unset");
			comUserAssginee.getStyle().set("width", "unset"); 
			
			hUser.add(comUserAssistant,comUserAssginee);
		}
		
		TaskTitleComponent comTaskTitle = new TaskTitleComponent(jsonTask);
		this.add(comTaskTitle);
		
		TaskContentComponent comTaskContent = new TaskContentComponent(jsonTask);
		this.add(comTaskContent);
		
		JsonArray jsonUserSupport = jsonTask.get("followersTask").getAsJsonArray();
		TaskSupportComponent comUserSupport = new TaskSupportComponent(jsonUserSupport);
		this.add(comUserSupport);
		
		TaskDateComponent comTaskDate = new TaskDateComponent(jsonTask,eType,eStatus);
		this.add(comTaskDate);
		
		comTaskAction = new TaskActionComponent(jsonTask,eType,false);
		this.add(comTaskAction);
		
		TaskTagComponent comTaskTag = new TaskTagComponent(jsonTask.get("id").getAsString());
		this.add(comTaskTag);
		
		configComponent();
//		comTaskContent.setVisible(false);
//		comUserSupport.setVisible(false);
		
		btnDropdown.addClickListener(e->{
			if(isDetail) {
				btnDropdown.setIcon(VaadinIcon.ANGLE_RIGHT.create());
			} else {
				btnDropdown.setIcon(VaadinIcon.ANGLE_DOWN.create());
			}
			
			isDetail = !isDetail;
			
			comTaskContent.setVisible(isDetail);
			comUserSupport.setVisible(isDetail);
			comTaskTag.setVisible(isDetail);
		});
		
		if(eType!=null) {
			if(eType.equals(TaskTypeEnum.DAGIAO.getKey())) {
				if(!SessionUtil.statusOfDisplayConfig(DisplayConfigEnum.dagiaoview_expand_task_info)) {
					btnDropdown.click();
				}
			} else if(eType.equals(TaskTypeEnum.DUOCGIAO.getKey())) {
				if(!SessionUtil.statusOfDisplayConfig(DisplayConfigEnum.duocgiaoview_expand_task_info)) {
					btnDropdown.click();
				}
			} else if(eType.equals(TaskTypeEnum.THEODOI.getKey())) {
				if(!SessionUtil.statusOfDisplayConfig(DisplayConfigEnum.hotroview_expand_task_info)) {
					btnDropdown.click();
				}
			}
		}
	}
}
