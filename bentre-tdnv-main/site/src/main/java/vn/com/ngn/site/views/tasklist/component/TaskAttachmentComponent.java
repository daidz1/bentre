package vn.com.ngn.site.views.tasklist.component;

import java.io.IOException;

import org.vaadin.alejandro.PdfBrowserViewer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

import vn.com.ngn.site.dialog.ViewFileDialog;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.util.GeneralUtil;
import vn.com.ngn.site.util.LocalDateUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;

public class TaskAttachmentComponent extends TaskInfoComponent{
	public TaskAttachmentComponent(JsonArray jsonArray) {
		this.jsonArray = jsonArray;

		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		super.buildLayout();
		
		if(jsonArray.size()>0) {
			for(int i = 0; i < jsonArray.size(); i++) {
				HorizontalLayout attachLayout = buildAttachmentLayout(jsonArray.get(i).getAsJsonObject());
				this.add(attachLayout);
				
				if(i<jsonArray.size()-1) {
					attachLayout.getStyle().set("border-bottom", "1px solid #e3e3e3");
					attachLayout.getStyle().set("padding-bottom", "12px");
				}
			}
		} else {
			Span span = new Span("Chưa cập nhật tập tin đính kèm nào.");
			
			span.getStyle().set("margin-left", "11px");
			span.getStyle().set("font-style", "italic");
			span.getStyle().set("color", "7f7c7c");
			
			this.add(span);
		}
		
		this.setPadding(false);
	}

	@Override
	public void configComponent() {
		super.configComponent();
	}

	private HorizontalLayout buildAttachmentLayout(JsonObject jsonAttachment) {
		HorizontalLayout hInfo = new HorizontalLayout();
		
		String fileName = jsonAttachment.get("fileName").getAsString();
		String description = jsonAttachment.get("description").getAsString();
		String fullName = jsonAttachment.getAsJsonObject("creator").get("fullName").getAsString();
		String dateString = LocalDateUtil.formatLocalDateTime(LocalDateUtil.longToLocalDateTime(jsonAttachment.get("createdTime").getAsLong()),LocalDateUtil.dateTimeFormater1);
		String filePath = jsonAttachment.get("filePath").getAsString();

		VerticalLayout vInfo = new VerticalLayout();
		String strUser = "<div style='width:100%'>"
				+"<div style='margin-bottom: 3px;'><b class='caption-head'>Tên đính kèm: </b><span style='font-weight:500;color:#454582'>"+fileName+"</span> </div>"
				+"<div style='width:100%;margin-bottom: 3px;'><b class='caption-head'>Mô tả: </b><div style='width: calc(100% - 110px); display: inline-block;'>"+description+"</div></div>"
				+"<div style='width:100%'><b class='caption-head'>Người nhập: </b><div style='width: calc(100% - 110px); display: inline-block;'>"+fullName+"<b style='margin-left: 30px;'>Ngày nhập: </b>"+dateString+"</div></div>"
				+"</div>";
		
		Html htmlUser = new Html(strUser);
		vInfo.add(htmlUser);
		vInfo.setPadding(false);
		
		Button btnDownload = new Button(VaadinIcon.DOWNLOAD.create());
		btnDownload.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		Anchor anchor = new Anchor("",btnDownload);
		anchor.setId(filePath);
		anchor.getElement().setAttribute("download", true);
		
		hInfo.add(vInfo,btnDownload,anchor);
		
		hInfo.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		hInfo.expand(vInfo);
		hInfo.setWidthFull();
		
//		hInfo.getStyle().set("padding-bottom", "5px");
//		hInfo.getStyle().set("border-bottom", "1px solid #cccccc");
		
		//hInfo.addClassName("detail-taskattachment");
		
		btnDownload.addClickListener(e->{
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
		
		return hInfo;
	}
}
