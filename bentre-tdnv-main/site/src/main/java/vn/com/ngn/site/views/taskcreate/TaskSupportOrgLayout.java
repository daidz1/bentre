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
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.dialog.task.TaskSupportOrgDialog;
import vn.com.ngn.site.dialog.task.TaskSupportOrgDialog;
import vn.com.ngn.site.model.taskcreate.TaskAssigneeOrgModel;
import vn.com.ngn.site.model.taskcreate.TaskAssigneeOrgModel;
import vn.com.ngn.site.util.component.HeaderUtil;
@SuppressWarnings("serial")
public class TaskSupportOrgLayout extends VerticalLayout implements LayoutInterface{
	private HorizontalLayout hCaption = HeaderUtil.createHeader5(VaadinIcon.USERS.create(),"Cơ quan/đơn vị hỗ trợ","rgb(4 164 71)");
	private Button btnChooseUser = new Button("Chọn cơ quan/đơn vị hỗ trợ",VaadinIcon.POINTER.create());
	private VerticalLayout vDisplay = new VerticalLayout();
	
	private Map<String,TaskAssigneeOrgModel> mapOrg = new HashMap<String,TaskAssigneeOrgModel>();
	
	private TaskAssigneeOrgLayout taskAssigneeOrgLayout;
	
	private String emptyCssClass = "task-assignee-empty-display";
	private String supportCssClass = "hLayout-userinfo-support-selected";
	
	public TaskSupportOrgLayout(JsonArray jsonInfo) {
		if(jsonInfo!=null)
			initOldValue(jsonInfo);
		
		buildLayout();
		configComponent();
	}
	
	public void initOldValue(JsonArray jsonInfo) {
		mapOrg.clear();
		
		for(JsonElement jsonEle : jsonInfo) {
			JsonObject jsonSupport = jsonEle.getAsJsonObject();
//			String userId = jsonSupport.get("userId").getAsString();
//			String fullName = jsonSupport.get("fullName").getAsString();
			String organizationId = jsonSupport.get("organizationId").getAsString();
			String organizationName = jsonSupport.get("organizationName").getAsString();
			TaskAssigneeOrgModel orgModel = new TaskAssigneeOrgModel();
			
			orgModel.setOrgId(organizationId);
			orgModel.setOrgName(organizationName);
			
			
			mapOrg.put(organizationId+"-"+organizationName, orgModel);
		}
	}
	
	public void initValueForm(List<TaskAssigneeOrgModel> listOrgSupport) {
		for(TaskAssigneeOrgModel orgModel : listOrgSupport) {
			mapOrg.put(orgModel.getOrgId()+"-"+orgModel.getOrgName(), orgModel);
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
			TaskSupportOrgDialog dialogSp = new TaskSupportOrgDialog(mapOrg,taskAssigneeOrgLayout.getOrgModel());
			
			dialogSp.open();
			
			dialogSp.addOpenedChangeListener(eClose->{
				if(!eClose.isOpened()) {
					if(dialogSp.getMapOrg().size()>0) {
						this.mapOrg = dialogSp.getMapOrg();
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
		if(mapOrg.size()==0) {
			String displayHtml = "<div class='"+emptyCssClass+"'>"
					+"Chưa có cơ quan/đơn vị nào được phân công hỗ trợ."
					+"</div>";
			
			Html html = new Html(displayHtml);
			vDisplay.add(html);
		} else {
			for(Entry<String,TaskAssigneeOrgModel> entry : mapOrg.entrySet()) {
				HorizontalLayout hDisplayUser = new HorizontalLayout();
				var orgModel = entry.getValue();
				
			
				String strOrgname = "<div class='info-block' title='"+orgModel.getOrgName()+"'><b>Đơn vị: </b>"+orgModel.getOrgName()+"<div>";
				
				
				hDisplayUser.setDefaultVerticalComponentAlignment(Alignment.CENTER);
				
				hDisplayUser.addClassName(supportCssClass);
				hDisplayUser.getStyle().set("font-size", "var(--lumo-font-size-m)");
				hDisplayUser.getStyle().set("padding", "15px");
				
				
				Html htmlOrgname = new Html(strOrgname);
				
				
				hDisplayUser.add(htmlOrgname);
				
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

	public Map<String, TaskAssigneeOrgModel> getMapOrg() {
		return mapOrg;
	}
	public void setMapUser(Map<String, TaskAssigneeOrgModel> mapOrg) {
		this.mapOrg = mapOrg;
	}
	public void setTaskAssigneeOrgLayout(TaskAssigneeOrgLayout taskAssigneeOrgLayout) {
		this.taskAssigneeOrgLayout = taskAssigneeOrgLayout;
	}
}
