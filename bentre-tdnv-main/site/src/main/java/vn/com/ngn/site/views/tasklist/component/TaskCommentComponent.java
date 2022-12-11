package vn.com.ngn.site.views.tasklist.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextField;

import vn.com.ngn.site.dialog.ViewFileDialog;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.model.UploadModuleDataWithDescriptionModel;
import vn.com.ngn.site.module.upload.UploadModuleWithDescription;
import vn.com.ngn.site.util.BroadcasterSupportUitl;
import vn.com.ngn.site.util.BroadcasterUtil;
import vn.com.ngn.site.util.GeneralUtil;
import vn.com.ngn.site.util.LocalDateUtil;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;

public class TaskCommentComponent extends TaskInfoComponent{
	private VerticalLayout vComment = new VerticalLayout();

	private String taskId;
	private boolean isSelfRefresh;
	private String token;

	private VerticalLayout vSubChatbox = createChatbox();
	
	public TaskCommentComponent(String taskId,JsonObject jsonObject,JsonArray jsonArray,boolean isSelfRefresh,String token) {
		this.taskId = taskId;
		this.jsonObject = jsonObject;
		this.jsonArray = jsonArray;
		this.isSelfRefresh = isSelfRefresh;
		this.token = token;
		
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		super.buildLayout();

		this.add(createChatbox());
		this.add(vComment);

		vComment.setPadding(false);
		vComment.setSpacing(false);

		displayCommnet();
	}

	@Override
	public void configComponent() {
		super.configComponent();
	}

	public void displayCommnet() {
		vComment.removeAll();
		
		if(token==null)
			token = SessionUtil.getToken();

		for(JsonElement jsonParentEle : jsonArray) {
			JsonObject jsonParentComment = jsonParentEle.getAsJsonObject();
			VerticalLayout vSubComment = new VerticalLayout();

			String parentId = jsonParentComment.get("id").getAsString();

			vComment.add(createCommentBlock(jsonParentComment,parentId,vSubComment));
			vComment.add(vSubComment);

			for(JsonElement jsonSubEle : jsonParentComment.getAsJsonArray("replies")) {
				vSubComment.add(createCommentBlock(jsonSubEle.getAsJsonObject(),parentId,vSubComment));
			}

			vSubComment.setPadding(false);
			vSubComment.setSpacing(false);
			vSubComment.getStyle().set("padding-left", "43px");
		}
	}

	private VerticalLayout createChatbox() {
		VerticalLayout vChatbox = new VerticalLayout();
		HorizontalLayout hChatbox = new HorizontalLayout();
		UploadModuleWithDescription upload = new UploadModuleWithDescription();

		Avatar ava = new Avatar();
		TextField txtComment = new TextField();
		Button btnAttachment = new Button(VaadinIcon.PAPERCLIP.create());

		txtComment.setWidthFull();
		txtComment.setPlaceholder("Nhập vào bình luận mới");

		btnAttachment.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAttachment.getElement().setAttribute("title", "Chọn để hiển thị giao diện thêm đính kèm.");

		vChatbox.add(hChatbox);
		vChatbox.add(upload);

		hChatbox.add(ava,txtComment,btnAttachment);
		hChatbox.expand(txtComment);
		hChatbox.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		hChatbox.setWidthFull();
		hChatbox.getStyle().set("margin-bottom", "10px");

		upload.setMultiFile(true);
		upload.initUpload();
		upload.setVisible(false);
		upload.getStyle().set("margin-bottom", "10px");

		vChatbox.setSpacing(false);
		vChatbox.setPadding(false);

		txtComment.addKeyDownListener(Key.ENTER, e->{
			if(!txtComment.getValue().isEmpty()) {
				try {
					String parentId = null;
					if(!vChatbox.getId().isEmpty())
						parentId = vChatbox.getId().get().replace("replyfor-", "");
					List<UploadModuleDataWithDescriptionModel> listAttachment = new ArrayList<UploadModuleDataWithDescriptionModel>();
					if(upload.isVisible())
						listAttachment = upload.getListFileUpload();
					JsonObject jsonResponse = TaskServiceUtil.createComment(taskId, txtComment.getValue().trim(), parentId,listAttachment);

					if(jsonResponse.get("status").getAsInt()==201) {
						btnTrigger.click();
						txtComment.setValue("");
						
						String messageBroadcast = BroadcasterSupportUitl.createMessageOnTask(jsonObject);
						messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.MAINVIEW);
						messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.TASKDETAIL);
						messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.COMMENTDIALOG);
						
						BroadcasterUtil.broadcast(messageBroadcast);

						if(isSelfRefresh) {
							reload();
						}
					} else {
						System.out.println(jsonResponse);
						NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		btnAttachment.addClickListener(e->{
			upload.setVisible(!upload.isVisible()); 
		});

		return vChatbox;
	}

	private HorizontalLayout createCommentBlock(JsonObject jsonComment,String parentId,VerticalLayout vSubCommnet) {
		JsonObject jsonCreator = jsonComment.getAsJsonObject("creator");

		String fullName = jsonCreator.get("fullName").getAsString();
		String comment = jsonComment.get("message").getAsString();
		JsonArray jsonArrAttachment = jsonComment.getAsJsonArray("attachments"); 

		HorizontalLayout hComment = new HorizontalLayout();
		Avatar ava = new Avatar(fullName);

		//ava.setImage("https://i.pinimg.com/736x/5d/d1/42/5dd14201fa667087ad54038a5425403c.jpg");

		VerticalLayout vCommnetWrap = new VerticalLayout();
		VerticalLayout vCommentContent = new VerticalLayout();
		H5 name = new H5(fullName);
		Div divCommentContent = new Div();

		HorizontalLayout hAction = new HorizontalLayout();
		Span spanReply = new Span("Trả lời");
		Span spanTime = new Span(LocalDateUtil.formatLocalDateTime(LocalDateUtil.longToLocalDateTime(jsonComment.get("createdTime").getAsLong()),LocalDateUtil.dateTimeFormater1));

		vCommnetWrap.add(vCommentContent,hAction);

		divCommentContent.setText(comment);

		vCommentContent.add(name);
		vCommentContent.add(divCommentContent);

		name.addClassName("comment-name");
		//divCommentContent.addClassName("comment-content");

		vCommentContent.addClassName("comment-content");

		vCommentContent.setSpacing(false);
		vCommnetWrap.addClassName("comment-wrap");
		vCommnetWrap.setSpacing(false);

		hAction.add(spanReply,spanTime); 

		spanReply.addClassName("comment-reply-button");
		spanTime.addClassName("comment-time");

		hComment.add(ava,vCommnetWrap);
		hComment.addClassName("commnet-block");
		hComment.setSpacing(false);

		if(jsonArrAttachment.size()>0) {
			Span spanAttach = new Span("Đính kèm ("+jsonArrAttachment.size()+")");
			MenuBar menuAttachment = new MenuBar();
			MenuItem attachment = menuAttachment.addItem(spanAttach);
			hAction.add(menuAttachment);
			menuAttachment.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

			spanAttach.getStyle().set("font-size", "13px !important");
			spanAttach.getStyle().set("margin-top", "-17px");
			menuAttachment.setHeight("20px");

			for(JsonElement jsonEle : jsonArrAttachment) {
				JsonObject jsonAttac = jsonEle.getAsJsonObject();

				String fileName = jsonAttac.get("fileName").getAsString();
				String filePath = jsonAttac.get("filePath").getAsString();
				String description = jsonAttac.get("description").getAsString();

				HorizontalLayout hLogut = new HorizontalLayout();
				Icon iconLogout = VaadinIcon.DOWNLOAD.create();
				iconLogout.setSize("13px");
				Span logoutText = new Span(fileName);
				Anchor anchor = new Anchor();
				anchor.getElement().setAttribute("download", true);
				anchor.setId(filePath);
				hLogut.add(iconLogout,logoutText);
				hComment.add(anchor);
				MenuItem itemAttach = attachment.getSubMenu().addItem(hLogut,
						e -> {
							try {
								JsonObject jsonResponse = TaskServiceUtil.getAttachmentContent(filePath);

								if(jsonResponse.get("status").getAsInt()==200) {
									String base64 = jsonResponse.get("result").getAsString();
									if(fileName.endsWith(".pdf")) {
										ViewFileDialog viewFile = new ViewFileDialog(fileName, GeneralUtil.getStreamResource(fileName, GeneralUtil.base64ToByteArray(base64)));

										viewFile.open();
									} else {
										anchor.setHref(GeneralUtil.getStreamResource(fileName, GeneralUtil.base64ToByteArray(base64)));

										Page page = UI.getCurrent().getPage();
										page.executeJavaScript("document.getElementById('"+filePath+"').click();");
									}
								} else {
									System.out.println(jsonResponse);
									NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!!", NotificationTypeEnum.ERROR);
								}
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						});
				itemAttach.getElement().setAttribute("title", "Ghi chú: "+description);
			}
		}

		spanReply.addClickListener(e->{
			vSubChatbox.setId("replyfor-"+parentId);
			TextField txtText = (TextField) ((HorizontalLayout) vSubChatbox.getComponentAt(0)).getComponentAt(1);
			txtText.focus();

			vSubCommnet.addComponentAtIndex(vSubCommnet.getComponentCount(), vSubChatbox);
		});

		return hComment;
	}
	
	public void reload() throws IOException {
		JsonObject jsonResponseGet = TaskServiceUtil.getCommentList(taskId,token);
		if(jsonResponseGet.get("status").getAsInt()==200) {
			jsonArray = jsonResponseGet.get("result").getAsJsonArray();
			displayCommnet();
		} else {
			System.out.println(jsonResponseGet);
		}
	}
}
