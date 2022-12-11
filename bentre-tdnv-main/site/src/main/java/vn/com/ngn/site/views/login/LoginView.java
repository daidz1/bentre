package vn.com.ngn.site.views.login;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.model.OrganizationModel;
import vn.com.ngn.site.model.UserModel;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.UserServiceUtil;
import vn.com.ngn.site.views.dashboard.DashboardNewView;
import vn.com.ngn.site.views.dashboard.DashboardView;
import vn.com.ngn.site.views.main.BlankView;

@SuppressWarnings("serial")
@Route("login")
@PageTitle("Đăng nhập")
@CssImport(value="/themes/site/views/login-view.css" , themeFor = "vaadin-login-overlay-wrapper")
public class LoginView extends VerticalLayout implements LayoutInterface, BeforeEnterObserver {
	private LoginOverlay formLogin = new LoginOverlay();
	private HorizontalLayout hTitle = new HorizontalLayout();
	private Image imgLogo = new Image("/images/logo.png","Logo.");
	
	private Label lblMainTitle = new Label();
	
	private LoginI18n i18n = LoginI18n.createDefault();
	
	public LoginView() {
		buildLayout();
		configComponent();
		
//		GeoLocation geoLocation = new GeoLocation();
//		geoLocation.setWatch(true);
//		geoLocation.setHighAccuracy(true);
//		geoLocation.setTimeout(100000);
//		geoLocation.setMaxAge(200000);
//		geoLocation.addValueChangeListener( e->{
//			Gson gson = new Gson();
//			JsonObject jsonPosition = gson.toJsonTree(e.getValue()).getAsJsonObject();
//			
//			SessionUtil.setPosition(jsonPosition);
//		});
//		this.add(geoLocation);
	}

	@Override
	public void buildLayout() {
		languageSetting();
		hTitle.add(imgLogo,lblMainTitle);
		imgLogo.setWidth("100px");
		imgLogo.setWidth("50px");
		imgLogo.setHeight("50px");
		lblMainTitle.getElement().setProperty("innerHTML", "<span style='font-weight:500;color: #e6ff13; padding-top: 10px; display: block;'>ỦY BAN NHÂN DÂN TỈNH BẾN TRE");
		formLogin.setTitle(hTitle);
		formLogin.setDescription("Hệ thống chỉ đạo điều hành");
		formLogin.setOpened(true);
		formLogin.setI18n(i18n);
		formLogin.setId("form-login");
		formLogin.setForgotPasswordButtonVisible(false);
		
		this.add(formLogin);
		this.setSizeFull();
		
		this.setId("login-view");
	}

	@Override
	public void configComponent() {
		formLogin.addLoginListener(e->{
			String userName = e.getUsername();
			String password = e.getPassword();
			authenticate(userName,password);
		});
		formLogin.addForgotPasswordListener(e->{
			NotificationUtil.showNotifi("Vui lòng liên hệ quản trị viên để hồi phục lại mật khẩu", NotificationTypeEnum.WARNING);
		});
	}
	
	private void authenticate(String username, String password) {
		try {
			JsonObject jsonObject = UserServiceUtil.login(username, password);
			if(jsonObject.get("status").getAsInt()==200) { //if all correct
				jsonObject.get("result").getAsJsonObject().addProperty("username", username);
				login(jsonObject.get("result").getAsJsonObject());
			} else if(jsonObject.get("status").getAsInt()==400){ //if username or password is wrong
				i18n.getErrorMessage().setTitle("Thông tin chưa chính xác");
				i18n.getErrorMessage().setMessage("Vui lòng kiếm tra lại tên đăng nhập và mật khẩu");
				formLogin.setI18n(i18n);
				formLogin.setError(true);
			} else if(jsonObject.get("status").getAsInt()==406){ //if there are another things make user can't login
				i18n.getErrorMessage().setTitle("Lỗi");
				i18n.getErrorMessage().setMessage(jsonObject.get("message").getAsString());
				formLogin.setI18n(i18n);
				formLogin.setError(true);
			}
		} catch (IOException e) {
			e.printStackTrace();
			i18n.getErrorMessage().setTitle("Lỗi hệ thống");
			i18n.getErrorMessage().setMessage("Không thể thực hiện xác thực, vui lòng thử lại sau.");
			formLogin.setI18n(i18n);
			formLogin.setError(true);
		}
	}
	
	private void login(JsonObject jsonEle) {
        Gson gson = new Gson();
        
		UserModel modelUser = new UserModel();
		modelUser.setId(jsonEle.get("id").getAsString());
		modelUser.setUsername(jsonEle.get("username").getAsString());
		modelUser.setEmail(jsonEle.get("email").getAsString());
		modelUser.setFullname(jsonEle.get("fullName").getAsString());
		modelUser.setJobTitle(jsonEle.get("jobTitle").getAsString());
		modelUser.setAccountDomino(!jsonEle.get("accountDomino").isJsonNull()?jsonEle.get("accountDomino").getAsString():null);
		
		//get display config
		Map<String, Boolean> mapDisplayConfig = new HashMap<String, Boolean>();
		for(Entry<String, JsonElement> entry : jsonEle.get("config").getAsJsonObject().entrySet()) {
			for(Entry<String, JsonElement> entrySub : entry.getValue().getAsJsonObject().entrySet()) {
				String key = entry.getKey()+"_"+entrySub.getKey();
				mapDisplayConfig.put(key, entrySub.getValue().getAsBoolean());
			}
		}
		
		//get org
		Map<String,OrganizationModel> mapOrg = new HashMap<String,OrganizationModel>();
		for(JsonElement jsonOrg : jsonEle.get("organizations").getAsJsonArray()) {
			OrganizationModel modelOrg = gson.fromJson(jsonOrg, OrganizationModel.class);
			mapOrg.put(modelOrg.getId(),modelOrg);
		}
		
		SessionUtil.setToken(jsonEle.get("loginToken").getAsString());
		SessionUtil.setUser(modelUser);
		SessionUtil.setOrgList(mapOrg);
		SessionUtil.setDisplayConfig(mapDisplayConfig);
		SessionUtil.setYear(LocalDate.now().getYear());
		
		if(mapOrg.size()==1) {
			SessionUtil.setOrg(mapOrg.values().iterator().next());
			this.getUI().ifPresent(ui->{
				ui.navigate(DashboardNewView.class);
				UI.getCurrent().getPage().reload();
			});
		} else {
			this.getUI().ifPresent(ui->{
				ui.navigate(BlankView.class);
			});
		}
	}
	public void languageSetting() {
		i18n.getForm().setTitle("Đăng nhập");
		i18n.getForm().setUsername("Tài khoản");
		i18n.getForm().setPassword("Mật khẩu");
		i18n.getForm().setSubmit("ĐĂNG NHẬP");
		i18n.getForm().setForgotPassword("Quên mật khẩu");
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		if(SessionUtil.isLogin()) {
			event.rerouteTo(DashboardNewView.class);
			
		}
	}
}
