package com.x.a_technologies.books_controller.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.x.a_technologies.books_controller.R
import com.x.a_technologies.books_controller.databinding.BooksListItemLayoutBinding
import com.x.a_technologies.books_controller.models.Book

interface SearchResultsCallBack{
    fun searchResultsItemClickListener(item: Book)
}

class SearchResultsAdapter(val allBooksList:ArrayList<Book>, val searchResultsCallBack: SearchResultsCallBack, val context: Context)
    : RecyclerView.Adapter<SearchResultsAdapter.ItemHolder>(), Filterable {
    inner class ItemHolder(val binding: BooksListItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    var filteredList = ArrayList<Book>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(BooksListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = filteredList[position]

        holder.binding.bookName.text = item.name
        holder.binding.bookAuthorName.text = item.author
        holder.binding.bookRentPrice.text = "${item.rentPrice} ${context.getString(R.string.sum)}"
        holder.binding.bookSellingPrice.text = "${item.sellingPrice} ${context.getString(R.string.sum)}"
        Glide.with(holder.binding.root).load(item.imageUrl).into(holder.binding.bookImage)
        holder.binding.isAvailable.setBackgroundColor(getAvailableColor(item.count))
        holder.binding.isAvailable.text = getAvailableText(item.count)

        holder.binding.root.setOnClickListener {
            searchResultsCallBack.searchResultsItemClickListener(item)
        }

    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    private fun getAvailableText(count: Int):String{
        return if (count == 0){
            context.getString(R.string.notAvailable)
        }else{
            context.getString(R.string.available)
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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val filteredBooks = ArrayList<Book>()

                if (charSequence.toString().isNotEmpty()){
                    for (item in allBooksList){
                        if (item.name.lowercase().contains(charSequence.toString().lowercase())){
                            filteredBooks.add(item)
                        }
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = filteredBooks
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filteredList.clear()
                filteredList.addAll(filterResults.values as Collection<Book>)
                notifyDataSetChanged()
            }
        }
    }

}