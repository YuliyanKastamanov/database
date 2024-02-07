package databaseApp.db.model.entity;


import databaseApp.db.model.entity.enums.TypeEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "db_types")
public class TypeEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private TypeEnum type;

    public TypeEntity() {
    }

    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }
}
