package com.app.cinema.models


import com.google.gson.annotations.SerializedName
import retrofit2.Response
import java.io.Serializable

data class MovieResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val movies: MutableList<Movie>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
) : Serializable