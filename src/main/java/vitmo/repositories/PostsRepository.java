package vitmo.repositories;

import vitmo.entities.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostsRepository extends CrudRepository<Post, Long>{
}