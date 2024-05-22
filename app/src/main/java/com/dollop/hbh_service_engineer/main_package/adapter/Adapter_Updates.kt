package com.dollop.hbh_service_engineer.main_package.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.dollop.hbh_service_engineer.R
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.Attachement
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.Check_In_Comments
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.Check_Out_Comments
import com.dollop.hbh_service_engineer.main_package.activity.Activity_Attachment
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils.I
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils.convert24HourToAMPM
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils.dateConvertor
import com.dollop.hbh_service_engineer.main_package.model.Update
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Adapter_Updates(var context: Context, private var arr: List<Update>, var status:String): RecyclerView.Adapter<Adapter_Updates.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_updates, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    private  fun checkStatus(imageView: ImageView)
    {
        if(status == Constants.In_Progress)
        {
            imageView.setImageResource(R.drawable.ic_inprogress)
            val layoutParams = imageView.layoutParams
            layoutParams.width = 150
            layoutParams.height = 70
            imageView.layoutParams = layoutParams
        }
    }

    private fun checkAttachment(attachment:String,linearLayout: LinearLayout)
    {
        if(attachment.isEmpty())
        {
            linearLayout.visibility = View.GONE
        }
        else
        {
            linearLayout.setOnClickListener {

                val bundle = Bundle()
                bundle.putString(Constants.Attachement,Attachement)
                I(context, Activity_Attachment::class.java,bundle)
            }
        }
    }

    private fun setCheckInAndOutDate(date:String,textView: TextView)
    {
        val checkin = dateConvertor(date)
        textView.text = checkin.subSequence(0,11)
    }

    private fun setCheckInAndOutTime(time:String,textView: TextView)
    {
        val formattime =  time.subSequence(11,16).toString()
        textView.text = convert24HourToAMPM(formattime)
    }

    private fun setUpTab(tabLayout:TabLayout,viewPager:ViewPager2,checkInComment:String,checkOutComment:String)
    {
        viewPager.adapter = Adapter_Comments(context as AppCompatActivity, checkInComment, checkOutComment)

        TabLayoutMediator(tabLayout,viewPager){

                tab,position ->tab.text = when(position)
        {
            0 -> Check_In_Comments
            1 -> Check_Out_Comments
            else -> Check_In_Comments
        }
        }.attach()

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        checkStatus(holder.iv_status)

        checkAttachment(arr[position].attachement.toString(),holder.ll_attachment)

        setCheckInAndOutDate(arr[position].checkIn.toString(),holder.tv_checkin_date)
        setCheckInAndOutDate(arr[position].checkOut.toString(),holder.tv_checkout_date)

        setCheckInAndOutTime(arr[position].checkIn.toString(), holder.tv_checkin_time)
        setCheckInAndOutTime(arr[position].checkOut.toString(), holder.tv_checkout_time)

        setUpTab(holder.tl_update_sample,holder.vp_update_sample,arr[position].checkInComment.toString(),arr[position].checkOutComment.toString())
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tl_update_sample = itemView.findViewById<TabLayout>(R.id.tl_update_sample)
        val vp_update_sample = itemView.findViewById<ViewPager2>(R.id.vp_update_sample)
        val iv_status = itemView.findViewById<ImageView>(R.id.iv_status)
        val tv_checkin_date = itemView.findViewById<TextView>(R.id.tv_checkin_date)
        val tv_checkout_date = itemView.findViewById<TextView>(R.id.tv_checkout_date)
        val tv_checkin_time = itemView.findViewById<TextView>(R.id.tv_checkin_time)
        val tv_checkout_time = itemView.findViewById<TextView>(R.id.tv_checkout_time)
        val ll_attachment = itemView.findViewById<LinearLayout>(R.id.ll_attachment)

    }
}
