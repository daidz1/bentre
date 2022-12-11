package ws.core.model;

import org.springframework.data.annotation.Id;

public class UserTaskCount {

	@Id
	public UserOrganization userTask;
	public int countTask;
	
	public UserTaskCount() {
		userTask=new UserOrganization();
	}
	
	@Override
	public String toString() {
		return userTask.userId+" ("+userTask.fullName+"), "+userTask.organizationId+" ("+userTask.organizationName+"), tasks: "+countTask;
	}
}
