package vn.com.ngn.site.dialog.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.dialog.DialogTemplate;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.model.UploadModuleDataWithDescriptionModel;
import vn.com.ngn.site.module.upload.UploadModuleWithDescription;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;

public class TaskCompleteDialog extends DialogTemplate{
	private String taskId;
	private Button btnComplete = new Button("Hoàn thành nhiệm vụ", VaadinIcon.CHECK_CIRCLE.create());
	private List<String> listTaskToComplete = new ArrayList<String>();

	private VerticalLayout vProgress = new VerticalLayout();
	private IntegerField txtPercent = new IntegerField("Phần trăm tiến độ");
	private TextArea txtExplain = new TextArea("Diễn giải tiến độ");
	private UploadModuleWithDescription uploadModule = new UploadModuleWithDescription();

	private Button btnTrigger = new Button();

	private boolean isUpdateProgressTo100 = false;

	public TaskCompleteDialog(String taskId) {
		this.taskId = taskId;

		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		super.buildLayout();

		caption.setText("Hoàn thành nhiệm vụ");

		String strDisplay = "";
		String strTaskInComplete = "";
		try {
			JsonObject jsonResponse = TaskServiceUtil.getSubTaskList(taskId);
			if(jsonResponse.get("status").getAsInt()==200) {
				JsonArray jsonArrSubTask = jsonResponse.getAsJsonArray("result");

				if(jsonArrSubTask.size()>0) {
					strDisplay+="<div style='font-weight: 500;margin-bottom:10px;'>Bạn muốn hoàn thành nhiệm vụ này, sau khi xác nhận sẽ không thể cập nhật tiến độ của nhiệm vụ.</div>";
					for(JsonElement jsonEle : jsonArrSubTask) {
						JsonObject jsonSubTask = jsonEle.getAsJsonObject();

						long completeTime = jsonSubTask.get("completedTime").getAsLong();

						if(completeTime==0) {
							String subTaskId = jsonSubTask.get("id").getAsString();
							String title = jsonSubTask.get("title").getAsString();
							String assignee = jsonSubTask.getAsJsonObject("assignee").get("fullName").getAsString();

							listTaskToComplete.add(subTaskId);
							strTaskInComplete+="<div style='margin: 10px 0; background: #f1f1f1; padding: 6px 15px; border-radius: 14px;'>"
									+ "<div><b>Tiêu đề: </b>"+title+"</div>"
									+ "<div><b>Người xử lý: </b>"+assignee+"</div>"
									+ "</div>";
						}
					}

					if(listTaskToComplete.size()>0) {
						strDisplay+="<div style='font-weight: 500; color: red;'>Lưu ý, có "+listTaskToComplete.size()+" nhiệm vụ con sau đây chưa được hoàn thành, nếu xác nhận hoàn thành nhiệm vụ này sẽ tự động hoàn thành tất cả những nhiệm vụ con.</div>";
						strDisplay+=strTaskInComplete;
					}
					strDisplay="<div>"+strDisplay+"</div>";
				} else {
					strDisplay = "<div style='font-weight: 500;margin-bottom:10px;'>Bạn muốn hoàn thành nhiệm vụ này, sau khi xác nhận sẽ không thể cập nhật tiến độ của nhiệm vụ.</div>";
				}
			} else {
				System.out.println(jsonResponse.get("message").getAsString());
				NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
		}

		Html html = new Html(strDisplay);

		vMain.add(vProgress);
		vMain.add(html);
		vMain.add(btnComplete);
		vMain.add(btnTrigger);

		vProgress.setVisible(false);

		btnTrigger.setVisible(false);

		btnComplete.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		vMain.setHorizontalComponentAlignment(Alignment.END, btnComplete);

		if(listTaskToComplete.size()>0) {
			this.setWidth("700px");
		} else {
			this.setWidth("500px");
		}
	}

	@Override
	public void configComponent() {
		super.configComponent();

		btnComplete.addClickListener(e->{
			try {
				boolean isOk = true;
				if(isUpdateProgressTo100) {
					if(validateProgressForm()) {
						String explain = txtExplain.getValue().trim();
						List<UploadModuleDataWithDescriptionModel> listAttachment = uploadModule.getListFileUpload();

						JsonObject jsonResponse = TaskServiceUtil.createProgresss(taskId, 100, explain, listAttachment);

						if(jsonResponse.get("status").getAsInt()==201) {
							
						} else {
							System.out.println(jsonResponse);
							NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
						}
					} else {
						return;
					}
				}

				if(listTaskToComplete.size()>0) {
					for(String subTaskId : listTaskToComplete) {
						JsonObject jsonResponseSub = TaskServiceUtil.completeTask(subTaskId, SessionUtil.getUserId(), SessionUtil.getOrgId());

						if(jsonResponseSub.get("status").getAsInt()==200) {

						} else {
							System.out.println(jsonResponseSub);
							NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
							isOk=false;
							break;
						}
					}
				}
				if(isOk) {
					JsonObject jsonResponse = TaskServiceUtil.completeTask(taskId, SessionUtil.getUserId(), SessionUtil.getOrgId());

					if(jsonResponse.get("status").getAsInt()==200) {
						NotificationUtil.showNotifi("Hoàn thành nhiệm vụ thành công.", NotificationTypeEnum.SUCCESS);
						close();
						//BroadcasterUtil.broadcast(BroadcasterSupportUitl.createMessageOnTask(jsonTask));

						btnTrigger.click();
					} else {
						System.out.println(jsonResponse);
						NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}

	public void setUpdateProgress() {
		isUpdateProgressTo100 = true;

		vProgress.setVisible(true);
		vProgress.setPadding(false);

		vProgress.add(txtPercent);
		vProgress.add(txtExplain);
		vProgress.add(uploadModule);

		txtPercent.setWidthFull();
		txtExplain.setWidthFull();
		uploadModule.setWidthFull();

		txtExplain.setHeight("100px");

		uploadModule.setMultiFile(true);
		uploadModule.initUpload();
		
		txtPercent.setValue(100);
		txtPercent.setReadOnly(true);

		txtExplain.setValue("Đã hoàn thành nhiêm vụ.");
	}

	public boolean validateProgressForm() {
		if(txtExplain.isEmpty()) {
			NotificationUtil.showNotifi("Vui lòng nhập vào diễn giải tiến độ", NotificationTypeEnum.WARNING);
			txtExplain.focus();
			return false;
		}

		return true;
	}

	public Button getBtnTrigger() {
		return btnTrigger;
	}
}
