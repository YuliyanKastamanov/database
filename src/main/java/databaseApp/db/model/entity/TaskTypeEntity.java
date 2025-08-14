package databaseApp.db.model.entity;


import databaseApp.db.model.entity.enums.TaskTypeEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;


@Entity
@Table(name = "db_types")
public class TaskTypeEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private TaskTypeEnum type;

    private String criType;

    private String dbRevision;

    public TaskTypeEntity() {
    }

    public TaskTypeEnum getType() {
        return type;
    }

    public void setType(TaskTypeEnum type) {
        this.type = type;
    }

    public String getCriType() {
        return criType;
    }

    public void setCriType(String criType) {
        this.criType = criType;
    }

    public String getDbRevision() {
        return dbRevision;
    }

    public void setDbRevision(String dbRevision) {
        this.dbRevision = dbRevision;
    }
}
