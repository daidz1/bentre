package vn.com.ngn.site.module;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.enums.TaskPriorityEnum;
import vn.com.ngn.site.enums.TaskStatusEnum;
import vn.com.ngn.site.enums.TaskTypeEnum;
import vn.com.ngn.site.model.CustomPairModel;
import vn.com.ngn.site.model.TaskFilterModel;
import vn.com.ngn.site.util.LocalDateUtil;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.service.TaskServiceUtil;

@SuppressWarnings("serial")
public class TaskFilterModule extends VerticalLayout implements LayoutInterface{
	private HorizontalLayout hKeyword = new HorizontalLayout();
	private TextField txtKeyword = new TextField("Từ khóa");

	private HorizontalLayout hFilter1 = new HorizontalLayout();
	private DatePicker dpStart = new DatePicker("Từ ngày");
	private DatePicker dpEnd = new DatePicker("Đến ngày");
	private ComboBox<CustomPairModel<String, String>> cmbTaskType = new ComboBox<CustomPairModel<String,String>>("Loại nhiệm vụ");
	private ComboBox<CustomPairModel<String, String>> cmbTaskStatus = new ComboBox<CustomPairModel<String,String>>("Tình trạng");

	private HorizontalLayout hFilter2 = new HorizontalLayout();
	private ComboBox<CustomPairModel<Integer, String>> cmbPriority = new ComboBox<CustomPairModel<Integer,String>>("Độ khẩn");
	private ComboBox<CustomPairModel<String, String>> cmbOwner = new ComboBox<CustomPairModel<String,String>>("Người giao nhiệm vụ");
	private ComboBox<CustomPairModel<String, String>> cmbAssignee = new ComboBox<CustomPairModel<String,String>>("Người xử lý");
	private ComboBox<CustomPairModel<String, String>> cmbFollower = new ComboBox<CustomPairModel<String,String>>("Người hỗ trợ");
	private Button btnSearch = new Button("Tìm kiếm",VaadinIcon.SEARCH.create());
	private Button btnReport = new Button("Lập báo cáo",VaadinIcon.FILE_FONT.create());
	private Anchor btnDownload = new Anchor("",new Button("Tải về",VaadinIcon.DOWNLOAD.create()));

	private ShortcutRegistration regisShortcut = null;
	
	private List<CustomPairModel<String, String>> listTaskType = new ArrayList<CustomPairModel<String,String>>();
	
	private String currentPurpose;

	public TaskFilterModule() {
		buildLayout();
		configComponent();
		initValue();
	}

	private void initValue() {
		//date picker
		dpStart.setValue(LocalDate.now().minusDays(30));
		dpEnd.setValue(LocalDate.now());
		
		//combobox type & status
		CustomPairModel<String, String> modelAllType = new CustomPairModel<String, String>(null, "Tất cả");
		listTaskType.add(modelAllType);
		for(TaskTypeEnum eType : TaskTypeEnum.values()) {
			if(!eType.getKey().equals(TaskTypeEnum.GIAOVIECTHAY.getKey()) && !eType.getKey().equals(TaskTypeEnum.THEODOITHAY.getKey())) {
			CustomPairModel<String, String> modelType = new CustomPairModel<String, String>(eType.getKey(), eType.getTitle());

			listTaskType.add(modelType);
			}
		}

		cmbTaskType.setItems(listTaskType);
		cmbTaskType.setItemLabelGenerator(CustomPairModel<String, String>::getValue);
		cmbTaskType.setValue(modelAllType);

		List<CustomPairModel<String, String>> listTaskStatus = new ArrayList<CustomPairModel<String,String>>();
		CustomPairModel<String, String> modelAllStatus = new CustomPairModel<String, String>(null, "Tất cả");
		listTaskStatus.add(modelAllStatus);
		for(TaskStatusEnum eType : TaskStatusEnum.values()) {
			if(eType.equals(TaskStatusEnum.TATCA))
				continue;
			CustomPairModel<String, String> modelStatus = new CustomPairModel<String, String>(eType.getKey(), eType.getCaption());

			listTaskStatus.add(modelStatus);
		}

		cmbTaskStatus.setItems(listTaskStatus);
		cmbTaskStatus.setItemLabelGenerator(CustomPairModel<String, String>::getValue);
		cmbTaskStatus.setValue(modelAllStatus);

		//combobox priority
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
		
		//combobox user filter
		try {
			JsonObject jsonResponse = TaskServiceUtil.getUserFilterList(getTaskFilter());
			
			if(jsonResponse.get("status").getAsInt()==200) {
				JsonObject jsonResult = jsonResponse.getAsJsonObject("result");
				
				//combobox owner
				List<CustomPairModel<String, String>> listUserOwner = new ArrayList<CustomPairModel<String,String>>();
				
				CustomPairModel<String, String> modelOwnerAll = new CustomPairModel<String, String>(null, "Tất cả cán bộ");
				listUserOwner.add(modelOwnerAll);
				for(JsonElement jsonEle : jsonResult.getAsJsonArray("nguoigiao")) {
					JsonObject jsonOb = jsonEle.getAsJsonObject().getAsJsonObject("userTask");
					
					String key = jsonOb.get("userId").getAsString()+"-"+jsonOb.get("organizationId").getAsString();
					String caption = jsonOb.get("fullName").getAsString()+" - "+jsonOb.get("organizationName").getAsString();
				
					CustomPairModel<String, String> modelUser = new CustomPairModel<String, String>(key, caption);
					
					listUserOwner.add(modelUser);
				}
				
				cmbOwner.setItems(listUserOwner);
				cmbOwner.setItemLabelGenerator(CustomPairModel<String, String>::getValue);
				cmbOwner.setValue(modelOwnerAll);
				
				//combobox assignee
				List<CustomPairModel<String, String>> listUserAssignee = new ArrayList<CustomPairModel<String,String>>();
				
				CustomPairModel<String, String> modelAssigneeAll = new CustomPairModel<String, String>(null, "Tất cả cán bộ");
				listUserAssignee.add(modelAssigneeAll);
				for(JsonElement jsonEle : jsonResult.getAsJsonArray("nguoixuly")) {
					JsonObject jsonOb = jsonEle.getAsJsonObject().getAsJsonObject("userTask");
					String key;
					String caption;
					try {
						key = jsonOb.get("userId").getAsString()+"-"+jsonOb.get("organizationId").getAsString();
						caption = jsonOb.get("fullName").getAsString()+" - "+jsonOb.get("organizationName").getAsString();
					} catch (Exception e) {
						//Dzung code
						key = jsonOb.get("organizationId").getAsString()+"-"+jsonOb.get("organizationId").getAsString();
						caption = jsonOb.get("organizationName").getAsString()+" - "+jsonOb.get("organizationName").getAsString();
						//end Dzung code
					}
				
					CustomPairModel<String, String> modelUser = new CustomPairModel<String, String>(key, caption);
					
					listUserAssignee.add(modelUser);
				}
				
				cmbAssignee.setItems(listUserAssignee);
				cmbAssignee.setItemLabelGenerator(CustomPairModel<String, String>::getValue);
				cmbAssignee.setValue(modelAssigneeAll);
				//combobox follower
				List<CustomPairModel<String, String>> listUserFollower = new ArrayList<CustomPairModel<String,String>>();
				
				CustomPairModel<String, String> modelFollowerAll = new CustomPairModel<String, String>(null, "Tất cả cán bộ");
				listUserFollower.add(modelFollowerAll);
				for(JsonElement jsonEle : jsonResult.getAsJsonArray("nguoihotro")) {
					JsonObject jsonOb = jsonEle.getAsJsonObject().getAsJsonObject("userTask");
					
					String key;
					String caption;
					try {
						key = jsonOb.get("userId").getAsString()+"-"+jsonOb.get("organizationId").getAsString();
						caption = jsonOb.get("fullName").getAsString()+" - "+jsonOb.get("organizationName").getAsString();
					} catch (Exception e) {
						//Dzung code
						key = jsonOb.get("organizationId").getAsString()+"-"+jsonOb.get("organizationId").getAsString();
						caption = jsonOb.get("organizationName").getAsString()+" - "+jsonOb.get("organizationName").getAsString();
						//end Dzung code
					}
				
					CustomPairModel<String, String> modelUser = new CustomPairModel<String, String>(key, caption);
					
					listUserFollower.add(modelUser);
				}
				
				cmbFollower.setItems(listUserFollower);
				cmbFollower.setItemLabelGenerator(CustomPairModel<String, String>::getValue);
				cmbFollower.setValue(modelFollowerAll);
			} else {
				System.out.println(jsonResponse);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void buildLayout() {
		this.add(hKeyword);
		this.add(hFilter1);
		this.add(hFilter2);

		//keyword
		hKeyword.add(dpStart,dpEnd,txtKeyword,btnSearch,btnReport,btnDownload);

		txtKeyword.setPlaceholder("Nhập vào tiêu đề hoặc nội dung nhiệm vụ để tìm kiếm...");

		dpStart.setLocale(new Locale("vi", "VN"));
		dpEnd.setLocale(new Locale("vi", "VN"));
		
		hKeyword.expand(txtKeyword);
		hKeyword.setWidthFull();
		hKeyword.setDefaultVerticalComponentAlignment(Alignment.END);

		//hfilter 1
		hFilter1.setDefaultVerticalComponentAlignment(Alignment.END);

		//h filter 2
		hFilter2.add(cmbTaskType,cmbTaskStatus,cmbPriority,cmbOwner,cmbAssignee,cmbFollower);

		hFilter2.setDefaultVerticalComponentAlignment(Alignment.END);

		this.setPadding(false);
		this.setSpacing(false);

		this.setPadding(false);
	}
	
	public TaskFilterModel getTaskFilterAll() {
		TaskFilterModel modelFilter = new TaskFilterModel();
		
		LocalDate endDate = dpEnd.getValue().equals(LocalDate.now()) ? LocalDate.now().plusDays(1) : dpEnd.getValue();

		modelFilter.setKeyword(txtKeyword.getValue());
		modelFilter.setUserid(SessionUtil.getUserId());
		modelFilter.setOrganizationId(SessionUtil.getOrgId());
		modelFilter.setCategorykey(cmbTaskType.getValue().getKey());
		modelFilter.setSubcategorykey(cmbTaskStatus.getValue().getKey());
		modelFilter.setFormdate(LocalDateUtil.localDateToLong(dpStart.getValue()));
		modelFilter.setTodate(LocalDateUtil.localDateToLong(endDate));
		modelFilter.setPriority(cmbPriority.getValue().getKey());
		modelFilter.setOwners(cmbOwner.getValue().getKey());
		modelFilter.setAssignees(cmbAssignee.getValue().getKey());
		modelFilter.setFollowers(cmbFollower.getValue().getKey());

		return modelFilter;
	}
	
	private TaskFilterModel getTaskFilter() {
		TaskFilterModel modelFilter = new TaskFilterModel();

		LocalDate endDate = dpEnd.getValue().equals(LocalDate.now()) ? LocalDate.now().plusDays(1) : dpEnd.getValue();
		
		modelFilter.setKeyword(txtKeyword.getValue());
		modelFilter.setUserid(SessionUtil.getUserId());
		modelFilter.setOrganizationId(SessionUtil.getOrgId());
		modelFilter.setCategorykey(cmbTaskType.getValue().getKey());
		modelFilter.setSubcategorykey(cmbTaskStatus.getValue().getKey());
		modelFilter.setFormdate(LocalDateUtil.localDateToLong(dpStart.getValue()));
		modelFilter.setTodate(LocalDateUtil.localDateToLong(endDate));
		modelFilter.setPriority(cmbPriority.getValue().getKey());
		
		return modelFilter;
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
		});
		
		cmbTaskType.addValueChangeListener(e->{
			if(e.getValue().getKey()==null) {
				cmbOwner.setVisible(true);
				cmbAssignee.setVisible(true);
				cmbFollower.setVisible(true);
			} else {
				switch (e.getValue().getKey()) {
				case "dagiao":
					cmbOwner.setVisible(false);
					cmbAssignee.setVisible(true);
					cmbFollower.setVisible(true);
					break;
				case "duocgiao":
					cmbOwner.setVisible(true);
					cmbAssignee.setVisible(false);
					cmbFollower.setVisible(true);
					break;
				case "theodoi":
					cmbOwner.setVisible(true);
					cmbAssignee.setVisible(true);
					cmbFollower.setVisible(false);
					break;
				}
			}
		});
	}

	public void setForSearching() {
		btnReport.setVisible(false);
		btnDownload.setVisible(false);
	}
	public void setForReporting() {
		txtKeyword.setVisible(false);
		btnSearch.setVisible(false);
		btnDownload.setEnabled(false);
	}
	
	public Button getBtnSearch() {
		return btnSearch;
	}
	public Button getBtnReport() {
		return btnReport;
	}
	public HorizontalLayout gethKeyword() {
		return hKeyword;
	}
	public TextField getTxtKeyword() {
		return txtKeyword;
	}
	public HorizontalLayout gethFilter1() {
		return hFilter1;
	}
	public DatePicker getDpStart() {
		return dpStart;
	}
	public DatePicker getDpEnd() {
		return dpEnd;
	}
	public ComboBox<CustomPairModel<String, String>> getCmbTaskType() {
		return cmbTaskType;
	}
	public ComboBox<CustomPairModel<String, String>> getCmbTaskStatus() {
		return cmbTaskStatus;
	}
	public HorizontalLayout gethFilter2() {
		return hFilter2;
	}
	public ComboBox<CustomPairModel<Integer, String>> getCmbPriority() {
		return cmbPriority;
	}
	public ComboBox<CustomPairModel<String, String>> getCmbOwner() {
		return cmbOwner;
	}
	public ComboBox<CustomPairModel<String, String>> getCmbAssignee() {
		return cmbAssignee;
	}
	public ComboBox<CustomPairModel<String, String>> getCmbFollower() {
		return cmbFollower;
	}
	public Anchor getBtnDownload() {
		return btnDownload;
	}
	public ShortcutRegistration getRegisShortcut() {
		return regisShortcut;
	}
	public String getCurrentPurpose() {
		return currentPurpose;
	}
}
