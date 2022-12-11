package vn.com.ngn.site.views.tasklist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.enums.TaskAssignmentStatusEnum;
import vn.com.ngn.site.enums.TaskAssignmentTypeEnum;
import vn.com.ngn.site.enums.TaskPriorityEnum;
import vn.com.ngn.site.enums.TaskStatusEnum;
import vn.com.ngn.site.enums.TaskTypeEnum;
import vn.com.ngn.site.model.CustomPairModel;
import vn.com.ngn.site.model.SimpleUserModel;
import vn.com.ngn.site.model.TaskFilterModel;
import vn.com.ngn.site.module.PaginationModule;
import vn.com.ngn.site.util.LocalDateUtil;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;
import vn.com.ngn.site.views.main.MainView;

@SuppressWarnings("serial")
@Route(value = "tasklist", layout = MainView.class)
@PageTitle("Danh sách nhiệm vụ")
public class TaskListView extends VerticalLayout implements LayoutInterface,BeforeEnterObserver,RouterLayout{
	private VerticalLayout vSearch;
	private HorizontalLayout hKeyword;
	private TextField txtKeyword;
	private HorizontalLayout hFilter1 = new HorizontalLayout();
	private ComboBox<CustomPairModel<Integer, String>> cmbPriority = new ComboBox<CustomPairModel<Integer,String>>("Độ khẩn");
	private ComboBox<CustomPairModel<String, String>> cmbOwner = new ComboBox<CustomPairModel<String,String>>("Người giao nhiệm vụ");
	private ComboBox<CustomPairModel<String, String>> cmbAssignee = new ComboBox<CustomPairModel<String,String>>("Người xử lý");
	private ComboBox<CustomPairModel<String, String>> cmbFollower = new ComboBox<CustomPairModel<String,String>>("Người hỗ trợ");
	private ComboBox<CustomPairModel<String, String>> cmbLeader = new ComboBox<CustomPairModel<String,String>>("Người giao nhiệm vụ");
	private ComboBox<CustomPairModel<String, String>> cmbAssistant = new ComboBox<CustomPairModel<String,String>>("Người theo dõi thay");
	private Button btnSearch;

	private PaginationModule pagination;

	private VerticalLayout vTaskList;

	private TaskTypeEnum eType;
	private TaskStatusEnum eStatus;
	
	//Dzung code
	private TaskAssignmentTypeEnum eAssignmentType;
	private TaskAssignmentStatusEnum eAssignmentStatus;

	private ShortcutRegistration regisShortcut = null;

	private List<CustomPairModel<String, String>> listUserOwner;
	private List<CustomPairModel<String, String>> listUserAssignee;
	private List<CustomPairModel<String, String>> listUserFollower;
	private List<CustomPairModel<String, String>> listUserLeader;
	private List<CustomPairModel<String, String>> listUserAssistant;

	public TaskListView() {

	}

	@Override
	public void buildLayout() {
		vSearch = new VerticalLayout();
		hKeyword = new HorizontalLayout();
		txtKeyword = new TextField("Từ khóa");
		btnSearch = new Button("Tìm kiếm",VaadinIcon.SEARCH.create());
		hFilter1 = new HorizontalLayout();
		cmbPriority = new ComboBox<CustomPairModel<Integer,String>>("Độ khẩn");
		cmbOwner = new ComboBox<CustomPairModel<String,String>>("Người giao nhiệm vụ");
		cmbAssignee = new ComboBox<CustomPairModel<String,String>>("Người xử lý");
		cmbFollower = new ComboBox<CustomPairModel<String,String>>("Người hỗ trợ");
		cmbLeader = new ComboBox<CustomPairModel<String,String>>("Người giao nhiệm vụ");
		cmbAssistant = new ComboBox<CustomPairModel<String,String>>("Người theo dõi thay");

		vTaskList = new VerticalLayout();

		this.add(vSearch);
		this.add(pagination);
		this.add(vTaskList);

		vTaskList.setWidthFull();
		vTaskList.setPadding(false);

		this.setWidthFull();
		this.getStyle().set("padding-left", "20px");
		this.getStyle().set("padding-right", "20px");

		buildSearchLayout();
	}

	@Override
	public void configComponent() {
		txtKeyword.addFocusListener(e->{
			regisShortcut = btnSearch.addClickShortcut(Key.ENTER);
		});
		txtKeyword.addBlurListener(e->{
			regisShortcut.remove();
		});
		btnSearch.addClickListener(e->{
			try {
				loadData();
				reCount(getTaskFilterAll());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		pagination.getBtnTrigger().addClickListener(e->{
			try {
				loadData();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		try {
			loadData(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void buildSearchLayout() {
		vSearch.add(hKeyword);
		vSearch.add(hFilter1);

		//keyword
		hKeyword.add(txtKeyword,btnSearch);

		txtKeyword.setPlaceholder("Nhập vào tiêu đề hoặc nội dung nhiệm vụ để tìm kiếm...");

		hKeyword.expand(txtKeyword);
		hKeyword.setWidthFull();
		hKeyword.setDefaultVerticalComponentAlignment(Alignment.END);

		//filter
		hFilter1.add(cmbPriority,cmbOwner,cmbAssignee,cmbFollower,cmbLeader,cmbAssistant);

		cmbOwner.setWidth("250px");
		cmbAssignee.setWidth("250px");
		cmbFollower.setWidth("250px");
		cmbLeader.setWidth("250px");
		cmbAssistant.setWidth("250px");

		hFilter1.setWidthFull();
		hFilter1.setDefaultVerticalComponentAlignment(Alignment.END);

		vSearch.setPadding(false);
		vSearch.setSpacing(false);
	}

	private void loadData() throws IOException {
		vTaskList.removeAll();

		JsonObject jsonResponse = TaskServiceUtil.getTaskList(getTaskFilterAll());

		if(jsonResponse.get("status").getAsInt()==200) {
			JsonArray jsonTaskList = jsonResponse.get("result").getAsJsonArray();

			for(JsonElement jsonTask : jsonTaskList) {
				TaskBlockLayout taskBlock = new TaskBlockLayout(jsonTask.getAsJsonObject(),eType.getKey(),eStatus.getKey(),this.getClass());
				vTaskList.add(taskBlock);
			}
		} else {
			System.out.println(jsonResponse);
			NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại", NotificationTypeEnum.ERROR);
		}
	}

	private TaskFilterModel getTaskFilterAll() {
		TaskFilterModel modelFilter = new TaskFilterModel();

		modelFilter.setKeyword(txtKeyword.getValue());
		modelFilter.setSkip(pagination.getSkip());
		modelFilter.setLimit(pagination.getLimit());
		modelFilter.setUserid(SessionUtil.getUserId());
		modelFilter.setOrganizationId(SessionUtil.getOrgId());
		modelFilter.setCategorykey(this.eType.getKey());
		modelFilter.setSubcategorykey(this.eStatus.getKey());
		//Dzung code
		if(this.eAssignmentType.getKey().equalsIgnoreCase(TaskAssignmentTypeEnum.ORGANIZATION.getKey())) {
			try {
				modelFilter.setAssignmentType(this.eAssignmentType.getKey());
				modelFilter.setAssignmentStatus(this.eAssignmentStatus.getKey());
			} catch (Exception e) {
				
			}
		}
		
		//end Dzung code
		modelFilter.setFormdate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getStartOfTheYear(SessionUtil.getYear())));
		modelFilter.setTodate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(SessionUtil.getYear())));
		modelFilter.setPriority(cmbPriority.getValue().getKey());

		if(eType==TaskTypeEnum.DUOCGIAO || eType==TaskTypeEnum.THEODOI) {
			modelFilter.setOwners(cmbOwner.getValue().getKey());
		}
		if(eType==TaskTypeEnum.DAGIAO || eType==TaskTypeEnum.THEODOI) {
			modelFilter.setAssignees(cmbAssignee.getValue().getKey());
		}
		if(eType==TaskTypeEnum.DAGIAO || eType==TaskTypeEnum.DUOCGIAO) {
			modelFilter.setFollowers(cmbFollower.getValue().getKey());
		}
		if(eType==TaskTypeEnum.GIAOVIECTHAY) {
			modelFilter.setOwners(cmbLeader.getValue().getKey());
		}
		if(eType==TaskTypeEnum.THEODOITHAY) {
			modelFilter.setAssistants(cmbAssistant.getValue().getKey());
		}

		return modelFilter;
	}

	private TaskFilterModel getTaskFilter() {
		TaskFilterModel modelFilter = new TaskFilterModel();

		modelFilter.setKeyword(txtKeyword.getValue());
		modelFilter.setSkip(pagination.getSkip());
		modelFilter.setLimit(pagination.getLimit());
		modelFilter.setUserid(SessionUtil.getUserId());
		modelFilter.setOrganizationId(SessionUtil.getOrgId());
		modelFilter.setCategorykey(this.eType.getKey());
		modelFilter.setSubcategorykey(this.eStatus.getKey());
		modelFilter.setFormdate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getStartOfTheYear(SessionUtil.getYear())));
		modelFilter.setTodate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(SessionUtil.getYear())));
		modelFilter.setPriority(cmbPriority.getValue().getKey());

		return modelFilter;
	}

	private void reCount(TaskFilterModel modelFilter) {
		try {
			JsonObject jsonResponse = TaskServiceUtil.getCountTaskList(modelFilter);

			if(jsonResponse.get("status").getAsInt()==200) {
				int count = jsonResponse.get("total").getAsInt();
				pagination.setItemCount(count);
				pagination.reCalculate();
			} else {
				System.out.println(jsonResponse);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initValue() {
		List<CustomPairModel<Integer, String>> listPriority = new ArrayList<CustomPairModel<Integer,String>>();
		CustomPairModel<Integer, String> modelAllPriority = new CustomPairModel<Integer, String>(0, "Tất cả");
		listPriority.add(modelAllPriority);
		for(TaskPriorityEnum ePrio : TaskPriorityEnum.values()) {
			CustomPairModel<Integer, String> modelPriority = new CustomPairModel<Integer, String>(ePrio.getKey(), ePrio.getCaption());

			listPriority.add(modelPriority);
		}

		cmbPriority.setItems(listPriority);
		cmbPriority.setItemLabelGenerator(CustomPairModel<Integer, String>::getValue);
		cmbPriority.setValue(modelAllPriority);

		try {
			JsonObject jsonResponse = TaskServiceUtil.getUserFilterList(getTaskFilter());
			if(jsonResponse.get("status").getAsInt()==200) {
				JsonObject jsonResult = jsonResponse.getAsJsonObject("result");

				//combobox owner
				if(eType==TaskTypeEnum.DUOCGIAO || eType==TaskTypeEnum.THEODOI) {
					listUserOwner = new ArrayList<CustomPairModel<String,String>>();

					CustomPairModel<String, String> modelAll = new CustomPairModel<String, String>(null, "Tất cả cán bộ");
					listUserOwner.add(modelAll);
					for(JsonElement jsonEle : jsonResult.getAsJsonArray("nguoigiao")) {
						JsonObject jsonOb = jsonEle.getAsJsonObject().getAsJsonObject("userTask");

						String key = jsonOb.get("userId").getAsString()+"-"+jsonOb.get("organizationId").getAsString();
						String caption = jsonOb.get("fullName").getAsString()+" - "+jsonOb.get("organizationName").getAsString();

						CustomPairModel<String, String> modelUser = new CustomPairModel<String, String>(key, caption);

						listUserOwner.add(modelUser);
					}

					cmbOwner.setItems(listUserOwner);
					cmbOwner.setItemLabelGenerator(CustomPairModel<String, String>::getValue);
					cmbOwner.setValue(modelAll);
				}
				//combobox assignee
				if(eType==TaskTypeEnum.DAGIAO || eType==TaskTypeEnum.THEODOI || eType==TaskTypeEnum.GIAOVIECTHAY || eType==TaskTypeEnum.THEODOITHAY) {
					listUserAssignee = new ArrayList<CustomPairModel<String,String>>();

					CustomPairModel<String, String> modelAll = new CustomPairModel<String, String>(null, "Tất cả cán bộ");
					listUserAssignee.add(modelAll);
					for(JsonElement jsonEle : jsonResult.getAsJsonArray("nguoixuly")) {
						try {
							JsonObject jsonOb = jsonEle.getAsJsonObject().getAsJsonObject("userTask");
							String key = jsonOb.get("userId").getAsString()+"-"+jsonOb.get("organizationId").getAsString();
							String caption = jsonOb.get("fullName").getAsString()+" - "+jsonOb.get("organizationName").getAsString();

							CustomPairModel<String, String> modelUser = new CustomPairModel<String, String>(key, caption);

							listUserAssignee.add(modelUser);
						} catch (Exception e) {
//							e.printStackTrace();
						}
					}

					cmbAssignee.setItems(listUserAssignee);
					cmbAssignee.setItemLabelGenerator(CustomPairModel<String, String>::getValue);
					cmbAssignee.setValue(modelAll);
				}
				//combobox follower
				if(eType==TaskTypeEnum.DAGIAO || eType==TaskTypeEnum.DUOCGIAO) {
					listUserFollower = new ArrayList<CustomPairModel<String,String>>();

					CustomPairModel<String, String> modelAll = new CustomPairModel<String, String>(null, "Tất cả cán bộ");
					listUserFollower.add(modelAll);
					for(JsonElement jsonEle : jsonResult.getAsJsonArray("nguoihotro")) {
						try {
							JsonObject jsonOb = jsonEle.getAsJsonObject().getAsJsonObject("userTask");
							String key = jsonOb.get("userId").getAsString()+"-"+jsonOb.get("organizationId").getAsString();
							String caption = jsonOb.get("fullName").getAsString()+" - "+jsonOb.get("organizationName").getAsString();

							CustomPairModel<String, String> modelUser = new CustomPairModel<String, String>(key, caption);

							listUserFollower.add(modelUser);
						} catch (Exception e) {
							// TODO Auto-generated catch block
//							e.printStackTrace();
						}
					}

					cmbFollower.setItems(listUserFollower);
					cmbFollower.setItemLabelGenerator(CustomPairModel<String, String>::getValue);
					cmbFollower.setValue(modelAll);
				}
				//combobox leader
				if(eType==TaskTypeEnum.GIAOVIECTHAY) {
					listUserLeader = new ArrayList<CustomPairModel<String,String>>();

					CustomPairModel<String, String> modelAll = new CustomPairModel<String, String>(null, "Tất cả cán bộ");
					listUserLeader.add(modelAll);
					for(SimpleUserModel modelUser : SessionUtil.getOrg().getLeadersTask()) {
						String key = modelUser.getUserId()+"-"+modelUser.getOrganizationId();
						String caption = modelUser.getFullName()+" - "+modelUser.getOrganizationName();

						CustomPairModel<String, String> modelLeader = new CustomPairModel<String, String>(key, caption);

						listUserLeader.add(modelLeader);
					}

					cmbLeader.setItems(listUserLeader);
					cmbLeader.setItemLabelGenerator(CustomPairModel<String, String>::getValue);
					cmbLeader.setValue(modelAll);
				}

				//combobox assistant
				if(eType==TaskTypeEnum.THEODOITHAY) {
					listUserAssistant = new ArrayList<CustomPairModel<String,String>>();

					CustomPairModel<String, String> modelAll = new CustomPairModel<String, String>(null, "Tất cả cán bộ");
					listUserAssistant.add(modelAll);
					for(SimpleUserModel modelUser : SessionUtil.getOrg().getAssistantsTask()) {
						String key = modelUser.getUserId()+"-"+modelUser.getOrganizationId();
						String caption = modelUser.getFullName()+" - "+modelUser.getOrganizationName();

						CustomPairModel<String, String> modelLeader = new CustomPairModel<String, String>(key, caption);

						listUserAssistant.add(modelLeader);
					}

					cmbAssistant.setItems(listUserAssistant);
					cmbAssistant.setItemLabelGenerator(CustomPairModel<String, String>::getValue);
					cmbAssistant.setValue(modelAll);
				}

				if(eType==TaskTypeEnum.DAGIAO) {
					cmbOwner.setVisible(false);
					cmbLeader.setVisible(false);
					cmbAssistant.setVisible(false);
				}
				if(eType==TaskTypeEnum.DUOCGIAO) {
					cmbAssignee.setVisible(false);
					cmbLeader.setVisible(false);
					cmbAssistant.setVisible(false);
				}
				if(eType==TaskTypeEnum.THEODOI) {
					cmbFollower.setVisible(false);
					cmbLeader.setVisible(false);
					cmbAssistant.setVisible(false);
				}
				if(eType==TaskTypeEnum.GIAOVIECTHAY) {
					cmbFollower.setVisible(false);
					cmbOwner.setVisible(false);
					cmbAssistant.setVisible(false);
				}
				if(eType==TaskTypeEnum.THEODOITHAY) {
					cmbFollower.setVisible(false);
					cmbOwner.setVisible(false);
					cmbLeader.setVisible(false);
				}
			} else {
				System.out.println(jsonResponse);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		//Dzung code
		this.eType=null;
		this.eStatus=null;
		this.eAssignmentStatus=null;
		this.eAssignmentType=null;
		//end Dzung code
		for(TaskTypeEnum eType : TaskTypeEnum.values()) {
			if(SessionUtil.getParam().get("type").equals(eType.getKey())) {
				this.eType = eType;
			}
		}
		System.out.println("eType: "+this.eType);

		for(TaskStatusEnum eStatus : TaskStatusEnum.values()) {
			if(SessionUtil.getParam().get("status").equals(eStatus.getKey())) {
				this.eStatus = eStatus;
			}
		}
		System.out.println("eStatys: "+this.eStatus);
		
		//Dzung code
		//param dung cho nhiem vu don vi duoc giao
		try {
			for(TaskAssignmentTypeEnum assignmentType:TaskAssignmentTypeEnum.values()) {
				if(SessionUtil.getParam().get("assignmentType").equals(assignmentType.getKey())) {
					this.eAssignmentType = assignmentType;
				}
			}
		} catch (Exception e) {
			
		}
		if(this.eAssignmentType!=null) {
			System.out.println("eAssignmentType: "+this.eAssignmentType);
		}
		
		try {
			for(TaskAssignmentStatusEnum assignmentStatus:TaskAssignmentStatusEnum.values()) {
				if(SessionUtil.getParam().get("assignmentStatus").equals(assignmentStatus.getKey())) {
					this.eAssignmentStatus = assignmentStatus;
				}
			}
		} catch (Exception e) {
			
			
		}
		if(this.eAssignmentStatus!=null) {
			System.out.println("eAssignmentStatus: "+this.eAssignmentStatus);
		}
		//end Dzung code

		this.removeAll();
		pagination = new PaginationModule();

		buildLayout();
		initValue();

		if(SessionUtil.getParam().containsKey("userduocgiao")) {
			String idUser = SessionUtil.getParam().get("userduocgiao");

			for(CustomPairModel<String, String> modelUser : listUserAssignee) {
				if(modelUser.getKey()!=null && modelUser.getKey().equals(idUser)) {
					cmbAssignee.setValue(modelUser);
				}
			}
		}

		if(SessionUtil.getParam().containsKey("userdagiao")) {
			String idUser = SessionUtil.getParam().get("userdagiao");

			for(CustomPairModel<String, String> modelUser : listUserOwner) {
				if(modelUser.getKey()!=null && modelUser.getKey().equals(idUser)) {
					cmbOwner.setValue(modelUser);
				}
			}
		}

		reCount(getTaskFilterAll());
		configComponent();
	}
}
