package com.x.a_technologies.books_controller.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.x.a_technologies.books_controller.databinding.TermsOfTradeItemLayoutBinding
import com.x.a_technologies.books_controller.models.TermsOfTrade

class TermsOfTradeAdapter(val termsList:ArrayList<TermsOfTrade>):RecyclerView.Adapter<TermsOfTradeAdapter.ItemHolder>() {
    inner class ItemHolder(val binding: TermsOfTradeItemLayoutBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(TermsOfTradeItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = termsList[position]

        holder.binding.title.setText(item.title)
        holder.binding.mainText.setText(item.mainText)

        holder.binding.title.addTextChangedListener {
            item.title = it.toString()
        }

        holder.binding.mainText.addTextChangedListener {
            item.mainText = it.toString()
        }

    }

    override fun getItemCount(): Int {
        return termsList.size
    }
}