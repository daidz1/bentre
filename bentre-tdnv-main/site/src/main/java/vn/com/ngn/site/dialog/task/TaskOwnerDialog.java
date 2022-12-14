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
		caption.setText("Ch???n c??n b??? giao nhi???m v???");
		
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
		
		Html htmlUsernameHeader = new Html("<div style='width:100%'><b>T??n c??n b???</b></div>");
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
			Button btnChoose = new Button("Ch???n");
			
			btnChoose.setId(userId);
			
			hUser.add(htmlUsername,btnChoose);
			hUser.addClassName("hLayout-userinfo-select");
			hUser.setDefaultVerticalComponentAlignment(Alignment.CENTER);
			hUser.setWidthFull();
			
			vContent.add(hUser);
			
			mapButton.put(userId+"-"+orgId, btnChoose);
			
			//X??t khi b???t l???i window sau khi ???? ch???n
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
						String title = "H???y ng?????i giao";
						String description = "B???n c?? mu???n h???y ng?????i giao nhi???m v??? n??y?";
						
						ConfirmDialog confDialog = new ConfirmDialog(title, description, 
								"X??c nh???n", 
								eConfirm->{
									setThemeDeSelectedForButton(btnChoose);
									System.out.println("step2");
									modelUser = null;
									displayUserSelected();
								},
								"H???y",
								eCancel->{
									eCancel.getSource().close();
								});
						confDialog.open();
					} else {
						String title = "?????i ng?????i x??? l??";
						String description = "B???n c?? mu???n ?????i ng?????i giao nhi???m v????";
						
						ConfirmDialog confDialog = new ConfirmDialog(title, description, 
								"X??c nh???n", 
								eConfirm->{
									modelUser = modelUserTmp;
									close();
								},
								"H???y",
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
		
		vBottom.add(HeaderUtil.createHeader5(VaadinIcon.HOME.create(),"????n v??? ch??? qu???n:","#ca0f0f"));
		vBottom.add(detail);
	}
	
	private void displayUserSelected() {
		vDisplayUserSelected.removeAll();
		if(modelUser==null) {
			String displayHtml = "<div class='task-assignee-empty-display'>"
					+"Ch??a c?? c??n b??? n??o ???????c ch???n."
					+"</div>";
			
			Html html = new Html(displayHtml);
			vDisplayUserSelected.add(html);
		} else {
			var captionSelected = HeaderUtil.createHeader5(VaadinIcon.USER_STAR.create(),"C??n b??? ???????c ch???n:",null);
			
			HorizontalLayout hDisplayUser = new HorizontalLayout();
			
			String strFullname = "<div class='info-block' title='"+modelUser.getFullName()+"'><b>H??? t??n: </b>"+modelUser.getFullName()+"<div>";
			String strOrgname = "<div class='info-block' title='"+modelUser.getOrgName()+"'><b>????n v???: </b>"+modelUser.getOrgName()+"<div>";
			
			hDisplayUser.setDefaultVerticalComponentAlignment(Alignment.CENTER);
			
			hDisplayUser.addClassName("hLayout-userinfo-owner-selected");
			
			Html htmlFullname = new Html(strFullname);
			Html htmlOrgname = new Html(strOrgname);
			Button btnDeSelected = new Button("H???y");
			
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
		btnInput.setText("H???y");
		btnInput.addThemeVariants(ButtonVariant.LUMO_ERROR);
	}
	
	private void setThemeDeSelectedForButton(Button btnInput) {
		btnInput.setText("Ch???n");
		btnInput.removeThemeVariants(ButtonVariant.LUMO_ERROR);
	}

	public TaskAssigneeUserModel getModelUser() {
		return modelUser;
	}
	public void setModelUser(TaskAssigneeUserModel modelUser) {
		this.modelUser = modelUser;
	}
}
