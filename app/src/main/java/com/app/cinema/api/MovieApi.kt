package com.app.cinema.api

import com.app.cinema.models.MovieResponse
import com.app.cinema.utilits.API_KEY
import com.app.cinema.utilits.GET_TOP_RATED
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {

    @GET(GET_TOP_RATED)
    suspend fun getTopRatedMovies(
        @Query("api_key") api_key: String = API_KEY,
        @Query("language") language: String = "ru",
        @Query("page") page: Int = 1
    ): Response<MovieResponse>
}