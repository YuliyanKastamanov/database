package databaseApp.db.repository;

import databaseApp.db.model.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, String> {


    Optional<TaskEntity> findByCri(String cri);
}
