package org.marino.tfgpagao.data.remote.responses

data class ResponseWithToken<T>(
    val body: T?,
    val token: String?
)