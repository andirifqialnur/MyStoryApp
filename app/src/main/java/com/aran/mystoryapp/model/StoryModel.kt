package com.aran.mystoryapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryModel(
    var name: String? = null,
    var image: String? = null,
    var description: String? = null,
    var lat: Double? = null,
    var lon: Double? = null
) : Parcelable
