package ws.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import org.springframework.web.util.HtmlUtils;

public class TestApp {

	public static void main(String[] args) {
		SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
		String stringDate = "20210805T162456,76+07";
		Date dateTime=null;
		try {
			dateTime=ISO8601DATEFORMAT.parse(stringDate);
			System.out.println(dateTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		String escape=HtmlUtils.htmlEscape("<script>alert('nek');\"</script>");
		System.out.println(escape);
		
		String unescape=HtmlUtils.htmlUnescape(escape);
		System.out.println(unescape);
		
		System.out.println(HtmlUtils.htmlUnescape("Bùi Như Khuê &lt;script&gt;alert(&#39;nek&#39;);&lt;/script&gt;"));
		
		System.out.println(HtmlUtils.htmlUnescape("'Bùi Như Khuê'"));
		
		
		String[] demo="OU=Phòng công chức, viên chức,OU=Sở Nội vụ,OU=UBND Tỉnh Bến Tre".split("=");
		LinkedList<String> result=new LinkedList<String>();
		int count=0;
 		for (String string : demo) {
			System.out.println("item: "+string);
			if(count==0) {
				count++;
				continue;
			}
			
			/* Bỏ ,OU hoặc , OU */
			String ok=string.replaceAll(",OU", "");
			ok=ok.replaceAll(", OU", "");
			result.add(0, ok);
			
			count++;
		}
 		
 		for (String string : result) {
			System.out.println("ok: "+string);
		}
	}
}
