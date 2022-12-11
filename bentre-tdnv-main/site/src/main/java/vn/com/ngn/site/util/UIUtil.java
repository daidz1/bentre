package vn.com.ngn.site.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.UI;

import vn.com.ngn.site.enums.DocOfEnum;
import vn.com.ngn.site.enums.DocTypeEnum;
import vn.com.ngn.site.enums.TaskAssignmentStatusEnum;
import vn.com.ngn.site.enums.TaskAssignmentTypeEnum;
import vn.com.ngn.site.enums.TaskStatusEnum;
import vn.com.ngn.site.enums.TaskTypeEnum;
import vn.com.ngn.site.util.service.TaskServiceUtil;
import vn.com.ngn.site.views.main.MainView;

public class UIUtil {
	public static MainView getMainView() {
		return (MainView) UI.getCurrent().getChildren()
				.filter(component -> component.getClass() == MainView.class).findFirst().orElse(null);
	}
	
	public static Map<String, Integer> getCountForMenu(String userId, String orgId, int year,String token) {
		Map<String, Integer> mapCount = new HashMap<String, Integer>();
		try {
			JsonObject jsonResponse = TaskServiceUtil.getCountAllTask(userId,orgId,year,token);
			
			if(jsonResponse.get("status").getAsInt()==200) {
				JsonObject jsonResult = jsonResponse.getAsJsonObject("result");
				
				JsonObject jsonVanBan = jsonResult.getAsJsonObject("vanban");
				
				JsonObject jsonDaGiaoDaHoanThanh = jsonResult.getAsJsonObject("dagiao").getAsJsonObject("dahoanthanh");
				JsonObject jsonDaGiaoChuaHoanThanh = jsonResult.getAsJsonObject("dagiao").getAsJsonObject("chuahoanthanh");
				JsonObject jsonDuocGiaoDaHoanThanh = jsonResult.getAsJsonObject("duocgiao").getAsJsonObject("dahoanthanh");
				JsonObject jsonDuocGiaoChuaHoanThanh = jsonResult.getAsJsonObject("duocgiao").getAsJsonObject("chuahoanthanh");
				JsonObject jsonHoTroDaHoanThanh = jsonResult.getAsJsonObject("theodoi").getAsJsonObject("dahoanthanh");
				JsonObject jsonHoTroChuaHoanThanh = jsonResult.getAsJsonObject("theodoi").getAsJsonObject("chuahoanthanh");
				JsonObject jsonGiaoViecThayDaHoanThanh = jsonResult.getAsJsonObject("giaoviecthay").getAsJsonObject("dahoanthanh");
				JsonObject jsonGiaoViecThayChuaHoanThanh = jsonResult.getAsJsonObject("giaoviecthay").getAsJsonObject("chuahoanthanh");
				JsonObject jsonTheoDoiThayDaHoanThanh = jsonResult.getAsJsonObject("theodoithay").getAsJsonObject("dahoanthanh");
				JsonObject jsonTheoDoiThayChuaHoanThanh = jsonResult.getAsJsonObject("theodoithay").getAsJsonObject("chuahoanthanh");
				
				//Dzung code
				JsonObject jsonDonViDuocGiao = jsonResult.getAsJsonObject("duocgiao").getAsJsonObject("nhiemvudonvi");
				System.out.println("===donviduocgiao: "+jsonDonViDuocGiao.toString()+"===");
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatusAssignmentTypeAssignmentStatus(TaskTypeEnum.DUOCGIAO, TaskStatusEnum.CHUAHOANTHANH, TaskAssignmentTypeEnum.ORGANIZATION, TaskAssignmentStatusEnum.CHUAPHAN_CANBO), jsonDonViDuocGiao.get("chuaphancanbo").getAsInt());
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatusAssignmentTypeAssignmentStatus(TaskTypeEnum.DUOCGIAO, TaskStatusEnum.CHUAHOANTHANH, TaskAssignmentTypeEnum.ORGANIZATION, TaskAssignmentStatusEnum.DAPHAN_CANBO), jsonDonViDuocGiao.get("daphancanbo").getAsInt());
				
				JsonObject jsonDonViTheoDoi = jsonResult.getAsJsonObject("theodoi").getAsJsonObject("nhiemvudonvi");
				System.out.println("===donvitheodoi: "+jsonDonViTheoDoi.toString()+"===");
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatusAssignmentTypeAssignmentStatus(TaskTypeEnum.THEODOI, TaskStatusEnum.CHUAHOANTHANH, TaskAssignmentTypeEnum.ORGANIZATION, TaskAssignmentStatusEnum.CHUAPHAN_CANBO), jsonDonViTheoDoi.get("chuaphancanbo").getAsInt());
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatusAssignmentTypeAssignmentStatus(TaskTypeEnum.THEODOI, TaskStatusEnum.CHUAHOANTHANH, TaskAssignmentTypeEnum.ORGANIZATION, TaskAssignmentStatusEnum.DAPHAN_CANBO), jsonDonViTheoDoi.get("daphancanbo").getAsInt());
				//end Dzung code
				
				mapCount.put(DocEnumUtil.createKeyByDocEnum(DocTypeEnum.VANBANDEN, DocOfEnum.SELF), jsonVanBan.get("vanbanden").getAsInt());
				mapCount.put(DocEnumUtil.createKeyByDocEnum(DocTypeEnum.VANBANDEN, DocOfEnum.ALL), jsonVanBan.get("tatcavanbanden").getAsInt());
				mapCount.put(DocEnumUtil.createKeyByDocEnum(DocTypeEnum.VANBANDI, DocOfEnum.SELF), jsonVanBan.get("vanbandi").getAsInt());
				mapCount.put(DocEnumUtil.createKeyByDocEnum(DocTypeEnum.VANBANDI, DocOfEnum.ALL), jsonVanBan.get("tatcavanbandi").getAsInt());
				
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.DAGIAO, TaskStatusEnum.DAHOANTHANH_KHONGHAN), jsonDaGiaoDaHoanThanh.get("khonghan").getAsInt());
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.DAGIAO, TaskStatusEnum.DAHOANTHANH_QUAHAN), jsonDaGiaoDaHoanThanh.get("quahan").getAsInt());
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.DAGIAO, TaskStatusEnum.DAHOANTHANH_TRONGHAN), jsonDaGiaoDaHoanThanh.get("tronghan").getAsInt());
				
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.DAGIAO, TaskStatusEnum.CHUAHOANTHANH_KHONGHAN), jsonDaGiaoChuaHoanThanh.get("khonghan").getAsInt());
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.DAGIAO, TaskStatusEnum.CHUAHOANTHANH_QUAHAN), jsonDaGiaoChuaHoanThanh.get("quahan").getAsInt());
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.DAGIAO, TaskStatusEnum.CHUAHOANTHANH_TRONGHAN), jsonDaGiaoChuaHoanThanh.get("tronghan").getAsInt());
				
				int allDaGiaoDaHoanThanh = jsonDaGiaoDaHoanThanh.get("khonghan").getAsInt() + jsonDaGiaoDaHoanThanh.get("quahan").getAsInt() + jsonDaGiaoDaHoanThanh.get("tronghan").getAsInt();
				int allDaGiaoChuaHoanThanh = jsonDaGiaoChuaHoanThanh.get("khonghan").getAsInt() + jsonDaGiaoChuaHoanThanh.get("quahan").getAsInt() + jsonDaGiaoChuaHoanThanh.get("tronghan").getAsInt();
				
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.DAGIAO, TaskStatusEnum.DAHOANTHANH), allDaGiaoDaHoanThanh);
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.DAGIAO, TaskStatusEnum.CHUAHOANTHANH), allDaGiaoChuaHoanThanh);
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.DAGIAO, TaskStatusEnum.TATCA), allDaGiaoDaHoanThanh+allDaGiaoChuaHoanThanh);
				
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.DUOCGIAO, TaskStatusEnum.DAHOANTHANH_KHONGHAN), jsonDuocGiaoDaHoanThanh.get("khonghan").getAsInt());
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.DUOCGIAO, TaskStatusEnum.DAHOANTHANH_QUAHAN), jsonDuocGiaoDaHoanThanh.get("quahan").getAsInt());
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.DUOCGIAO, TaskStatusEnum.DAHOANTHANH_TRONGHAN), jsonDuocGiaoDaHoanThanh.get("tronghan").getAsInt());
				
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.DUOCGIAO, TaskStatusEnum.CHUAHOANTHANH_KHONGHAN), jsonDuocGiaoChuaHoanThanh.get("khonghan").getAsInt());
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.DUOCGIAO, TaskStatusEnum.CHUAHOANTHANH_QUAHAN), jsonDuocGiaoChuaHoanThanh.get("quahan").getAsInt());
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.DUOCGIAO, TaskStatusEnum.CHUAHOANTHANH_TRONGHAN), jsonDuocGiaoChuaHoanThanh.get("tronghan").getAsInt());

				int allDuocGiaoDaHoanThanh = jsonDuocGiaoDaHoanThanh.get("khonghan").getAsInt() + jsonDuocGiaoDaHoanThanh.get("quahan").getAsInt() + jsonDuocGiaoDaHoanThanh.get("tronghan").getAsInt();
				int allDuocChuaHoanThanh = jsonDuocGiaoChuaHoanThanh.get("khonghan").getAsInt() + jsonDuocGiaoChuaHoanThanh.get("quahan").getAsInt() + jsonDuocGiaoChuaHoanThanh.get("tronghan").getAsInt();
				
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.DUOCGIAO, TaskStatusEnum.DAHOANTHANH), allDuocGiaoDaHoanThanh);
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.DUOCGIAO, TaskStatusEnum.CHUAHOANTHANH), allDuocChuaHoanThanh);
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.DUOCGIAO, TaskStatusEnum.TATCA), allDuocGiaoDaHoanThanh+allDuocChuaHoanThanh);
				
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.THEODOI, TaskStatusEnum.DAHOANTHANH_KHONGHAN), jsonHoTroDaHoanThanh.get("khonghan").getAsInt());
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.THEODOI, TaskStatusEnum.DAHOANTHANH_QUAHAN), jsonHoTroDaHoanThanh.get("quahan").getAsInt());
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.THEODOI, TaskStatusEnum.DAHOANTHANH_TRONGHAN), jsonHoTroDaHoanThanh.get("tronghan").getAsInt());
				
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.THEODOI, TaskStatusEnum.CHUAHOANTHANH_KHONGHAN), jsonHoTroChuaHoanThanh.get("khonghan").getAsInt());
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.THEODOI, TaskStatusEnum.CHUAHOANTHANH_QUAHAN), jsonHoTroChuaHoanThanh.get("quahan").getAsInt());
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.THEODOI, TaskStatusEnum.CHUAHOANTHANH_TRONGHAN), jsonHoTroChuaHoanThanh.get("tronghan").getAsInt());
			
				int allTheoDoiDaHoanThanh = jsonHoTroDaHoanThanh.get("khonghan").getAsInt() + jsonHoTroDaHoanThanh.get("quahan").getAsInt() + jsonHoTroDaHoanThanh.get("tronghan").getAsInt();
				int allTheoDoiChuaHoanThanh = jsonHoTroChuaHoanThanh.get("khonghan").getAsInt() + jsonHoTroChuaHoanThanh.get("quahan").getAsInt() + jsonHoTroChuaHoanThanh.get("tronghan").getAsInt();
				
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.THEODOI, TaskStatusEnum.DAHOANTHANH), allTheoDoiDaHoanThanh);
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.THEODOI, TaskStatusEnum.CHUAHOANTHANH), allTheoDoiChuaHoanThanh);
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.THEODOI, TaskStatusEnum.TATCA), allTheoDoiDaHoanThanh+allTheoDoiChuaHoanThanh);
				
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.GIAOVIECTHAY, TaskStatusEnum.DAHOANTHANH_KHONGHAN), jsonGiaoViecThayDaHoanThanh.get("khonghan").getAsInt());
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.GIAOVIECTHAY, TaskStatusEnum.DAHOANTHANH_QUAHAN), jsonGiaoViecThayDaHoanThanh.get("quahan").getAsInt());
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.GIAOVIECTHAY, TaskStatusEnum.DAHOANTHANH_TRONGHAN), jsonGiaoViecThayDaHoanThanh.get("tronghan").getAsInt());
				
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.GIAOVIECTHAY, TaskStatusEnum.CHUAHOANTHANH_KHONGHAN), jsonGiaoViecThayChuaHoanThanh.get("khonghan").getAsInt());
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.GIAOVIECTHAY, TaskStatusEnum.CHUAHOANTHANH_QUAHAN), jsonGiaoViecThayChuaHoanThanh.get("quahan").getAsInt());
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.GIAOVIECTHAY, TaskStatusEnum.CHUAHOANTHANH_TRONGHAN), jsonGiaoViecThayChuaHoanThanh.get("tronghan").getAsInt());
				
				int allGiaoViecThayDaHoanThanh = jsonGiaoViecThayDaHoanThanh.get("khonghan").getAsInt() + jsonGiaoViecThayDaHoanThanh.get("quahan").getAsInt() + jsonGiaoViecThayDaHoanThanh.get("tronghan").getAsInt();
				int allGiaoViecThayChuaHoanThanh = jsonGiaoViecThayChuaHoanThanh.get("khonghan").getAsInt() + jsonGiaoViecThayChuaHoanThanh.get("quahan").getAsInt() + jsonGiaoViecThayChuaHoanThanh.get("tronghan").getAsInt();
				
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.GIAOVIECTHAY, TaskStatusEnum.DAHOANTHANH), allGiaoViecThayDaHoanThanh);
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.GIAOVIECTHAY, TaskStatusEnum.CHUAHOANTHANH), allGiaoViecThayChuaHoanThanh);
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.GIAOVIECTHAY, TaskStatusEnum.TATCA), allGiaoViecThayDaHoanThanh+allGiaoViecThayChuaHoanThanh);
				
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.THEODOITHAY, TaskStatusEnum.DAHOANTHANH_KHONGHAN), jsonTheoDoiThayDaHoanThanh.get("khonghan").getAsInt());
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.THEODOITHAY, TaskStatusEnum.DAHOANTHANH_QUAHAN), jsonTheoDoiThayDaHoanThanh.get("quahan").getAsInt());
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.THEODOITHAY, TaskStatusEnum.DAHOANTHANH_TRONGHAN), jsonTheoDoiThayDaHoanThanh.get("tronghan").getAsInt());
				
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.THEODOITHAY, TaskStatusEnum.CHUAHOANTHANH_KHONGHAN), jsonTheoDoiThayChuaHoanThanh.get("khonghan").getAsInt());
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.THEODOITHAY, TaskStatusEnum.CHUAHOANTHANH_QUAHAN), jsonTheoDoiThayChuaHoanThanh.get("quahan").getAsInt());
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.THEODOITHAY, TaskStatusEnum.CHUAHOANTHANH_TRONGHAN), jsonTheoDoiThayChuaHoanThanh.get("tronghan").getAsInt());
				
				int allTheoDoiThayDaHoanThanh = jsonTheoDoiThayDaHoanThanh.get("khonghan").getAsInt() + jsonTheoDoiThayDaHoanThanh.get("quahan").getAsInt() + jsonTheoDoiThayDaHoanThanh.get("tronghan").getAsInt();
				int allTheoDoiThayChuaHoanThanh = jsonTheoDoiThayChuaHoanThanh.get("khonghan").getAsInt() + jsonTheoDoiThayChuaHoanThanh.get("quahan").getAsInt() + jsonTheoDoiThayChuaHoanThanh.get("tronghan").getAsInt();
				
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.THEODOITHAY, TaskStatusEnum.DAHOANTHANH), allTheoDoiThayDaHoanThanh);
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.THEODOITHAY, TaskStatusEnum.CHUAHOANTHANH), allTheoDoiThayChuaHoanThanh);
				mapCount.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.THEODOITHAY, TaskStatusEnum.TATCA), allTheoDoiThayDaHoanThanh+allTheoDoiThayChuaHoanThanh);
				
				return mapCount;
			} else {
				return mapCount;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return mapCount;
		}
	}
}
