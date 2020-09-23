package com.example.medcords.repository

import com.example.medcords.model.PhotosResponse
import com.example.medcords.model.random.RandomPhotoResponse
import com.example.medcords.network.Api

class Repository(private var api:Api) {

    suspend fun getPhotos(query: String,clientId: String,page:Int,per_page:Int):PhotosResponse{
        return api.getPhotosList(query,clientId,page,per_page) }


 suspend fun getRandomPhoto(clientId: String): RandomPhotoResponse {
        return api.getRandomPhoto(clientId) }

    }