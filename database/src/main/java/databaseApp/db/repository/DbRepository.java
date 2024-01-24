package databaseApp.db.repository;

import databaseApp.db.model.entity.DbEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbRepository extends JpaRepository<DbEntity, String> {
}
