package com.aran.mystoryapp.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aran.mystoryapp.di.Injection
import com.aran.mystoryapp.repositories.StoriesRepo
import com.aran.mystoryapp.response.ListStoryItem

class StoryViewModel(private val storiesRepository: StoriesRepo) : ViewModel() {

    fun stories(header: String): LiveData<PagingData<ListStoryItem>> = storiesRepository.getStoriesForPaging(header).cachedIn(viewModelScope)

    class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T: ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(StoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return StoryViewModel(Injection.provideRepository(context)) as T
            }
            else throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}