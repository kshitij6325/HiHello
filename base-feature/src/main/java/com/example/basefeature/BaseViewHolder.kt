package com.example.basefeature

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseViewHolder<in D>(binding: ViewBinding, onItemClick: ((Int) -> Unit)? = null) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            onItemClick?.invoke(adapterPosition)
        }
    }

    abstract fun onBind(data: D)
}

