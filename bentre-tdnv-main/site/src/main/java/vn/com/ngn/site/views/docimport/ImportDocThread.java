package vn.com.ngn.site.views.docimport;

import java.util.List;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;

import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.model.CustomPairModel;
import vn.com.ngn.site.model.docimport.DocImportGridModel;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.DocServiceUtil;

public class ImportDocThread implements Runnable{
	private UI ui;
	private String token;
	private String userInfo;
	private CustomPairModel<Grid<DocImportGridModel>, List<DocImportGridModel>> pair;
	private DocImportGridModel modelSeleted;
	public ImportDocThread(UI ui,CustomPairModel<Grid<DocImportGridModel>, List<DocImportGridModel>> pair,DocImportGridModel modelSeleted,String token,String userInfo) {
		this.ui = ui;
		this.token = token;
		this.userInfo = userInfo;
		this.pair = pair;
		this.modelSeleted = modelSeleted;
	}

	@Override
	public void run() {
		ui.access(() -> {
			try {
				JsonObject jsonResponse = DocServiceUtil.createDocument(modelSeleted.getJsonDoc(),token,userInfo);

				if(jsonResponse.get("status").getAsInt()==201) {
					NotificationUtil.showNotifi("Thêm thành công văn bản số hiệu "+modelSeleted.getJsonDoc().get("docSignal").getAsString()+".", NotificationTypeEnum.SUCCESS);
				
					pair.getKey().asMultiSelect().deselect(modelSeleted);
					pair.getValue().remove(modelSeleted);
				} else if(jsonResponse.get("status").getAsInt()==409){
					NotificationUtil.showNotifi("Văn bản số hiệu "+modelSeleted.getJsonDoc().get("docSignal").getAsString()+" đã tồn tại", NotificationTypeEnum.ERROR);
				} else {
					System.out.println(jsonResponse);
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
