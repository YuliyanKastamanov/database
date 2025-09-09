package databaseApp.db.model.dto;

import databaseApp.db.model.entity.enums.TaskTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class AddTaskDTOs{

    private List<AddTaskDTO> taskNumbers;

    @NotNull
    private TaskTypeEnum type;

    @NotBlank
    private String revision;

    public AddTaskDTOs() {
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public List<AddTaskDTO> getTaskNumbers() {
        return taskNumbers;
    }

    public void setTaskNumbers(List<AddTaskDTO> taskNumbers) {
        this.taskNumbers = taskNumbers;
    }

    public TaskTypeEnum getType() {
        return type;
    }

    public void setType(TaskTypeEnum type) {
        this.type = type;
    }
}
