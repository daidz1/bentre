package vn.com.ngn.site.module.upload;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.shared.Registration;

import elemental.json.impl.JreJsonString;
import vn.com.ngn.site.model.CustomPairModel;
import vn.com.ngn.site.model.UploadModuleDataWithDescriptionModel;

@SuppressWarnings("serial")
public class UploadModuleWithDescription extends UploadModule {
	private Map<String, CustomPairModel<VerticalLayout,TextField>> mapForDisplay = new HashMap<String, CustomPairModel<VerticalLayout,TextField>>();

	@Override
	public void buildLayout() {
		super.buildLayout();
	}

	@Override
	public void configComponent() {
		super.configComponent();
		
		upload.addFinishedListener(e->{
			mapForDisplay.put(e.getFileName(), createFileBlock(e.getFileName()));
		});
		this.addFileRemoveListener(e->{
			mapForDisplay.remove(e.getFileName());
			loadDisplay();
		});
	}
	
	Registration addFileRemoveListener(ComponentEventListener<FileRemoveEvent> listener) {
        return super.addListener(FileRemoveEvent.class, listener);
    }

	@DomEvent("file-remove")
	public static class FileRemoveEvent extends ComponentEvent<UploadModuleWithDescription> {
		private String fileName;

		public FileRemoveEvent(UploadModuleWithDescription source, boolean fromClient, @EventData("event.detail.file.name") JreJsonString fileNameJson) {
			super(source, fromClient);
			fileName = fileNameJson.getString();
		}

		public String getFileName() {
			return fileName;
		}
	}

	@Override
	public void loadDisplay() {
		super.loadDisplay();
		vDisplay.removeAll();
		if(mapForDisplay.size()==0) {
			vDisplay.setVisible(false);
		} else {
			vDisplay.setVisible(true);
		}
		for(Entry<String, CustomPairModel<VerticalLayout,TextField>> entry : mapForDisplay.entrySet()) {
			vDisplay.add(entry.getValue().getKey());
		}
	}

	public CustomPairModel<VerticalLayout,TextField> createFileBlock(String fileName) {
		VerticalLayout vLayout = new VerticalLayout();

		String strFileName = "<div style='font-size: 13px;'><b>Tên tập tin:</b> "+fileName+"</div>";
		
		Html htmlFileName = new Html(strFileName);
		TextField txtDescription = new TextField("Miêu tả đính kèm");

		txtDescription.setWidthFull();
		txtDescription.addThemeVariants(TextFieldVariant.LUMO_SMALL);

		vLayout.add(htmlFileName);
		vLayout.add(txtDescription);
		
		vLayout.setSpacing(false);

		return new CustomPairModel<VerticalLayout, TextField>(vLayout, txtDescription);
	}

	public List<UploadModuleDataWithDescriptionModel> getListFileUpload(){
		List<UploadModuleDataWithDescriptionModel> list = new ArrayList<UploadModuleDataWithDescriptionModel>();

		for(String fileName : bufferMulti.getFiles()) {
			if (mapForDisplay.containsKey(fileName)) {
				String fileType = bufferMulti.getFileData(fileName).getMimeType();
				String description = mapForDisplay.get(fileName).getValue().getValue().trim();
				InputStream inputStream = bufferMulti.getInputStream(fileName);

				UploadModuleDataWithDescriptionModel modelFile = new UploadModuleDataWithDescriptionModel();

				modelFile.setFileName(fileName);
				modelFile.setFileType(fileType);
				modelFile.setDescription(description);
				modelFile.setInputStream(inputStream);

				list.add(modelFile);
			}
		}

		return list;
	}
}
