package vn.com.ngn.site.views.tasklist.component;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class TaskTitleComponent extends TaskInfoComponent{
	public TaskTitleComponent(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
		
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		
		String title = jsonObject.get("title").getAsString();
		
		HorizontalLayout htitle = new HorizontalLayout();
		htitle.setWidthFull();
		
		String strHtml = "<div style='width:100%'><b class='caption-head'>Tiêu đề: </b> <div style='width: calc(100% - 110px); display: inline-block;font-weight:500'>"+title+"</div></div>";
		
		Html html = new Html(strHtml);
		
		htitle.add(html);
		htitle.setWidthFull();
		htitle.addClassName("row");
		
		this.add(htitle);
	}
}
