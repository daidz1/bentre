package vn.com.ngn.site.views.main.settings;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.LayoutInterface;

@SuppressWarnings("serial")
public class SettingLayout extends VerticalLayout implements LayoutInterface{
	protected HorizontalLayout hInfo =new HorizontalLayout();
	protected Icon icoInfo = VaadinIcon.INFO_CIRCLE.create();
	protected Span spanInfo = new Span();
	
	protected VerticalLayout vContent = new VerticalLayout();
	
	@Override
	public void buildLayout() {
		this.add(hInfo);
		this.add(vContent);
		
		hInfo.add(icoInfo,spanInfo);
		
		icoInfo.setSize("var(--lumo-font-size-xs)");
		icoInfo.getStyle().set("color","#848282");
		
		spanInfo.getStyle().set("color","#848282");
		spanInfo.getStyle().set("font-style","italic");
		
		hInfo.expand(spanInfo);
		hInfo.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		hInfo.setWidthFull();
		
		this.setPadding(false);
	}

	@Override
	public void configComponent() {
		
	}
}
