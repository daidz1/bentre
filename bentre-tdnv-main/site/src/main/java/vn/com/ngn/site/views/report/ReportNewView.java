package vn.com.ngn.site.views.report;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import vn.com.ngn.site.LayoutInterface;
import vn.com.ngn.site.enums.TaskStatusEnum;
import vn.com.ngn.site.enums.TaskTypeEnum;
import vn.com.ngn.site.model.CustomPairModel;
import vn.com.ngn.site.views.main.MainView;
import vn.com.ngn.site.views.report.display.OrgReportLayout;
import vn.com.ngn.site.views.report.display.SelfReportLayout;

@SuppressWarnings("serial")
@Route(value = "reportnew", layout = MainView.class)
@PageTitle("Báo cáo thống kê")
public class ReportNewView extends VerticalLayout implements LayoutInterface {
	private ComboBox<CustomPairModel<String, String>> cmbReportType = new ComboBox<CustomPairModel<String, String>>("Loại báo cáo");

	private HorizontalLayout hFilter = new HorizontalLayout();
	private DatePicker dpStart = new DatePicker("Từ ngày");
	private DatePicker dpEnd = new DatePicker("Đến ngày");

	private ComboBox<CustomPairModel<String, String>> cmbOrgType = new ComboBox<CustomPairModel<String,String>>("Loại đơn vị");
	private ComboBox<CustomPairModel<String, String>> cmbOrgTypeA = new ComboBox<CustomPairModel<String,String>>("Loại đơn vị A");
	private ComboBox<CustomPairModel<String, String>> cmbOrgTypeB = new ComboBox<CustomPairModel<String,String>>("Loại đơn vị B");
	private ComboBox<CustomPairModel<String, String>> cmbOrgTypeC = new ComboBox<CustomPairModel<String,String>>("Loại đơn vị C");
	private ComboBox<CustomPairModel<String, String>> cmbOrgTypeD = new ComboBox<CustomPairModel<String,String>>("Loại đơn vị D");

	private ComboBox<CustomPairModel<String, String>> cmbTaskType = new ComboBox<CustomPairModel<String,String>>("Loại nhiệm vụ");
	private ComboBox<CustomPairModel<String, String>> cmbTaskStatus = new ComboBox<CustomPairModel<String,String>>("Tình trạng");

	private Button btnReport = new Button("Lập báo cáo",VaadinIcon.FILE_FONT.create());
	private Anchor btnDownload = new Anchor("",new Button("Tải về",VaadinIcon.DOWNLOAD.create()));

	private VerticalLayout vDisplay = new VerticalLayout();

	private List<CustomPairModel<String, String>> listOrgType = new ArrayList<CustomPairModel<String,String>>();

	public ReportNewView() {
		buildLayout();
		configComponent();

		initValue();
		controlFilterDisplay();
	}

	private void initValue() {
		/* Init value cmb report type */
		List<CustomPairModel<String, String>> listReportType = new ArrayList<CustomPairModel<String,String>>();
		listReportType.add(new CustomPairModel<String, String>("self-report", "Báo cáo nhiệm vụ của cá nhân"));
		listReportType.add(new CustomPairModel<String, String>("org-report", "Báo cáo nhiệm vụ của đơn vị"));
		listReportType.add(new CustomPairModel<String, String>("quick-report", "Báo cáo nhanh số liệu nhiệm vụ"));

		cmbReportType.setItems(listReportType);
		cmbReportType.setItemLabelGenerator(CustomPairModel<String, String>::getValue);
		cmbReportType.setValue(listReportType.get(0));

		/* Init value filter */
		dpStart.setValue(LocalDate.now().minusDays(30));
		dpEnd.setValue(LocalDate.now());

		List<CustomPairModel<String, String>> listTaskType = new ArrayList<CustomPairModel<String,String>>();
		CustomPairModel<String, String> modelAllType = new CustomPairModel<String, String>(null, "Tất cả");
		listTaskType.add(modelAllType);
		for(TaskTypeEnum eType : TaskTypeEnum.values()) {
			if(!eType.getKey().equals(TaskTypeEnum.GIAOVIECTHAY.getKey()) && !eType.getKey().equals(TaskTypeEnum.THEODOITHAY.getKey())) {
				CustomPairModel<String, String> modelType = new CustomPairModel<String, String>(eType.getKey(), eType.getTitle());

				listTaskType.add(modelType);
			}
		}

		listOrgType.add(new CustomPairModel<String, String>("all", "Tất cả"));
		listOrgType.add(new CustomPairModel<String, String>("org-a", "Loại đơn vị A"));
		listOrgType.add(new CustomPairModel<String, String>("org-b", "Loại đơn vị B"));
		listOrgType.add(new CustomPairModel<String, String>("org-c", "Loại đơn vị C"));
		listOrgType.add(new CustomPairModel<String, String>("org-d", "Loại đơn vị D"));

		cmbOrgType.setItems(listOrgType);
		cmbOrgType.setItemLabelGenerator(CustomPairModel<String, String>::getValue);
		cmbOrgType.setValue(listOrgType.get(0));

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
	}

	@Override
	public void buildLayout() {
		this.add(cmbReportType);
		this.add(hFilter);
		this.add(vDisplay);

		hFilter.add(dpStart,dpEnd);
		hFilter.add(cmbOrgType,cmbOrgTypeA,cmbOrgTypeB,cmbOrgTypeC,cmbOrgTypeD);
		hFilter.add(cmbTaskType,cmbTaskStatus);
		hFilter.add(btnReport,btnDownload);
		hFilter.setDefaultVerticalComponentAlignment(Alignment.END);

		cmbReportType.setHelperText("Chọn loại báo cáo muốn lập");
		cmbReportType.setWidthFull();

		this.setSpacing(false);
	}

	@Override
	public void configComponent() {
		cmbReportType.addValueChangeListener(e->{
			controlFilterDisplay();
			vDisplay.removeAll();
		});

		cmbOrgType.addValueChangeListener(e->{
			controlFilterOrgTypeDisplay();
		});

		btnReport.addClickListener(e->{
			loadReport();
		});
	}

	private void controlFilterDisplay() {
		dpStart.setVisible(false);
		dpEnd.setVisible(false);
		cmbOrgType.setVisible(false);
		cmbOrgTypeA.setVisible(false);
		cmbOrgTypeB.setVisible(false);
		cmbOrgTypeC.setVisible(false);
		cmbOrgTypeD.setVisible(false);
		cmbTaskType.setVisible(false);
		cmbTaskStatus.setVisible(false);

		switch (cmbReportType.getValue().getKey()) {
		case "self-report":
			dpStart.setVisible(true);
			dpEnd.setVisible(true);

			cmbTaskType.setVisible(true);
			cmbTaskStatus.setVisible(true);
			break;
		case "org-report":
			dpStart.setVisible(true);
			dpEnd.setVisible(true);

			cmbOrgType.setVisible(true);
			controlFilterOrgTypeDisplay();

			cmbTaskType.setVisible(true);
			cmbTaskStatus.setVisible(true);
			break;
		case "quick-report":

			break;
		}
	}

	private void controlFilterOrgTypeDisplay() {
		cmbOrgTypeA.setVisible(false);
		cmbOrgTypeB.setVisible(false);
		cmbOrgTypeC.setVisible(false);
		cmbOrgTypeD.setVisible(false);

		switch (cmbOrgType.getValue().getKey()) {
		case "org-a":
			cmbOrgTypeA.setVisible(true);
			break;
		case "org-b":
			cmbOrgTypeB.setVisible(true);
			break;
		case "org-c":
			cmbOrgTypeC.setVisible(true);
			break;
		case "org-d":
			cmbOrgTypeD.setVisible(true);
			break;
		default:
			break;
		}
	}

	private void loadReport() {
		vDisplay.removeAll();

		switch (cmbReportType.getValue().getKey()) {
		case "self-report":
			SelfReportLayout selfReportDisplay = new SelfReportLayout();

			selfReportDisplay.setLdStartDate(dpStart.getValue());
			selfReportDisplay.setLdEndDate(dpEnd.getValue());

			selfReportDisplay.buildReport();

			vDisplay.add(selfReportDisplay);
			break;
		case "org-report":
			OrgReportLayout orgReportDisplay = new OrgReportLayout();

			orgReportDisplay.setLdStartDate(dpStart.getValue());
			orgReportDisplay.setLdEndDate(dpEnd.getValue());

			orgReportDisplay.buildReport();

			vDisplay.add(orgReportDisplay);
			break;
		case "quick-report":

			break;
		}
	}
}
