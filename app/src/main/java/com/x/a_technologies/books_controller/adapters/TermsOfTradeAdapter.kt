package com.x.a_technologies.books_controller.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.x.a_technologies.books_controller.R
import com.x.a_technologies.books_controller.databinding.TermsOfTradeItemLayoutBinding
import com.x.a_technologies.books_controller.datas.DatabaseRef
import com.x.a_technologies.books_controller.models.TermsOfTrade

class TermsOfTradeAdapter(val termsList:ArrayList<TermsOfTrade>, val context: Context):RecyclerView.Adapter<TermsOfTradeAdapter.ItemHolder>() {
    inner class ItemHolder(val binding: TermsOfTradeItemLayoutBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(TermsOfTradeItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = termsList[position]

        holder.binding.title.setText(item.title)
        holder.binding.mainText.setText(item.mainText)

        holder.binding.title.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                holder.binding.title.addTextChangedListener {
                    item.title = it.toString()
                }
            }
        }

        holder.binding.mainText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                holder.binding.mainText.addTextChangedListener {
                    item.mainText = it.toString()
                }
            }
        }

        holder.binding.deleteLayer.setOnClickListener {
            isLoading(true, holder)

            DatabaseRef.termsOfTradeRef.child(item.termsOfTradeId).removeValue().addOnCompleteListener {
                isLoading(false, holder)

                if (it.isSuccessful){
                    termsList.removeAt(position)
                    notifyDataSetChanged()
                }else{
                    Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
                }

            }
        }

    }

    override fun getItemCount(): Int {
        return termsList.size
    }

    private fun isLoading(bool: Boolean, holder: ItemHolder) {
        if (bool) {
            holder.binding.apply {
                deleteLayer.visibility = View.GONE
                deletingProgressBar.visibility = View.VISIBLE
            }
        } else {
            holder.binding.apply {
                deleteLayer.visibility = View.VISIBLE
                deletingProgressBar.visibility = View.GONE
            }
        }
    }

}