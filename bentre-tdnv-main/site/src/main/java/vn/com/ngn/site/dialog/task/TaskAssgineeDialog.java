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
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import vn.com.ngn.site.dialog.DialogTemplate;
import vn.com.ngn.site.model.CustomPairModel;
import vn.com.ngn.site.model.taskcreate.TaskAssigneeUserModel;
import vn.com.ngn.site.util.GeneralUtil;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.HeaderUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;

@SuppressWarnings("serial")
public class TaskAssgineeDialog extends DialogTemplate{
	private VerticalLayout vTop = new VerticalLayout();
	private HorizontalLayout hSearch = new HorizontalLayout();
	private VerticalLayout vDisplayUserSelected = new VerticalLayout();
	
	private TextField txtKeyword = new TextField();
	private Button btnSearch = new Button(VaadinIcon.SEARCH.create());
	
	private VerticalLayout vBottom = new VerticalLayout();
	
	private List<CustomPairModel<String, Details>> listOrgLayout = new ArrayList<CustomPairModel<String,Details>>();
	
	private TaskAssigneeUserModel modelUser;
	private Map<String, Button> mapButton = new HashMap<String, Button>();
	
	private ShortcutRegistration regisShortcut = null;
	
	private Map<String,TaskAssigneeUserModel> mapUserSupport = new HashMap<String,TaskAssigneeUserModel>();

	public TaskAssgineeDialog(TaskAssigneeUserModel modelInput,Map<String,TaskAssigneeUserModel> mapUserSupport) {
		this.modelUser = modelInput;
		this.mapUserSupport = mapUserSupport;
		
		buildLayout();
		configComponent();
		displayUserSelected();
	}

	@Override
	public void buildLayout() {
		super.buildLayout();
		caption.setText("Chọn cán bộ xử lý nhiệm vụ");
		
		vMain.add(vTop);
		vMain.add(vBottom);
		
		vMain.setWidthFull();
		
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
		txtKeyword.setPlaceholder("Nhập vào tên cán bộ hoặc tên đơn vị để tìm...");
		
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
			
			var jsonCurrentOrg = jsonResult.get("currentOrganzation").getAsJsonObject();
			var jsonArrSubOrg = jsonResult.get("subOrganzation").getAsJsonArray();
			
			vBottom.add(HeaderUtil.createHeader5(VaadinIcon.HOME.create(),"Đơn vị chủ quản:","#ca0f0f"));
			vBottom.add(buildOrgBlock(jsonCurrentOrg.get("organization").getAsJsonObject(), jsonCurrentOrg.get("users").getAsJsonArray(),true).getValue());
			vBottom.add(HeaderUtil.createHeader5(VaadinIcon.HOME_O.create(),"Đơn vị trực thuộc:","#115fa2"));
			
			for(JsonElement jsonSubOrgEle : jsonArrSubOrg) {
				var jsonSubOrg = jsonSubOrgEle.getAsJsonObject();
				
				CustomPairModel<String, Details> subOrgData = buildOrgBlock(jsonSubOrg.get("organization").getAsJsonObject(), jsonSubOrg.get("users").getAsJsonArray(),false);
				vBottom.add(subOrgData.getValue());
			
				listOrgLayout.add(new CustomPairModel<String, Details>(subOrgData.getKey(), subOrgData.getValue()));
			}
		} else {
			System.out.println(jsonObject);
		}
	}
	
	private void search() {
		String keyword = txtKeyword.getValue().trim();
		
		for(CustomPairModel<String, Details> model : listOrgLayout) {
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
	
	private CustomPairModel<String ,Details> buildOrgBlock(JsonObject jsonOrg, JsonArray jsonArrUser,boolean isMain) {
		Details detail = new Details();
		String searchString = "";
		
		String orgId = jsonOrg.get("id").getAsString();
		String orgName = jsonOrg.get("name").getAsString();
		
		searchString +=orgName;
		
		Board boardHeader = new Board();
		boardHeader.addRow(new Html("<h5 style='margin: 0'>"+orgName+"</h5>"));
		boardHeader.setWidthFull();
		
		VerticalLayout vContent = new VerticalLayout();
		HorizontalLayout hHeader = new HorizontalLayout();
		
		Html htmlUsernameHeader = new Html("<div style='width:100%'><b>Tên cán bộ</b></div>");
		Html htmlJobTitleHeader = new Html("<div style='width:100%'><b>Chức vụ</b></div>");
		Html htmlBlankHeader = new Html("<div style='width:140px'></div>");
		
		hHeader.add(htmlUsernameHeader,htmlJobTitleHeader,htmlBlankHeader);
		hHeader.setWidthFull();
		
		vContent.add(hHeader);
		
		for(JsonElement jsonUserEle : jsonArrUser) {
			HorizontalLayout hUser = new HorizontalLayout();
			
			JsonObject jsonUser = jsonUserEle.getAsJsonObject();
			
			String userId = jsonUser.get("id").getAsString();
			String fullName = jsonUser.get("fullName").getAsString();
			String jobTitle = jsonUser.get("jobTitle").getAsString();
			
			searchString+=" - "+fullName+" - "+jobTitle;
			
			String strUsername = "<div class='info-block' title='"+fullName+"'>"+fullName+"</div>";
			String strJobTitle = "<div class='info-block' title='"+jobTitle+"'>"+jobTitle+"</div>";
			
			Html htmlUsername = new Html(strUsername);
			Html htmlJobTitle = new Html(strJobTitle);
			Button btnChoose = new Button("Chọn");
			
			btnChoose.setId(userId);
			
			hUser.add(htmlUsername,htmlJobTitle,btnChoose);
			hUser.addClassName("hLayout-userinfo-select");
			hUser.setDefaultVerticalComponentAlignment(Alignment.CENTER);
			hUser.setWidthFull();
			
			vContent.add(hUser);
			
			mapButton.put(userId+"-"+orgId, btnChoose);
			
			//Xét khi bật lại window sau khi đã chọn
			if(modelUser!=null && modelUser.getIdUser().equals(userId) && modelUser.getIdOrg().equals(orgId)) {
				setThemeSelectedForButton(btnChoose);
			}
			for(Entry<String,TaskAssigneeUserModel> entry : mapUserSupport.entrySet()) {
				if(entry.getKey().split(Pattern.quote("-"))[0].equals(userId)) {
					btnChoose.setEnabled(false);
				}
			}
			var modelUserTmp = new TaskAssigneeUserModel();
			
			modelUserTmp.setIdUser(userId);
			modelUserTmp.setFullName(fullName);
			modelUserTmp.setJobTitle(jobTitle);
			modelUserTmp.setIdOrg(orgId);
			modelUserTmp.setOrgName(orgName);
			btnChoose.addClickListener(e->{
				if(modelUser==null) {
					modelUser = modelUserTmp;
					
					setThemeSelectedForButton(btnChoose);
					
					close();
				} else {
					if(modelUser.getIdUser().equals(userId) && modelUser.getIdOrg().equals(orgId)) {
						String title = "Hủy người xử lý";
						String description = "Bạn có muốn hủy quyền xử lý của cán bộ này?";
						
						ConfirmDialog confDialog = new ConfirmDialog(title, description, 
								"Xác nhận", 
								eConfirm->{
									setThemeDeSelectedForButton(btnChoose);
									System.out.println("step2");
									modelUser = null;
									displayUserSelected();
								},
								"Hủy",
								eCancel->{
									eCancel.getSource().close();
								});
						confDialog.open();
					} else {
						String title = "Đổi người xử lý";
						String description = "Bạn có muốn đổi người xử lý nhiệm vụ?";
						
						ConfirmDialog confDialog = new ConfirmDialog(title, description, 
								"Xác nhận", 
								eConfirm->{
									modelUser = modelUserTmp;
									close();
								},
								"Hủy",
								eCancel->{
									eCancel.getSource().close();
								});
						confDialog.open();
					}
				}
			});
		}
		
		//detail.setSummaryText(jsonOrg.get("name").getAsString());
		detail.setSummary(boardHeader);
		detail.setContent(vContent);
		
		detail.addThemeVariants(DetailsVariant.FILLED,DetailsVariant.REVERSE);
		detail.getStyle().set("width", "100%");
		
		if(isMain) {
			detail.setOpened(true);
		} else {
		}
		
		return new CustomPairModel<String, Details>(searchString, detail);
	}
	
	private void displayUserSelected() {
		vDisplayUserSelected.removeAll();
		if(modelUser==null) {
			String displayHtml = "<div class='task-assignee-empty-display'>"
					+"Chưa có cán bộ nào được phân công xử lý."
					+"</div>";
			
			Html html = new Html(displayHtml);
			vDisplayUserSelected.add(html);
		} else {
			var captionSelected = HeaderUtil.createHeader5(VaadinIcon.USER_STAR.create(),"Cán bộ được phân công:",null);
			
			HorizontalLayout hDisplayUser = new HorizontalLayout();
			
			String strFullname = "<div class='info-block' title='"+modelUser.getFullName()+"'><b>Họ tên: </b>"+modelUser.getFullName()+"<div>";
			String strOrgname = "<div class='info-block' title='"+modelUser.getOrgName()+"'><b>Đơn vị: </b>"+modelUser.getOrgName()+"<div>";
			String strJobTitle = "<div class='info-block' title='"+modelUser.getJobTitle()+"'><b>Chức vụ: </b>"+modelUser.getJobTitle()+"<div>";
			
			hDisplayUser.setDefaultVerticalComponentAlignment(Alignment.CENTER);
			
			hDisplayUser.addClassName("hLayout-userinfo-assignee-selected");
			
			Html htmlFullname = new Html(strFullname);
			Html htmlOrgname = new Html(strOrgname);
			Html htmlJobTitle = new Html(strJobTitle);
			Button btnDeSelected = new Button("Hủy");
			
			btnDeSelected.addThemeVariants(ButtonVariant.LUMO_ERROR);
			
			hDisplayUser.add(htmlFullname,htmlOrgname,htmlJobTitle,btnDeSelected);
			
			hDisplayUser.setWidthFull();
			vDisplayUserSelected.add(captionSelected);
			vDisplayUserSelected.add(hDisplayUser);
			
			btnDeSelected.addClickListener(e->{
				mapButton.get(modelUser.getIdUser()+"-"+modelUser.getIdOrg()).click();
				modelUser = null;
			});
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

	public TaskAssigneeUserModel getModelUser() {
		return modelUser;
	}
	public void setModelUser(TaskAssigneeUserModel modelUser) {
		this.modelUser = modelUser;
	}
}
