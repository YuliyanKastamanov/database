package databaseApp.db.model.dto;

import databaseApp.db.model.entity.enums.TypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DbAddTaskDTO {

    @NotBlank
    @Size(min = 3, max = 20)
    private String taskNumber;

    @NotBlank
    private String revision;
    @NotBlank
    private String socStatus;

    @NotBlank
    private String socDescription;

    private String comment;

    private String coversheetSap;

    private String coversheetStatus;

    private String createdMJob;

    private String statusMJob;

    private String sbReference;

    @NotBlank
    private TypeEnum type;

    public DbAddTaskDTO() {
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

    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }
}
