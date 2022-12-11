package vn.com.ngn.site.views.report.display;

import java.time.LocalDate;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.site.util.LocalDateUtil;

@SuppressWarnings("serial")
public abstract class ReportDisplayLayout extends VerticalLayout {
	protected LocalDate ldStartDate;
	protected LocalDate ldEndDate;
	
	protected long startTime;
	protected long endTime;
	
	protected String getTimeCaption() {
		String date = "Từ ngày "+LocalDateUtil.formatLocalDate(ldStartDate, LocalDateUtil.dateFormater1);
		date+=" đến ngày "+LocalDateUtil.formatLocalDate(ldEndDate, LocalDateUtil.dateFormater1);
		return date;
	}
	
	public abstract void buildReport();
	
	public void setLdStartDate(LocalDate ldStartDate) {
		this.ldStartDate = ldStartDate;
		this.startTime = LocalDateUtil.localDateToLong(ldStartDate);
	}
	public void setLdEndDate(LocalDate ldEndDate) {
		this.ldEndDate = ldEndDate;
		this.endTime = LocalDateUtil.localDateToLong(ldEndDate);
	}
}
