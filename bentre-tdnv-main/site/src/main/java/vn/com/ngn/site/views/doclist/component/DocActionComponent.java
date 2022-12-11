package vn.com.ngn.site.views.doclist.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.model.style.ButtonTheme;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.Lumo;

@SuppressWarnings("serial")
public class DocActionComponent extends DocInfoComponent{
	private FlexLayout hAction = new FlexLayout();
	private FlexLayout hAction_1 = new FlexLayout();
	
	private HorizontalLayout hTaskCount = new HorizontalLayout();
	private HorizontalLayout hAttachment = new HorizontalLayout();
	private HorizontalLayout hDetail = new HorizontalLayout();
	private HorizontalLayout hUpdate = new HorizontalLayout();
//	private HorizontalLayout hCreateTask = new HorizontalLayout();
	private Button hCreateTask = new Button("Giao nhiệm vụ cho cá nhân", new Icon(VaadinIcon.PLUS_CIRCLE_O));
	private Button hCreateOrgTask = new Button("Giao nhiệm vụ cho cơ quan/đơn vị ", new Icon(VaadinIcon.PLUS_CIRCLE_O));
	private String eType;
	
	public DocActionComponent(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
		
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		
		//comment
		int attachmentCount = 0;
		if(jsonObject.get("docAttachments").isJsonArray()) {
			JsonArray jsonArrComment = jsonObject.getAsJsonArray("docAttachments");

			attachmentCount = jsonArrComment.size();
		} else {
			attachmentCount = jsonObject.get("docAttachments").getAsInt();
		}
		Icon iconAttachment = VaadinIcon.FILE_TEXT.create();
		iconAttachment.setSize("12px");
		Span spanAttachment = new Span("Đính kèm: "+attachmentCount);
		
		hAttachment.add(iconAttachment,spanAttachment);
		hAttachment.setVerticalComponentAlignment(Alignment.CENTER, iconAttachment);
		hAttachment.addClassName("action-attachment");
		
		//comment
		int taskCount = jsonObject.get("countTask").getAsInt();
		Icon iconTaskCount = VaadinIcon.FILE_TEXT.create();
		iconTaskCount.setSize("12px");
		Span spanTask = new Span("Nhiệm vụ: "+taskCount);
		
		hTaskCount.add(iconTaskCount,spanTask);
		hTaskCount.setVerticalComponentAlignment(Alignment.CENTER, iconTaskCount);
		hTaskCount.addClassName("action-attachment");
		
		//detail
		Icon iconDetail = VaadinIcon.EYE.create();
		iconDetail.setSize("12px");
		Span spanDetail = new Span("Xem chi tiết");

		hDetail.add(iconDetail,spanDetail);
		hDetail.setVerticalComponentAlignment(Alignment.CENTER, iconDetail);
		hDetail.addClassName("action-detail");
		
		//update
		Icon iconUpdate = VaadinIcon.EDIT.create();
		iconUpdate.setSize("12px");
		Span spanUpdate = new Span("Cập nhật");

		hUpdate.add(iconUpdate,spanUpdate);
		hUpdate.setVerticalComponentAlignment(Alignment.CENTER, iconDetail);
		hUpdate.addClassName("action-detail");
		
		//create task
//		hCreateTask.addClassName("square-bordered");
		hCreateTask.getStyle().set("border-radius" ,"1em");
		hCreateTask.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		hCreateTask.addThemeVariants(ButtonVariant.LUMO_SMALL);
		
		//create task
//		hCreateOrgTask.addClassName("square-bordered");
		hCreateOrgTask.getStyle().set("border-radius" ,"1em");
		hCreateOrgTask.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		hCreateOrgTask.addThemeVariants(ButtonVariant.LUMO_SMALL);
		
		hAction.setFlexWrap(FlexWrap.WRAP);
		hAction.add(hTaskCount,hAttachment,hDetail,hUpdate,hCreateTask,hCreateOrgTask);
		
		this.add(hAction);
	}
	
	public HorizontalLayout gethTaskCount() {
		return hTaskCount;
	}
	public HorizontalLayout gethAttachment() {
		return hAttachment;
	}
	public String geteType() {
		return eType;
	}
	public HorizontalLayout gethDetail() {
		return hDetail;
	}
	public void sethDetail(HorizontalLayout hDetail) {
		this.hDetail = hDetail;
	}

	public HorizontalLayout gethUpdate() {
		return hUpdate;
	}

	public void sethUpdate(HorizontalLayout hUpdate) {
		this.hUpdate = hUpdate;
	}

	public Button gethCreateTask() {
		return hCreateTask;
	}

	public void sethCreateTask(Button hCreateTask) {
		this.hCreateTask = hCreateTask;
	}
	
	
	
}
