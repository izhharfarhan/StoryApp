package com.dicoding.picodiploma.loginwithanimation.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.response.RegisterResponse
import com.dicoding.picodiploma.loginwithanimation.data.result.Result
import kotlinx.coroutines.launch

class SignupViewModel(private val repository: UserRepository) : ViewModel() {

    fun register(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> {
        val result = MutableLiveData<Result<RegisterResponse>>()
        viewModelScope.launch {
            result.value = Result.Loading
            try {
                val response = repository.register(name, email, password)
                result.value = Result.Success(response)
            } catch (exception: Exception) {
                result.value = Result.Error(exception)
            }
        }
        return result
    }
}
