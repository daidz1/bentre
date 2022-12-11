package vn.com.ngn.site.views.tasklist.component;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.progressbar.ProgressBar;

import vn.com.ngn.site.dialog.ViewFileDialog;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.util.GeneralUtil;
import vn.com.ngn.site.util.LocalDateUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;

public class TaskProgressComponent extends TaskInfoComponent{
	private VerticalLayout vDetail = new VerticalLayout();
	private boolean isExpand = false;

	public TaskProgressComponent(JsonArray jsonArray) {
		this.jsonArray = jsonArray;

		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		super.buildLayout();
		vDetail.getStyle().set("padding-left", "30px");
		vDetail.getStyle().set("padding-right", "0px");

		if(jsonArray.size()>0) {
			this.add(buildLastestBlock(1,jsonArray.get(0).getAsJsonObject()));
			this.add(vDetail);
			int count = 0;
			for(JsonElement jsonEle : jsonArray) {
				if(count==0) {
					count++;
					continue;
				} else {
					vDetail.add(buildLastestBlock(0,jsonEle.getAsJsonObject()));
					count++;
				}
			}
			vDetail.setVisible(false);
		} else {
			Span span = new Span("Chưa cập nhật tiến độ nào.");

			span.getStyle().set("margin-left", "11px");
			span.getStyle().set("font-style", "italic");
			span.getStyle().set("color", "7f7c7c");

			this.add(span);
		}
	}

	@Override
	public void configComponent() {
		super.configComponent();
	}

	private VerticalLayout buildLastestBlock(int index,JsonObject jsonProgress) {
		VerticalLayout vLayout = new VerticalLayout();
		int percent = jsonProgress.get("percent").getAsInt();

		//progress text
		String strProgress = "";
		if(index==1) {
			strProgress = "<div style='color:#1676f3;'><b>Tiến độ hiện tại: </b>"+percent+"%</div>";
		} else {
			strProgress = "<div style='color:#1676f3;'><b>Tiến độ: </b>"+percent+"%</div>";
		}

		Html htmlProgress = new Html(strProgress);

		// progress bar
		ProgressBar progressBar = new ProgressBar(0, 100, percent);

		if(index==1)
			progressBar.setHeight("5px");
		else 
			progressBar.setHeight("3px");

		//thông tin progress
		HorizontalLayout hLayout = new HorizontalLayout();

		String fullName = jsonProgress.getAsJsonObject("creator").get("fullName").getAsString();
		String dateString = LocalDateUtil.formatLocalDateTime(LocalDateUtil.longToLocalDateTime(jsonProgress.get("createdTime").getAsLong()),LocalDateUtil.dateTimeFormater1);

		String strUser = "<div style='width:100%'>"
				+"<div style='width:100%; margin-bottom:3px'><b class='caption-head'>Diễn giải: </b><div style='width: calc(100% - 110px); display: inline-block;font-weight:500'>"+jsonProgress.get("explain").getAsString()+"</div></div>"
				+"<div style='margin-bottom: 3px;'><b class='caption-head'>Người nhập: </b>"+fullName+" <b style='margin-left: 30px;'>Ngày nhập: </b>"+dateString+"</div>"
				+"</div>";

		Html htmlUser = new Html(strUser);

		//thông tin dinh kiem
		VerticalLayout vDinhKem = new VerticalLayout();

		JsonArray jsonArrAttachment = jsonProgress.getAsJsonArray("attachments"); 

		MenuBar menuAttachment = new MenuBar();
		MenuItem attachment = menuAttachment.addItem("Đính kèm ("+jsonArrAttachment.size()+")");

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
			vDinhKem.add(anchor);
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

		vDinhKem.add(menuAttachment);
		if(index==1 && jsonArray.size()>1) {
			HorizontalLayout hExpand = new HorizontalLayout();
			Span expandText = new Span("Hiển thị tất cả ("+(jsonArray.size()-1)+")");
			hExpand.add(expandText);
			hExpand.setDefaultVerticalComponentAlignment(Alignment.CENTER);

			hExpand.getStyle().set("color", "#0849da");
			hExpand.getStyle().set("cursor", "pointer");

			hExpand.addClickListener(e->{
				if(isExpand) {
					expandText.setText("Hiển thị tất cả ("+(jsonArray.size()-1)+")");
				} else {
					expandText.setText("Ẩn bớt");
				}

				vDetail.setVisible(!isExpand);

				isExpand = !isExpand;
			});

			vDinhKem.add(hExpand);
		}

		vDinhKem.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		vDinhKem.setPadding(false);
		vDinhKem.setSizeUndefined();

		hLayout.add(htmlUser);
		hLayout.add(vDinhKem);

		hLayout.expand(htmlUser);

		hLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		hLayout.setWidthFull();
		hLayout.getStyle().set("margin", "0");

		vLayout.add(htmlProgress);
		vLayout.add(progressBar);
		vLayout.add(hLayout);

		vLayout.setWidthFull();
		vLayout.setPadding(false);
		//vLayout.addClassName("detail-taskprogress");

		return vLayout;
	}
}
