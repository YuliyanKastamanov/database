package databaseApp.db.model.dto;


import databaseApp.db.model.entity.enums.TaskTypeEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


import java.util.Set;

public class CheckTaskStatusDTO {

    @NotEmpty
    private Set<String> taskNumbers;
    @NotNull
    private TaskTypeEnum taskType;
    @NotNull
    private String projectType;
    @NotNull
    private String revision;

    public CheckTaskStatusDTO() {
    }

    public Set<String> getTaskNumbers() {
        return taskNumbers;
    }

    public void setTaskNumbers(Set<String> taskNumbers) {
        this.taskNumbers = taskNumbers;
    }

    public TaskTypeEnum getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskTypeEnum taskType) {
        this.taskType = taskType;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }
}
