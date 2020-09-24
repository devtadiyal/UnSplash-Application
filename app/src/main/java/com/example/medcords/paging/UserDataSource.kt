package com.example.medcords.paging

import androidx.paging.PageKeyedDataSource
import com.example.medcords.model.PhotosResponse
import com.example.medcords.model.Result
import com.example.medcords.network.ApiService
import com.example.medcords.network.ApiServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
/**
 * Created by Morris on 03,June,2019
 */
class UserDataSource : PageKeyedDataSource<Int, Result>() {
  override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Result>) {
    val service = ApiServiceBuilder.buildService(ApiService::class.java)
    val call = service.getUsers("london","297osa_zHv-Zeo5GlmDeHgW2j_f8jpDk1tHcrxaaMa4",params.key,PAGE_SIZE)
    call.enqueue(object : Callback<PhotosResponse> {
      override fun onResponse(call: Call<PhotosResponse>, response: Response<PhotosResponse>) {
        if (response.isSuccessful) {
          val apiResponse = response.body()!!
          val responseItems = apiResponse.results
          val key = if (params.key > 1) params.key - 1 else 0
          responseItems?.let {
            callback.onResult(responseItems, key)
          }
        }
      }
      override fun onFailure(call: Call<PhotosResponse>, t: Throwable) {
      }
    })
  }
  override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Result>) {
    val service = ApiServiceBuilder.buildService(ApiService::class.java)
    val call = service.getUsers("london","297osa_zHv-Zeo5GlmDeHgW2j_f8jpDk1tHcrxaaMa4",
      FIRST_PAGE,PAGE_SIZE)
    call.enqueue(object : Callback<PhotosResponse> {
      override fun onResponse(call: Call<PhotosResponse>, response: Response<PhotosResponse>) {
        if (response.isSuccessful) {
          val apiResponse = response.body()!!
          val responseItems = apiResponse.results
          responseItems?.let {
            callback.onResult(responseItems, null, FIRST_PAGE + 1)
          }
        }
      }
      override fun onFailure(call: Call<PhotosResponse>, t: Throwable) {
      }
    })
  }
  override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Result>) {
    val service = ApiServiceBuilder.buildService(ApiService::class.java)
    val call = service.getUsers("london","297osa_zHv-Zeo5GlmDeHgW2j_f8jpDk1tHcrxaaMa4",params.key,PAGE_SIZE)
    call.enqueue(object : Callback<PhotosResponse> {
      override fun onResponse(call: Call<PhotosResponse>, response: Response<PhotosResponse>) {
        if (response.isSuccessful) {
          val apiResponse = response.body()!!
          val responseItems = apiResponse.results
          val key = params.key + 1
          responseItems?.let {
            callback.onResult(responseItems, key)
          }
        }
      }
      override fun onFailure(call: Call<PhotosResponse>, t: Throwable) {
      }
    })
  }
  companion object {
    const val PAGE_SIZE = 30
    const val FIRST_PAGE = 1
  }
}