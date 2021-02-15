package com.app.cinema.repository

import com.app.cinema.api.RetrofitInstance

class MoviesRepository {

    suspend fun getTopRatedMovies(language: String, page: Int) =
        RetrofitInstance.api.getTopRatedMovies(language = language, page = page)
}