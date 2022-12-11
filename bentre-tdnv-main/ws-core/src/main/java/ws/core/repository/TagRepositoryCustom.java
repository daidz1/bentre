package ws.core.repository;

import java.util.List;
import java.util.Optional;

import ws.core.model.Tag;
import ws.core.model.filter.TagFilter;

public interface TagRepositoryCustom{
	
	long countAll(TagFilter tagFilter);
	
	List<Tag> findAll(TagFilter tagFilter);
	
	Optional<Tag> findOne(TagFilter tagFilter);
}
