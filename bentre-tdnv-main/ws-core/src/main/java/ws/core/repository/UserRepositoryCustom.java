package ws.core.repository;

import java.util.List;

import ws.core.model.User;
import ws.core.model.filter.UserFilter;

public interface UserRepositoryCustom{
	List<User> findAll(UserFilter userFilter, int skip, int limit);
	int countAll(UserFilter userFilter);
	
	boolean checkPassword(String userId, String password);
}
