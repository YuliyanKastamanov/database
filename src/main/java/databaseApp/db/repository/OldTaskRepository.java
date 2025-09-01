package databaseApp.db.repository;

import databaseApp.db.model.entity.BaseTask;
import databaseApp.db.model.entity.OldTasksEntity;
import databaseApp.db.model.entity.TaskTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OldTaskRepository extends JpaRepository<OldTasksEntity, String>  {

    List<OldTasksEntity> findAll();

    Optional<OldTasksEntity> findByCriAndRevision(String cri, String revision);

    List<OldTasksEntity> findByCriOrderByLastUpdateAsc(String cri);

}
