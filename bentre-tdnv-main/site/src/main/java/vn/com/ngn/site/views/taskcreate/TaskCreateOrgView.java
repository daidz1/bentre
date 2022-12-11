package vn.com.ngn.site.views.taskcreate;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.views.main.MainView;

@SuppressWarnings("serial")
@Route(value = "taskcreateorg", layout = MainView.class)
@PageTitle("Tạo nhiệm vụ mới giao cho cơ quan/đơn vị")
public class TaskCreateOrgView extends VerticalLayout implements LayoutInterface{
	private Button btnChangeState = new Button("Giao nhiều nhiệm vụ",VaadinIcon.REFRESH.create());
	private SingleTaskCreateOrgLayout layoutCreateTask = new SingleTaskCreateOrgLayout(null,null);
	private MultiTaskCreateOrgLayout layoutMultiCreateTask = new MultiTaskCreateOrgLayout();
	
	public TaskCreateOrgView() {
    	buildLayout();
    	configComponent();
    }

	@Override
	public void buildLayout() {
		this.add(btnChangeState);
		this.add(layoutCreateTask);
		this.add(layoutMultiCreateTask);
		
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
	}
}
