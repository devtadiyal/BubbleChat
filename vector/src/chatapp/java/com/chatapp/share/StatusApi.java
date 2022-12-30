package com.chatapp.share;

import com.google.gson.JsonElement;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface StatusApi {
    @Multipart
    @POST("status/status.php")
    Call<FileUploadResponse> upload(
            @Part("username") String userName,
             @Part MultipartBody.Part image
    );
    @Multipart
    @POST("status/status.php")
    Call<FileUploadResponse> upload1(
            @Part("username") RequestBody description,
             @Part MultipartBody.Part image
    );
}
