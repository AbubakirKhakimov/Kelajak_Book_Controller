package com.x.a_technologies.books_controller.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.x.a_technologies.books_controller.R
import com.x.a_technologies.books_controller.databinding.ReviewsItemLayoutBinding
import com.x.a_technologies.books_controller.datas.DatabaseRef
import com.x.a_technologies.books_controller.models.Keys
import com.x.a_technologies.books_controller.models.Review
import com.x.a_technologies.books_controller.models.User
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

interface ReviewsAdapterCallBack{
    fun itemDeletedListener()
}

class ReviewsAdapter(val reviewsList:ArrayList<Review>, val bookId:String, val context: Context, val reviewsAdapterCallBack: ReviewsAdapterCallBack)
    :RecyclerView.Adapter<ReviewsAdapter.ItemHolder>() {
    inner class ItemHolder(val binding: ReviewsItemLayoutBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(ReviewsItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = reviewsList[position]

        holder.binding.apply {
            userName.text = getUserName(item.senderUser)
            reviewText.text = item.reviewText
            reviewSendDate.text = getDate(item.reviewSendTimeMillis)

            if (item.senderUser.imageUrl == null){
                userImage.setImageResource(R.drawable.user_profile_human)
            }else{
                Glide.with(root).load(item.senderUser.imageUrl).into(userImage)
            }

            if (item.adminMessage == null){
                adminEnterMessageLayout.visibility = View.VISIBLE
                adminMessageLayout.visibility = View.GONE
            }else{
                adminMessage.text = item.adminMessage
                adminMessageSendDate.text = getDate(item.adminMessageSendTimeMillis!!)
                adminEnterMessageLayout.visibility = View.GONE
                adminMessageLayout.visibility = View.VISIBLE
            }

            reviewEdt.addTextChangedListener {
                if (it!!.isEmpty()){
                    sendReview.visibility = View.GONE
                }else{
                    sendReview.visibility = View.VISIBLE
                }
            }

            sendReview.setOnClickListener {
                isSendLoading(true, holder)

                item.adminMessage = reviewEdt.text.toString()
                item.adminMessageSendTimeMillis = Date().time
                DatabaseRef.reviewsRef.child(bookId).child(item.reviewId).setValue(item).addOnCompleteListener {
                    if (it.isSuccessful){
                        reviewEdt.clearFocus()
                        notifyItemChanged(position)
                    }else{
                        item.adminMessage = null
                        item.adminMessageSendTimeMillis = null
                        Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
                    }
                    isSendLoading(false, holder)
                }
            }

            deleteReview.setOnClickListener {
                isDeleteLoading(true, holder)
                val queryHashMap = hashMapOf<String, Any?>(
                    "${Keys.REVIEWS_KEY}/$bookId/${item.reviewId}" to null,
                    "${Keys.USERS_REVIEWS_IDS}/${item.senderUser.phoneNumber}/${item.reviewId}" to null
                )

                DatabaseRef.rootRef.updateChildren(queryHashMap).addOnCompleteListener {
                    if (it.isSuccessful){
                        reviewsList.removeAt(position)
                        reviewsAdapterCallBack.itemDeletedListener()
                        notifyDataSetChanged()
                    }else{
                        Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
                    }
                    isDeleteLoading(false, holder)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return reviewsList.size
    }

    private fun getUserName(item:User):String{
        return "${item.firstName} ${item.lastName}"
    }

    private fun getDate(timeMillis:Long):String{
        return SimpleDateFormat("dd MMMM yyyy").format(Date(timeMillis))
    }

    private fun isSendLoading(bool:Boolean, holder: ItemHolder){
        if (bool){
            holder.binding.sendReview.visibility = View.GONE
            holder.binding.sendingProgressBar.visibility = View.VISIBLE
        }else{
            holder.binding.sendReview.visibility = View.VISIBLE
            holder.binding.sendingProgressBar.visibility = View.GONE
        }
    }

    private fun isDeleteLoading(bool:Boolean, holder: ItemHolder){
        if (bool){
            holder.binding.deleteReview.visibility = View.GONE
            holder.binding.deletingProgressBar.visibility = View.VISIBLE
        }else{
            holder.binding.deleteReview.visibility = View.VISIBLE
            holder.binding.deletingProgressBar.visibility = View.GONE
        }
    }

}