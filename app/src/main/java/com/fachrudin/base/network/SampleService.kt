package com.fachrudin.base.network

import com.fachrudin.base.entities.News
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author achmad.fachrudin
 * @date 21-May-19
 */
interface SampleService {
    @GET("top-headlines")
    fun getNewsAsync(
        @Query("apiKey") apiKey: String,
        @Query("country") country: String,
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int,
        @Query("q") q: String?
    ): Deferred<Response<News>>
}