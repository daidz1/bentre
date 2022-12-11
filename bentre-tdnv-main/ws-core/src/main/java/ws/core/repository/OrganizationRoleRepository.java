package ws.core.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.OrganizationRole;


public interface OrganizationRoleRepository extends MongoRepository<OrganizationRole, ObjectId>{
	
}
