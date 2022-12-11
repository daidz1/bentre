package vn.com.ngn.site.views.report.excel;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.vaadin.flow.server.StreamResource;

import vn.com.ngn.site.model.ReportModel;
import vn.com.ngn.site.util.LocalDateUtil;

public class ReportDaGiaoExcel extends ExcelExport{
	private String template = templatePath+"dagiao.xlsx";
	private List<ReportModel> listData = new ArrayList<ReportModel>();
	
	public StreamResource createReport() throws Exception
	{	
		FileInputStream fileExcelStream = new FileInputStream(template);
		workbook = new XSSFWorkbook(fileExcelStream);
		createSheet();

		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		workbook.write(byteOutputStream);
		
		workbook.close();

		return getStreamResource("Nhiem vu da giao-"+new Date().getTime()+".xlsx", byteOutputStream.toByteArray());
	}
	
	public void createSheet() throws XPathExpressionException {
		XSSFSheet sheetSum = workbook.getSheet("dagiao");
		row = sheetSum.getRow(0);
		cell = row.getCell(0);
		 
		row = sheetSum.getRow(1);
		cell = row.getCell(0);
		cell.setCellValue("(Tính từ ngày "+LocalDateUtil.formatLocalDate(startDate,LocalDateUtil.dateFormater1)+" đến ngày "+LocalDateUtil.formatLocalDate(endDate,LocalDateUtil.dateFormater1)+")");
		
		int indexRow = 4;
		int startDataRow = indexRow;
		
		XSSFRow rowTemplate = sheetSum.getRow(indexRow);
		
		int stt = 1;
		for(ReportModel model : listData) {
			if(indexRow==startDataRow)
			{
				row = rowTemplate;
				indexRow++;
			}  
			else
			{  
				row = sheetSum.createRow(indexRow++);
				row.copyRowFrom(rowTemplate, cellPolicy);
			}
			row.setHeight((short)-1);
			cell = row.getCell(0);
			cell.setCellValue(stt++);
			cell = row.getCell(1);
			cell.setCellValue(model.getTrichYeu()+"\n"+model.getNoiDung());
			cell = row.getCell(2);
			cell.setCellValue(model.getNguoiXuLy());
			cell = row.getCell(3);
			cell.setCellValue(model.getNguoiHoTro());
			cell = row.getCell(4);
			cell.setCellValue(model.getNgayGiao());
			cell = row.getCell(5);
			cell.setCellValue(model.getHanXuLy());
			cell = row.getCell(6);
			cell.setCellValue(model.getTinhTrangXuLy());
			cell = row.getCell(7);
			cell.setCellValue(model.getKetQua());
		}
	}

	public void setListData(List<ReportModel> listData) {
		this.listData = listData;
	}
}
