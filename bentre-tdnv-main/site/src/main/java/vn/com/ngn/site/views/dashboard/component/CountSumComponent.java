package vn.com.ngn.site.views.dashboard.component;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.enums.TaskStatusEnum;
import vn.com.ngn.site.enums.TaskTypeEnum;

@SuppressWarnings("serial")
public class CountSumComponent extends VerticalLayout {
	public CountSumComponent(TaskTypeEnum eType,JsonObject jsonCountNotDone,JsonObject jsonCountDone) {
		JsonObject jsonSumNotDone = jsonCountNotDone.getAsJsonObject("sum");
		JsonObject jsonSumDone = jsonCountDone.getAsJsonObject("sum");
		
		int tronghanNotDone = jsonSumNotDone.get("tronghan").getAsInt();
		int khonghanNotDone = jsonSumNotDone.get("khonghan").getAsInt();
		int quahanNotDone = jsonSumNotDone.get("quahan").getAsInt();
		int sumNotDone = tronghanNotDone + khonghanNotDone + quahanNotDone;
		
		int tronghanDone = jsonSumDone.get("tronghan").getAsInt();
		int khonghanDone = jsonSumDone.get("khonghan").getAsInt();
		int quahanDone = jsonSumDone.get("quahan").getAsInt();
		int sumDone = tronghanDone + khonghanDone + quahanDone;
		
		int sumAll = sumNotDone + sumDone;
		float percentNotDone = ((float)sumNotDone/sumAll)*100;

		Html htmlSumAll = new Html("<div style='margin-top: 10px;'>"
				+ "<span style='display: inline-block; font-weight: 500; font-size: 40px; margin-right: 3px; color: #3270e0;'>"+sumAll+"</span>"
				+ "<span style='color:#928a8a;'>nhiệm vụ</span>"
				+ "</div>");

		Html htmlBar = new Html("<div style='width:100%'>"
				+ "<div style='display:inline-block;background: #3f85dc;height:8px; width:"+percentNotDone+"%'></div>"
				+ "<div style='display:inline-block;background: #ef7777;height:8px; width: "+(100-percentNotDone)+"%'></div>"
				+ "</div>");
		
		Html htmlCountOfBar = new Html("<div style='margin-bottom: 25px; font-size:var(--lumo-font-size-l)'>"
				+ "<span style='font-weight: 500; color: #3f85dc;'>"+sumNotDone+"</span> <span style='color:#989898'>đang thực hiện</span>"
				+ "<span style='margin-left: 11px;font-weight: 500; color: #ef7777;'>"+sumDone+"</span> <span style='color:#989898'>đã hoàn thành</span>"
				+ "</div>");

		HorizontalLayout hSumDetail = new HorizontalLayout();

		Html htmlSumDetailDoing = new Html("<div style='width:100%;border-right: 1px solid #dadada;line-height: 1.5;font-size:var(--lumo-font-size-l)'>"
				+ "<div style='font-weight:bold'>Chưa hoàn thành</div>"
				+ "<div class='sum-detail' type='"+eType.getKey()+"' status='"+TaskStatusEnum.CHUAHOANTHANH_KHONGHAN.getKey()+"'><span style='display: inline-block; font-weight: 500;color: green;'>"+khonghanNotDone+"</span> <span style='color: #8e8888;'>Không hạn</span></div>"
				+ "<div class='sum-detail' type='"+eType.getKey()+"' status='"+TaskStatusEnum.CHUAHOANTHANH_TRONGHAN.getKey()+"'><span style='display: inline-block; font-weight: 500;color: #177bd0;'>"+tronghanNotDone+"</span> <span style='color: #8e8888;'>Trong hạn</span></div>"
				+ "<div class='sum-detail' type='"+eType.getKey()+"' status='"+TaskStatusEnum.CHUAHOANTHANH_QUAHAN.getKey()+"'><span style='display: inline-block; font-weight: 500; color: red;'>"+quahanNotDone+"</span> <span style='color: #8e8888;'>Quá hạn</span></div>"
				+ "</div>");

		Html htmlSumDetailDoing2 = new Html("<div style='width:100%;padding-left: 10px;line-height: 1.5;font-size:var(--lumo-font-size-l)'>"
				+ "<div style='font-weight:bold'>Đã hoàn thành</div>"
				+ "<div class='sum-detail' type='"+eType.getKey()+"' status='"+TaskStatusEnum.DAHOANTHANH_KHONGHAN.getKey()+"'><span style='display: inline-block; font-weight: 500; color: green;'>"+khonghanDone+"</span> <span style='color: #8e8888;'>Không hạn</span></div>"
				+ "<div class='sum-detail' type='"+eType.getKey()+"' status='"+TaskStatusEnum.DAHOANTHANH_TRONGHAN.getKey()+"'><span style='display: inline-block; font-weight: 500; color: #177bd0;'>"+tronghanDone+"</span> <span style='color: #8e8888;'>Trong hạn</span></div>"
				+ "<div class='sum-detail' type='"+eType.getKey()+"' status='"+TaskStatusEnum.DAHOANTHANH_QUAHAN.getKey()+"'><span style='display: inline-block; font-weight: 500; color: red;'>"+quahanDone+"</span> <span style='color: #8e8888;'>Quá hạn</span></div>"
				+ "</div>");

		hSumDetail.add(htmlSumDetailDoing,htmlSumDetailDoing2);
		hSumDetail.setWidthFull();

		this.add(htmlSumAll);
		this.add(htmlBar);
		this.add(htmlCountOfBar);
		this.add(hSumDetail);
		
		this.setPadding(false);
		this.setHeightFull();
	}
}
