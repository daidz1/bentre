package ws.core.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.AppMobi;

public interface AppMobiRepository extends MongoRepository<AppMobi, ObjectId>{

}
