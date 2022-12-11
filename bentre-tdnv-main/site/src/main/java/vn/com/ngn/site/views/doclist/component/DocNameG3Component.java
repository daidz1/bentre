package vn.com.ngn.site.views.doclist.component;

import com.google.gson.JsonArray;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@SuppressWarnings("serial")
public class DocNameG3Component extends DocInfoComponent{
	public DocNameG3Component(JsonArray jsonArrG3) {
		this.jsonArray = jsonArrG3;
		
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		
		HorizontalLayout hG3 = new HorizontalLayout();
		hG3.setWidthFull();
		
		String strHoTro = "Không có hỗ trợ nào";
		if(jsonArray.size()>0) {
			strHoTro = jsonArray.toString();
		}
		
		String strHtml = "<div style='width:100%'><b class='caption-head'>Hỗ trợ: </b> <div style='width: calc(100% - 110px); display: inline-block;font-style:italic;color: green;'>"+strHoTro+"</div></div>";
		
		Html html = new Html(strHtml);
		
		hG3.add(html);
		hG3.setWidthFull();
		hG3.addClassName("row");
		
		this.add(hG3);
	}
}
