package com.app.cinema.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.cinema.repository.MoviesRepository

class ViewModelProviderFactory(
    private val moviesRepository: MoviesRepository,
    val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TopRatedMoviesFragmentViewModel(moviesRepository, app) as T
    }


}