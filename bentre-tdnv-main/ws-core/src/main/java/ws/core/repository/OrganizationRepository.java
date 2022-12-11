package ws.core.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.Organization;


public interface OrganizationRepository extends MongoRepository<Organization, ObjectId>{
	Optional<Organization> findByName(String name);
	List<Organization> findByOrgTypeMysql(String orgType);
}
