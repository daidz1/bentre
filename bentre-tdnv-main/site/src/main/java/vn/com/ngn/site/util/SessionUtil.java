package vn.com.ngn.site.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonObject;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.WrappedSession;

import vn.com.ngn.site.enums.DisplayConfigEnum;
import vn.com.ngn.site.enums.PermissionEnum;
import vn.com.ngn.site.model.OrganizationModel;
import vn.com.ngn.site.model.RoleModel;
import vn.com.ngn.site.model.UserModel;

public class SessionUtil {
	final static public String TOKEN = "TOKEN";
	final static public String USER = "USER";
	final static public String ORG = "ORG";

	final static public String MAPORG = "MAPORG";

	final static public String USERID = "USERID";
	final static public String ORGID = "ORGID";

	final static public String DISPLAYCONFIG = "DISPLAYCONFIG";
	final static public String PERMISSION = "PERMISSION";

	final static public String LEADERTASK = "LEADERTASK";
	final static public String ASSISTTASK = "ASSISTTASK";

	final static public String YEAR = "YEAR";

	final static public String POSITION = "POSITION";
	final static public String PARAM = "PARAM";

	/* true false section */
	public static boolean isLogin() {
		if(getUser()!=null) {
			return true; 
		} else {
			return false;
		}
	}
	
	public static boolean isHasPermission(PermissionEnum ePermission) {
		if(getPermission().contains(ePermission.toString()))
			return true;
		return false;
	}

	public static boolean statusOfDisplayConfig(DisplayConfigEnum eConfig) {
		if(getDisplayConfig().get(eConfig.toString()))
			return true;
		return false;
	}

	/* set section */
	public static void setToken(String token) {
		try {
			getSession().setAttribute(TOKEN,token);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setUser(UserModel modelUser) {
		try {
			setAttribute(USERID,modelUser.getId());
			setAttribute(USER,modelUser);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setPermission(List<String> listPermission) {
		try {
			setAttribute(PERMISSION,listPermission);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setOrg(OrganizationModel modelOrg) {
		try {
			setAttribute(ORGID,modelOrg.getId());
			setAttribute(ORG,modelOrg);

			List<String> listPermission = new ArrayList<String>();

			for(RoleModel modelRole : modelOrg.getRoles()) {
				listPermission.removeAll(modelRole.getPermissionKeys());
				listPermission.addAll(modelRole.getPermissionKeys());
			}

			setPermission(listPermission);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setOrgList(Map<String,OrganizationModel> listOrg) {
		try {
			setAttribute(MAPORG,listOrg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setDisplayConfig(Map<String, Boolean> mapConfig) {
		try {
			setAttribute(DISPLAYCONFIG,mapConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setYear(int year) {
		try {
			setAttribute(YEAR,year);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setPosition(JsonObject position) {
		try {
			setAttribute(POSITION,position);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setParam(Map<String, String> param) {
		try {
			setAttribute(PARAM,param);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/* get section */
	public static String getToken() {
		try {
			String token = (String) getAttribute(TOKEN);

			return token;
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}

	public static UserModel getUser() {
		try {
			UserModel modelUser = (UserModel) getSession().getAttribute(USER);
			return modelUser;
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}

	public static String getUserId() {
		try {
			String userId = (String) getAttribute(USERID);
			return userId;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> getPermission() {
		try {
			List<String> listPermission = (List<String>) getAttribute(PERMISSION);
			System.out.println("===List permission===");
			for(String s: listPermission) {
				System.out.println(s);
			}
			return listPermission;
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}

	public static OrganizationModel getOrg() {
		try {
			OrganizationModel modelOrg = (OrganizationModel) getAttribute(ORG);

			return modelOrg;
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}

	public static String getOrgId() {
		try {
			String orgId = (String) getAttribute(ORGID);

			return orgId;
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,OrganizationModel> getOrgList() {
		try {
			Map<String,OrganizationModel> mapOrg = (Map<String,OrganizationModel>) getAttribute(MAPORG);

			return mapOrg;
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String,Boolean> getDisplayConfig() {
		try {
			Map<String,Boolean> mapDisplayConfig = (Map<String,Boolean>) getAttribute(DISPLAYCONFIG);

			return mapDisplayConfig;
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}

	public static int getYear() {
		try {
			int orgId = (int) getAttribute(YEAR);

			return orgId;
		} catch (Exception e) {
			return 0;
		}
	}

	public static JsonObject getPosition() {
		try {
			JsonObject position = (JsonObject) getAttribute(POSITION);

			return position;
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getParam() {
		try {
			Map<String, String> position = (Map<String, String>) getAttribute(PARAM);

			return position;
		} catch (Exception e) {
			return null;
		}
	}
	
	/* Support function */
	public static WrappedSession getSession() {
		return VaadinService.getCurrentRequest().getWrappedSession();
	}

	public static Object getAttribute(String attribute) {
		return getSession().getAttribute(attribute);
	}

	public static void setAttribute(String key, Object value) {
		getSession().setAttribute(key, value);
	}

	public static void removeAttributes(String... keys) {
		for (String key : keys) {
			getSession().removeAttribute(key);
		}
	}
	
	public static void cleanAllSession() {
		Set<String> attributeNames = new HashSet<>(getSession().getAttributeNames());
		attributeNames.forEach(getSession()::removeAttribute);
	}
}
