package databaseApp.db.model.dto;

import databaseApp.db.model.entity.enums.ResponseEnum;
import jakarta.validation.constraints.NotNull;

public class AddTaskResponseDTO {

    @NotNull
    private AddTaskDTO addTaskDTO;

    @NotNull
    private ResponseEnum status;

    public AddTaskResponseDTO() {
    }

    public AddTaskDTO getAddTaskDTO() {
        return addTaskDTO;
    }

    public void setAddTaskDTO(AddTaskDTO addTaskDTO) {
        this.addTaskDTO = addTaskDTO;
    }

    public ResponseEnum getStatus() {
        return status;
    }

    public void setStatus(ResponseEnum status) {
        this.status = status;
    }
}
