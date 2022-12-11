package ws.core.repository;

import java.util.List;
import java.util.Optional;

import ws.core.model.OrganizationRole;
import ws.core.model.filter.OrganizationRoleFilter;

public interface OrganizationRoleRepositoryCustom{
	List<OrganizationRole> findAll(OrganizationRoleFilter organizationRoleFilter, int skip, int limit);
	int countAll(OrganizationRoleFilter organizationRoleFilter);
	Optional<OrganizationRole> findOne(OrganizationRoleFilter organizationRoleFilter);
	
	
	List<OrganizationRole> getRolesOrganizationUser(String organizationId, String userId);
	String getRolesOrganizationUserString(String organizationId, String userId);
	List<OrganizationRole> getRolesOrganization(String organizationId, String keyword);
	int countRolesOrganization(String organizationId, String keyword);
}
