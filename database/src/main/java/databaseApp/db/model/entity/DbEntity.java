package databaseApp.db.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "db")
public class DbEntity extends BaseTask {



    @ManyToOne
    private DbType dbType;

    public DbEntity() {
    }

    public DbType getDbType() {
        return dbType;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }
}
