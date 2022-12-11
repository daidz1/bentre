package ws.core.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.Task;

public interface TaskRepository extends MongoRepository<Task, ObjectId>{

}
