package com.dollop.hbh_service_engineer.main_package.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dollop.hbh_service_engineer.R

class Adapter_Expertise(var context: Context, var list: List<String>): RecyclerView.Adapter<Adapter_Expertise.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expertise, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tv_expertise.text = list[position]

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tv_expertise = itemView.findViewById<TextView>(R.id.tv_expertise)
    }

}


