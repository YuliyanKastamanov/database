package databaseApp.db.model.dto;

import databaseApp.db.model.entity.enums.TaskTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class UpdateTaskDTO {

    @NotBlank
    @Size(min = 3, max = 20)
    private String taskNumber;
    private String revision;
    private String socStatus;
    private String socDescription;
    private String comment;
    @NotBlank
    private String coversheetSap;
    @NotBlank
    private String coversheetStatus;
    @NotBlank
    private String createdMJob;
    @NotBlank
    private String statusMJob;
    private String sbReference;
    @NotBlank
    private TaskTypeEnum type;

    public UpdateTaskDTO() {
    }

    public String getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getSocStatus() {
        return socStatus;
    }

    public void setSocStatus(String socStatus) {
        this.socStatus = socStatus;
    }

    public String getSocDescription() {
        return socDescription;
    }

    public void setSocDescription(String socDescription) {
        this.socDescription = socDescription;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCoversheetSap() {
        return coversheetSap;
    }

    public void setCoversheetSap(String coversheetSap) {
        this.coversheetSap = coversheetSap;
    }

    public String getCoversheetStatus() {
        return coversheetStatus;
    }

    public void setCoversheetStatus(String coversheetStatus) {
        this.coversheetStatus = coversheetStatus;
    }

    public String getCreatedMJob() {
        return createdMJob;
    }

    public void setCreatedMJob(String createdMJob) {
        this.createdMJob = createdMJob;
    }

    public String getStatusMJob() {
        return statusMJob;
    }

    public void setStatusMJob(String statusMJob) {
        this.statusMJob = statusMJob;
    }

    public String getSbReference() {
        return sbReference;
    }

    public void setSbReference(String sbReference) {
        this.sbReference = sbReference;
    }

    public TaskTypeEnum getType() {
        return type;
    }

    public void setType(TaskTypeEnum type) {
        this.type = type;
    }
}
