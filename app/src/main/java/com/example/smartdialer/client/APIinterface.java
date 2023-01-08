package com.example.smartdialer.client;



import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface APIinterface {
    @Multipart
    @POST
    Call<ResponseBody> upload(@Url String url, @Part MultipartBody.Part file);

    @GET
    Call<ResponseBody> getResponse(@Url String url);

    @FormUrlEncoded
    @POST
    Call<ResponseBody> postResponseBody(@Url String url, @FieldMap Map<String, String> param);

    @Headers({"Content-Type: text/plain"})
    @POST
    Call<ResponseBody> postBodyToResponseBody(@Url String url, @Body RequestBody body);

}
