package vn.com.ngn.site.views.doclist.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.LayoutInterface;

@SuppressWarnings("serial")
public class DocInfoComponent extends VerticalLayout implements LayoutInterface{
	protected String stringValue;
	protected JsonObject jsonObject = new JsonObject();
	protected JsonArray jsonArray = new JsonArray();
	
	protected String eType;
	protected String eStatus;
	
	protected Button btnTrigger = new Button();
	
	@Override
	public void buildLayout() {
		this.add(btnTrigger);
		
		btnTrigger.setVisible(false);
		
		this.setPadding(false);
	}

	@Override
	public void configComponent() {
		
	}

	public Button getBtnTrigger() {
		return btnTrigger;
	}
}
