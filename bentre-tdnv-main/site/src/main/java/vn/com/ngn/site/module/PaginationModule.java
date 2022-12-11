package vn.com.ngn.site.module;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextFieldVariant;

import vn.com.ngn.site.LayoutInterface;

@SuppressWarnings("serial")
@CssImport("/themes/site/components/pagination.css")
public class PaginationModule extends VerticalLayout implements LayoutInterface{
	private HorizontalLayout hMain = new HorizontalLayout();

	private HorizontalLayout hLeft = new HorizontalLayout();
	private Span textItem1 = new Span("Hiển thị");
	private ComboBox<Integer> cmbItem = new ComboBox<Integer>();
	private Span textItem2 = new Span("dòng");
	private HorizontalLayout hTotal = new HorizontalLayout();
	private Span textTotal1 = new Span("Tổng số");
	private Span textTotal = new Span("0");
	private Span textTotal2 = new Span("nhiệm vụ");

	private HorizontalLayout hRight = new HorizontalLayout();
	private Button btnFirst = new Button(VaadinIcon.ANGLE_DOUBLE_LEFT.create());
	private Button btnPrevious = new Button(VaadinIcon.ANGLE_LEFT.create());
	private Button btnNext = new Button(VaadinIcon.ANGLE_RIGHT.create());
	private Button btnLast = new Button(VaadinIcon.ANGLE_DOUBLE_RIGHT.create());
	private IntegerField txtPageNumber = new IntegerField();
	private Span spanSlash = new Span("/");
	private Span spanPage = new Span();
 
	private Button btnTrigger = new Button();

	private int itemPerPage = 0;
	private int currentPage = 1;
	private int maxPage = 0;
	private int skip = 0;
	private int limit = 0;

	private int itemCount = 0;

	public PaginationModule() {
		initValue();

		buildLayout();
		configComponent();
	}

	private void initValue() {
		List<Integer> listItemCount = new ArrayList<Integer>();
		listItemCount.add(5);
		listItemCount.add(10);
		listItemCount.add(20);
		listItemCount.add(30);
		listItemCount.add(40);

		cmbItem.setItems(listItemCount);
		cmbItem.setValue(5);

		txtPageNumber.setValue(1);
	}

	@Override
	public void buildLayout() {
		this.add(hMain,btnTrigger);
		btnTrigger.setVisible(false);

		hMain.add(hLeft,hRight);

		hMain.expand(hLeft);
		hMain.setWidthFull();

		this.setWidthFull();
		this.setPadding(false);

		buildLeft();
		buildRight();
	}

	@Override
	public void configComponent() {
		cmbItem.addValueChangeListener(e->{
			reCalculate();
		});

		btnFirst.addClickListener(e->{
			int page = 1;
			txtPageNumber.setValue(page);
			currentPage = page;

			calculateSkipLimit();
		});

		btnPrevious.addClickListener(e->{
			int page = currentPage > 1 ? currentPage -1 : 1;
			txtPageNumber.setValue(page);
			currentPage = page;

			calculateSkipLimit();
		});

		btnNext.addClickListener(e->{
			int page = currentPage < maxPage ? currentPage + 1 : maxPage;
			txtPageNumber.setValue(page);
			currentPage = page;

			calculateSkipLimit();
		});

		btnLast.addClickListener(e->{
			int page = maxPage;
			txtPageNumber.setValue(page);
			currentPage = page;

			calculateSkipLimit();
		});

		txtPageNumber.addKeyDownListener(Key.ENTER, e->{
			int value = txtPageNumber.getValue();
			if(value < 0)
				value = 1;
			else if(value > maxPage)
				value = maxPage;
			txtPageNumber.setValue(value);

			currentPage = value;
			calculateSkipLimit();
		});

		btnTrigger.addClickListener(e->{
			//System.out.println("triggerd");
		});
	}

	private void buildLeft() {
		hLeft.add(textItem1,cmbItem,textItem2,hTotal);

		textItem1.getStyle().set("font-size", "var(--lumo-font-size-s)");
		textItem2.getStyle().set("font-size", "var(--lumo-font-size-s)");

		cmbItem.setWidth("80px");

		hTotal.add(textTotal1,textTotal,textTotal2);

		textTotal1.getStyle().set("font-size", "var(--lumo-font-size-s)");
		textTotal.getStyle().set("font-size", "var(--lumo-font-size-m)");
		textTotal2.getStyle().set("font-size", "var(--lumo-font-size-s)");

		hTotal.addClassName("total-display");
		hTotal.setDefaultVerticalComponentAlignment(Alignment.END);

		hLeft.getStyle().set("margin-left", "20px");

		hLeft.setDefaultVerticalComponentAlignment(Alignment.CENTER);
	}

	private void buildRight() {
		hRight.add(btnFirst,btnPrevious,txtPageNumber,spanSlash,spanPage,btnNext,btnLast);

		txtPageNumber.setMin(1);
		txtPageNumber.setWidth("80px");
		txtPageNumber.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);

		spanSlash.getStyle().set("font-size", "var(--lumo-font-size-s)");
		spanPage.getStyle().set("font-size", "var(--lumo-font-size-s)");

		hRight.setDefaultVerticalComponentAlignment(Alignment.CENTER);
	}

	public void reCalculate() {
		itemPerPage = cmbItem.getValue();
		maxPage = (itemCount % itemPerPage) == 0 ? itemCount / itemPerPage : (itemCount / itemPerPage) + 1;
		currentPage = currentPage > maxPage ? maxPage : currentPage;

		if(currentPage==0)
			currentPage = 1;
		calculateSkipLimit();
		txtPageNumber.setValue(currentPage);
		txtPageNumber.setMax(maxPage);
		spanPage.setText(String.valueOf(maxPage));
		textTotal.setText(String.valueOf(itemCount));
	}

	private void calculateSkipLimit() {
		skip = currentPage == 1 ? 0 :(currentPage - 1) * itemPerPage;
		limit = itemPerPage;

		btnTrigger.click();

		//		System.out.println("ItemPerPage: "+itemPerPage);
		//		System.out.println("MaxPage: "+maxPage);
		//		System.out.println("CurrentPage: "+currentPage);
		//		System.out.println("Skip: "+skip);
		//		System.out.println("Limit: "+limit);
	}

	public Button getBtnTrigger() {
		return btnTrigger;
	}
	public int getSkip() {
		return skip;
	}
	public int getLimit() {
		return limit;
	}
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}
	public Span getTextTotal2() {
		return textTotal2;
	}
}
