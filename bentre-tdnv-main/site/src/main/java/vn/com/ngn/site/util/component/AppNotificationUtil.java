package vn.com.ngn.site.util.component;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.dialog.task.TaskDetailDialog;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.util.LocalDateUtil;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.UIUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;

public class AppNotificationUtil {
	public static HorizontalLayout buildNotifyBlock(JsonObject jsonNotifi) {
		String notifyId = jsonNotifi.get("id").getAsString();
		String taskId = !jsonNotifi.get("taskId").isJsonNull() ? jsonNotifi.get("taskId").getAsString() : null;
		String userName = !jsonNotifi.getAsJsonObject("creator").get("fullName").isJsonNull() ? jsonNotifi.getAsJsonObject("creator").get("fullName").getAsString() : "HỆ THỐNG";
		String title = jsonNotifi.get("title").getAsString().toLowerCase();
		String content = jsonNotifi.get("content").getAsString();
		String time = LocalDateUtil.formatLocalDateTime(LocalDateUtil.longToLocalDateTime(jsonNotifi.get("createdTime").getAsLong()),LocalDateUtil.dateTimeFormater1);
		String actionKey = jsonNotifi.get("action").getAsString();
		boolean isViewed = jsonNotifi.get("viewed").getAsBoolean();

		HorizontalLayout hNotify = new HorizontalLayout();
		Icon icon = VaadinIcon.NOTEBOOK.create();
		VerticalLayout vContent = new VerticalLayout();

		switch (actionKey) {
		case "nhiemvumoi":
			icon = VaadinIcon.ARROW_FORWARD.create();
			icon.getStyle().set("background", "#1676f3");
			break;
		case "capnhatnhiemvu":
			icon = VaadinIcon.EDIT.create();
			icon.getStyle().set("background", "#e29b5e");
			break;
		case "thaydoichutri":
			icon = VaadinIcon.USER_STAR.create();
			icon.getStyle().set("background", "#e29b5e");
			break; 
		case "thaydoinguoigiao":
			icon = VaadinIcon.USER_HEART.create();
			icon.getStyle().set("background", "#e29b5e");
			break; 
		case "capnhattiendo":
			icon = VaadinIcon.PROGRESSBAR.create();
			icon.getStyle().set("background", "#208090");
			break;
		case "ykienvaphanhoi":
			icon = VaadinIcon.CHAT.create();
			icon.getStyle().set("background", "#8d9e37ad");
			break;
		case "hoanthanhnhiemvu":
			icon = VaadinIcon.CHECK_CIRCLE.create();
			icon.getStyle().set("background", "rgb(27 181 46)");
			break;
		case "trieuhoinhiemvu":
			icon = VaadinIcon.REFRESH.create();
			icon.getStyle().set("background", "rgb(27 167 134)");
			break;
		case "daxoanhiemvu":
			icon = VaadinIcon.TRASH.create();
			icon.getStyle().set("background", "#b51818");
			break; 
		case "danhgianhiemvu":
			icon = VaadinIcon.STAR.create();
			icon.getStyle().set("background", "#ffce44");
			break; 
		case "nhiemvusapquahan":
			icon = VaadinIcon.CALENDAR_CLOCK.create();
			icon.getStyle().set("background", "#f7913b");
			break; 
		case "nhiemvudaquahan":
			icon = VaadinIcon.CALENDAR_CLOCK.create();
			icon.getStyle().set("background", "#d02626");
			break; 
		case "loginweb":
			icon = VaadinIcon.SIGN_IN.create();
			icon.getStyle().set("background", "#6d6d6d");
			break; 
		case "loginfail":
			icon = VaadinIcon.PASSWORD.create();
			icon.getStyle().set("background", "#d02626");
			break; 
		default:
			break;
		}
		icon.setSize("var(--lumo-font-size-xxs)");
		hNotify.add(icon,vContent);
		String strTitle = "<div><b>"+userName+"</b> "+title+"</div>";
		String strContent = "<div>"+content+"</div>";
		String strTime = "<div style='font-size: var(--lumo-font-size-xs); font-weight: 700; color: #4f93c3;'>"+time+"</div>";

		Html htmlTitle = new Html(strTitle);
		Html htmlContent = new Html(strContent);
		Html htmlTime = new Html(strTime);

		vContent.add(htmlTitle);
		vContent.add(htmlContent);
		vContent.add(htmlTime);

		vContent.setPadding(false);
		vContent.setSpacing(false);

		hNotify.expand(vContent);
		hNotify.setWidthFull();

		icon.addClassName("notify-icon");

		hNotify.addClassName("notify-block");
		if(isViewed)
			hNotify.addClassName("notify-block-viewed");

		hNotify.addClickListener(e->{
			try {
				if(taskId!=null) {
					JsonObject jsonResponse = TaskServiceUtil.getTaskDetail(taskId);

					if(jsonResponse.get("status").getAsInt()==200) {
						TaskDetailDialog dialogTask = new TaskDetailDialog(jsonResponse.getAsJsonObject("result"), null, null);

						dialogTask.open();

						if(!isViewed) {
							setViewedNotify(notifyId,hNotify);
						}
						dialogTask.addOpenedChangeListener(eClose->{
							if(!eClose.isOpened()) {
								if(dialogTask.getTaskDetail().isChange()) {
									UIUtil.getMainView().updateCountMenu(SessionUtil.getUserId(), SessionUtil.getOrgId(),SessionUtil.getYear(),SessionUtil.getToken());
								}
							}
						});
					} else if(jsonResponse.get("status").getAsInt()==404){
						setViewedNotify(notifyId,hNotify);
						
						NotificationUtil.showNotifi("Nhiệm vụ đã bị xóa!", NotificationTypeEnum.ERROR);
					} else {
						System.out.println(jsonResponse);
						NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
			}
		});
		
		return hNotify;
	}
	
	private static void setViewedNotify(String notifyId, HorizontalLayout hNotify) throws IOException {
		JsonObject jsonReponseNotify = TaskServiceUtil.setViewdNotify(notifyId);
		if(jsonReponseNotify.get("status").getAsInt()==200) {
			hNotify.addClassName("notify-block-viewed");
		} else {
			System.out.println(jsonReponseNotify.get("message").getAsString());
			NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
		}
	}
}
