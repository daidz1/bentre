package ws.core.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.LogRequest;


public interface LogRequestRepository extends MongoRepository<LogRequest, ObjectId>{
	
}
