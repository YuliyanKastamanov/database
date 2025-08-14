package databaseApp.db.model.dto;

import databaseApp.db.model.entity.enums.TaskTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CheckTaskStatusDTO {
    @NotBlank
    @Size(min = 3, max = 20)
    private String taskNumber;
    @NotNull
    private TaskTypeEnum type;

    public CheckTaskStatusDTO() {
    }

    public String getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    public TaskTypeEnum getType() {
        return type;
    }

    public void setType(TaskTypeEnum type) {
        this.type = type;
    }
}
