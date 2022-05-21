package com.aran.mystoryapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aran.mystoryapp.response.ListStoryItem
import com.aran.mystoryapp.response.StoriesResponse

@Database(
    entities = [ListStoryItem::class],
    version = 1,
    exportSchema = false
)

abstract class DataStories : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE : DataStories? = null

        @JvmStatic
        fun getDatabase(context: Context): DataStories {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext, DataStories::class.java,"data_stories"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}