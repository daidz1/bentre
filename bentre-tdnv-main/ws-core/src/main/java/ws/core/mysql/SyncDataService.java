package ws.core.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SyncDataService {

	/* B1 lấy thông tin _user MySQL qua MongoDB */
	
	@Autowired
	private SyncUserService syncUserService;
	
	public void syncAll() {
		
	}
}
