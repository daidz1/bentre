package vn.com.ngn.site.views.dashboard;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.enums.PermissionEnum;
import vn.com.ngn.site.enums.TaskStatusEnum;
import vn.com.ngn.site.enums.TaskTypeEnum;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;
import vn.com.ngn.site.views.main.MainView;

@SuppressWarnings("serial")
@Route(value = "dashboard", layout = MainView.class)
//@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Tổng quan")
public class DashboardView extends VerticalLayout implements LayoutInterface {
	Board hTest = new Board();

    public DashboardView() {
    	buildLayout();
    	configComponent();
    }

	@Override
	public void buildLayout() {
		this.add(hTest);
		try {
			JsonObject jsonResponse = TaskServiceUtil.getCountDashboard(SessionUtil.getUserId(), SessionUtil.getOrgId(),SessionUtil.getYear(),10);
		
			if(jsonResponse.get("status").getAsInt()==200) {
				JsonObject jsonResult = jsonResponse.getAsJsonObject("result");
				
				JsonObject jsonDaGiao = jsonResult.getAsJsonObject("dagiao");
				JsonObject jsonDuocGiao = jsonResult.getAsJsonObject("duocgiao");
				JsonObject jsonTheoDoi = jsonResult.getAsJsonObject("theodoi");
				if(SessionUtil.isHasPermission(PermissionEnum.giaonhiemvu)) {
					DashboardInfoLayout dbDaGiaoChuaHoanThanh = new DashboardInfoLayout(TaskTypeEnum.DAGIAO.getKey(),TaskStatusEnum.CHUAHOANTHANH.getKey(),"Nhiệm vụ đã giao chưa hoàn thành",jsonDaGiao.getAsJsonObject("chuahoanthanh"));
					DashboardInfoLayout dbDaGiaoDaHoanThanh = new DashboardInfoLayout(TaskTypeEnum.DAGIAO.getKey(),TaskStatusEnum.DAHOANTHANH.getKey(),"Nhiệm vụ đã giao đã hoàn thành",jsonDaGiao.getAsJsonObject("dahoanthanh"));				
					hTest.addRow(dbDaGiaoChuaHoanThanh,dbDaGiaoDaHoanThanh);
				}
				
				DashboardInfoLayout dbDuocChuaHoanThanh = new DashboardInfoLayout(TaskTypeEnum.DUOCGIAO.getKey(),TaskStatusEnum.CHUAHOANTHANH.getKey(),"Nhiệm vụ được giao chưa hoàn thành",jsonDuocGiao.getAsJsonObject("chuahoanthanh"));
				DashboardInfoLayout dbDuocoDaHoanThanh = new DashboardInfoLayout(TaskTypeEnum.DUOCGIAO.getKey(),TaskStatusEnum.DAHOANTHANH.getKey(),"Nhiệm vụ được giao đã hoàn thành",jsonDuocGiao.getAsJsonObject("dahoanthanh"));
				
				DashboardInfoLayout dbTheoDoiChuaHoanThanh = new DashboardInfoLayout(TaskTypeEnum.THEODOI.getKey(),TaskStatusEnum.CHUAHOANTHANH.getKey(),"Nhiệm vụ theo dõi chưa hoàn thành",jsonTheoDoi.getAsJsonObject("chuahoanthanh"));
				DashboardInfoLayout dbTheoDoiDaHoanThanh = new DashboardInfoLayout(TaskTypeEnum.THEODOI.getKey(),TaskStatusEnum.DAHOANTHANH.getKey(),"Nhiệm vụ theo dõi đã hoàn thành",jsonTheoDoi.getAsJsonObject("dahoanthanh"));
				
				hTest.addRow(dbDuocChuaHoanThanh,dbDuocoDaHoanThanh);
				hTest.addRow(dbTheoDoiChuaHoanThanh,dbTheoDoiDaHoanThanh);
			} else {
				System.out.println(jsonResponse);
				NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại!", NotificationTypeEnum.ERROR);
			}
		} catch (IOException e) {
			e.printStackTrace();
			NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại!", NotificationTypeEnum.ERROR);
		}
		
//		hTest.addRow(new DashboardInfoLayout());
//		hTest.addRow(new DashboardInfoLayout());
//		hTest.addRow(new DashboardInfoLayout());
		
		hTest.setWidthFull();
		
		this.setSizeFull();
		this.setId("dashboard-view");
	}

	@Override
	public void configComponent() {
		
	}
}
