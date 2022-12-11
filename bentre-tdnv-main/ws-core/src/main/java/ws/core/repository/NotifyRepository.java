package ws.core.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.Notify;

public interface NotifyRepository extends MongoRepository<Notify, ObjectId>{

}
