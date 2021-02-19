package com.app.cinema.repository

import com.app.cinema.api.RetrofitInstance

class MoviesRepository {

    suspend fun getTopRatedMovies(language: String, page: Int) =
        RetrofitInstance.api.getTopRatedMovies(language = language, page = page)

    suspend fun getPopularMovies(language: String, page: Int) =
        RetrofitInstance.api.getPopularMovies(language = language, page = page)

    suspend fun getUpcomingMovies(language: String, page: Int) =
        RetrofitInstance.api.getUpcomingMovies(language = language, page = page)

    suspend fun getSimilarMovies(movieId: Int) =
        RetrofitInstance.api.getSimilarMovies(movie_id = movieId)
}