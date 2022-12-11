package vn.com.ngn.site.views.taskcreate;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.dialog.task.TaskAssgineeDialog;
import vn.com.ngn.site.dialog.task.TaskAssigneeOrgDialog;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.model.taskcreate.TaskAssigneeOrgModel;
import vn.com.ngn.site.util.component.HeaderUtil;
import vn.com.ngn.site.util.component.NotificationUtil;

@SuppressWarnings("serial")
public class TaskAssigneeOrgLayout extends VerticalLayout implements LayoutInterface{
	private HorizontalLayout hWrapCaption = new HorizontalLayout();
//	private HorizontalLayout hCaption = HeaderUtil.createHeader5WithBackground(VaadinIcon.USER_STAR.create(),"Cơ quan/đơn vị xử lý","#1676f3","rgb(22 118 243 / 20%)");
	private HorizontalLayout hCaption = HeaderUtil.createHeader5(VaadinIcon.USER_STAR.create(),"Cơ quan/đơn vị xử lý","#1676f3");
	private Button btnChooseUser = new Button("Chọn cơ quan/đơn vị xử lý",VaadinIcon.POINTER.create());
	private VerticalLayout vDisplay = new VerticalLayout();
	
	private TaskAssigneeOrgModel orgModel;
	
	private TaskSupportOrgLayout taskSupportOrgLayout;
	
	private JsonObject jsonInfo;
	
	private String emptyCssClass = "task-assignee-empty-display";
	private String assigneeCssClass = "hLayout-userinfo-assignee-selected";
	
	public TaskAssigneeOrgLayout(JsonObject jsonInfo) {
		this.jsonInfo = jsonInfo;
		
		if(jsonInfo!=null)
			initOldValue(jsonInfo);
		
		buildLayout();
		configComponent();
	}
	
	public void initOldValue(JsonObject jsonInfo) {
		String organizationId = jsonInfo.get("organizationId").getAsString();
		String organizationName = jsonInfo.get("organizationName").getAsString();
		
		orgModel = new TaskAssigneeOrgModel();
		orgModel.setOrgId(organizationId);
		orgModel.setOrgName(organizationName);
		
	}
	
	public void initValueForm(TaskAssigneeOrgModel orgModel) {
		if(orgModel==null)
			return;
		this.orgModel = orgModel;
		
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
			TaskAssigneeOrgDialog dialogAss = new TaskAssigneeOrgDialog(orgModel,taskSupportOrgLayout.getMapOrg());
			
			dialogAss.open();
			
			dialogAss.addOpenedChangeListener(eClose->{
				if(!eClose.isOpened()) {
					if(dialogAss.getOrgModel()!=null) {
						this.orgModel = dialogAss.getOrgModel();
						displayResult();
					} else {
						if(this.orgModel!=null) {
							this.orgModel=null;
							displayResult();
						}
					}
				}
			});
		});
	}
	
	public void displayResult() {
		vDisplay.removeAll();
		if(orgModel==null) {
			String displayHtml = "<div class='"+emptyCssClass+"'>"
					+"Chưa có cơ quan/đơn vị xử lý."
					+"</div>";
			
			Html html = new Html(displayHtml);
			
			vDisplay.add(html);
		} else {
			HorizontalLayout hDisplayUser = new HorizontalLayout();
			
			
			String strOrgname = "<div class='info-block' title='"+orgModel.getOrgName()+"'><b>Đơn vị: </b>"+orgModel.getOrgName()+"<div>";
			
			
			hDisplayUser.setDefaultVerticalComponentAlignment(Alignment.CENTER);
			
			hDisplayUser.addClassName(assigneeCssClass);
			hDisplayUser.getStyle().set("font-size", "var(--lumo-font-size-m)");
			hDisplayUser.getStyle().set("padding", "15px");
			
			
			Html htmlOrgname = new Html(strOrgname);
			
			
			hDisplayUser.add(htmlOrgname);
			
			hDisplayUser.setWidthFull();
			vDisplay.add(hDisplayUser);
		}
	}
	
	public boolean validateForm() {
		if(orgModel==null) {
			btnChooseUser.focus();
			
			NotificationUtil.showNotifi("Vui lòng chọn một cơ quan/đơn vị xử lý nhiệm vụ", NotificationTypeEnum.WARNING);
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
	public TaskAssigneeOrgModel getOrgModel() {
		return orgModel;
	}
	public void setorgModel(TaskAssigneeOrgModel orgModel) {
		this.orgModel = orgModel;
	}
	public void setTaskSupportOrgLayout(TaskSupportOrgLayout taskSupportOrgLayout) {
		this.taskSupportOrgLayout = taskSupportOrgLayout;
	}
}
