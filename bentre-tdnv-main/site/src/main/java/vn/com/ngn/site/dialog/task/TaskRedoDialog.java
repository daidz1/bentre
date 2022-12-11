package vn.com.ngn.site.dialog.task;

import java.io.IOException;
import java.util.List;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;

import vn.com.ngn.site.dialog.DialogTemplate;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.model.UploadModuleDataWithDescriptionModel;
import vn.com.ngn.site.module.upload.UploadModuleWithDescription;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;

@SuppressWarnings("serial")
public class TaskRedoDialog extends DialogTemplate{
	private TextArea txtReason = new TextArea("Lý do");
	private UploadModuleWithDescription uploadModule = new UploadModuleWithDescription();
	private Button btnSave = new Button("Xác nhận",VaadinIcon.REFRESH.create());
	
	private Button btnTrigger = new Button();

	private String taskId;
	
	public TaskRedoDialog(String taskId) {
		this.taskId = taskId;

		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		super.buildLayout();
		caption.setText("Làm lại nhiệm vụ");
		
		vMain.add(txtReason);
		vMain.add(btnSave);
		vMain.add(btnTrigger);
		
		btnTrigger.setVisible(false);

		txtReason.setWidthFull();
		uploadModule.setWidthFull();

		txtReason.setHeight("200px");

		btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		vMain.setHorizontalComponentAlignment(Alignment.END, btnSave);

		this.setWidth("700px");

		configUpload();
	}

	@Override
	public void configComponent() {
		btnSave.addClickListener(e->{
			if(validateForm()) {
				String reason = txtReason.getValue().trim();
				try {
					JsonObject jsonResponse = TaskServiceUtil.redoTask(taskId, SessionUtil.getUserId(), SessionUtil.getOrgId(),reason);

					if(jsonResponse.get("status").getAsInt()==200) {
						NotificationUtil.showNotifi("Cập nhật tiến độ thành công.", NotificationTypeEnum.SUCCESS);
						btnTrigger.click();
						close();
					} else {
						System.out.println(jsonResponse);
						NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	private void configUpload() {
		uploadModule.setMultiFile(true);
		uploadModule.initUpload();
	}

	public boolean validateForm() {
		if(txtReason.isEmpty()) {
			NotificationUtil.showNotifi("Vui lòng nhập vào lý do làm lại nhiệm vụ.", NotificationTypeEnum.WARNING);
			txtReason.focus();
			return false;
		}

		return true;
	}

	public Button getBtnSave() {
		return btnSave;
	}
	public Button getBtnTrigger() {
		return btnTrigger;
	}
}