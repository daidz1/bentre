package vn.com.ngn.site.views.main.settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.ListDataProvider;

import vn.com.ngn.site.enums.LogTypeEnum;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.model.LoginLogGridModel;
import vn.com.ngn.site.util.LocalDateUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.UserServiceUtil;

@SuppressWarnings("serial")
public class LoginLogLayout extends SettingLayout{
	private Grid<LoginLogGridModel> gridLog = new Grid<LoginLogGridModel>();
	
	public LoginLogLayout() {
		buildLayout();
		configComponent();
		
		loadData();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		spanInfo.setText("Thông tin 10 lần đăng nhập cuối cùng trong 30 ngày gần nhất");

		gridLog.addColumn(LoginLogGridModel::getStt).setHeader("STT").setFlexGrow(1);
		gridLog.addColumn(LoginLogGridModel::getTime).setHeader("Đăng nhập lúc").setFlexGrow(4);
		gridLog.addColumn(LoginLogGridModel::getPlateform).setHeader("Nền tảng").setFlexGrow(4);
		gridLog.addColumn(LoginLogGridModel::getDevice).setHeader("Thiết bị").setFlexGrow(10);
		
		vContent.add(gridLog);
	}

	@Override
	public void configComponent() {
		super.configComponent();
	}
	
	private void loadData() {
		try {
			JsonObject jsonResponse = UserServiceUtil.getLog(0, 10, 0, 0, LogTypeEnum.login);
			
			if(jsonResponse.get("status").getAsInt()==200) {
				JsonArray jsonArrLog = jsonResponse.getAsJsonArray("result");
				
				List<LoginLogGridModel> listGrid = new ArrayList<LoginLogGridModel>();
				int stt = 0;
				for(JsonElement jsonEle : jsonArrLog) {
					JsonObject jsonLog = jsonEle.getAsJsonObject();
					
					String time = LocalDateUtil.formatLocalDateTime(LocalDateUtil.longToLocalDateTime(jsonLog.get("createdTime").getAsLong()),LocalDateUtil.dateTimeFormater1);
					String platform = "Không xác định";
					String device = "Không xác định";
					if(jsonLog.has("clientRequest") && !jsonLog.get("clientRequest").isJsonNull()) {
						JsonObject jsonClientRequest = jsonLog.getAsJsonObject("clientRequest");
						
						if(jsonClientRequest.has("remote") && !jsonClientRequest.get("remote").isJsonNull()) {
							String remote = jsonClientRequest.get("remote").getAsString();
							
							if(remote.equals("web"))
								platform = "Ứng dụng Web";
							else
								platform = "Ứng dụng di động";
						}
						
						if(jsonClientRequest.has("useragent") && !jsonClientRequest.get("useragent").isJsonNull()) {
							device = jsonClientRequest.get("useragent").getAsString();
						}
					}
					
					LoginLogGridModel modelGrid = new LoginLogGridModel();
					modelGrid.setStt(++stt);
					modelGrid.setTime(time);
					modelGrid.setPlatform(platform);
					modelGrid.setDevice(device);
					
					listGrid.add(modelGrid);
				}
				
				ListDataProvider<LoginLogGridModel> dataProvider = new ListDataProvider<LoginLogGridModel>(listGrid);
				gridLog.setDataProvider(dataProvider);
			} else {
				NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
