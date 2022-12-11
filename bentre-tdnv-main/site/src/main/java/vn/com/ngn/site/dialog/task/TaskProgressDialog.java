package vn.com.ngn.site.dialog.task;

import java.io.IOException;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

import vn.com.ngn.site.dialog.DialogTemplate;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.model.CustomPairModel;
import vn.com.ngn.site.model.TaskDetailStateForUser;
import vn.com.ngn.site.util.BroadcasterSupportUitl;
import vn.com.ngn.site.util.BroadcasterUtil;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;
import vn.com.ngn.site.views.tasklist.component.TaskProgressComponent;

@SuppressWarnings("serial")
public class TaskProgressDialog extends DialogTemplate{
	private Button btnUpdateProgress = new Button("Cập nhật tiến độ",VaadinIcon.PROGRESSBAR.create());
	private VerticalLayout vProgress = new VerticalLayout();
	
	private String taskId;
	
	private JsonObject jsonTask = new JsonObject();
	private JsonArray jsonArray = new JsonArray();
	
	private String userId = SessionUtil.getUserId();
	private String orgId = SessionUtil.getOrgId();
	private String token = SessionUtil.getToken();
	
	private Registration broadcasterRegistration;
	
	public TaskProgressDialog(String taskId) {
		this.taskId = taskId;
		
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		super.buildLayout();
		caption.setText("Danh sách tiến độ của nhiệm vụ");
		
		vMain.add(btnUpdateProgress);
		vMain.add(vProgress);
		try {
			rebuildLayout();
			setStateOfTask();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		vProgress.setPadding(false);
		
		this.setWidth("80%");
		//this.setMinHeight("500px");
	}

	@Override
	public void configComponent() {
		super.configComponent();
		btnUpdateProgress.addClickListener(e->{
			TaskCreateProgressDialog dialog = new TaskCreateProgressDialog(taskId);
			dialog.open();
			if(jsonArray.size()>0) {
				dialog.setPercentInit(jsonArray.get(0).getAsJsonObject().get("percent").getAsInt());
			}
			dialog.getBtnTrigger().addClickListener(eTrigger->{
				try {
					rebuildLayout();
					setStateOfTask();
					
					String messageBroadcast = BroadcasterSupportUitl.createMessageOnTask(jsonTask);
					messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.MAINVIEW);
					messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.TASKDETAIL);
					messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.PROGRESSDIALOG);
					
					BroadcasterUtil.broadcast(messageBroadcast);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			});
		});
	}
	
	private void rebuildLayout() throws IOException {
		vProgress.removeAll();
		
		JsonObject jsonResponseGet = TaskServiceUtil.getTaskDetail(taskId,token,userId,orgId);
		
		if(jsonResponseGet.get("status").getAsInt()==200) {
			jsonTask = jsonResponseGet.getAsJsonObject("result");
			jsonArray = jsonTask.getAsJsonArray("processes");
			
			TaskProgressComponent taskProgress = new TaskProgressComponent(jsonArray);				
			vProgress.add(taskProgress);
		} else {
			System.out.println(jsonResponseGet);
		}
	}
	
	public void setStateOfTask() {
		btnUpdateProgress.setVisible(false);
		
		TaskDetailStateForUser state = new TaskDetailStateForUser(userId,jsonTask);
		if(state.isOwner()) {
			if(state.isCompleted()) {
			} else {
				btnUpdateProgress.setVisible(true);
			}
		} else if(state.isAssignee()) {
			if(state.isCompleted()) {
			} else {
				btnUpdateProgress.setVisible(true);
			}
		}
	}
	
	@Override
	protected void onAttach(AttachEvent attachEvent) {
		String orgId = SessionUtil.getOrgId();
		int year = SessionUtil.getYear();

		UI ui = attachEvent.getUI();
		broadcasterRegistration = BroadcasterUtil.register(newMessage -> {
			ui.access(() -> {
				if(BroadcasterSupportUitl.checkHasOption(newMessage, BroadcasterSupportUitl.PROGRESSDIALOG)) {
					List<CustomPairModel<String, String>> listUserPair = BroadcasterSupportUitl.decodeMessageWithOnlyUser(BroadcasterSupportUitl.removeAllOption(newMessage));

					for(CustomPairModel<String, String> pair : listUserPair) {
						if(pair.getKey().equals(userId) && pair.getValue().equals(orgId)) {
							try {
								rebuildLayout();
								setStateOfTask();
								
								NotificationUtil.showNotifi("Có cập nhật tiến độ mới và cửa sổ hiện tại đã được làm mới.", NotificationTypeEnum.SUCCESS);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			});
		});
	}
	
	@Override
	protected void onDetach(DetachEvent detachEvent) {
		broadcasterRegistration.remove();
		broadcasterRegistration = null;
	}
}
