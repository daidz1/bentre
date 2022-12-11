package vn.com.ngn.site.dialog;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

import vn.com.ngn.site.views.main.settings.DisplayConfigLayout;
import vn.com.ngn.site.views.main.settings.LoginLogLayout;

@SuppressWarnings("serial")
public class SettingDialog extends DialogTemplate {
	private Tab tabDisplayConfig = new Tab("Cấu hình hiển thị");
	private Tab tabLoginLog = new Tab("Nhật ký đăng nhập");
	
	private Tabs tabs = new Tabs(tabDisplayConfig, tabLoginLog);
	private VerticalLayout vTabDisplay = new VerticalLayout();
	
	private DisplayConfigLayout layoutDisplayConfig = new DisplayConfigLayout();
	private LoginLogLayout layoutLoginLog = new LoginLogLayout();
	
	private Map<Tab, Component> tabsToPages = new HashMap<>();
	
	public SettingDialog() {
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		caption.setText("Cài đặt người dùng");
		
		tabsToPages.put(tabDisplayConfig, layoutDisplayConfig);
		tabsToPages.put(tabLoginLog, layoutLoginLog);
		
		vMain.add(tabs);
		vMain.add(vTabDisplay);
		
		vTabDisplay.add(layoutDisplayConfig,layoutLoginLog);
		layoutLoginLog.setVisible(false);
		
		tabs.setWidthFull();
		
		this.setWidth("900px");
	}

	@Override
	public void configComponent() {
		super.configComponent();
		
		tabs.addSelectedChangeListener(event -> {
		    tabsToPages.values().forEach(page -> page.setVisible(false));
		    Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
		    selectedPage.setVisible(true);
		});
	}
}
