package ws.core.repository;

import java.util.List;

import ws.core.model.GroupUserTask;
import ws.core.model.filter.GroupUserTaskFilter;

public interface GroupUserTaskRepositoryCustom{
	List<GroupUserTask> findAll(GroupUserTaskFilter groupUserTaskFilter, int skip, int limit);
	long countAll(GroupUserTaskFilter groupUserTaskFilter);
}
