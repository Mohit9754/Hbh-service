package com.dollop.hbh_service_engineer.main_package.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dollop.hbh_service_engineer.R
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils
import com.dollop.hbh_service_engineer.main_package.model.Customer

class Adapter_Customer_list (var context: Context, private var arr: List<Customer>, private var tv_customer_name:EditText, private var ll_customer:LinearLayout): RecyclerView.Adapter<Adapter_Customer_list.MyViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_customer_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  arr.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tv_customer_name.text = arr[position].customerName

        holder.ll_customer_select.setOnClickListener {

            tv_customer_name.setText(arr[position].customerName)
            Utils.collapseView(ll_customer)

        }
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val tv_customer_name = itemView.findViewById<TextView>(R.id.tv_customer_name)
        val ll_customer_select = itemView.findViewById<LinearLayout>(R.id.ll_customer_select)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterdNames:List<Customer>) {

        arr = filterdNames
        notifyDataSetChanged()
    }
}