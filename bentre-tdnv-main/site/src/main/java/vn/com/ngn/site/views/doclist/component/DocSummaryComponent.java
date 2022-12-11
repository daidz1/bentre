package vn.com.ngn.site.views.doclist.component;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@SuppressWarnings("serial")
public class DocSummaryComponent extends DocInfoComponent{
	public DocSummaryComponent(String stringValue) {
		this.stringValue = stringValue;
		
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		
		HorizontalLayout hSummary = new HorizontalLayout();
		hSummary.setWidthFull();
		
		String strHtml = "<div style='width:100%'><b class='caption-head'>Trích dẫn: </b> <div style='width: calc(100% - 110px); display: inline-block;font-weight:500'>"+stringValue+"</div></div>";
		
		Html html = new Html(strHtml);
		
		hSummary.add(html);
		hSummary.setWidthFull();
		hSummary.addClassName("row");
		
		this.add(hSummary);
	}
}
