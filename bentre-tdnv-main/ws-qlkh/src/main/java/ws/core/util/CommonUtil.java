package ws.core.util;

import org.bson.Document;

public class CommonUtil {

	public static Document filterFields(Document article, String fields) {
		Document document=new Document();
		String []fieldKeys=fields.split(",");
		for (String key : fieldKeys) {
			key=key.trim();
			if(article.containsKey(key)) {
				document.append(key, article.get(key));
			}
		}
		return document;
	}

	public static String insertString(String originalString, String stringToBeInserted, int index){
		String newString = originalString.substring(0, index + 1)
                + stringToBeInserted
                + originalString.substring(index + 1);
		return newString;
	}
}
