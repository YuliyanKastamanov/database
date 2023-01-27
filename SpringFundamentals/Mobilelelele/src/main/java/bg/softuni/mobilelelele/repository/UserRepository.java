package bg.softuni.mobilelelele.repository;

import bg.softuni.mobilelelele.model.dto.UserLoginDTO;
import bg.softuni.mobilelelele.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

}
