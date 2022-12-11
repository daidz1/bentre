package vn.com.ngn.site.views.dashboard.component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.Crosshair;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.XAxis;
import com.vaadin.flow.component.charts.model.YAxis;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

import vn.com.ngn.site.enums.TaskStatusEnum;
import vn.com.ngn.site.enums.TaskTypeEnum;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.views.tasklist.TaskListView;

@SuppressWarnings("serial")
public class Top10ChartComponent extends VerticalLayout {
	public Top10ChartComponent(TaskTypeEnum eType,JsonObject jsonCountNotDone,JsonObject jsonCountDone) {
		JsonArray jsonTopNotDone = jsonCountNotDone.getAsJsonArray("top");
		JsonArray jsonTopDone = jsonCountDone.getAsJsonArray("top");
		
		Chart chart1 = buildChart(eType, TaskStatusEnum.CHUAHOANTHANH, jsonTopNotDone);
		Chart chart2 = buildChart(eType, TaskStatusEnum.DAHOANTHANH, jsonTopDone);

		Tab tabTop10doing = new Tab("Top 10 cán bộ chưa hoàn thành");
		Tab tabTop10done = new Tab("Top 10 cán bộ đã hoàn thành");

		Tabs tabs = new Tabs(tabTop10doing, tabTop10done);
		VerticalLayout vTabDisplay = new VerticalLayout();

		vTabDisplay.add(chart1,chart2);
		chart2.setVisible(false);
		
		chart1.setHeight("300px");
		chart2.setHeight("300px");

		Map<Tab, Component> tabsToPages = new HashMap<>();

		tabsToPages.put(tabTop10doing, chart1);
		tabsToPages.put(tabTop10done, chart2);

		tabs.addSelectedChangeListener(event -> {
			tabsToPages.values().forEach(page -> page.setVisible(false));
			Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
			selectedPage.setVisible(true);
		});

		tabs.setWidthFull();
		
		this.add(tabs);
		this.add(vTabDisplay);

		this.setPadding(false);
	}
	
	private Chart buildChart(TaskTypeEnum eType, TaskStatusEnum eStatus, JsonArray jsonTop) {
		Chart chart = new Chart(ChartType.COLUMN);
		
		List<Number> listCount = new LinkedList<Number>();
		String[] listName = new String[10];
		String[] listId = new String[10];
		
		int i = 0;
		for(JsonElement jsonEle : jsonTop) {
			try {
				JsonObject jsonOb = jsonEle.getAsJsonObject();
				
				listName[i] = jsonOb.getAsJsonObject("userTask").get("fullName").getAsString();
				listId[i] = jsonOb.getAsJsonObject("userTask").get("userId").getAsString()+"-"+jsonOb.getAsJsonObject("userTask").get("organizationId").getAsString();
				listCount.add(jsonOb.get("countTask").getAsInt());
				i++;
			} catch (Exception e1) {
				
//				e1.printStackTrace();
			}
		}

		Configuration configuration = chart.getConfiguration();
		
		configuration.setTitle("Top 10 cán bộ");
		
		configuration.addSeries(
				new ListSeries("Nhiệm vụ", listCount));

		XAxis x = new XAxis();
		x.setCrosshair(new Crosshair());
		x.setCategories(listName);
		configuration.addxAxis(x);

		YAxis y = new YAxis();
		y.setMin(0);
		y.setTitle("Số lượng nhiệm vụ");
		configuration.addyAxis(y);
		
		chart.addPointClickListener(e->{
			System.out.println(e.getCategory()+" "+listId[e.getItemIndex()]);
			
			String userType = "";
			
			if(eType==TaskTypeEnum.DAGIAO|| eType==TaskTypeEnum.THEODOI) {
				userType=TaskTypeEnum.DUOCGIAO.getKey();
			} else if(eType==TaskTypeEnum.DUOCGIAO) {
				userType=TaskTypeEnum.DAGIAO.getKey();
			}
			
			Map<String, String> mapParam = new HashMap<String, String>();
			mapParam.put("type", eType.getKey());
			mapParam.put("status", eStatus.getKey());
			mapParam.put("user"+userType, listId[e.getItemIndex()]);
			
			SessionUtil.setParam(mapParam);
			getUI().ifPresent(ui -> ui.navigate(TaskListView.class));
		});
		
		return chart;
	}
}
