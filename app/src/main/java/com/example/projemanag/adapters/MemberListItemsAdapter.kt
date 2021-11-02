package com.example.projemanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projemanag.R
import com.example.projemanag.databinding.ItemMemberBinding
import com.example.projemanag.models.User
import com.example.projemanag.utils.Constants

open class MemberListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            ItemMemberBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        (holder as MyViewHolder).binding.apply {

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(ivMemberImage)

            tvMemberName.text = model.name
            tvMemberEmail.text = model.email

            if (model.selected) {
                ivSelectedMember.visibility =
                    View.VISIBLE
            } else {
                ivSelectedMember.visibility =
                    View.GONE
            }

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    if (model.selected) {
                        onClickListener!!.onClick(position, model, Constants.UN_SELECT)
                    } else {
                        onClickListener!!.onClick(position, model, Constants.SELECT)
                    }
                }
            }
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }


    interface OnClickListener {
        fun onClick(position: Int, user: User, action: String)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(val binding: ItemMemberBinding) : RecyclerView.ViewHolder(binding.root)
}