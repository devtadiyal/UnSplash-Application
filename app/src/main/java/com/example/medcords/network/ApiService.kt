package com.example.medcords.network

import com.example.medcords.model.PhotosResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
/**
 * Created by Morris on 03,June,2019
 */
interface ApiService {
    @GET("search/photos")
    fun getUsers( @Query("query") query: String,
                  @Query("client_id") client: String,
                  @Query("page") page: Int,
                  @Query("per_page") per_page: Int): Call<PhotosResponse>
}