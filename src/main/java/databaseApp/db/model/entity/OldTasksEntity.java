package databaseApp.db.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "old_tasks")
public class OldTasksEntity extends BaseTask{

    private String  taskType;

    @Column(name = "cri")
    private String cri;

    public OldTasksEntity() {
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getCri() {
        return cri;
    }

    public void setCri(String cri) {
        this.cri = cri;
    }
}
