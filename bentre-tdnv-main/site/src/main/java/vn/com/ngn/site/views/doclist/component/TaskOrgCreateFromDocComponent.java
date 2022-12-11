package vn.com.ngn.site.views.doclist.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.dialog.task.ChooseOrgGroupDialog;
import vn.com.ngn.site.dialog.task.ChooseUserGroupDialog;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.model.UploadModuleDataWithDescriptionModel;
import vn.com.ngn.site.model.taskcreate.TaskAssigneeOrgModel;
import vn.com.ngn.site.model.taskcreate.TaskAssigneeUserModel;
import vn.com.ngn.site.model.taskcreate.TaskInfoCreateModel;
import vn.com.ngn.site.util.BroadcasterSupportUitl;
import vn.com.ngn.site.util.BroadcasterUtil;
import vn.com.ngn.site.util.GeneralUtil;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.UIUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.DocServiceUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;
import vn.com.ngn.site.views.taskcreate.TaskAssgineeLayout;
import vn.com.ngn.site.views.taskcreate.TaskAssigneeOrgLayout;
import vn.com.ngn.site.views.taskcreate.TaskInfoLayout;
import vn.com.ngn.site.views.taskcreate.TaskOwnerLayout;
import vn.com.ngn.site.views.taskcreate.TaskSupportLayout;
import vn.com.ngn.site.views.taskcreate.TaskSupportOrgLayout;

@SuppressWarnings("serial")
public class TaskOrgCreateFromDocComponent extends VerticalLayout implements LayoutInterface {
	private TaskInfoLayout taskInfoLayout;
	private CheckboxGroup<UploadModuleDataWithDescriptionModel> cbDocAttach = new CheckboxGroup<UploadModuleDataWithDescriptionModel>();
	private Button btnChooseOrgTemplate = new Button("Chọn nhóm cơ quan/đơn vị",VaadinIcon.AREA_SELECT.create());
	private TaskOwnerLayout taskOwnerLayout;
	private TaskAssigneeOrgLayout taskAssigneeOrgLayout;
	private TaskSupportOrgLayout taskSupportOrgLayout;

	private HorizontalLayout hAction = new HorizontalLayout();
	private Button btnCancel = new  Button("Hủy",VaadinIcon.TRASH.create());
	private Button btnSaveTask = new  Button("Giao nhiệm vụ",VaadinIcon.ARROW_FORWARD.create());

	private JsonObject jsonDoc;

	private Button btnTrigger = new Button();

	public TaskOrgCreateFromDocComponent(JsonObject jsonDoc) {
		this.jsonDoc = jsonDoc;

		if(SessionUtil.getOrg().getLeadersTask().size()>0)
			taskOwnerLayout = new TaskOwnerLayout(null);
		taskInfoLayout = new TaskInfoLayout(null);
		taskAssigneeOrgLayout = new TaskAssigneeOrgLayout(null);
		taskSupportOrgLayout = new TaskSupportOrgLayout(null);

		taskAssigneeOrgLayout.gethWrapCaption().add(btnChooseOrgTemplate);
		
		String docSummary = jsonDoc.get("docSummary").getAsString();
		
		taskInfoLayout.getTxtTaskTitle().setValue(docSummary);
		taskInfoLayout.getTxtTaskDescription().setValue(docSummary);
		
		List<UploadModuleDataWithDescriptionModel> listDocAttach = new ArrayList<UploadModuleDataWithDescriptionModel>();
		for(JsonElement jsonAttachEle : jsonDoc.getAsJsonArray("docAttachments")) {
			JsonObject jsonAttach = jsonAttachEle.getAsJsonObject();
			
			UploadModuleDataWithDescriptionModel model = new UploadModuleDataWithDescriptionModel();
			model.setFileName(jsonAttach.get("fileName").getAsString());
			model.setFileType(jsonAttach.get("fileType").getAsString());
			model.setDescription(jsonAttach.get("filePath").getAsString());
			
			listDocAttach.add(model);
		}
		
		cbDocAttach.setLabel("Lấy đính kèm từ văn bản");
		cbDocAttach.setItems(listDocAttach);
		cbDocAttach.setItemLabelGenerator(UploadModuleDataWithDescriptionModel::getFileName);
		cbDocAttach.select(listDocAttach);
		
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		H4 title = new H4("Giao nhiệm vụ cho cơ quan/đơn vị");
		title.getStyle().set("color", "#1d4b90");
		this.add(title);
		this.add(taskInfoLayout);
		if(taskOwnerLayout!=null)
			this.add(taskOwnerLayout);
		this.add(taskAssigneeOrgLayout);
		this.add(taskSupportOrgLayout);
		this.add(hAction);
		this.add(btnTrigger);
		
		taskInfoLayout.addComponentAtIndex(5, cbDocAttach);
		
		taskInfoLayout.getStyle().set("padding-bottom", "20px");
		taskInfoLayout.getStyle().set("border-bottom", "1px solid rgb(226, 226, 226)");
		hAction.add(btnCancel,btnSaveTask);
		hAction.setAlignItems(Alignment.END);

		btnSaveTask.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		btnCancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
		btnChooseOrgTemplate.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		btnTrigger.setVisible(false);
		
		this.setHorizontalComponentAlignment(Alignment.END, hAction);
		this.setSizeFull();
	}

	@Override
	public void configComponent() {
		btnChooseOrgTemplate.addClickListener(e->{
			ChooseOrgGroupDialog dialog = new ChooseOrgGroupDialog();

			dialog.open();

			dialog.addOpenedChangeListener(eClosed->{
				if(dialog.getJsonAssign()!=null) {
					taskAssigneeOrgLayout.initOldValue(dialog.getJsonAssign());
					taskAssigneeOrgLayout.displayResult();

					taskSupportOrgLayout.initOldValue(dialog.getJsonFollow());
					taskSupportOrgLayout.displayResult();
				}
			});
		});

		btnSaveTask.addClickListener(e->{
			System.out.println("=====TaskOrgCreateFromDocComponent: btnSaveTask=====");
			if(taskInfoLayout.validateForm()) {
				if(taskAssigneeOrgLayout.validateForm()) {
					TaskInfoCreateModel modelTaskInfo = taskInfoLayout.getFormData();
					TaskAssigneeUserModel modelUserOwner = null;
					TaskAssigneeUserModel modelUserAssistant = null;
					TaskAssigneeOrgModel modelOrgAssignee = taskAssigneeOrgLayout.getOrgModel();
					List<TaskAssigneeOrgModel> listOrgSupport = new ArrayList<TaskAssigneeOrgModel>(taskSupportOrgLayout.getMapOrg().values());
					String docId = jsonDoc.get("id").getAsString();
					
					if(taskOwnerLayout!=null && taskOwnerLayout.getModelUser()!=null) {
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
						for(UploadModuleDataWithDescriptionModel modelDocAttach : cbDocAttach.getValue()) {
							if(modelDocAttach.getInputStream()==null) {
								JsonObject jsonResponse = DocServiceUtil.getAttachmentContent(modelDocAttach.getDescription());
								
								if(jsonResponse.get("status").getAsInt()==200) {
									String base64 = jsonResponse.get("result").getAsString();
									
									modelDocAttach.setDescription("Đính kèm từ văn bản");
									modelDocAttach.setInputStream(GeneralUtil.byteArrayToInputStream(GeneralUtil.base64ToByteArray(base64)));
								
									modelTaskInfo.getListFileUpload().add(modelDocAttach);
								} else {
									System.out.println(jsonResponse);
									NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại sau!!", NotificationTypeEnum.ERROR);
								}
							} else {
								modelTaskInfo.getListFileUpload().add(modelDocAttach);
							}
						}
						
						JsonObject jsonResponse = TaskServiceUtil.createOrgTask(null,docId,modelTaskInfo,modelUserOwner,modelUserAssistant, modelOrgAssignee, listOrgSupport);
						
						if(jsonResponse.get("status").getAsInt()==201) {
							NotificationUtil.showNotifi("Giao nhiệm vụ thành công.", NotificationTypeEnum.SUCCESS);
							
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
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		taskAssigneeOrgLayout.setTaskSupportOrgLayout(taskSupportOrgLayout);
		taskSupportOrgLayout.setTaskAssigneeOrgLayout(taskAssigneeOrgLayout);
	}

	public Button getBtnTrigger() {
		return btnTrigger;
	}
	public Button getBtnCancel() {
		return btnCancel;
	}
}
