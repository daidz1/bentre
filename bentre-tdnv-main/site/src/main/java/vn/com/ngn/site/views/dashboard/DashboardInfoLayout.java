package vn.com.ngn.site.views.dashboard;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.Crosshair;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.XAxis;
import com.vaadin.flow.component.charts.model.YAxis;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.enums.TaskStatusEnum;
import vn.com.ngn.site.enums.TaskTypeEnum;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.views.tasklist.TaskListView;

@SuppressWarnings("serial")
public class DashboardInfoLayout extends VerticalLayout implements LayoutInterface{
	private Div divCaption = new Div();
	private HorizontalLayout hCount = new HorizontalLayout();
	private VerticalLayout vTrongHan;
	private VerticalLayout vKhongHan;
	private VerticalLayout vQuaHan;
	private VerticalLayout vChart = new VerticalLayout();
	private Chart chart = new Chart(ChartType.COLUMN);
	
	private String caption;
	private JsonObject jsonData;
	private String eType;
	private String eStatus;

	public DashboardInfoLayout(String eType,String eStatus,String caption, JsonObject jsonData) {
		this.caption = caption;
		this.jsonData = jsonData;
		this.eType = eType;
		this.eStatus = eStatus;
		
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.add(divCaption);
		this.add(hCount);
		this.add(vChart);
		
		
		divCaption.addClassNames("caption-block");
		
		JsonObject jsonSum = jsonData.get("sum").getAsJsonObject();
		
		int tronghan = jsonSum.get("tronghan").getAsInt();
		int khonghan = jsonSum.get("khonghan").getAsInt();
		int quahan = jsonSum.get("quahan").getAsInt();

		vTrongHan = buildCountBlock("Trong hạn",tronghan,"Nhiệm vụ trong hạn","#1c64af","#1c64af24");
		vKhongHan = buildCountBlock("Không hạn",khonghan,"Nhiệm vụ không hạn","#d08f18","#f7f0e3");
		vQuaHan = buildCountBlock("Quá hạn",quahan,"Nhiệm vụ quá hạn","#c12222","#c1222226");
		
		hCount.add(vTrongHan);
		hCount.add(vKhongHan);
		hCount.add(vQuaHan);

		divCaption.setText(caption+": "+(tronghan+khonghan+quahan));
		
		hCount.setWidthFull();

		this.setWidthFull();

		buildChart();
	}

	@Override
	public void configComponent() {
		vTrongHan.addClickListener(e->{
			String taskStatus="";
			if(eStatus==TaskStatusEnum.CHUAHOANTHANH.getKey()) {
				taskStatus = TaskStatusEnum.CHUAHOANTHANH_TRONGHAN.getKey();
			} else if(eStatus==TaskStatusEnum.DAHOANTHANH.getKey()) {
				taskStatus = TaskStatusEnum.DAHOANTHANH_TRONGHAN.getKey();
			}
			
			Map<String, String> mapParam = new HashMap<String, String>();
			mapParam.put("type", eType);
			mapParam.put("status", taskStatus);
			
			SessionUtil.setParam(mapParam);
			getUI().ifPresent(ui -> ui.navigate(TaskListView.class));
//			QueryParameters params = QueryParameters.simple(mapParam);
//			getUI().ifPresent(ui -> ui.navigate(TaskListView.class.getAnnotation(Route.class).value(),params));
		});
		
		vKhongHan.addClickListener(e->{
			String taskStatus="";
			if(eStatus==TaskStatusEnum.CHUAHOANTHANH.getKey()) {
				taskStatus = TaskStatusEnum.CHUAHOANTHANH_KHONGHAN.getKey();
			} else if(eStatus==TaskStatusEnum.DAHOANTHANH.getKey()) {
				taskStatus = TaskStatusEnum.DAHOANTHANH_KHONGHAN.getKey();
			}
			
			Map<String, String> mapParam = new HashMap<String, String>();
			mapParam.put("type", eType);
			mapParam.put("status", taskStatus);
			
			SessionUtil.setParam(mapParam);
			getUI().ifPresent(ui -> ui.navigate(TaskListView.class));
//			QueryParameters params = QueryParameters.simple(mapParam);
//			getUI().ifPresent(ui -> ui.navigate(TaskListView.class.getAnnotation(Route.class).value(),params));
		});
		
		vQuaHan.addClickListener(e->{
			String taskStatus="";
			if(eStatus==TaskStatusEnum.CHUAHOANTHANH.getKey()) {
				taskStatus = TaskStatusEnum.CHUAHOANTHANH_QUAHAN.getKey();
			} else if(eStatus==TaskStatusEnum.DAHOANTHANH.getKey()) {
				taskStatus = TaskStatusEnum.DAHOANTHANH_QUAHAN.getKey();
			}
			
			Map<String, String> mapParam = new HashMap<String, String>();
			mapParam.put("type", eType);
			mapParam.put("status", taskStatus);
			
			SessionUtil.setParam(mapParam);
			getUI().ifPresent(ui -> ui.navigate(TaskListView.class));
//			QueryParameters params = QueryParameters.simple(mapParam);
//			getUI().ifPresent(ui -> ui.navigate(TaskListView.class.getAnnotation(Route.class).value(),params));
		});
	}

	private VerticalLayout buildCountBlock(String title, int count, String description, String colorCode, String subColorcode) {
		VerticalLayout vCount = new VerticalLayout();

		Span lblTitle = new Span(title);
		Span lblCount = new Span(String.valueOf(count));
		Span lblDescription = new Span(description);

		vCount.add(lblTitle);
		vCount.add(lblCount);
		vCount.add(lblDescription);

		lblTitle.addClassName("title");
		lblCount.addClassName("count");
		lblDescription.addClassName("description");

		lblCount.getStyle().set("color", colorCode);

		lblTitle.getStyle().set("color", colorCode);
		lblTitle.getStyle().set("background", subColorcode);

		vCount.setWidthFull();
		vCount.addClassNames("count-block");

		return vCount;
	}

	private void buildChart() {
		vChart.add(chart);
		
		List<Number> listCount = new LinkedList<Number>();
		String[] listName = new String[10];
		String[] listId = new String[10];
		
		JsonArray jsonTop = jsonData.getAsJsonArray("top");
		int i = 0;
		for(JsonElement jsonEle : jsonTop) {
			try {
				JsonObject jsonOb = jsonEle.getAsJsonObject();
				
				listName[i] = jsonOb.getAsJsonObject("userTask").get("fullName").getAsString();
				listId[i] = jsonOb.getAsJsonObject("userTask").get("userId").getAsString()+"-"+jsonOb.getAsJsonObject("userTask").get("organizationId").getAsString();
				listCount.add(jsonOb.get("countTask").getAsInt());
				i++;
			} catch (Exception e1) {
				JsonObject jsonOb = jsonEle.getAsJsonObject();
				listName[i] = jsonOb.getAsJsonObject("userTask").get("organizationName").getAsString();
				listId[i] = jsonOb.getAsJsonObject("userTask").get("organizationId").getAsString()+"-"+jsonOb.getAsJsonObject("userTask").get("organizationId").getAsString();
				listCount.add(jsonOb.get("countTask").getAsInt());
				i++;
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
			
			if(eType==TaskTypeEnum.DAGIAO.getKey() || eType==TaskTypeEnum.THEODOI.getKey()) {
				userType=TaskTypeEnum.DUOCGIAO.getKey();
			} else if(eType==TaskTypeEnum.DUOCGIAO.getKey()) {
				userType=TaskTypeEnum.DAGIAO.getKey();
			}
			
			Map<String, String> mapParam = new HashMap<String, String>();
			mapParam.put("type", eType);
			mapParam.put("status", eStatus);
			mapParam.put("user"+userType, listId[e.getItemIndex()]);
			
			SessionUtil.setParam(mapParam);
			getUI().ifPresent(ui -> ui.navigate(TaskListView.class));
//			QueryParameters params = QueryParameters.simple(mapParam);
//			getUI().ifPresent(ui -> ui.navigate(TaskListView.class.getAnnotation(Route.class).value(),params));
		});

		vChart.addClassNames("chart-block");
	}
}
