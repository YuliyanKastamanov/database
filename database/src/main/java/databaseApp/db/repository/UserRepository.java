package databaseApp.db.repository;

import databaseApp.db.model.entity.Role;
import databaseApp.db.model.entity.User;
import databaseApp.db.model.entity.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {



    boolean findByuNumber(String s);

    boolean findByEmail(String email);

    Role findByRoleEnum(RoleEnum roleEnum);
}
