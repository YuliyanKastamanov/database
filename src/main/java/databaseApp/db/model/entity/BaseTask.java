package databaseApp.db.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@MappedSuperclass
public abstract class BaseTask extends BaseEntity {

    @Column(name = "task_number", nullable = false)
    private String taskNumber;
    @Column(name = "revision", nullable = false)
    private String revision;
    @Column(name = "soc_status")
    private String socStatus;
    @Column(name = "soc_description")
    private String socDescription;
    @Column(name = "comment")
    private String comment;
    /*@ManyToOne
    @JoinTable(
            name = "users_tasks",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id")
    )
    private UserEntity jce;
*/
    @Column(name = "jce_name")
    private String jceName;
    @Column(name = "coversheet_sap")
    private String coversheetSap;
    @Column(name = "coversheet_status")
    private String coversheetStatus;
    @Column(name = "created_m_job")
    private String createdMJob;
    @Column(name = "status_m_job")
    private String statusMJob;
    @Column(name = "cri", nullable = false, unique = true)
    private String cri;
    @Column(name = "last_update")
    private LocalDate lastUpdate;
    @Column(name = "has_history")
    private String hasHistory;
    @Column(name = "current_update")
    private String currentUpdate;
    @Column(name = "sb_reference")
    private String sbReference;

    public BaseTask() {
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

    /*public UserEntity getJce() {
        return jce;
    }
*/
    /*public void setJce(UserEntity jce) {
        this.jce = jce;
    }*/

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
}
