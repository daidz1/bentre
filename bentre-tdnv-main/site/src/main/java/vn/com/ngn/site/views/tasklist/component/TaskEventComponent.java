package vn.com.ngn.site.views.tasklist.component;

import java.io.IOException;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.util.GeneralUtil;
import vn.com.ngn.site.util.LocalDateUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;

public class TaskEventComponent extends TaskInfoComponent{
	private String taskId;
	public TaskEventComponent(String taskId,JsonArray jsonArray) {
		this.taskId = taskId;
		this.jsonArray = jsonArray;

		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		super.buildLayout();
		
		if(jsonArray.size()>0) {
			for(JsonElement jsonEle : jsonArray) {
				this.add(buildEventLayout(jsonEle.getAsJsonObject()));
			}
		} else {
			Span span = new Span("Chưa có thao tác nào được thực hiện.");
			
			span.getStyle().set("margin-left", "11px");
			span.getStyle().set("font-style", "italic");
			span.getStyle().set("color", "7f7c7c");
			
			this.add(span);
		}
		
		this.setPadding(false);
	}

	@Override
	public void configComponent() {
		super.configComponent();
	}
	
	public void reload() throws IOException {
		JsonObject jsonResponseGet = TaskServiceUtil.getTaskDetail(taskId);
		
		if(jsonResponseGet.get("status").getAsInt()==200) {
			jsonArray = jsonResponseGet.getAsJsonObject("result").get("events").getAsJsonArray();
			
			this.removeAll();
			
			for(JsonElement jsonEle : jsonArray) {
				this.add(buildEventLayout(jsonEle.getAsJsonObject()));
			}
		} else {
			System.out.println(jsonResponseGet);
		}
	}

	private HorizontalLayout buildEventLayout(JsonObject jsonEvent) {
		HorizontalLayout hInfo = new HorizontalLayout();
		
		String title = jsonEvent.get("title").getAsString();
		String fullName = (jsonEvent.has("creator") && !jsonEvent.getAsJsonObject("creator").get("fullName").isJsonNull()) ? jsonEvent.getAsJsonObject("creator").get("fullName").getAsString() : "Hệ thống";
		JsonObject jsonDescription = jsonEvent.getAsJsonObject("descriptions");
		String time = LocalDateUtil.formatLocalDateTime(LocalDateUtil.longToLocalDateTime(jsonEvent.get("createdTime").getAsLong()),LocalDateUtil.dateTimeFormater1);
		//String actionKey = "none";
		String actionKey = !jsonEvent.get("action").isJsonNull() && jsonEvent.has("action") ? jsonEvent.get("action").getAsString() : "none";
		
		Icon icon = VaadinIcon.NOTEBOOK.create();
		VerticalLayout vContent = new VerticalLayout();
		HorizontalLayout hTitle = new HorizontalLayout();
		Span spanTitle = new Span(title);
		Span spanFullName = new Span(fullName);
		Span spanTime = new Span(time);
		
		Html htmlDescription = null;
		
		switch (actionKey) {
		case "nhiemvumoi":
			icon = VaadinIcon.ARROW_FORWARD.create();
			icon.getStyle().set("background", "#1676f3");
			spanTitle.getStyle().set("color", "#1676f3");
			break;
		case "capnhatnhiemvu":
			icon = VaadinIcon.EDIT.create();
			icon.getStyle().set("background", "#e29b5e");
			spanTitle.getStyle().set("color", "#e29b5e");
			break;
		case "capnhattiendo":
			icon = VaadinIcon.PROGRESSBAR.create();
			icon.getStyle().set("background", "#208090");
			spanTitle.getStyle().set("color", "#208090");
			break;
		case "ykienvaphanhoi":
			icon = VaadinIcon.CHAT.create();
			icon.getStyle().set("background", "#8d9e37ad");
			spanTitle.getStyle().set("color", "#8d9e37ad");
			break;
		case "hoanthanhnhiemvu":
			icon = VaadinIcon.CHECK_CIRCLE.create();
			icon.getStyle().set("background", "rgb(27 181 46)");
			spanTitle.getStyle().set("color", "rgb(27 181 46)");
			break;
		case "trieuhoinhiemvu":
			icon = VaadinIcon.REFRESH.create();
			icon.getStyle().set("background", "rgb(27 167 134)");
			spanTitle.getStyle().set("color", "rgb(27 167 134)");
			break;
		case "daxoanhiemvu":
			icon = VaadinIcon.TRASH.create();
			icon.getStyle().set("background", "#b51818");
			spanTitle.getStyle().set("color", "#b51818");
			break; 
		case "danhgianhiemvu":
			icon = VaadinIcon.STAR.create();
			icon.getStyle().set("background", "#ffce44");
			spanTitle.getStyle().set("color", "#ffce44");
			break; 
//		case "thaydoichutri":
//			icon = VaadinIcon.STAR.create();
//			icon.getStyle().set("background", "#ffce44");
//			spanTitle.getStyle().set("color", "#ffce44");
//			break; 
//		case "thaydoinguoigiao":
//			icon = VaadinIcon.STAR.create();
//			icon.getStyle().set("background", "#ffce44");
//			spanTitle.getStyle().set("color", "#ffce44");
//			break; 
		default:
			break;
		}
		
		String strDes = "";
		for(Entry<String,JsonElement> entry : jsonDescription.entrySet()) {
			strDes+="<div style='margin-bottom:3px;width:100%'>"
					+ "<b class='caption-head'>"+entry.getKey()+"</b> "
					+ "<div style='width: calc(100% - 112px); display: inline-block;'>: "+entry.getValue().getAsString()+"</div>"
					+ "</div>";
		}
		htmlDescription = new Html("<div style='width:100%'>"+strDes+"</div>");
		
		hInfo.add(icon,vContent);
		icon.setSize("var(--lumo-font-size-m)");
		icon.addClassName("event-icon");
		
		vContent.add(hTitle,htmlDescription);
		
		hTitle.add(spanTitle,spanFullName,spanTime);
		spanTitle.addClassName("event-title");
		
		vContent.setPadding(false);
		vContent.addClassName("event-content-block");
		
		hInfo.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		hInfo.setWidthFull();
		hInfo.addClassName("event-block");
		
		return hInfo;
	}
}
