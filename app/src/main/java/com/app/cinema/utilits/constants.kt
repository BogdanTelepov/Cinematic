package com.app.cinema.utilits


const val API_KEY = "bc676353b7e7680ff503b33310391170"
const val BASE_URL = "https://api.themoviedb.org"
const val GET_POPULAR = "3/movie/popular"
const val GET_TOP_RATED = "3/movie/top_rated"
const val GET_UPCOMING = "3/movie/upcoming"
const val GET_SIMILAR = "3/movie/{movie_id}/similar"
const val QUERY_PAGE_SIZE = 20

const val IMAGE_URL = "https://image.tmdb.org/t/p/w500"



fun parseDate(date: String): String {
    val array: List<String> = date.split("-")
    val year = array[0]
    val month = array[1]
    val day = array[2]
    return year.trim()

}


