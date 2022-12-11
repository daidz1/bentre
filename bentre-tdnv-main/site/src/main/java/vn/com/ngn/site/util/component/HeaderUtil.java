package vn.com.ngn.site.util.component;

import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class HeaderUtil {
	public static HorizontalLayout createHeader5WithBackground(Icon icon, String caption,String color, String background) {
		HorizontalLayout hLayout = new HorizontalLayout();
		
		if(icon!=null) {
			hLayout.add(icon);
			icon.setSize("12px");
		}
		H5 h4 = new H5(caption);
		
		h4.getStyle().set("margin","0 0 0 5px");
		
		hLayout.add(h4);
		
		if(color!=null) {
			hLayout.getStyle().set("color", color);
			h4.getStyle().set("color", color);
		}
		hLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		hLayout.getStyle().set("background",background);
		hLayout.getStyle().set("padding","7px 10px");
		hLayout.getStyle().set("border-radius","10px");
		
		return hLayout;
	}
	public static HorizontalLayout createHeader5(Icon icon, String caption,String color) {
		HorizontalLayout hLayout = new HorizontalLayout();
		
		if(icon!=null) {
			hLayout.add(icon);
			icon.setSize("12px");
		}
		H5 h5 = new H5(caption);
		
		h5.getStyle().set("margin","0 0 0 5px");
		
		hLayout.add(h5);
		
		if(color!=null) {
			hLayout.getStyle().set("color", color);
			h5.getStyle().set("color", color);
		}
		hLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		
		return hLayout;
	}
}
