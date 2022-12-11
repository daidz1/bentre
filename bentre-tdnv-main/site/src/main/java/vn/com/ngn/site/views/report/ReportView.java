package vn.com.ngn.site.views.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.enums.TaskTypeEnum;
import vn.com.ngn.site.model.ReportModel;
import vn.com.ngn.site.model.TaskFilterModel;
import vn.com.ngn.site.module.TaskFilterModule;
import vn.com.ngn.site.util.LocalDateUtil;
import vn.com.ngn.site.util.service.ReportServiceUtil;
import vn.com.ngn.site.views.main.MainView;
import vn.com.ngn.site.views.report.excel.ReportDaGiaoExcel;
import vn.com.ngn.site.views.report.excel.ReportDuocGiaoExcel;
import vn.com.ngn.site.views.report.excel.ReportHoTroExcel;
import vn.com.ngn.site.views.report.excel.ReportTatCaExcel;

@SuppressWarnings("serial")
@Route(value = "report", layout = MainView.class)
@PageTitle("Báo cáo thống kê")
public class ReportView extends VerticalLayout implements LayoutInterface {
	private TaskFilterModule taskFilter = new TaskFilterModule();
	private VerticalLayout vDisplay = new VerticalLayout();
	
	public ReportView() {
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.add(taskFilter);
		this.add(vDisplay);

		taskFilter.setForReporting();
	}

	@Override
	public void configComponent() {
		taskFilter.getBtnReport().addClickListener(e->{
			try {
				loadData();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}

	private void loadData() throws IOException {
		taskFilter.getBtnDownload().setEnabled(false);
		String eType = taskFilter.getCmbTaskType().getValue().getKey();

		String display = "<table class='table-report'>"
				+"<thead>"
				+ "<th style='width:35px'>STT</th>"
				+ "<th style='width:35%'>Trích yếu/Nội dung giao</th>";
		if(eType==null) {
			display+="<th>Người giao</th>"
					+ "<th>Người xử lý</th>"
					+"<th>Người theo dõi</th>";
		}
		if(eType==TaskTypeEnum.DAGIAO.getKey()) {
			display+="<th>Người xử lý</th>"
					+"<th>Người theo dõi</th>";
		}
		if(eType==TaskTypeEnum.DUOCGIAO.getKey()) {
			display+= "<th>Người giao</th>"
					+ "<th>Ngày theo dõi</th>";
		}
		if(eType==TaskTypeEnum.THEODOI.getKey()) {
			display+="<th>Người giao</th>"
					+ "<th>Người xử lý</th>"
					+"<th>Người theo dõi</th>";
		}
		display+="<th>Ngày giao</th>"
				+"<th>Hạn xử lý</th>"
				+ "<th>Tình trạng xử lý</th>"
				+ "<th style='width:10%'>Kết quả</th>"
				+ "</thead>"
				+ "<tbody>";
		TaskFilterModel modelFilter = taskFilter.getTaskFilterAll();
		JsonObject jsonResponse = ReportServiceUtil.getReport(modelFilter);

		int stt = 1;
		List<ReportModel> listReport = new ArrayList<ReportModel>();
		if(jsonResponse.get("status").getAsInt()==200) {
			JsonArray jsonTaskList = jsonResponse.get("result").getAsJsonArray();

			for(JsonElement jsonEle : jsonTaskList) {
				JsonObject jsonTask = jsonEle.getAsJsonObject();
				JsonObject jsonOwner = jsonTask.getAsJsonObject("owner");
				JsonObject jsonAssignee = jsonTask.getAsJsonObject("assignee");
				JsonArray jsonArrFollower = jsonTask.getAsJsonArray("followersTask");
				JsonObject jsonProgress = !jsonTask.get("process").isJsonNull()? jsonTask.getAsJsonObject("process") : null;

				String title = jsonTask.get("title").getAsString();
				String description = jsonTask.get("description").getAsString();
				String owner = jsonOwner.get("fullName").getAsString()+" ("+jsonOwner.get("organizationName").getAsString()+")";
				String assignee = jsonAssignee.get("fullName").getAsString()+" ("+jsonAssignee.get("organizationName").getAsString()+")";
				String follower = "";
				String createTime = LocalDateUtil.formatLocalDateTime(LocalDateUtil.longToLocalDateTime(jsonTask.get("createdTime").getAsLong()),LocalDateUtil.dateTimeFormater1);
				String endTime = jsonTask.get("endTime").getAsLong() != 0 ? LocalDateUtil.formatLocalDateTime(LocalDateUtil.longToLocalDateTime(jsonTask.get("endTime").getAsLong()),LocalDateUtil.dateTimeFormater1) : "Không hạn";
				String progress = jsonProgress != null ? jsonProgress.get("percent").getAsString()+"%, "+jsonProgress.get("explain").getAsString() : "Chưa cập nhật";
				String result = jsonTask.get("completedTime").getAsLong() != 0 ? "Đã hoàn thành" : "Chưa hoàn thành";

				if(jsonArrFollower.size()>0) {
					for(JsonElement jsonEleFollower : jsonArrFollower) {
						JsonObject jsonFollower = jsonEleFollower.getAsJsonObject();
						follower += jsonFollower.get("fullName").getAsString()+" ("+jsonFollower.get("organizationName").getAsString()+"), ";
					}
					follower = follower.trim();
					follower = follower.substring(0,follower.length()-1);
				} else {
					follower = "Không có";
				}
				ReportModel modelReport = new ReportModel();
				modelReport.setTrichYeu(title);
				modelReport.setNoiDung(description);
				modelReport.setNguoiGiao(owner);
				modelReport.setNguoiXuLy(assignee);
				modelReport.setNguoiHoTro(follower);
				modelReport.setNgayGiao(createTime);
				modelReport.setHanXuLy(endTime);
				modelReport.setTinhTrangXuLy(progress);
				modelReport.setKetQua(result);

				listReport.add(modelReport);

				display+= "<tr>"
						+ "<td style='text-align:center'>"+(stt++)+"</td>"
						+ "<td><b>Trích yếu: </b>"+title+"<br/><b>Nội dung giao: </b>"+description+"</td>";
				if(eType==null) {
					display+="<td>"+owner+"</td>"
							+"<td>"+assignee+"</td>"
							+"<td>"+follower+"</td>";
				}
				if(eType==TaskTypeEnum.DAGIAO.getKey()) {
					display+="<td>"+assignee+"</td>"
							+"<td>"+follower+"</td>";
				}
				if(eType==TaskTypeEnum.DUOCGIAO.getKey()) {
					display+= "<td>"+owner+"</td>"
							+ "<td>"+follower+"</td>";
				}
				if(eType==TaskTypeEnum.THEODOI.getKey()) {
					display+="<td>"+owner+"</td>"
							+"<td>"+assignee+"</td>"
							+"<td>"+follower+"</td>";
				}
				display+= "<td style='text-align:center'>"+createTime+"</td>"
						+ "<td style='text-align:center'>"+endTime+"</td>"
						+ "<td>"+progress+"</td>"
						+ "<td style='text-align:center'>"+result+"</td>"
						+ "</tr>";
			}

			display+="</tbody>"
					+ "</table>";

			Html htmlDisplay = new Html(display);
			vDisplay.removeAll();
			vDisplay.add(htmlDisplay);

			if(eType==null) {
				ReportTatCaExcel excelReport = new ReportTatCaExcel();
				excelReport.setStartDate(taskFilter.getDpStart().getValue());
				excelReport.setEndDate(taskFilter.getDpEnd().getValue());
				excelReport.setListData(listReport);

				try {
					taskFilter.getBtnDownload().setHref(excelReport.createReport());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(eType==TaskTypeEnum.DAGIAO.getKey()) {
				ReportDaGiaoExcel excelReport = new ReportDaGiaoExcel();
				excelReport.setStartDate(taskFilter.getDpStart().getValue());
				excelReport.setEndDate(taskFilter.getDpEnd().getValue());
				excelReport.setListData(listReport);

				try {
					taskFilter.getBtnDownload().setHref(excelReport.createReport());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(eType==TaskTypeEnum.DUOCGIAO.getKey()) {
				ReportDuocGiaoExcel excelReport = new ReportDuocGiaoExcel();
				excelReport.setStartDate(taskFilter.getDpStart().getValue());
				excelReport.setEndDate(taskFilter.getDpEnd().getValue());
				excelReport.setListData(listReport);

				try {
					taskFilter.getBtnDownload().setHref(excelReport.createReport());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(eType==TaskTypeEnum.THEODOI.getKey()) {
				ReportHoTroExcel excelReport = new ReportHoTroExcel();
				excelReport.setStartDate(taskFilter.getDpStart().getValue());
				excelReport.setEndDate(taskFilter.getDpEnd().getValue());
				excelReport.setListData(listReport);

				try {
					taskFilter.getBtnDownload().setHref(excelReport.createReport());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			taskFilter.getBtnDownload().setEnabled(true);
		} else {
			System.out.println(jsonResponse);
		}
	}
}
