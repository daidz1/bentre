package ws.core.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ws.core.model.User;
import ws.core.repository.UserRepository;
import ws.core.security.CustomUserDetails;

@Service
public class UserService implements UserDetailsService {

	@Autowired 
	private UserRepository userRepository;

	@Override 
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { 
		User user = userRepository.findByUsername(username).get(); 
		if (user == null) { 
			throw new UsernameNotFoundException(username); 
		} 

		CustomUserDetails customUserDetails=new CustomUserDetails();
		customUserDetails.setUser(user);
		return customUserDetails; 
	}

	public User getUserByUsername(String username) {
		User user = userRepository.findByUsername(username).get(); 
		if (user == null) { 
			throw new UsernameNotFoundException(username); 
		} 
		return user;
	}
	
	public boolean validForCreate(User user) throws Exception{
		/* Check username */
		User usercheck=null;
		try {
			usercheck=userRepository.findByUsername(user.username).get();
		} catch (Exception e) {}
		
		if(usercheck!=null) {
			throw new Exception("username đã tồn tại");
		}
		
		/* check email */
		usercheck=null;
		try {
			usercheck=userRepository.findByEmail(user.email).get();
		} catch (Exception e) {}
		
		if(usercheck!=null) {
			throw new Exception("email đã tồn tại");
		}
		
		return true;
	}
	
	public boolean validForUpdate(User user) throws Exception{
		
		return true;
	}
	
	public void saveLog(User user, HttpServletRequest request) {
		
	}
}
