package databaseApp.db.model.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {


    @Column(name = "u_number", unique = true, nullable = false)
    private String uNumber;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<RoleEntity> roles = new ArrayList<>();

    public UserEntity() {
    }


    public String getuNumber() {
        return uNumber;
    }

    public void setuNumber(String uNumber) {
        this.uNumber = uNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleEntity> roles) {
        this.roles = roles;
    }

}
