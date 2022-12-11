package vn.com.ngn.site.views.doclist.component;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@SuppressWarnings("serial")
public class DocSignerNameComponent extends DocInfoComponent{
	public DocSignerNameComponent(String stringValue) {
		this.stringValue = stringValue;
		
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		
		HorizontalLayout hSigner = new HorizontalLayout();
		hSigner.setWidthFull();
		
		String strHtml = "<div style='width:100%'><b class='caption-head'>Người ký: </b> <div style='width: calc(100% - 110px); display: inline-block;'>"+stringValue+"</div></div>";
		
		Html html = new Html(strHtml);
		
		hSigner.add(html);
		hSigner.setWidthFull();
		hSigner.addClassName("row");
		
		this.add(hSigner);
	}
}
