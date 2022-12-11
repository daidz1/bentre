package vn.com.ngn.site.views.doclist.component;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@SuppressWarnings("serial")
public class DocOrgComponent extends DocInfoComponent{
	public DocOrgComponent(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
		
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		
		String orgCreated = jsonObject.get("docOrgCreated").getAsString();
		String orgRecieved = jsonObject.get("docOrgReceived").getAsString();
		HorizontalLayout hDate = new HorizontalLayout();
		hDate.setWidthFull();
		
		String strHtml = "<div><b class='caption-head'>CQ ban hành: </b> "+orgCreated+" <b style='margin-left:15px'>CQ nhận: </b>"+orgRecieved+"</div>";
		
		Html html = new Html(strHtml);
		
		hDate.add(html);
		hDate.addClassName("row");
		
		this.add(hDate);
	}
}
