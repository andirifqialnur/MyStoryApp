package com.aran.mystoryapp.helper

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.aran.mystoryapp.model.UserModel
import kotlinx.coroutines.launch

class SharedViewModel(private val pref: UserPreference) : ViewModel() {

    fun getUser() : LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            pref.signOut()
        }
    }
}