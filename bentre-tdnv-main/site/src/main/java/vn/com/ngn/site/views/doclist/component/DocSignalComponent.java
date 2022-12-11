package vn.com.ngn.site.views.doclist.component;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@SuppressWarnings("serial")
public class DocSignalComponent extends DocInfoComponent{
	public DocSignalComponent(String stringValue) {
		this.stringValue = stringValue;
		
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		
		HorizontalLayout hSignal = new HorizontalLayout();
		hSignal.setWidthFull();
		
		String strHtml = "<div style='width:100%;'><b class='caption-head'>Số ký hiệu: </b> <div style='width: calc(100% - 110px); display: inline-block;font-weight:500;color: #b04242;'>"+stringValue+"</div></div>";
		
		Html html = new Html(strHtml);
		
		hSignal.add(html);
		hSignal.setWidthFull();
		hSignal.addClassName("row");
		
		this.add(hSignal);
	}
}
