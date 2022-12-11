package vn.com.ngn.site.views.report.excel;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;

import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.vaadin.flow.server.StreamResource;

import vn.com.ngn.site.util.ConfigurationUtil;

public class ExcelExport {
	protected XSSFWorkbook workbook;
	protected XSSFRow row;
	protected XSSFCell cell;
	protected CellCopyPolicy cellPolicy = new CellCopyPolicy();
	
	protected LocalDate startDate;
	protected LocalDate endDate;
	
	protected String templatePath = ConfigurationUtil.getProperty("report.template.url");
	
	public ExcelExport() {
		cellPolicy.createBuilder().cellStyle(true);
		cellPolicy.createBuilder().cellValue(false);
	}
	
	public StreamResource getStreamResource(String filename, byte[] content) {
        return new StreamResource(filename,
                () -> new ByteArrayInputStream(content));
    }
	
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
}
