package vn.com.ngn.site.views.taskcreate;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.dialog.task.TaskAssgineeDialog;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.model.taskcreate.TaskAssigneeUserModel;
import vn.com.ngn.site.util.component.HeaderUtil;
import vn.com.ngn.site.util.component.NotificationUtil;

@SuppressWarnings("serial")
public class TaskAssgineeLayout extends VerticalLayout implements LayoutInterface{
	private HorizontalLayout hWrapCaption = new HorizontalLayout();
	private HorizontalLayout hCaption = HeaderUtil.createHeader5WithBackground(VaadinIcon.USER_STAR.create(),"Phân công cán bộ xử lý","#1676f3","rgb(22 118 243 / 20%)");
	private Button btnChooseUser = new Button("Chọn cán bộ xử lý",VaadinIcon.POINTER.create());
	private VerticalLayout vDisplay = new VerticalLayout();
	
	private TaskAssigneeUserModel modelUser;
	
	private TaskSupportLayout taskSupportLayout;
	
	private JsonObject jsonInfo;
	
	private String emptyCssClass = "task-assignee-empty-display";
	private String assigneeCssClass = "hLayout-userinfo-assignee-selected";
	
	public TaskAssgineeLayout(JsonObject jsonInfo) {
		this.jsonInfo = jsonInfo;
		
		if(jsonInfo!=null)
			initOldValue(jsonInfo);
		
		buildLayout();
		configComponent();
	}
	
	public void initOldValue(JsonObject jsonInfo) {
		String userId = jsonInfo.get("userId").getAsString();
		String fullName = jsonInfo.get("fullName").getAsString();
		String organizationId = jsonInfo.get("organizationId").getAsString();
		String organizationName = jsonInfo.get("organizationName").getAsString();
		String jobTitle = "";
		
		modelUser = new TaskAssigneeUserModel();
		modelUser.setIdUser(userId);
		modelUser.setFullName(fullName);
		modelUser.setIdOrg(organizationId);
		modelUser.setOrgName(organizationName);
		modelUser.setJobTitle(jobTitle);
	}
	
	public void initValueForm(TaskAssigneeUserModel modelUser) {
		if(modelUser==null)
			return;
		this.modelUser = modelUser;
		
		displayResult();
	}

	@Override
	public void buildLayout() {
		this.add(hWrapCaption);
		this.add(btnChooseUser);
		this.add(vDisplay);
		
		hWrapCaption.add(hCaption);
		
		btnChooseUser.setWidthFull();
		
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
			TaskAssgineeDialog dialogAss = new TaskAssgineeDialog(modelUser,taskSupportLayout.getMapUser());
			
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
			String displayHtml = "<div class='"+emptyCssClass+"'>"
					+"Chưa có cán bộ nào được phân công xử lý."
					+"</div>";
			
			Html html = new Html(displayHtml);
			
			vDisplay.add(html);
		} else {
			HorizontalLayout hDisplayUser = new HorizontalLayout();
			
			String strFullname = "<div class='info-block' title='"+modelUser.getFullName()+"'><b>Họ tên: </b>"+modelUser.getFullName()+"<div>";
			String strOrgname = "<div class='info-block' title='"+modelUser.getOrgName()+"'><b>Đơn vị: </b>"+modelUser.getOrgName()+"<div>";
			String strJobTitle = "<div class='info-block' title='"+modelUser.getJobTitle()+"'><b>Chức vụ: </b>"+modelUser.getJobTitle()+"<div>";
			
			hDisplayUser.setDefaultVerticalComponentAlignment(Alignment.CENTER);
			
			hDisplayUser.addClassName(assigneeCssClass);
			hDisplayUser.getStyle().set("font-size", "var(--lumo-font-size-m)");
			hDisplayUser.getStyle().set("padding", "15px");
			
			Html htmlFullname = new Html(strFullname);
			Html htmlOrgname = new Html(strOrgname);
			Html htmlJobTitle = new Html(strJobTitle);
			
			hDisplayUser.add(htmlFullname,htmlOrgname,htmlJobTitle);
			
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
	
	public void simplifyDisplay() {
		emptyCssClass = "task-assignee-empty-display-simple";
		assigneeCssClass = "hLayout-userinfo-assignee-selected-simple";
		
		hCaption.setVisible(false);
		
		displayResult();
	}
	
	public HorizontalLayout gethWrapCaption() {
		return hWrapCaption;
	}
	public Button getBtnChooseUser() {
		return btnChooseUser;
	}
	public TaskAssigneeUserModel getModelUser() {
		return modelUser;
	}
	public void setModelUser(TaskAssigneeUserModel modelUser) {
		this.modelUser = modelUser;
	}
	public void setTaskSupportLayout(TaskSupportLayout taskSupportLayout) {
		this.taskSupportLayout = taskSupportLayout;
	}
}
