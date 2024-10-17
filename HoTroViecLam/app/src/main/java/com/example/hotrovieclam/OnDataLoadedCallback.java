package com.example.hotrovieclam;

import com.example.hotrovieclam.Model.Job;

import java.util.List;

public interface OnDataLoadedCallback {
    void onDataLoaded(List<Job> jobList);
}
