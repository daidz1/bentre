package vn.com.ngn.site.views.tasklist.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import vn.com.ngn.site.enums.TaskTypeEnum;

public class TaskActionComponent extends TaskInfoComponent{
	HorizontalLayout hAction = new HorizontalLayout();
	
	private HorizontalLayout hPriority = new HorizontalLayout();
	private HorizontalLayout hProgress = new HorizontalLayout();
	private HorizontalLayout hComment = new HorizontalLayout();
	private HorizontalLayout hAttachment = new HorizontalLayout();
	private HorizontalLayout hSubtask = new HorizontalLayout();
	private HorizontalLayout hEvent = new HorizontalLayout();
	private HorizontalLayout hDetail = new HorizontalLayout();
	
	private boolean isMinimal = false;
	
	private String eType;
	
	public TaskActionComponent(JsonObject jsonObject,String eType,boolean isMinimal) {
		this.jsonObject = jsonObject;
		this.isMinimal = isMinimal;
		this.eType = eType;
		
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		super.buildLayout();
		
		//priority
		int intPrio = jsonObject.get("priority").getAsInt();
		String textPrio = jsonObject.get("priorityName").getAsString();
		Icon iconPrio = VaadinIcon.FLAG_O.create();
		iconPrio.setSize("12px");
		Span spanPrio = new Span(textPrio);
		
		hPriority.add(iconPrio,spanPrio);
		hPriority.setVerticalComponentAlignment(Alignment.CENTER, iconPrio);
		hPriority.getElement().setAttribute("priority", String.valueOf(intPrio));
		hPriority.addClassName("action-priority");
		
		//progress
		int progress;

		if(jsonObject.get("processes").isJsonArray()) {
			JsonArray jsonArrProgress = jsonObject.getAsJsonArray("processes");
			
			progress = jsonArrProgress.size() > 0 ? jsonArrProgress.get(0).getAsJsonObject().get("percent").getAsInt() : 0;
		} else {
			progress = jsonObject.get("processes").getAsInt();
		}
		
		Icon iconProgress = VaadinIcon.PROGRESSBAR.create();
		iconProgress.setSize("12px");
		
		String progressText = "";
		if(isMinimal) {
			progressText = progress+"%";
		} else {
			progressText = "Tiến độ: "+progress+"%";
		}
		Span spanProgress = new Span(progressText);
		
		hProgress.add(iconProgress,spanProgress);
		hProgress.setVerticalComponentAlignment(Alignment.CENTER, iconProgress);
		hProgress.addClassName("action-progress");
		
		//comment
		int commentCount = 0;
		if(jsonObject.get("comments").isJsonArray()) {
			JsonArray jsonArrComment = jsonObject.getAsJsonArray("comments");

			for(JsonElement jsonParentEle : jsonArrComment) {
				commentCount+=1;
				commentCount+=jsonParentEle.getAsJsonObject().getAsJsonArray("replies").size();
			}
		} else {
			commentCount = jsonObject.get("comments").getAsInt();
		}
		
		Icon iconComment = VaadinIcon.CHAT.create();
		iconComment.setSize("12px");
		String commentText = "";
		if(isMinimal) {
			commentText = String.valueOf(commentCount);
		} else {
			commentText = "Bình luận: "+commentCount;
		}
		Span spanComment = new Span(commentText);
		
		hComment.add(iconComment,spanComment);
		hComment.setVerticalComponentAlignment(Alignment.CENTER, iconComment);
		hComment.addClassName("action-commnet");
		
		hAction.add(hPriority,hProgress,hComment);
		hAction.addClassName("row");
		hAction.getElement().setAttribute("role", "action");
		
		//attachment
		int attachmentCount;
		if(jsonObject.get("attachments").isJsonArray()) {
			JsonArray jsonArrAttachment = jsonObject.getAsJsonArray("attachments");
			
			attachmentCount = jsonArrAttachment.size();
		} else {
			attachmentCount = jsonObject.get("attachments").getAsInt();
		}
		
		Icon iconAttachment = VaadinIcon.PAPERCLIP.create();
		iconAttachment.setSize("12px");
		String attachText = "";
		if(isMinimal) {
			attachText = String.valueOf(attachmentCount);
		} else {
			attachText = "Đính kèm: "+attachmentCount;
		}
		Span spanAttachment = new Span(attachText);
		
		hAttachment.add(iconAttachment,spanAttachment);
		hAttachment.setVerticalComponentAlignment(Alignment.CENTER, iconAttachment);
		hAttachment.addClassName("action-attachment");
		
		//subtask
		if(eType!=null) {
			//attachment
			int subTaskCount = jsonObject.get("countSubTask").getAsInt();
			
			Icon iconSubTask = VaadinIcon.FILE_TREE.create();
			iconSubTask.setSize("12px");
			Span spanSubTask = new Span("Nhiệm vụ con: "+subTaskCount);
			
			hSubtask.add(iconSubTask,spanSubTask);
			hSubtask.setVerticalComponentAlignment(Alignment.CENTER, iconSubTask);
			hSubtask.addClassName("action-subtask");
		} else {
			hSubtask.setVisible(false);
		}
		 //event
		int eventCount;
		if(jsonObject.get("events").isJsonArray()) {
			JsonArray jsonArrEvent = jsonObject.getAsJsonArray("events");
			
			eventCount = jsonArrEvent.size();
		} else {
			eventCount = jsonObject.get("events").getAsInt();
		}
		
		Icon iconEvent = VaadinIcon.FILE_TEXT.create();
		iconEvent.setSize("12px");
		String eventText = "";
		if(isMinimal) {
			eventText = String.valueOf(eventCount);
		} else {
			eventText = "Nhật ký: "+eventCount;
		}
		Span spanEvent = new Span(eventText);
		
		hEvent.add(iconEvent,spanEvent);
		hEvent.setVerticalComponentAlignment(Alignment.CENTER, iconEvent);
		hEvent.addClassName("action-attachment");
		
		//detail
		Icon iconDetail = VaadinIcon.EYE.create();
		iconDetail.setSize("12px");
		Span spanDetail = new Span();
		if(isMinimal) {
			spanDetail.setVisible(false);
			hDetail.setHeight("25px");
		} else {
			spanDetail.setText("Xem chi tiết");
		}

		hDetail.add(iconDetail,spanDetail);
		hDetail.setVerticalComponentAlignment(Alignment.CENTER, iconDetail);
		hDetail.addClassName("action-detail");
		
		hAction.add(hPriority,hProgress,hAttachment,hComment,hSubtask,hEvent,hDetail);
		hAction.addClassName("row");
		hAction.getElement().setAttribute("role", "action");
		hAction.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		
		this.add(hAction);
	}
	public HorizontalLayout gethPriority() {
		return hPriority;
	}
	public HorizontalLayout gethProgress() {
		return hProgress;
	}
	public HorizontalLayout gethComment() {
		return hComment;
	}
	public HorizontalLayout gethAttachment() {
		return hAttachment;
	}
	public HorizontalLayout gethDetail() {
		return hDetail;
	}
	public HorizontalLayout gethSubtask() {
		return hSubtask;
	}
	public HorizontalLayout gethEvent() {
		return hEvent;
	}
	public void sethEvent(HorizontalLayout hEvent) {
		this.hEvent = hEvent;
	}
	public String geteType() {
		return eType;
	}
	public HorizontalLayout gethAction() {
		return hAction;
	}
}
