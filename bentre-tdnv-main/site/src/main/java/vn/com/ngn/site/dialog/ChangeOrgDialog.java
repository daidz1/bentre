package vn.com.ngn.site.dialog;

import java.util.Map.Entry;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.model.OrganizationModel;
import vn.com.ngn.site.model.RoleModel;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.views.dashboard.DashboardNewView;

@SuppressWarnings("serial")
@CssImport("/themes/site/components/change-org-dialog.css")
public class ChangeOrgDialog extends DialogTemplate{
	private HorizontalLayout hOrgList = new HorizontalLayout();
	
	public ChangeOrgDialog() {
		System.out.println("=====ChangeOrgDialog=====");
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		super.buildLayout();
		vMain.add(hOrgList);
		
		if(SessionUtil.getOrg()!=null) {
			caption.setText("Chọn đơn vị để đổi:");
		} else {
			caption.setText("Chọn đơn vị để đăng nhập:");
			this.setCloseOnOutsideClick(false);
			btnClose.setVisible(false);
		}
		for(Entry<String, OrganizationModel> entry : SessionUtil.getOrgList().entrySet()) {
			hOrgList.add(createOrgBlock(entry.getValue()));
		}
	}

	@Override
	public void configComponent() {
		super.configComponent();
	}
	
	public HorizontalLayout createOrgBlock(OrganizationModel modelOrg) {
		HorizontalLayout hOrg = new HorizontalLayout();
		
		String roleString = "";
		for(RoleModel modelRole : modelOrg.getRoles()) {
			roleString += modelRole.getName()+", ";
		}
		if(modelOrg.getRoles().size()>0) {
			roleString = roleString.trim();
			roleString = roleString.substring(0,roleString.length()-1);
		} else {
			roleString = "Chưa xác định";
		}
		
		String strOrgInfo = "<div style='width:100%;overflow:hidden'>"
				+"<div class='org-name'><b>Tên đơn vị: </b> <b style='color:#2871cc' title='"+modelOrg.getName()+"'>"+modelOrg.getName()+"</b></div>"
				+"<div class='org-role'><b>Vai trò: </b> <i style='color:gray' title='"+roleString+"'>"+roleString+"</i></div>"
				+"<div>";
		
		Html htmlOrg = new Html(strOrgInfo);
		
		Icon icon = VaadinIcon.HOME.create();
		icon.setSize("16px");
		
		hOrg.add(icon,htmlOrg);
		
		hOrg.setVerticalComponentAlignment(Alignment.CENTER, icon);
		hOrg.setVerticalComponentAlignment(Alignment.CENTER, htmlOrg);
		
		hOrg.setFlexGrow(1, htmlOrg);
		
		hOrg.setPadding(false);
		hOrg.setHeight("70px");
		hOrg.setWidth("400px");
		hOrg.addClassName("org-block");
		
		hOrg.addClickListener(e->{
			if(SessionUtil.getOrg()!=null) {
				if(SessionUtil.getOrgId().equals(modelOrg.getId())) {
					NotificationUtil.showNotifi("Đơn vị hiện tại đang là đơn vị được chọn", NotificationTypeEnum.ERROR);
				} else {
					String title = "Đổi đơn vị";
					String description = "Bạn có muốn đổi qua đơn vị "+modelOrg.getName()+" (Sau khi đổi, vai trò và danh sách nhiệm vụ sẽ hiển thị theo đơn vị được chọn)";
					
					ConfirmDialog confDialog = new ConfirmDialog(title, description, 
							"Xác nhận", 
							eConfirm->{
								SessionUtil.setOrg(modelOrg);
								UI.getCurrent().navigate(DashboardNewView.class);
								UI.getCurrent().getPage().reload();
							},
							"Hủy",
							eCancel->{
								eCancel.getSource().close();
							});
					confDialog.open();
				}
			} else {
				SessionUtil.setOrg(modelOrg);
				UI.getCurrent().navigate(DashboardNewView.class);
				UI.getCurrent().getPage().reload();
				this.close();
			}
		});
		
		return hOrg;
	}
}
