package vn.com.ngn.site.views.doclist;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
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
import vn.com.ngn.site.dialog.DialogTemplate;
import vn.com.ngn.site.enums.DocOfEnum;
import vn.com.ngn.site.enums.DocTypeEnum;
import vn.com.ngn.site.enums.NotificationTypeEnum;
import vn.com.ngn.site.enums.PermissionEnum;
import vn.com.ngn.site.form.DocCreateForm;
import vn.com.ngn.site.model.DocFilterModel;
import vn.com.ngn.site.model.doccreate.DocModel;
import vn.com.ngn.site.module.PaginationModule;
import vn.com.ngn.site.util.LocalDateUtil;
import vn.com.ngn.site.util.SessionUtil;
import vn.com.ngn.site.util.component.NotificationUtil;
import vn.com.ngn.site.util.service.DocServiceUtil;
import vn.com.ngn.site.views.main.MainView;

@SuppressWarnings("serial")
@Route(value = "doclist", layout = MainView.class)
@PageTitle("Danh sách văn bản")
public class DocListView extends VerticalLayout implements LayoutInterface,BeforeEnterObserver,RouterLayout{
	private VerticalLayout vSearch;
	private HorizontalLayout hKeyword;
	private TextField txtKeyword;
	private HorizontalLayout hFilter1 = new HorizontalLayout();
	private Button btnSearch;

	private PaginationModule pagination;

	private VerticalLayout vTaskList;

	private DocTypeEnum eType;
	private DocOfEnum eOf;

	private ShortcutRegistration regisShortcut = null;
	//Dzung code
	private Button cmdAdd;

	//end Dzung code

	public DocListView() {

	}

	@Override
	public void buildLayout() {
		vSearch = new VerticalLayout();
		hKeyword = new HorizontalLayout();
		txtKeyword = new TextField("Từ khóa");
		btnSearch = new Button("Tìm kiếm",VaadinIcon.SEARCH.create());
		hFilter1 = new HorizontalLayout();

		vTaskList = new VerticalLayout();
		//Dzung code
		cmdAdd = new Button("Thêm văn bản",new Icon(VaadinIcon.PLUS));
		//end Dzung code

		this.add(vSearch);
		this.add(pagination);
		this.add(vTaskList);
		
		pagination.getTextTotal2().setText("văn bản");

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
				reCount(getDocFilter());
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
		
		//Dzung code
		if(SessionUtil.isHasPermission(PermissionEnum.themvanban)) {
			cmdAdd.setVisible(true);
		}else {
			cmdAdd.setVisible(false);
		}
		
		cmdAdd.addClickListener(ee->{
			
			DialogTemplate dialog = new DialogTemplate();
			dialog.setCaption(new H4("Thêm văn bản"));
			dialog.buildLayout();
			dialog.configComponent();
			
			DocCreateForm form = new DocCreateForm(null, this.eType);
			form.setDialog(dialog);
			dialog.add(form);
			dialog.open();
			dialog.addOpenedChangeListener(e->{
				if(dialog.isOpened()==false) {
					try {
						loadData();
						reCount(getDocFilter());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
			
			
		});
		//end Dzung code
		
	}

	private void buildSearchLayout() {
		vSearch.add(hKeyword);
		vSearch.add(hFilter1);

		//keyword
		hKeyword.add(txtKeyword,btnSearch,cmdAdd);

		txtKeyword.setPlaceholder("Nhập vào từ khóa của văn bản để tìm kiếm...");

		hKeyword.expand(txtKeyword);
		hKeyword.setWidthFull();
		hKeyword.setDefaultVerticalComponentAlignment(Alignment.END);

		//filter
		hFilter1.add();

		hFilter1.setWidthFull();
		hFilter1.setDefaultVerticalComponentAlignment(Alignment.END);

		vSearch.setPadding(false);
		vSearch.setSpacing(false);
	}

	private void loadData() throws IOException {
		System.out.println("=====DocListView loadData=====");
		vTaskList.removeAll();

		JsonObject jsonResponse = DocServiceUtil.getDocList(getDocFilter());

		if(jsonResponse.get("status").getAsInt()==200) {
			JsonArray jsonTaskList = jsonResponse.get("result").getAsJsonArray();

			for(JsonElement jsonTask : jsonTaskList) {
				vTaskList.add(new DocBlockLayout(jsonTask.getAsJsonObject(),eType.getKey(),eOf.toString()));
			}
		} else {
			System.out.println(jsonResponse);
			NotificationUtil.showNotifi("Có lỗi xảy ra, vui lòng thử lại", NotificationTypeEnum.ERROR);
		}
	}

	private DocFilterModel getDocFilter() {
		DocFilterModel modelFilter = new DocFilterModel();

		modelFilter.setKeyword(txtKeyword.getValue());
		modelFilter.setSkip(pagination.getSkip());
		modelFilter.setLimit(pagination.getLimit());
		modelFilter.setDocCategory(this.eType.getKey());
		modelFilter.setFormdate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getStartOfTheYear(SessionUtil.getYear())));
		modelFilter.setTodate(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(SessionUtil.getYear())));

		if(eOf.equals(DocOfEnum.ALL)) {
			modelFilter.setAccountDomino(PermissionEnum.xemvanban.toString());
		} else if(eOf.equals(DocOfEnum.SELF)){
			modelFilter.setAccountDomino(SessionUtil.getUser().getAccountDomino());
		}

		return modelFilter;
	}

	private void reCount(DocFilterModel modelFilter) {
		try {
			JsonObject jsonResponse = DocServiceUtil.getCountDocList(modelFilter);
			System.out.println(eType.getKey()+"- "+eOf);
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

	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		for(DocTypeEnum eType : DocTypeEnum.values()) {
			if(SessionUtil.getParam().get("type").equals(eType.getKey())) {
				this.eType = eType;
			}
		}
		for(DocOfEnum eOf : DocOfEnum.values()) {
			if(SessionUtil.getParam().get("of").equals(eOf.toString())) {
				this.eOf = eOf;
			}
		}

		this.removeAll();
		pagination = new PaginationModule();

		buildLayout();
		initValue();

		reCount(getDocFilter());
		configComponent();
	}
}
