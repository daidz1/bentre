package vn.com.ngn.site.views.main.settings;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import vn.com.ngn.site.enums.DisplayConfigEnum;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.enums.PermissionEnum;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.HeaderUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.UserServiceUtil;

@SuppressWarnings("serial")
public class DisplayConfigLayout extends SettingLayout{
	private Button btnSave = new Button("Lưu cấu hình",VaadinIcon.CHECK.create());
	
	private HorizontalLayout hCaptionDaGiaoView = HeaderUtil.createHeader5WithBackground(null,"Trang danh sách nhiệm vụ đã giao","rgb(65 78 95)","rgb(73 86 103 / 11%)");
	private Checkbox cbDaGiaoViewAssigneeOrg = new Checkbox("Hiển thị đơn vị cán bộ xử lý (Danh sách nhiệm vụ)");
	private Checkbox cbDaGiaoViewExpandTask = new Checkbox("Mở rộng thông tin nhiệm vụ (Danh sách nhiệm vụ)");
	
	private HorizontalLayout hCaptionDuocGiaoView = HeaderUtil.createHeader5WithBackground(null,"Trang danh sách nhiệm vụ được giao","rgb(65 78 95)","rgb(73 86 103 / 11%)");
	private Checkbox cbDuocGiaoViewOwnerOrg = new Checkbox("Hiển thị đơn vị cán bộ giao nhiệm vụ  (Danh sách nhiệm vụ)");
	private Checkbox cbDuocGiaoViewExpandTask = new Checkbox("Mở rộng thông tin nhiệm vụ (Danh sách nhiệm vụ)");
	
	private HorizontalLayout hCaptionHoTroView = HeaderUtil.createHeader5WithBackground(null,"Trang danh sách nhiệm vụ hỗ trợ","rgb(65 78 95)","rgb(73 86 103 / 11%)");
	private Checkbox cbHoTroViewAssigneeOrg = new Checkbox("Hiển thị đơn vị cán bộ xử lý (Danh sách nhiệm vụ)");
	private Checkbox cbHoTroViewOwnerOrg = new Checkbox("Hiển thị đơn vị cán bộ giao nhiệm vụ (Danh sách nhiệm vụ)");
	private Checkbox cbHoTroViewExpandTask = new Checkbox("Mở rộng thông tin nhiệm vụ (Danh sách nhiệm vụ)");
	
	public DisplayConfigLayout() {
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		spanInfo.setText("Cấu hình hiển thị trên giao diện theo từng trang");
		
		hInfo.add(btnSave);
		btnSave.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		
		if(SessionUtil.isHasPermission(PermissionEnum.giaonhiemvu)) {
			vContent.add(hCaptionDaGiaoView);
			vContent.add(cbDaGiaoViewAssigneeOrg);
			vContent.add(cbDaGiaoViewExpandTask);
		}
		
		vContent.add(hCaptionDuocGiaoView);
		vContent.add(cbDuocGiaoViewOwnerOrg);
		vContent.add(cbDuocGiaoViewExpandTask);
		
		vContent.add(hCaptionHoTroView);
		vContent.add(cbHoTroViewAssigneeOrg);
		vContent.add(cbHoTroViewOwnerOrg);
		vContent.add(cbHoTroViewExpandTask);
	}

	@Override
	public void configComponent() {
		super.configComponent();
		
		cbDaGiaoViewAssigneeOrg.setValue(SessionUtil.statusOfDisplayConfig(DisplayConfigEnum.dagiaoview_display_assignee_org));
		cbDaGiaoViewExpandTask.setValue(SessionUtil.statusOfDisplayConfig(DisplayConfigEnum.dagiaoview_expand_task_info));
		
		cbDuocGiaoViewOwnerOrg.setValue(SessionUtil.statusOfDisplayConfig(DisplayConfigEnum.duocgiaoview_display_owner_org));
		cbDuocGiaoViewExpandTask.setValue(SessionUtil.statusOfDisplayConfig(DisplayConfigEnum.duocgiaoview_expand_task_info));
		
		cbHoTroViewAssigneeOrg.setValue(SessionUtil.statusOfDisplayConfig(DisplayConfigEnum.hotroview_display_assignee_org));
		cbHoTroViewOwnerOrg.setValue(SessionUtil.statusOfDisplayConfig(DisplayConfigEnum.hotroview_display_owner_org));
		cbHoTroViewExpandTask.setValue(SessionUtil.statusOfDisplayConfig(DisplayConfigEnum.hotroview_expand_task_info));
		
		btnSave.addClickListener(e->{
			boolean dagiaoview_display_assignee_org = cbDaGiaoViewAssigneeOrg.getValue();
			boolean dagiaoview_expand_task_info = cbDaGiaoViewExpandTask.getValue();
			
			boolean duocgiaoview_display_owner_org = cbDuocGiaoViewOwnerOrg.getValue();
			boolean duocgiaoview_expand_task_info = cbDuocGiaoViewExpandTask.getValue();
			
			boolean hotroview_display_assignee_org = cbHoTroViewAssigneeOrg.getValue();
			boolean hotroview_display_owner_org = cbHoTroViewOwnerOrg.getValue();
			boolean hotroview_expand_task_info = cbHoTroViewExpandTask.getValue();
			
			JsonObject jsonConfig = new JsonObject();
			
			JsonObject jsonDaGiaoView = new JsonObject();
			jsonDaGiaoView.addProperty("display_assignee_org", dagiaoview_display_assignee_org);
			jsonDaGiaoView.addProperty("expand_task_info", dagiaoview_expand_task_info);
			
			JsonObject jsonDuocGiaoView = new JsonObject();
			jsonDuocGiaoView.addProperty("display_owner_org", duocgiaoview_display_owner_org);
			jsonDuocGiaoView.addProperty("expand_task_info", duocgiaoview_expand_task_info);
			
			JsonObject jsonHoTroView = new JsonObject();
			jsonHoTroView.addProperty("display_assignee_org", hotroview_display_assignee_org);
			jsonHoTroView.addProperty("display_owner_org", hotroview_display_owner_org);
			jsonHoTroView.addProperty("expand_task_info", hotroview_expand_task_info);
			
			jsonConfig.add("dagiaoview", jsonDaGiaoView);
			jsonConfig.add("duocgiaoview", jsonDuocGiaoView);
			jsonConfig.add("hotroview", jsonHoTroView);
			
			try {
				JsonObject jsonObject = UserServiceUtil.updateDisplayConfig(jsonConfig);
				
				int respCode = jsonObject.get("status").getAsInt();
				
				if(respCode==200) {
					SessionUtil.getDisplayConfig().put(DisplayConfigEnum.dagiaoview_display_assignee_org.toString(), dagiaoview_display_assignee_org);
					SessionUtil.getDisplayConfig().put(DisplayConfigEnum.dagiaoview_expand_task_info.toString(), dagiaoview_expand_task_info);
				
					SessionUtil.getDisplayConfig().put(DisplayConfigEnum.duocgiaoview_display_owner_org.toString(), duocgiaoview_display_owner_org);
					SessionUtil.getDisplayConfig().put(DisplayConfigEnum.duocgiaoview_expand_task_info.toString(), duocgiaoview_expand_task_info);
					
					SessionUtil.getDisplayConfig().put(DisplayConfigEnum.hotroview_display_assignee_org.toString(), hotroview_display_assignee_org);
					SessionUtil.getDisplayConfig().put(DisplayConfigEnum.hotroview_display_owner_org.toString(), hotroview_display_owner_org);
					SessionUtil.getDisplayConfig().put(DisplayConfigEnum.hotroview_expand_task_info.toString(), hotroview_expand_task_info);
					
					NotificationUtil.showNotifi("Cập nhật cấu hình thành công.", NotificationTypeEnum.SUCCESS);
					
					UI.getCurrent().getPage().reload();
				} else {
					NotificationUtil.showNotifi("Có lỗi xảy ra!! Vui lòng thử lại.", NotificationTypeEnum.ERROR);
				}
				System.out.println(jsonObject);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}
}
