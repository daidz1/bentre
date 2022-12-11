package vn.com.ngn.site.dialog.task;

import java.io.IOException;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.dialog.DialogTemplate;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.model.CustomPairModel;
import vn.com.ngn.site.util.BroadcasterSupportUitl;
import vn.com.ngn.site.util.BroadcasterUtil;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;
import vn.com.ngn.site.views.tasklist.component.TaskCommentComponent;

@SuppressWarnings("serial")
public class TaskCommentDialog extends DialogTemplate{
	private String taskId;
	private TaskCommentComponent comComment;
	
	private Registration broadcasterRegistration;
	
	public TaskCommentDialog(String taskId) {
		this.taskId = taskId;
		
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		super.buildLayout();
		caption.setText("Trao đổi ý kiến của nhiệm vụ");
		try {
			JsonObject jsonResponseGet = TaskServiceUtil.getTaskDetail(taskId);
			
			if(jsonResponseGet.get("status").getAsInt()==200) {
				JsonObject jsonObject = jsonResponseGet.getAsJsonObject("result");
				JsonArray jsonArray = jsonObject.get("comments").getAsJsonArray();
				
				comComment = new TaskCommentComponent(taskId, jsonObject, jsonArray,true,SessionUtil.getToken());
				
				vMain.add(comComment);
			} else {
				System.out.println(jsonResponseGet);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.setWidth("80%");
		this.setMinHeight("500px");
	}

	@Override
	public void configComponent() {
		super.configComponent();
	}
	
	@Override
	protected void onAttach(AttachEvent attachEvent) {
		String userId = SessionUtil.getUserId();
		String orgId = SessionUtil.getOrgId();
		int year = SessionUtil.getYear();
		String token = SessionUtil.getToken();

		UI ui = attachEvent.getUI();
		broadcasterRegistration = BroadcasterUtil.register(newMessage -> {
			ui.access(() -> {
				if(BroadcasterSupportUitl.checkHasOption(newMessage, BroadcasterSupportUitl.COMMENTDIALOG)) {
					List<CustomPairModel<String, String>> listUserPair = BroadcasterSupportUitl.decodeMessageWithOnlyUser(BroadcasterSupportUitl.removeAllOption(newMessage));

					for(CustomPairModel<String, String> pair : listUserPair) {
						if(pair.getKey().equals(userId) && pair.getValue().equals(orgId)) {
							try {
								comComment.reload();
								
								NotificationUtil.showNotifi("Có trao đổi ý kiến mới và cửa sổ hiện tại đã được làm mới.", NotificationTypeEnum.SUCCESS);
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
