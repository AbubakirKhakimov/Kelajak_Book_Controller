package com.x.a_technologies.books_controller.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.x.a_technologies.books_controller.databinding.BooksListItemLayoutBinding
import com.x.a_technologies.books_controller.models.Book

interface BooksListCallBack{
    fun booksListItemClickListener(position: Int)
}

class BooksListAdapter(val booksList:ArrayList<Book>, val booksListCallBack: BooksListCallBack):RecyclerView.Adapter<BooksListAdapter.ItemHolder>() {
    inner class ItemHolder(val binding: BooksListItemLayoutBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(BooksListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = booksList[position]

        holder.binding.bookName.text = item.name
        holder.binding.bookAuthorName.text = item.author
        holder.binding.bookRentPrice.text = "${item.rentPrice} so'm"
        holder.binding.bookSellingPrice.text = "${item.sellingPrice} so'm"
        Glide.with(holder.binding.root).load(item.imageUrl).into(holder.binding.bookImage)
        holder.binding.isAvailable.setBackgroundColor(getAvailableColor(item.count))
        holder.binding.isAvailable.text = getAvailableText(item.count)

        holder.binding.rootLayout.setOnClickListener {
            booksListCallBack.booksListItemClickListener(position)
        }

    }

    override fun getItemCount(): Int {
        return booksList.size
    }

    fun getAvailableText(count: Int):String{
        return if (count == 0){
            "Not available"
        }else{
            "Available"
        }
    }

    private fun getAvailableColor(count:Int):Int{
        return when {
            count == 0 -> {
                Color.parseColor("#EA2B1F")
            }
            count <= 3 -> {
                Color.parseColor("#F3B61F")
            }
            count > 3 -> {
                Color.parseColor("#4CAF50")
            }
            else -> Color.parseColor("#4CAF50")
        }
    }

}