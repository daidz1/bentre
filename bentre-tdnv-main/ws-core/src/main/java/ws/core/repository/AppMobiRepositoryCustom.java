package ws.core.repository;

import java.util.List;

import ws.core.model.AppMobi;
import ws.core.model.filter.AppMobiFilter;

public interface AppMobiRepositoryCustom{
	List<AppMobi> findAll(AppMobiFilter appMobiFilter, int skip, int limit);
	int countAll(AppMobiFilter appMobiFilter);
	AppMobi get(String userId, String deviceId);
}
