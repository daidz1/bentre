package ws.core.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ws.core.model.GroupUserTask;


public interface GroupUserTaskRepository extends MongoRepository<GroupUserTask, ObjectId>{
	
}
