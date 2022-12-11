package vn.com.ngn.site.views.taskcreate;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.dialog.task.TaskOwnerDialog;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.model.taskcreate.TaskAssigneeUserModel;
import vn.com.ngn.site.util.component.HeaderUtil;
import vn.com.ngn.site.util.component.NotificationUtil;

@SuppressWarnings("serial")
public class TaskOwnerLayout extends VerticalLayout implements LayoutInterface{
	private HorizontalLayout hCaption = HeaderUtil.createHeader5WithBackground(VaadinIcon.USER_HEART.create(),"Cán bộ giao nhiệm vụ","rgb(162 27 58)","rgb(162 27 58 / 9%)");
	private Button btnChooseUser = new Button("Chọn cán bộ giao nhiệm vụ",VaadinIcon.POINTER.create());
	private VerticalLayout vDisplay = new VerticalLayout();
	
	private TaskAssigneeUserModel modelUser;
	
	private JsonObject jsonInfo;
	
	public TaskOwnerLayout(JsonObject jsonInfo) {
		this.jsonInfo = jsonInfo;
		
		if(jsonInfo!=null)
			initOldValue();
		
		buildLayout();
		configComponent();
	}
	
	public void initOldValue() {
		btnChooseUser.setVisible(false);
		
		JsonObject jsonAssignee = jsonInfo.getAsJsonObject("assignee");
		String userId = jsonAssignee.get("userId").getAsString();
		String fullName = jsonAssignee.get("fullName").getAsString();
		String organizationId = jsonAssignee.get("organizationId").getAsString();
		String organizationName = jsonAssignee.get("organizationName").getAsString();
		String jobTitle = "";
		
		modelUser = new TaskAssigneeUserModel();
		modelUser.setIdUser(userId);
		modelUser.setFullName(fullName);
		modelUser.setIdOrg(organizationId);
		modelUser.setOrgName(organizationName);
		modelUser.setJobTitle(jobTitle);
	}

	@Override
	public void buildLayout() {
		this.add(hCaption);
		this.add(btnChooseUser);
		this.add(vDisplay);
		
		btnChooseUser.setWidthFull();
		btnChooseUser.addThemeVariants(ButtonVariant.LUMO_ERROR);
		
		vDisplay.setWidthFull();
		vDisplay.setPadding(false);
		
		this.setWidthFull();
		this.getStyle().set("padding-bottom", "20px");
		this.getStyle().set("border-bottom", "1px solid #e2e2e2");
		displayResult();
	}

	@Override
	public void configComponent() {
		btnChooseUser.addClickListener(e->{
			TaskOwnerDialog dialogAss = new TaskOwnerDialog(modelUser);
			
			dialogAss.open();
			
			dialogAss.addOpenedChangeListener(eClose->{
				if(!eClose.isOpened()) {
					if(dialogAss.getModelUser()!=null) {
						this.modelUser = dialogAss.getModelUser();
						displayResult();
					} else {
						if(this.modelUser!=null) {
							this.modelUser=null;
							displayResult();
						}
					}
				}
			});
		});
	}
	
	public void displayResult() {
		vDisplay.removeAll();
		if(modelUser==null) {
			String displayHtml = "<div class='task-assignee-empty-display'>"
					+"Chưa có cán bộ nào được chọn."
					+"</div>";
			
			Html html = new Html(displayHtml);
			
			vDisplay.add(html);
		} else {
			HorizontalLayout hDisplayUser = new HorizontalLayout();
			
			String strFullname = "<div class='info-block' title='"+modelUser.getFullName()+"'><b>Họ tên: </b>"+modelUser.getFullName()+"<div>";
			String strOrgname = "<div class='info-block' title='"+modelUser.getOrgName()+"'><b>Đơn vị: </b>"+modelUser.getOrgName()+"<div>";
			
			hDisplayUser.setDefaultVerticalComponentAlignment(Alignment.CENTER);
			
			hDisplayUser.addClassName("hLayout-userinfo-owner-selected");
			hDisplayUser.getStyle().set("font-size", "var(--lumo-font-size-m)");
			hDisplayUser.getStyle().set("padding", "15px");
			
			Html htmlFullname = new Html(strFullname);
			Html htmlOrgname = new Html(strOrgname);
			
			hDisplayUser.add(htmlFullname,htmlOrgname);
			
			hDisplayUser.setWidthFull();
			vDisplay.add(hDisplayUser);
		}
	}
	
	public boolean validateForm() {
		if(modelUser==null) {
			btnChooseUser.focus();
			
			NotificationUtil.showNotifi("Vui lòng chọn một cán bộ xử lý nhiệm vụ", NotificationTypeEnum.WARNING);
			return false;
		}
		
		return true;
	}
	
	public TaskAssigneeUserModel getModelUser() {
		return modelUser;
	}
	public void setModelUser(TaskAssigneeUserModel modelUser) {
		this.modelUser = modelUser;
	}
}
