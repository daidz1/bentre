package ws.core.service;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ws.core.model.Permission;
import ws.core.model.User;
import ws.core.repository.UserRepository;

@Service
public class InitProjectService {

	@Autowired
	public UserRepository userRepository;
	
	@Autowired
	public PasswordEncoder passwordEncoder;
	
	public void installDataIfNotExists() {
		try {
			System.out.println("Check và khởi tạo dữ liệu mặc định");
			
			/* Add admin account */
			User administrator=  createAdministrator();
			
			/* Declare permission */
			initPermission(administrator);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private User createAdministrator() {
		Optional<User> checkAdmin = userRepository.findByUsername("administrator");
		User administrator = null;
		if(checkAdmin.isPresent()==false) {
			administrator=new User();
			administrator.setId(new ObjectId("6051883e2e85d71a67ca8319"));
			administrator.active=true;
			administrator.username="administrator";
			administrator.email="administrator@dev.com";
			administrator.fullName="Administrator";
			administrator.jobTitle="Quản trị cấp cao";
			administrator.creatorName="administrator";
			administrator.creatorId=administrator.getId();
			administrator.password=passwordEncoder.encode("abc123");
			userRepository.save(administrator);
		}else {
			System.out.println("Tìm thấy");
			administrator=checkAdmin.get();
		}
		return administrator;
	}
	
	private void initPermission(User administrator) {
		Permission permission=new Permission();
		permission.name="";
		permission.description="";
		permission.key="";
		permission.name="";
		permission.order=0;
		permission.groupId="";
		permission.groupName="";
		permission.groupOrder=0;
	}
}
