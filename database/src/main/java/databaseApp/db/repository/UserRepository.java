package databaseApp.db.repository;

import databaseApp.db.model.entity.RoleEntity;
import databaseApp.db.model.entity.UserEntity;
import databaseApp.db.model.entity.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {



    Optional<UserEntity> findByuNumber(String uNumber);

    Optional<UserEntity> findByEmail(String email);

    //RoleEntity findByRole(RoleEnum roleEnum);
}
