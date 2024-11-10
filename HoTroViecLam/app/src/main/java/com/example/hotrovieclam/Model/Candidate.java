package com.example.hotrovieclam.Model;

public class Candidate {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    private String jobID;
    private String id;
    private String title;
    private String salary;
    private String status;

    public Candidate(String title, String salary, String status) {
        this.title = title;
        this.salary = salary;
        this.status = status;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Candidate() {

    }
    public String getTitle() {
        return title;
    }

    public String getSalary() {
        return salary;
    }

    public String getStatus() {
        return status;
    }
}
