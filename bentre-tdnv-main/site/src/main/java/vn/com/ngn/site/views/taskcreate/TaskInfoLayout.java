package vn.com.ngn.site.views.taskcreate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.enums.TaskPriorityEnum;
import vn.com.ngn.site.model.CustomPairModel;
import vn.com.ngn.site.model.UploadModuleDataWithDescriptionModel;
import vn.com.ngn.site.model.taskcreate.TaskInfoCreateModel;
import vn.com.ngn.site.module.upload.UploadModuleWithDescription;
import vn.com.ngn.site.util.LocalDateUtil;
import vn.com.ngn.site.util.component.NotificationUtil;

@SuppressWarnings("serial")
public class TaskInfoLayout extends VerticalLayout implements LayoutInterface {
	private TextField txtTaskTitle = new TextField("Tiêu đề nhiệm vụ *");
	private TextArea txtTaskDescription = new TextArea("Nội dung nhiệm vụ *");
	private ComboBox<CustomPairModel<Integer, String>> cmbPriority = new ComboBox<CustomPairModel<Integer,String>>("Độ khẩn *");
	private DateTimePicker dpCreateTime = new DateTimePicker("Ngày nhập");
	private Board boardEndtime = new Board();
	private DatePicker dpEndTime = new DatePicker("Hạn xử lý");
	private TimePicker tpEndTime = new TimePicker("-");
	//private DateTimePicker dpEndTime = new DateTimePicker("Hạn xử lý *");
	private UploadModuleWithDescription upload = new UploadModuleWithDescription();
	
	private List<CustomPairModel<Integer, String>> listPriority = new ArrayList<CustomPairModel<Integer, String>>();
	
	private JsonObject jsonInfo;
	
	public TaskInfoLayout(JsonObject jsonInfo) {
		this.jsonInfo = jsonInfo;
		
		initValueForm();
		
		buildLayout();
		configComponent();
		
		if(jsonInfo!=null)
			initOldValue();
	}
	
	public void initValueForm() {
		dpCreateTime.setValue(LocalDateTime.now());
		dpCreateTime.setEnabled(false);
		CustomPairModel<Integer, String> modelSelect = null; 
		for(TaskPriorityEnum e : TaskPriorityEnum.values()) {
			CustomPairModel<Integer, String> modelPriority = new CustomPairModel<Integer, String>(e.getKey(), e.getCaption());
			listPriority.add(modelPriority);
			
			if(e.getKey()==1) {
				modelSelect = modelPriority;
			}
		}
		
		cmbPriority.setItems(listPriority);
		cmbPriority.setItemLabelGenerator(CustomPairModel<Integer, String>::getValue);
		cmbPriority.setValue(modelSelect);
	}
	
	public void initOldValue() {
		String title = jsonInfo.get("title").getAsString();
		String description = jsonInfo.get("description").getAsString();
		int priority = jsonInfo.get("priority").getAsInt();
		LocalDateTime startTime = LocalDateUtil.longToLocalDateTime(jsonInfo.get("createdTime").getAsLong());
		LocalDateTime endTime = LocalDateUtil.longToLocalDateTime(jsonInfo.get("endTime").getAsLong());
	
		txtTaskTitle.setValue(title);
		txtTaskDescription.setValue(description);
		
		for(CustomPairModel<Integer, String> modelPrio : listPriority) {
			if(modelPrio.getKey()==priority) {
				cmbPriority.setValue(modelPrio);
				break;
			}
		}
		
		dpCreateTime.setValue(startTime);
		if(jsonInfo.get("endTime").getAsLong()!=0) {
			LocalDate dateEnd = endTime.toLocalDate();
			LocalTime timeEnd = endTime.toLocalTime();
			tpEndTime.setValue(timeEnd);
			dpEndTime.setValue(dateEnd);
		}
	}
	
	public void initValueForm(TaskInfoCreateModel modelTaskInfo) {
		if(modelTaskInfo==null)
			return;
		txtTaskTitle.setValue(modelTaskInfo.getTitle());
		txtTaskDescription.setValue(modelTaskInfo.getDescription());
		
		for(CustomPairModel<Integer, String> modelPrio : listPriority) {
			if(modelPrio.getKey()==modelTaskInfo.getPriority()) {
				cmbPriority.setValue(modelPrio);
				break;
			}
		}
		dpCreateTime.setValue(modelTaskInfo.getCreatetime());
		if(modelTaskInfo.getEndTime()!=null) {
			dpEndTime.setValue(modelTaskInfo.getEndTime().toLocalDate());
			tpEndTime.setValue(modelTaskInfo.getEndTime().toLocalTime());
		}
	}
	
	@Override
	public void buildLayout() {
		this.add(txtTaskTitle);
		this.add(txtTaskDescription);
		this.add(cmbPriority);
		this.add(dpCreateTime);
		this.add(boardEndtime);
		this.add(upload);
		
		txtTaskTitle.setWidthFull();
		txtTaskDescription.setWidthFull();
		cmbPriority.setWidthFull();
		dpCreateTime.setWidthFull();
		dpEndTime.setWidthFull();
		tpEndTime.setWidthFull();
		upload.setWidth("100%");
		
		txtTaskDescription.setHeight("150px");
		
		dpCreateTime.setLocale(new Locale("vi", "VN"));
		
		boardEndtime.add(new Row(dpEndTime,tpEndTime));
		
		boardEndtime.setWidthFull();
		
		tpEndTime.setStep(Duration.ofMinutes(30));
		dpEndTime.setMin(LocalDate.now()); 
		dpEndTime.setLocale(new Locale("vi", "VN"));
		tpEndTime.setLocale(new Locale("vi", "VN"));
		
		tpEndTime.getStyle().set("padding-left", "2px");
		
		this.setWidthFull();
		this.setPadding(false);
		
		configUpload();
	}

	@Override
	public void configComponent() {
		dpEndTime.addValueChangeListener(e->{
			if(tpEndTime.getValue()==null) {
				String strCurrentHour = LocalTime.now().getHour()+3 < 10 ? "0"+(LocalTime.now().getHour()+3) : LocalTime.now().getHour()+3 >= 24 ? "0"+(LocalTime.now().getHour()+3-24): ""+(LocalTime.now().getHour()+3);
				tpEndTime.setValue(LocalTime.parse(strCurrentHour+":00"));
			}
		});
	}
	
	private void configUpload() {
		upload.setMultiFile(true);
		upload.initUpload();
	}
	
	public boolean validateForm() {
		if(txtTaskTitle.isEmpty()) {
			txtTaskTitle.focus();
			
			NotificationUtil.showNotifi("Tiêu đề nhiệm vụ không được để trống", NotificationTypeEnum.WARNING);
			return false;
		}
		if(txtTaskDescription.isEmpty()) {
			txtTaskDescription.focus();
			
			NotificationUtil.showNotifi("Nội dung nhiệm vụ không được để trống", NotificationTypeEnum.WARNING);
			return false;
		}
		if((dpEndTime.getValue()!=null && tpEndTime.getValue()==null) || (dpEndTime.getValue()==null && tpEndTime.getValue()!=null)) {
			dpEndTime.focus();
			
			NotificationUtil.showNotifi("Vui lòng nhập đủ ngày giờ của hạn xử lý, để trống cả 2 nếu là nhiệm vụ không hạn.", NotificationTypeEnum.WARNING);
			return false;
		}
		if(dpEndTime.getValue()!=null && tpEndTime.getValue()!=null) {
			if(jsonInfo==null) {
				if(dpEndTime.getValue().equals(LocalDate.now()) && tpEndTime.getValue().isBefore(LocalTime.now())) {
					tpEndTime.focus();
					
					NotificationUtil.showNotifi("Hạn xử lý không thể nhỏ hơn thời gian hiện tại.", NotificationTypeEnum.WARNING);
					return false;
				}
			} else {
				if(jsonInfo.get("endTime").getAsLong()==0) {
					if(dpEndTime.getValue().equals(LocalDate.now()) && tpEndTime.getValue().isBefore(LocalTime.now())) {
						tpEndTime.focus();
						
						NotificationUtil.showNotifi("Hạn xử lý không thể nhỏ hơn thời gian hiện tại.", NotificationTypeEnum.WARNING);
						return false;
					}
				} else {
					LocalDateTime endTimeOld = LocalDateUtil.longToLocalDateTime(jsonInfo.get("endTime").getAsLong());
					
					if(LocalDateTime.of(dpEndTime.getValue(), tpEndTime.getValue()).isBefore(LocalDateTime.now())) {
						if(LocalDateTime.of(dpEndTime.getValue(), tpEndTime.getValue()).isBefore(endTimeOld)) {
							tpEndTime.focus();
							
							NotificationUtil.showNotifi("Nhiệm vụ đã quá hạn không thể cập nhật hạn xử lý bé hơn hạn xử lý hiện tại.", NotificationTypeEnum.WARNING);
							return false;
						}
					}
				}
			}
		}
		
		return true;
	}
	
	public TaskInfoCreateModel getFormData() {
		TaskInfoCreateModel modelTask = new TaskInfoCreateModel();
		
		String title = txtTaskTitle.getValue().trim();
		String description = txtTaskDescription.getValue().trim();
		int priority = cmbPriority.getValue().getKey();
		LocalDateTime createTime = dpCreateTime.getValue();
		LocalDateTime endTime = dpEndTime.getValue()!=null ? LocalDateTime.of(dpEndTime.getValue(), tpEndTime.getValue()) : null;
		List<UploadModuleDataWithDescriptionModel> listFileUpload = upload.getListFileUpload();
		
		modelTask.setTitle(title);
		modelTask.setDescription(description);
		modelTask.setPriority(priority);
		modelTask.setCreatetime(createTime);
		modelTask.setEndTime(endTime);
		modelTask.setListFileUpload(listFileUpload);
		
		return modelTask;
	}

	public TextField getTxtTaskTitle() {
		return txtTaskTitle;
	}
	public TextArea getTxtTaskDescription() {
		return txtTaskDescription;
	}
	public ComboBox<CustomPairModel<Integer, String>> getCmbPriority() {
		return cmbPriority;
	}
	public DateTimePicker getDpCreateTime() {
		return dpCreateTime;
	}
	public Board getBoardEndtime() {
		return boardEndtime;
	}
	public DatePicker getDpEndTime() {
		return dpEndTime;
	}
	public TimePicker getTpEndTime() {
		return tpEndTime;
	}
	public UploadModuleWithDescription getUpload() {
		return upload;
	}
}
