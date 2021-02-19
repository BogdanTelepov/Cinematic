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

    val popularMovies: MutableLiveData<Resource<MovieResponse>> = MutableLiveData()
    var popularMoviesPage = 1
    var popularMoviesResponse: MovieResponse? = null

    val upcomingMovies: MutableLiveData<Resource<MovieResponse>> = MutableLiveData()
    var upcomingMoviesPage = 1
    var upcomingMovieResponse: MovieResponse? = null


    init {

        getTopRatedMovies("ru")
        getPopularMovies("ru")
        getUpcomingMovies("ru")
    }

    fun getTopRatedMovies(language: String) = viewModelScope.launch {
        safeCall(language)
    }

    fun getPopularMovies(language: String) = viewModelScope.launch {
        safeCallPopularMovies(language)
    }

    fun getUpcomingMovies(language: String) = viewModelScope.launch {
        safeCallUpcomingMovies(language)
    }





    private fun handleUpcomingMoviesResponse(response: Response<MovieResponse>): Resource<MovieResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                if (upcomingMovieResponse == null) {
                    upcomingMovieResponse = it
                } else {
                    val upcomingMovies = upcomingMovieResponse?.movies
                    val newMovies = it.movies
                    upcomingMovies?.addAll(newMovies)
                }

                return Resource.Success(upcomingMovieResponse ?: it)
            }
        }

        return Resource.Error(response.message())


    }

    private suspend fun safeCallUpcomingMovies(language: String) {
        upcomingMovies.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response =
                    moviesRepository.getUpcomingMovies(language, page = upcomingMoviesPage)
                upcomingMovies.postValue(handleUpcomingMoviesResponse(response))
            } else {
                upcomingMovies.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> upcomingMovies.postValue(Resource.Error("Network Failure"))
                else -> upcomingMovies.postValue(Resource.Error("Conversion Error"))
            }
        }
    }


    private fun handlePopularMoviesResponse(response: Response<MovieResponse>): Resource<MovieResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                if (popularMoviesResponse == null) {
                    popularMoviesResponse = it
                } else {
                    val popularMovies = popularMoviesResponse?.movies
                    val newMovies = it.movies
                    popularMovies?.addAll(newMovies)
                }

                return Resource.Success(popularMoviesResponse ?: it)
            }
        }

        return Resource.Error(response.message())


    }

    private suspend fun safeCallPopularMovies(language: String) {
        popularMovies.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = moviesRepository.getPopularMovies(language, page = popularMoviesPage)
                popularMovies.postValue(handlePopularMoviesResponse(response))
            } else {
                popularMovies.postValue(Resource.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> popularMovies.postValue(Resource.Error("Network Failure"))
                else -> popularMovies.postValue(Resource.Error("Conversion Error"))
            }
        }
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