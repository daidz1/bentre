package vn.com.ngn.site.dialog;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

import vn.com.ngn.site.LayoutInterface;

@SuppressWarnings("serial")
public class DialogTemplate extends Dialog implements LayoutInterface{
	protected HorizontalLayout hHead = new HorizontalLayout();
	protected H4 caption = new H4();
	protected Button btnClose = new Button(VaadinIcon.CLOSE.create());
	
	protected VerticalLayout vMain = new VerticalLayout();
	
	@Override
	public void buildLayout() {
		this.add(hHead);
		this.add(vMain);
		
		hHead.add(caption,btnClose);

		btnClose.addThemeVariants(ButtonVariant.LUMO_SMALL);
		
		hHead.expand(caption);
		
		caption.getStyle().set("margin", "0 0 0 10px");
		caption.getStyle().set("color", "#1d4b90");
		
		btnClose.getStyle().set("background", "#d45353");
		btnClose.getStyle().set("color", "#fff");
		
		hHead.getStyle().set("border-bottom", "1px solid rgb(226 226 226)");
		hHead.getStyle().set("margin-bottom", "10px");
		hHead.getStyle().set("padding-bottom", "5px");
		
		hHead.setWidthFull();
		hHead.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		
		vMain.setPadding(false);
		this.setMaxHeight("100%");
	}

	@Override
	public void configComponent() {
		btnClose.addClickListener(e->{
			System.out.println("dialog close");
			close();
		});
	}
	
	public void setCaption(H4 caption) {
		this.caption = caption;
	}
}
