package ws.core.repository;

import java.util.List;
import java.util.Optional;

import ws.core.model.Organization;
import ws.core.model.filter.OrganizationFilter;

public interface OrganizationRepositoryCustom{
	List<Organization> findAll(OrganizationFilter organizationFilter, int skip, int limit);
	long countAll(OrganizationFilter organizationFilter);
	Optional<Organization> findOne(OrganizationFilter organizationFilter);
}
