package vn.com.ngn.site.views.doclist.layout;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.views.doclist.component.TaskCreateFromDocComponent;
import vn.com.ngn.site.views.taskcreate.MultiTaskCreateLayout;
import vn.com.ngn.site.views.taskcreate.SingleTaskCreateLayout;

public class TaskCreateFromDocLayout extends VerticalLayout implements LayoutInterface{
	private Button btnChangeState = new Button("Giao nhiều nhiệm vụ",VaadinIcon.REFRESH.create());
	private TaskCreateFromDocComponent layoutCreateTask ;
	private MultiTaskCreateFromDocLayout layoutMultiCreateTask ;
	private JsonObject jsonDoc;
	
	private Button btnTrigger = new Button();
	private Button btnCancel = new Button();
	
    public TaskCreateFromDocLayout(JsonObject jsonDoc) {
    	this.jsonDoc = jsonDoc;
    	layoutMultiCreateTask = new MultiTaskCreateFromDocLayout(jsonDoc);
    	layoutCreateTask = new TaskCreateFromDocComponent(jsonDoc);
    	buildLayout();
    	configComponent();
    }

	@Override
	public void buildLayout() {
		this.add(btnChangeState);
		this.add(layoutCreateTask);
		this.add(layoutMultiCreateTask);
		this.add(btnTrigger);
		btnTrigger.setVisible(false);
		
		btnChangeState.getStyle().set("margin-left", "10px");
		
		layoutMultiCreateTask.setVisible(false);
		
		this.setSpacing(false);
	}

	@Override
	public void configComponent() {
		btnChangeState.addClickListener(e->{
			layoutCreateTask.setVisible(!layoutCreateTask.isVisible());
			layoutMultiCreateTask.setVisible(!layoutMultiCreateTask.isVisible());
			
			if(layoutCreateTask.isVisible()) {
				btnChangeState.setText("Giao nhiều nhiệm vụ");
			} else {
				btnChangeState.setText("Giao một nhiệm vụ");
			}
		});
		
		layoutCreateTask.getBtnCancel().addClickListener(e->{
			System.out.println("=====TaskCreateFromDocComponent: BtnCancel click =====");
			btnCancel.click();
		});
		
		layoutCreateTask.getBtnTrigger().addClickListener(e->{
			System.out.println("=====TaskCreateFromDocComponent: btnTrigger click =====");
			btnTrigger.click();
		});
		
		layoutMultiCreateTask.getBtnTrigger().addClickListener(e->{
			System.out.println("=====MultiTaskCreateFromDocComponent: btnTrigger click =====");
			btnTrigger.click();
		});
		
	}

	public Button getBtnTrigger() {
		return btnTrigger;
	}

	public void setBtnTrigger(Button btnTrigger) {
		this.btnTrigger = btnTrigger;
	}

	public Button getBtnCancel() {
		return btnCancel;
	}

	public void setBtnCancel(Button btnCancel) {
		this.btnCancel = btnCancel;
	}
	

}
