package com.app.cinema.api

import com.app.cinema.models.MovieResponse
import com.app.cinema.utilits.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {

    @GET(GET_TOP_RATED)
    suspend fun getTopRatedMovies(
        @Query("api_key") api_key: String = API_KEY,
        @Query("language") language: String = "ru",
        @Query("page") page: Int = 1
    ): Response<MovieResponse>


    @GET(GET_POPULAR)
    suspend fun getPopularMovies(
        @Query("api_key") api_key: String = API_KEY,
        @Query("language") language: String = "ru",
        @Query("page") page: Int = 1
    ): Response<MovieResponse>

    @GET(GET_UPCOMING)
    suspend fun getUpcomingMovies(
        @Query("api_key") api_key: String = API_KEY,
        @Query("language") language: String = "ru",
        @Query("page") page: Int = 1
    ): Response<MovieResponse>


    @GET(GET_SIMILAR)
    suspend fun getSimilarMovies(
        @Query("api_key") api_key: String = API_KEY,
        @Path("movie_id") movie_id: Int
    ): Response<MovieResponse>

}