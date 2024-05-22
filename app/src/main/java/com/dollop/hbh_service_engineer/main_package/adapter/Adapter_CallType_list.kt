package com.dollop.hbh_service_engineer.main_package.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dollop.hbh_service_engineer.R
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils
import com.dollop.hbh_service_engineer.main_package.model.CallType

class Adapter_CallType_list(var context: Context, private var arr:List<CallType>, private var et_calltype: EditText, private var ll_calltype_list: LinearLayout, private var iv_calltype:ImageView): RecyclerView.Adapter<Adapter_CallType_list.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_customer_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tv_calltype.text = arr[position].callType

        holder.ll_calltype.setOnClickListener {

            et_calltype.setText(arr[position].callType)
            Utils.collapseView(ll_calltype_list)
            iv_calltype.setImageResource(R.drawable.ic_drop_down)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tv_calltype = itemView.findViewById<TextView>(R.id.tv_customer_name)
        val ll_calltype = itemView.findViewById<LinearLayout>(R.id.ll_customer_select)
    }
}