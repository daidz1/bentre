package ws.core.repository;

import java.util.List;

import ws.core.model.Doc;
import ws.core.model.filter.DocFilter;

public interface DocRepositoryCustom{
	List<Doc> findAll(DocFilter docFilter, int skip, int limit);
	int countAll(DocFilter docFilter);
}
