package vn.com.ngn.site.util.service;

import com.google.gson.Gson;

import okhttp3.MediaType;
import vn.com.ngn.site.util.ConfigurationUtil;

public class ServiceUtil {
	protected static MediaType mediaTypeJson = MediaType.parse("application/json");
	protected static String url = ConfigurationUtil.getProperty("service.url");
	protected static Gson gson = new Gson(); 
}
