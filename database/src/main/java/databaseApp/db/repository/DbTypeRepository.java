package databaseApp.db.repository;

import databaseApp.db.model.entity.DbType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbTypeRepository extends JpaRepository<DbType, String> {
}
