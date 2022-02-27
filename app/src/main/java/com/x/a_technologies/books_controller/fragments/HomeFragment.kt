package com.x.a_technologies.books_controller.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.x.a_technologies.books_controller.R
import com.x.a_technologies.books_controller.adapters.*
import com.x.a_technologies.books_controller.databinding.FragmentHomeBinding
import com.x.a_technologies.books_controller.datas.DatabaseRef
import com.x.a_technologies.books_controller.models.Book
import com.x.a_technologies.books_controller.utils.MainViewModel


class HomeFragment : Fragment(), CategoriesCallBack, BooksListCallBack, SearchResultsCallBack {

    lateinit var binding: FragmentHomeBinding
    lateinit var booksListAdapter: BooksListAdapter
    lateinit var categoriesAdapter: CategoriesAdapter
    lateinit var searchResultsAdapter: SearchResultsAdapter
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

        binding.navigationView.itemIconTintList = null
        check()

        binding.addBook.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addBookFragment, bundleOf(
                "categoriesList" to categoriesList
            ))
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadData()
        }

        binding.openDrawer.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.search.addTextChangedListener {
            if (it!!.isEmpty()){
                isSearched(false)
            }else{
                isSearched(true)
            }

            searchResultsAdapter.filter.filter(it.toString())
        }

        binding.navigationView.setNavigationItemSelectedListener {
            binding.drawerLayout.closeDrawers()

            when (it.itemId) {
                R.id.changeLanguage -> {
                    ChangeLanguageFragment().show(requireActivity().supportFragmentManager, "tag")
                }
                R.id.termsOfTrade -> {
                    findNavController().navigate(R.id.action_homeFragment_to_termsOfTradeFragment)
                }
                R.id.socialMediaRef -> {
                    findNavController().navigate(R.id.action_homeFragment_to_socialMediaRefFragment)
                }
            }
            true
        }

    }

    private fun check(){
        if (dataIsEmpty){
            dataIsEmpty = false

            categoriesAdapter = CategoriesAdapter(requireActivity(), categoriesList, this)
            booksListAdapter = BooksListAdapter(booksList, this, requireActivity())
            searchResultsAdapter = SearchResultsAdapter(booksList, this, requireActivity())

            loading()
        }

        initRecycler()
        loadData()
    }

    private fun initRecycler(){
        binding.categoriesRv.adapter = categoriesAdapter
        binding.booksListRv.adapter = booksListAdapter
        binding.searchResultsRv.adapter = searchResultsAdapter
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
            categoriesList.add(0, getString(R.string.category_all))

            categoriesAdapter.selectedItemPosition = 0
            categoriesAdapter.notifyDataSetChanged()
            binding.categoriesRv.scrollToPosition(0)

            binding.categoriesRv.visibility = View.VISIBLE
            binding.categoryShimmer.stopShimmer()
            binding.categoryShimmer.visibility = View.GONE
        }

        viewModel.booksData.observe(requireActivity()) {
            booksList.clear()
            booksList.addAll(it)

            booksListAdapter.notifyDataSetChanged()
            binding.swipeRefreshLayout.isRefreshing = false

            binding.booksListRv.visibility = View.VISIBLE
            binding.booksListShimmer.stopShimmer()
            binding.booksListShimmer.visibility = View.GONE
        }

        viewModel.error.observe(requireActivity()) {
            Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun loading() {
        binding.categoriesRv.visibility = View.GONE
        binding.booksListRv.visibility = View.GONE

        binding.categoryShimmer.visibility = View.VISIBLE
        binding.booksListShimmer.visibility = View.VISIBLE
    }

    private fun isSearched(bool:Boolean){
        if (bool){
            binding.categoriesRv.visibility = View.GONE
            binding.booksListRv.visibility = View.GONE
            binding.searchResultsRv.visibility = View.VISIBLE
        }else{
            binding.categoriesRv.visibility = View.VISIBLE
            binding.booksListRv.visibility = View.VISIBLE
            binding.searchResultsRv.visibility = View.GONE
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

    override fun searchResultsItemClickListener(item: Book) {
        findNavController().navigate(
            R.id.action_homeFragment_to_addBookFragment, bundleOf(
                "categoriesList" to categoriesList,
                "currentBook" to item
            )
        )
    }

}