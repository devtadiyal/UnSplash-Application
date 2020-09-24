package com.example.medcords.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.medcords.model.Result

/**
 * Created by Morris on 03,June,2019
 */
class UserDataSourceFactory : DataSource.Factory<Int, Result>() {
     val userLiveDataSource = MutableLiveData<UserDataSource>()
    override fun create(): DataSource<Int, Result> {
        val userDataSource = UserDataSource()
        userLiveDataSource.postValue(userDataSource)
        return userDataSource
    }
}