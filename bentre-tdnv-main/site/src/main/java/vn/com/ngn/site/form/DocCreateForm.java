package vn.com.ngn.site.form;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.FileHandler;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.dom.Element;

import vn.com.ngn.site.enums.DocTypeEnum;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.model.UploadModuleDataModel;
import vn.com.ngn.site.model.doccreate.DocModel;
import vn.com.ngn.site.module.upload.UploadModuleBasic;
import vn.com.ngn.site.module.upload.UploadModuleWithDescription;
import vn.com.ngn.site.util.DocEnumUtil;
import vn.com.ngn.site.util.GeneralUtil;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.ChipComponent;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.DocServiceUtil;
import vn.com.ngn.site.views.doclist.component.DocAttachmentComponent;

@SuppressWarnings("serial")
public class DocCreateForm extends FormLayout {
	//	private DocModel model = new DocModel();
	private JsonObject jsonDoc;
	private JsonArray jsonDeleteDocAttachments = new JsonArray();
	private boolean isInsert = true;

	private ComboBox<LoaiVanBan> docCategory = new ComboBox<>("Loại văn bản");
	private ComboBox<Entry<Integer, String>> docSecurity = new ComboBox<>("Độ mật");
	private TextField docNumber = new TextField("Số hiệu");
	private TextField docSymbol = new TextField("Ký hiệu");
	private DatePicker docRegDate = new DatePicker("Ngày ban hành");
	private TextField docType = new TextField("Thể loại");
	private TextField docSigner = new TextField("Người ký");
	private TextField docOrgReceived = new TextField("Nơi nhận");
	private TextField docOrgCreated = new TextField("Nơi ban hành");
	private TextArea docSummary = new TextArea("Trích yếu");
	private UploadModuleBasic upload = new UploadModuleBasic();

	private VerticalLayout attachLayout = new VerticalLayout();

	private Button save = new Button("Lưu");
	private String docId = "";

	private DocTypeEnum eType;

	private Dialog dialog=null;
	
	ShortcutRegistration registration;

	public DocCreateForm(JsonObject jsonDoc, DocTypeEnum eType) {

		System.out.println("=====DocCreateForm=====");
		this.eType =eType;
		if(jsonDoc==null) {
			this.jsonDoc = new JsonObject();
			this.isInsert = true;
		}else {
			this.jsonDoc = jsonDoc;
			this.docId = jsonDoc.get("id").getAsString();
			this.isInsert = false;
		}

		buildLayout();
		configComponent();
	}

	private void buildLayout() {
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		HorizontalLayout buttons = new HorizontalLayout(save,upload);
		buttons.setWidth("100%");
		buttons.setJustifyContentMode(JustifyContentMode.END);

//		this.add(docCategory,docSecurity,docNumber,docSymbol,docRegDate,docType,docSigner,docSummary,docOrgCreated,docOrgReceived);
		this.add(docCategory,docSecurity,docNumber,docSymbol,docRegDate,docType);
		this.add(docSigner,2);
		this.add(docSummary,2);
		this.add(docOrgCreated,docOrgReceived);
		this.add(attachLayout,2);
		this.add(upload,2);
		this.add(new VerticalLayout(),2);
		this.add(buttons,2);
		configUpload();

	}

	private void configComponent() {
		initDocCategory();
		initDocSecurity();
		updateControls();
		initDocAttachment();

		save.addClickListener(e->{
			doSave();
		});
		
		registration = save.addClickShortcut(Key.ENTER);

		Date date = new Date() ;
		docRegDate.setValue(GeneralUtil.convertToLocalDateViaInstant(date));

		docCategory.setRequiredIndicatorVisible(true);
		docSecurity.setRequiredIndicatorVisible(true);
		docNumber.setRequiredIndicatorVisible(true);
		docSymbol.setRequiredIndicatorVisible(true);
		docRegDate.setRequiredIndicatorVisible(true);
		docType.setRequiredIndicatorVisible(true);
		docSigner.setRequiredIndicatorVisible(true);
		//		docOrgReceived.setRequiredIndicatorVisible(true);
		docOrgCreated.setRequiredIndicatorVisible(true);
		
		docSummary.setMinHeight("75px");
		docSummary.setRequiredIndicatorVisible(true);
	
		docSummary.addFocusListener(e->{
			registration.remove();
		});
		docSummary.addBlurListener(e->{
			save.addClickShortcut(Key.ENTER);
		});
	}
	private void configUpload() {
		upload.setMultiFile(true);
		upload.initUpload();
	}
	private void initDocCategory(){
		List<LoaiVanBan> lst = new ArrayList<>();
		lst.add(new LoaiVanBan("FrOfficialIn","Văn bản đến"));
		lst.add(new LoaiVanBan("FrOfficialOut","Văn bản đi"));
		docCategory.setItemLabelGenerator(e->{
			return e.getTenLoai();
		});
		docCategory.setItems(lst);
		docCategory.setAllowCustomValue(false);
		docCategory.setEnabled(false);
		ListDataProvider<LoaiVanBan> dataProvider = (ListDataProvider<LoaiVanBan>) docCategory.getDataProvider();
		dataProvider.getItems().forEach(e->{
			if(e.getMaLoai()==this.eType.getKey()) {
				docCategory.setValue(e);
			}
		});
	}

	private void initDocSecurity(){
		List<Entry<Integer, String>> lst = new ArrayList<>();
		lst.add(Map.entry(0, "Thường"));
		lst.add(Map.entry(1, "Mật"));
		lst.add(Map.entry(2, "Tối mật"));
		lst.add(Map.entry(3, "Tuyệt mật"));
		docSecurity.setItems(lst);
		docSecurity.setItemLabelGenerator(e->{
			return e.getValue();
		});
		docSecurity.setAllowCustomValue(false);

		docSecurity.setValue(lst.get(0));

	}

	private void initDocAttachment() {
		try {
			JsonObject jsonResponseGet = DocServiceUtil.getAttachmentList(docId);
			if(jsonResponseGet.get("status").getAsInt()==200) {
				JsonArray jsonArray = jsonResponseGet.get("result").getAsJsonArray();
				if(jsonArray.size()==0)return;
				attachLayout.removeAll();
				attachLayout.setMargin(true);
				attachLayout.setSpacing(false);
				Span title = new Span("File đính kèm");
				title.getStyle().set("padding-left", " var(--lumo-space-s)");
				attachLayout.add(title);

				HorizontalLayout layout = new HorizontalLayout();
				if(jsonArray.size()>0) {
					for(int i = 0; i < jsonArray.size(); i++) {
						JsonObject obj = jsonArray.get(i).getAsJsonObject();
						Icon icon = new Icon(VaadinIcon.PAPERCLIP);
						icon.setSize("12px");
						ChipComponent<String> chip = new ChipComponent<String>(icon, obj.get("fileName").getAsString());
						layout.add(chip);
						chip.addBtnDeleteClickListener(ev->{
							layout.remove(chip);
							this.jsonDeleteDocAttachments.add(obj.get("id").getAsString());
							if(layout.getComponentCount()==0) {
								this.remove(attachLayout);
							}
						});

					}
					attachLayout.add(layout);
				} else {
					System.out.println(jsonResponseGet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

	}

	private void updateModel() {
		jsonDoc = new JsonObject();
		jsonDoc.addProperty("id", this.docId);
		jsonDoc.addProperty("docCategory", docCategory.getValue().getMaLoai());
		jsonDoc.addProperty("docSecurity", docSecurity.getValue().getKey());
		jsonDoc.addProperty("docNumber", Integer.valueOf(docNumber.getValue()));
		jsonDoc.addProperty("docSymbol",docSymbol.getValue());

		java.util.Date d;
		try {
			d = new SimpleDateFormat("yyyy-MM-dd").parse(docRegDate.getValue().toString());
			jsonDoc.addProperty("docRegDate",d.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		jsonDoc.addProperty("docType",docType.getValue());
		jsonDoc.addProperty("docSigner",docSigner.getValue());
		jsonDoc.addProperty("docSummary",docSummary.getValue());
		jsonDoc.addProperty("docOrgCreated",docOrgCreated.getValue());
		jsonDoc.addProperty("docOrgReceived",docOrgReceived.getValue());
		System.out.println("=====after update model=====");
		System.out.println(jsonDoc);
	}


	private void updateControls() {
		System.out.println(jsonDoc);
//		if(jsonDoc.has("id")==false) {
//			System.out.println("null");
//			return;
//		}
		//so hieu
		if(jsonDoc.get("docNumber")!=null) {
			docNumber.setValue(jsonDoc.get("docNumber").getAsString());
		}else {
			docNumber.setValue("");
		}
		if(docNumber.getValue().equals("0")) {
			docNumber.setValue("");
		}
		//ki hieu
		if(jsonDoc.get("docSymbol")!=null) {
			docSymbol.setValue(jsonDoc.get("docSymbol").getAsString());
		}else {
			docSymbol.setValue("");
		}

		//ngay ban hanh
		Date date = new Date();
		if(jsonDoc.get("docRegDate")!=null) {
			date = new Date(jsonDoc.get("docRegDate").getAsLong()) ;
		}else {
			date = new Date();
		}
		docRegDate.setValue(GeneralUtil.convertToLocalDateViaInstant(date));

		//do mat
		ListDataProvider<Entry<Integer,String>> dataProvider_1 = (ListDataProvider<Entry<Integer,String>>) docSecurity.getDataProvider();
		if(jsonDoc.get("docSecurity")!=null) {
			dataProvider_1.getItems().forEach(e->{
				if(e.getKey()==jsonDoc.get("docSecurity").getAsInt()) {
					docSecurity.setValue(e);
				}
			});
		}else {
			docSecurity.setValue((Entry<Integer, String>) dataProvider_1.getItems().toArray()[0]);
		}

		//the loai
		if(jsonDoc.get("docType")!=null) {
			docType.setValue(jsonDoc.get("docType").getAsString());
		}else {
			docType.setValue("");
		}
		//nguoi ky
		if(jsonDoc.get("docSigner")!=null) {
			docSigner.setValue(jsonDoc.get("docSigner").getAsString());
		}else {
			docSigner.setValue("");
		}
		//trich yeu
		if(jsonDoc.get("docSummary")!=null) {
			docSummary.setValue(jsonDoc.get("docSummary").getAsString());
		}else {
			docSummary.setValue("");
		}
		//noi nhan
		if(jsonDoc.get("docOrgReceived")!=null) {
			docOrgReceived.setValue(jsonDoc.get("docOrgReceived").getAsString());
		}else {
			docOrgReceived.setValue("");
		}
		//don vi ban hanh
		if(jsonDoc.get("docOrgReceived")!=null) {
			docOrgCreated.setValue(jsonDoc.get("docOrgCreated").getAsString());
		}else {
			docOrgCreated.setValue("");
		}

	}

	private void doSave() {
		System.out.println("=====dosave=====");
		if(validate()==false) return;
		updateModel();
		if(isInsert) {
			try {
				JsonObject result= DocServiceUtil.newDocument(jsonDoc);
				System.out.println(result);
				if(result.get("status").getAsInt()==201) {
					NotificationUtil.showNotifi("Lưu văn bản thành công",NotificationTypeEnum.SUCCESS);
					this.docId  =result.get("result").getAsJsonObject().get("id").getAsString();
					doUploadFile(docId);
					System.out.println(docId);
//					jsonDoc = new JsonObject();
//					updateControls();
					this.dialog.close();

				}else {
					NotificationUtil.showNotifi(result.get("message").getAsString(), NotificationTypeEnum.ERROR);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			try {
				jsonDoc.add("deleteAttachments", this.jsonDeleteDocAttachments);
				System.out.println(jsonDoc);
				JsonObject result= DocServiceUtil.editDocument(jsonDoc);
				System.out.println(result);
				if(result.get("status").getAsInt()==200) {
					NotificationUtil.showNotifi("Cập nhật văn bản thành công",NotificationTypeEnum.SUCCESS);
					this.docId  =result.get("result").getAsJsonObject().get("id").getAsString();
					doUploadFile(docId);
					System.out.println(docId);
					this.dialog.close();

				}else {
					NotificationUtil.showNotifi(result.get("message").getAsString(), NotificationTypeEnum.ERROR);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void doUploadFile(String docId) {
		List<UploadModuleDataModel> lst = this.upload.getListFileUpload();
		for(UploadModuleDataModel model:lst) {
			JsonObject object = new JsonObject();
			object.addProperty("fileName", model.getFileName());
			object.addProperty("fileType", model.getFileType());
			File file = new File(model.getFileName());
			try {
				FileUtils.copyInputStreamToFile(model.getInputStream(), file);
				JsonObject result = DocServiceUtil.uploadAttachment(docId, object, file);
				FileUtils.forceDelete(file);
				System.out.println(result);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println(model.getFileName());
		}
	}

	private boolean validate() {
		try {
			Integer.valueOf(docNumber.getValue());
		}catch(Exception e) {
			docNumber.setErrorMessage("Không được để trống");
			docNumber.setInvalid(true);
			docNumber.focus();
			return false;
		}

		if(docSecurity.getValue()==null) {
			docSecurity.setErrorMessage("Không được để trống");
			docSecurity.setInvalid(true);
			docSecurity.focus();
			return false;
		}

		if(docSymbol.getValue().isEmpty()) {
			docSymbol.setErrorMessage("Không được để trống");
			docSymbol.setInvalid(true);
			docSymbol.focus();
			return false;
		}

		if(docRegDate.getValue()==null) {
			docRegDate.setErrorMessage("Không được để trống");
			docRegDate.setInvalid(true);
			docRegDate.focus();
			return false;
		}


		if(docType.getValue().isEmpty()) {
			docType.setErrorMessage("Không được để trống");
			docType.setInvalid(true);
			docType.focus();
			return false;
		}
		if(docSigner.getValue().isEmpty()) {
			docSigner.setErrorMessage("Không được để trống");
			docSigner.setInvalid(true);
			docSigner.focus();
			return false;
		}
		if(docSummary.getValue().isEmpty()) {
			docSummary.setErrorMessage("Không được để trống");
			docSummary.setInvalid(true);
			docSummary.focus();
			return false;
		}
		if(docOrgCreated.getValue().isEmpty()) {
			docOrgCreated.setErrorMessage("Không được để trống");
			docOrgCreated.setInvalid(true);
			docOrgCreated.focus();
			return false;
		}

		docNumber.setErrorMessage(null);
		docNumber.focus();
		docSecurity.setErrorMessage(null);
		docSymbol.setErrorMessage(null);
		docOrgCreated.setErrorMessage(null);
		docSummary.setErrorMessage(null);
		docSigner.setErrorMessage(null);
		docType.setErrorMessage(null);
		docRegDate.setErrorMessage(null);
		return true;
	}

	public void setDialog(Dialog dialog) {
		this.dialog = dialog;
	}

}

class LoaiVanBan{
	public LoaiVanBan(String maLoai, String tenLoai) {
		this.maLoai = maLoai;
		this.tenLoai = tenLoai;
	}
	String tenLoai;
	String maLoai;
	public String getTenLoai() {
		return tenLoai;
	}
	public void setTenLoai(String tenLoai) {
		this.tenLoai = tenLoai;
	}
	public String getMaLoai() {
		return maLoai;
	}
	public void setMaLoai(String maLoai) {
		this.maLoai = maLoai;
	}
}
