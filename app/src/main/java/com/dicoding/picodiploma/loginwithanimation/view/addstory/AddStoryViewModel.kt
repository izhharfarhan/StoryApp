package com.dicoding.picodiploma.loginwithanimation.view.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.dicoding.picodiploma.loginwithanimation.data.result.Result

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {
    fun addStory(token: String, photo: MultipartBody.Part, description: RequestBody): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()
        viewModelScope.launch {
            result.value = Result.Loading
            try {
                val response = repository.addStory(token, photo, description)
                result.value = Result.Success(response.message ?: "Story added successfully")
            } catch (e: Exception) {
                result.value = Result.Error(e)
            }
        }
        return result
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}


