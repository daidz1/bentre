package vn.com.ngn.site.views.tasklist.component;

import java.io.IOException;
import java.time.LocalDateTime;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.dialog.task.TaskAttachmentDialog;
import vn.com.ngn.site.dialog.task.TaskCommentDialog;
import vn.com.ngn.site.dialog.task.TaskDetailDialog;
import vn.com.ngn.site.dialog.task.TaskEventDialog;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.enums.TaskPriorityEnum;
import vn.com.ngn.site.util.LocalDateUtil;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.UIUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;

public class TaskTreeComponent extends VerticalLayout implements LayoutInterface{
	private VerticalLayout vTree = new VerticalLayout();
	private String taskId;
	public TaskTreeComponent(String taskId) {
		this.taskId = taskId;
		
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		this.add(vTree);
	}

	@Override
	public void configComponent() {
		
	}
	
	public void loadData() {
		vTree.removeAll();
		try {
			JsonObject jsonResponse = TaskServiceUtil.getTaskTree(taskId);
			if(jsonResponse.get("status").getAsInt()==200) {
				JsonObject jsonResult = jsonResponse.getAsJsonObject("result");
				JsonArray jsonArr = new JsonArray(1);
				jsonArr.add(jsonResult);
				buildTreeBlock(vTree, jsonArr,true);
			} else {
				System.out.println(jsonResponse);
				NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại!", NotificationTypeEnum.ERROR);
			}
		} catch (IOException e) {
			NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại!", NotificationTypeEnum.ERROR);
			e.printStackTrace();
		}
	}
	
	private void buildTreeBlock(VerticalLayout vParent, JsonArray jsonSub,boolean isRoot) {
		vParent.setPadding(false);
		for(JsonElement jsonEle : jsonSub) {
			VerticalLayout vTreeBlock = new VerticalLayout();
			
			vTreeBlock.addClassName("vLayout-tree-parent");
			vTreeBlock.setPadding(false);
			vTreeBlock.setSpacing(false);
			if(!isRoot) {
				vParent.getStyle().set("margin-left", "100px");
			}
			vParent.add(vTreeBlock);
			
			JsonObject jsonTask = jsonEle.getAsJsonObject();
			
			JsonArray jsonArrSubTask = jsonTask.getAsJsonArray("subTasks");
			
			String id =jsonTask.get("id").getAsString();
//			String assignee = jsonTask.getAsJsonObject("assignee").get("fullName").getAsString();
			String assignee = jsonTask.getAsJsonObject("assignee").get("fullName").isJsonNull()?null:jsonTask.getAsJsonObject("assignee").get("fullName").getAsString();
			String assigneeOrg = jsonTask.getAsJsonObject("assignee").get("organizationName").getAsString();
			String title = jsonTask.get("title").getAsString();
			int priority = jsonTask.get("priority").getAsInt();
			int progress = jsonTask.get("processes").getAsInt();
			long endTimeL = jsonTask.get("endTime").getAsLong();
			long nowL = LocalDateUtil.localDateTimeToLong(LocalDateTime.now());
			String startTime = LocalDateUtil.formatLocalDateTime(LocalDateUtil.longToLocalDateTime(jsonTask.get("createdTime").getAsLong()),LocalDateUtil.dateTimeFormater1);
			String endTime = jsonTask.get("endTime").getAsLong()==0? "Không hạn" : LocalDateUtil.formatLocalDateTime(LocalDateUtil.longToLocalDateTime(jsonTask.get("endTime").getAsLong()),LocalDateUtil.dateTimeFormater1);
			String completeTime = jsonTask.get("completedTime").getAsLong()==0? "Đang xử lý" : LocalDateUtil.formatLocalDateTime(LocalDateUtil.longToLocalDateTime(jsonTask.get("completedTime").getAsLong()),LocalDateUtil.dateTimeFormater1);
			
			VerticalLayout vSubTreeBlock = new VerticalLayout();
			//Vertical simple
			Html htmlAssignee = new Html("<div><span class='assignee'>"+assignee+"</span> - <span class='createtime'>Ngày giao: "+startTime+" - Hạn xử lý: "+endTime+"</span></div>");
			VerticalLayout vTaskContent = new VerticalLayout();
			HorizontalLayout hTitle = new HorizontalLayout();
			Html htmlTitle = new Html("<div class='title' title='"+title+"'>"+title+"</div>");
			HorizontalLayout hSummary = new HorizontalLayout();
			Icon icoPriority = VaadinIcon.FLAG.create();
			Span spanProgress = new Span(progress+"%");
			Icon icoShowDetail = VaadinIcon.ANGLE_DOWN.create();
			ProgressBar progressBar = new ProgressBar(0, 100, progress);
			
			vTaskContent.add(hTitle);
			vTaskContent.add(progressBar);
			
			hTitle.add(htmlTitle,hSummary);
			
			hSummary.add(icoPriority,spanProgress,icoShowDetail);
			icoPriority.setSize("13px");

			spanProgress.setWidth("35px");
			spanProgress.getStyle().set("font-weight", "500");
			spanProgress.getStyle().set("color", "#208090");
			
			for(TaskPriorityEnum ePrio : TaskPriorityEnum.values()) {
				if(ePrio.getKey()==priority)
					icoPriority.getStyle().set("color", ePrio.getColor());
			}
			icoShowDetail.setSize("13px");

			hTitle.expand(htmlTitle);
			hTitle.setWidthFull();
			
			hSummary.setDefaultVerticalComponentAlignment(Alignment.CENTER);

			vTaskContent.setPadding(false);
			vTaskContent.addClassName("vTask-block");
			vTaskContent.setWidth("800px");
			
			//Vertical detail
			VerticalLayout vDetailBlock = new VerticalLayout();
			HorizontalLayout hHead = new HorizontalLayout();
			Html htmlDetailAssignee = new Html("<div><span class='assignee'>"+assignee+"</span> <span class='assignee-org'>("+assigneeOrg+")</span> - <span class='createtime'>"+startTime+"</span></div>");
			HorizontalLayout hContent = new HorizontalLayout();
			Span spanLine = new Span();
			
			VerticalLayout vTaskContentDetail = new VerticalLayout();
			HorizontalLayout hTitleDetail = new HorizontalLayout();
			Html htmlTitleDetail = new Html("<div class='title' title='"+title+"'>"+title+"</div>");
			HorizontalLayout hSummaryDetail = new HorizontalLayout();
			Icon icoHideDetail = VaadinIcon.ANGLE_UP.create();
			Html htmlProgress = new Html("<div style='color:#1676f3;'><b>Tiến độ hiện tại: </b>"+progress+"%</div>");
			ProgressBar progressBarDetail = new ProgressBar(0, 100, progress);
			TaskActionComponent taskAction = new TaskActionComponent(jsonTask, null, true);
			Html htmlDate = new Html("<div><b style='margin-left:0px'>Hạn xử lý: </b>"+endTime+" <b style='margin-left:15px'>Ngày hoàn thành: </b>"+completeTime+"</div>");
			
			Button btnCollapse = new Button(VaadinIcon.PLUS.create());
			
			//tree block
			vTreeBlock.add(vDetailBlock);
			
			vDetailBlock.add(hHead);
			vDetailBlock.add(hContent);
			
			vDetailBlock.setPadding(false);
			vDetailBlock.addClassName("block");
			
			//horizontal head
//			hHead.add(htmlDetailAssignee);
			hHead.add(htmlAssignee);
			
			
			hHead.setDefaultVerticalComponentAlignment(Alignment.CENTER);
			if(isRoot) {
				hHead.getStyle().set("margin-left", "13px");
			} else {
				hHead.getStyle().set("margin-left", "62px");
			}
//			if(endTimeL>=nowL || endTimeL==0) {
//				icoCalendar.getStyle().set("color", "green");
//			} else { 
//				icoCalendar.getStyle().set("color", "red");
//			}
			
			//horizontal layout
			hContent.add(spanLine,vTaskContent,btnCollapse);
			 
			//task content
			vTaskContentDetail.add(hTitleDetail);
			vTaskContentDetail.add(htmlProgress);
			vTaskContentDetail.add(progressBarDetail);
			vTaskContentDetail.add(taskAction);
			
			taskAction.gethAction().add(htmlDate);
			
			hTitleDetail.add(htmlTitleDetail,hSummaryDetail);
			
			hSummaryDetail.add(icoHideDetail);

			icoHideDetail.setSize("13px");

			hTitleDetail.expand(htmlTitleDetail);
			hTitleDetail.setWidthFull();
			
			hSummaryDetail.setDefaultVerticalComponentAlignment(Alignment.CENTER);
			
			//taskAction.gethDetail().setVisible(false);
			taskAction.gethProgress().setVisible(false);
			
			vTaskContentDetail.setPadding(false);
			vTaskContentDetail.addClassName("vTask-block");
			
			vTaskContentDetail.setWidth("800px");
			
			hContent.setVerticalComponentAlignment(Alignment.END, spanLine);
			
			if(!isRoot) {
				spanLine.addClassName("block-line");
			}
			
			if(jsonArrSubTask.size()>0) {
				buildTreeBlock(vSubTreeBlock, jsonArrSubTask, false);
				vParent.add(vSubTreeBlock);
				vSubTreeBlock.setVisible(false);
				vSubTreeBlock.getStyle().set("width", "unset");
				vSubTreeBlock.addClassName("vLayout-tree-parent-line");
				
				btnCollapse.addClickListener(e->{
					boolean isSubVisible = vSubTreeBlock.isVisible();
					vSubTreeBlock.setVisible(!isSubVisible);
					
					if(isSubVisible) {
						btnCollapse.setIcon(VaadinIcon.PLUS.create());
						btnCollapse.removeThemeVariants(ButtonVariant.LUMO_ERROR);
					} else {
						btnCollapse.setIcon(VaadinIcon.MINUS.create());
						btnCollapse.addThemeVariants(ButtonVariant.LUMO_ERROR);
					}
				});
			} else {
				btnCollapse.setVisible(false);
			}
			
			taskAction.gethDetail().addClickListener(e->{
				try {
					JsonObject jsonResponse = TaskServiceUtil.getTaskDetail(id);
					
					if(jsonResponse.get("status").getAsInt()==200) {
						TaskDetailDialog dialogTask = new TaskDetailDialog(jsonResponse.getAsJsonObject("result"), null, null);
						
						dialogTask.open();
						
						dialogTask.getTaskDetail().getBtnTriggerDelete().addClickListener(eDelete->{
							UIUtil.getMainView().updateCountMenu(SessionUtil.getUserId(), SessionUtil.getOrgId(),SessionUtil.getYear(),SessionUtil.getToken());

							this.setVisible(false);
						});
					} else {
						System.out.println(jsonResponse.get("message").getAsString());
						NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
					NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
				}
			});
			taskAction.gethAttachment().addClickListener(e->{
				TaskAttachmentDialog dialogAttachment = new TaskAttachmentDialog(id);
				
				dialogAttachment.open();
			});
			
			taskAction.gethComment().addClickListener(e->{
				TaskCommentDialog dialogCommnet = new TaskCommentDialog(id);
				
				dialogCommnet.open();
			});
			
			taskAction.gethEvent().addClickListener(e->{
				TaskEventDialog dialogEvent = new TaskEventDialog(id);
				
				dialogEvent.open();
			});
			
			icoShowDetail.addClickListener(e->{
				hHead.remove(htmlAssignee);
				hHead.addComponentAtIndex(0,htmlDetailAssignee);
				
				hContent.remove(vTaskContent);
				hContent.addComponentAtIndex(1,vTaskContentDetail);
			});
			
			icoHideDetail.addClickListener(e->{
				hHead.remove(htmlDetailAssignee);
				hHead.addComponentAtIndex(0,htmlAssignee);
				
				hContent.remove(vTaskContentDetail);
				hContent.addComponentAtIndex(1,vTaskContent);
			});
		}
	}
}
