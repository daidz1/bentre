package vn.com.ngn.site.views.doclist.component;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@SuppressWarnings("serial")
public class DocBossNameComponent extends DocInfoComponent{
	public DocBossNameComponent(String stringValue) {
		this.stringValue = stringValue;
		
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		
		HorizontalLayout hBoss = new HorizontalLayout();
		hBoss.setWidthFull();
		
		String strHtml = "<div style='width:100%'><b class='caption-head'>Chủ trì: </b> <div style='width: calc(100% - 110px); display: inline-block;font-weight:500;color: #2865c2;'>"+stringValue+"</div></div>";
		
		Html html = new Html(strHtml);
		
		hBoss.add(html);
		hBoss.setWidthFull();
		hBoss.addClassName("row");
		
		this.add(hBoss);
	}
}
