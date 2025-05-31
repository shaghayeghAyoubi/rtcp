package com.example.myapplication.data.remote.network

import com.example.myapplication.domain.repository.TokenRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

//class TokenInterceptor(private val token: String) : Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val request = chain.request().newBuilder()
//            .addHeader("Authorization", "Bearer $token")
//            .build()
//        return chain.proceed(request)
//    }
//}

class TokenInterceptor @Inject constructor(
    private val tokenRepository: TokenRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // Get token from repository (blocking read, since Interceptor is sync)
        val token = runBlocking {
            tokenRepository.getAccessToken().firstOrNull().also {
                println("Token fetched in interceptor: $it")
            }
        }

        return if (!token.isNullOrEmpty()) {
            val newRequest = original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(original)
        }
    }
}