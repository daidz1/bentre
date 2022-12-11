package vn.com.ngn.site.views.search;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.model.TaskFilterModel;
import vn.com.ngn.site.module.PaginationModule;
import vn.com.ngn.site.module.TaskFilterModule;
import vn.com.ngn.site.util.service.TaskServiceUtil;
import vn.com.ngn.site.views.main.MainView;
import vn.com.ngn.site.views.tasklist.TaskBlockLayout;

@SuppressWarnings("serial")
@Route(value = "search", layout = MainView.class)
@PageTitle("Tìm kiếm nhiệm vụ")
public class SearchView extends VerticalLayout implements LayoutInterface {
	private TaskFilterModule taskFilter = new TaskFilterModule();
	private PaginationModule pagination = new PaginationModule();
	private VerticalLayout vTaskList = new VerticalLayout();

    public SearchView() {
    	buildLayout();
		configComponent();
    }

	@Override
	public void buildLayout() {
		this.add(taskFilter);
		this.add(pagination);
		this.add(vTaskList);
		
		taskFilter.setForSearching();
	}

	@Override
	public void configComponent() {
		pagination.getBtnTrigger().addClickListener(e->{
			try {
				loadData();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		taskFilter.getBtnSearch().addClickListener(e->{
			reCount(taskFilter.getTaskFilterAll());
			try {
				loadData();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}
	
	private void reCount(TaskFilterModel modelFilter) {
		try {
			JsonObject jsonResponse = TaskServiceUtil.getTaskList(modelFilter);

			if(jsonResponse.get("status").getAsInt()==200) {
				int count = jsonResponse.get("total").getAsInt();
				pagination.setItemCount(count);
				pagination.reCalculate();
			} else {
				System.out.println(jsonResponse);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadData() throws IOException {
		vTaskList.removeAll();
		TaskFilterModel modelFilter = taskFilter.getTaskFilterAll();
		modelFilter.setSkip(pagination.getSkip());
		modelFilter.setLimit(pagination.getLimit());
		JsonObject jsonResponse = TaskServiceUtil.getTaskList(modelFilter);
		if(jsonResponse.get("status").getAsInt()==200) {
			JsonArray jsonTaskList = jsonResponse.get("result").getAsJsonArray();

			for(JsonElement jsonTask : jsonTaskList) {
				vTaskList.add(new TaskBlockLayout(jsonTask.getAsJsonObject(),modelFilter.getCategorykey(),modelFilter.getSubcategorykey(),this.getClass()));
			}
		} else {
			System.out.println(jsonResponse);
		}
	}
}
