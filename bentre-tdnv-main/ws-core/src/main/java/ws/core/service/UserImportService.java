package ws.core.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ws.core.model.object.UserImportRaw;

// TODO: Auto-generated Javadoc
/**
 * The Class UserImportService.
 */
@Service
public class UserImportService {
	
	/** The file location service. */
	@Autowired
	protected FileLocationService fileLocationService;
	
	/**
	 * Read file from excel.
	 *
	 * @param pathFile the path file
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InvalidFormatException the invalid format exception
	 */
	public List<UserImportRaw> readFileFromExcel(String pathFile) throws IOException, InvalidFormatException{
		List<UserImportRaw> userImportRaws=new ArrayList<>();
		
		XSSFWorkbook workbook = new XSSFWorkbook(new File(fileLocationService.getPathAttachments()+ File.separator + pathFile));
        XSSFSheet sheet = workbook.getSheetAt(0);
        
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if(row.getRowNum()>=1) {
            	String username=null;
            	String fullname=null;
            	String jobTitle=null;
            	String officePhone=null;
            	String employeeID=null;
            	String emailAddress=null;
            	String description=null;
            	String organizationUnit=null;
            	String groups=null;
            	boolean enabled = true;
            	String password=null;
            	
            	
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    
                    if(cell.getColumnIndex()==0) {
                    	username=row.getCell(0).getStringCellValue();
                    	if(username.isEmpty()) {
                    		break;
                    	}
                    }
                    
                    if(cell.getColumnIndex()==1) {
                    	fullname=cell.getStringCellValue();
                    }
                    
                    if(cell.getColumnIndex()==2) {
                    	jobTitle=cell.getStringCellValue();
                    }
                    
                    if(cell.getColumnIndex()==3) {
                    	officePhone=cell.getStringCellValue();
                    }
                    
                    if(cell.getColumnIndex()==4) {
                    	employeeID=cell.getStringCellValue();
                    }
                    
                    if(cell.getColumnIndex()==5) {
                    	emailAddress=cell.getStringCellValue();
                    }
                    
                    if(cell.getColumnIndex()==6) {
                    	description=cell.getStringCellValue();
                    }
                    
                    if(cell.getColumnIndex()==7) {
                    	organizationUnit=cell.getStringCellValue();
                    }
                    
                    if(cell.getColumnIndex()==8) {
                    	groups=cell.getStringCellValue();
                    }
                    
                    if(cell.getColumnIndex()==9) {
                    	enabled=cell.getBooleanCellValue();
                    }
                    
                    if(cell.getColumnIndex()==10) {
                    	password=cell.getStringCellValue();
                    }
                }
                
                UserImportRaw userImportRaw=new UserImportRaw();
            	userImportRaw.setUsername(username);
            	userImportRaw.setFullname(fullname);
            	userImportRaw.setJobTitle(jobTitle);
            	userImportRaw.setOfficePhone(officePhone);
            	userImportRaw.setEmployeeID(employeeID);
            	userImportRaw.setEmailAddress(emailAddress);
            	userImportRaw.setDescription(description);
            	userImportRaw.setOrganizationUnit(organizationUnit);
            	userImportRaw.setGroups(groups);
            	userImportRaw.setEnabled(enabled);
            	userImportRaw.setPassword(password);
                
            	userImportRaws.add(userImportRaw);
            }
        }
        workbook.close();
        
		return userImportRaws;
	}
	
	
	/**
	 * Gets the organization name order by root list.
	 *
	 * @param organizations the organizations, exampe: OU=Phòng công chức, viên chức,OU=Sở Nội vụ,OU=UBND Tỉnh Bến Tre
	 * @return the organization name order by root list
	 */
	public LinkedList<String> getOrganizationNameOrderByRootList(String organizations) {
		LinkedList<String> result=new LinkedList<String>();
		int count=0;
		String[] arraySplit=organizations.split("=");
 		for (String string : arraySplit) {
			if(count==0) {
				count++;
				continue;
			}
			
			/* Bỏ ,OU hoặc , OU */
			String organizationName=string.replaceAll(",OU", "");
			organizationName=organizationName.replaceAll(", OU", "");
			
			/* Lật lại vị trí */
			result.add(0, organizationName);
			
			count++;
		}
		return result;
	}
}
