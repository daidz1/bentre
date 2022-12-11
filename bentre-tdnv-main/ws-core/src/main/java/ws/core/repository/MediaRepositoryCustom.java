package ws.core.repository;

import java.util.List;
import java.util.Optional;

import ws.core.model.Media;
import ws.core.model.filter.MediaFilter;

public interface MediaRepositoryCustom{
	
	long countAll(MediaFilter mediaFilter);
	
	List<Media> findAll(MediaFilter mediaFilter);
	
	Optional<Media> findOne(MediaFilter mediaFilter);
}
