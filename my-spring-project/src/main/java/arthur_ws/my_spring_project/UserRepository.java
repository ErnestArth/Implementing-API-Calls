package arthur_ws.my_spring_project;

import arthur_ws.my_spring_project.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);

    UserEntity findByUserId(String userId);

    UserEntity findUserByEmailVerificationToken(String token);

    // working with SQL
//    @Query(value = "select * from Users u where u.EMAIL_VERIFICATION_STATUS = 'true'",
//            countQuery = "select count(*) from Users u where u.EMAIL_VERIFICATION_STATUS = 'true'",
//            nativeQuery = true)
//    Page<UserEntity> findAllUsersWithConfirmedEmailAddress(Pageable pageableRequest);
}
