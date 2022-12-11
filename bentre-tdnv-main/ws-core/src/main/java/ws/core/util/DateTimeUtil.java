package ws.core.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateTimeUtil{
	
	public static SimpleDateFormat getDateFormatToSQL() {
		return new SimpleDateFormat("yyyy-MM-dd");
	}
	public static SimpleDateFormat getDatetimeFormat() {
		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	}
	public static SimpleDateFormat getDateFormat() {
		return new SimpleDateFormat("dd/MM/yyyy");
	}
	public static SimpleDateFormat getTimeFormat() {
		return new SimpleDateFormat("HH:mm:ss");
	}
	public static SimpleDateFormat getDatetimeFormatToSQL() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	public static SimpleDateFormat getTimedateFormat() {
		return new SimpleDateFormat("HH:mm - dd/MM/yyyy");
	}
	public static SimpleDateFormat getMonthdayyearFormat() {
		return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	}
	public static SimpleDateFormat getDateFolder() {
		return new SimpleDateFormat("yyyy/MM/dd");
	}
	public static String distanceDateBefore(Date date){
		Calendar now=Calendar.getInstance();
		Calendar atmoment=Calendar.getInstance();
		atmoment.setTime(date);
		int year=now.get(Calendar.YEAR)-atmoment.get(Calendar.YEAR);
		int month=now.get(Calendar.MONTH)-atmoment.get(Calendar.MONTH);
		int day=now.get(Calendar.DAY_OF_MONTH)-atmoment.get(Calendar.DAY_OF_MONTH);
		int hour=now.get(Calendar.HOUR_OF_DAY)-atmoment.get(Calendar.HOUR_OF_DAY);
		int minutes=now.get(Calendar.MINUTE)-atmoment.get(Calendar.MINUTE);
		if(year>0){
			return year+" năm trước";
		}else if(month>0){
			return month+" tháng trước";
		}else if(day>0){
			return day+" ngày trước";
		}else if(hour>0){
			return hour+" giờ trước";
		}else if(minutes>0){
			return minutes+" phút trước";
		}
		return "Mới đây";
	}
	
	
	public static int getYearAttmoment() {
		Calendar attmoment=Calendar.getInstance();
		return attmoment.get(Calendar.YEAR);
	}
	
	public static int getMonthAttmoment() {
		Calendar attmoment=Calendar.getInstance();
		return attmoment.get(Calendar.MONTH);
	}
	
	public static Date getDueDate_after(int numberDay) {
		Calendar dueDate=Calendar.getInstance();
		dueDate.add(Calendar.DATE, numberDay);
		return dueDate.getTime();
	}
	
	public static Date getStartDate_before(int numberDay) {
		Calendar dueDate=Calendar.getInstance();
		dueDate.add(Calendar.DATE, -numberDay);
		return dueDate.getTime();
	}
	
	public static List<Date> getTatCa(){
		List<Date> result=new ArrayList<Date>();
		int nam=Calendar.getInstance().get(Calendar.YEAR);
		Calendar batdau=Calendar.getInstance();
		Calendar ketthuc=Calendar.getInstance();
		batdau.set(2000, 0, 1, 0, 0, 0);
		ketthuc.set(nam, 11, 31, 23, 59, 59);
		result.add(batdau.getTime());
		result.add(ketthuc.getTime());
		return result;
	}
	
	public static List<Date> getNamAt_NumberYear(int numberYear){
		List<Date> result=new ArrayList<Date>();
		Calendar batdau=Calendar.getInstance();
		Calendar ketthuc=Calendar.getInstance();
		batdau.set(numberYear, 0, 1, 0, 0, 0);
		ketthuc.set(numberYear, 11, 31, 23, 59, 59);
		result.add(batdau.getTime());
		result.add(ketthuc.getTime());
		return result;
	}
	
	public static List<Date> getNamAtMoment(){
		List<Date> result=new ArrayList<Date>();
		int nam=Calendar.getInstance().get(Calendar.YEAR);
		Calendar batdau=Calendar.getInstance();
		Calendar ketthuc=Calendar.getInstance();
		batdau.set(nam, 0, 1, 0, 0, 0);
		ketthuc.set(nam, 11, 31, 23, 59, 59);
		result.add(batdau.getTime());
		result.add(ketthuc.getTime());
		return result;
	}
	
	public static List<Date> getQuyAtMoment(){
		List<Date> result=new ArrayList<Date>();
		int nam=Calendar.getInstance().get(Calendar.YEAR);
		int thang=Calendar.getInstance().get(Calendar.MONTH);
		Calendar batdau=Calendar.getInstance();
		batdau.set(nam, 0, 1, 0, 0, 0);
		Calendar ketthuc=Calendar.getInstance();
		ketthuc.set(nam, 2, 31, 23, 0, 0);
		switch(thang){
			case 0: case 1: case 2:{
				batdau.set(nam, 0, 1, 0, 0, 0);
				ketthuc.set(nam, 2, 31, 23, 59, 59);
				break;
			}
			case 3: case 4: case 5:{
				batdau.set(nam, 3, 1, 0, 0, 0);
				ketthuc.set(nam, 5, 30, 23, 59, 59);
				break;
			}
			case 6: case 7: case 8:{
				batdau.set(nam, 6, 1, 0, 0, 0);
				ketthuc.set(nam, 8, 30, 23, 59, 59);
				break;
			}
			case 9: case 10: case 11:{
				batdau.set(nam, 9, 1, 0, 0, 0);
				ketthuc.set(nam, 11, 31, 23, 59, 59);
				break;
			}
		}
		result.add(batdau.getTime());
		result.add(ketthuc.getTime());
		return result;
	}
	
	public static List<Date> getThangAtMoment(){
		List<Date> result=new ArrayList<Date>();
		int nam=Calendar.getInstance().get(Calendar.YEAR);
		int thang=Calendar.getInstance().get(Calendar.MONTH);
		int ngayket=28;
		switch(thang){
			case 0: case 2:	case 4: case 6: case 7: case 9: case 11:{
				ngayket=31;
				break;
			}
			case 3: case 5:	case 8: case 10:{
				ngayket=30;
				break;
			}
			case 1:{
				if(namNhuan(Calendar.YEAR))
					ngayket=29;
				else 
					ngayket=28;
				break;
			}
		}
		Calendar batdau=Calendar.getInstance();
		Calendar ketthuc=Calendar.getInstance();
		batdau.set(nam, thang, 1, 0, 0, 0);
		ketthuc.set(nam, thang, ngayket, 23, 59, 59);
		result.add(batdau.getTime());
		result.add(ketthuc.getTime());
		return result;
	}
	
	private static boolean namNhuan(int year){
		if (((year % 4 == 0) && (year % 100!= 0)) || (year%400 == 0))
			return true;
		return false;
	}
	
	public static List<Date> getTuanAtMoment(){
		List<Date> result=new ArrayList<Date>();
		
		Calendar batdau=Calendar.getInstance(TimeZone.getDefault());
		int ngaytrongtuan=batdau.get(Calendar.DAY_OF_WEEK)-1;
		
		batdau.set(batdau.get(Calendar.YEAR), batdau.get(Calendar.MONTH), batdau.get(Calendar.DATE), 0, 0, 0);
		batdau.add(Calendar.DATE, -ngaytrongtuan);
		
		Calendar ketthuc=Calendar.getInstance(TimeZone.getDefault());
		ketthuc.set(ketthuc.get(Calendar.YEAR), ketthuc.get(Calendar.MONTH), ketthuc.get(Calendar.DATE), 23, 59, 59);
		ketthuc.add(Calendar.DATE, 7);
		
		result.add(batdau.getTime());
		result.add(ketthuc.getTime());
		return result;
	}
	
	public static List<Date> getNgayAtMoment(){
		List<Date> result=new ArrayList<Date>();
		int nam=Calendar.getInstance().get(Calendar.YEAR);
		int thang=Calendar.getInstance().get(Calendar.MONTH);
		int ngay=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		Calendar batdau=Calendar.getInstance();
		Calendar ketthuc=Calendar.getInstance();
		batdau.set(nam, thang, ngay, 0, 0, 0);
		ketthuc.set(nam, thang, ngay, 23, 59, 59);
		result.add(batdau.getTime());
		result.add(ketthuc.getTime());
		return result;
	}
	
	public static long getDifferenceDays(Date d1, Date d2) {
	    long diff = d2.getTime() - d1.getTime();
	    long days=TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	    if(days==0) {
	    	return 1;
	    }
	    return days;
	}
	
	public static Date getDateStartOfDay(Date day){
		Calendar batdau=Calendar.getInstance();
		batdau.setTime(day);
		batdau.set(Calendar.HOUR_OF_DAY, 0);
		batdau.set(Calendar.MINUTE, 0);
		batdau.set(Calendar.SECOND, 0);
		batdau.set(Calendar.MILLISECOND, 0);
		return batdau.getTime();
	}
	
	public static Date getDateEndOfDay(Date day){
		Calendar batdau=Calendar.getInstance();
		batdau.setTime(day);
		batdau.set(Calendar.HOUR_OF_DAY, 23);
		batdau.set(Calendar.MINUTE, 59);
		batdau.set(Calendar.SECOND, 59);
		return batdau.getTime();
	}
	
	public static Date getDateStartOfYear(int year){
		Calendar batdau=Calendar.getInstance();
		batdau.set(year, 0, 1, 0, 0, 0);
		return batdau.getTime();
	}
	
	public static Date getDateEndOfYear(int year){
		Calendar batdau=Calendar.getInstance();
		batdau.set(year, 11, 31, 0, 0, 0);
		return batdau.getTime();
	}
	
	public static Date backDate(Date date){
		Calendar customDate=Calendar.getInstance();
		customDate.setTime(date);
		customDate.add(Calendar.DATE, -1);
		return customDate.getTime();
	}
	
	public static Date backDate(Date date, int number){
		Calendar customDate=Calendar.getInstance();
		customDate.setTime(date);
		customDate.add(Calendar.DATE, -number);
		return customDate.getTime();
	}
	
	public static Date nextDate(Date date){
		Calendar customDate=Calendar.getInstance();
		customDate.setTime(date);
		customDate.add(Calendar.DATE, 1);
		return customDate.getTime();
	}
	
	public static java.sql.Date converDateUtilToSql(Date date){
		return new java.sql.Date(date.getTime());
	}
	
	public static Date endDate(Date date) {
		if(date==null)
			return null;
		Calendar endDate=Calendar.getInstance(TimeZone.getDefault());
		endDate.setTime(date);
		endDate.set(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DATE), 23, 59, 59);
		return endDate.getTime();
	}
	
	public static List<Date> getDatesBetween(Date startDate, Date endDate) {
		List<Date> datesInRange = new ArrayList<>();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startDate);

		Calendar endCalendar = new GregorianCalendar();
		endCalendar.setTime(endDate);

		while (calendar.before(endCalendar)) {
			Date result = calendar.getTime();
			datesInRange.add(result);
			calendar.add(Calendar.DATE, 1);
		}
		return datesInRange;
	}
}
