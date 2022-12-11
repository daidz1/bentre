package vn.com.ngn.site.dialog.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.TextField;

import vn.com.ngn.site.dialog.DialogTemplate;
import vn.com.ngn.site.model.CustomPairModel;
import vn.com.ngn.site.model.taskcreate.TaskAssigneeOrgModel;
import vn.com.ngn.site.util.GeneralUtil;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.HeaderUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;
@SuppressWarnings("serial")
public class TaskSupportOrgDialog extends DialogTemplate{
	private VerticalLayout vTop = new VerticalLayout();
	private HorizontalLayout hSearch = new HorizontalLayout();
	private VerticalLayout vDisplayUserSelected = new VerticalLayout();

	private TextField txtKeyword = new TextField();
	private Button btnSearch = new Button(VaadinIcon.SEARCH.create());

	private VerticalLayout vBottom = new VerticalLayout();

	private List<CustomPairModel<String, Component>> listOrgLayout = new ArrayList<CustomPairModel<String,Component>>();

	private Map<String,TaskAssigneeOrgModel> mapOrg = new HashMap<String,TaskAssigneeOrgModel>();
	private Map<String, Button> mapButton = new HashMap<String, Button>();

	private TaskAssigneeOrgModel orgAssigneeModel;

	private ShortcutRegistration regisShortcut = null;

	// phục vụ cho việc khi truyền 1 list vào sẽ disable các user giống nhau và khác đơn vị
	private List<String> listIdSelected = new ArrayList<String>();

	public TaskSupportOrgDialog(Map<String,TaskAssigneeOrgModel> mapInput,TaskAssigneeOrgModel orgAssigneeModel) {
		this.mapOrg = mapInput;
		this.orgAssigneeModel = orgAssigneeModel;

		buildLayout();
		configComponent();
		displayUserSelected();
	}

	@Override
	public void buildLayout() {
		super.buildLayout();
		caption.setText("Chọn cơ quan/đơn vị hỗ trợ nhiệm vụ");

		vMain.add(vTop);
		vMain.add(vBottom);

		this.setWidth("900px");

		try {
			buildTopLayout();
			buildBottomLayout();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void configComponent() {
		super.configComponent();
		txtKeyword.addFocusListener(e->{
			regisShortcut = btnSearch.addClickShortcut(Key.ENTER);
		});
		txtKeyword.addBlurListener(e->{
			regisShortcut.remove();
		});
		btnSearch.addClickListener(e->{
			search();
		});
	}

	private void buildTopLayout() throws IOException {
		vTop.add(hSearch);
		vTop.add(vDisplayUserSelected);

		hSearch.add(txtKeyword,btnSearch);

		txtKeyword.setWidthFull();
		txtKeyword.setPlaceholder("Nhập vào tên cơ quan/đơn vị để tìm...");

		hSearch.setWidthFull();

		vDisplayUserSelected.setPadding(false);

		vTop.setMinHeight("100px");
		vTop.setWidthFull();
		vTop.setPadding(false);

		loadData();
	}

	private void buildBottomLayout() {
		vBottom.setWidthFull();
		vBottom.setPadding(false);
	}

	private void loadData() throws IOException {
		var jsonObject = TaskServiceUtil.getAssigneeList(SessionUtil.getUserId(), SessionUtil.getOrgId());

		if(jsonObject.get("status").getAsInt()==200) {
			var jsonResult = jsonObject.get("result").getAsJsonObject();

			var jsonArrSubOrg = jsonResult.get("subOrganzation").getAsJsonArray();

			vBottom.add(HeaderUtil.createHeader5(VaadinIcon.HOME_O.create(),"Đơn vị trực thuộc:","#115fa2"));

			for(JsonElement jsonSubOrgEle : jsonArrSubOrg) {
				var jsonSubOrg = jsonSubOrgEle.getAsJsonObject();

				CustomPairModel<String, Component> subOrgData = buildOrgBlock(jsonSubOrg.get("organization").getAsJsonObject(), jsonSubOrg.get("users").getAsJsonArray(),false);
				vBottom.add(subOrgData.getValue());

				listOrgLayout.add(subOrgData);
			}
		} else {
			System.out.println(jsonObject);
		}
	}

	private void search() {
		String keyword = txtKeyword.getValue().trim();

		for(CustomPairModel<String, Component> model : listOrgLayout) {
			if(!keyword.isEmpty()) {
				if(GeneralUtil.getSearchString(model.getKey()).contains(GeneralUtil.getSearchString(keyword))) {
					model.getValue().setVisible(true);
				} else {
					model.getValue().setVisible(false);
				}
			} else {
				model.getValue().setVisible(true);
			}
		}
	}

	
	private CustomPairModel<String ,Component> buildOrgBlock(JsonObject jsonOrg, JsonArray jsonArrUser,boolean isMain) {
		HorizontalLayout detail = new HorizontalLayout();
		String searchString = "";

		String orgId = jsonOrg.get("id").getAsString();
		String orgName = jsonOrg.get("name").getAsString();

		searchString = orgName;
		Button btnChoose = new Button("Chọn");
		btnChoose.setId(orgId);

		var orgModelTmp = new TaskAssigneeOrgModel();

		orgModelTmp.setOrgId(orgId);
		orgModelTmp.setOrgName(orgName);
		
		String idToGet = orgId+"-"+orgName;
		mapButton.put(idToGet, btnChoose);
//		Xét khi bật lại window sau khi đã chọn
		if(mapOrg.containsKey(idToGet)) {
			setThemeSelectedForButton(btnChoose);
			listIdSelected.add(idToGet);
		}
		
		if(orgAssigneeModel!=null && orgAssigneeModel.getOrgId().equals(orgId)) {
			btnChoose.setEnabled(false);
			btnChoose.getElement().setAttribute("title", "Cơ quan/đơn vị đã được chọn làm xử lý");
		}
		
		btnChoose.addClickListener(e->{
			
			if(!mapOrg.containsKey(idToGet)) {
				mapOrg.put(idToGet, orgModelTmp);
				setThemeSelectedForButton(btnChoose);
				displayUserSelected();
				for(Entry<String,Button> entry : mapButton.entrySet()) {
					if(!entry.getKey().equals(idToGet) && entry.getKey().split(Pattern.quote("-"))[0].equals(orgId)) {
						mapButton.get(entry.getKey()).setEnabled(false);
					}
				}
			} else {
					String title = "Hủy cơ quan/đơn vị xử lý";
					String description = "Bạn có muốn hủy quyền xử lý của cơ quan/đơn vị này?";
					ConfirmDialog confDialog = new ConfirmDialog(title, description, 
							"Xác nhận", 
							eConfirm->{
								setThemeDeSelectedForButton(btnChoose);
								mapOrg.remove(idToGet);
								displayUserSelected();
								for(Entry<String,Button> entry : mapButton.entrySet()) {
									if(!entry.getKey().equals(idToGet) && entry.getKey().split(Pattern.quote("-"))[0].equals(orgId)) {
										mapButton.get(entry.getKey()).setEnabled(true);
									}
								}
							},
							"Hủy",
							eCancel->{
								eCancel.getSource().close();
							});
					confDialog.open();
			}
		});
		detail.setWidth("100%");
		Span lblOrgName = new Span(orgName);
		detail.add(lblOrgName,btnChoose);
		detail.setFlexGrow(1, lblOrgName);
		
		return new CustomPairModel<String, Component>(searchString, detail);
	}
	private void displayUserSelected() {
		vDisplayUserSelected.removeAll();
		if(mapOrg.size()==0) {
			String displayHtml = "<div class='task-assignee-empty-display'>"
					+"Chưa có cơ quan/đơn vị nào được phân công hỗ trợ."
					+"</div>";

			Html html = new Html(displayHtml);
			vDisplayUserSelected.add(html);
		} else {
			var captionSelected = HeaderUtil.createHeader5(VaadinIcon.CHECK.create(),"Danh sách cơ quan/đơn vị được phân công hỗ trợ:",null);
			vDisplayUserSelected.add(captionSelected);

			for(Entry<String,TaskAssigneeOrgModel> entry : mapOrg.entrySet()) {
				HorizontalLayout hDisplayUser = new HorizontalLayout();
				var modelUser = entry.getValue();

				String strOrgname = "<div class='info-block' title='"+modelUser.getOrgName()+"'><b>Đơn vị: </b>"+modelUser.getOrgName()+"<div>";


				hDisplayUser.setDefaultVerticalComponentAlignment(Alignment.CENTER);

				hDisplayUser.addClassName("hLayout-userinfo-support-selected");

				Html htmlOrgname = new Html(strOrgname);
				Button btnDeSelected = new Button("Hủy");

				btnDeSelected.addThemeVariants(ButtonVariant.LUMO_ERROR);

				hDisplayUser.add(htmlOrgname,btnDeSelected);

				hDisplayUser.setWidthFull();
				vDisplayUserSelected.add(hDisplayUser);

				btnDeSelected.addClickListener(e->{
					String idToGet = modelUser.getOrgId()+"-"+modelUser.getOrgName();
					mapButton.get(idToGet).click();
					mapOrg.remove(idToGet);
				});
			}
		}
	}

	private void setThemeSelectedForButton(Button btnInput) {
		btnInput.setText("Hủy");
		btnInput.addThemeVariants(ButtonVariant.LUMO_ERROR);
	}

	private void setThemeDeSelectedForButton(Button btnInput) {
		btnInput.setText("Chọn");
		btnInput.removeThemeVariants(ButtonVariant.LUMO_ERROR);
	}

	public Map<String, TaskAssigneeOrgModel> getMapOrg() {
		return mapOrg;
	}
	public void setMapOrg(Map<String, TaskAssigneeOrgModel> mapOrg) {
		this.mapOrg = mapOrg;
	}

}
