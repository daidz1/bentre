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

	// ph???c v??? cho vi???c khi truy???n 1 list v??o s??? disable c??c user gi???ng nhau v?? kh??c ????n v???
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
		caption.setText("Ch???n c?? quan/????n v??? h??? tr??? nhi???m v???");

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
		txtKeyword.setPlaceholder("Nh???p v??o t??n c?? quan/????n v??? ????? t??m...");

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

			vBottom.add(HeaderUtil.createHeader5(VaadinIcon.HOME_O.create(),"????n v??? tr???c thu???c:","#115fa2"));

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
		Button btnChoose = new Button("Ch???n");
		btnChoose.setId(orgId);

		var orgModelTmp = new TaskAssigneeOrgModel();

		orgModelTmp.setOrgId(orgId);
		orgModelTmp.setOrgName(orgName);
		
		String idToGet = orgId+"-"+orgName;
		mapButton.put(idToGet, btnChoose);
//		X??t khi b???t l???i window sau khi ???? ch???n
		if(mapOrg.containsKey(idToGet)) {
			setThemeSelectedForButton(btnChoose);
			listIdSelected.add(idToGet);
		}
		
		if(orgAssigneeModel!=null && orgAssigneeModel.getOrgId().equals(orgId)) {
			btnChoose.setEnabled(false);
			btnChoose.getElement().setAttribute("title", "C?? quan/????n v??? ???? ???????c ch???n l??m x??? l??");
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
					String title = "H???y c?? quan/????n v??? x??? l??";
					String description = "B???n c?? mu???n h???y quy???n x??? l?? c???a c?? quan/????n v??? n??y?";
					ConfirmDialog confDialog = new ConfirmDialog(title, description, 
							"X??c nh???n", 
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
							"H???y",
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
					+"Ch??a c?? c?? quan/????n v??? n??o ???????c ph??n c??ng h??? tr???."
					+"</div>";

			Html html = new Html(displayHtml);
			vDisplayUserSelected.add(html);
		} else {
			var captionSelected = HeaderUtil.createHeader5(VaadinIcon.CHECK.create(),"Danh s??ch c?? quan/????n v??? ???????c ph??n c??ng h??? tr???:",null);
			vDisplayUserSelected.add(captionSelected);

			for(Entry<String,TaskAssigneeOrgModel> entry : mapOrg.entrySet()) {
				HorizontalLayout hDisplayUser = new HorizontalLayout();
				var modelUser = entry.getValue();

				String strOrgname = "<div class='info-block' title='"+modelUser.getOrgName()+"'><b>????n v???: </b>"+modelUser.getOrgName()+"<div>";


				hDisplayUser.setDefaultVerticalComponentAlignment(Alignment.CENTER);

				hDisplayUser.addClassName("hLayout-userinfo-support-selected");

				Html htmlOrgname = new Html(strOrgname);
				Button btnDeSelected = new Button("H???y");

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
		btnInput.setText("H???y");
		btnInput.addThemeVariants(ButtonVariant.LUMO_ERROR);
	}

	private void setThemeDeSelectedForButton(Button btnInput) {
		btnInput.setText("Ch???n");
		btnInput.removeThemeVariants(ButtonVariant.LUMO_ERROR);
	}

	public Map<String, TaskAssigneeOrgModel> getMapOrg() {
		return mapOrg;
	}
	public void setMapOrg(Map<String, TaskAssigneeOrgModel> mapOrg) {
		this.mapOrg = mapOrg;
	}

}
