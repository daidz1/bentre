package vn.com.ngn.site.views.dashboard.component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.dialog.task.TaskDetailDialog;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.enums.TaskTypeEnum;
import vn.com.ngn.site.model.TaskFilterModel;
import vn.com.ngn.site.util.LocalDateUtil;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.UIUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;

@SuppressWarnings("serial")
public class TaskListComponent extends VerticalLayout {
	private Details detail = new Details();
	private VerticalLayout vMain = new VerticalLayout();
	
	public TaskListComponent(TaskTypeEnum eType) {
		this.add(detail);
		
		Html htmlCaption = new Html("<div style='color: #c12525;'>(*) Top 10 nhiệm vụ quá hạn và sắp đến hạn</div>");
		
		detail.setSummary(htmlCaption);
		detail.addContent(vMain);
		
		vMain.setPadding(false);
		
		detail.getStyle().set("width", "100%");
		detail.getStyle().set("padding", "0");
		
		TaskFilterModel modelFilter = new TaskFilterModel();

		modelFilter.setLimit(10);
		modelFilter.setUserid(SessionUtil.getUserId());
		modelFilter.setOrganizationId(SessionUtil.getOrgId());
		modelFilter.setCategorykey(eType.getKey());

		try {
			JsonObject jsonResponse = TaskServiceUtil.getTaskListTop(modelFilter);

			if(jsonResponse.get("status").getAsInt()==200) {
				JsonArray jsonTaskList = jsonResponse.get("result").getAsJsonArray();

				if(jsonTaskList.size()>0) {
					for(JsonElement jsonEle : jsonTaskList) {
						JsonObject jsonTask = jsonEle.getAsJsonObject();
						String taskId= jsonTask.get("id").getAsString();
						String title = jsonTask.get("title").getAsString();
						LocalDateTime ldtEndTime = LocalDateUtil.longToLocalDateTime(jsonTask.get("endTime").getAsLong());

						long days = ChronoUnit.DAYS.between(LocalDateTime.now(), ldtEndTime);
						long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), ldtEndTime);
						long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), ldtEndTime);

						boolean isExpired = minutes < 0;

						String timeValue = "";
						String backgroud = "";
						String color = "";

						if(days<0)
							days*=-1;
						if(hours<0)
							hours*=-1;
						if(minutes<0)
							minutes*=-1;
						if(days>0) {
							timeValue = days+" ngày "+(hours - days*24)+" giờ";
						} else {
							if(hours>0) {
								timeValue = hours+" giờ "+(minutes - hours*60)+" phút";
							} else {
								timeValue = minutes+" phút";
							}
						}

						if(!isExpired) {
							timeValue = "Còn "+timeValue+" đến hạn";
							backgroud = "#00800024";
							color = "green";
						} else {
							timeValue = "Đã quá hạn "+timeValue;
							backgroud = "#ff000014";
							color = "red";
						}

						HorizontalLayout hTask = new HorizontalLayout();
						Html htmlTaskTitle = new Html("<div>"
								+ "<span style='font-weight: 500; font-size:var(--lumo-font-size-m)'>"+title+"</span>"
								+ "<span style='font-size:var(--lumo-font-size-xs);display:inline-block;background: "+backgroud+"; padding: 2px 12px; color: "+color+"; font-weight: 500; margin-left: 10px; border-radius: 16px;'>"+timeValue+"</span>"
								+ "</div>");

						hTask.add(htmlTaskTitle);

						hTask.setWidthFull();
						hTask.addClassName("hLayout-task");

						hTask.addClickListener(e->{
							try {
								JsonObject jsonDetailResponse = TaskServiceUtil.getTaskDetail(taskId);

								if(jsonDetailResponse.get("status").getAsInt()==200) {
									TaskDetailDialog dialogTask = new TaskDetailDialog(jsonDetailResponse.getAsJsonObject("result"), null, null);
									dialogTask.open();

									dialogTask.addOpenedChangeListener(eClose->{
										if(!eClose.isOpened()) {
											if(dialogTask.getTaskDetail().isChange()) {
												UIUtil.getMainView().updateCountMenu(SessionUtil.getUserId(), SessionUtil.getOrgId(),SessionUtil.getYear(),SessionUtil.getToken());
											}
										}
									});
								} else {
									System.out.println(jsonResponse);
									NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
								}
							} catch (Exception e2) {
								e2.printStackTrace();
							}
						});

						vMain.add(hTask);
					}
				} else {
					Html htmlEmpty = new Html("<div style='color:#a9a1a1'>"
							+ "Không có nhiệm vụ nào."
							+ "</div>");
					
					vMain.add(htmlEmpty);
				}
			} else {
				System.out.println(jsonResponse);
				NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại", NotificationTypeEnum.ERROR);
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
