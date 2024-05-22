package com.dollop.hbh_service_engineer.main_package.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dollop.hbh_service_engineer.R
import com.dollop.hbh_service_engineer.basic.Database.UserDataHelper
import com.dollop.hbh_service_engineer.basic.Retrofit.APIError
import com.dollop.hbh_service_engineer.basic.Retrofit.RetrofitClient
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.Today_Assigned
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.In_Progress
import com.dollop.hbh_service_engineer.basic.UtilityTools.StatusCodeConstant
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils
import com.dollop.hbh_service_engineer.databinding.FragmentHomeBinding
import com.dollop.hbh_service_engineer.main_package.adapter.Adapter_home_tab
import com.dollop.hbh_service_engineer.main_package.model.EngineerProfile
import com.dollop.hbh_service_engineer.main_package.model.EngineerProfileModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response

class Fragment_HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        init()
        return binding.root
    }

    private fun init() {

        setupTab()
        getProfileDetails()
    }

    private fun setProfilePic(profilePic:String)
    {
        Utils.Picasso(profilePic,binding.ivProfile,R.drawable.mystry)
    }

    private fun setFullName(firstName:String,lastName:String)
    {
        binding.tvFullname.text = "$firstName $lastName"
    }

    private fun setAssigned(assigned:String)
    {
        binding.tvAssigned.text = assigned
    }

    private fun setInProgress(inProgress:String)
    {
        binding.tvInprogress.text = inProgress
    }

    private fun setClosed(closed:String)
    {
        binding.tvClosed.text = closed
    }

    @SuppressLint("SetTextI18n")
    private fun setProfileDetails(list: EngineerProfile)
    {
        val obj = UserDataHelper(requireContext())
        obj.updateAllData(list)

        setProfilePic(list.profilePic)
        setFullName(list.userName,list.lastName)
        setAssigned(list.assigned)
        setInProgress(list.inProgress)
        setClosed(list.closed)
    }

    private fun getProfileDetails()
    {
        RetrofitClient.client.getEngineerProfileApi(Utils.GetSession().token!!)!!.enqueue(object : retrofit2.Callback<EngineerProfileModel?> {
            override fun onResponse(
                call: Call<EngineerProfileModel?>,
                response: Response<EngineerProfileModel?>
            ) {
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        assert(response.body() != null)
                        val EngineerProfileModel = response.body()
                        val list = EngineerProfileModel?.engineerProfile
                        setProfileDetails(list!!)

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

            override fun onFailure(call: Call<EngineerProfileModel?>, t: Throwable) {
                call.cancel()
                t.printStackTrace()
                Utils.E("getMessage::" + t.message)
            }
        })
    }

    private fun setupTab()
    {
        val tabLayout = binding.tlHome
        val viewPager = binding.vpHome

        viewPager.adapter = Adapter_home_tab(requireActivity() as AppCompatActivity)
        TabLayoutMediator(tabLayout,viewPager){

                tab,position ->tab.text = when(position)
        {
            0 -> Today_Assigned
            1 -> In_Progress
            else -> Today_Assigned
        }
        }.attach()
    }
}