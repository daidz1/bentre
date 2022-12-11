package vn.com.ngn.site.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class LocalDateUtil {
	protected SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	protected SimpleDateFormat sdfDatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static DateTimeFormatter dateFormater1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	public static DateTimeFormatter dateFormater2 = DateTimeFormatter.ofPattern("yyyyMMdd");
	public static DateTimeFormatter dateFormater3 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public static DateTimeFormatter dateTimeFormater1 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	public static DateTimeFormatter dateTimeFormater2 = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
	
	public static LocalDate longToLocalDate(long longValue) {
		return Instant.ofEpochMilli(longValue).atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	public static LocalDateTime longToLocalDateTime(long longValue) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(longValue), TimeZone.getDefault().toZoneId());
	}

	public static long localDateTimeToLong(LocalDateTime dateTime) {
		ZonedDateTime zdt = ZonedDateTime.of(dateTime, ZoneId.systemDefault());
		return zdt.toInstant().toEpochMilli();
	}
	
	public static long localDateToLong(LocalDate date) {
		return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public static String formatLocalDate(LocalDate date, DateTimeFormatter dateFormat) {
		return date.format(dateFormat);
	}
	
	public static String formatLocalDateTime(LocalDateTime dateTime, DateTimeFormatter dateFormat) {
		return dateTime.format(dateFormat);
	}
	
	public static LocalDate stringToLocalDate(String strDate, DateTimeFormatter dateFormat) {
		return LocalDate.parse(strDate, dateFormat);
	}
	
	public static LocalDateTime stringToLocalDateTime(String strDatetime, DateTimeFormatter dateFormat) {
		return LocalDateTime.parse(strDatetime, dateFormat);
	}
	
	public static LocalDateTime getStartOfTheYear(int year) {
		return LocalDateTime.of(year, 1, 1, 0, 0,0);
	}
	
	public static LocalDateTime getEndtOfTheYear(int year) {
		return LocalDateTime.of(year, 12, 31, 23, 59,59);
	}
}
