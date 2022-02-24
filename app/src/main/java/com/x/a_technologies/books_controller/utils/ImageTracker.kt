package com.x.a_technologies.books_controller.utils

import android.net.Uri

interface ImageCallBack{
    fun imageSelected(uri: Uri)
}

object ImageTracker {
    var imageCallBack:ImageCallBack? = null
}