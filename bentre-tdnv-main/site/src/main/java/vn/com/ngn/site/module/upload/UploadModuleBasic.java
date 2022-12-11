package vn.com.ngn.site.module.upload;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.shared.Registration;

import elemental.json.Json;
import elemental.json.impl.JreJsonString;
import vn.com.ngn.site.model.UploadModuleDataModel;

@SuppressWarnings("serial")
public class UploadModuleBasic extends UploadModule {
	private List<String> listFileName = new ArrayList<String>();
	
	@Override
	public void buildLayout() {
		super.buildLayout();
	}

	@Override
	public void configComponent() {
		super.configComponent();
		
		upload.addFinishedListener(e->{
			listFileName.add(e.getFileName());
		});
		this.addFileRemoveListener(e->{
			listFileName.remove(e.getFileName());
		});
	}
	
	Registration addFileRemoveListener(ComponentEventListener<FileRemoveEvent> listener) {
        return super.addListener(FileRemoveEvent.class, listener);
    }

	@DomEvent("file-remove")
	public static class FileRemoveEvent extends ComponentEvent<UploadModuleBasic> {
		private String fileName;

		public FileRemoveEvent(UploadModuleBasic source, boolean fromClient, @EventData("event.detail.file.name") JreJsonString fileNameJson) {
			super(source, fromClient);
			fileName = fileNameJson.getString();
		}

		public String getFileName() {
			return fileName;
		}
	}

	@Override
	public void loadDisplay() {
	}

	public List<UploadModuleDataModel> getListFileUpload(){
		List<UploadModuleDataModel> list = new ArrayList<UploadModuleDataModel>();

		for(String fileName : bufferMulti.getFiles()) {
			if (listFileName.contains(fileName)) {
				String fileType = bufferMulti.getFileData(fileName).getMimeType();
				InputStream inputStream = bufferMulti.getInputStream(fileName);

				UploadModuleDataModel modelFile = new UploadModuleDataModel();

				modelFile.setFileName(fileName);
				modelFile.setFileType(fileType);
				modelFile.setInputStream(inputStream);

				list.add(modelFile);
			}
		}

		return list;
	}
	
	public void clear() {
		listFileName.clear();
		upload.getElement().setPropertyJson("files", Json.createArray());
		bufferMulti.getFiles().clear();
	}
}
