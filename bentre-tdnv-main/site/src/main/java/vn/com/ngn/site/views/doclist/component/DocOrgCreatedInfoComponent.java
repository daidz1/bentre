package vn.com.ngn.site.views.doclist.component;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@SuppressWarnings("serial")
public class DocOrgCreatedInfoComponent extends DocInfoComponent{
	public DocOrgCreatedInfoComponent(String stringValue) {
		this.stringValue = stringValue;
		
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		HorizontalLayout hOrg = new HorizontalLayout();
		hOrg.setWidthFull();
		
		String strHtml = "<div class='org-created'>CQ ban hành: "+stringValue+"</div>";
		
		Html html = new Html(strHtml);
		
		hOrg.add(html);
		hOrg.addClassName("row");
		hOrg.getElement().setAttribute("role", "org-created");
		
		this.add(hOrg);
	}
}
