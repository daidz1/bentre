package ws.core.util;

import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SuppressWarnings("serial")
public class ResponseCMS extends Document{
	
	private HttpStatus status=null;
	
	public void setStatus(HttpStatus status) {
		this.status=status;
		this.put("status", status.value());
	}
	
	public void setTotal(Object result) {
		this.put("total", result);
	}
	
	public void setResult(Object result) {
		this.put("result", result);
	}
	
	public void setMessage(Object message) {
		this.put("message", message);
	}
	
	public void setDebug(Object debug) {
		this.put("debug", debug);
	}
	
	public Object build() {
		return ResponseEntity.status(status).body(this);
	}
}
