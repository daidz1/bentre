package vn.com.ngn.site.util;

import vn.com.ngn.site.enums.DocOfEnum;
import vn.com.ngn.site.enums.DocTypeEnum;

public class DocEnumUtil {
	public static String createKeyByDocEnum(DocTypeEnum type, DocOfEnum of){
		return type.getKey()+"_"+of.toString();
	}
}
