package databaseApp.db.model.dto;


import databaseApp.db.model.entity.enums.TaskTypeEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


import java.util.List;

public class CheckTaskStatusDTO {

    @NotEmpty
    private List<String> taskNumbers;
    @NotNull
    private TaskTypeEnum taskType;
    @NotNull
    private String projectType;

    public CheckTaskStatusDTO() {
    }

    public List<String> getTaskNumbers() {
        return taskNumbers;
    }

    public void setTaskNumbers(List<String> taskNumbers) {
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
}
