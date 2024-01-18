package databaseApp.db.model.entity;

import databaseApp.db.model.entity.enums.DbTypeEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "db")
public class Db extends BaseTask {



    @ManyToOne
    private DbType dbType;

    public Db() {
    }

    public DbType getDbType() {
        return dbType;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }
}
