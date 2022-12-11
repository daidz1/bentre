package vn.com.ngn.site.views.tasklist.component;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class TaskContentComponent extends TaskInfoComponent{
	public TaskContentComponent(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
		
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		
		String description = jsonObject.get("description").getAsString();
		
		HorizontalLayout hContent = new HorizontalLayout();
		hContent.setWidthFull();
		
		String strHtml = "<div style='width:100%'><b class='caption-head'>Nội dung: </b> <div style='width: calc(100% - 110px); display: inline-block;'>"+description+"</div></div>";
		
		Html html = new Html(strHtml);
		
		hContent.add(html);
		hContent.addClassName("row");
		
		this.add(hContent);
	}
}
