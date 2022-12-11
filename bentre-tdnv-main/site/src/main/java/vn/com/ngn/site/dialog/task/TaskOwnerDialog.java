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
import vn.com.ngn.site.model.SimpleUserModel;
import vn.com.ngn.site.model.taskcreate.TaskAssigneeUserModel;
import vn.com.ngn.site.util.GeneralUtil;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.HeaderUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;

public class TaskOwnerDialog extends DialogTemplate{
	private VerticalLayout vTop = new VerticalLayout();
	private VerticalLayout vDisplayUserSelected = new VerticalLayout();
	
	private VerticalLayout vBottom = new VerticalLayout();
	
	private TaskAssigneeUserModel modelUser;
	private Map<String, Button> mapButton = new HashMap<String, Button>();
	
	public TaskOwnerDialog(TaskAssigneeUserModel modelInput) {
		this.modelUser = modelInput;
		
		buildLayout();
		configComponent();
		displayUserSelected();
	}

	@Override
	public void buildLayout() {
		super.buildLayout();
		caption.setText("Chọn cán bộ giao nhiệm vụ");
		
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
	}
	
	private void buildTopLayout() throws IOException {
		vTop.add(vDisplayUserSelected);
		
		vDisplayUserSelected.setPadding(false);
		
		//vTop.setMinHeight("100px");
		vTop.setWidthFull();
		vTop.setPadding(false);
		
		loadData();
	}
	
	private void buildBottomLayout() {
		vBottom.setWidthFull();
		vBottom.setPadding(false);
	}
	
	private void loadData() throws IOException {
		Details detail = new Details();
		
		Board boardHeader = new Board();
		boardHeader.addRow(new Html("<h5 style='margin: 0'>"+SessionUtil.getOrg().getName()+"</h5>"));
		boardHeader.setWidthFull();
		
		VerticalLayout vContent = new VerticalLayout();
		HorizontalLayout hHeader = new HorizontalLayout();
		
		Html htmlUsernameHeader = new Html("<div style='width:100%'><b>Tên cán bộ</b></div>");
		Html htmlBlankHeader = new Html("<div style='width:140px'></div>");
		
		hHeader.add(htmlUsernameHeader,htmlBlankHeader);
		hHeader.setWidthFull();
		
		vContent.add(hHeader);
		
		for(SimpleUserModel modelSimpleUser : SessionUtil.getOrg().getLeadersTask()) {
			HorizontalLayout hUser = new HorizontalLayout();
			
			String userId = modelSimpleUser.getUserId();
			String orgId = modelSimpleUser.getOrganizationId();
			String fullName = modelSimpleUser.getFullName();
			String orgName = modelSimpleUser.getOrganizationName();
			
			String strUsername = "<div class='info-block' title='"+fullName+"'>"+fullName+"</div>";
			
			Html htmlUsername = new Html(strUsername);
			Button btnChoose = new Button("Chọn");
			
			btnChoose.setId(userId);
			
			hUser.add(htmlUsername,btnChoose);
			hUser.addClassName("hLayout-userinfo-select");
			hUser.setDefaultVerticalComponentAlignment(Alignment.CENTER);
			hUser.setWidthFull();
			
			vContent.add(hUser);
			
			mapButton.put(userId+"-"+orgId, btnChoose);
			
			//Xét khi bật lại window sau khi đã chọn
			if(modelUser!=null && modelUser.getIdUser().equals(userId) && modelUser.getIdOrg().equals(orgId)) {
				setThemeSelectedForButton(btnChoose);
			}
			var modelUserTmp = new TaskAssigneeUserModel();
			
			modelUserTmp.setIdUser(userId);
			modelUserTmp.setFullName(fullName);
			modelUserTmp.setIdOrg(orgId);
			modelUserTmp.setOrgName(orgName);
			btnChoose.addClickListener(e->{
				if(modelUser==null) {
					modelUser = modelUserTmp;
					
					setThemeSelectedForButton(btnChoose);
					
					close();
				} else {
					if(modelUser.getIdUser().equals(userId) && modelUser.getIdOrg().equals(orgId)) {
						System.out.println("step1");
						String title = "Hủy người giao";
						String description = "Bạn có muốn hủy người giao nhiệm vụ này?";
						
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
						String description = "Bạn có muốn đổi người giao nhiệm vụ?";
						
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
		
		detail.setOpened(true);
		
		vBottom.add(HeaderUtil.createHeader5(VaadinIcon.HOME.create(),"Đơn vị chủ quản:","#ca0f0f"));
		vBottom.add(detail);
	}
	
	private void displayUserSelected() {
		vDisplayUserSelected.removeAll();
		if(modelUser==null) {
			String displayHtml = "<div class='task-assignee-empty-display'>"
					+"Chưa có cán bộ nào được chọn."
					+"</div>";
			
			Html html = new Html(displayHtml);
			vDisplayUserSelected.add(html);
		} else {
			var captionSelected = HeaderUtil.createHeader5(VaadinIcon.USER_STAR.create(),"Cán bộ được chọn:",null);
			
			HorizontalLayout hDisplayUser = new HorizontalLayout();
			
			String strFullname = "<div class='info-block' title='"+modelUser.getFullName()+"'><b>Họ tên: </b>"+modelUser.getFullName()+"<div>";
			String strOrgname = "<div class='info-block' title='"+modelUser.getOrgName()+"'><b>Đơn vị: </b>"+modelUser.getOrgName()+"<div>";
			
			hDisplayUser.setDefaultVerticalComponentAlignment(Alignment.CENTER);
			
			hDisplayUser.addClassName("hLayout-userinfo-owner-selected");
			
			Html htmlFullname = new Html(strFullname);
			Html htmlOrgname = new Html(strOrgname);
			Button btnDeSelected = new Button("Hủy");
			
			btnDeSelected.addThemeVariants(ButtonVariant.LUMO_ERROR);
			
			hDisplayUser.add(htmlFullname,htmlOrgname,btnDeSelected);
			
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
