package com.app.cinema.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.cinema.MovieApplication
import com.app.cinema.models.MovieResponse
import com.app.cinema.repository.MoviesRepository
import com.app.cinema.utilits.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class TopRatedMoviesFragmentViewModel(
    private val moviesRepository: MoviesRepository,
    app: Application
) : AndroidViewModel(app) {

    val topRatedMovies: MutableLiveData<Resource<MovieResponse>> = MutableLiveData()
    var topRatedMoviesPage = 1
    var topRatedMoviesResponse: MovieResponse? = null

    init {

        getTopRatedMovies("ru")

    }

    fun getTopRatedMovies(language: String) = viewModelScope.launch {
        safeCall(language)
    }


    private fun handleMovieResponse(response: Response<MovieResponse>): Resource<MovieResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                topRatedMoviesPage++
                if (topRatedMoviesResponse == null) {
                    topRatedMoviesResponse = it
                } else {
                    val oldMovies = topRatedMoviesResponse?.movies
                    val newMovies = it.movies
                    oldMovies?.addAll(newMovies)
                }
                return Resource.Success(topRatedMoviesResponse ?: it)
            }
        }

        return Resource.Error(response.message())
    }


    private suspend fun safeCall(language: String) {
        topRatedMovies.postValue(Resource.Loading())
        try {

            if (hasInternetConnection()) {
                val response =
                    moviesRepository.getTopRatedMovies(language = language, topRatedMoviesPage)
                topRatedMovies.postValue(handleMovieResponse(response))
            } else {
                topRatedMovies.postValue(Resource.Error("No Internet Connection"))
            }

        } catch (t: Throwable) {
            when (t) {
                is IOException -> topRatedMovies.postValue(Resource.Error("Network Failure"))
                else -> topRatedMovies.postValue(Resource.Error("Conversion Error"))
            }
        }
    }


    private fun hasInternetConnection(): Boolean {
        val connectionManager =
            getApplication<MovieApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectionManager.activeNetwork ?: return false
            val capabilities =
                connectionManager.getNetworkCapabilities(activeNetwork) ?: return false

            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectionManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }


}