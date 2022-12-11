package vn.com.ngn.site.views.taskcreate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.dialog.task.ChooseUserGroupDialog;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.enums.TaskAssignmentStatusEnum;
import vn.com.ngn.site.enums.TaskAssignmentTypeEnum;
import vn.com.ngn.site.enums.TaskStatusEnum;
import vn.com.ngn.site.enums.TaskTypeEnum;
import vn.com.ngn.site.model.CustomPairModel;
import vn.com.ngn.site.model.taskcreate.TaskAssigneeUserModel;
import vn.com.ngn.site.model.taskcreate.TaskInfoCreateModel;
import vn.com.ngn.site.util.BroadcasterSupportUitl;
import vn.com.ngn.site.util.BroadcasterUtil;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.UIUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;
import vn.com.ngn.site.views.tasklist.TaskListView;

@SuppressWarnings("serial")
public class SingleTaskCreateLayout extends VerticalLayout implements LayoutInterface {
	private SplitLayout splitLayout = new SplitLayout();
	
	private VerticalLayout vLeft = new VerticalLayout();
	private TaskInfoLayout taskInfoLayout;
	
	private VerticalLayout vRight = new VerticalLayout();
	private Button btnChooseUserTemplate = new Button("Chọn nhóm cán bộ",VaadinIcon.AREA_SELECT.create());
	private TaskOwnerLayout taskOwnerLayout;
	private TaskAssgineeLayout taskAssgineeLayout;
	private TaskSupportLayout taskSupportLayout;
	
	private HorizontalLayout hAction = new HorizontalLayout();
	private Button btnSaveTask = new  Button("Giao nhiệm vụ",VaadinIcon.ARROW_FORWARD.create());
	
	private String taskId;
	private String parentId;
	private JsonObject jsonTask;
	
	private Button btnTrigger = new Button();
	
    public SingleTaskCreateLayout(String taskId,String parentId) {
    	this.taskId = taskId;
    	this.parentId = parentId;
    	
    	if(taskId!=null) {
    		try {
    			btnChooseUserTemplate.setVisible(false);
    			
				JsonObject jsonResponse = TaskServiceUtil.getTaskDetail(taskId);
				
				if(jsonResponse.get("status").getAsInt()==200) {
					jsonTask = jsonResponse.getAsJsonObject("result");
					
					taskInfoLayout = new TaskInfoLayout(jsonTask);
					taskAssgineeLayout = new TaskAssgineeLayout(jsonTask.getAsJsonObject("assignee"));
					taskSupportLayout = new TaskSupportLayout(jsonTask.getAsJsonArray("followersTask"));
				} else {
					System.out.println(jsonResponse.get("message").getAsString());
					NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
			}
    		
    		btnSaveTask.setText("Cập nhật nhiệm vụ");
    	} else if(parentId!=null){
    		try {
				JsonObject jsonResponse = TaskServiceUtil.getTaskDetail(parentId);
				
				if(jsonResponse.get("status").getAsInt()==200) {
					jsonTask = jsonResponse.getAsJsonObject("result");
					taskInfoLayout = new TaskInfoLayout(jsonTask);
					if(SessionUtil.getOrg().getLeadersTask().size()>0)
		    			taskOwnerLayout = new TaskOwnerLayout(null);
					taskAssgineeLayout = new TaskAssgineeLayout(null);
					taskSupportLayout = new TaskSupportLayout(null);
					
					taskAssgineeLayout.gethWrapCaption().add(btnChooseUserTemplate);
				} else {
					System.out.println(jsonResponse.get("message").getAsString());
					NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
			}
    		
    		btnSaveTask.setText("Giao tiếp nhiệm vụ");
    	} else {
    		if(SessionUtil.getOrg().getLeadersTask().size()>0)
    			taskOwnerLayout = new TaskOwnerLayout(null);
    		taskInfoLayout = new TaskInfoLayout(null);
			taskAssgineeLayout = new TaskAssgineeLayout(null);
			taskSupportLayout = new TaskSupportLayout(null);
			
			taskAssgineeLayout.gethWrapCaption().add(btnChooseUserTemplate);
    	}
    	
    	buildLayout();
    	configComponent();
    }

	@Override
	public void buildLayout() {
		this.add(splitLayout);
		this.add(btnTrigger);
		
		btnTrigger.setVisible(false);
		
		splitLayout.addToPrimary(vLeft);
		splitLayout.addToSecondary(vRight);
		
		splitLayout.setSplitterPosition(55);
		splitLayout.setSizeFull();
		
		this.setSizeFull();
		this.setPadding(false);
		
		buildLeftLayout();
		buildRightLayout();
	}

	@Override
	public void configComponent() {
		btnChooseUserTemplate.addClickListener(e->{
			ChooseUserGroupDialog dialog = new ChooseUserGroupDialog();
			
			dialog.open();
			
			dialog.addOpenedChangeListener(eClosed->{
				if(dialog.getJsonAssign()!=null) {
					taskAssgineeLayout.initOldValue(dialog.getJsonAssign());
					taskAssgineeLayout.displayResult();
					
					taskSupportLayout.initOldValue(dialog.getJsonFollow());
					taskSupportLayout.displayResult();
				}
			});
		});
		
		btnSaveTask.addClickListener(e->{
			if((taskId==null && parentId==null) || (taskId==null && parentId!=null)) { // check if create new task or create task from parent task
				if(taskInfoLayout.validateForm()) {
					if(taskAssgineeLayout.validateForm()) {
						TaskInfoCreateModel modelTaskInfo = taskInfoLayout.getFormData();
						TaskAssigneeUserModel modelUserOwner = null;
						TaskAssigneeUserModel modelUserAssistant = null;
						TaskAssigneeUserModel modelUserAssignee = taskAssgineeLayout.getModelUser();
						List<TaskAssigneeUserModel> listUserSupport = new ArrayList<TaskAssigneeUserModel>(taskSupportLayout.getMapUser().values());
					
						if(taskOwnerLayout!=null && taskOwnerLayout.getModelUser()!=null) { // check if create task for boss, true = set current user to assistant, false = set current uset to owner
							modelUserOwner = taskOwnerLayout.getModelUser();
							
							modelUserAssistant = new TaskAssigneeUserModel();
							modelUserAssistant.setIdUser(SessionUtil.getUserId());
							modelUserAssistant.setFullName(SessionUtil.getUser().getFullname());
							modelUserAssistant.setIdOrg(SessionUtil.getOrgId());
							modelUserAssistant.setOrgName(SessionUtil.getOrg().getName());
						} else {
							modelUserOwner = new TaskAssigneeUserModel();
							modelUserOwner.setIdUser(SessionUtil.getUserId());
							modelUserOwner.setFullName(SessionUtil.getUser().getFullname());
							modelUserOwner.setIdOrg(SessionUtil.getOrgId());
							modelUserOwner.setOrgName(SessionUtil.getOrg().getName());
						}
						try {
							if(taskId==null && parentId==null) { //check if create new task
								JsonObject jsonResponse = TaskServiceUtil.createTask(null,null,modelTaskInfo,modelUserOwner,modelUserAssistant, modelUserAssignee, listUserSupport);
								System.out.println("=====SingleTaskCreateLayout: btnSaveTask click=====");
								System.out.println(jsonResponse);
								if(jsonResponse.get("status").getAsInt()==201) {
									NotificationUtil.showNotifi("Giao nhiệm vụ thành công.", NotificationTypeEnum.SUCCESS);
									Map<String, String> mapParam = new HashMap<String, String>();
									
									if(modelUserAssistant==null)
										mapParam.put("type", TaskTypeEnum.DAGIAO.getKey());
									else
										mapParam.put("type", TaskTypeEnum.GIAOVIECTHAY.getKey());

									mapParam.put("status", TaskStatusEnum.TATCA.getKey());
									mapParam.put("assignmentType",TaskAssignmentTypeEnum.USER.getKey());
									mapParam.put("assignmentStatus",TaskAssignmentStatusEnum.CHUAPHAN_CANBO.getKey());
									
									
									SessionUtil.setParam(mapParam);
									getUI().ifPresent(ui -> ui.navigate(TaskListView.class));
									
									UIUtil.getMainView().updateCountMenu(SessionUtil.getUserId(), SessionUtil.getOrgId(),SessionUtil.getYear(),SessionUtil.getToken());
									
									String messageBroadcast = BroadcasterSupportUitl.createMessageOnTask(jsonResponse.getAsJsonObject("result"));
									messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.UPDATEUI);
									messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.MAINVIEW);
									
									BroadcasterUtil.broadcast(messageBroadcast);
								} else {
									System.out.println(jsonResponse);
									NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
								}
							} else if(taskId==null && parentId!=null) { // check if create task from parent task
								JsonObject jsonResponse = TaskServiceUtil.createTask(parentId,null,modelTaskInfo,modelUserOwner,modelUserAssistant, modelUserAssignee, listUserSupport);
								
								if(jsonResponse.get("status").getAsInt()==201) {
									NotificationUtil.showNotifi("Giao tiếp nhiệm vụ thành công.", NotificationTypeEnum.SUCCESS);
									
									UIUtil.getMainView().updateCountMenu(SessionUtil.getUserId(), SessionUtil.getOrgId(),SessionUtil.getYear(),SessionUtil.getToken());
									
									String messageBroadcast = BroadcasterSupportUitl.createMessageOnTask(jsonResponse.getAsJsonObject("result"));
									messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.UPDATEUI);
									messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.MAINVIEW);
									
									BroadcasterUtil.broadcast(messageBroadcast);
									btnTrigger.click();
								} else {
									System.out.println(jsonResponse);
									NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
								}
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			} else if(taskId!=null){ // check if update task
				if(taskInfoLayout.validateForm()) {
					TaskAssigneeUserModel modelUserAssignee = taskAssgineeLayout.getModelUser();
					JsonObject jsonAssignOld = jsonTask.getAsJsonObject("assignee");
					
					//check if change main user of task, if it does, confirm to user
					if(modelUserAssignee.getIdUser()==jsonAssignOld.get("userId").getAsString() && modelUserAssignee.getIdOrg()==jsonAssignOld.get("organizationId").getAsString()) {
						updateTask();
					} else {
						String title = "Bạn muốn thay đổi người chủ trì của nhiệm vụ?";
						String description = "Sau khi thay đổi người chủ trì, cán bộ chủ trì cũ sẽ không thể theo dõi nhiệm vụ này nữa. Những nhiệm vụ con (nếu có) sẽ được chuyển qua cho cán bộ chủ trì mới quản lý.";
						ConfirmDialog confDialog = new ConfirmDialog(title, description, 
								"Xác nhận", 
								eConfirm->{
									updateTask();
								},
								"Hủy",
								eCancel->{
									eCancel.getSource().close();
								});
						confDialog.open();
					}
				}
			}
		});
		
		taskAssgineeLayout.setTaskSupportLayout(taskSupportLayout);
		taskSupportLayout.setTaskAssgineeLayout(taskAssgineeLayout);
	}
	
	private void buildLeftLayout() {
		vLeft.add(taskInfoLayout);
		vLeft.add(hAction);
		
		hAction.add(btnSaveTask);
		hAction.setAlignItems(Alignment.END);
		
		btnSaveTask.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		
		vLeft.setHorizontalComponentAlignment(Alignment.END, hAction);
		vLeft.setWidthFull();
	}
	
	private void buildRightLayout() {
		if(taskOwnerLayout!=null) {
			vRight.add(taskOwnerLayout);
		}
		vRight.add(taskAssgineeLayout);
		vRight.add(taskSupportLayout);
		
		btnChooseUserTemplate.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnChooseUserTemplate.addThemeVariants(ButtonVariant.LUMO_SMALL);
		
		vRight.setWidthFull();
	}
	
	private void updateTask() {
		TaskInfoCreateModel modelTaskInfo = taskInfoLayout.getFormData();
		TaskAssigneeUserModel modelUserAssignee = taskAssgineeLayout.getModelUser();
		Map<String, TaskAssigneeUserModel> mapUserSupport = taskSupportLayout.getMapUser();
		List<String> listKeyOld = new ArrayList<String>();
		List<TaskAssigneeUserModel> listSupporToAdd = new ArrayList<TaskAssigneeUserModel>();
		List<CustomPairModel<String,String>> listSupportToDelete = new ArrayList<CustomPairModel<String,String>>();
		
		//check update assignee
		JsonObject jsonAssignOld = jsonTask.getAsJsonObject("assignee");
		if(modelUserAssignee.getIdUser()==jsonAssignOld.get("userId").getAsString() && modelUserAssignee.getIdOrg()==jsonAssignOld.get("organizationId").getAsString()) {
			modelUserAssignee = null;
		}
		//get list to delete
		JsonArray jsonArrSupport = jsonTask.getAsJsonArray("followersTask");
		
		for(JsonElement jsonEle : jsonArrSupport) {
			JsonObject jsonSupport = jsonEle.getAsJsonObject();
			String userId = jsonSupport.get("userId").getAsString();
			String organizationId = jsonSupport.get("organizationId").getAsString();
			
			String key = userId+"-"+organizationId;
			
			if(!mapUserSupport.containsKey(key)) {
				listSupportToDelete.add(new CustomPairModel<String, String>(userId, organizationId));
			}
			
			listKeyOld.add(key);
		}
		
		for(Entry<String, TaskAssigneeUserModel> entry : mapUserSupport.entrySet()) {
			if(!listKeyOld.contains(entry.getKey())) {
				listSupporToAdd.add(entry.getValue());
			}
		}
		try {
			JsonObject jsonResponse = TaskServiceUtil.updateTask(taskId,modelTaskInfo,modelUserAssignee, listSupportToDelete, listSupporToAdd);
		
			if(jsonResponse.get("status").getAsInt()==200) {
				NotificationUtil.showNotifi("Cập nhật nhiệm vụ thành công.", NotificationTypeEnum.SUCCESS);
				JsonObject jsonResult = jsonResponse.getAsJsonObject("result");
				
				if(modelUserAssignee!=null) {
					jsonResult.add("assigneeOld", jsonAssignOld);
				}
				
				String messageBroadcast = BroadcasterSupportUitl.createMessageOnTask(jsonResult);
				messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.UPDATEUI);
				messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.MAINVIEW);
				messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.TASKDETAIL);
				
				BroadcasterUtil.broadcast(messageBroadcast);
				btnTrigger.click();
			} else {
				System.out.println(jsonResponse);
				NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public Button getBtnTrigger() {
		return btnTrigger;
	}
}
