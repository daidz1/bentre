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
import vn.com.ngn.site.model.taskcreate.TaskAssigneeOrgModel;
import vn.com.ngn.site.model.taskcreate.TaskAssigneeUserModel;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;
import vn.com.ngn.site.views.taskcreate.TaskAssigneeOrgLayout;
import vn.com.ngn.site.views.taskcreate.TaskSupportOrgLayout;


public class EditOrgGroupDialog extends DialogTemplate{
private SplitLayout splitLayout = new SplitLayout();
	
	private VerticalLayout vLeft = new VerticalLayout();
	private TextField txtName = new TextField("Tên nhóm");
	private TextArea txtDescription = new TextArea("Mô tả");
	
	private VerticalLayout vRight = new VerticalLayout();
	private TaskAssigneeOrgLayout taskAssigneeOrgLayout;
	private TaskSupportOrgLayout taskSupportOrgLayout;
	
	private HorizontalLayout hAction = new HorizontalLayout();
	private Button btnSaveTask = new  Button("Tạo nhóm",VaadinIcon.PLUS.create());
	
	private JsonObject jsonTask;
	
	private Button btnTrigger = new Button();
	
    public EditOrgGroupDialog(JsonObject jsonTask) {
    	this.jsonTask = jsonTask;
    	
    	if(jsonTask!=null) {
    		caption.setText("Cập nhật nhóm giao việc");
    		
    		String name = jsonTask.get("name").getAsString(); 
    		String description = !jsonTask.get("description").isJsonNull() ? jsonTask.get("description").getAsString() : ""; 
    		
    		txtName.setValue(name);
    		txtDescription.setValue(description);
    		
    		taskAssigneeOrgLayout = new TaskAssigneeOrgLayout(jsonTask.getAsJsonObject("assigneeTask"));
			taskSupportOrgLayout = new TaskSupportOrgLayout(jsonTask.getAsJsonArray("followersTask"));
    		
    		btnSaveTask.setText("Cập nhật nhóm");
    	} else {
    		caption.setText("Thêm nhóm giao việc");
    		
    		taskAssigneeOrgLayout = new TaskAssigneeOrgLayout(null);
			taskSupportOrgLayout = new TaskSupportOrgLayout(null);
    	}
    	
    	buildLayout();
    	configComponent();
    }

	@Override
	public void buildLayout() {
		super.buildLayout();
		txtName.setRequiredIndicatorVisible(true);
		
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
					if(taskAssigneeOrgLayout.validateForm()) {
						String name = txtName.getValue();
						String description = txtDescription.getValue();
						TaskAssigneeUserModel modelUserCreator = new TaskAssigneeUserModel();
						TaskAssigneeOrgModel modelOrgAssignee = taskAssigneeOrgLayout.getOrgModel();
						List<TaskAssigneeOrgModel> listOrgSupport = new ArrayList<TaskAssigneeOrgModel>(taskSupportOrgLayout.getMapOrg().values());
					
						modelUserCreator.setIdUser(SessionUtil.getUserId());
						modelUserCreator.setFullName(SessionUtil.getUser().getFullname());
						modelUserCreator.setIdOrg(SessionUtil.getOrgId());
						modelUserCreator.setOrgName(SessionUtil.getOrg().getName());
						
						try {
							JsonObject jsonResponse = TaskServiceUtil.createOrgGroup(name, description, modelUserCreator, modelOrgAssignee, listOrgSupport);
							
							if(jsonResponse.get("status").getAsInt()==201) {
								NotificationUtil.showNotifi("Thêm nhóm cơ quan/đơn vị thành công.", NotificationTypeEnum.SUCCESS);
								
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
					if(taskAssigneeOrgLayout.validateForm()) {
						String id = jsonTask.get("id").getAsString();
						String name = txtName.getValue();
						String description = txtDescription.getValue();
						TaskAssigneeUserModel modelUserCreator = new TaskAssigneeUserModel();
						TaskAssigneeOrgModel modelOrgAssignee = taskAssigneeOrgLayout.getOrgModel();
						List<TaskAssigneeOrgModel> listOrgSupport = new ArrayList<TaskAssigneeOrgModel>(taskSupportOrgLayout.getMapOrg().values());
					
						modelUserCreator.setIdUser(SessionUtil.getUserId());
						modelUserCreator.setFullName(SessionUtil.getUser().getFullname());
						modelUserCreator.setIdOrg(SessionUtil.getOrgId());
						modelUserCreator.setOrgName(SessionUtil.getOrg().getName());
						
						try {
							JsonObject jsonResponse = TaskServiceUtil.updateOrgGroup(id,name, description, modelUserCreator, modelOrgAssignee, listOrgSupport);
							
							if(jsonResponse.get("status").getAsInt()==200) {
								NotificationUtil.showNotifi("Cập nhật nhóm cơ quan/đơn vị thành công.", NotificationTypeEnum.SUCCESS);
								
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
		
		taskAssigneeOrgLayout.setTaskSupportOrgLayout(taskSupportOrgLayout);
		taskSupportOrgLayout.setTaskAssigneeOrgLayout(taskAssigneeOrgLayout);
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
		vRight.add(taskAssigneeOrgLayout);
		vRight.add(taskSupportOrgLayout);
		
		vRight.setWidthFull();
	}
	
	private boolean validateForm() {
		if(txtName.isEmpty()) {
			txtName.focus();
			txtName.setErrorMessage("Tên mẫu cơ quan/đơn vị không được để trống");
			txtName.setInvalid(true);
			NotificationUtil.showNotifi("Tên mẫu cơ quan/đơn vị không được để trống", NotificationTypeEnum.WARNING);
			return false;
		}
		
		return true;
	}

	public Button getBtnTrigger() {
		return btnTrigger;
	}
}
