package com.example.yourapp.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.WebSocketManager
import com.example.myapplication.domain.model.LoginRequest
import com.example.myapplication.domain.model.LoginResponse
import com.example.myapplication.domain.usecase.LoginUseCase
import com.example.myapplication.presentation.login.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//@HiltViewModel
//class LoginViewModel @Inject constructor(
//    private val loginUseCase: LoginUseCase
//) : ViewModel()  {
//
//    var username by mutableStateOf("")
//    var password by mutableStateOf("")
//    var loginState by mutableStateOf<Result<LoginResponse>?>(null)
//        private set
//
//    fun login() {
//        val request = LoginRequest(username = username, password = password)
//
//        viewModelScope.launch {
//            loginState = loginUseCase(request)
//        }
//    }
//}
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var loginState by mutableStateOf<LoginState>(LoginState.Idle)
        private set

    private val _navigateToCameraList = MutableSharedFlow<Unit>()
    val navigateToCameraList = _navigateToCameraList.asSharedFlow()

    fun login() {
        val request = LoginRequest(username = username, password = password)

        viewModelScope.launch {
            loginState = LoginState.Loading
            val result = loginUseCase(request)
            loginState = result.fold(
                onSuccess = {
                    _navigateToCameraList.emit(Unit)  // just navigate
                    LoginState.Success(it)

                },
                onFailure = { LoginState.Error(it.message) }
            )
        }
    }
}