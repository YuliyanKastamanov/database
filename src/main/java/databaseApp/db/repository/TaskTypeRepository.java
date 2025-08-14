package databaseApp.db.repository;

import databaseApp.db.model.entity.TaskTypeEntity;
import databaseApp.db.model.entity.enums.TaskTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskTypeRepository extends JpaRepository<TaskTypeEntity, String> {



    Optional<TaskTypeEntity> findByType(TaskTypeEnum name);
}
