package databaseApp.db.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class TaskEntity extends BaseTask {



    @ManyToOne
    private TaskTypeEntity taskTypeEntity;

    @Column(name = "cri", nullable = false, unique = true)
    private String cri;


    public TaskEntity() {
    }

    public TaskTypeEntity getTaskTypeEntity() {
        return taskTypeEntity;
    }

    public void setTaskTypeEntity(TaskTypeEntity taskTypeEntity) {
        this.taskTypeEntity = taskTypeEntity;
    }

    public String getCri() {
        return cri;
    }

    public void setCri(String cri) {
        this.cri = cri;
    }
}
