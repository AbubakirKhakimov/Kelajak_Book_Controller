package com.x.a_technologies.books_controller.models

data class Review(
   var reviewId:String = "",
   var senderUser: User = User(),
   var reviewText:String = "",
   var reviewSendTimeMillis:Long = 0L,
   var adminMessage:String? = null,
   var adminMessageSendTimeMillis:Long? = null
)