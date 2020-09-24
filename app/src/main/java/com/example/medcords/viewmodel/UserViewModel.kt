package com.example.medcords.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.medcords.model.Result
import com.example.medcords.paging.UserDataSource
import com.example.medcords.paging.UserDataSourceFactory

class UserViewModel : ViewModel() {
    var userPagedList: LiveData<PagedList<Result>>
    private var liveDataSource: LiveData<UserDataSource>
    init {
        val itemDataSourceFactory = UserDataSourceFactory()
        liveDataSource = itemDataSourceFactory.userLiveDataSource
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(UserDataSource.PAGE_SIZE)
            .build()
        userPagedList = LivePagedListBuilder(itemDataSourceFactory, config)
            .build()
    }
}