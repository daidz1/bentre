package vn.com.ngn.site.views.doclist.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.splitlayout.SplitLayout;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.enums.TaskAssignmentStatusEnum;
import vn.com.ngn.site.enums.TaskAssignmentTypeEnum;
import vn.com.ngn.site.enums.TaskStatusEnum;
import vn.com.ngn.site.enums.TaskTypeEnum;
import vn.com.ngn.site.model.taskcreate.TaskAssigneeUserModel;
import vn.com.ngn.site.model.taskcreate.TaskDataForMultiCreateModel;
import vn.com.ngn.site.model.taskcreate.TaskInfoCreateModel;
import vn.com.ngn.site.util.BroadcasterSupportUitl;
import vn.com.ngn.site.util.BroadcasterUtil;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.UIUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;
import vn.com.ngn.site.views.taskcreate.TaskBlockForMultiCreateLayout;
import vn.com.ngn.site.views.taskcreate.TaskInfoLayout;
import vn.com.ngn.site.views.tasklist.TaskListView;

public class MultiTaskCreateFromDocLayout extends VerticalLayout implements LayoutInterface{
	private int count = 0;
	private HorizontalLayout hHead = new HorizontalLayout();
	private Button btnAddNewTask = new Button("Thêm nhiệm vụ",VaadinIcon.PLUS.create());
	private Button btnCreateTask = new Button("Giao nhiệm vụ",VaadinIcon.FORWARD.create());
	private Checkbox cbType = new Checkbox("Dùng chung nội dung nhiệm vụ");

	private SplitLayout splitLayout = new SplitLayout();
	private TaskInfoLayout layoutTaskInfo = new TaskInfoLayout(null);
	private VerticalLayout vListTask = new VerticalLayout();
	private VerticalLayout vListTask2 = new VerticalLayout();
	private LinkedList<TaskBlockForMultiCreateLayout> listTaskLayout = new LinkedList<TaskBlockForMultiCreateLayout>();

	
	private Button btnTrigger = new Button();
	
	private JsonObject jsonDoc;
	public MultiTaskCreateFromDocLayout(JsonObject jsonDoc) {
		this.jsonDoc = jsonDoc;
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.add(hHead);
		this.add(splitLayout);
		this.add(vListTask);

		hHead.add(btnAddNewTask, btnCreateTask, cbType);

		btnAddNewTask.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		btnCreateTask.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		hHead.getStyle().set("margin", "15px 0px 0px 8px");
		hHead.setDefaultVerticalComponentAlignment(Alignment.CENTER);

		splitLayout.addToPrimary(layoutTaskInfo);
		splitLayout.addToSecondary(vListTask2);
		splitLayout.setSplitterPosition(50);
		splitLayout.setWidthFull();

		layoutTaskInfo.setPadding(true);
		splitLayout.setVisible(false);

		vListTask.setPadding(false);
		vListTask2.setPadding(false);

		addNewTask(null);

		this.setPadding(false);
	}

	@Override
	public void configComponent() {
		btnAddNewTask.addClickListener(e->{
			addNewTask(null);
		});
		
		btnCreateTask.addClickListener(e->{
			if(listTaskLayout.size()>0) {
				boolean isValid = true;
				
				if(cbType.getValue()) {
					if(!layoutTaskInfo.validateForm()) {
						isValid = false;
					}
				}
				for(TaskBlockForMultiCreateLayout modelTaskLayout : listTaskLayout) {
					if(!modelTaskLayout.validateForm()) {
						isValid = false;
					}
				}
				
				if(isValid) {
					boolean isAllGood = true;
					List<TaskBlockForMultiCreateLayout> listToDelete = new ArrayList<TaskBlockForMultiCreateLayout>();
					for(TaskBlockForMultiCreateLayout modelTaskLayout : listTaskLayout) {
						TaskDataForMultiCreateModel modelData = modelTaskLayout.getData();
						
						String taskTitle = modelData.getModelTaskInfo().getTitle();
						String assigneeName = modelData.getModelUserAssignee().getFullName();
						
						TaskAssigneeUserModel modelUserOwner = new TaskAssigneeUserModel();
						modelUserOwner.setIdUser(SessionUtil.getUserId());
						modelUserOwner.setFullName(SessionUtil.getUser().getFullname());
						modelUserOwner.setIdOrg(SessionUtil.getOrgId());
						modelUserOwner.setOrgName(SessionUtil.getOrg().getName());
						try {
							TaskInfoCreateModel modelTaskInfo = null;
							if(cbType.getValue()) {
								modelTaskInfo = layoutTaskInfo.getFormData();
							} else {
								modelTaskInfo = modelData.getModelTaskInfo();
							}
							String docId = this.jsonDoc.get("id").getAsString();
							JsonObject jsonResponse = TaskServiceUtil.createTask(null,docId,modelTaskInfo,modelUserOwner,null, modelData.getModelUserAssignee(), modelData.getListUserSupport());
							
							if(jsonResponse.get("status").getAsInt()==201) {
								UIUtil.getMainView().updateCountMenu(SessionUtil.getUserId(), SessionUtil.getOrgId(),SessionUtil.getYear(),SessionUtil.getToken());
								
								String messageBroadcast = BroadcasterSupportUitl.createMessageOnTask(jsonResponse.getAsJsonObject("result"));
								messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.UPDATEUI);
								messageBroadcast = BroadcasterSupportUitl.appendMessageWithOption(messageBroadcast,BroadcasterSupportUitl.MAINVIEW);
								
								BroadcasterUtil.broadcast(messageBroadcast);
								
								if(!cbType.getValue()) {
									NotificationUtil.showNotifi("Giao nhiệm vụ \""+taskTitle+"\" thành công", NotificationTypeEnum.SUCCESS);
								} else {
									NotificationUtil.showNotifi("Giao nhiệm vụ cho cán bộ "+assigneeName+" thành công", NotificationTypeEnum.SUCCESS);
								}
								
								listToDelete.add(modelTaskLayout);
								btnTrigger.click();
							} else {
								System.out.println(jsonResponse);
								isAllGood = false;
								if(!cbType.getValue()) {
									NotificationUtil.showNotifi("Không thể giao nhiệm vụ \""+taskTitle+"\", vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
								} else {
									NotificationUtil.showNotifi("Có lỗi xảy ra khi giao nhiệm vụ cho cán bộ "+assigneeName+" vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
								}
							}
						} catch (Exception e2) {
							e2.printStackTrace();
							isAllGood = false;
							if(!cbType.getValue()) {
								NotificationUtil.showNotifi("Không thể giao nhiệm vụ \""+taskTitle+"\", vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
							} else {
								NotificationUtil.showNotifi("Có lỗi xảy ra khi giao nhiệm vụ cho "+assigneeName+" vui lòng thử lại sau!", NotificationTypeEnum.ERROR);
							}
						}
					}
					
					if(isAllGood) {
						Map<String, String> mapParam = new HashMap<String, String>();

						mapParam.put("type", TaskTypeEnum.DAGIAO.getKey());
						mapParam.put("status", TaskStatusEnum.TATCA.getKey());
						mapParam.put("assignmentType", TaskAssignmentTypeEnum.USER.getKey());
						mapParam.put("assignmentStatus", TaskAssignmentStatusEnum.CHUAPHAN_CANBO.getKey());
						
						
						SessionUtil.setParam(mapParam);
						getUI().ifPresent(ui -> ui.navigate(TaskListView.class));
						
						UIUtil.getMainView().updateCountMenu(SessionUtil.getUserId(), SessionUtil.getOrgId(),SessionUtil.getYear(),SessionUtil.getToken());
					} else {
						listTaskLayout.removeAll(listToDelete);
						
						reloadTaskList();
					}
				}
			} else {
				NotificationUtil.showNotifi("Vui lòng tạo ít nhất một nhiệm vụ để giao", NotificationTypeEnum.WARNING);
			}
		});

		cbType.addValueChangeListener(e->{
			if(e.getValue()) {
				splitLayout.setVisible(true);
			} else {
				splitLayout.setVisible(false);
			}
			reloadTaskList();
		});
	}

	private void addNewTask(TaskDataForMultiCreateModel modelDuplicateData) {
		TaskBlockForMultiCreateLayout modelLayoutTask = new TaskBlockForMultiCreateLayout();
		modelLayoutTask.setFormId(++count);
		modelLayoutTask.setSpecific(!cbType.getValue());
		modelLayoutTask.initForm();
		if(modelDuplicateData!=null) {
			modelLayoutTask.setData(modelDuplicateData);
		}

		if(cbType.getValue()) {
			vListTask2.add(modelLayoutTask);
		} else {
			vListTask.add(modelLayoutTask);
		}

		listTaskLayout.add(modelLayoutTask);

		modelLayoutTask.getMenuItemClone().addClickListener(e->{
			addNewTask(modelLayoutTask.getData());
		});

		modelLayoutTask.getMenuItemDelete().addClickListener(e->{
			listTaskLayout.remove(modelLayoutTask);

			reloadTaskList();
		});
	}

	private void reloadTaskList() {
		vListTask.removeAll();
		vListTask2.removeAll();
		for(TaskBlockForMultiCreateLayout model : listTaskLayout) {
			model.setSpecific(!cbType.getValue());
			model.reloadForm();

			if(cbType.getValue()) {
				vListTask2.add(model);
			}
			else{
				vListTask.add(model);
			}
		}
	}

	public Button getBtnTrigger() {
		return btnTrigger;
	}

	public void setBtnTrigger(Button btnTrigger) {
		this.btnTrigger = btnTrigger;
	}
	


}
