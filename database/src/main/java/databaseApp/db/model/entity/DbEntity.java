package databaseApp.db.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "db")
public class DbEntity extends BaseTask {



    @ManyToOne
    private TypeEntity typeEntity;

    public DbEntity() {
    }

    public TypeEntity getTypeEntity() {
        return typeEntity;
    }

    public void setTypeEntity(TypeEntity typeEntity) {
        this.typeEntity = typeEntity;
    }
}
