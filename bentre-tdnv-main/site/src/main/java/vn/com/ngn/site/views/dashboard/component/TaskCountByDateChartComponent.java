package vn.com.ngn.site.views.dashboard.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.AxisTitle;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataLabels;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.PlotOptionsLine;
import com.vaadin.flow.component.charts.model.Shape;
import com.vaadin.flow.component.charts.model.YAxis;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.enums.TaskTypeEnum;
import vn.com.ngn.site.model.TaskFilterModel;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;

@SuppressWarnings("serial")
public class TaskCountByDateChartComponent extends VerticalLayout {
	public TaskCountByDateChartComponent(TaskTypeEnum eType) {
		try {
			TaskFilterModel modelFilter = new TaskFilterModel();

			modelFilter.setLimit(7);
			modelFilter.setUserid(SessionUtil.getUserId());
			modelFilter.setOrganizationId(SessionUtil.getOrgId());
			modelFilter.setCategorykey(eType.getKey());
			
			JsonObject jsonResponse = TaskServiceUtil.getTaskSumByDate(modelFilter);

			Number[] listCountTaskCreateByDate = new Number[7];
			Number[] listCountTaskCompleteByDate = new Number[7];
			String[] listName = new String[7];
			if(jsonResponse.get("status").getAsInt()==200) {
				JsonArray jsonTaskList = jsonResponse.get("result").getAsJsonArray();

				int i = 0;
				for(JsonElement jsonEle : jsonTaskList) {
					JsonObject jsonTaskSum = jsonEle.getAsJsonObject();
					String datetime = jsonTaskSum.get("dateTime").getAsString();
					int taskNumber = jsonTaskSum.get("taskNumber").getAsInt();
					int taskCompleted = jsonTaskSum.get("taskCompleted").getAsInt();
					
					listName[i] = datetime.substring(5,datetime.length());
					listCountTaskCreateByDate[i] = taskNumber;
					listCountTaskCompleteByDate[i] = taskCompleted;
					i++;
				}
				
				Chart chart = new Chart();

				Configuration configuration = chart.getConfiguration();
				configuration.getChart().setType(ChartType.LINE);

				configuration.getTitle()
				.setText("Thống kê nhiệm vụ trong 7 ngày gần nhất");

				configuration.getxAxis().setCategories(listName);

				YAxis yAxis = configuration.getyAxis();
				yAxis.setTitle(new AxisTitle("Số lượng nhiệm vụ"));

				configuration
				.getTooltip()
				.setFormatter(
						"'<b>'+ this.series.name +'</b><br/>'+this.x +': '+ this.y +'°C'");

				PlotOptionsLine plotOptions = new PlotOptionsLine();
				plotOptions.setEnableMouseTracking(false);
				configuration.setPlotOptions(plotOptions);

				DataSeries ds = new DataSeries();
				ds.setName("Nhiệm vụ được tạo");
				ds.setData(listCountTaskCreateByDate);
				DataLabels callout = new DataLabels(true);
				callout.setShape(Shape.CALLOUT);
				callout.setY(-12);
				ds.get(5).setDataLabels(callout);
				configuration.addSeries(ds);

				ds = new DataSeries();
				ds.setName("Nhiệm vụ hoàn thành");
				ds.setData(listCountTaskCompleteByDate);
				ds.get(6).setDataLabels(callout);
				configuration.addSeries(ds);

				chart.setHeight("280px");
				
				this.add(chart);
			} else {
				System.out.println(jsonResponse);
				NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại", NotificationTypeEnum.ERROR);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
