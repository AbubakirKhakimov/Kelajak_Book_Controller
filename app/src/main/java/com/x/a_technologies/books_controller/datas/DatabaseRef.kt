package com.x.a_technologies.books_controller.datas

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.x.a_technologies.books_controller.models.Keys

object DatabaseRef {
    val rootRef = Firebase.database.reference
    val usersRef = Firebase.database.getReference(Keys.USERS_KEY)
    val booksRef = Firebase.database.getReference(Keys.BOOKS_KEY)
    val reviewsRef = Firebase.database.getReference(Keys.REVIEWS_KEY)
    val usersReviewsIdsRef = Firebase.database.getReference(Keys.USERS_REVIEWS_IDS)
    val categoriesRef = Firebase.database.getReference(Keys.CATEGORIES_KEY)
    val termsOfTradeRef = Firebase.database.getReference(Keys.TERMS_OF_TRADE_KEY)
    val socialMediaRef = Firebase.database.getReference(Keys.SOCIAL_MEDIA_REFERENCES_KEY)
    val usersStorageRef = FirebaseStorage.getInstance().getReference(Keys.USERS_STORAGE_KEY)
    val storageRef = FirebaseStorage.getInstance().getReference("books image")
}