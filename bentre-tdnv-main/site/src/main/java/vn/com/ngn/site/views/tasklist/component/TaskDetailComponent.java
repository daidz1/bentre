package vn.com.ngn.site.views.tasklist.component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.shared.Registration;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.dialog.task.SetAssigneeForOrgTaskDialog;
import vn.com.ngn.site.dialog.task.SetSupportForOrgTaskDialog;
import vn.com.ngn.site.dialog.task.TaskAssgineeDialog;
import vn.com.ngn.site.dialog.task.TaskCompleteDialog;
import vn.com.ngn.site.dialog.task.TaskCreateProgressDialog;
import vn.com.ngn.site.dialog.task.TaskForwardDialog;
import vn.com.ngn.site.dialog.task.TaskRatingDialog;
import vn.com.ngn.site.dialog.task.TaskRedoDialog;
import vn.com.ngn.site.dialog.task.TaskUpdateDialog;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.enums.PermissionEnum;
import vn.com.ngn.site.enums.TaskAssignmentTypeEnum;
import vn.com.ngn.site.enums.TaskTypeEnum;
import vn.com.ngn.site.model.CustomPairModel;
import vn.com.ngn.site.model.TaskDetailStateForUser;
import vn.com.ngn.site.model.taskcreate.TaskAssigneeUserModel;
import vn.com.ngn.site.util.BroadcasterSupportUitl;
import vn.com.ngn.site.util.BroadcasterUtil;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.HeaderUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;
import vn.com.ngn.site.views.taskcreate.SingleTaskCreateLayout;
import vn.com.ngn.site.views.taskcreate.SingleTaskCreateOrgLayout;

public class TaskDetailComponent extends VerticalLayout implements LayoutInterface{
	private static final long serialVersionUID = 2318346631267263824L;
	private HorizontalLayout hAction = new HorizontalLayout();
	private Button btnForwardTask = new Button("Giao tiếp nhiệm vụ",VaadinIcon.ARROW_FORWARD.create());
	private Button btnUpdateTask = new Button("Cập nhật nhiệm vụ",VaadinIcon.EDIT.create());
	private Button btnUpdateProgress = new Button("Cập nhật tiến độ",VaadinIcon.PROGRESSBAR.create());
	private Button btnCompleteTask = new Button("Hoàn thành nhiệm vụ",VaadinIcon.CHECK_CIRCLE.create());
	private Button btnRedoTask = new Button("Làm lại",VaadinIcon.REFRESH.create());
	private Button btnRating = new Button("Đánh giá",VaadinIcon.STAR.create());
	private Button btnDeleteTask = new Button("Xóa nhiệm vụ",VaadinIcon.TRASH.create());
	//Dzung code
	private Button btnSetAssignee = new Button("Phân cán bộ xử lý");
	private Button btnSetSupport = new Button("Phân cán bộ hỗ trợ");
	//end Dzung code

	private VerticalLayout vDisplayThings = new VerticalLayout();

	private SplitLayout splitLayout = new SplitLayout();

	private VerticalLayout vLeft = new VerticalLayout();
	private HorizontalLayout captionTaskInfo = HeaderUtil.createHeader5WithBackground(VaadinIcon.INFO.create(),"Thông tin nhiệm vụ","rgb(14 89 183)","rgb(14 89 183 / 9%)");
	private VerticalLayout vTaskInfo = new VerticalLayout();
	private HorizontalLayout captionTaskAttachment = HeaderUtil.createHeader5WithBackground(VaadinIcon.FILE_TEXT.create(),"Đính kèm nhiệm vụ","rgb(101, 95, 89)","rgb(101 95 89/ 12%)");
	private VerticalLayout vTaskAttachment = new VerticalLayout();
	private TaskAttachmentComponent taskAttachment;
	private HorizontalLayout captionTaskProgress = HeaderUtil.createHeader5WithBackground(VaadinIcon.PROGRESSBAR.create(),"Tiến độ nhiệm vụ","#208090","#2080901c");
	private VerticalLayout vTaskProgress = new VerticalLayout();
	private TaskProgressComponent taskProgress;
	private HorizontalLayout captionTaskComment = HeaderUtil.createHeader5WithBackground(VaadinIcon.CHAT.create(),"Trao đổi ý kiến","rgb(137, 154, 47)","rgb(159 179 52/ 14%)");
	private VerticalLayout vTaskComment = new VerticalLayout();
	private TaskCommentComponent taskComment;

	private VerticalLayout vRight = new VerticalLayout();
	private VerticalLayout vUserOfTask = new VerticalLayout();
	private HorizontalLayout captionUserOwner = HeaderUtil.createHeader5WithBackground(VaadinIcon.USER_HEART.create(),"Cán bộ giao nhiệm vụ","rgb(162 27 58)","rgb(162 27 58 / 9%)");
	private HorizontalLayout captionUserAssistant = HeaderUtil.createHeader5WithBackground(VaadinIcon.USER_HEART.create(),"Cán bộ giao nhiệm vụ thay","rgb(162 27 58)","rgb(162 27 58 / 9%)");
	private HorizontalLayout captionUserAssignee = HeaderUtil.createHeader5WithBackground(VaadinIcon.USER_STAR.create(),"Cán bộ xử lý","#1676f3","rgb(22 118 243 / 20%)");
	private HorizontalLayout captionUserSupport = HeaderUtil.createHeader5WithBackground(VaadinIcon.USERS.create(),"Cán bộ hỗ trợ","rgb(4 164 71)","rgb(62 184 114 / 20%)");
	private HorizontalLayout captionSubTask = HeaderUtil.createHeader5WithBackground(VaadinIcon.LIST.create(),"Nhiệm vụ con","rgb(56 55 55)","rgb(53 57 54 / 7%)");

	private VerticalLayout vSubTask = new VerticalLayout();

	private JsonObject jsonTask = new JsonObject();
	private String eType;
	private String eStatus;

	private TaskActionComponent comTaskAction;

	private boolean isChange;
	
	private String userId = SessionUtil.getUserId();
	private String orgId = SessionUtil.getOrgId();
	private String token = SessionUtil.getToken();
	
	private boolean isHasGiaoNhiemVuPerm = SessionUtil.isHasPermission(PermissionEnum.giaonhiemvu);
	
	private Button btnTriggerDelete = new Button();

	private Registration broadcasterRegistration;
	
	public TaskDetailComponent(JsonObject jsonTask,String eType, String eStatus) {
		this.jsonTask = jsonTask;
		this.eType = eType;
		this.eStatus = eStatus;

		buildLayout();
		configComponent();
		setStateOfTask();

		reDisplayThings();
	}

	@Override
	public void buildLayout() {
		
//		this.add(comTaskTag);
		this.add(hAction);
		this.add(vDisplayThings);
		this.add(splitLayout);
		this.add(btnTriggerDelete);

		btnTriggerDelete.setVisible(false);

		this.setSizeFull();

		buildAction();
		buildInfoLayout();
		System.out.println(jsonTask.get("id").getAsString());
	}

	@Override
	public void configComponent() {
		btnForwardTask.addClickListener(e->{
			TaskForwardDialog dialog = new TaskForwardDialog(jsonTask.get("id").getAsString());

			dialog.open();

			dialog.getLayoutTask().getBtnTrigger().addClickListener(eClose->{
				dialog.close();
				rebuildLeftLayout();
				rebuildRightLayout();
				setStateOfTask();
			});
		});

		btnUpdateTask.addClickListener(e->{
			String assignmentType = jsonTask.get("assignmentType").getAsString();
			JsonObject assignee = jsonTask.get("assignee").getAsJsonObject();
			String userId = assignee.get("userId").isJsonNull()?"":assignee.get("userId").getAsString();
			
			if(assignmentType.equalsIgnoreCase("organization")&& userId.isEmpty()==false) {
				assignmentType="user";
			}
			System.out.println("===assignmentType: "+assignmentType+"===");
			TaskUpdateDialog dialog = new TaskUpdateDialog(jsonTask.get("id").getAsString(),assignmentType);

			dialog.open();
			if(assignmentType.equalsIgnoreCase("organization") ) {
				((SingleTaskCreateOrgLayout)dialog.getLayoutTask()).getBtnTrigger().addClickListener(eClose->{
					dialog.close();
					rebuildLeftLayout();
					rebuildRightLayout();
					setStateOfTask();
				});
			}else {
				((SingleTaskCreateLayout)dialog.getLayoutTask()).getBtnTrigger().addClickListener(eClose->{
					dialog.close();
					rebuildLeftLayout();
					rebuildRightLayout();
					setStateOfTask();
				});
			}
			
		});

		btnUpdateProgress.addClickListener(e->{
			TaskCreateProgressDialog dialog = new TaskCreateProgressDialog(jsonTask.get("id").getAsString());
			dialog.open();
			JsonArray jsonArrProgress = jsonTask.getAsJsonArray("processes");
			if(jsonArrProgress.size()>0) {
				dialog.setPercentInit(jsonArrProgress.get(0).getAsJsonObject().get("percent").getAsInt());
			}
			dialog.getBtnTrigger().addClickListener(eTrigger->{
				rebuildLeftLayout();

				setStateOfTask();
				
				String messageBroadcast = BroadcasterSupportUitl.createMessageOnTask(jsonTask);
				messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.MAINVIEW);
				messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.TASKDETAIL);
				messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.PROGRESSDIALOG);
				
				BroadcasterUtil.broadcast(messageBroadcast);
			});
		});

		btnCompleteTask.addClickListener(e->{
			TaskDetailStateForUser state = new TaskDetailStateForUser(userId,jsonTask);

			TaskCompleteDialog dialogComplete = new TaskCompleteDialog(jsonTask.get("id").getAsString());

			if(state.getProgress()<100) {
				dialogComplete.setUpdateProgress();
			}

			dialogComplete.open();

			dialogComplete.getBtnTrigger().addClickListener(eTrigger->{
				rebuildLeftLayout();
				rebuildRightLayout();
				
				setStateOfTask();								
				reDisplayThings();
				
				String messageBroadcast = BroadcasterSupportUitl.createMessageOnTask(jsonTask);
				messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.UPDATEUI);
				messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.MAINVIEW);
				messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.TASKDETAIL);
				messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.PROGRESSDIALOG);
				
				BroadcasterUtil.broadcast(messageBroadcast);
			});
		});

		btnRedoTask.addClickListener(e->{
			TaskRedoDialog dialog = new TaskRedoDialog(jsonTask.get("id").getAsString());
			dialog.open();

			dialog.getBtnTrigger().addClickListener(eTrigger->{
				rebuildLeftLayout();
				setStateOfTask();
				reDisplayThings();
				
				String messageBroadcast = BroadcasterSupportUitl.createMessageOnTask(jsonTask);
				messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.UPDATEUI);
				messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.MAINVIEW);
				messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.TASKDETAIL);
				messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.PROGRESSDIALOG);
				
				BroadcasterUtil.broadcast(messageBroadcast);
			});
		});

		btnRating.addClickListener(e->{
			TaskRatingDialog dialog = new TaskRatingDialog(jsonTask.get("id").getAsString());
			dialog.open();

			dialog.getBtnTrigger().addClickListener(eTrigger->{
				rebuildLeftLayout();

				reDisplayThings();
				
				String messageBroadcast = BroadcasterSupportUitl.createMessageOnTask(jsonTask);
				messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.MAINVIEW);
				messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.TASKDETAIL);
				
				BroadcasterUtil.broadcast(messageBroadcast);
			});
		});

		btnDeleteTask.addClickListener(e->{
			String title = "Xóa nhiệm vụ";
			String description = "Bạn muốn xóa nhiệm vụ này?";

			ConfirmDialog confDialog = new ConfirmDialog(title, description, 
					"Xác nhận", 
					eConfirm->{
						try {
							JsonObject jsonResponse = TaskServiceUtil.deleteTask(jsonTask.get("id").getAsString());

							if(jsonResponse.get("status").getAsInt()==200) {
								NotificationUtil.showNotifi("Xóa nhiệm vụ thành công.", NotificationTypeEnum.SUCCESS);
								
								String messageBroadcast = BroadcasterSupportUitl.createMessageOnTask(jsonTask);
								messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.UPDATEUI);
								messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.MAINVIEW);
								messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.TASKDETAIL);
								
								BroadcasterUtil.broadcast(messageBroadcast);
								
								btnTriggerDelete.click();
							} else {
								System.out.println(jsonResponse);
								NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					},
					"Hủy",
					eCancel->{
						eCancel.getSource().close();
					});
			confDialog.open();
		});
		
		//Dzung code
		btnSetAssignee.addClickListener(e->{
			TaskAssigneeUserModel userModel = new TaskAssigneeUserModel();
			JsonObject jsonUser = this.jsonTask.getAsJsonObject("assignee");
			
			if(jsonUser.get("userId").isJsonNull()==false) {
				userModel.setIdUser(jsonUser.get("userId").getAsString());
				userModel.setFullName(jsonUser.get("fullName").getAsString());
				userModel.setIdOrg(jsonUser.get("organizationId").getAsString());
				userModel.setOrgName(jsonUser.get("organizationName").getAsString());
			}else {
				userModel = null;
			}
			SetAssigneeForOrgTaskDialog dialogAss = new SetAssigneeForOrgTaskDialog(jsonTask.get("id").getAsString(), userModel,new HashMap<String, TaskAssigneeUserModel>());

			dialogAss.open();

			dialogAss.addOpenedChangeListener(eClose->{
				if(!eClose.isOpened()) {
					System.out.println("dialog close");
					try {
						vUserOfTask.removeAll();
						this.jsonTask =  TaskServiceUtil.getTaskDetail(jsonTask.get("id").getAsString(),token,userId,orgId);
						this.jsonTask  = this.jsonTask.getAsJsonObject("result");
						buildUserInfo();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
		});

		btnSetSupport.addClickListener(e->{
			TaskAssigneeUserModel userModel = new TaskAssigneeUserModel();
			JsonArray jsonUsers = this.jsonTask.get("followersTask").getAsJsonArray();
			JsonObject jsonUser = jsonUsers.get(0).getAsJsonObject();
			
			if(jsonUser.get("userId").isJsonNull()==false) {
				userModel.setIdUser(jsonUser.get("userId").getAsString());
				userModel.setFullName(jsonUser.get("fullName").getAsString());
				userModel.setIdOrg(jsonUser.get("organizationId").getAsString());
				userModel.setOrgName(jsonUser.get("organizationName").getAsString());
			}else {
				userModel = null;
			}
			SetSupportForOrgTaskDialog dialogAss = new SetSupportForOrgTaskDialog(jsonTask.get("id").getAsString(), userModel);

			dialogAss.open();

			dialogAss.addOpenedChangeListener(eClose->{
				if(!eClose.isOpened()) {
					System.out.println("dialog close");
					try {
						vUserOfTask.removeAll();
						this.jsonTask =  TaskServiceUtil.getTaskDetail(jsonTask.get("id").getAsString(),token,userId,orgId);
						this.jsonTask  = this.jsonTask.getAsJsonObject("result");
						buildUserInfo();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
		});
		//end Dzung code
	}

	private void buildAction() {
		hAction.add(btnForwardTask,btnUpdateTask,btnUpdateProgress,btnCompleteTask,btnRedoTask,btnRating,btnDeleteTask,btnSetAssignee,btnSetSupport);
		hAction.getStyle().set("margin-left", "10px");


		btnDeleteTask.addThemeVariants(ButtonVariant.LUMO_ERROR);
	}

	private void buildInfoLayout() {
		splitLayout.addToPrimary(vLeft);
		splitLayout.addToSecondary(vRight);

		splitLayout.setSplitterPosition(55);
		splitLayout.setMinHeight("100%");

		splitLayout.setWidthFull();

		buildLeftLayout();
		buildRightLayout();
	}

	private void buildLeftLayout() {
		vLeft.add(captionTaskInfo);
		vLeft.add(vTaskInfo);
		vLeft.add(captionTaskProgress);
		vLeft.add(vTaskProgress);
		vLeft.add(captionTaskAttachment);
		vLeft.add(vTaskAttachment);
		vLeft.add(captionTaskComment);
		vLeft.add(vTaskComment);

		vTaskInfo.setWidthFull();
		vTaskInfo.addClassName("detail-taskinfo");

		vTaskAttachment.setWidthFull();
		vTaskAttachment.setPadding(false);
		vTaskAttachment.addClassName("detail-taskattachment");

		vTaskProgress.setWidthFull();
		vTaskProgress.setPadding(false);
		vTaskProgress.addClassName("detail-taskprogress");

		vTaskComment.setWidthFull();
		vTaskComment.addClassName("detail-taskcomment");

		vLeft.setWidthFull();

		vLeft.setMinHeight("100%");

		buildTaskInfo();
		buildTaskAttachment();
		buildTaskProgress();
		buildTaskComment();
	}

	private void buildRightLayout() {
		vRight.add(vUserOfTask);
		vRight.add(vSubTask);

		vUserOfTask.addClassName("user-display");
		
		vRight.setWidthFull();
		
		buildUserInfo();
		buildSubTaskLayout();
	}

	public void reDisplayThings() {
		vDisplayThings.removeAll();

		boolean isHasThing = false;
		if(jsonTask.has("rating") && !jsonTask.get("rating").isJsonNull()) {
			JsonObject jsonRating = jsonTask.getAsJsonObject("rating");

			int star = jsonRating.get("star").getAsInt();
			String comment = jsonRating.get("comment").getAsString();
			String creator = jsonRating.getAsJsonObject("creator").get("fullName").getAsString();
			VerticalLayout vRating = new VerticalLayout();
			HorizontalLayout hStar = new HorizontalLayout();
			Html html1 = new Html("<span><b>Đánh giá:</b> <b>"+star+"</b></span>");
			Icon iconStar = VaadinIcon.STAR.create();
			Html html2 = new Html("<span></span>");

			iconStar.setSize("13px");
			iconStar.getStyle().set("color", "#ffce44");

			hStar.setDefaultVerticalComponentAlignment(Alignment.CENTER);
			hStar.add(html1,iconStar,html2);

			Html htmlComment = new Html("<div style='margin-top: 3px;'><b>Nhận xét:</b> "+comment+" - <b>"+creator+"</b></div>");

			vRating.add(hStar);
			vRating.add(htmlComment);

			vRating.setPadding(false);
			vRating.setSpacing(false);

			vRating.getStyle().set("background", "#f3f3f3");
			vRating.getStyle().set("border-radius", "12px");
			vRating.getStyle().set("padding", "10px 20px");

			vDisplayThings.add(vRating);

			isHasThing = true;
		}

		vDisplayThings.setVisible(isHasThing);
	}

	public void rebuildLeftLayout() {
		try {
			JsonObject jsonTaskOld = jsonTask;
			JsonObject jsonResponse = TaskServiceUtil.getTaskDetail(jsonTask.get("id").getAsString(),token,userId,orgId);
			if(jsonResponse.get("status").getAsInt()==200) {
				jsonTask = jsonResponse.getAsJsonObject("result");

				vTaskInfo.removeAll();
				vTaskAttachment.removeAll();
				vTaskProgress.removeAll();
				vTaskComment.removeAll();
				buildTaskInfo();
				buildTaskAttachment();
				buildTaskProgress();
				buildTaskComment();

				if(jsonTaskOld.get("endTime").getAsLong()!=jsonTask.get("endTime").getAsLong()
						|| jsonTaskOld.get("completedTime").getAsLong()!=jsonTask.get("completedTime").getAsLong()) {
					isChange = true;
				}
			} else {
				System.out.println(jsonResponse.get("message").getAsString());
				NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
		}
	}

	public void rebuildRightLayout() {
		vUserOfTask.removeAll();
		vSubTask.removeAll();

		buildUserInfo();
		buildSubTaskLayout();
	}

	/* build left sub layout */
	@SuppressWarnings("deprecation")
	private void buildTaskInfo() {
		vTaskInfo.add(captionTaskInfo);

		TaskTitleComponent comTaskTitle = new TaskTitleComponent(jsonTask);
		vTaskInfo.add(comTaskTitle);

		TaskContentComponent comTaskContent = new TaskContentComponent(jsonTask);
		vTaskInfo.add(comTaskContent);

		TaskDateComponent comTaskDate = new TaskDateComponent(jsonTask,eType,eStatus);
		vTaskInfo.add(comTaskDate);

		comTaskAction = new TaskActionComponent(jsonTask,null,false);
		vTaskInfo.add(comTaskAction);

		comTaskAction.gethDetail().setVisible(false);
		comTaskAction.gethEvent().setVisible(false);

		comTaskAction.gethProgress().addClickListener(e->{
			captionTaskProgress.getElement().callFunction("scrollIntoView");
		});

		comTaskAction.gethAttachment().addClickListener(e->{
			captionTaskAttachment.getElement().callFunction("scrollIntoView");
		});

		comTaskAction.gethComment().addClickListener(e->{
			captionTaskComment.getElement().callFunction("scrollIntoView");
		});
	}

	private void buildTaskAttachment() {
		taskAttachment = new TaskAttachmentComponent(jsonTask.getAsJsonArray("attachments"));

		vTaskAttachment.add(captionTaskAttachment);
		vTaskAttachment.add(taskAttachment);
	}

	private void buildTaskProgress() {
		taskProgress = new TaskProgressComponent(jsonTask.getAsJsonArray("processes"));

		taskProgress.removeClassName("detail-taskprogress");

		vTaskProgress.add(captionTaskProgress);
		vTaskProgress.add(taskProgress);
	}

	private void buildTaskComment() {
		taskComment = new TaskCommentComponent(jsonTask.get("id").getAsString(),jsonTask,jsonTask.getAsJsonArray("comments"),false,token);

		vTaskComment.add(captionTaskComment);
		vTaskComment.add(taskComment);

		taskComment.getBtnTrigger().addClickListener(e->{
			rebuildLeftLayout();
		});
	}

	/* build right sub layout */
	private void buildUserInfo() {
		vUserOfTask.add(captionUserOwner);
		vUserOfTask.add(buildUserLayout(jsonTask.getAsJsonObject("owner")));
		if(jsonTask.has("assistantTask") && !jsonTask.get("assistantTask").isJsonNull()) {
			vUserOfTask.add(captionUserAssistant);
			vUserOfTask.add(buildUserLayout(jsonTask.getAsJsonObject("assistantTask")));
		}
		vUserOfTask.add(captionUserAssignee);
		vUserOfTask.add(buildUserLayout(jsonTask.getAsJsonObject("assignee")));
		vUserOfTask.add(captionUserSupport);
		JsonArray jsonArrSupport = jsonTask.getAsJsonArray("followersTask");
		if(jsonArrSupport.size()>0) {
			for(JsonElement jsonEle : jsonArrSupport) {
				vUserOfTask.add(buildUserLayout(jsonEle.getAsJsonObject()));
			}
		} else {
			Span span = new Span("Không có cán bộ hỗ trợ nào.");

			span.getStyle().set("margin-left", "11px");
			span.getStyle().set("font-style", "italic");
			span.getStyle().set("color", "7f7c7c");

			vUserOfTask.add(span);
		}
	}

	private void buildSubTaskLayout() {
		vSubTask.add(captionSubTask);
		if(jsonTask.getAsJsonArray("subTasks").size()>0) {
			SubTaskListComponent subTaskLayout = new SubTaskListComponent(jsonTask.getAsJsonArray("subTasks"));
			vSubTask.add(subTaskLayout);
		}
		else {
			Span span = new Span("Không có nhiệm vụ con nào.");

			span.getStyle().set("margin-left", "11px");
			span.getStyle().set("font-style", "italic");
			span.getStyle().set("color", "7f7c7c");

			vSubTask.add(span);
		}
	}

	private HorizontalLayout buildUserLayout(JsonObject jsonUser) {
		HorizontalLayout hUser = new  HorizontalLayout();
		String fullName="";
		try {
			 fullName = jsonUser.get("fullName").getAsString();
		} catch (Exception e) {
		
		}

		String strFullname = "<div class='info-block' title='"+fullName+"'><b>Họ tên: </b>"+fullName+"<div>";
		String strOrgname = "<div class='info-block' title='"+jsonUser.get("organizationName").getAsString()+"'><b>Đơn vị: </b>"+jsonUser.get("organizationName").getAsString()+"<div>";

		hUser.setDefaultVerticalComponentAlignment(Alignment.CENTER);

		hUser.getStyle().set("font-size", "14px");
		hUser.getStyle().set("padding", "4px");

		Html htmlFullname = new Html(strFullname);
		Html htmlOrgname = new Html(strOrgname);

		hUser.add(htmlFullname,htmlOrgname);

		hUser.setWidthFull();
		//hUser.addClassName("user-display");

		return hUser;
	}

	public void setStateOfTask() {
		btnForwardTask.setVisible(false);
		btnUpdateTask.setVisible(false);
		btnUpdateProgress.setVisible(false);
		btnCompleteTask.setVisible(false);
		btnRedoTask.setVisible(false);
		btnRating.setVisible(false);
		btnDeleteTask.setVisible(false);
		//Dzung code
		btnSetAssignee.setVisible(false);
		btnSetSupport.setVisible(false);
		//end Dzung code

		TaskDetailStateForUser state = new TaskDetailStateForUser(userId,jsonTask);
		if(state.isOwner() || state.isAssistant()) {
			if(state.isCompleted()) {
				btnRedoTask.setVisible(true);
				btnRating.setVisible(true);
			} else {
				btnUpdateTask.setVisible(true);
				btnUpdateProgress.setVisible(true);
				btnCompleteTask.setVisible(true);
				btnDeleteTask.setVisible(true);
			}
		} else if(state.isAssignee()) {
			if(state.isCompleted()) {
			} else {
				btnUpdateProgress.setVisible(true);
				btnCompleteTask.setVisible(true);

				if(isHasGiaoNhiemVuPerm) {
					btnForwardTask.setVisible(true);
				}
			}
		} else if(state.isSupport()) {

		}
		
		//Dzung code
		JsonObject assignee = jsonTask.get("assignee").getAsJsonObject();
		String userId = assignee.get("userId").isJsonNull()?"":assignee.get("userId").getAsString();
		if(SessionUtil.getParam().get("type").equals(TaskTypeEnum.DUOCGIAO.getKey())) {
//			if(userId.isEmpty()) {
			if(SessionUtil.getParam().get("assignmentType").equals(TaskAssignmentTypeEnum.ORGANIZATION.getKey())) {
				btnUpdateProgress.setVisible(false);
				btnCompleteTask.setVisible(false);
				btnSetAssignee.setVisible(true);
			}
		}
		
		JsonArray support = jsonTask.get("followersTask").getAsJsonArray();
		
		System.out.println("===701==="+SessionUtil.getParam().get("type")+" - "+eType);
		if(SessionUtil.getParam().get("type").equals(TaskTypeEnum.THEODOI.getKey())) {
			if(SessionUtil.getParam().get("assignmentType").equals(TaskAssignmentTypeEnum.ORGANIZATION.getKey())) {
				if(support.size()>0){
					btnSetSupport.setVisible(true);
				}
			}
			
		}
		
		
		//end Dzung code
	}
	
	@Override
	protected void onAttach(AttachEvent attachEvent) {
		String userId = SessionUtil.getUserId();
		String orgId = SessionUtil.getOrgId();
		int year = SessionUtil.getYear();

		UI ui = attachEvent.getUI();
		broadcasterRegistration = BroadcasterUtil.register(newMessage -> {
			ui.access(() -> {
				if(BroadcasterSupportUitl.checkHasOption(newMessage, BroadcasterSupportUitl.TASKDETAIL)) {
					List<CustomPairModel<String, String>> listUserPair = BroadcasterSupportUitl.decodeMessageWithOnlyUser(BroadcasterSupportUitl.removeAllOption(newMessage));

					for(CustomPairModel<String, String> pair : listUserPair) {
						if(pair.getKey().equals(userId) && pair.getValue().equals(orgId)) {
							rebuildLeftLayout();
							rebuildRightLayout();
							reDisplayThings();
							setStateOfTask();
							
							NotificationUtil.showNotifi("Cửa sổ chi tiết nhiệm vụ hiện tại có thay đổi và đã được làm mới.", NotificationTypeEnum.SUCCESS);
						}
					}
				}
			});
		});
	}
	
	@Override
	protected void onDetach(DetachEvent detachEvent) {
		broadcasterRegistration.remove();
		broadcasterRegistration = null;
	}

	public JsonObject getJsonTask() {
		return jsonTask;
	}
	public boolean isChange() {
		return isChange;
	}
	public Button getBtnTriggerDelete() {
		return btnTriggerDelete;
	}
	public Button getBtnForwardTask() {
		return btnForwardTask;
	}
	public void setBtnForwardTask(Button btnForwardTask) {
		this.btnForwardTask = btnForwardTask;
	}
	public Button getBtnUpdateTask() {
		return btnUpdateTask;
	}
	public void setBtnUpdateTask(Button btnUpdateTask) {
		this.btnUpdateTask = btnUpdateTask;
	}
	public Button getBtnUpdateProgress() {
		return btnUpdateProgress;
	}
	public void setBtnUpdateProgress(Button btnUpdateProgress) {
		this.btnUpdateProgress = btnUpdateProgress;
	}
	public Button getBtnCompleteTask() {
		return btnCompleteTask;
	}
	public void setBtnCompleteTask(Button btnCompleteTask) {
		this.btnCompleteTask = btnCompleteTask;
	}
	public Button getBtnRedoTask() {
		return btnRedoTask;
	}
	public void setBtnRedoTask(Button btnRedoTask) {
		this.btnRedoTask = btnRedoTask;
	}
	public Button getBtnRating() {
		return btnRating;
	}
	public void setBtnRating(Button btnRating) {
		this.btnRating = btnRating;
	}
	public Button getBtnDeleteTask() {
		return btnDeleteTask;
	}
	public void setBtnDeleteTask(Button btnDeleteTask) {
		this.btnDeleteTask = btnDeleteTask;
	}
	public void setBtnTriggerDelete(Button btnTriggerDelete) {
		this.btnTriggerDelete = btnTriggerDelete;
	}

	public Button getBtnSetAssignee() {
		return btnSetAssignee;
	}

	public void setBtnSetAssignee(Button btnSetAssignee) {
		this.btnSetAssignee = btnSetAssignee;
	}
	
}
