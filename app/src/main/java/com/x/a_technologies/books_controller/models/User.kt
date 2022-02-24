package com.x.a_technologies.books_controller.models

data class User(
    var phoneNumber:String = "",
    var firstName:String = "",
    var lastName:String = "",
    var imageUrl:String? = null
)