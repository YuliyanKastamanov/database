package databaseApp.db.model.entity;

import databaseApp.db.model.entity.enums.RoleEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class RoleEntity extends BaseEntity{

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    public RoleEntity() {
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }
}
