package vn.com.ngn.site.dialog.doc;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import vn.com.ngn.site.dialog.DialogTemplate;
import vn.com.ngn.site.util.service.DocServiceUtil;
import vn.com.ngn.site.views.doclist.component.DocAttachmentComponent;

public class DocAttachmentDialog extends DialogTemplate{
	private String docId;
	
	public DocAttachmentDialog(String docId) {
		this.docId = docId;
		
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		super.buildLayout();
		caption.setText("Danh sác đính kèm của văn bản");
		try {
			JsonObject jsonResponseGet = DocServiceUtil.getAttachmentList(docId);
			
			if(jsonResponseGet.get("status").getAsInt()==200) {
				JsonArray jsonArray = jsonResponseGet.get("result").getAsJsonArray();
				
				DocAttachmentComponent comComment = new DocAttachmentComponent(true,jsonArray);
				
				vMain.add(comComment);
			} else {
				System.out.println(jsonResponseGet);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.setWidth("80%");
		//this.setMinHeight("500px");
	}

	@Override
	public void configComponent() {
		super.configComponent();
	}
}
