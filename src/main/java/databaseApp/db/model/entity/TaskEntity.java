package databaseApp.db.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class TaskEntity extends BaseTask {



    @ManyToOne
    private TaskTypeEntity taskTypeEntity;

    public TaskEntity() {
    }

    public TaskTypeEntity getTypeEntity() {
        return taskTypeEntity;
    }

    public void setTypeEntity(TaskTypeEntity taskTypeEntity) {
        this.taskTypeEntity = taskTypeEntity;
    }
}
