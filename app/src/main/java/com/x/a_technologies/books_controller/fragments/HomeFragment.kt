package com.x.a_technologies.books_controller.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.x.a_technologies.books_controller.R
import com.x.a_technologies.books_controller.adapters.BooksListAdapter
import com.x.a_technologies.books_controller.adapters.BooksListCallBack
import com.x.a_technologies.books_controller.adapters.CategoriesAdapter
import com.x.a_technologies.books_controller.adapters.CategoriesCallBack
import com.x.a_technologies.books_controller.databinding.FragmentHomeBinding
import com.x.a_technologies.books_controller.datas.DatabaseRef
import com.x.a_technologies.books_controller.models.Book
import com.x.a_technologies.books_controller.utils.MainViewModel


class HomeFragment : Fragment(), CategoriesCallBack, BooksListCallBack {

    lateinit var binding: FragmentHomeBinding
    lateinit var booksListAdapter: BooksListAdapter
    lateinit var categoriesAdapter: CategoriesAdapter
    lateinit var viewModel:MainViewModel

    var dataIsEmpty = true
    var categoriesList:ArrayList<String> = ArrayList()
    var booksList:ArrayList<Book> = ArrayList()

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
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        check()

        binding.addBook.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addBookFragment, bundleOf(
                "categoriesList" to categoriesList
            ))
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadData()
        }

    }

    private fun check(){
        if (dataIsEmpty){
            dataIsEmpty = false

            categoriesAdapter = CategoriesAdapter(requireActivity(), categoriesList, this)
            booksListAdapter = BooksListAdapter(booksList, this)
            initRecycler()

            loadData()
        }else{
            initRecycler()
        }
    }

    private fun initRecycler(){
        binding.categoriesRv.adapter = categoriesAdapter
        binding.booksListRv.adapter = booksListAdapter
    }

    private fun loadData(){
        binding.swipeRefreshLayout.isRefreshing = true
        viewModel.loadCategories()
        viewModel.loadAllBooks()
    }

    private fun initObservers(){
        viewModel.categoriesData.observe(requireActivity()) {
            categoriesList.clear()
            categoriesList.addAll(it)
            categoriesList.add(0, "Barchasi")

            categoriesAdapter.selectedItemPosition = 0
            categoriesAdapter.notifyDataSetChanged()
            binding.categoriesRv.scrollToPosition(0)
        }

        viewModel.booksData.observe(requireActivity()) {
            booksList.clear()
            booksList.addAll(it)

            booksListAdapter.notifyDataSetChanged()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        viewModel.error.observe(requireActivity()) {
            Toast.makeText(requireActivity(), "Error!", Toast.LENGTH_SHORT).show()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun clickCategory(position: Int) {
        binding.swipeRefreshLayout.isRefreshing = true

        if (position == 0){
            viewModel.loadAllBooks()
        }else{
            viewModel.loadBooksByCategories(categoriesList[position])
        }
    }

    override fun booksListItemClickListener(position: Int) {
        findNavController().navigate(
            R.id.action_homeFragment_to_addBookFragment, bundleOf(
                "categoriesList" to categoriesList,
                "currentBook" to booksList[position]
            )
        )
    }

}