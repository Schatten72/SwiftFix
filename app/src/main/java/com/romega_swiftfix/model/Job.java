package com.romega_swiftfix.model;

public class Job {
    private String jobNumber;
    private String description;
    private double latitude;
    private double longitude;
    private String userEmail;
    private String imageUrl;

    private Object timestamp;



    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }



    public Job() {
        // Default constructor required for Firestore
    }

    public Job(String jobNumber, String description, double latitude, double longitude, String userEmail) {
        this.jobNumber = jobNumber;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userEmail = userEmail;
    }
    public String getJobId() {
        return jobNumber; // JobNumber serves as the job ID
    }
}
