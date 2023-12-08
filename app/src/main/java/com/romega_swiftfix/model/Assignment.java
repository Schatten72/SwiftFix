package com.romega_swiftfix.model;

public class Assignment {
    private String assignmentTime;
    private String details;
    private String status;
    private String jobId;

    private String repairmanId;
    private String repairmanLocation;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public Assignment() {
        // Default constructor
    }
    public String getAssignmentTime() {
        return assignmentTime;
    }

    public void setAssignmentTime(String assignmentTime) {
        this.assignmentTime = assignmentTime;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getRepairmanId() {
        return repairmanId;
    }

    public void setRepairmanId(String repairmanId) {
        this.repairmanId = repairmanId;
    }

    public String getRepairmanLocation() {
        return repairmanLocation;
    }

    public void setRepairmanLocation(String repairmanLocation) {
        this.repairmanLocation = repairmanLocation;
    }




}
