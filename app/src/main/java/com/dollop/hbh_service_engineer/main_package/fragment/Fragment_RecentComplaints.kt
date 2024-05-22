package com.dollop.hbh_service_engineer.main_package.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dollop.hbh_service_engineer.basic.Retrofit.APIError
import com.dollop.hbh_service_engineer.basic.Retrofit.RetrofitClient
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.In_Progress
import com.dollop.hbh_service_engineer.basic.UtilityTools.StatusCodeConstant
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils
import com.dollop.hbh_service_engineer.databinding.FragmentRecentComplaintsBinding
import com.dollop.hbh_service_engineer.main_package.adapter.Adapter_Recent_Complaints
import com.dollop.hbh_service_engineer.main_package.model.ComplainListInProgressModel
import com.dollop.hbh_service_engineer.main_package.model.Complaint
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Fragment_RecentComplaints() : Fragment() {

    private var status = ""

    constructor(status:String):this()
    {
        this.status = status
    }

    private lateinit var binding: FragmentRecentComplaintsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRecentComplaintsBinding.inflate(inflater, container, false)

        return  binding.root
    }

    private fun checkStatus()
    {
        var TodaysDate = ""
        var Status = ""

        if(status == In_Progress)
        {
            Status = In_Progress
            setData(Status,TodaysDate)
        }
        else
        {
            val time = Calendar.getInstance().time
            val outputFormat = SimpleDateFormat("yyyy-MMM-dd", Locale.getDefault())
            TodaysDate = outputFormat.format(time)
            setData(Status,TodaysDate)
        }
    }

    private fun init()
    {
        checkStatus()
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun setData(status: String,TodaysDate:String)
    {
            val progressDialog = Utils.initProgressDialog(activity)
            val apiservice = RetrofitClient.client
            val data = Utils.GetSession()

            apiservice.getComplaintListApiRecent(data.token!!,status,TodaysDate,TodaysDate)!!.enqueue(object : retrofit2.Callback<ComplainListInProgressModel?> {
                override fun onResponse(
                    call: Call<ComplainListInProgressModel?>,
                    response: Response<ComplainListInProgressModel?>
                ) {
                    progressDialog.dismiss()
                    try {
                        if (response.code() == StatusCodeConstant.OK) {
                            assert(response.body() != null)
                            val complainlist = response.body()
                            val list = complainlist?.complaintList
                            setUpRecycler(list)

                        } else {
                            if (response.code() == StatusCodeConstant.BAD_REQUEST) {
                                assert(response.errorBody() != null)
                                val message = Gson().fromJson(
                                    response.errorBody()!!.charStream(),
                                    APIError::class.java
                                )
                                Utils.T(activity, message.message)
                            } else if (response.code() == StatusCodeConstant.UNAUTHORIZED) {
                                assert(response.errorBody() != null)
                                val message = Gson().fromJson(
                                    response.errorBody()!!.charStream(),
                                    APIError::class.java
                                )
                                Utils.T(activity, message.message)
                                Utils.UnAuthorizationToken(requireContext())
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ComplainListInProgressModel?>, t: Throwable) {
                    call.cancel()
                    t.printStackTrace()
                    progressDialog.dismiss()
                    Utils.E("getMessage::" + t.message)
                }
            })
        }

    private fun setUpRecycler(list: List<Complaint>?)
    {
        val Adapter = Adapter_Recent_Complaints(requireContext(),list)
        binding.rvInProgress.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        binding.rvInProgress.adapter = Adapter
    }
}