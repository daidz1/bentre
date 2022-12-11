package ws.core.repository;

import java.util.List;

import ws.core.model.RoleTemplate;

public interface RoleTemplateRepositoryCustom{
	List<RoleTemplate> findAll(int skip, int limit, String keyword);
	int countAll(String keyword);
}
