package vn.com.ngn.site.views.tasklist.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@SuppressWarnings("serial")
public class TaskSupportComponent extends TaskInfoComponent{
	public TaskSupportComponent(JsonArray jsonArray) {
		this.jsonArray = jsonArray;
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		
		String strUser = "";
		
		if(jsonArray.size()>0) {
			for(JsonElement jsonEle : jsonArray) {
				JsonObject jsonOb = jsonEle.getAsJsonObject();
				String organizationName="";
				String fullName = "";
				try {
					fullName = jsonOb.get("fullName").getAsString();
				} catch (Exception e) {
				
				}
				organizationName = jsonOb.get("organizationName").getAsString();
				strUser+="<span class='user-support' title='"+organizationName+"'>"+fullName+" ("+organizationName+")</span>";
			}
		} else {
			strUser = "<span style='font-style: italic; color: #3dae64;'>Không có</span>";
		}

		HorizontalLayout hUserSupport = new HorizontalLayout();
		hUserSupport.setWidthFull();
		
		String strHtml = "<div><b class='caption-head'>Cán bộ hỗ trợ: </b> "+strUser+"</div>";
		
		Html html = new Html(strHtml);
		
		hUserSupport.add(html);
		hUserSupport.addClassName("row");
		
		this.add(hUserSupport);
	}
}
