package vn.com.ngn.site.util;

import com.google.gson.JsonObject;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WebBrowser;

public class ClientInfoUitl {
	public static JsonObject getUserInfo() {
		WebBrowser browser = VaadinSession.getCurrent().getBrowser();
		
		String ipAddress = browser.getAddress();
		
		if(ipAddress.endsWith(":1"))
			ipAddress="127.0.0.1";
		
		JsonObject jsonInfo = new JsonObject();
		
		jsonInfo.addProperty("ipadress", ipAddress);
		jsonInfo.addProperty("useragent", browser.getBrowserApplication());
		jsonInfo.addProperty("remote", "web");
		jsonInfo.add("location", SessionUtil.getPosition());
		
		return jsonInfo;
	}
}
