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
import com.dollop.hbh_service_engineer.basic.UtilityTools.StatusCodeConstant
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils
import com.dollop.hbh_service_engineer.databinding.FragmentAllComplaintBinding
import com.dollop.hbh_service_engineer.main_package.adapter.Adapter_AllComplaint
import com.dollop.hbh_service_engineer.main_package.model.ComplainListInProgressModel
import com.dollop.hbh_service_engineer.main_package.model.Complaint
import com.dollop.hbh_service_engineer.main_package.model.FilterData
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response

class Fragment_AllComplaint() : Fragment() {

    private var status: String = ""
    private lateinit var filter:FilterData

    constructor(status: String,filter:FilterData) : this() {
        this.status = status
        this.filter = filter
    }

    lateinit var binding: FragmentAllComplaintBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAllComplaintBinding.inflate(inflater, container, false)

//        init()

        return binding.root
    }

    private fun init() {

        getComplaintListApi(status)
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun getComplaintListApi(callStatus:String) {

        val progressDialog = Utils.initProgressDialog(activity)

        Utils.E(filter.startDate+"  "+filter.endDate)

        RetrofitClient.client.getComplaintListApi(Utils.GetSession().token!!,callStatus,filter.startDate,filter.endDate,filter.customerName,filter.location,filter.callType)!!
            .enqueue(object : retrofit2.Callback<ComplainListInProgressModel?> {
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
        val Adapter = Adapter_AllComplaint(requireContext(), list,status)
        binding.rvClosed.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvClosed.adapter = Adapter
    }

}