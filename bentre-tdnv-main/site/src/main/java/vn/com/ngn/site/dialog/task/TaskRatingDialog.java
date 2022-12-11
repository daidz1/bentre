package vn.com.ngn.site.dialog.task;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.TextArea;

import vn.com.ngn.site.dialog.DialogTemplate;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.module.RatingModule;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;

public class TaskRatingDialog extends DialogTemplate{
	private RatingModule ratingModule = new RatingModule();
	private TextArea txtDesciption = new TextArea("Lời nhận xét");
	private Button btnConfirm = new Button("Đánh giá",VaadinIcon.STAR.create());
	
	private String taskId;
	
	private Button btnTrigger = new Button();
	
	public TaskRatingDialog(String taskId) {
		this.taskId = taskId;
		
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		super.buildLayout();
		
		caption.setText("Đánh giá nhiệm vụ");
		
		vMain.add(ratingModule);
		vMain.add(txtDesciption);
		vMain.add(btnConfirm);
		vMain.add(btnTrigger);
		
		txtDesciption.setHeight("150px");
		txtDesciption.setWidthFull();
		
		btnTrigger.setVisible(false);
		
		btnConfirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		
		vMain.setHorizontalComponentAlignment(Alignment.END, btnConfirm);
		
		this.setWidth("450px");
	}

	@Override
	public void configComponent() {
		super.configComponent();
		
		btnConfirm.addClickListener(e->{
			if(validate()) {
				int star = ratingModule.getStar();
				String comment = txtDesciption.getValue().trim();
				System.out.println(star);
				try {
					JsonObject jsonResponse = TaskServiceUtil.ratingTask(taskId, star, comment);

					if(jsonResponse.get("status").getAsInt()==201) {
						NotificationUtil.showNotifi("Đánh giá nhiệm vụ thành công.", NotificationTypeEnum.SUCCESS);
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
	
	private boolean validate() {
		if(txtDesciption.isEmpty()) {
			txtDesciption.focus();
			
			NotificationUtil.showNotifi("Vui lòng nhập vào nhận xét", NotificationTypeEnum.WARNING);
			return false;
		}
		
		return true;
	}

	public Button getBtnTrigger() {
		return btnTrigger;
	}
}
