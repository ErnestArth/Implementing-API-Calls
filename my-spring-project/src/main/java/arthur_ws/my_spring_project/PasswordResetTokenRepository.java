package arthur_ws.my_spring_project;

import arthur_ws.my_spring_project.entity.PasswordResetTokenEntity;
import org.springframework.data.repository.CrudRepository;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity, Long > {
    PasswordResetTokenEntity findByToken(String token);
}
