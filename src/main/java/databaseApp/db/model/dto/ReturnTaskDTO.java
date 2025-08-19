package databaseApp.db.model.dto;


import java.time.LocalDate;

public class ReturnTaskDTO {
    private String taskNumber;
    private String revision;
    private String socStatus;
    private String socDescription;
    private String comment;
    private String jceName;
    private String coversheetSap;
    private String coversheetStatus;
    private String createdMJob;
    private String statusMJob;
    private String cri;
    private LocalDate lastUpdate;
    private String hasHistory;
    private String currentUpdate;
    private String sbReference;
    private String id;
    private boolean exists = true; // true if task exists
    private String statusInfo;   // optional descriptive message

    public ReturnTaskDTO() {
    }



    public boolean isExists() { return exists; }
    public void setExists(boolean exists) { this.exists = exists; }

    public String getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(String statusInfo) {
        this.statusInfo = statusInfo;
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

    public String getJceName() {
        return jceName;
    }

    public void setJceName(String jceName) {
        this.jceName = jceName;
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

    public String getCri() {
        return cri;
    }

    public void setCri(String cri) {
        this.cri = cri;
    }

    public LocalDate getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDate lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getHasHistory() {
        return hasHistory;
    }

    public void setHasHistory(String hasHistory) {
        this.hasHistory = hasHistory;
    }

    public String getCurrentUpdate() {
        return currentUpdate;
    }

    public void setCurrentUpdate(String currentUpdate) {
        this.currentUpdate = currentUpdate;
    }

    public String getSbReference() {
        return sbReference;
    }

    public void setSbReference(String sbReference) {
        this.sbReference = sbReference;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
