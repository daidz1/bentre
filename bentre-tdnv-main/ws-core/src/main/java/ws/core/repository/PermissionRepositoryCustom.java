package ws.core.repository;

import java.util.List;

import ws.core.model.Permission;

public interface PermissionRepositoryCustom{
	List<Permission> getList(String permissionKeys);
}
