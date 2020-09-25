package com.example.medcords.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.medcords.model.Result


class UserDataSourceFactory(private var city:String,private var client_id:String) : DataSource.Factory<Int, Result>() {
     val userLiveDataSource = MutableLiveData<UserDataSource>()

    override fun create(): DataSource<Int, Result> {
        val userDataSource = UserDataSource(city,client_id)
        userLiveDataSource.postValue(userDataSource)
        return userDataSource
    }

}