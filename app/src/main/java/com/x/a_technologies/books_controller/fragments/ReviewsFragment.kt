package com.x.a_technologies.books_controller.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.x.a_technologies.books_controller.R
import com.x.a_technologies.books_controller.adapters.ReviewsAdapter
import com.x.a_technologies.books_controller.adapters.ReviewsAdapterCallBack
import com.x.a_technologies.books_controller.databinding.FragmentReviewsBinding
import com.x.a_technologies.books_controller.models.Book
import com.x.a_technologies.books_controller.models.Review
import com.x.a_technologies.books_controller.utils.MainViewModel
import java.util.*

class ReviewsFragment : Fragment(), ReviewsAdapterCallBack {

    lateinit var binding: FragmentReviewsBinding
    lateinit var viewModel: MainViewModel
    lateinit var currentBook: Book
    lateinit var reviewsAdapter: ReviewsAdapter
    var reviewsList = ArrayList<Review>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        initObservers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReviewsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentBook = arguments?.getSerializable("currentBook") as Book

        binding.bookName.text = getStringRes(currentBook.name)
        reviewsAdapter = ReviewsAdapter(reviewsList, currentBook.bookId, requireActivity(), this)
        binding.allReviewsRv.adapter = reviewsAdapter

        isLoading(true)
        viewModel.loadReviews(currentBook.bookId)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun initObservers(){
        viewModel.reviewsData.observe(requireActivity()){
            reviewsList.addAll(it)
            reviewsAdapter.notifyDataSetChanged()
            isLoading(false)
        }

        viewModel.error.observe(requireActivity()){
            Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show()
            isLoading(false)
        }
    }

    private fun getStringRes(text:String):String{
        return when(Locale.getDefault().language){
            "ru" -> "${getString(R.string.all_reviews_of)} \"$text\""
            "uz" -> "\"$text\" ${getString(R.string.all_reviews_of)}"
            else -> "${getString(R.string.all_reviews_of)} \"$text\""
        }
    }

    private fun isLoading(bool: Boolean){
        if (bool){
            binding.progressBar.visibility = View.VISIBLE
            binding.reviewsCount.visibility = View.GONE
        }else{
            binding.progressBar.visibility = View.GONE
            reviewsCountManager()
        }
    }

    private fun reviewsCountManager(){
        if (reviewsList.isEmpty()) {
            binding.reviewsCount.visibility = View.GONE
            binding.noReviewsTitle.visibility = View.VISIBLE
        }else{
            binding.noReviewsTitle.visibility = View.GONE
            binding.reviewsCount.visibility = View.VISIBLE
            binding.reviewsCount.text = "${reviewsList.size} ${getString(R.string.reviews)}"
        }
    }

    override fun itemDeletedListener() {
        reviewsCountManager()
    }

}