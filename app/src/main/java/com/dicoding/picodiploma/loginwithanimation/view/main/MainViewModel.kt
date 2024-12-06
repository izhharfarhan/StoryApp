package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.response.StoryResponse
import kotlinx.coroutines.launch
import com.dicoding.picodiploma.loginwithanimation.data.result.Result
import kotlinx.coroutines.flow.first

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getStories(): LiveData<Result<StoryResponse>> {
        val result = MutableLiveData<Result<StoryResponse>>()
        viewModelScope.launch {
            result.value = Result.Loading
            try {
                val token = repository.getSession().first().token
                val response = repository.getStories("Bearer $token")
                result.value = Result.Success(response)
            } catch (e: Exception) {
                result.value = Result.Error(e)
            }
        }
        return result
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}