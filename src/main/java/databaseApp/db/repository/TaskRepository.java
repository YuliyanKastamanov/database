package databaseApp.db.repository;

import databaseApp.db.model.entity.TaskEntity;
import databaseApp.db.model.entity.TaskTypeEntity;
import databaseApp.db.model.entity.enums.TaskTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, String> {


    Optional<TaskEntity> findByCri(String cri);
    List<TaskEntity> findAllByTaskTypeEntity(TaskTypeEntity taskTypeEntity);

    List<TaskEntity> findAll();
}
