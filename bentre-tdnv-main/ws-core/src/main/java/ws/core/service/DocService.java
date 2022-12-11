package ws.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ws.core.model.request.ReqDocCreate;
import ws.core.model.request.ReqDocEdit;
import ws.core.model.request.ReqDocNew;
import ws.core.repository.DocRepository;

@Service
public class DocService {
	
	@Autowired
	protected DocRepository docRepository;
	
	public boolean validForImport(ReqDocCreate docImport) throws Exception{
		/* Check username */
//		User usercheck=null;
//		try {
//			usercheck=userRepository.findByUsername(task.username).get();
//		} catch (Exception e) {}
//		
//		if(usercheck!=null) {
//			throw new Exception("username đã tồn tại");
//		}
//		
//		/* check email */
//		usercheck=null;
//		try {
//			usercheck=userRepository.findByEmail(task.email).get();
//		} catch (Exception e) {}
//		
//		if(usercheck!=null) {
//			throw new Exception("email đã tồn tại");
//		}
		
		return true;
	}
	
	public boolean validForNew(ReqDocNew docNew) throws Exception{
		/* Check username */
//		User usercheck=null;
//		try {
//			usercheck=userRepository.findByUsername(task.username).get();
//		} catch (Exception e) {}
//		
//		if(usercheck!=null) {
//			throw new Exception("username đã tồn tại");
//		}
//		
//		/* check email */
//		usercheck=null;
//		try {
//			usercheck=userRepository.findByEmail(task.email).get();
//		} catch (Exception e) {}
//		
//		if(usercheck!=null) {
//			throw new Exception("email đã tồn tại");
//		}
		
		return true;
	}
	
	public boolean validForEdit(ReqDocEdit docEdit) throws Exception{
		/* Check username */
//		User usercheck=null;
//		try {
//			usercheck=userRepository.findByUsername(task.username).get();
//		} catch (Exception e) {}
//		
//		if(usercheck!=null) {
//			throw new Exception("username đã tồn tại");
//		}
//		
//		/* check email */
//		usercheck=null;
//		try {
//			usercheck=userRepository.findByEmail(task.email).get();
//		} catch (Exception e) {}
//		
//		if(usercheck!=null) {
//			throw new Exception("email đã tồn tại");
//		}
		
		return true;
	}
	
}
