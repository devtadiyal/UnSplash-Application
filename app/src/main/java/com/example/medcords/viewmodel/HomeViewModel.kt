package com.example.medcords.viewmodel

import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medcords.model.random.RandomPhotoResponse
import com.example.medcords.repository.Repository
import kotlinx.coroutines.launch


const val ACCESS_KEY = "297osa_zHv-Zeo5GlmDeHgW2j_f8jpDk1tHcrxaaMa4"

class HomeViewModel(private var repository: Repository) : ViewModel() {
    val loading = MutableLiveData<Boolean>()
    val shimmer = MutableLiveData<Boolean>()
    private val randomPhoto = MutableLiveData<RandomPhotoResponse>()

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
}
