package vn.com.ngn.site.views.taskcreate;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.views.main.MainView;

@SuppressWarnings("serial")
@Route(value = "taskcreate", layout = MainView.class)
@PageTitle("Tạo nhiệm vụ mới")
public class TaskCreateView extends VerticalLayout implements LayoutInterface {
	private Button btnChangeState = new Button("Giao nhiều nhiệm vụ",VaadinIcon.REFRESH.create());
	private SingleTaskCreateLayout layoutCreateTask = new SingleTaskCreateLayout(null,null);
	private MultiTaskCreateLayout layoutMultiCreateTask = new MultiTaskCreateLayout();
	
    public TaskCreateView() {
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
