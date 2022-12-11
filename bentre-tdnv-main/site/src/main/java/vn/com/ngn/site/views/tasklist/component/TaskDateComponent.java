package vn.com.ngn.site.views.tasklist.component;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import vn.com.ngn.site.enums.TaskStatusEnum;
import vn.com.ngn.site.enums.TaskTypeEnum;
import vn.com.ngn.site.util.LocalDateUtil;

public class TaskDateComponent extends TaskInfoComponent{
	public TaskDateComponent(JsonObject jsonObject,String eType, String eStatus) {
		this.jsonObject = jsonObject;
		this.eType = eType;
		this.eStatus = eStatus;
		
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		
		String startTime = LocalDateUtil.formatLocalDateTime(LocalDateUtil.longToLocalDateTime(jsonObject.get("createdTime").getAsLong()),LocalDateUtil.dateTimeFormater1);
		String endTime = jsonObject.get("endTime").getAsLong()==0? "Không hạn" : LocalDateUtil.formatLocalDateTime(LocalDateUtil.longToLocalDateTime(jsonObject.get("endTime").getAsLong()),LocalDateUtil.dateTimeFormater1);
		String completeTime = jsonObject.get("completedTime").getAsLong()==0? "Đang xử lý" : LocalDateUtil.formatLocalDateTime(LocalDateUtil.longToLocalDateTime(jsonObject.get("completedTime").getAsLong()),LocalDateUtil.dateTimeFormater1);
		HorizontalLayout hDate = new HorizontalLayout();
		hDate.setWidthFull();
		
		String strHtml = "<div><b class='caption-head'>Ngày giao: </b> "+startTime+" <b style='margin-left:15px'>Hạn xử lý: </b>"+endTime+"  <b style='margin-left:15px'>Ngày hoàn thành: </b>"+completeTime+"</div>";
		
		Html html = new Html(strHtml);
		
		hDate.add(html);
		hDate.addClassName("row");
		
		this.add(hDate);
	}
}
