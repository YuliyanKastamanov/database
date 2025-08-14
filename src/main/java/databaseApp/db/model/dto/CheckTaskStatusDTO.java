package databaseApp.db.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CheckTaskStatusDTO {
    @NotBlank
    @Size(min = 3, max = 20)
    private List<String> taskNumbers;
    private String taskType;
    private String projectType;

    public CheckTaskStatusDTO() {
    }

    public List<String> getTaskNumbers() {
        return taskNumbers;
    }

    public void setTaskNumbers(List<String> taskNumbers) {
        this.taskNumbers = taskNumbers;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }
}
