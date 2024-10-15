package com.example.findwork;

import com.example.findwork.Job.JobDataAapi;

import java.util.List;

public interface OnDataLoadedCallback {
    void onDataLoaded(List<JobDataAapi.Job> jobList);
}
