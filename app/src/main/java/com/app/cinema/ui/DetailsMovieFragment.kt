package com.app.cinema.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import com.app.cinema.R
import com.app.cinema.adapters.MovieAdapter
import com.app.cinema.repository.MoviesRepository
import com.app.cinema.utilits.Resource
import com.app.cinema.utilits.parseDate
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_details_movie.*


class DetailsMovieFragment : Fragment() {

    private val args: DetailsMovieFragmentArgs by navArgs()

    lateinit var viewModel: DetailsMovieFragmentViewModel
    lateinit var similarMoviesAdapter: MovieAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details_movie, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val movie = args.movie

        val moviesRepository = MoviesRepository()
        val viewModelProviderFactory = activity?.application?.let {
            ViewModelProviderFactory(
                moviesRepository,
                it
            )
        }
        viewModel = ViewModelProvider(
            this,
            viewModelProviderFactory!!
        ).get(DetailsMovieFragmentViewModel::class.java)




        viewModel.similarMovies.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { movieResponse ->

                        similarMoviesAdapter.differ.submitList(movieResponse.movies.toList())

                    }

                }
                is Resource.Error -> {
                    response.message?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Loading -> {

                }
            }

        })





        Glide.with(this).load("https://image.tmdb.org/t/p/w500" + movie.backdropPath)
            .into(movie_image)

        Glide.with(this).load("https://image.tmdb.org/t/p/w500" + movie.posterPath)
            .into(movie_poster)

        movie_title.text = movie.title.trim()
        movie_vote.text = movie.voteAverage.toString().trim()
        movie_rating.rating = movie.voteAverage.toFloat() / 2
        movie_date.text = parseDate(movie.releaseDate)
        movie_description.text = movie.overview.trim()

    }

    private fun setupAdapter() {
        similarMoviesAdapter = MovieAdapter()
        rv_similarMovies.apply {
            adapter = similarMoviesAdapter


        }
    }

}