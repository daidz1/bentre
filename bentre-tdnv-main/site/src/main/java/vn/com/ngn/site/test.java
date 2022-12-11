package vn.com.ngn.site;

import com.google.gson.JsonObject;

public class test {
	public static void main(String args[]) {
		JsonObject obj = new JsonObject();
		System.out.println(obj);
		if(obj.get("test")==null) {
			System.out.println("jsonNull");
		}
		
		
	}

}
