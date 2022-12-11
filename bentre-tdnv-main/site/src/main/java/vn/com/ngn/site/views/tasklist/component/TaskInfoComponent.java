package vn.com.ngn.site.views.tasklist.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.enums.TaskStatusEnum;
import vn.com.ngn.site.enums.TaskTypeEnum;

public class TaskInfoComponent extends VerticalLayout implements LayoutInterface{
	protected JsonObject jsonObject = new JsonObject();
	protected JsonArray jsonArray = new JsonArray();
	
	protected String eType;
	protected String eStatus;
	
	protected Button btnTrigger = new Button();
	
	protected Class<? extends Component> classCall;
	
	protected String token;
	
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
