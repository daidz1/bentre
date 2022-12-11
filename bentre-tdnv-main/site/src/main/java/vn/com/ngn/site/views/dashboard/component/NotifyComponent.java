package vn.com.ngn.site.views.dashboard.component;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.enums.TaskTypeEnum;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.AppNotificationUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;

@SuppressWarnings("serial")
public class NotifyComponent extends VerticalLayout {
	public NotifyComponent(TaskTypeEnum eType) {
		H4 captionNotifi = new H4("Top 10 Thông báo mới nhất");
		VerticalLayout vNotify = new VerticalLayout();
		
		captionNotifi.getStyle().set("color", "#232e38"); 
		captionNotifi.getStyle().set("margin-top", "5px"); 
		this.add(captionNotifi);
		this.add(vNotify);
		
		vNotify.setHeightFull();
		vNotify.setPadding(false);
		vNotify.getStyle().set("overflow", "auto");
		
		this.expand(vNotify);
		try {
			int skip=0,limit=10;
			JsonObject jsonReponse = TaskServiceUtil.getUserNoitify(skip, limit, SessionUtil.getUserId(), SessionUtil.getOrgId(),eType.getKey());

			if(jsonReponse.get("status").getAsInt()==200) {
				JsonArray jsonArrNotifi = jsonReponse.getAsJsonArray("result");

				for(JsonElement jsonEle : jsonArrNotifi) {
					JsonObject jsonNotifi = jsonEle.getAsJsonObject();

					vNotify.add(AppNotificationUtil.buildNotifyBlock(jsonNotifi));
				}
			} else {
				System.out.println(jsonReponse);
				NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
			}
		} catch (IOException e1) {
			NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
			e1.printStackTrace();
		}
	}
}
