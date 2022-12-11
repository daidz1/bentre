package vn.com.ngn.site.views.dashboard;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.enums.TaskTypeEnum;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;
import vn.com.ngn.site.views.dashboard.component.CountSumComponent;
import vn.com.ngn.site.views.dashboard.component.NotifyComponent;
import vn.com.ngn.site.views.dashboard.component.TaskCountByDateChartComponent;
import vn.com.ngn.site.views.dashboard.component.TaskListComponent;
import vn.com.ngn.site.views.dashboard.component.Top10ChartComponent;

@SuppressWarnings("serial")
public class DashboardContentLayout extends VerticalLayout implements LayoutInterface{
	private Board boardWrap = new Board();
	private Row rowWrap = new Row();
	private Board boardIn = new Board();
	private Row rowIn1 = new Row();
	private Row rowIn2 = new Row();
	
	private VerticalLayout vTaskSummary = new VerticalLayout();
	private VerticalLayout vTaskCountByDate = new VerticalLayout();
	private VerticalLayout vTop10Chart = new VerticalLayout();
	private VerticalLayout vNotifiSummary = new VerticalLayout();
	
	private CountSumComponent comTaskSummary;
	private TaskCountByDateChartComponent comTaskCountByDate;
	private Top10ChartComponent comTop10Chart;
	private NotifyComponent comNotifiSummary;
	
	private TaskListComponent comTaskList;
	
	private TaskTypeEnum eType;
	
	private JsonObject jsonCountNotDone;
	private JsonObject jsonCountDone;
	public DashboardContentLayout(TaskTypeEnum eType) {
		this.eType = eType;
		
		loadData();
		buildLayout();
		configComponent();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void buildLayout() {
		comTaskSummary = new CountSumComponent(eType,jsonCountNotDone, jsonCountDone);
		comTaskCountByDate = new TaskCountByDateChartComponent(eType);
		comTop10Chart = new Top10ChartComponent(eType,jsonCountNotDone, jsonCountDone);
		comNotifiSummary = new NotifyComponent(eType);
		comTaskList = new TaskListComponent(eType);
		
		this.add(comTaskList);
		this.add(boardWrap);
		
		boardWrap.add(rowWrap);
		
		rowWrap.add(boardIn,vNotifiSummary);
		boardIn.add(rowIn1,rowIn2);

		rowIn1.add(vTaskSummary,vTaskCountByDate);
		rowIn2.add(vTop10Chart);
		
		rowWrap.setComponentSpan(boardIn, 2);
		
		vTaskSummary.add(comTaskSummary);
		vTaskCountByDate.add(comTaskCountByDate);
		vTop10Chart.add(comTop10Chart);
		vNotifiSummary.add(comNotifiSummary);
		
		vTaskSummary.getStyle().set("padding", "5px");
		vTaskCountByDate.getStyle().set("padding", "5px");
		vTop10Chart.getStyle().set("padding", "5px");
		vNotifiSummary.getStyle().set("padding", "5px");
		
		comTaskSummary.addClassName("summary-layout");
		comTaskCountByDate.addClassName("summary-layout");
		comTop10Chart.addClassName("summary-layout");
		comNotifiSummary.addClassName("summary-layout");
		comTaskList.addClassName("summary-layout");
		 
		rowIn1.setId("dboard-row2");
		rowIn2.setId("dboard-row3");
		comNotifiSummary.setId("dboard-notify");
		
		this.setPadding(false);
		 
		// original code in js file
		UI.getCurrent().getPage().executeJavaScript("resizeNotifyPanel();function resizeNotifyPanel()\r\n" + 
				"{\r\n" + 
				"	console.log(\"recursive for height\");\r\n" + 
				"	var heightRow1 = $(\"#dboard-row2\").height();\r\n" + 
				"	var heightRow2 = $(\"#dboard-row3\").height();\r\n" + 
				"\r\n" + 
				"	var heightDetail = heightRow1 + heightRow2 - 10;\r\n" + 
				"	console.log(heightRow1);\r\n" + 
				"\r\n" + 
				"	//console.log(heightFull+\"--\"+heightFormSearch+\"--\"+heightSVG+\"--\"+heightHeader+\"--\"+heightDetail);\r\n" + 
				"\r\n" + 
				"	$(\"#dboard-notify\").css(\"height\",heightDetail+\"px\");\r\n" + 
				"}");
	}

	@Override
	public void configComponent() {

	}
	
	private void loadData() {
		try {
			JsonObject jsonCountResponse = TaskServiceUtil.getCountDashboard(SessionUtil.getUserId(), SessionUtil.getOrgId(),SessionUtil.getYear(),10);
			
			if(jsonCountResponse.get("status").getAsInt()==200) {
				JsonObject jsonResult = jsonCountResponse.getAsJsonObject("result");
				
				jsonCountNotDone = jsonResult.getAsJsonObject(eType.getKey()).getAsJsonObject("chuahoanthanh");
				jsonCountDone = jsonResult.getAsJsonObject(eType.getKey()).getAsJsonObject("dahoanthanh");
			} else {
				System.out.println(jsonCountResponse);
				NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại!", NotificationTypeEnum.ERROR);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
