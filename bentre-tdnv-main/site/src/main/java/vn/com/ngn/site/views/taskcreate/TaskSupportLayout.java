package vn.com.ngn.site.views.taskcreate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.dialog.task.TaskSupportDialog;
import vn.com.ngn.site.model.taskcreate.TaskAssigneeUserModel;
import vn.com.ngn.site.util.component.HeaderUtil;

@SuppressWarnings("serial")
public class TaskSupportLayout extends VerticalLayout implements LayoutInterface{
	private HorizontalLayout hCaption = HeaderUtil.createHeader5WithBackground(VaadinIcon.USERS.create(),"Phân công cán bộ hỗ trợ","rgb(4 164 71)","rgb(62 184 114 / 20%)");
	private Button btnChooseUser = new Button("Chọn cán bộ hỗ trợ",VaadinIcon.POINTER.create());
	private VerticalLayout vDisplay = new VerticalLayout();
	
	private Map<String,TaskAssigneeUserModel> mapUser = new HashMap<String,TaskAssigneeUserModel>();
	
	private TaskAssgineeLayout taskAssgineeLayout;
	
	private JsonArray jsonInfo;
	
	private String emptyCssClass = "task-assignee-empty-display";
	private String supportCssClass = "hLayout-userinfo-support-selected";
	
	public TaskSupportLayout(JsonArray jsonInfo) {
		this.jsonInfo = jsonInfo;
		
		if(jsonInfo!=null)
			initOldValue(jsonInfo);
		
		buildLayout();
		configComponent();
	}
	
	public void initOldValue(JsonArray jsonInfo) {
		mapUser.clear();
		
		for(JsonElement jsonEle : jsonInfo) {
			JsonObject jsonSupport = jsonEle.getAsJsonObject();
			String userId = jsonSupport.get("userId").getAsString();
			String fullName = jsonSupport.get("fullName").getAsString();
			String organizationId = jsonSupport.get("organizationId").getAsString();
			String organizationName = jsonSupport.get("organizationName").getAsString();
			String jobTitle = "";
			
			TaskAssigneeUserModel modelUser = new TaskAssigneeUserModel();
			modelUser.setIdUser(userId);
			modelUser.setFullName(fullName);
			modelUser.setIdOrg(organizationId);
			modelUser.setOrgName(organizationName);
			modelUser.setJobTitle(jobTitle);
			
			mapUser.put(userId+"-"+organizationId, modelUser);
		}
	}
	
	public void initValueForm(List<TaskAssigneeUserModel> listUserSupport) {
		for(TaskAssigneeUserModel modelUser : listUserSupport) {
			mapUser.put(modelUser.getIdUser()+"-"+modelUser.getIdOrg(), modelUser);
		}
		displayResult();
	}

	@Override
	public void buildLayout() {
		this.add(hCaption);
		this.add(btnChooseUser);
		this.add(vDisplay);
		
		btnChooseUser.setWidthFull();
		btnChooseUser.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		
		vDisplay.setWidthFull();
		vDisplay.setPadding(false);
		
		this.setWidthFull();
		
		displayResult();
	}

	@Override
	public void configComponent() {
		btnChooseUser.addClickListener(e->{
			TaskSupportDialog dialogSp = new TaskSupportDialog(mapUser,taskAssgineeLayout.getModelUser());
			
			dialogSp.open();
			
			dialogSp.addOpenedChangeListener(eClose->{
				if(!eClose.isOpened()) {
					if(dialogSp.getMapUser().size()>0) {
						this.mapUser = dialogSp.getMapUser();
						displayResult();
					} else {
						//vì cho map bằng giá trị của nhau nên ko cần phải set lại
						displayResult();
					}
				}
			});
		});
	}
	
	public void displayResult() {
		vDisplay.removeAll();
		if(mapUser.size()==0) {
			String displayHtml = "<div class='"+emptyCssClass+"'>"
					+"Chưa có cán bộ nào được phân công hỗ trợ."
					+"</div>";
			
			Html html = new Html(displayHtml);
			vDisplay.add(html);
		} else {
			for(Entry<String,TaskAssigneeUserModel> entry : mapUser.entrySet()) {
				HorizontalLayout hDisplayUser = new HorizontalLayout();
				var modelUser = entry.getValue();
				
				String strFullname = "<div class='info-block' title='"+modelUser.getFullName()+"'><b>Họ tên: </b>"+modelUser.getFullName()+"<div>";
				String strOrgname = "<div class='info-block' title='"+modelUser.getOrgName()+"'><b>Đơn vị: </b>"+modelUser.getOrgName()+"<div>";
				String strJobTitle = "<div class='info-block' title='"+modelUser.getJobTitle()+"'><b>Chức vụ: </b>"+modelUser.getJobTitle()+"<div>";
				
				hDisplayUser.setDefaultVerticalComponentAlignment(Alignment.CENTER);
				
				hDisplayUser.addClassName(supportCssClass);
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
	}
	
	public void simplifyDisplay() {
		emptyCssClass = "task-assignee-empty-display-simple";
		supportCssClass = "hLayout-userinfo-support-selected-simple";
		
		hCaption.setVisible(false);
		
		displayResult();
	}

	public Map<String, TaskAssigneeUserModel> getMapUser() {
		return mapUser;
	}
	public void setMapUser(Map<String, TaskAssigneeUserModel> mapUser) {
		this.mapUser = mapUser;
	}
	public void setTaskAssgineeLayout(TaskAssgineeLayout taskAssgineeLayout) {
		this.taskAssgineeLayout = taskAssgineeLayout;
	}
}