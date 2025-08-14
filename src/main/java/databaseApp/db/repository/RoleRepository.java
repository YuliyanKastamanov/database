package databaseApp.db.repository;

import databaseApp.db.model.entity.RoleEntity;
import databaseApp.db.model.entity.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, String> {


    Optional<RoleEntity> findByRole(RoleEnum role);
}
