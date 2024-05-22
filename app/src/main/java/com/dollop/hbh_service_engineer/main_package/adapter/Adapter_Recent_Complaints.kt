package com.dollop.hbh_service_engineer.main_package.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dollop.hbh_service_engineer.R
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils.dateConvertor
import com.dollop.hbh_service_engineer.main_package.activity.Activity_ComplaintsDetails
import com.dollop.hbh_service_engineer.main_package.model.Complaint
import com.google.android.material.card.MaterialCardView

class Adapter_Recent_Complaints(val context: Context, private var arr: List<Complaint>?): RecyclerView.Adapter<Adapter_Recent_Complaints.MyViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recent_complaints, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.arr!!.size
    }

    private fun checkEmptyData(data:String,textView: TextView,linearLayout: LinearLayout)
    {
        if(data.isEmpty())
        {
            linearLayout.visibility = View.GONE
        }
        else
        {
            textView.text = data
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tv_name.text = arr!![position].customerName

        Utils.Picasso(arr!![position].represantativeprofilePic.toString(),holder.img_profile,R.drawable.mystry)

        checkEmptyData(arr!![position].machineSrNo.toString(),holder.tv_serial_no,holder.ll_serial_no)
        checkEmptyData(arr!![position].modelNo.toString(),holder.tv_model_no,holder.ll_modelno)

        holder.tv_complaints_id.text = arr!![position].complaintId
        holder.tv_date.text = dateConvertor(arr!![position].createdDate!!)

        holder.mcv_home_progress.setOnClickListener {

            val bundle = Bundle()
            bundle.putString(Constants.Complain_Id, arr!![position].complaintId)
            bundle.putString(Constants.callStatus, Constants.In_Progress)
            Utils.I(context, Activity_ComplaintsDetails::class.java, bundle)
        }
    }

    class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    {
        val img_profile = itemView.findViewById<ImageView>(R.id.img_profile)
        val tv_name = itemView.findViewById<TextView>(R.id.tv_name)
        val tv_serial_no = itemView.findViewById<TextView>(R.id.tv_serial_no)
        val tv_model_no = itemView.findViewById<TextView>(R.id.tv_model_no)
        val tv_complaints_id = itemView.findViewById<TextView>(R.id.tv_complaints_id)
        val tv_date = itemView.findViewById<TextView>(R.id.tv_date)
        val mcv_btn = itemView.findViewById<MaterialCardView>(R.id.mcv_btn)
        val ll_serial_no = itemView.findViewById<LinearLayout>(R.id.ll_serial_no)
        val ll_modelno = itemView.findViewById<LinearLayout>(R.id.ll_modelno)
        val mcv_home_progress = itemView.findViewById<MaterialCardView>(R.id.mcv_home_progress)

    }
}