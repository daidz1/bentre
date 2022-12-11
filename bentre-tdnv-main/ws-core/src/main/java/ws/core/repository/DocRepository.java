package ws.core.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.Doc;

public interface DocRepository extends MongoRepository<Doc, ObjectId>{

}
