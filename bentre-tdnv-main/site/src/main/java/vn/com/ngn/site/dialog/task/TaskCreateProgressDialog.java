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
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;

public class TaskCreateProgressDialog extends DialogTemplate{
	private IntegerField txtPercent = new IntegerField("Phần trăm tiến độ");
	private TextArea txtExplain = new TextArea("Diễn giải tiến độ");
	private UploadModuleWithDescription uploadModule = new UploadModuleWithDescription();
	private Button btnSave = new Button("Cập nhật tiến độ",VaadinIcon.PROGRESSBAR.create());
	
	private Button btnTrigger = new Button();

	private String taskId;
	
	public TaskCreateProgressDialog(String taskId) {
		this.taskId = taskId;

		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		super.buildLayout();
		caption.setText("Cập nhật tiến độ nhiệm vụ");
		
		vMain.add(txtPercent);
		vMain.add(txtExplain);
		vMain.add(uploadModule);
		vMain.add(btnSave);
		vMain.add(btnTrigger);
		
		txtPercent.setHelperText("Phần trăm tiến độ thực hiện được có giá trị từ 0% -> 100%");
		
		btnTrigger.setVisible(false);

		txtPercent.setWidthFull();
		txtExplain.setWidthFull();
		uploadModule.setWidthFull();

		txtPercent.setMin(0);
		txtPercent.setMax(100);
		txtPercent.setValue(0);

		txtExplain.setHeight("200px");

		btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		vMain.setHorizontalComponentAlignment(Alignment.END, btnSave);

		this.setWidth("700px");

		configUpload();
	}

	@Override
	public void configComponent() {
		super.configComponent();
		btnSave.addClickListener(e->{
			if(validateForm()) {
				int percent = txtPercent.getValue();
				String explain = txtExplain.getValue().trim();
				List<UploadModuleDataWithDescriptionModel> listAttachment = uploadModule.getListFileUpload();

				try {
					JsonObject jsonResponse = TaskServiceUtil.createProgresss(taskId, percent, explain, listAttachment);

					if(jsonResponse.get("status").getAsInt()==201) {
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
		if(txtPercent.isEmpty()) {
			NotificationUtil.showNotifi("Vui lòng nhập vào phần trăm tiến độ", NotificationTypeEnum.WARNING);
			txtPercent.focus();
			return false;
		} else if(txtPercent.getValue()<0 || txtPercent.getValue()>100) {
			NotificationUtil.showNotifi("Giá trị % tiến độ chỉ từ 0 -> 100%", NotificationTypeEnum.WARNING);
			txtPercent.focus();
			return false;
		}
		if(txtExplain.isEmpty()) {
			NotificationUtil.showNotifi("Vui lòng nhập vào diễn giải tiến độ", NotificationTypeEnum.WARNING);
			txtExplain.focus();
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
	public void setPercentInit(int percent) {
		txtPercent.setValue(percent);
	}
}