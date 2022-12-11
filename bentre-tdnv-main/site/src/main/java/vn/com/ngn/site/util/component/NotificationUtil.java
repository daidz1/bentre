package vn.com.ngn.site.util.component;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;

import vn.com.ngn.site.enums.NotificationTypeEnum;

public class NotificationUtil {
	public static void showNotifi(String text, NotificationTypeEnum NotificationTypeEnum) {
		Notification notification = new Notification(text);
		notification.setPosition(Position.BOTTOM_END);
		notification.setDuration(5000);
		switch (NotificationTypeEnum) {
		case NORMAL:
			
			break;
		case SUCCESS:
			notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
			break;
		case WARNING:
			notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
			break;
		case ERROR:
			notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
			break;
		}
		
		notification.open();
	}
}
