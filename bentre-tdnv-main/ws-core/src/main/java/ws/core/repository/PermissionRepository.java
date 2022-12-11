package ws.core.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.Permission;


public interface PermissionRepository extends MongoRepository<Permission, ObjectId>{
	Optional<Permission> findByKey(String permissionKey);
}
