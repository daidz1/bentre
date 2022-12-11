package vn.com.ngn.site.dialog.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import vn.com.ngn.site.dialog.DialogTemplate;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.model.taskcreate.TaskAssigneeUserModel;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;
import vn.com.ngn.site.views.taskcreate.TaskAssgineeLayout;
import vn.com.ngn.site.views.taskcreate.TaskSupportLayout;

public class EditUserGroupDialog extends DialogTemplate{
private SplitLayout splitLayout = new SplitLayout();
	
	private VerticalLayout vLeft = new VerticalLayout();
	private TextField txtName = new TextField("Tên nhóm");
	private TextArea txtDescription = new TextArea("Mô tả");
	
	private VerticalLayout vRight = new VerticalLayout();
	private TaskAssgineeLayout taskAssgineeLayout;
	private TaskSupportLayout taskSupportLayout;
	
	private HorizontalLayout hAction = new HorizontalLayout();
	private Button btnSaveTask = new  Button("Tạo nhóm",VaadinIcon.PLUS.create());
	
	private JsonObject jsonTask;
	
	private Button btnTrigger = new Button();
	
    public EditUserGroupDialog(JsonObject jsonTask) {
    	this.jsonTask = jsonTask;
    	
    	if(jsonTask!=null) {
    		caption.setText("Cập nhật nhóm giao việc");
    		
    		String name = jsonTask.get("name").getAsString(); 
    		String description = !jsonTask.get("description").isJsonNull() ? jsonTask.get("description").getAsString() : ""; 
    		
    		txtName.setValue(name);
    		txtDescription.setValue(description);
    		
    		taskAssgineeLayout = new TaskAssgineeLayout(jsonTask.getAsJsonObject("assigneeTask"));
			taskSupportLayout = new TaskSupportLayout(jsonTask.getAsJsonArray("followersTask"));
    		
    		btnSaveTask.setText("Cập nhật nhóm");
    	} else {
    		caption.setText("Thêm nhóm giao việc");
    		
			taskAssgineeLayout = new TaskAssgineeLayout(null);
			taskSupportLayout = new TaskSupportLayout(null);
    	}
    	
    	buildLayout();
    	configComponent();
    }

	@Override
	public void buildLayout() {
		super.buildLayout();
		vMain.add(splitLayout);
		vMain.add(btnTrigger);
		
		btnTrigger.setVisible(false);
		
		splitLayout.addToPrimary(vLeft);
		splitLayout.addToSecondary(vRight);
		
		splitLayout.setSplitterPosition(55);
		splitLayout.setSizeFull();
		
		buildLeftLayout();
		buildRightLayout();
		
		this.setWidth("80%");
		this.setMinHeight("80%");
	}

	@Override
	public void configComponent() {
		super.configComponent();
		btnSaveTask.addClickListener(e->{
			if(jsonTask==null) {
				if(validateForm()) {
					if(taskAssgineeLayout.validateForm()) {
						String name = txtName.getValue();
						String description = txtDescription.getValue();
						TaskAssigneeUserModel modelUserCreator = new TaskAssigneeUserModel();
						TaskAssigneeUserModel modelUserAssignee = taskAssgineeLayout.getModelUser();
						List<TaskAssigneeUserModel> listUserSupport = new ArrayList<TaskAssigneeUserModel>(taskSupportLayout.getMapUser().values());
					
						modelUserCreator.setIdUser(SessionUtil.getUserId());
						modelUserCreator.setFullName(SessionUtil.getUser().getFullname());
						modelUserCreator.setIdOrg(SessionUtil.getOrgId());
						modelUserCreator.setOrgName(SessionUtil.getOrg().getName());
						
						try {
							JsonObject jsonResponse = TaskServiceUtil.createUserGroup(name, description, modelUserCreator, modelUserAssignee, listUserSupport);
							
							if(jsonResponse.get("status").getAsInt()==201) {
								NotificationUtil.showNotifi("Thêm nhóm người dùng thành công.", NotificationTypeEnum.SUCCESS);
								
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
				}
			} else{
				if(validateForm()) {
					if(taskAssgineeLayout.validateForm()) {
						String id = jsonTask.get("id").getAsString();
						String name = txtName.getValue();
						String description = txtDescription.getValue();
						TaskAssigneeUserModel modelUserCreator = new TaskAssigneeUserModel();
						TaskAssigneeUserModel modelUserAssignee = taskAssgineeLayout.getModelUser();
						List<TaskAssigneeUserModel> listUserSupport = new ArrayList<TaskAssigneeUserModel>(taskSupportLayout.getMapUser().values());
					
						modelUserCreator.setIdUser(SessionUtil.getUserId());
						modelUserCreator.setFullName(SessionUtil.getUser().getFullname());
						modelUserCreator.setIdOrg(SessionUtil.getOrgId());
						modelUserCreator.setOrgName(SessionUtil.getOrg().getName());
						
						try {
							JsonObject jsonResponse = TaskServiceUtil.updateUserGroup(id,name, description, modelUserCreator, modelUserAssignee, listUserSupport);
							
							if(jsonResponse.get("status").getAsInt()==200) {
								NotificationUtil.showNotifi("Cập nhật nhóm người dùng thành công.", NotificationTypeEnum.SUCCESS);
								
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
				}
			}
		});
		
		taskAssgineeLayout.setTaskSupportLayout(taskSupportLayout);
		taskSupportLayout.setTaskAssgineeLayout(taskAssgineeLayout);
	}
	
	private void buildLeftLayout() {
		vLeft.add(txtName);
		vLeft.add(txtDescription);
		vLeft.add(hAction);
		
		txtName.setWidthFull();
		txtDescription.setWidthFull();
		
		txtDescription.setHeight("150px");
		
		hAction.add(btnSaveTask);
		hAction.setAlignItems(Alignment.END);
		
		btnSaveTask.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		
		vLeft.setHorizontalComponentAlignment(Alignment.END, hAction);
		vLeft.setWidthFull();
	}
	
	private void buildRightLayout() {
		vRight.add(taskAssgineeLayout);
		vRight.add(taskSupportLayout);
		
		vRight.setWidthFull();
	}
	
	private boolean validateForm() {
		if(txtName.isEmpty()) {
			txtName.focus();
			
			NotificationUtil.showNotifi("Tên mẫu người dùng không được để trống", NotificationTypeEnum.WARNING);
			return false;
		}
		
		return true;
	}

	public Button getBtnTrigger() {
		return btnTrigger;
	}
}
