package com.x.a_technologies.books_controller.fragments

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.x.a_technologies.books_controller.R
import com.x.a_technologies.books_controller.adapters.CategorySpinnerAdapter
import com.x.a_technologies.books_controller.databinding.AddCategoryLayoutBinding
import com.x.a_technologies.books_controller.databinding.FragmentAddBookBinding
import com.x.a_technologies.books_controller.datas.DatabaseRef
import com.x.a_technologies.books_controller.models.Book
import com.x.a_technologies.books_controller.utils.ImageCallBack
import com.x.a_technologies.books_controller.utils.ImageTracker
import com.x.a_technologies.books_controller.utils.MainViewModel
import java.util.*
import kotlin.collections.ArrayList

class AddBookFragment : Fragment(), ImageCallBack {

    lateinit var binding: FragmentAddBookBinding
    lateinit var viewModel:MainViewModel
    lateinit var spinnerAdapter: CategorySpinnerAdapter
    lateinit var uploadedBookId:String
    var currentBook: Book? = null
    var categoriesList = ArrayList<String>()
    var isImageSelected: Boolean = false

    lateinit var name: String
    lateinit var author: String
    lateinit var moreInformation: String
    lateinit var count: String
    lateinit var language: String
    lateinit var alphabetType: String
    lateinit var pagesCount: String
    lateinit var coatingType: String
    lateinit var manufacturingCompany: String
    lateinit var rentPrice: String
    lateinit var sellingPrice: String

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
        binding = FragmentAddBookBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadArguments()

        binding.addCategory.setOnClickListener {
            showAlertDialog()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.save.setOnClickListener {
            check()
        }

        binding.chooseImage.setOnClickListener {
            chooseImage()
        }

        binding.toReviews.setOnClickListener {
            findNavController().navigate(R.id.action_addBookFragment_to_reviewsFragment, bundleOf(
                "currentBook" to currentBook
            ))
        }

    }

    private fun loadArguments(){
        currentBook = arguments?.getSerializable("currentBook") as Book?
        categoriesList.addAll(arguments?.getStringArrayList("categoriesList")!!)
        categoriesList.removeAt(0)
        initViews()
    }

    private fun initViews(){
        spinnerAdapter = CategorySpinnerAdapter(categoriesList)
        binding.categorySpinner.adapter = spinnerAdapter

        if (currentBook != null){
            binding.labelText.text = getString(R.string.edit_book)
            binding.toReviews.visibility = View.VISIBLE
            Glide.with(requireActivity()).load(currentBook!!.imageUrl).into(binding.bookImage)
            binding.bookName.setText(currentBook!!.name)
            binding.bookAuthorName.setText(currentBook!!.author)
            binding.bookCount.setText(currentBook!!.count.toString())
            binding.bookPagesCount.setText(currentBook!!.pagesCount.toString())
            binding.bookLanguage.setText(currentBook!!.language)
            binding.bookAlphabetType.setText(currentBook!!.alphabetType)
            binding.bookCoatingType.setText(currentBook!!.coatingType)
            binding.bookManufacturingCompany.setText(currentBook!!.manufacturingCompany)
            binding.bookRentPrice.setText(currentBook!!.rentPrice)
            binding.bookSellingPrice.setText(currentBook!!.sellingPrice)
            binding.bookMoreInformation.setText(currentBook!!.moreInformation)
            binding.categorySpinner.setSelection(categoriesList.indexOf(currentBook!!.category))
        }
    }

    private fun check() {
        name = binding.bookName.text.toString().trim()
        author = binding.bookAuthorName.text.toString().trim()
        moreInformation = binding.bookMoreInformation.text.toString().trim()
        count = binding.bookCount.text.toString().trim()
        language = binding.bookLanguage.text.toString().trim()
        alphabetType = binding.bookAlphabetType.text.toString().trim()
        pagesCount = binding.bookPagesCount.text.toString().trim()
        coatingType = binding.bookCoatingType.text.toString().trim()
        manufacturingCompany = binding.bookManufacturingCompany.text.toString().trim()
        rentPrice = binding.bookRentPrice.text.toString().trim()
        sellingPrice = binding.bookSellingPrice.text.toString().trim()

        if (name.isEmpty() || author.isEmpty() || count.isEmpty() || language.isEmpty() ||
            pagesCount.isEmpty() || coatingType.isEmpty() || manufacturingCompany.isEmpty() ||
            rentPrice.isEmpty() || sellingPrice.isEmpty() || moreInformation.isEmpty() || alphabetType.isEmpty()
        ) {
            Toast.makeText(requireActivity(), getString(R.string.please_fill_in_all_the_boxes), Toast.LENGTH_SHORT).show()
        } else {
            if (currentBook == null){
                addBook()
            }else{
                editBook()
            }
        }
    }

    private fun editBook(){
        uploadedBookId = currentBook!!.bookId
        isLoading(true)

        if (isImageSelected){
            viewModel.writeImageDatabase(binding.bookImage, uploadedBookId)
        }else{
            saveBookDatabase(
                currentBook!!.imageUrl,
                currentBook!!.searchedCount,
                currentBook!!.addedTimeMillis
            )
        }
    }

    private fun addBook(){
        if (isImageSelected) {
            isLoading(true)
            uploadedBookId = DatabaseRef.booksRef.push().key!!
            viewModel.writeImageDatabase(binding.bookImage, uploadedBookId)
        } else {
            Toast.makeText(requireActivity(), getString(R.string.please_choose_a_picture), Toast.LENGTH_SHORT).show()
        }
    }

    private fun initObservers(){
        viewModel.imageUrlData.observe(requireActivity()){
            if (currentBook == null){
                saveBookDatabase(it)
            }else{
                saveBookDatabase(
                    it,
                    currentBook!!.searchedCount,
                    currentBook!!.addedTimeMillis
                )
            }
        }

        viewModel.error.observe(requireActivity()){
            isLoading(false)
            Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveBookDatabase(imageUrl: String, searchedCount:Int = 0, addedTimeMillis:Long = Date().time) {
        val book = Book(
            uploadedBookId,
            imageUrl,
            count.toInt(),
            name,
            author,
            moreInformation,
            addedTimeMillis,
            language,
            alphabetType,
            pagesCount.toInt(),
            coatingType,
            manufacturingCompany,
            categoriesList[binding.categorySpinner.selectedItemPosition],
            searchedCount,
            rentPrice,
            sellingPrice
        )

        DatabaseRef.booksRef.child(uploadedBookId).setValue(book).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(requireActivity(), getString(R.string.successfully_saved), Toast.LENGTH_SHORT).show()
                isLoading(false)

                findNavController().popBackStack()
            } else {
                isLoading(false)
                Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAlertDialog() {
        val alertDialog = AlertDialog.Builder(requireActivity()).create()
        val bindingDialog = AddCategoryLayoutBinding.inflate(layoutInflater)
        alertDialog.setView(bindingDialog.root)

        bindingDialog.saveButton.setOnClickListener {
            val categoryName = bindingDialog.categoryName.text.toString()

            if (categoryName.isEmpty()) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.please_enter_a_category_name),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                DatabaseRef.categoriesRef.push().setValue(categoryName)
                categoriesList.add(categoryName)
                spinnerAdapter.notifyDataSetChanged()
                alertDialog.dismiss()
            }
        }

        bindingDialog.closeButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    override fun imageSelected(uri: Uri) {
        Glide.with(requireActivity()).load(uri).into(binding.bookImage)
        isImageSelected = true
    }

    private fun isLoading(bool: Boolean) {
        if (bool) {
            binding.save.visibility = View.GONE
            binding.saveProgressBar.visibility = View.VISIBLE
        } else {
            binding.save.visibility = View.VISIBLE
            binding.saveProgressBar.visibility = View.GONE
        }
    }

    private fun chooseImage() {
        val intentChooser = Intent()
        intentChooser.type = "image/"
        intentChooser.action = Intent.ACTION_GET_CONTENT
        ImageTracker.imageCallBack = this
        startActivityForResult(intentChooser, 1)
    }

}