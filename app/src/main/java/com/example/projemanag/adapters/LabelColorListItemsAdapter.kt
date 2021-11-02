package com.example.projemanag.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projemanag.databinding.ItemLabelColourBinding

class LabelColorListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<String>,
    private val mSelectedColor: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            ItemLabelColourBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]

        (holder as MyViewHolder).binding.apply {

            viewMain
                .setBackgroundColor(Color.parseColor(item))

            if (item == mSelectedColor) {
                ivSelectedColor.visibility =
                    View.VISIBLE
            } else {
                ivSelectedColor.visibility =
                    View.GONE
            }

            holder.itemView.setOnClickListener {

                if (onItemClickListener != null) {
                    onItemClickListener!!.onClick(position, item)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onClick(position: Int, color: String)
    }


    private class MyViewHolder(val binding: ItemLabelColourBinding) :
        RecyclerView.ViewHolder(binding.root)
}