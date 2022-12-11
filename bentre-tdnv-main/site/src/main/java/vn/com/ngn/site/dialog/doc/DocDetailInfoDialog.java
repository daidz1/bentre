package vn.com.ngn.site.dialog.doc;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import vn.com.ngn.site.dialog.DialogTemplate;
import vn.com.ngn.site.util.component.HeaderUtil;
import vn.com.ngn.site.views.doclist.component.DocAttachmentComponent;
import vn.com.ngn.site.views.doclist.component.DocDetailInfoComponent;

public class DocDetailInfoDialog extends DialogTemplate{
	private HorizontalLayout captionDocAttachment = HeaderUtil.createHeader5WithBackground(VaadinIcon.FILE_TEXT.create(),"Đính kèm văn bản","rgb(101, 95, 89)","rgb(101 95 89/ 12%)");
	private JsonObject jsonDoc;
	
	public DocDetailInfoDialog(JsonObject jsonDoc) {
		this.jsonDoc = jsonDoc;
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		
		caption.setText("Thông tin văn bản");
		
		vMain.add(new DocDetailInfoComponent(jsonDoc));
		vMain.add(captionDocAttachment);
		vMain.add(new DocAttachmentComponent(false,jsonDoc.getAsJsonArray("docAttachments")));
		
		this.setWidth("900px");
	}

	@Override
	public void configComponent() {
		super.configComponent();

	}
}
