package vn.com.ngn.site.views.doclist;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.dialog.DialogTemplate;
import vn.com.ngn.site.dialog.doc.DocAttachmentDialog;
import vn.com.ngn.site.dialog.doc.DocDetailDialog;
import vn.com.ngn.site.dialog.doc.TaskListOfDocDialog;
import vn.com.ngn.site.enums.DocOfEnum;
import vn.com.ngn.site.enums.DocTypeEnum;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.form.DocCreateForm;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.DocServiceUtil;
import vn.com.ngn.site.views.doclist.component.DocActionComponent;
import vn.com.ngn.site.views.doclist.component.DocBossNameComponent;
import vn.com.ngn.site.views.doclist.component.DocDateComponent;
import vn.com.ngn.site.views.doclist.component.DocOrgCreatedInfoComponent;
import vn.com.ngn.site.views.doclist.component.DocOrgRecievedInfoComponent;
import vn.com.ngn.site.views.doclist.component.DocSignalComponent;
import vn.com.ngn.site.views.doclist.component.DocSummaryComponent;

@SuppressWarnings("serial")
public class DocBlockLayout extends VerticalLayout implements LayoutInterface{
	private JsonObject jsonDoc;

	private DocActionComponent comTaskAction;

	private String eType;
	private String eOf;

	public DocBlockLayout(JsonObject jsonTask,String eType, String eStatus) {
		this.jsonDoc = jsonTask;
		this.eType = eType;
		this.eOf = eStatus;

		buildLayout();
	}

	@Override
	public void buildLayout() {
		this.setSpacing(false);
		this.setWidthFull();
		this.addClassName("doc-block");

		rebuild();
	}

	@Override
	public void configComponent() {
		comTaskAction.gethDetail().addClickListener(e->{
			System.out.println("=====DocDetail click=====");
			try {
				JsonObject jsonResponse = DocServiceUtil.getDocDetail(jsonDoc.get("id").getAsString());

				if(jsonResponse.get("status").getAsInt()==200) {
					DocDetailDialog dialogDoc = new DocDetailDialog(jsonResponse.getAsJsonObject("result"));
					dialogDoc.setSizeFull();
					dialogDoc.open();
					//Dzung code
					dialogDoc.addOpenedChangeListener(e2->{
						if(dialogDoc.isOpened()==false) {
							try {
								JsonObject response = DocServiceUtil.getDocDetail(jsonDoc.get("id").getAsString());
								this.jsonDoc = response.getAsJsonObject("result");
								rebuild();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
						}
					});
					//end Dzung code
				} else {
					System.out.println(jsonResponse.get("message").getAsString());
					NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
			}
		});

		comTaskAction.gethTaskCount().addClickListener(e->{
			TaskListOfDocDialog dialog = new TaskListOfDocDialog(jsonDoc.get("id").getAsString());

			dialog.open();
		});

		comTaskAction.gethAttachment().addClickListener(e->{
			DocAttachmentDialog dialogAttachment = new DocAttachmentDialog(jsonDoc.get("id").getAsString());

			dialogAttachment.open();
		});
		
		//Dzung code
		if(this.jsonDoc.get("docFrom").getAsString().equals(SessionUtil.getUser().getAccountDomino())) {
			comTaskAction.gethUpdate().setVisible(true);
			comTaskAction.gethUpdate().addClickListener(e->{
				DialogTemplate dialog = new DialogTemplate();
				dialog.setWidth("80%");
				dialog.setCaption(new H4("Cập nhật văn bản"));
				dialog.buildLayout();
				dialog.configComponent();
				DocTypeEnum eType=null;;
				for(DocTypeEnum ee : DocTypeEnum.values()) {
					if(this.jsonDoc.get("docCategory").getAsString().equals(ee.getKey())) {
						eType = ee;
					}
				}
				DocCreateForm form = new DocCreateForm(jsonDoc, eType);
				form.setDialog(dialog);
				dialog.add(form);
				dialog.open();
				dialog.addOpenedChangeListener(e1->{
					if(dialog.isOpened()==false) {
						try {
							JsonObject response = DocServiceUtil.getDocDetail(jsonDoc.get("id").getAsString());
							this.jsonDoc = response.getAsJsonObject("result");
							rebuild();
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
					}
				});
			});
		}else {
			comTaskAction.gethUpdate().setVisible(false);
		}
		
		comTaskAction.gethCreateTask().addClickListener(e->{
			System.out.println("click click");
		});
		//end Dzung code
	}

	private void rebuild() {
		this.removeAll();

//		HorizontalLayout hOrg = new HorizontalLayout();
//		hOrg.setWidthFull();
//		hOrg.setDefaultVerticalComponentAlignment(Alignment.CENTER);
//
//		this.add(hOrg);
//		if(eOf == DocOfEnum.ALL.toString()) {
//			String strOrgCreated = jsonDoc.get("docOrgCreated").getAsString();
//			String strOrgRecieved = jsonDoc.get("docOrgReceived").getAsString();
//
//			DocOrgCreatedInfoComponent comOrgCreated = new DocOrgCreatedInfoComponent(strOrgCreated);
//			DocOrgRecievedInfoComponent comOrgRecieved = new DocOrgRecievedInfoComponent(strOrgRecieved);
//
//			comOrgCreated.getStyle().set("width", "unset");
//			comOrgRecieved.getStyle().set("width", "unset");
//
//			hOrg.add(comOrgCreated,comOrgRecieved);
//		} else if(eType == DocTypeEnum.VANBANDI.getKey()) {
//			String strOrgRecieved = jsonDoc.get("docOrgReceived").getAsString();
//
//			DocOrgRecievedInfoComponent comOrgRecieved = new DocOrgRecievedInfoComponent(strOrgRecieved);
//			hOrg.add(comOrgRecieved);
//		} else if(eType == DocTypeEnum.VANBANDEN.getKey()) {
//			String strOrgCreated = jsonDoc.get("docOrgCreated").getAsString();
//
//			DocOrgCreatedInfoComponent comOrgCreated = new DocOrgCreatedInfoComponent(strOrgCreated);
//			hOrg.add(comOrgCreated);
//		}
//
//		//so ky hieu
//		String strDocSignal = jsonDoc.get("docSignal").getAsString();
//		DocSignalComponent comDocSignal = new DocSignalComponent(strDocSignal);
////		this.add(comDocSignal);
//		hOrg.add(comDocSignal);
//
//		//trich dan
//		String strDocSummary = jsonDoc.get("docSummary").getAsString();
//		DocSummaryComponent comDocSummary = new DocSummaryComponent(strDocSummary);
//		this.add(comDocSummary);
//
//		//ngay nhap
//		DocDateComponent comTaskDate = new DocDateComponent(jsonDoc);
////		this.add(comTaskDate);
//		hOrg.add(comTaskDate);
//
//		//chu tri
//		String strDocBossName = jsonDoc.get("creatorName").getAsString();
//		DocBossNameComponent comDocBossName = new DocBossNameComponent(strDocBossName);
//		this.add(comDocBossName);

//		comTaskAction = new DocActionComponent(jsonDoc);
//		
//		this.add(comTaskAction);
//		configComponent();
		
		
		
		
		//trich dan
		String strDocSummary = jsonDoc.get("docSummary").getAsString();
		Html comDocSumary = new Html("<div><strong>Trích yếu: "+strDocSummary+"</strong></div>");
		this.add(comDocSumary);
		
		FlexLayout hlayout_1 = new FlexLayout();
		
		//ky hieu
		String strDocSymbol = jsonDoc.get("docSymbol").getAsString();
		Html comDocSymbol = new Html("<div style='margin-right:15px'><strong>Ký hiệu:</strong> "+strDocSummary+"</div>");
		hlayout_1.add(comDocSymbol);
		//so hieu
		String strDocNumber = jsonDoc.get("docNumber").getAsString();
		Html comDocNumber = new Html("<div style='margin-right:15px'><strong>Số hiệu:</strong> "+strDocNumber+"</div>");
		hlayout_1.add(comDocNumber);
		
		//ngay ky
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String strDocRegDate= df.format(new Date(jsonDoc.get("docRegDate").getAsLong()));
		Html comDocRegDate = new Html("<div style='margin-right:15px'><strong>Ngày ký:</strong> "+strDocRegDate+"</div>");
		hlayout_1.add(comDocRegDate);
		
		//nguoi ky
		String strDocSigner = jsonDoc.get("docSigner").getAsString();
		Html comDocSigner = new Html("<div style='margin-right:15px'><strong>Người ký:</strong> "+strDocSigner+"</div>");
		hlayout_1.add(comDocSigner);
		//co quan ban hanh
		String strOrgCreated = jsonDoc.get("docOrgCreated").getAsString();
		Html comOrgCreated = new Html("<div><strong>Cơ quan ban hành:</strong> "+strOrgCreated+"</div>");
		hlayout_1.add(comOrgCreated);
		
		
		hlayout_1.setFlexWrap(FlexWrap.WRAP);
		this.add(hlayout_1);

		
		comTaskAction = new DocActionComponent(jsonDoc);
		this.add(comTaskAction);
		configComponent();

		
	}
}
