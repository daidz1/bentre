package ws.core.repository;

import java.util.List;

import ws.core.model.LogRequest;
import ws.core.model.filter.LogRequestFilter;

public interface LogRequestRepositoryCustom{
	List<LogRequest> findAll(LogRequestFilter logRequestFilter, int skip, int limit);
	Long countAll(LogRequestFilter logRequestFilter);
}
