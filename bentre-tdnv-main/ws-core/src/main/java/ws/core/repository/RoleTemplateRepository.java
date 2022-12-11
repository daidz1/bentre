package ws.core.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.RoleTemplate;


public interface RoleTemplateRepository extends MongoRepository<RoleTemplate, ObjectId>{
	
}
