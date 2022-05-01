package com.aran.mystoryapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryModel(
    var name: String? = null,
    var image: String? = null,
    var description: String? = null,
    var createAt: String? = null
) : Parcelable
