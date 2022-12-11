package vn.com.ngn.site.views.dashboard;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.enums.PermissionEnum;
import vn.com.ngn.site.enums.TaskTypeEnum;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.views.main.MainView;
import vn.com.ngn.site.views.tasklist.TaskListView;

@SuppressWarnings("serial")
@Route(value = "dashboardnew", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Tổng quan")
@NpmPackage(value = "jquery", version = "3.4.1")
@JavaScript("./js/dashboard.js")
public class DashboardNewView extends VerticalLayout implements LayoutInterface {
	private HorizontalLayout hSelectType = new HorizontalLayout();
	private Span selectDaGiao = new Span("Đã giao");
	private Span selectDuocGiao = new Span("Được giao");
	private Span selectHoTro = new Span("Hỗ trợ");
	private VerticalLayout vDisplay = new VerticalLayout();
    
	public DashboardNewView() {
    	buildLayout();
    	configComponent();
    }

	@Override
	public void buildLayout() {
		this.add(hSelectType);
		this.add(vDisplay);
		
		this.setWidthFull();
		this.setMinHeight("100%");
		this.setId("dashboardnew-view");
		
		vDisplay.setPadding(false);
		 
		buildSelectLayout();
	}

	@Override
	public void configComponent() {
		selectDaGiao.addClickListener(e->{
			selectDaGiao.addClassName("select-button-active");
			selectDuocGiao.removeClassName("select-button-active");
			selectHoTro.removeClassName("select-button-active");
			
			vDisplay.removeAll();
			vDisplay.add(new DashboardContentLayout(TaskTypeEnum.DAGIAO));
		});
		
		selectDuocGiao.addClickListener(e->{
			selectDuocGiao.addClassName("select-button-active");
			selectDaGiao.removeClassName("select-button-active");
			selectHoTro.removeClassName("select-button-active");
			
			vDisplay.removeAll();
			vDisplay.add(new DashboardContentLayout(TaskTypeEnum.DUOCGIAO));
		});
		
		selectHoTro.addClickListener(e->{
			selectHoTro.addClassName("select-button-active");
			selectDuocGiao.removeClassName("select-button-active");
			selectDaGiao.removeClassName("select-button-active");
			
			vDisplay.removeAll();
			vDisplay.add(new DashboardContentLayout(TaskTypeEnum.THEODOI));
		});
	}
	
	private void buildSelectLayout() {
		hSelectType.add(selectDuocGiao,selectHoTro);
		
		selectDaGiao.addClassName("select-button");
		selectDuocGiao.addClassName("select-button");
		selectHoTro.addClassName("select-button");
		
		if(SessionUtil.isHasPermission(PermissionEnum.giaonhiemvu)) {
			hSelectType.addComponentAsFirst(selectDaGiao);
			vDisplay.add(new DashboardContentLayout(TaskTypeEnum.DAGIAO));
			selectDaGiao.addClassName("select-button-active");
		} else {
			vDisplay.add(new DashboardContentLayout(TaskTypeEnum.DUOCGIAO));
			selectDuocGiao.addClassName("select-button-active");
		}
	}
	
	@ClientCallable
	private void navigateTask(elemental.json.JsonObject data){
		Map<String, String> mapParam = new HashMap<String, String>();
		mapParam.put("type", data.getString("type"));
		mapParam.put("status", data.getString("status"));
		
		SessionUtil.setParam(mapParam);
		getUI().ifPresent(ui -> ui.navigate(TaskListView.class));
	}
}
