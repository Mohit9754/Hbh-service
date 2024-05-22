package com.dollop.hbh_service_engineer.main_package.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dollop.hbh_service_engineer.R
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.Complain_Id
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.In_Progress
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.Mobile
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.Open
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.callStatus
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils.I
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils.dateConvertor
import com.dollop.hbh_service_engineer.main_package.activity.Activity_ComplaintsDetails
import com.dollop.hbh_service_engineer.main_package.model.Complaint

class Adapter_AllComplaint(var context:Context, private var arr: List<Complaint>?, private var status: String):RecyclerView.Adapter<Adapter_AllComplaint.MyViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_allcomplaints, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  arr!!.size
    }

    fun setStatus(iv_status:ImageView)
    {
        if (status == In_Progress)
        {
            iv_status.setImageResource(R.drawable.ic_inprogress)
            val layoutParams = iv_status.layoutParams
            layoutParams.width = 140
            layoutParams.height = 70
            iv_status.layoutParams = layoutParams

        }
        else if(status == Open)
        {
            iv_status.setImageResource(R.drawable.ic_pending)
            val layoutParams = iv_status.layoutParams
            layoutParams.width = 125
            layoutParams.height = 70
            iv_status.layoutParams = layoutParams
        }
    }

    fun checkEmptyData(data:String,textView: TextView,linearLayout: LinearLayout)
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tv_name.text = arr!![position].customerName
        holder.tv_complaints_id.text = arr!![position].complaintId
        holder.tv_category.text = Mobile

        setStatus(holder.iv_status)
        checkEmptyData(arr!![position].modelNo.toString(),holder.tv_model_no,holder.ll_model)
        checkEmptyData(arr!![position].machineSrNo.toString(),holder.tv_serial_no,holder.ll_serial_no)

        holder.tv_date.text = dateConvertor(arr!![position].createdDate.toString())

        holder.rl_closed.setOnClickListener {

            val bundle = Bundle()
            bundle.putString(Complain_Id, arr!![position].complaintId)
            bundle.putString(callStatus,status)

            I(context,Activity_ComplaintsDetails::class.java,bundle)
        }
    }

    class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    {
        val rl_closed = itemView.findViewById<RelativeLayout>(R.id.rl_closed)
        val tv_name = itemView.findViewById<TextView>(R.id.tv_name)
        val tv_complaints_id = itemView.findViewById<TextView>(R.id.tv_complaint_id)
        val tv_category = itemView.findViewById<TextView>(R.id.tv_category)
        val tv_model_no = itemView.findViewById<TextView>(R.id.tv_model)
        val tv_serial_no = itemView.findViewById<TextView>(R.id.tv_machinesrno)
        val tv_date = itemView.findViewById<TextView>(R.id.tv_date)
        val ll_model = itemView.findViewById<LinearLayout>(R.id.ll_model)
        val ll_serial_no = itemView.findViewById<LinearLayout>(R.id.ll_serial_no)
        val iv_status = itemView.findViewById<ImageView>(R.id.iv_status)
    }

}