package vn.com.ngn.site.module.upload;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;

import vn.com.ngn.site.LayoutInterface;

@SuppressWarnings("serial")
@CssImport("/themes/site/components/upload.css")
public class UploadModule extends VerticalLayout implements LayoutInterface {
	protected Upload upload = new Upload();
	protected MemoryBuffer bufferSingle;
	protected MultiFileMemoryBuffer bufferMulti;
	protected UploadI18N i18nUpload = new UploadI18N();
	protected VerticalLayout vDisplay = new VerticalLayout();
	
	protected boolean isMultiFile = false;
	protected int maxFileSize = 40000000;
	protected List<String> listAcceptedFileType = new ArrayList<String>();
	
	public void initUpload() {
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		this.add(upload);
		this.add(vDisplay);

		upload.getStyle().set("box-sizing", "border-box");
		upload.setWidthFull();

		vDisplay.setWidthFull();
		vDisplay.setSpacing(false);
		vDisplay.setVisible(false);
		vDisplay.addClassName("upload-display-layout");

		if(!isMultiFile) {
			bufferSingle = new MemoryBuffer();
			upload.setReceiver(bufferSingle);
		} else {
			bufferMulti = new MultiFileMemoryBuffer();
			upload.setReceiver(bufferMulti);
		}
		upload.setAcceptedFileTypes(listAcceptedFileType.toArray(new String[listAcceptedFileType.size()]));
		upload.setMaxFileSize(maxFileSize);

		this.setWidthFull();
		this.setPadding(false);

		setLanguage();		
	}
	
	@Override
	public void configComponent() {
		upload.addAllFinishedListener(e->{
			loadDisplay();
		});
	}

	private void setLanguage() {
		i18nUpload.setDropFiles(
				new UploadI18N.DropFiles().setOne("Kéo vào một tập tin")
				.setMany("Kéo vào một hoặc nhiều tập tin"))
		.setAddFiles(new UploadI18N.AddFiles()
				.setOne("Chọn một tập tin").setMany("Chọn một hoặc nhiều tập tin"))
		.setCancel("Hủy")
		.setError(new UploadI18N.Error()
				.setTooManyFiles("Vượt số lượng tập tin cho phép.")
				.setFileIsTooBig("Tập tin quá dung lượng cho phép.")
				.setIncorrectFileType("Loại tập tin không hỗ trợ."))
		.setUploading(new UploadI18N.Uploading()
				.setStatus(new UploadI18N.Uploading.Status()
						.setConnecting("Đang kết nối...")
						.setStalled("Đang xảy ra vấn đề.")
						.setProcessing("Đang đăng tải..."))
				.setRemainingTime(
						new UploadI18N.Uploading.RemainingTime()
						.setPrefix("Còn lại: ")
						.setUnknown(
								"Không xác định."))
				.setError(new UploadI18N.Uploading.Error()
						.setServerUnavailable("Có lỗi xảy ra.")
						.setUnexpectedServerError(
								"Có lỗi xảy ra.")
						.setForbidden("Có lỗi xảy ra.")));

		upload.setI18n(i18nUpload);
	}
	
	public void loadDisplay() {
		
	}
	public int getListFileCount() {
		return bufferMulti.getFiles().size();
	}
	public MemoryBuffer getBufferSingle() {
		return bufferSingle;
	}
	public MultiFileMemoryBuffer getBufferMulti() {
		return bufferMulti;
	}
	public boolean isMultiFile() {
		return isMultiFile;
	}
	public void setMultiFile(boolean isMultiFile) {
		this.isMultiFile = isMultiFile;
	}
	public Upload getUpload() {
		return upload;
	}
	public void setMaxFileSize(int maxFileSize) {
		this.maxFileSize = maxFileSize;
	}
	public List<String> getListAcceptedFileType() {
		return listAcceptedFileType;
	}
}
