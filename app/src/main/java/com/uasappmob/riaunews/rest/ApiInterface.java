package com.uasappmob.riaunews.rest;

import com.uasappmob.riaunews.model.News;
import com.uasappmob.riaunews.response.ResponseCreate;
import com.uasappmob.riaunews.response.ResponseLogin;
import com.uasappmob.riaunews.response.ResponseNews;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {
    // Login
    @FormUrlEncoded
    @POST("login.php")
    Call<ResponseLogin> login(
            @Field("username") String username,
            @Field("password") String password
    );

    // Register
    @Multipart
    @POST("register.php")
    Call<ResponseCreate> register(
            @Part("username") RequestBody username,
            @Part("password") RequestBody password,
            @Part("name") RequestBody name,
            @Part("phone_number") RequestBody phone_number,
            @Part("email") RequestBody email,
            @Part MultipartBody.Part image
    );

    // Create News
    @Multipart
    @POST("createNews.php")
    Call<ResponseCreate> createNews(
            @Part("title") RequestBody title,
            @Part("category") RequestBody category,
            @Part("content") RequestBody content,
            @Part MultipartBody.Part cover
    );

    // Read News
    @GET("readNews.php")
    Call<ResponseNews<List<News>>> getNews();

    // Update News
    @FormUrlEncoded
    @POST("updateNews.php")
    Call<ResponseCreate> updateNews(
            @Query("id") String id,
            @Field("title") String title,
            @Field("category") String category,
            @Field("content") String content
    );

    // Delete News
    @DELETE("deleteNews.php")
    Call<ResponseNews<String>> deleteNews(@Query("id") String id);
}
