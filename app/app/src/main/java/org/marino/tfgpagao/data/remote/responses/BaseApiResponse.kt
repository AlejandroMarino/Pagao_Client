package org.marino.tfgpagao.data.remote.responses


import com.google.gson.Gson
import okhttp3.ResponseBody
import org.marino.tfgpagao.utils.NetworkResult
import retrofit2.Response

abstract class BaseApiResponse {

    suspend fun <T, R> safeApiCall(
        apiCall: suspend () -> Response<R>,
        transform: (R) -> T
    ): NetworkResult<T> {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return NetworkResult.Success(transform(body))
                }
            }
            return error("${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                return if (body != null) {
                    NetworkResult.Success(body)
                } else {
                    NetworkResult.SuccessNoData()
                }
            }
            val errorMessage = if (response.code() == 401 || response.code() == 403) {
                "Session Expired"
            } else {
                parseErrorMessage(response.errorBody())
            }
            return error(errorMessage)
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    suspend fun <T> safeApiCallWithToken(apiCall: suspend () -> Response<T>): NetworkResult<ResponseWithToken<T>> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                val authorizationHeader = response.headers()["Authorization"]

                return if (body != null) {
                    NetworkResult.Success(ResponseWithToken(body, authorizationHeader))
                } else {
                    NetworkResult.SuccessNoData()
                }
            }
            val errorMessage = if (response.code() == 401) {
                "Session Expired"
            } else {
                parseErrorMessage(response.errorBody())
            }
            return error(errorMessage)
        } catch (e: Exception) {
            error(e.message ?: e.toString())
        }
    }

    private fun parseErrorMessage(errorBody: ResponseBody?): String {
        return try {
            val errorResponse = Gson().fromJson(errorBody?.string(), ErrorResponse::class.java)
            errorResponse.message ?: "Unknown error"
        } catch (e: Exception) {
            "Session expired"
        }
    }

    private fun <T> error(errorMessage: String): NetworkResult<T> =
        NetworkResult.Error(errorMessage)

}