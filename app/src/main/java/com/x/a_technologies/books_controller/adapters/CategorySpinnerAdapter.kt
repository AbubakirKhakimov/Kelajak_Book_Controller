package com.x.a_technologies.books_controller.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.x.a_technologies.books_controller.R

class CategorySpinnerAdapter(val categoryList:ArrayList<String>):BaseAdapter() {

    override fun getCount(): Int {
        return categoryList.size
    }

    override fun getItem(position: Int): Any {
        return categoryList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView = convertView ?: LayoutInflater.from(parent?.context)
            .inflate(R.layout.category_spinner_item_layout, parent, false)

        itemView.findViewById<TextView>(R.id.categoryName).text = categoryList[position]

        return itemView
    }

}