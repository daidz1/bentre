package vn.com.ngn.site.views.main;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.Theme;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.dialog.ChangeOrgDialog;
import vn.com.ngn.site.dialog.ChangePasswordDialog;
import vn.com.ngn.site.dialog.ChangeUserInfoDialog;
import vn.com.ngn.site.dialog.SettingDialog;
import vn.com.ngn.site.enums.DocOfEnum;
import vn.com.ngn.site.enums.DocTypeEnum;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.enums.PermissionEnum;
import vn.com.ngn.site.enums.TaskAssignmentStatusEnum;
import vn.com.ngn.site.enums.TaskAssignmentTypeEnum;
import vn.com.ngn.site.enums.TaskStatusEnum;
import vn.com.ngn.site.enums.TaskTypeEnum;
import vn.com.ngn.site.model.CustomPairModel;
import vn.com.ngn.site.util.BroadcasterSupportUitl;
import vn.com.ngn.site.util.BroadcasterUtil;
import vn.com.ngn.site.util.DocEnumUtil;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.TaskEnumUtil;
import vn.com.ngn.site.util.UIUtil;
import vn.com.ngn.site.util.component.AppNotificationUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;
import vn.com.ngn.site.views.dashboard.DashboardNewView;
import vn.com.ngn.site.views.dashboard.DashboardView;
import vn.com.ngn.site.views.docimport.DocImportView;
import vn.com.ngn.site.views.doclist.DocCreateListView;
import vn.com.ngn.site.views.doclist.DocListView;
import vn.com.ngn.site.views.login.LoginView;
import vn.com.ngn.site.views.report.ReportOrgView;
import vn.com.ngn.site.views.report.ReportView;
import vn.com.ngn.site.views.search.SearchView;
import vn.com.ngn.site.views.taskcreate.TaskCreateOrgView;
import vn.com.ngn.site.views.taskcreate.TaskCreateView;
import vn.com.ngn.site.views.tasklist.TaskListView;

@SuppressWarnings("serial")
@Push
@PWA(name = "site", shortName = "site", enableInstallPrompt = false)
@Theme(themeFolder = "site")
public class MainView extends AppLayout implements LayoutInterface, BeforeEnterObserver {
	private HorizontalLayout hHeader = new HorizontalLayout();
	private HorizontalLayout hHeaderLeft = new HorizontalLayout();
	
	private H1 viewTitle = new H1();
	private HorizontalLayout hHeaderRight = new HorizontalLayout();
	private HorizontalLayout hNotifi = new HorizontalLayout();
	private Button btnNotifi = new Button(VaadinIcon.BELL.create());
	private VerticalLayout vNotifi = new VerticalLayout();
	
	private MenuBar menuRight = new MenuBar();
	private MenuItem menuItemOrg;
	private MenuItem menuItemUser;
	private MenuItem menuItemUserInfo;
	private MenuItem menuItemChangePW;
	private MenuItem menuItemSetting;
	private MenuItem menuItemLogout;

	private Span spanUserName;

	private VerticalLayout vDrawer = new VerticalLayout();
	private HorizontalLayout hLogo = new HorizontalLayout();
	private Image mainLogo = new Image("images/logo.png", "Logo");
	private Html spanTitle = new Html("<span style='color: #fff; line-height: 1.2; font-weight: 500;'>ỦY BAN NHÂN DÂN TỈNH BẾN TRE<span>");
	private ComboBox<CustomPairModel<Integer, String>> cmbYear = new ComboBox<CustomPairModel<Integer,String>>();
	private VerticalLayout vMenu = new VerticalLayout();
	private Details detailCreateTask = new Details();
	private Details detailDocList = new Details();
	private Details detailTaskDaGiao = new Details();
	private Details detailTaskDuocGiao = new Details();
	private Details detailTaskHoTro = new Details();
	private Details detailTaskGiaoViecThay = new Details();
	private Details detailTaskTheoDoiThay = new Details();
	//Dzung code
	private Details detailTaskDonViDuocGiao = new Details();
	private Details detailTaskDonViDuocGiaoHoTro = new Details();
	//end

	private List<CustomPairModel<Class<? extends Component>, HorizontalLayout>> listNavigateLayout = new ArrayList<CustomPairModel<Class<? extends Component>,HorizontalLayout>>();

	private Map<String, Span> mapTaskCountComponent = new HashMap<String, Span>();
	
	private Gson gson = new Gson(); 

	private String strNaviActive = "navigate-active";

	private boolean isBuildLayout = false;
	private Registration broadcasterRegistration;

	public MainView() {
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
//		hHeader.add(geoLocation);
	}

	@Override
	public void buildLayout() {
		this.setPrimarySection(Section.DRAWER);
		this.addToDrawer(vDrawer);
		this.addToNavbar(true,hHeader);
		buildDrawer();
		buildHeader();

		updateCountMenu(SessionUtil.getUserId(), SessionUtil.getOrgId(),SessionUtil.getYear(),SessionUtil.getToken());
	}
	@Override
	public void configComponent() {
		cmbYear.addValueChangeListener(e->{
			SessionUtil.setYear(e.getValue().getKey());
			UI.getCurrent().getPage().reload();
		});

		btnNotifi.addClickListener(e->{
			if(!vNotifi.hasClassName("notifi-layout-display")) {
				vNotifi.removeAll();
				vNotifi.addClassNames("notifi-layout-display");
				btnNotifi.removeClassName("notifi-button-active");

				H4 captionNotifi = new H4("Thông báo");
				Button btnCheckAll = new Button("Xem tất cả",VaadinIcon.CHECK.create());
				HorizontalLayout hCaption = new HorizontalLayout(captionNotifi,btnCheckAll);

				btnCheckAll.addThemeVariants(ButtonVariant.LUMO_SMALL);
				btnCheckAll.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

				hCaption.expand(captionNotifi);

				captionNotifi.getStyle().set("color", "#232e38"); 
				captionNotifi.getStyle().set("margin-top", "5px"); 

				vNotifi.add(hCaption);

				loadNotify(0,10);

				btnCheckAll.addClickListener(eCheckAll->{
					try {
						JsonObject jsonReponseNotify = TaskServiceUtil.setViewdAllNotify(SessionUtil.getUserId(), SessionUtil.getOrgId());
						if(jsonReponseNotify.get("status").getAsInt()==200) {
							for(int i = 0 ; i < vNotifi.getComponentCount() ;i++) {
								try {
									HorizontalLayout hNotifyBlock = ((HorizontalLayout)vNotifi.getComponentAt(i));
									if(hNotifyBlock.hasClassName("notify-block") && !hNotifyBlock.hasClassName("notify-block-viewed")) {
										hNotifyBlock.addClassName("notify-block-viewed");
									}
								} catch (Exception e2) {
									System.out.println("Reached the expand button");
								}
							}
						} else {
							System.out.println(jsonReponseNotify.get("message").getAsString());
							NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
						}
					} catch (Exception e3) {
						e3.printStackTrace();
					}
				});
			} else {
				vNotifi.removeClassName("notifi-layout-display");
			}
		});

		menuItemOrg.addClickListener(e->{
			ChangeOrgDialog dialog = new ChangeOrgDialog();

			dialog.open();
		});

		menuItemUserInfo.addClickListener(e->{
			ChangeUserInfoDialog dialog = new ChangeUserInfoDialog();

			dialog.open();

			dialog.addOpenedChangeListener(eClose->{
				if(!eClose.isOpened()) {
					spanUserName.setText(SessionUtil.getUser().getFullname());
				}
			});
		});

		menuItemChangePW.addClickListener(e->{
			ChangePasswordDialog dialog = new ChangePasswordDialog();

			dialog.open();
		});

		menuItemSetting.addClickListener(e->{
			SettingDialog dialog = new SettingDialog();

			dialog.open();
		});

		menuItemLogout.addClickListener(e -> {
			SessionUtil.cleanAllSession();
			UI.getCurrent().navigate(LoginView.class);
		});
	}

	private void buildDrawer() {
		vDrawer.add(hLogo);
		vDrawer.add(cmbYear);
		vDrawer.add(vMenu);

		/* Dashboard button */
		vMenu.add(createSimpleButtonNavi(VaadinIcon.DASHBOARD, "Tổng quan", DashboardNewView.class,null));
		vMenu.add(createSimpleButtonNavi(VaadinIcon.DASHBOARD, "Theo dõi công việc", DashboardView.class,null));

		/* Create task button */
//		if(SessionUtil.isHasPermission(PermissionEnum.giaonhiemvu)) {
//			vMenu.add(createSimpleButtonNavi(VaadinIcon.PLUS, "Giao nhiệm vụ cho cá nhân", TaskCreateView.class,null));
//		}
		
		/* Create task group */
		if(SessionUtil.isHasPermission(PermissionEnum.giaonhiemvu)) {
			vMenu.add(detailCreateTask);
			VerticalLayout vCreateTask = new VerticalLayout();
			vCreateTask.add(createSimpleButtonNavi(VaadinIcon.USER, "Cá nhân", TaskCreateView.class,null));
			vCreateTask.add(createSimpleButtonNavi(VaadinIcon.INSTITUTION, "Cơ quan/đơn vị", TaskCreateOrgView.class,null));
			vCreateTask.setSpacing(false);
			vCreateTask.setMargin(false);
			detailCreateTask.setSummaryText("Giao nhiệm vụ");
			detailCreateTask.setContent(vCreateTask);
			detailCreateTask.addClassName("detail-menu");
			detailCreateTask.getElement().setAttribute("type", "task");
		}
		
		/* Import doc button */
//		if(SessionUtil.isHasPermission(PermissionEnum.quanlynhapvanban)) {
//			vMenu.add(createSimpleButtonNavi(VaadinIcon.UPLOAD, "Nhập văn bản XML", DocImportView.class,null));
//		}
		/* Doc list buttons */
		if(SessionUtil.isHasPermission(PermissionEnum.xemvanban) || SessionUtil.getUser().getAccountDomino()!=null) {
			vMenu.add(detailDocList);

			VerticalLayout vDoc = new VerticalLayout();
			
			//Dzung code
//			if(SessionUtil.isHasPermission(PermissionEnum.quanlynhapvanban)) {
//				vDoc.add(createDocCreateListButton(null,"+ Thêm văn bản", DocTypeEnum.VANBANDEN, DocOfEnum.ALL));
//			}
			//end Dzung code
			
			
			if(SessionUtil.getUser().getAccountDomino()!=null) {
				vDoc.add(createDocListButton(VaadinIcon.ARROW_BACKWARD,"Văn bản đến", DocTypeEnum.VANBANDEN, DocOfEnum.SELF));
				vDoc.add(createDocListButton(VaadinIcon.ARROW_FORWARD,"Văn bản đi", DocTypeEnum.VANBANDI, DocOfEnum.SELF));
			}
			if(SessionUtil.isHasPermission(PermissionEnum.xemvanban)) {
				vDoc.add(createDocListButton(VaadinIcon.ARCHIVE,"Tất cả văn bản đến", DocTypeEnum.VANBANDEN, DocOfEnum.ALL));
				vDoc.add(createDocListButton(VaadinIcon.ARCHIVE,"Tất cả văn bản đi", DocTypeEnum.VANBANDI, DocOfEnum.ALL));
			}

			vDoc.setSpacing(false);
			vDoc.setPadding(false);
			
			detailDocList.setSummaryText("Danh sách văn bản");
			detailDocList.setContent(vDoc);
			detailDocList.addClassName("detail-menu");
			detailDocList.getElement().setAttribute("type", "doc");
		}
		
		/* Da giao task list buttons */
		if(SessionUtil.isHasPermission(PermissionEnum.giaonhiemvu)) {
			vMenu.add(detailTaskDaGiao);
			
			VerticalLayout vDaGiao = new VerticalLayout();
			vDaGiao.add(createTaskListButton(VaadinIcon.CORNER_UPPER_LEFT,"Tất cả", TaskTypeEnum.DAGIAO, TaskStatusEnum.TATCA, null,null));
			vDaGiao.add(createHighLightedTaskListButton(null, "Chưa hoàn thành", TaskTypeEnum.DAGIAO, TaskStatusEnum.CHUAHOANTHANH,null,null));
			vDaGiao.add(createTaskListButton(null,"+ Trong hạn",TaskTypeEnum.DAGIAO, TaskStatusEnum.CHUAHOANTHANH_TRONGHAN, null,null));
			vDaGiao.add(createTaskListButton(null,"+ Quá hạn",TaskTypeEnum.DAGIAO, TaskStatusEnum.CHUAHOANTHANH_QUAHAN, null,null));
			vDaGiao.add(createTaskListButton(null,"+ Không hạn",TaskTypeEnum.DAGIAO, TaskStatusEnum.CHUAHOANTHANH_KHONGHAN, null,null));
			vDaGiao.add(createHighLightedTaskListButton(null, "Đã hoàn thành", TaskTypeEnum.DAGIAO, TaskStatusEnum.DAHOANTHANH,null,null));
			vDaGiao.add(createTaskListButton(null,"+ Trong hạn", TaskTypeEnum.DAGIAO, TaskStatusEnum.DAHOANTHANH_TRONGHAN, null,null));
			vDaGiao.add(createTaskListButton(null,"+ Quá hạn",TaskTypeEnum.DAGIAO, TaskStatusEnum.DAHOANTHANH_QUAHAN, null,null));
			vDaGiao.add(createTaskListButton(null,"+ Không hạn",TaskTypeEnum.DAGIAO, TaskStatusEnum.DAHOANTHANH_KHONGHAN, null,null));
			vDaGiao.setSpacing(false);
			vDaGiao.setPadding(false);
			
			detailTaskDaGiao.setSummaryText("Nhiệm vụ đã giao (0)");
			detailTaskDaGiao.setContent(vDaGiao);
			detailTaskDaGiao.addClassName("detail-menu");
			detailTaskDaGiao.getElement().setAttribute("type", "task");
		}
		
		/* Duoc giao task list buttons */
		vMenu.add(detailTaskDuocGiao);
		
		VerticalLayout vDuocGiao = new VerticalLayout();
		vDuocGiao.add(createTaskListButton(VaadinIcon.CORNER_UPPER_LEFT,"Tất cả",TaskTypeEnum.DUOCGIAO, TaskStatusEnum.TATCA, null,null));
		vDuocGiao.add(createHighLightedTaskListButton(null, "Chưa hoàn thành", TaskTypeEnum.DUOCGIAO, TaskStatusEnum.CHUAHOANTHANH,null,null));
		vDuocGiao.add(createTaskListButton(null,"+ Trong hạn",TaskTypeEnum.DUOCGIAO, TaskStatusEnum.CHUAHOANTHANH_TRONGHAN, null,null));
		vDuocGiao.add(createTaskListButton(null,"+ Quá hạn", TaskTypeEnum.DUOCGIAO, TaskStatusEnum.CHUAHOANTHANH_QUAHAN, null,null));
		vDuocGiao.add(createTaskListButton(null,"+ Không hạn", TaskTypeEnum.DUOCGIAO, TaskStatusEnum.CHUAHOANTHANH_KHONGHAN, null,null));
		vDuocGiao.add(createHighLightedTaskListButton(null, "Đã hoàn thành",TaskTypeEnum.DUOCGIAO, TaskStatusEnum.DAHOANTHANH,null,null));
		vDuocGiao.add(createTaskListButton(null,"+ Trong hạn", TaskTypeEnum.DUOCGIAO, TaskStatusEnum.DAHOANTHANH_TRONGHAN, null,null));
		vDuocGiao.add(createTaskListButton(null,"+ Quá hạn", TaskTypeEnum.DUOCGIAO, TaskStatusEnum.DAHOANTHANH_QUAHAN, null,null));
		vDuocGiao.add(createTaskListButton(null,"+ Không hạn", TaskTypeEnum.DUOCGIAO, TaskStatusEnum.DAHOANTHANH_KHONGHAN, null,null));
		vDuocGiao.setSpacing(false);
		vDuocGiao.setPadding(false);
		
		detailTaskDuocGiao.setSummaryText("Nhiệm vụ được giao (0)");
		detailTaskDuocGiao.setContent(vDuocGiao);
		detailTaskDuocGiao.addClassName("detail-menu");
		detailTaskDuocGiao.getElement().setAttribute("type", "task");
		
		/* Ho tro task list buttons */
		vMenu.add(detailTaskHoTro);
		
		VerticalLayout vHoTro = new VerticalLayout();
		vHoTro.add(createTaskListButton(VaadinIcon.CORNER_UPPER_LEFT,"Tất cả",TaskTypeEnum.THEODOI, TaskStatusEnum.TATCA, null,null));
		vHoTro.add(createHighLightedTaskListButton(null, "Chưa hoàn thành",TaskTypeEnum.THEODOI, TaskStatusEnum.CHUAHOANTHANH,null,null));
		vHoTro.add(createTaskListButton(null,"+ Trong hạn",TaskTypeEnum.THEODOI, TaskStatusEnum.CHUAHOANTHANH_TRONGHAN, null,null));
		vHoTro.add(createTaskListButton(null,"+ Quá hạn",TaskTypeEnum.THEODOI, TaskStatusEnum.CHUAHOANTHANH_QUAHAN, null,null));
		vHoTro.add(createTaskListButton(null,"+ Không hạn",TaskTypeEnum.THEODOI, TaskStatusEnum.CHUAHOANTHANH_KHONGHAN, null,null));
		vHoTro.add(createHighLightedTaskListButton(null, "Đã hoàn thành",TaskTypeEnum.THEODOI, TaskStatusEnum.DAHOANTHANH,null,null));
		vHoTro.add(createTaskListButton(null,"+ Trong hạn",TaskTypeEnum.THEODOI, TaskStatusEnum.DAHOANTHANH_TRONGHAN, null,null));
		vHoTro.add(createTaskListButton(null,"+ Quá hạn",TaskTypeEnum.THEODOI, TaskStatusEnum.DAHOANTHANH_QUAHAN, null,null));
		vHoTro.add(createTaskListButton(null,"+ Không hạn",TaskTypeEnum.THEODOI, TaskStatusEnum.DAHOANTHANH_KHONGHAN, null,null));
		vHoTro.setSpacing(false);
		vHoTro.setPadding(false);
		
		detailTaskHoTro.setSummaryText("Nhiệm vụ hỗ trợ (0)");
		detailTaskHoTro.setContent(vHoTro);
		detailTaskHoTro.addClassName("detail-menu");
		detailTaskHoTro.getElement().setAttribute("type", "task");
		
		/* Da giao thay task list buttons */
		if(SessionUtil.getOrg().getLeadersTask().size()>0) {
			vMenu.add(detailTaskGiaoViecThay);
			
			VerticalLayout vGiaoViecThay = new VerticalLayout();
			vGiaoViecThay.add(createTaskListButton(VaadinIcon.CORNER_UPPER_LEFT,"Tất cả",TaskTypeEnum.GIAOVIECTHAY, TaskStatusEnum.TATCA, null,null));
			vGiaoViecThay.add(createHighLightedTaskListButton(null, "Chưa hoàn thành",TaskTypeEnum.GIAOVIECTHAY, TaskStatusEnum.CHUAHOANTHANH,null,null));
			vGiaoViecThay.add(createTaskListButton(null,"+ Trong hạn",TaskTypeEnum.GIAOVIECTHAY, TaskStatusEnum.CHUAHOANTHANH_TRONGHAN, null,null));
			vGiaoViecThay.add(createTaskListButton(null,"+ Quá hạn",TaskTypeEnum.GIAOVIECTHAY, TaskStatusEnum.CHUAHOANTHANH_QUAHAN, null,null));
			vGiaoViecThay.add(createTaskListButton(null,"+ Không hạn",TaskTypeEnum.GIAOVIECTHAY, TaskStatusEnum.CHUAHOANTHANH_KHONGHAN, null,null));
			vGiaoViecThay.add(createHighLightedTaskListButton(null, "Đã hoàn thành",TaskTypeEnum.GIAOVIECTHAY, TaskStatusEnum.DAHOANTHANH,null,null));
			vGiaoViecThay.add(createTaskListButton(null,"+ Trong hạn",TaskTypeEnum.GIAOVIECTHAY, TaskStatusEnum.DAHOANTHANH_TRONGHAN, null,null));
			vGiaoViecThay.add(createTaskListButton(null,"+ Quá hạn",TaskTypeEnum.GIAOVIECTHAY, TaskStatusEnum.DAHOANTHANH_QUAHAN, null,null));
			vGiaoViecThay.add(createTaskListButton(null,"+ Không hạn",TaskTypeEnum.GIAOVIECTHAY, TaskStatusEnum.DAHOANTHANH_KHONGHAN, null,null));
			vGiaoViecThay.setSpacing(false);
			vGiaoViecThay.setPadding(false);
			
			detailTaskGiaoViecThay.setSummaryText("Nhiệm vụ đã giao thay (0)");
			detailTaskGiaoViecThay.setContent(vGiaoViecThay);
			detailTaskGiaoViecThay.getStyle().set("width", "100%");
			detailTaskGiaoViecThay.addClassName("detail-menu");
			detailTaskGiaoViecThay.getElement().setAttribute("type", "task");
		}

		/* Duoc giao thay task list buttons */
		if(SessionUtil.getOrg().getAssistantsTask().size()>0) {
			vMenu.add(detailTaskTheoDoiThay);
			
			VerticalLayout vTheoDoiThay = new VerticalLayout();
			vTheoDoiThay.add(createTaskListButton(VaadinIcon.CORNER_UPPER_LEFT,"Tất cả",TaskTypeEnum.THEODOITHAY, TaskStatusEnum.TATCA, null,null));
			vTheoDoiThay.add(createHighLightedTaskListButton(null, "Chưa hoàn thành",TaskTypeEnum.THEODOITHAY, TaskStatusEnum.CHUAHOANTHANH,null,null));
			vTheoDoiThay.add(createTaskListButton(null,"+ Trong hạn",TaskTypeEnum.THEODOITHAY, TaskStatusEnum.CHUAHOANTHANH_TRONGHAN, null,null));
			vTheoDoiThay.add(createTaskListButton(null,"+ Quá hạn",TaskTypeEnum.THEODOITHAY, TaskStatusEnum.CHUAHOANTHANH_QUAHAN, null,null));
			vTheoDoiThay.add(createTaskListButton(null,"+ Không hạn",TaskTypeEnum.THEODOITHAY, TaskStatusEnum.CHUAHOANTHANH_KHONGHAN, null,null));
			vTheoDoiThay.add(createHighLightedTaskListButton(null, "Đã hoàn thành",TaskTypeEnum.THEODOITHAY, TaskStatusEnum.DAHOANTHANH,null,null));
			vTheoDoiThay.add(createTaskListButton(null,"+ Trong hạn",TaskTypeEnum.THEODOITHAY, TaskStatusEnum.DAHOANTHANH_TRONGHAN, null,null));
			vTheoDoiThay.add(createTaskListButton(null,"+ Quá hạn",TaskTypeEnum.THEODOITHAY, TaskStatusEnum.DAHOANTHANH_QUAHAN, null,null));
			vTheoDoiThay.add(createTaskListButton(null,"+ Không hạn",TaskTypeEnum.THEODOITHAY, TaskStatusEnum.DAHOANTHANH_KHONGHAN, null,null));
			vTheoDoiThay.setSpacing(false);
			vTheoDoiThay.setPadding(false);
			
			detailTaskTheoDoiThay.setSummaryText("Nhiệm vụ được theo dõi thay (0)");
			detailTaskTheoDoiThay.setContent(vTheoDoiThay);
			detailTaskTheoDoiThay.addClassName("detail-menu");
			detailTaskTheoDoiThay.getElement().setAttribute("type", "task");
		}
		
		//Dzung code
		/* Nhiem vu don vi duoc giao*/
		if(SessionUtil.isHasPermission(PermissionEnum.phannhiemvudonvi)) {
			vMenu.add(detailTaskDonViDuocGiao);
			
			VerticalLayout vDonViDuocGiao = new VerticalLayout();
			vDonViDuocGiao.add(createTaskListButton(null,"+ Chưa phân",TaskTypeEnum.DUOCGIAO, TaskStatusEnum.CHUAHOANTHANH, TaskAssignmentTypeEnum.ORGANIZATION, TaskAssignmentStatusEnum.CHUAPHAN_CANBO));
			vDonViDuocGiao.add(createTaskListButton(null, "+ Đã phân", TaskTypeEnum.DUOCGIAO, TaskStatusEnum.CHUAHOANTHANH, TaskAssignmentTypeEnum.ORGANIZATION, TaskAssignmentStatusEnum.DAPHAN_CANBO));

			vDonViDuocGiao.setSpacing(false);
			vDonViDuocGiao.setPadding(false);
			
			detailTaskDonViDuocGiao.setSummaryText("Nhiệm vụ đơn vị được giao (0)");
			detailTaskDonViDuocGiao.setContent(vDonViDuocGiao);
			detailTaskDonViDuocGiao.addClassName("detail-menu");
			detailTaskDonViDuocGiao.getElement().setAttribute("type", "task");
		}
		/* Nhiem vu don vi duoc giao hỗ trợ*/
		if(SessionUtil.isHasPermission(PermissionEnum.phannhiemvudonvi)) {
			vMenu.add(detailTaskDonViDuocGiaoHoTro);
			
			VerticalLayout vDonViDuocGiaoHoTro = new VerticalLayout();
			vDonViDuocGiaoHoTro.add(createTaskListButton(null,"+ Chưa phân",TaskTypeEnum.THEODOI, TaskStatusEnum.CHUAHOANTHANH, TaskAssignmentTypeEnum.ORGANIZATION, TaskAssignmentStatusEnum.CHUAPHAN_CANBO));
			vDonViDuocGiaoHoTro.add(createTaskListButton(null, "+ Đã phân", TaskTypeEnum.THEODOI, TaskStatusEnum.CHUAHOANTHANH, TaskAssignmentTypeEnum.ORGANIZATION, TaskAssignmentStatusEnum.DAPHAN_CANBO));

			vDonViDuocGiaoHoTro.setSpacing(false);
			vDonViDuocGiaoHoTro.setPadding(false);
			
			detailTaskDonViDuocGiaoHoTro.setSummaryText("Nhiệm vụ đơn vị hỗ trợ (0)");
			detailTaskDonViDuocGiaoHoTro.setContent(vDonViDuocGiaoHoTro);
			detailTaskDonViDuocGiaoHoTro.addClassName("detail-menu");
			detailTaskDonViDuocGiaoHoTro.getElement().setAttribute("type", "task");
		}
		//end Dzung code
		
		/* Report button */
		vMenu.add(createSimpleButtonNavi(VaadinIcon.FILE, "Báo cáo thống kê nhiệm vụ cá nhân", ReportView.class,null));
		
		//Dzung code
		vMenu.add(createSimpleButtonNavi(VaadinIcon.FILE, "Báo cáo thống kê nhiệm vụ đơn vị", ReportOrgView.class,null));
		//end Dzung code
		
		//vMenu.add(createSimpleButtonNavi(VaadinIcon.FILE, "Báo cáo thống kê (mới)", ReportNewView.class,null));
		/* Search task button */
		vMenu.add(createSimpleButtonNavi(VaadinIcon.SEARCH, "Tra cứu nhiệm vụ", SearchView.class,null));

		vMenu.setSpacing(false);
		vMenu.setPadding(false);
		vMenu.setHeightFull();
		vMenu.addClassName("menu-layout");

		hLogo.add(mainLogo,spanTitle);
		hLogo.setId("logo");
		hLogo.setAlignItems(FlexComponent.Alignment.CENTER);

		mainLogo.setHeight("30px");
		hLogo.getStyle().set("background", "#233348");

		vDrawer.setHorizontalComponentAlignment(Alignment.CENTER, cmbYear);

		vDrawer.setClassName("sidemenu-menu");
		vDrawer.setSizeFull();
		vDrawer.setPadding(false);
		vDrawer.setSpacing(false);
		vDrawer.getThemeList().set("spacing-s", true);
		vDrawer.setAlignItems(FlexComponent.Alignment.STRETCH);

		loadCMBYear();
	}
	
	private void buildHeader() {
		hHeader.add(new DrawerToggle(),hHeaderLeft,hHeaderRight);

		hHeaderLeft.add(viewTitle);

		hHeaderRight.add(hNotifi,menuRight);

		hHeader.setFlexGrow(1, hHeaderLeft);

		hHeader.setClassName("sidemenu-header");
		hHeader.getThemeList().set("dark", true);
		hHeader.setWidthFull();
		hHeader.setSpacing(false);
		hHeader.setAlignItems(FlexComponent.Alignment.CENTER);

		buildRightMenu();
	}

	private void buildRightMenu() {
		hNotifi.add(btnNotifi,vNotifi);
		btnNotifi.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		btnNotifi.addClassName("notifi-button");
		vNotifi.addClassName("notifi-layout");

		hNotifi.addClassName("notifi-parent-layout");

		try {
			JsonObject jsonResponse = TaskServiceUtil.getCountUserNoitify(SessionUtil.getUserId(), SessionUtil.getOrgId());

			if(jsonResponse.get("status").getAsInt()==200) {
				JsonObject jsonResult = jsonResponse.getAsJsonObject("result");
				if(jsonResult.get("unview").getAsInt()>0)
					btnNotifi.addClassName("notifi-button-active");
			} else {
				System.out.println(jsonResponse);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		//menu item
		HorizontalLayout hOrg = new HorizontalLayout();
		Icon iconOrg = VaadinIcon.HOME.create();
		iconOrg.setSize("0.875rem");
		Span orgName = new Span(SessionUtil.getOrg().getName());
		hOrg.add(iconOrg,orgName);

		HorizontalLayout hUser = new HorizontalLayout();
		Icon iconUser = VaadinIcon.USER.create();
		iconUser.setSize("0.875rem");
		Icon iconDropdown = VaadinIcon.ANGLE_DOWN.create();
		iconDropdown.setSize("0.875rem");
		spanUserName = new Span(SessionUtil.getUser().getFullname());
		hUser.add(iconUser,spanUserName,iconDropdown);

		HorizontalLayout hUserInfo = new HorizontalLayout();
		Icon iconInfo = VaadinIcon.INFO.create();
		iconInfo.setSize("0.875rem");
		Span userInfoText = new Span("Thông tin tài khoản");
		hUserInfo.add(iconInfo,userInfoText);

		HorizontalLayout hChangePW = new HorizontalLayout();
		Icon iconChangePW = VaadinIcon.KEY.create();
		iconChangePW.setSize("0.875rem");
		Span userChangePW = new Span("Đổi mật khẩu");
		hChangePW.add(iconChangePW,userChangePW);

		HorizontalLayout hSetting = new HorizontalLayout();
		Icon iconhSetting = VaadinIcon.COG.create();
		iconhSetting.setSize("0.875rem");
		Span userSetting = new Span("Cài đặt");
		hSetting.add(iconhSetting,userSetting);

		HorizontalLayout hLogut = new HorizontalLayout();
		Icon iconLogout = VaadinIcon.SIGN_OUT.create();
		iconLogout.setSize("0.875rem");
		Span logoutText = new Span("Đăng xuất");
		hLogut.add(iconLogout,logoutText);

		menuItemOrg = menuRight.addItem(hOrg);
		menuItemUser = menuRight.addItem(hUser);

		menuItemUserInfo = menuItemUser.getSubMenu().addItem(hUserInfo);
		menuItemChangePW = menuItemUser.getSubMenu().addItem(hChangePW);
		menuItemSetting = menuItemUser.getSubMenu().addItem(hSetting);
		menuItemLogout = menuItemUser.getSubMenu().addItem(hLogut);

		menuRight.addThemeVariants(MenuBarVariant.LUMO_CONTRAST);
		menuRight.setOpenOnHover(true);
	}

	private void loadCMBYear() {
		var listYear = new ArrayList<CustomPairModel<Integer,String>>();

		int yearToSelect = SessionUtil.getYear()!=0 ? SessionUtil.getYear() : LocalDate.now().getYear();
		CustomPairModel<Integer, String> modelYearSelect = null;
		for(int i = 2018 ; i <= LocalDate.now().getYear() ; i++) {
			var modelYear = new CustomPairModel<Integer, String>(i,"Dữ liệu năm "+i);
			listYear.add(modelYear);

			if(i==yearToSelect) {
				modelYearSelect = modelYear;
			}
		}

		cmbYear.setItems(listYear);
		cmbYear.setItemLabelGenerator(CustomPairModel<Integer,String>::getValue);
		cmbYear.setAllowCustomValue(false);
		cmbYear.setValue(modelYearSelect);
		cmbYear.setHelperText("Hiển thị dữ liệu của năm được chọn");
		cmbYear.setWidth("80%");
	}

	private HorizontalLayout createSimpleButtonNavi(VaadinIcon vaadinIcon, String text, Class<? extends Component> navigationTarget, Map<String, String> mapParam) {
		var hNavigate = new HorizontalLayout();

		var spanCaption = new Span(text);
		Icon icon = null;
		if(vaadinIcon!=null) {
			icon = new Icon(vaadinIcon);
			icon.setSize("10px");
			hNavigate.add(icon);

			hNavigate.setAlignSelf(Alignment.CENTER, icon);
		}
		hNavigate.add(spanCaption);

		
		hNavigate.addClickListener(e->{
			SessionUtil.setParam(mapParam);
			hNavigate.getUI().ifPresent(ui -> ui.navigate(navigationTarget));
		});

		hNavigate.setWidthFull();
		hNavigate.addClassName("navigate-layout");
		hNavigate.setFlexGrow(1, spanCaption);
		
		hNavigate.getElement().removeAttribute("param");
		if(mapParam!=null) {
			hNavigate.getElement().setAttribute("param", gson.toJson(mapParam));
		}

		listNavigateLayout.add(new CustomPairModel<Class<? extends Component>, HorizontalLayout>(navigationTarget, hNavigate));

		return hNavigate;
	}

	private Component createTaskListButton(
			VaadinIcon icon,
			String text,
			TaskTypeEnum type, 
			TaskStatusEnum status, 
			TaskAssignmentTypeEnum assignmentType, 
			TaskAssignmentStatusEnum assignmentStatus) {
		Map<String, String> mapParam = new HashMap<String, String>();
		mapParam.put("type", type.getKey());
		mapParam.put("status", status.getKey());
		//Dzung code
		try {
			mapParam.put("assignmentType", assignmentType.getKey());
			mapParam.put("assignmentStatus", assignmentStatus.getKey());
		} catch (Exception e) {
			mapParam.put("assignmentType", TaskAssignmentTypeEnum.USER.getKey());
			mapParam.put("assignmentStatus", TaskAssignmentStatusEnum.DAPHAN_CANBO.getKey());
		}
		//end Dzung code

		var hNavigate = createSimpleButtonNavi(icon, text, TaskListView.class,mapParam);

		var spanCount = new Span(String.valueOf(0));

		hNavigate.add(spanCount);
		hNavigate.addClassName("navigate-layout-in");

//		mapTaskCountComponent.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(type, status), spanCount);
		//Dzung code
		if(assignmentType==null) {
			mapTaskCountComponent.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(type, status), spanCount);
		}else {
			mapTaskCountComponent.put(TaskEnumUtil.createKeyByTaskTypeTaskStatusAssignmentTypeAssignmentStatus(type, status,assignmentType, assignmentStatus), spanCount);
		}
		
		//end Dzung code
		

		return hNavigate;
	}

	private Component createHighLightedTaskListButton(
			VaadinIcon icon,
			String text,
			TaskTypeEnum type, 
			TaskStatusEnum status,
			//Dzung code
			TaskAssignmentTypeEnum assignmentType, 
			TaskAssignmentStatusEnum assignmentStatus
			//end Dzung code
			) {
		Map<String, String> mapParam = new HashMap<String, String>();
		mapParam.put("type", type.getKey());
		mapParam.put("status", status.getKey());
		//Dzung code
		try {
			mapParam.put("assignmentType", assignmentType.getKey());
			mapParam.put("assignmentStatus", assignmentStatus.getKey());
		} catch (Exception e) {
			mapParam.put("assignmentType", TaskAssignmentTypeEnum.USER.getKey());
			mapParam.put("assignmentStatus", TaskAssignmentStatusEnum.DAPHAN_CANBO.getKey());
		}
		//end Dzung code

		var hNavigate = createSimpleButtonNavi(icon, text, TaskListView.class,mapParam);

		var spanCount = new Span(String.valueOf(0));
		spanCount.getStyle().set("color", "#2662bb");
		spanCount.getStyle().set("font-weight", "bold");

		hNavigate.add(spanCount);
		hNavigate.addClassName("navigate-layout-in");
		hNavigate.getStyle().set("color", "black");

		mapTaskCountComponent.put(TaskEnumUtil.createKeyByTaskTypeTaskStatus(type, status), spanCount);

		return hNavigate;
	}

	private Component createDocListButton(VaadinIcon icon,String text,DocTypeEnum type, DocOfEnum of) {
		Map<String, String> mapParam = new HashMap<String, String>();
		mapParam.put("type", type.getKey());
		mapParam.put("of", of.toString());
		
		var hNavigate = createSimpleButtonNavi(icon, text, DocListView.class,mapParam);

		var spanCount = new Span(String.valueOf(0));

		hNavigate.add(spanCount);
		hNavigate.addClassName("navigate-layout-in");

		mapTaskCountComponent.put(DocEnumUtil.createKeyByDocEnum(type, of), spanCount);

		return hNavigate;
	}

	public void updateCountMenu(String userId, String orgId, int year,String token) {
		/* Loop navigate layout on menu to update count */
		Map<String, Integer> mapCount = UIUtil.getCountForMenu(userId,orgId,year,token);
		for(Entry<String, Span> entry : mapTaskCountComponent.entrySet()) {
			int count = mapCount.containsKey(entry.getKey()) ? mapCount.get(entry.getKey()) : 0;
			entry.getValue().setText(String.valueOf(count));
		}

		detailTaskDaGiao.setSummaryText("Nhiệm vụ đã giao ("+mapCount.get(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.DAGIAO, TaskStatusEnum.CHUAHOANTHANH))+")");
		detailTaskDuocGiao.setSummaryText("Nhiệm vụ được giao ("+mapCount.get(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.DUOCGIAO, TaskStatusEnum.CHUAHOANTHANH))+")");
		detailTaskHoTro.setSummaryText("Nhiệm vụ hỗ trợ ("+mapCount.get(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.THEODOI, TaskStatusEnum.CHUAHOANTHANH))+")");
		detailTaskGiaoViecThay.setSummaryText("Nhiệm vụ đã giao thay ("+mapCount.get(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.GIAOVIECTHAY, TaskStatusEnum.CHUAHOANTHANH))+")");
		detailTaskTheoDoiThay.setSummaryText("Nhiệm vụ được theo dõi thay ("+mapCount.get(TaskEnumUtil.createKeyByTaskTypeTaskStatus(TaskTypeEnum.THEODOITHAY, TaskStatusEnum.CHUAHOANTHANH))+")");
		
		//Dzung code
		detailTaskDonViDuocGiao.setSummaryText("Nhiệm vụ đơn vị được giao ("+mapCount.get(TaskEnumUtil.createKeyByTaskTypeTaskStatusAssignmentTypeAssignmentStatus(TaskTypeEnum.DUOCGIAO, TaskStatusEnum.CHUAHOANTHANH, TaskAssignmentTypeEnum.ORGANIZATION,TaskAssignmentStatusEnum.CHUAPHAN_CANBO))+")");
		detailTaskDonViDuocGiaoHoTro.setSummaryText("Nhiệm vụ đơn vị theo dõi ("+mapCount.get(TaskEnumUtil.createKeyByTaskTypeTaskStatusAssignmentTypeAssignmentStatus(TaskTypeEnum.THEODOI, TaskStatusEnum.CHUAHOANTHANH, TaskAssignmentTypeEnum.ORGANIZATION,TaskAssignmentStatusEnum.CHUAPHAN_CANBO))+")");
		//end Dzung code
	}

	public void loadNotify(int skip, int limit) {
		try {
			JsonObject jsonReponse = TaskServiceUtil.getUserNoitify(skip, limit, SessionUtil.getUserId(), SessionUtil.getOrgId(),null);

			if(jsonReponse.get("status").getAsInt()==200) {
				int total = jsonReponse.get("total").getAsInt();
				JsonArray jsonArrNotifi = jsonReponse.getAsJsonArray("result");

				for(JsonElement jsonEle : jsonArrNotifi) {
					JsonObject jsonNotifi = jsonEle.getAsJsonObject();
					vNotifi.add(AppNotificationUtil.buildNotifyBlock(jsonNotifi));
				}

				if(skip+limit<total) {
					Span spanViewMore = new Span("Xem thêm");
					spanViewMore.addClassName("notify-viewmore");
					vNotifi.add(spanViewMore);

					spanViewMore.addClickListener(e->{
						loadNotify(skip+limit,limit);
						vNotifi.remove(spanViewMore);
					});
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

	@Override
	protected void afterNavigation() {
		super.afterNavigation();

		viewTitle.setText(getCurrentPageTitle());
	}

	private String getCurrentPageTitle() {
		PageTitle title = this.getContent().getClass().getAnnotation(PageTitle.class);
		return title == null ? "" : title.value();
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		String userId = SessionUtil.getUserId();
		String orgId = SessionUtil.getOrgId();
		int year = SessionUtil.getYear();
		String token = SessionUtil.getToken();

		UI ui = attachEvent.getUI();
		broadcasterRegistration = BroadcasterUtil.register(newMessage -> {
			ui.access(() -> {
				if(BroadcasterSupportUitl.checkHasOption(newMessage, BroadcasterSupportUitl.MAINVIEW)) {
					List<CustomPairModel<String, String>> listUserPair = BroadcasterSupportUitl.decodeMessageWithOnlyUser(BroadcasterSupportUitl.removeAllOption(newMessage));

					for(CustomPairModel<String, String> pair : listUserPair) {
						if(pair.getKey().equals(userId) && pair.getValue().equals(orgId)) {
							btnNotifi.addClassName("notifi-button-active");
							NotificationUtil.showNotifi("Bạn có thông báo mới", NotificationTypeEnum.SUCCESS);

							if(BroadcasterSupportUitl.checkHasOption(newMessage, BroadcasterSupportUitl.UPDATEUI)) {
								updateCountMenu(userId,orgId,year,token);
							}
						}
					}
				}
			});
		});
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		broadcasterRegistration.remove();
		broadcasterRegistration = null;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		if(!SessionUtil.isLogin()) { // check if login, if not navigate to login view
			event.forwardTo(LoginView.class);
		} else {
			if(SessionUtil.getOrg()==null) { // if login but not choose organization, navigate to blank view
				
				event.forwardTo(BlankView.class);
			} else {
				if(isBuildLayout==false) {
					buildLayout();
					configComponent();
					isBuildLayout = true;
				}
			}
		}
		
		Class<?> currentClass =  event.getNavigationTarget();
		
		System.out.println("=====MainView beforeEnter=====");
		for(CustomPairModel<Class<? extends Component>, HorizontalLayout> comNaviagte : listNavigateLayout) {
			if(comNaviagte.getValue().hasClassName(strNaviActive)){
				comNaviagte.getValue().removeClassName(strNaviActive);
			}
			
			if(comNaviagte.getKey().equals(TaskListView.class) && currentClass.equals(TaskListView.class)) {
				JsonObject jsonParam = new JsonParser().parse(comNaviagte.getValue().getElement().getAttribute("param")).getAsJsonObject();
				//Dzung code
				if(SessionUtil.getParam().get("type").equals(jsonParam.get("type").getAsString())
						&& SessionUtil.getParam().get("status").equals(jsonParam.get("status").getAsString())
						&& SessionUtil.getParam().get("assignmentType").equals(jsonParam.get("assignmentType").getAsString())
						&& SessionUtil.getParam().get("assignmentStatus").equals(jsonParam.get("assignmentStatus").getAsString()))
						
				{
					comNaviagte.getValue().addClassName(strNaviActive);
				} else if(comNaviagte.getValue().hasClassName(strNaviActive)) {
					comNaviagte.getValue().removeClassName(strNaviActive);
				}
				//end Dzung code
				
//				if(SessionUtil.getParam().get("type").equals(jsonParam.get("type").getAsString())
//						&& SessionUtil.getParam().get("status").equals(jsonParam.get("status").getAsString())) {
//					comNaviagte.getValue().addClassName(strNaviActive);
//				} else if(comNaviagte.getValue().hasClassName(strNaviActive)) {
//					comNaviagte.getValue().removeClassName(strNaviActive);
//				}
			} else if(comNaviagte.getKey().equals(DocListView.class)  && currentClass.equals(DocListView.class)) {
				JsonObject jsonParam = new JsonParser().parse(comNaviagte.getValue().getElement().getAttribute("param")).getAsJsonObject();
				if(SessionUtil.getParam().get("type").equals(jsonParam.get("type").getAsString())
						&& SessionUtil.getParam().get("of").equals(jsonParam.get("of").getAsString())) {
					comNaviagte.getValue().addClassName(strNaviActive);
				} else if(comNaviagte.getValue().hasClassName(strNaviActive)) {
					comNaviagte.getValue().removeClassName(strNaviActive);
				}
			} else {
				if(comNaviagte.getKey().equals(event.getNavigationTarget())) {
					comNaviagte.getValue().addClassName(strNaviActive);
				} else if(comNaviagte.getValue().hasClassName(strNaviActive)) {
					comNaviagte.getValue().removeClassName(strNaviActive);
				}
			}
		}
		System.out.println("=====End MainView beforeEnter=====");
	}
}
