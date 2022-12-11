package vn.com.ngn.site.views.docimport;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.util.ReplacingInputStream;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.dialog.doc.DocDetailInfoDialog;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.model.CustomPairModel;
import vn.com.ngn.site.model.UploadModuleDataModel;
import vn.com.ngn.site.model.docimport.DocImportGridModel;
import vn.com.ngn.site.module.upload.UploadModuleBasic;
import vn.com.ngn.site.util.ClientInfoUitl;
import vn.com.ngn.site.util.GeneralUtil;
import vn.com.ngn.site.util.LocalDateUtil;
import vn.com.ngn.site.util.ReadXMLUtil;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.views.main.MainView;

@SuppressWarnings("serial")
@Route(value = "docimport", layout = MainView.class)
@PageTitle("Nhập văn bản")
public class DocImportView extends VerticalLayout implements LayoutInterface {
	private SplitLayout splitLayout = new SplitLayout();

	private VerticalLayout vLeft = new VerticalLayout();
	private UploadModuleBasic upload = new UploadModuleBasic();
	private Button btnUpload = new Button("Lấy văn bản",VaadinIcon.FILE_SEARCH.create());

	private VerticalLayout vRight = new VerticalLayout();
	private VerticalLayout vDisplay = new VerticalLayout();
	private Html htmlBlank;
	private Button btnImport = new Button("Nhập văn bản",VaadinIcon.UPLOAD.create());

	private List<CustomPairModel<Grid<DocImportGridModel>,List<DocImportGridModel>>> listGridImport = new ArrayList<CustomPairModel<Grid<DocImportGridModel>,List<DocImportGridModel>>>();
	public DocImportView() {
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.add(splitLayout);

		splitLayout.addToPrimary(vLeft);
		splitLayout.addToSecondary(vRight);

		splitLayout.setSplitterPosition(30);
		splitLayout.setSizeFull();

		this.setSizeFull();

		buildLeftLayout();
		buildRightLayout();
	}

	@Override
	public void configComponent() {
		btnUpload.addClickListener(e->{
			if(upload.getListFileCount()>0) {
				listGridImport.clear();
				loadDisplay();

				NotificationUtil.showNotifi("Lấy văn bản thành công.", NotificationTypeEnum.SUCCESS);
			} else {
				NotificationUtil.showNotifi("Vui lòng chọn ít nhất 1 tập tin .xml", NotificationTypeEnum.WARNING);
			}
		});

		btnImport.addClickListener(e->{
			UI ui = UI.getCurrent();
			String token = SessionUtil.getToken();
			String userInfo = ClientInfoUitl.getUserInfo().toString();
			ExecutorService executor = Executors.newFixedThreadPool(20);
			for(CustomPairModel<Grid<DocImportGridModel>, List<DocImportGridModel>> pair : listGridImport) {
				for(DocImportGridModel modelSeleted : pair.getKey().getSelectedItems()) {
					Runnable importDoc = new ImportDocThread(ui,pair,modelSeleted, token, userInfo);
					executor.execute(importDoc);
				}
			}

			executor.shutdown();

			// Wait until all threads are finish
			while (!executor.isTerminated()) {
				// Running ...
			}
			for(CustomPairModel<Grid<DocImportGridModel>, List<DocImportGridModel>> pair : listGridImport) {
				pair.getKey().getDataProvider().refreshAll();
				System.out.println("Finished all threads");
			}
		});
	}

	private void buildLeftLayout() {
		vLeft.add(upload);
		vLeft.add(btnUpload);

		vLeft.setHorizontalComponentAlignment(Alignment.END, btnUpload);
		vLeft.setWidthFull();

		configUpload();
	}

	private void buildRightLayout() {
		vRight.add(vDisplay);
		vRight.add(btnImport);

		htmlBlank = new Html("<div style='height: 100%; background: whitesmoke; width: calc(100% - 2px); text-align: center; line-height: 50; border: 1px dashed #cab4b4; color: #8c8c8c;'>Chọn 1 hoặc nhiều file xml để tiến hành lấy văn bản.</div>");

		vDisplay.add(htmlBlank);

		vDisplay.setPadding(false);
		vDisplay.setHeightFull();
		vDisplay.getStyle().set("overflow", "auto");

		btnImport.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		vRight.setHorizontalComponentAlignment(Alignment.END, btnImport);

		vRight.expand(vDisplay);

		vRight.setSizeFull();
	}

	private void configUpload() {
		upload.setMultiFile(true);
		upload.getListAcceptedFileType().add("text/xml");
		upload.getListAcceptedFileType().add("application/xml");
		upload.initUpload();
	}

	private void loadDisplay() {
		vDisplay.removeAll();

		for(UploadModuleDataModel modelUpload : upload.getListFileUpload()) {
			try {
				List<JsonObject> listDoc = extractXMLContent(modelUpload.getInputStream());
				vDisplay.add(buildXMLDataBlock(modelUpload.getFileName(),listDoc));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		upload.clear();
	}

	private VerticalLayout buildXMLDataBlock(String fileName, List<JsonObject> listDoc) {
		VerticalLayout vBlock = new VerticalLayout();
		Details details = new Details();

		Icon iconCaption = VaadinIcon.FILE_CODE.create();
		Html htmlCaption = new Html("<div style='font-size:var(--lumo-font-size-m);font-weight:500'>"+fileName+" ("+listDoc.size()+" văn bản)</div>");
		HorizontalLayout hCaption = new HorizontalLayout(iconCaption,htmlCaption);
		iconCaption.setSize("var(--lumo-font-size-m)");
		hCaption.setWidthFull();

		Button btnDelete = new Button(VaadinIcon.CLOSE.create());
		btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR,ButtonVariant.LUMO_SMALL,ButtonVariant.LUMO_TERTIARY);

		HorizontalLayout hHead = new HorizontalLayout(hCaption,btnDelete);

		hHead.expand(hCaption);
		hHead.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		hHead.setWidthFull();

		VerticalLayout vGrid = new VerticalLayout();

		TextField txtSearch = new TextField();
		txtSearch.setPlaceholder("Nhập từ khóa để tìm kiếm...");
		txtSearch.setWidthFull();
		Button btnSearch = new Button(VaadinIcon.SEARCH.create());
		HorizontalLayout hSearch = new HorizontalLayout(txtSearch,btnSearch);
		hSearch.expand(txtSearch);
		hSearch.setWidthFull();

		Grid<DocImportGridModel> grid = new Grid<DocImportGridModel>();
		grid.setSelectionMode(SelectionMode.MULTI);

		grid.addComponentColumn(DocImportGridModel::getComDocSummary).setHeader("Trích dẫn").setFlexGrow(6);
		grid.addComponentColumn(DocImportGridModel::getComDocSymbol).setHeader("Ký hiệu").setFlexGrow(1);
		grid.addComponentColumn(DocImportGridModel::getComDocSignal).setHeader("Số hiệu").setFlexGrow(1);
		grid.addComponentColumn(DocImportGridModel::getComDocDate).setHeader("Ngày nhập").setFlexGrow(1);
		grid.addComponentColumn(DocImportGridModel::getComAction).setHeader("Hành động").setFlexGrow(1);

		List<DocImportGridModel> listGrid = new ArrayList<DocImportGridModel>();
		for(JsonObject jsonDoc : listDoc) {
			Icon iconDocType = jsonDoc.get("docCategory").getAsString().equals("FrOfficialIn") ? VaadinIcon.ARROW_BACKWARD.create() : VaadinIcon.ARROW_FORWARD.create();
			Html htmlSummary = new Html("<div style='width:calc(100% - 30px);white-space: initial;'>"+jsonDoc.get("docSummary").getAsString()+"</div>");		
			HorizontalLayout hSummary = new HorizontalLayout(iconDocType,htmlSummary);
			iconDocType.setSize("var(--lumo-font-size-m)");
			hSummary.expand(htmlSummary);
			hSummary.setWidthFull();

			Span spanDocSymbol = new Span(jsonDoc.get("docSymbol").getAsString());
			Span spanDocSignal = new Span(jsonDoc.get("docSignal").getAsString());
			Span spanDocDate = new Span(LocalDateUtil.formatLocalDate(LocalDateUtil.longToLocalDate(jsonDoc.get("docDate").getAsLong()),LocalDateUtil.dateFormater1));

			Button btnDetail = new Button(VaadinIcon.EYE.create());
			HorizontalLayout hAction = new HorizontalLayout(btnDetail);

			DocImportGridModel modelGrid = new DocImportGridModel();
			modelGrid.setComDocSummary(hSummary);
			modelGrid.setComDocSymbol(spanDocSymbol);
			modelGrid.setComDocSignal(spanDocSignal);
			modelGrid.setComDocDate(spanDocDate);
			modelGrid.setComAction(hAction);
			modelGrid.setJsonDoc(jsonDoc);

			listGrid.add(modelGrid);

			btnDetail.addClickListener(e->{
				DocDetailInfoDialog dialog = new DocDetailInfoDialog(jsonDoc);
				dialog.open();
			});
		}
		ListDataProvider<DocImportGridModel> dataProvider = new ListDataProvider<DocImportGridModel>(listGrid);
		grid.setDataProvider(dataProvider);
		grid.asMultiSelect().select(listGrid);

		CustomPairModel<Grid<DocImportGridModel>, List<DocImportGridModel>> pair = new CustomPairModel<Grid<DocImportGridModel>, List<DocImportGridModel>>(grid, listGrid);
		listGridImport.add(pair);

		vGrid.setPadding(false);
		vGrid.add(txtSearch,grid);

		details.setSummary(hHead);
		details.setContent(vGrid);

		details.addThemeVariants(DetailsVariant.FILLED);
		details.addClassName("details");

		vBlock.add(details);

		vBlock.setPadding(false);

		btnDelete.addClickListener(e->{
			listGridImport.remove(pair);
			((VerticalLayout)vBlock.getParent().get()).remove(vBlock);
			if(vDisplay.getComponentCount()==0) {
				vDisplay.add(htmlBlank);
			}
		});

		txtSearch.addValueChangeListener(e->{
			applyFilter(e.getValue().trim(), dataProvider);
		});

		return vBlock;
	}

	private List<JsonObject> extractXMLContent(InputStream inputStream) {
		List<JsonObject> listDoc = new ArrayList<JsonObject>();

		ReadXMLUtil xmlUtil = null;
		try {
			InputStream ris = new ReplacingInputStream(inputStream, "<!DOCTYPE database SYSTEM 'xmlschemas/domino_9_0_1.dtd'>", "");

			xmlUtil = new ReadXMLUtil(ris);
			xmlUtil.getXMLByInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			NodeList listNode = xmlUtil.getDocument().getElementsByTagName("document");
			for (int i = 0; i < listNode.getLength();i++) {
				Node node = listNode.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element eleDoc = (Element) node;
					//Single info
					String docCategory = eleDoc.getAttribute("form");
					String docFrom = xmlUtil.getElementByAttribute(eleDoc, "item", "name","DocFrom").getElementsByTagName("text").item(0).getTextContent();
					String norNameBoss = xmlUtil.getElementByAttribute(eleDoc, "item", "name","NorNameBoss").getElementsByTagName("text").item(0).getTextContent();
					String strNorNameG3 = xmlUtil.getElementByAttribute(eleDoc, "item", "name","NorNameG3").getElementsByTagName("text").item(0).getTextContent();
					JsonArray jsonArrNornameG3 = new JsonArray();
					if(strNorNameG3!=null && !strNorNameG3.isEmpty()) {
						String[] arrNorName = strNorNameG3.split(",");
						for(int z = 0;z<arrNorName.length;z++) {
							jsonArrNornameG3.add(arrNorName[z].trim());
						}
					}
					String docRegCode = xmlUtil.getElementByAttribute(eleDoc, "item", "name","DocRegCode").getElementsByTagName("number").item(0).getTextContent();
					String docSecurity = xmlUtil.getElementByAttribute(eleDoc, "item", "name","DocSecurity").getElementsByTagName("text").item(0).getTextContent();
					String docNumber = xmlUtil.getElementByAttribute(eleDoc, "item", "name","DocNumber").getElementsByTagName("text").item(0).getTextContent();
					String docSymbol = xmlUtil.getElementByAttribute(eleDoc, "item", "name","DocSymbol").getElementsByTagName("text").item(0).getTextContent();
					String docSignal = xmlUtil.getElementByAttribute(eleDoc, "item", "name","DocSignal").getElementsByTagName("text").item(0).getTextContent();
					String strDocDate = xmlUtil.getElementByAttribute(eleDoc, "item", "name","DocDate").getElementsByTagName("datetime").item(0).getTextContent();
					String strDocRegDate = xmlUtil.getElementByAttribute(eleDoc, "item", "name","DocRegDate").getElementsByTagName("datetime").item(0).getTextContent();
					long docDate = 0;
					long docRegDate = 0;
					if(strDocDate.length()==8) {
						docDate = LocalDateUtil.localDateToLong(LocalDateUtil.stringToLocalDate(strDocDate, LocalDateUtil.dateFormater2));
						docRegDate = LocalDateUtil.localDateToLong(LocalDateUtil.stringToLocalDate(strDocRegDate, LocalDateUtil.dateFormater2));
					} else {
						docDate = LocalDateUtil.localDateTimeToLong(LocalDateUtil.stringToLocalDateTime(strDocDate.substring(0,strDocDate.length()-6), LocalDateUtil.dateTimeFormater2));
						docRegDate = LocalDateUtil.localDateTimeToLong(LocalDateUtil.stringToLocalDateTime(strDocRegDate.substring(0,strDocRegDate.length()-6), LocalDateUtil.dateTimeFormater2));
					}
					String docType = xmlUtil.getElementByAttribute(eleDoc, "item", "name","DocType").getElementsByTagName("text").item(0).getTextContent();
					String docSigner = xmlUtil.getElementByAttribute(eleDoc, "item", "name","DocSigner").getElementsByTagName("text").item(0).getTextContent();
					int docCopies = 0;
					int docPages = 0;

					try {
						docCopies = Integer.parseInt(xmlUtil.getElementByAttribute(eleDoc, "item", "name","DocCopies").getElementsByTagName("number").item(0).getTextContent());
						docPages = Integer.parseInt(xmlUtil.getElementByAttribute(eleDoc, "item", "name","DocPages").getElementsByTagName("number").item(0).getTextContent());
					} catch (Exception e) {
						e.printStackTrace();
					}
					String docOrgReceived = xmlUtil.getElementByAttribute(eleDoc, "item", "name","PLCQ").getElementsByTagName("text").item(0).getTextContent();
					String docOrgCreated = xmlUtil.getElementByAttribute(eleDoc, "item", "name","DocOrgCreated").getElementsByTagName("text").item(0).getTextContent();
					String docSummary = xmlUtil.getElementByAttribute(eleDoc, "item", "name","DocSummary").getElementsByTagName("text").item(0).getTextContent();

					//Get attachment
					JsonArray jsonArrAttach = new JsonArray();
					NodeList nodeListAttachment = eleDoc.getElementsByTagName("file");
					for (int j = 0; j < nodeListAttachment.getLength();j++) {
						Node nodeFile = nodeListAttachment.item(j);
						if (nodeFile.getNodeType() == Node.ELEMENT_NODE) {
							Element eleFile = (Element) nodeFile;

							String fileName = eleFile.getAttribute("name");
							String fileType = FilenameUtils.getExtension(fileName);
							String strCreateTime = ((Element)eleFile.getElementsByTagName("created").item(0)).getElementsByTagName("datetime").item(0).getTextContent();
							String strUpdateTime = ((Element)eleFile.getElementsByTagName("modified").item(0)).getElementsByTagName("datetime").item(0).getTextContent();
							long createTime = LocalDateUtil.localDateTimeToLong(LocalDateUtil.stringToLocalDateTime(strCreateTime.substring(0,strCreateTime.length()-6), LocalDateUtil.dateTimeFormater2));
							long updateTime = LocalDateUtil.localDateTimeToLong(LocalDateUtil.stringToLocalDateTime(strUpdateTime.substring(0,strUpdateTime.length()-6), LocalDateUtil.dateTimeFormater2));
							String fileBase64 = eleFile.getElementsByTagName("filedata").item(0).getTextContent();

							JsonObject jsonAttach = new JsonObject();
							jsonAttach.addProperty("fileName", fileName);
							jsonAttach.addProperty("fileType", fileType);
							jsonAttach.addProperty("createdTime", createTime);
							jsonAttach.addProperty("updatedTime", updateTime);
							jsonAttach.addProperty("fileBase64", fileBase64);

							jsonArrAttach.add(jsonAttach);
						}
					}

					JsonObject jsonDoc = new JsonObject();

					jsonDoc.addProperty("docCategory", docCategory);
					jsonDoc.addProperty("docFrom", docFrom);
					jsonDoc.addProperty("norNameBoss", norNameBoss);
					jsonDoc.add("norNameG3", jsonArrNornameG3);
					jsonDoc.addProperty("docRegCode", docRegCode);
					jsonDoc.addProperty("docSecurity", docSecurity);
					jsonDoc.addProperty("docNumber", docNumber);
					jsonDoc.addProperty("docSymbol", docSymbol);
					jsonDoc.addProperty("docSignal", docSignal);
					jsonDoc.addProperty("docDate", docDate);
					jsonDoc.addProperty("docRegDate", docRegDate);
					jsonDoc.addProperty("docType", docType);
					jsonDoc.addProperty("docSigner", docSigner);
					jsonDoc.addProperty("docCopies", docCopies);
					jsonDoc.addProperty("docPages", docPages);
					jsonDoc.addProperty("docOrgReceived", docOrgReceived);
					jsonDoc.addProperty("docOrgCreated", docOrgCreated);
					jsonDoc.addProperty("docSummary", docSummary);
					jsonDoc.add("docAttachments", jsonArrAttach);

					listDoc.add(jsonDoc);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return listDoc;
	}

	private void applyFilter(String keyword,ListDataProvider<DocImportGridModel> dataProvider) {
		dataProvider.clearFilters();
		if(!keyword.isEmpty()) {
			String searchString = GeneralUtil.getSearchString(keyword);

			dataProvider.addFilter(doc -> GeneralUtil.getSearchString(
					doc.getJsonDoc().get("docSummary").getAsString()
					+" "+doc.getJsonDoc().get("docSignal").getAsString()
					).contains(searchString));
		}
	}
}
