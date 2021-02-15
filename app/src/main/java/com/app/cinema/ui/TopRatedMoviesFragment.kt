package com.app.cinema.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.cinema.R
import com.app.cinema.adapters.MovieAdapter
import com.app.cinema.repository.MoviesRepository
import com.app.cinema.utilits.QUERY_PAGE_SIZE
import com.app.cinema.utilits.Resource
import kotlinx.android.synthetic.main.fragment_top_rated_movies.*


class TopRatedMoviesFragment : Fragment() {

    lateinit var viewModel: TopRatedMoviesFragmentViewModel
    lateinit var movieAdapter: MovieAdapter

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_top_rated_movies, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
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
        ).get(TopRatedMoviesFragmentViewModel::class.java)


        viewModel.topRatedMovies.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newResponse ->
                        movieAdapter.differ.submitList(newResponse.movies.toList())
                        val totalPages = newResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.topRatedMoviesPage == totalPages
                        if (isLastPage) {
                            rv_top_rated_movies.setPadding(0, 0, 0, 0)
                        }

                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }

        })


    }


    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }


    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.getTopRatedMovies("ru")
                isScrolling = false
            }
        }
    }

    private fun setupAdapter() {
        movieAdapter = MovieAdapter()
        rv_top_rated_movies.apply {
            adapter = movieAdapter
            addOnScrollListener(this@TopRatedMoviesFragment.scrollListener)

        }
    }
}