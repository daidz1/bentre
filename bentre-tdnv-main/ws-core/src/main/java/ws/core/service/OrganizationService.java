package ws.core.service;

import java.util.LinkedList;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ws.core.model.Organization;
import ws.core.repository.OrganizationRepository;

@Service
public class OrganizationService {

	@Autowired 
	private OrganizationRepository organizationRepository;

	public String getPath(Organization childOrganization) throws Exception {
		try {
			return generalPath(new LinkedList<String>(), childOrganization);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private String generalPath(LinkedList<String> paths, Organization childOrganization) throws Exception{
		paths.add(childOrganization.getId());
		
		/* Nếu parentId không tồn tại hoặc null thì là ROOT */
		if(childOrganization.parentId==null || childOrganization.parentId.isEmpty()) {
			String result="";
			for (int i=paths.size()-1;i>=0;i--) {
				if(i==0) {
					result+=paths.get(i);
				}else {
					result+=paths.get(i)+"/";
				}
			}
			return result;
		}
		
		/* Ngược lại thì tiếp tục truy vấn parentId */
		try {
			Organization parentOrganization=organizationRepository.findById(new ObjectId(childOrganization.parentId)).get();
			return generalPath(paths, parentOrganization);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
}
