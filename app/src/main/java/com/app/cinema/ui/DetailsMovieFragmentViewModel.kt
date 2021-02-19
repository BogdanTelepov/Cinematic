package com.app.cinema.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
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

class DetailsMovieFragmentViewModel(
    private val moviesRepository: MoviesRepository,
    application: Application
) : AndroidViewModel(application) {

    val similarMovies: MutableLiveData<Resource<MovieResponse>> = MutableLiveData()

    private var similarMoviesResponse: MovieResponse? = null


    fun getSimilarMovies(movie_id: Int) = viewModelScope.launch {
        safeCall(movie_id)
    }

    private fun handleMovieResponse(response: Response<MovieResponse>): Resource<MovieResponse> {
        if (response.isSuccessful) {
            response.body()?.let {

                if (similarMoviesResponse == null) {
                    similarMoviesResponse = it
                } else {
                    val oldMovies = similarMoviesResponse?.movies
                    val newMovies = it.movies
                    oldMovies?.addAll(newMovies)
                }
                return Resource.Success(similarMoviesResponse ?: it)
            }
        }

        return Resource.Error(response.message())
    }


    private suspend fun safeCall(movie_id: Int) {
        similarMovies.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response =
                    moviesRepository.getSimilarMovies(movie_id)
                similarMovies.postValue(handleMovieResponse(response))
            } else {
                similarMovies.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> similarMovies.postValue(Resource.Error("Network Failure"))
                else -> similarMovies.postValue(Resource.Error("Conversion Error"))

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
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectionManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }


}