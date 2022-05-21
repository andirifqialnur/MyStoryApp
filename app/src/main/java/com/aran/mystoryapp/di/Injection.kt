package com.aran.mystoryapp.di

import android.content.Context
import com.aran.mystoryapp.api.ApiConfig
import com.aran.mystoryapp.data.DataStories
import com.aran.mystoryapp.repositories.StoriesRepo

object Injection {

    fun provideRepository(context: Context): StoriesRepo {
        val database  = DataStories.getDatabase(context)
        val apiService = ApiConfig.getApiService()

        return StoriesRepo(database, apiService)
    }
}