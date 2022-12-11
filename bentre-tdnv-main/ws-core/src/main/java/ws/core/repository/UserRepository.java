package ws.core.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.User;

public interface UserRepository extends MongoRepository<User, ObjectId>{
	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String username);
}
