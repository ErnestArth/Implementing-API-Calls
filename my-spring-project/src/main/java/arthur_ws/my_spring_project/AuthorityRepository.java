package arthur_ws.my_spring_project;

import arthur_ws.my_spring_project.entity.AuthorityEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends CrudRepository<AuthorityEntity,Long> {
    AuthorityEntity findByName(String name);
}
