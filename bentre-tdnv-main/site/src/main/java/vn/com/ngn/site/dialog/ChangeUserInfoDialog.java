package vn.com.ngn.site.dialog;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;

import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.model.UserModel;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.UserServiceUtil;

@SuppressWarnings("serial")
public class ChangeUserInfoDialog extends DialogTemplate{
	private TextField txtUsername = new TextField("Tên tài khoản");
	private EmailField txtEmail = new EmailField("Địa chỉ Email");
	private TextField txtFullname = new TextField("Họ và tên");
	private TextField txtJobTile = new TextField("Chức vụ");
	private TextField txtDominoAccount = new TextField("Domino Account");
	private Button btnSave = new Button("Cập nhật thông tin",VaadinIcon.INFO.create());
	
	public ChangeUserInfoDialog() {
		buildLayout();
		configComponent();
		
		initValue();
	}
	
	public void initValue() {
		txtUsername.setValue(SessionUtil.getUser().getUsername());
		txtEmail.setValue(SessionUtil.getUser().getEmail());
		txtFullname.setValue(SessionUtil.getUser().getFullname());
		txtJobTile.setValue(SessionUtil.getUser().getJobTitle()+"");
		txtDominoAccount.setValue(SessionUtil.getUser().getAccountDomino()!=null?SessionUtil.getUser().getAccountDomino():"Chưa được liên kết");
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		caption.setText("Thay đổi thông tin tài khoản");
		
		vMain.add(txtUsername);
		vMain.add(txtEmail);
		vMain.add(txtFullname);
		vMain.add(txtJobTile);
		vMain.add(txtDominoAccount);
		vMain.add(btnSave);
		
		txtUsername.setEnabled(false);
		
		txtUsername.setWidthFull();
		txtEmail.setWidthFull();
		txtFullname.setWidthFull();
		txtJobTile.setWidthFull();
		txtDominoAccount.setWidthFull();
		
		txtJobTile.setEnabled(false);
		txtDominoAccount.setEnabled(false);

		btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		
		vMain.setHorizontalComponentAlignment(Alignment.END, btnSave);
		
		this.setWidth("400px");
	}

	@Override
	public void configComponent() {
		super.configComponent();
		btnSave.addClickListener(e->{
			if(validateForm()) {
				String username = txtUsername.getValue().trim();
				String email = txtEmail.getValue().trim();
				String fullname = txtFullname.getValue().trim();
				String jobTitle = txtJobTile.getValue().trim();
				
				UserModel modelUser = new UserModel();
				modelUser.setUsername(username);
				modelUser.setEmail(email);
				modelUser.setFullname(fullname);
				modelUser.setJobTitle(jobTitle);
				
				try {
					JsonObject jsonObject = UserServiceUtil.changeUserInfo(modelUser);
					
					int respCode = jsonObject.get("status").getAsInt();
					
					if(respCode==200) {
						SessionUtil.getUser().setEmail(email);
						SessionUtil.getUser().setFullname(fullname);
						
						NotificationUtil.showNotifi("Thay đổi thông tin tài khoản thành công.", NotificationTypeEnum.SUCCESS);
					} else {
						NotificationUtil.showNotifi("Có lỗi xảy ra!! Vui lòng thử lại.", NotificationTypeEnum.ERROR);
					}
					System.out.println(jsonObject);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	public boolean validateForm() {
		if(txtEmail.isEmpty()) {
			NotificationUtil.showNotifi("Vui lòng nhập vào địa chỉ email", NotificationTypeEnum.WARNING);
			txtEmail.focus();
			return false;
		} else if(txtEmail.isInvalid()) {
			NotificationUtil.showNotifi("Địa chỉ email không chính xác", NotificationTypeEnum.WARNING);
			txtEmail.focus();
			return false;
		}
		if(txtFullname.isEmpty()) {
			NotificationUtil.showNotifi("Vui lòng nhập vào họ tên", NotificationTypeEnum.WARNING);
			txtFullname.focus();
			return false;
		}
			
		return true;
	}
}