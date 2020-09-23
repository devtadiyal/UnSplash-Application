package com.example.medcords.viewmodel

import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medcords.model.PhotosResponse
import com.example.medcords.model.random.RandomPhotoResponse
import com.example.medcords.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


const val ACCESS_KEY = "297osa_zHv-Zeo5GlmDeHgW2j_f8jpDk1tHcrxaaMa4"

class HomeViewModel(private var repository: Repository) : ViewModel() {
    private val job = Job()
    val loading = MutableLiveData<Boolean>()
    val shimmer = MutableLiveData<Boolean>()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    private val photos = MutableLiveData<PhotosResponse>()
    private val randomPhoto = MutableLiveData<RandomPhotoResponse>()

    val getPhoto: LiveData<PhotosResponse>
        get() = photos

    val getRandomPhoto: LiveData<RandomPhotoResponse>
        get() = randomPhoto

    //method for get random image from repository
    fun getRandomPhoto(view: View) {
        try {
            loading.value = true
            viewModelScope.launch {
                var getRandomPhoto = repository.getRandomPhoto(ACCESS_KEY)
                randomPhoto.postValue(getRandomPhoto)
                loading.value = false
            }
        } catch (e: Exception) {
            Toast.makeText(view.context, "Something went wrong...", Toast.LENGTH_SHORT).show()
        }

    }

    //method for get image list from repository
    fun getPhotos(view: View, page: Int) {
        viewModelScope.launch {
            try {
                shimmer.value = true
                var getPhotosList = repository.getPhotos("london", ACCESS_KEY, page, 30)
                photos.postValue(getPhotosList)
                shimmer.value = false
            } catch (e: Exception) {
                Toast.makeText(view.context, "Something went wrong...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //close coroutine scope
    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }


}
