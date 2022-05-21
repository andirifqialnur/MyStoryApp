package com.aran.mystoryapp.repositories

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.aran.mystoryapp.api.ApiService
import com.aran.mystoryapp.data.DataStories
import com.aran.mystoryapp.response.ListStoryItem
import retrofit2.http.Header

class StoriesRepo(private val database: DataStories, private val apiService: ApiService) {
    fun getStoriesForPaging(header: String) : LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            pagingSourceFactory = {
                StoriesPaging(apiService, header)
            }
        ).liveData
    }
}