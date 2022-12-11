package vn.com.ngn.site.dialog.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

import vn.com.ngn.site.dialog.DialogTemplate;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.model.taskcreate.OrgTemplateGridModel;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;

@SuppressWarnings("serial")
public class ChooseOrgGroupDialog extends DialogTemplate{
	private HorizontalLayout hSearch = new HorizontalLayout();
	private Button btnAdd = new Button("Thêm mới",VaadinIcon.PLUS.create());
	private TextField txtSearch = new TextField();
	private Button btnSearch = new Button("Tìm kiếm",VaadinIcon.SEARCH.create());
	
	private Grid<OrgTemplateGridModel> grid = new Grid<OrgTemplateGridModel>();
	private List<OrgTemplateGridModel> listData = new ArrayList<OrgTemplateGridModel>();
	
	private ShortcutRegistration regisShortcut = null;
	
	private JsonObject jsonAssign;
	private JsonArray jsonFollow;
	
	public ChooseOrgGroupDialog() {
		buildLayout();
		configComponent();
		
		try {
			loadData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		
		caption.setText("Chọn nhóm cơ quan/đơn vị giao nhiệm vụ");
		
		vMain.add(hSearch);
		vMain.add(grid);
		
		this.setWidth("1200px");
		
		buildSearchLayout();
		buildGrid();
	}
	
	@Override
	public void configComponent() {
		super.configComponent();
		
		txtSearch.addFocusListener(e->{
			regisShortcut = btnSearch.addClickShortcut(Key.ENTER);
		});
		txtSearch.addBlurListener(e->{
			regisShortcut.remove();
		});
		btnSearch.addClickListener(e->{
			try {
				loadData();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		
		btnAdd.addClickListener(e->{
			EditOrgGroupDialog dialog = new EditOrgGroupDialog(null);
			
			dialog.open();
			
			dialog.getBtnTrigger().addClickListener(eClose->{
				try {
					loadData();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			});
		});
	}
	
	private void buildSearchLayout() {
		hSearch.add(btnAdd,txtSearch,btnSearch);
		
		btnAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		txtSearch.setPlaceholder("Nhập vào từ khóa để tìm kiếm...");
		
		hSearch.expand(txtSearch);
		hSearch.setWidthFull();
	}
	
	private void buildGrid() {
		grid.addComponentColumn(OrgTemplateGridModel::getName).setHeader("Tên nhóm").setWidth("100px");
		grid.addComponentColumn(OrgTemplateGridModel::getDescription).setHeader("Mô tả").setWidth("100px");
		grid.addComponentColumn(OrgTemplateGridModel::getOrgAssign).setHeader("Cơ quan/đơn vị xử lý");
		grid.addComponentColumn(OrgTemplateGridModel::getOrgSupport).setHeader("Cơ quan/đơn vị hỗ trợ"); 
		grid.addComponentColumn(OrgTemplateGridModel::getAction).setHeader("Hành động");
		
		grid.getColumns().forEach(licenseCol -> licenseCol.setAutoWidth(true));
		
		grid.setWidthFull();
	}
	
	private void loadData() throws IOException {
		String keyword = txtSearch.getValue().trim();
		int skip = 0;
		int limit = 100;
		String userId = SessionUtil.getUserId();
		String orgId = SessionUtil.getOrgId();
		String assignmentType = "Organization";
		
		JsonObject jsonResponse = TaskServiceUtil.getUserGroupList(keyword, skip, limit, userId, orgId, assignmentType);

		if(jsonResponse.get("status").getAsInt()==200) {
			listData.clear();

			JsonArray jsonTaskList = jsonResponse.get("result").getAsJsonArray();
			
			for(JsonElement jsonEle : jsonTaskList) {
				JsonObject jsonUserGroup = jsonEle.getAsJsonObject();
				JsonObject jsonUserAssign = jsonUserGroup.getAsJsonObject("assigneeTask");
				JsonArray jsonArrUserFollow = jsonUserGroup.getAsJsonArray("followersTask");
				
				String idGroup = jsonUserGroup.get("id").getAsString();
				//name
				String name = "<div style='max-width: 250px; overflow: hidden; text-overflow: ellipsis;' title='"+jsonUserGroup.get("name").getAsString()+"'>"+jsonUserGroup.get("name").getAsString()+"</div>";
				Html htmlName = new Html(name); 
				
				//description
				String description = "<div style='max-width: 200px; overflow: hidden; text-overflow: ellipsis;' title='"+jsonUserGroup.get("description").getAsString()+"'>"+jsonUserGroup.get("description").getAsString()+"</div>";
				Html htmlDescription = new Html(description); 

				//user assignee
				String userAssignInfo = jsonUserAssign.get("fullName").getAsString()+"</b> - "+jsonUserAssign.get("organizationName").getAsString();
				String userAssign = "<div style='max-width: 350px; overflow: hidden; text-overflow: ellipsis;' title='"+userAssignInfo+"'><b>"+userAssignInfo+"</div>";
				Icon iconUserAssgin = VaadinIcon.USER_STAR.create();
				Html htmlUserAssign = new Html(userAssign); 
				HorizontalLayout hUserAssign = new HorizontalLayout(iconUserAssgin,htmlUserAssign);
				hUserAssign.setDefaultVerticalComponentAlignment(Alignment.CENTER);
				
				//user follow
				String userFollow = "<div><b>"+jsonArrUserFollow.size()+"</b> cán bộ</div>";
				Icon iconUserFollow = VaadinIcon.USERS.create();
				Html htmlUserFollow = new Html(userFollow); 
				HorizontalLayout hUserFollow = new HorizontalLayout(iconUserFollow,htmlUserFollow);
				hUserFollow.setDefaultVerticalComponentAlignment(Alignment.CENTER);
				
				MenuBar menuFollower = new MenuBar();
				MenuItem menuItemfollower = menuFollower.addItem(htmlUserFollow);
				for(JsonElement jsonEleFollow : jsonArrUserFollow) {
					JsonObject jsonFollow = jsonEleFollow.getAsJsonObject();
					
					menuItemfollower.getSubMenu().addItem(jsonFollow.get("fullName").getAsString());
				}
				
				//Action
				Button btnSelect = new Button(VaadinIcon.POINTER.create());
				Button btnEdit = new Button(VaadinIcon.EDIT.create());
				Button btnDelete = new Button(VaadinIcon.TRASH.create());
				HorizontalLayout hAction = new HorizontalLayout(btnSelect,btnEdit,btnDelete);
				
				btnSelect.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
				btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);
				
				OrgTemplateGridModel modelGrid = new OrgTemplateGridModel();
				modelGrid.setName(htmlName);
				modelGrid.setDescription(htmlDescription);
				modelGrid.setOrgAssign(htmlUserAssign);
				modelGrid.setOrgSupport(menuFollower);
				modelGrid.setAction(hAction);
				
				listData.add(modelGrid);
				
				btnSelect.addClickListener(e->{
					jsonAssign = jsonUserAssign;
					jsonFollow = jsonArrUserFollow;
					
					close();
				});
				
				btnEdit.addClickListener(e->{
					EditUserGroupDialog dialog = new EditUserGroupDialog(jsonUserGroup);
					
					dialog.open();
					
					dialog.getBtnTrigger().addClickListener(eClose->{
						try {
							loadData();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					});
				});
				
				btnDelete.addClickListener(e->{
					String title = "Xóa nhóm giao việc";
					String question = "Bạn muốn xóa nhóm giao việc này?";
					
					ConfirmDialog confDialog = new ConfirmDialog(title, question, 
							"Xác nhận", 
							eConfirm->{
								try {
									JsonObject jsonDelResponse = TaskServiceUtil.deleteUserGroup(idGroup);
								
									if(jsonDelResponse.get("status").getAsInt()==200) {
										NotificationUtil.showNotifi("Xóa nhóm giao việc thành công.", NotificationTypeEnum.SUCCESS);
										listData.remove(modelGrid);
										grid.getDataProvider().refreshAll();
									} else {
										System.out.println(jsonResponse);
										NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
									}
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							},
							"Hủy",
							eCancel->{
								eCancel.getSource().close();
							});
					confDialog.open();
				});
			}

			grid.setItems(listData);
		} else {
			System.out.println(jsonResponse);
			NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại", NotificationTypeEnum.ERROR);
		}
	}

	public JsonObject getJsonAssign() {
		return jsonAssign;
	}
	public JsonArray getJsonFollow() {
		return jsonFollow;
	}
}
