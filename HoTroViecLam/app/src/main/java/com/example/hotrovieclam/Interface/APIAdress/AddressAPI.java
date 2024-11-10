package com.example.hotrovieclam.Interface.APIAdress;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AddressAPI {
    @GET("api-tinhthanh/{A}/{B}.htm")
    Call<ApiResponse> getAddressList(@Path("A") int type, @Path("B") String id);
}
