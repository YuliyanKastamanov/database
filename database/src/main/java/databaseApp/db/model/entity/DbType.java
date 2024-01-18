package databaseApp.db.model.entity;


import databaseApp.db.model.entity.enums.DbTypeEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "db_types")
public class DbType extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private DbTypeEnum type;

    public DbType() {
    }

    public DbTypeEnum getType() {
        return type;
    }

    public void setType(DbTypeEnum type) {
        this.type = type;
    }
}
