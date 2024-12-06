package com.dicoding.picodiploma.loginwithanimation.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.response.DetailStoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.result.Result
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: UserRepository) : ViewModel() {
    fun getStoryDetail(storyId: String, token: String): LiveData<Result<DetailStoryResponse>> {
        val result = MutableLiveData<Result<DetailStoryResponse>>()
        viewModelScope.launch {
            result.value = Result.Loading
            try {
                val response = repository.getStoryDetail(storyId, "Bearer $token")
                result.value = Result.Success(response)
            } catch (e: Exception) {
                result.value = Result.Error(e)
            }
        }
        return result
    }
}
