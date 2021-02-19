package com.app.cinema.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.cinema.repository.MoviesRepository
import java.lang.IllegalArgumentException

class ViewModelProviderFactory(
    private val moviesRepository: MoviesRepository,
    val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TopRatedMoviesFragmentViewModel::class.java) -> TopRatedMoviesFragmentViewModel(
                moviesRepository,
                app
            ) as T
            modelClass.isAssignableFrom(DetailsMovieFragmentViewModel::class.java) -> DetailsMovieFragmentViewModel(
                moviesRepository,
                app
            ) as T

            else -> throw IllegalArgumentException("ViewModelClass not found")
        }
    }


    //return TopRatedMoviesFragmentViewModel(moviesRepository, app) as T

}