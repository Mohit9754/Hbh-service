package com.dollop.hbh_service_engineer.main_package.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dollop.hbh_service_engineer.R
import com.dollop.hbh_service_engineer.basic.Database.UserDataHelper
import com.dollop.hbh_service_engineer.basic.Retrofit.APIError
import com.dollop.hbh_service_engineer.basic.Retrofit.RetrofitClient
import com.dollop.hbh_service_engineer.basic.UtilityTools.StatusCodeConstant
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils.I
import com.dollop.hbh_service_engineer.databinding.BottomSheetLogoutBinding
import com.dollop.hbh_service_engineer.databinding.FragmentProfileBinding
import com.dollop.hbh_service_engineer.main_package.activity.Activity_EditProfileActivity
import com.dollop.hbh_service_engineer.main_package.activity.Activity_LoginActivity
import com.dollop.hbh_service_engineer.main_package.activity.Activity_Settings
import com.dollop.hbh_service_engineer.main_package.adapter.Adapter_Expertise
import com.dollop.hbh_service_engineer.main_package.model.EngineerProfile
import com.dollop.hbh_service_engineer.main_package.model.EngineerProfileModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response

class Fragment_ProfileFragment : Fragment(),View.OnClickListener {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var bottomsheetbinding: BottomSheetLogoutBinding
    private lateinit var dialog:BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
//        init()
        return binding.root
    }

    private fun init()
    {
        binding.mcvSetting.setOnClickListener(this)
        binding.llLogout.setOnClickListener(this)
        binding.mcvEditProfile.setOnClickListener(this)
        getProfileApi()
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun setProfileImage(profilePic:String)
    {
        Utils.Picasso(profilePic,binding.ivProfile,R.drawable.mystry)
    }

    private fun setFullName(userName:String,lastName:String)
    {
        binding.tvFullname.text = "$userName $lastName"
    }

    private fun setMobileNo(mobileNo:String)
    {
        binding.tvMobileNo.text = "+91 $mobileNo"
    }

    private fun setPendingData(assigned:String)
    {
        binding.tvPending.text = assigned
    }

    private fun setInProgressData(inProgress:String)
    {
        binding.tvInprogress.text = inProgress
    }

    private fun setClosedData(closed:String)
    {
        binding.tvClosed.text = closed
    }

    private fun setUpExpertise(expertise:String)
    {
        val list = expertise.split(",")
        val Adapter = Adapter_Expertise(requireContext(),list)

        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START

        binding.rvEpertise.layoutManager = layoutManager
        binding.rvEpertise.adapter = Adapter

    }

    private fun setProfileDetails(list: EngineerProfile?)
    {
        val obj = UserDataHelper(requireContext())
        if (list != null) {
            obj.updateAllData(list)
        }

        setProfileImage(list!!.profilePic)
        setFullName(list.userName,list.lastName)
        setMobileNo(list.mobile)
        setPendingData(list.assigned)
        setInProgressData(list.inProgress)
        setClosedData(list.closed)
        setUpExpertise(list.experties)

    }

    private fun getProfileApi()
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
                        setProfileDetails(list)

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

    private fun showLogoutSheet() {

        dialog = BottomSheetDialog(requireActivity())
        bottomsheetbinding = BottomSheetLogoutBinding.inflate(layoutInflater)

        bottomsheetbinding.mcvCancel.setOnClickListener(this)
        bottomsheetbinding.mcvYes.setOnClickListener(this)

        dialog.setCancelable(true)
        dialog.setContentView(bottomsheetbinding.root)
        dialog.show()
    }

    private fun logOut()
    {
        val obj = UserDataHelper(requireContext())
        obj.deleteAll()
        I(requireContext(),Activity_LoginActivity::class.java,null)
        activity?.finishAffinity()
     }

    private fun setting()
    {
        I(requireActivity(),Activity_Settings::class.java,null)
    }

    private fun editProfile()
    {
        I(requireActivity(),Activity_EditProfileActivity::class.java,null)
    }

    override fun onClick(view: View?) {

        if (view == binding.mcvSetting)
        {
           setting()
        }

        else if(view == binding.llLogout)
        {
            showLogoutSheet()
        }

        else if(view == binding.mcvEditProfile)
        {
            editProfile()
        }

        else if(view == bottomsheetbinding.mcvCancel )
        {
            dialog.dismiss()
        }

        else if(view == bottomsheetbinding.mcvYes)
        {
            logOut()
        }

    }
}