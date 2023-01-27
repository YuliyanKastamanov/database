package bg.softuni.mobilelelele.model.entity;

import bg.softuni.mobilelelele.model.entity.enums.UserRoleEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "user_roles")
public class UserRoleEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRoleEnum userRole;


    public long getId() {
        return id;
    }

    public UserRoleEntity setId(long id) {
        this.id = id;
        return this;
    }

    public UserRoleEnum getUserRole() {
        return userRole;
    }

    public UserRoleEntity setUserRole(UserRoleEnum userRole) {
        this.userRole = userRole;
        return this;
    }

    @Override
    public String toString() {
        return "UserRoleEntity{" +
                "id=" + id +
                ", userRole=" + userRole +
                '}';
    }
}
