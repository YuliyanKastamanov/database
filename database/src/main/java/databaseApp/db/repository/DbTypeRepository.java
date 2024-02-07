package databaseApp.db.repository;

import databaseApp.db.model.entity.TypeEntity;
import databaseApp.db.model.entity.enums.TypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DbTypeRepository extends JpaRepository<TypeEntity, String> {



    Optional<TypeEntity> findByType(TypeEnum name);
}
