package ws.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ws.core.model.OrganizationRole;
import ws.core.repository.OrganizationRoleRepositoryCustom;

@Service
public class OrganizationRoleService {
	@Autowired 
	private OrganizationRoleRepositoryCustom organizationRoleRepositoryCustom;
	
	public enum Method{
		and, or;
	}
	
	public boolean hasRole(String organizationId, String userId, List<String> permissions, Method method) {
		boolean result=false;
		List<OrganizationRole> organizationRoles=organizationRoleRepositoryCustom.getRolesOrganizationUser(organizationId, userId);
		if(method==Method.and) {
			boolean checkRoundEachRole=false;
			for(String permission:permissions) {
				checkRoundEachRole=false;
				for (OrganizationRole organizationRole : organizationRoles) {
					if(organizationRole.permissionKeys.contains(permission)) {
						checkRoundEachRole=true;
						break;
					}
				}
				
				if(checkRoundEachRole==false) {
					result=false;
					break;
				}
			}
			
			if(checkRoundEachRole) {
				result=true;
			}
		}else if(method==Method.or) {
			for(String permission:permissions) {
				boolean checkRoundEachRole=false;
				for (OrganizationRole organizationRole : organizationRoles) {
					if(organizationRole.permissionKeys.contains(permission)) {
						checkRoundEachRole=true;
						break;
					}
				}
				
				if(checkRoundEachRole==true) {
					result=true;
					break;
				}
			}
		}
		return result;
	}
	
	public boolean hasRole(String organizationId, String userId, String permission) {
		boolean result=false;
		List<OrganizationRole> organizationRoles=organizationRoleRepositoryCustom.getRolesOrganizationUser(organizationId, userId);
		for (OrganizationRole organizationRole : organizationRoles) {
			if(organizationRole.permissionKeys.contains(permission)) {
				result=true;
				break;
			}
		}
		return result;
	}
}
