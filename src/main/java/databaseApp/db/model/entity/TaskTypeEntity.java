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
    private String oldRevision1;
    private String oldRevision2;
    private String oldRevision3;

    public TaskTypeEntity() {
    }

    public String getOldRevision1() {
        return oldRevision1;
    }

    public void setOldRevision1(String oldRevision1) {
        this.oldRevision1 = oldRevision1;
    }

    public String getOldRevision2() {
        return oldRevision2;
    }

    public void setOldRevision2(String oldRevision2) {
        this.oldRevision2 = oldRevision2;
    }

    public String getOldRevision3() {
        return oldRevision3;
    }

    public void setOldRevision3(String oldRevision3) {
        this.oldRevision3 = oldRevision3;
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
