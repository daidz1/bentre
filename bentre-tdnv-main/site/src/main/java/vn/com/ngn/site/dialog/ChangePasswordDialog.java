package vn.com.ngn.site.dialog;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.PasswordField;


import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.util.GeneralUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.UserServiceUtil;

@SuppressWarnings("serial")
public class ChangePasswordDialog extends DialogTemplate {
	private PasswordField txtOldPW = new PasswordField("Mật khẩu cũ");
	private PasswordField txtNewPW = new PasswordField("Mật khẩu mới");
	private PasswordField txtNewPWCf = new PasswordField("Xác nhận mật khẩu mới");
	private Button btnSave = new Button("Đổi mật khẩu",VaadinIcon.KEY.create());
	
	public ChangePasswordDialog() {
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		caption.setText("Thay đổi mật khẩu");
		 
		vMain.add(txtOldPW);
		vMain.add(txtNewPW);
		vMain.add(txtNewPWCf);
		vMain.add(btnSave);
		
		txtOldPW.setWidthFull();
		txtNewPW.setWidthFull();
		txtNewPWCf.setWidthFull();

		btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		
		vMain.setHorizontalComponentAlignment(Alignment.END, btnSave);
		
		this.setWidth("400px");
	}

	@Override
	public void configComponent() {
		super.configComponent();
		btnSave.addClickListener(e->{
			if(validateForm()) {
				String oldPw = txtOldPW.getValue().trim();
				String newPw = txtNewPWCf.getValue().trim();
				
				try {
					JsonObject jsonObject = UserServiceUtil.changePassword(oldPw, newPw);
					
					int respCode = jsonObject.get("status").getAsInt();
					
					if(respCode==200) {
						NotificationUtil.showNotifi("Thay đổi mật khẩu thành công", NotificationTypeEnum.SUCCESS);
					} else if(respCode==409) {
						NotificationUtil.showNotifi(jsonObject.get("message").getAsString(), NotificationTypeEnum.ERROR);
					} else {
						NotificationUtil.showNotifi("Có lỗi xảy ra!! Vui lòng thử lại.", NotificationTypeEnum.ERROR);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	public boolean validateForm() {
		if(txtOldPW.isEmpty()) {
			NotificationUtil.showNotifi("Vui lòng nhập vào mật khẩu cũ", NotificationTypeEnum.WARNING);
			txtOldPW.focus();
			return false;
		}
		if(txtNewPW.isEmpty()) {
			NotificationUtil.showNotifi("Vui lòng nhập vào mật khẩu mới", NotificationTypeEnum.WARNING);
			txtNewPW.focus();
			return false;
		}else {
			if(GeneralUtil.checkPassword(txtNewPW.getValue().trim())==false){
				NotificationUtil.showNotifi("Mật khẩu phải gồm kí tự thường, hoa, đặc biệt và số",NotificationTypeEnum.WARNING);
				txtNewPW.focus();
				return false;
			}
		}
		
		if(txtNewPWCf.isEmpty()) {
			NotificationUtil.showNotifi("Vui lòng nhập vào xác nhận mật khẩu mới", NotificationTypeEnum.WARNING);
			txtNewPWCf.focus();
			return false;
		}
		if(!txtNewPW.getValue().trim().equals(txtNewPWCf.getValue().trim())) {
			NotificationUtil.showNotifi("Xác nhận mật khẩu mới không chính xác", NotificationTypeEnum.WARNING);
			txtNewPW.focus();
			return false;
		}
			
		return true;
	}
}