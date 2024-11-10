package com.example.hotrovieclam.Interface.APIAdress;

import com.example.hotrovieclam.Model.Address;

import java.util.List;

public class ApiResponse {
    private String status;
    private List<Address> data;

    public String getStatus() {
        return status;
    }

    public List<Address> getData() {
        return data;
    }
}
