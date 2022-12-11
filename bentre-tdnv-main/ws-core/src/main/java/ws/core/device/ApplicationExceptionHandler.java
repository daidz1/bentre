package ws.core.device;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ws.core.util.ResponseCMS;


@RestControllerAdvice
public class ApplicationExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Object handleInvalidArgument(MethodArgumentNotValidException ex) {
		ResponseCMS responseCMS=new ResponseCMS();
		responseCMS.setStatus(HttpStatus.BAD_REQUEST);
		
		ArrayList<String> messages=new ArrayList<String>();
		Document errors=new Document();
		ex.getBindingResult().getFieldErrors().forEach(error->{
			errors.put(error.getField(), error.getDefaultMessage());
			messages.add(error.getDefaultMessage());
		});
		
		responseCMS.setMessage(messages.stream().collect(Collectors.joining("; ")));
		responseCMS.setResult(errors);
		return responseCMS.build();
	}
}
