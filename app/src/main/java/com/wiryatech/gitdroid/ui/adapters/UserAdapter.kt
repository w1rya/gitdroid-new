package com.wiryatech.gitdroid.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.wiryatech.gitdroid.R
import com.wiryatech.gitdroid.data.models.User
import kotlinx.android.synthetic.main.item_user.view.*

class UserAdapter : RecyclerView.Adapter<UserAdapter.ListViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return newItem.login == oldItem.login
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return newItem == oldItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false))
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val user =  differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this)
                .load(user.avatar_url)
                .apply(RequestOptions().override(64).placeholder(R.drawable.ic_round_account_circle_64))
                .into(iv_avatar)

            tv_username.text = user.login
            tv_user_type.text = user.type

            setOnClickListener {
                onItemClickListener?.let { it(user) }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((User) -> Unit)? = null

    fun setOnItemClickListener(listener: (User) -> Unit) {
        onItemClickListener = listener
    }

    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

}