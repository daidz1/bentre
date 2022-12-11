package vn.com.ngn.site.views.doclist.component;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import vn.com.ngn.site.util.LocalDateUtil;

@SuppressWarnings("serial")
public class DocDateComponent extends DocInfoComponent{
	public DocDateComponent(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
		
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		
		String docTime = LocalDateUtil.formatLocalDate(LocalDateUtil.longToLocalDate(jsonObject.get("docDate").getAsLong()),LocalDateUtil.dateFormater1);
		String docRegTime = LocalDateUtil.formatLocalDate(LocalDateUtil.longToLocalDate(jsonObject.get("docRegDate").getAsLong()),LocalDateUtil.dateFormater1);
		HorizontalLayout hDate = new HorizontalLayout();
		hDate.setWidthFull();
		
		String strHtml = "<div><b class='caption-head'>Ngày nhập: </b> "+docTime+" <b style='margin-left:15px'>Ngày ký: </b>"+docRegTime+"</div>";
		
		Html html = new Html(strHtml);
		
		hDate.add(html);
		hDate.addClassName("row");
		
		this.add(hDate);
	}
}
