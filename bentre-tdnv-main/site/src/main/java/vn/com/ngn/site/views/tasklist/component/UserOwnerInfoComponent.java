package vn.com.ngn.site.views.tasklist.component;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import vn.com.ngn.site.enums.DisplayConfigEnum;
import vn.com.ngn.site.enums.TaskTypeEnum;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.views.tasklist.TaskListView;

public class UserOwnerInfoComponent extends TaskInfoComponent{
	public UserOwnerInfoComponent(JsonObject jsonObject,String eType,Class<? extends Component> classCall) {
		this.jsonObject = jsonObject;
		this.eType = eType;
		this.classCall = classCall;
		
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		String fullName = jsonObject.get("fullName").getAsString();
		String orgName = jsonObject.get("organizationName").getAsString();
		
		HorizontalLayout hUserAssign = new HorizontalLayout();
		hUserAssign.setWidthFull();
		
		String strHtmlFull = "<div class='user-owner'>Người giao: "+fullName+" ("+orgName+")</div>";
		String strHtml = "<div class='user-owner'>Người giao: "+fullName+"</div>";
		
		String strHtmlDisplay = strHtmlFull;
		if(classCall.equals(TaskListView.class)) {
			if(eType.equals(TaskTypeEnum.DUOCGIAO.getKey())) {
				if(!SessionUtil.statusOfDisplayConfig(DisplayConfigEnum.duocgiaoview_display_owner_org)) {
					strHtmlDisplay = strHtml;
				}
			} else if(eType.equals(TaskTypeEnum.THEODOI.getKey())) {
				if(!SessionUtil.statusOfDisplayConfig(DisplayConfigEnum.hotroview_display_owner_org)) {
					strHtmlDisplay = strHtml;
				}
			}
		}
		
		Html html = new Html(strHtmlDisplay);
		
		hUserAssign.add(html);
		hUserAssign.addClassName("row");
		hUserAssign.getElement().setAttribute("role", "user-owner");
		
		this.add(hUserAssign);
	}
}
