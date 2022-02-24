package com.x.a_technologies.books_controller.utils

import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.x.a_technologies.books_controller.datas.DatabaseRef
import com.x.a_technologies.books_controller.models.Book
import com.x.a_technologies.books_controller.models.Review
import java.io.ByteArrayOutputStream

class MainViewModel:ViewModel() {

    val categoriesData = MutableLiveData<ArrayList<String>>()
    val booksData = MutableLiveData<ArrayList<Book>>()
    val imageUrlData = MutableLiveData<String>()
    val reviewsData = MutableLiveData<ArrayList<Review>>()
    val error = MutableLiveData<String>()

    fun loadCategories(){
        DatabaseRef.categoriesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoriesList = ArrayList<String>()

                for (item in snapshot.children){
                    categoriesList.add(item.value.toString())
                }

                categoriesData.value = categoriesList
            }
            override fun onCancelled(mError: DatabaseError) {
                error.value = "Error!"
            }
        })
    }

    fun loadAllBooks(){
        DatabaseRef.booksRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val allBooksList = ArrayList<Book>()

                for (item in snapshot.children){
                    allBooksList.add(item.getValue(Book::class.java)!!)
                }
                allBooksList.reverse()

                booksData.value = allBooksList
            }

            override fun onCancelled(mError: DatabaseError) {
                error.value = "Error!"
            }
        })
    }

    fun loadBooksByCategories(category:String){
        val categoryEqualQuery = DatabaseRef.booksRef.orderByChild("category").equalTo(category)

        categoryEqualQuery.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val booksByCategoriesList = ArrayList<Book>()

                for (item in snapshot.children){
                    booksByCategoriesList.add(item.getValue(Book::class.java)!!)
                }
                booksByCategoriesList.reverse()

                booksData.value = booksByCategoriesList
            }
            override fun onCancelled(mError: DatabaseError) {
                error.value = "Error!"
            }
        })
    }

    fun loadReviews(bookId:String){
        val reviewsQuery = DatabaseRef.reviewsRef.child(bookId).orderByChild("reviewSendTimeMillis")

        reviewsQuery.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val reviewsList = ArrayList<Review>()

                for (item in snapshot.children){
                    reviewsList.add(item.getValue(Review::class.java)!!)
                }
                reviewsList.reverse()

                reviewsData.value = reviewsList
            }

            override fun onCancelled(mError: DatabaseError) {
                error.value = "Error!"
            }
        })
    }

    fun writeImageDatabase(bookImage: ImageView, bookId: String) {
        val bitmap: Bitmap = bookImage.drawable.toBitmap()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val imageRef = DatabaseRef.storageRef.child(bookId)
        val uploadTask = imageRef.putBytes(byteArray)

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                imageUrlData.value = task.result.toString()
            } else {
                error.value = "Error!"
            }
        }
    }

}