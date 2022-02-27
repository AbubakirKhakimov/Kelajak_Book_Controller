package com.x.a_technologies.books_controller.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.hawk.Hawk
import com.x.a_technologies.books_controller.R
import com.x.a_technologies.books_controller.utils.ImageTracker
import com.x.a_technologies.books_controller.utils.LocaleManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && data.data != null && resultCode == RESULT_OK){
            if (ImageTracker.imageCallBack != null) {
                ImageTracker.imageCallBack!!.imageSelected(data.data!!)
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        Hawk.init(newBase).build()
        super.attachBaseContext(LocaleManager.setLocale(newBase))
    }

}