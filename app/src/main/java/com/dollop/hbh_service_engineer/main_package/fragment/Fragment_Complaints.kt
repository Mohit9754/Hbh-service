package com.dollop.hbh_service_engineer.main_package.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dollop.hbh_service_engineer.R
import com.dollop.hbh_service_engineer.basic.Retrofit.APIError
import com.dollop.hbh_service_engineer.basic.Retrofit.RetrofitClient
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.Closed
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.In_Progress
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.Pending
import com.dollop.hbh_service_engineer.basic.UtilityTools.KeyboardUtils.hideKeyboard
import com.dollop.hbh_service_engineer.basic.UtilityTools.StatusCodeConstant
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils.expandView
import com.dollop.hbh_service_engineer.databinding.BottomSheetFilterBinding
import com.dollop.hbh_service_engineer.databinding.FragmentComplaintsBinding
import com.dollop.hbh_service_engineer.main_package.adapter.Adapter_CallType_list
import com.dollop.hbh_service_engineer.main_package.adapter.Adapter_Customer_list
import com.dollop.hbh_service_engineer.main_package.adapter.Adapter_AllComplaint_tab
import com.dollop.hbh_service_engineer.main_package.model.CallType
import com.dollop.hbh_service_engineer.main_package.model.Customer
import com.dollop.hbh_service_engineer.main_package.model.FilterData
import com.dollop.hbh_service_engineer.main_package.model.GetCallType
import com.dollop.hbh_service_engineer.main_package.model.GetCustomerModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar

class Fragment_Complaints : Fragment(),View.OnClickListener {

    private lateinit var binding:FragmentComplaintsBinding
    private lateinit var bottomsheetbinding:BottomSheetFilterBinding
    private lateinit var dialog:BottomSheetDialog
    private val calendar = Calendar.getInstance()
    private var filter = FilterData()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentComplaintsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun setOnfocusChangeLis()
    {
        bottomsheetbinding.etLocation.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus)
            {
                Utils.collapseView(bottomsheetbinding.llCustomer)
                Utils.collapseView(bottomsheetbinding.llCalltype)
                bottomsheetbinding.mcvLocation.strokeColor = ContextCompat.getColor(requireContext(), R.color.blue)

            } else
            {
                bottomsheetbinding.mcvLocation.strokeColor  = ContextCompat.getColor(requireContext(), R.color.mcv_stroke)
            }
        }
    }

    private fun setCustomerList(list: List<Customer>)
    {
        val filterdCustomerlist = ArrayList<Customer>()

        val customerListAdapter = Adapter_Customer_list(requireContext(),list,bottomsheetbinding.etCustomerName,bottomsheetbinding.llCustomer)
        bottomsheetbinding.rvCustomer.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        bottomsheetbinding.rvCustomer.adapter = customerListAdapter

        bottomsheetbinding.etCustomerNameSearch.addTextChangedListener(object: TextWatcher {

            override fun afterTextChanged(s: Editable) {

                filterdCustomerlist.clear()
                for (data in list) {

                    if (data.customerName.toLowerCase().contains(s.toString().toLowerCase()))
                    {
                        filterdCustomerlist.add(data)
                    }
                }
                customerListAdapter.filterList(filterdCustomerlist)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun setCallTypeList(list: List<CallType>)
    {
        val callTypeAdapter = Adapter_CallType_list(requireContext(),list,bottomsheetbinding.etCalltype,bottomsheetbinding.llCalltype,bottomsheetbinding.ivCalltype)
        bottomsheetbinding.rvCalltype.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        bottomsheetbinding.rvCalltype.adapter = callTypeAdapter

    }

    private fun getCustommerListApi()
    {
        val data = Utils.GetSession()
        RetrofitClient.client.getCustomerApi(data.token!!)!!.enqueue(object : retrofit2.Callback<GetCustomerModel?> {
            override fun onResponse(
                call: Call<GetCustomerModel?>,
                response: Response<GetCustomerModel?>
            ) {
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        assert(response.body() != null)
                        val customermodel = response.body()
                        val list = customermodel?.customer
                        setCustomerList(list!!)

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

            override fun onFailure(call: Call<GetCustomerModel?>, t: Throwable) {
                call.cancel()
                t.printStackTrace()
                Utils.E("getMessage::" + t.message)
            }
        })
    }

    private fun getCallTypeListApi()
    {

        RetrofitClient.client.getCallTypeApi(Utils.GetSession().token!!)!!.enqueue(object : retrofit2.Callback<GetCallType?> {
            override fun onResponse(
                call: Call<GetCallType?>,
                response: Response<GetCallType?>
            ) {
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        assert(response.body() != null)
                        val getcalltype = response.body()
                        val list = getcalltype?.callType
                        setCallTypeList(list!!)

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

            override fun onFailure(call: Call<GetCallType?>, t: Throwable) {
                call.cancel()
                t.printStackTrace()
                Utils.E("getMessage::" + t.message)
            }
        })
    }

    private fun checkFilterData(data:String,editText:EditText)
    {
        if(data.isNotEmpty())
        {
            editText.setText(data)
        }
    }

    private fun setFilterData()
    {
        checkFilterData(filter.startDate,bottomsheetbinding.etStartdate)
        checkFilterData(filter.endDate,bottomsheetbinding.etEnddate)
        checkFilterData(filter.customerName,bottomsheetbinding.etCustomerName)
        checkFilterData(filter.location,bottomsheetbinding.etLocation)
        checkFilterData(filter.callType,bottomsheetbinding.etCalltype)
    }

    @SuppressLint("MissingInflatedId", "SimpleDateFormat")
    private fun showBottomSheet()
    {
        dialog = BottomSheetDialog(requireActivity())
        bottomsheetbinding =  BottomSheetFilterBinding.inflate(layoutInflater)


        bottomsheetbinding.etStartdate.setOnClickListener(this)
        setOnfocusChangeLis()
        bottomsheetbinding.etEnddate.setOnClickListener(this)
        bottomsheetbinding.etCustomerName.setOnClickListener(this)
        bottomsheetbinding.etCalltype.setOnClickListener(this)
        bottomsheetbinding.tvClearFilter.setOnClickListener(this)
        bottomsheetbinding.llForDispatch.setOnClickListener(this)

//        bottomsheetbinding.llForDispatch.setOnClickListener {
//        }

        getCustommerListApi()
        getCallTypeListApi()
        setFilterData()
        bottomsheetbinding.tvApplyFilter.setOnClickListener(this)

//        bottomsheetbinding =  BottomSheetFilterBinding.inflate(layoutInflater)
        dialog.setCancelable(true)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.setContentView(bottomsheetbinding.root)
        dialog.show()
    }

    private fun init()
    {
        binding.ivFilter.setOnClickListener(this)
        setupTab()
    }

    private fun setupTab()
    {
        val tabLayout = binding.tlComplaints
        val viewPager = binding.vpComplaints

        viewPager.adapter = Adapter_AllComplaint_tab(requireActivity() as AppCompatActivity,filter)
        TabLayoutMediator(tabLayout,viewPager){

                tab,position ->tab.text = when(position)
        {
            0 -> Pending
            1 -> In_Progress
            2 -> Closed
            else -> Pending
        }
        }.attach()
    }

    private fun selectStartDate()
    {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(),
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay" // +1 because months are 0-based
                bottomsheetbinding.etStartdate.setText(selectedDate)
                bottomsheetbinding.tvStartDateError.visibility = View.GONE
            },
            year, month, day)

        if(bottomsheetbinding.etEnddate.text.isNotEmpty())
        {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val date = dateFormat.parse(bottomsheetbinding.etEnddate.text.toString())
            val calendar = Calendar.getInstance()
            calendar.time = date
            val maxDate = calendar.timeInMillis
            datePickerDialog.datePicker.maxDate = maxDate

        }
        else
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    private fun selectEndDate()
    {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDay ->
                val selectedDate =
                    "$selectedYear-${selectedMonth + 1}-$selectedDay" // +1 because months are 0-based
                bottomsheetbinding.etEnddate.setText(selectedDate)
                bottomsheetbinding.tvEndDateError.visibility = View.GONE
            },
            year, month, day
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        if(bottomsheetbinding.etStartdate.text.isNotEmpty())
        {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val date = dateFormat.parse(bottomsheetbinding.etStartdate.text.toString())
            val calendar = Calendar.getInstance()
            calendar.time = date
            val minDate = calendar.timeInMillis
            datePickerDialog.datePicker.minDate = minDate

        }
        datePickerDialog.show()
    }

    private fun showCustomerList()
    {
        if(bottomsheetbinding.llCustomer.visibility == View.GONE) {
            expandView(bottomsheetbinding.llCustomer)
            Utils.collapseView(bottomsheetbinding.llCalltype)
        }
        else
            bottomsheetbinding.llCustomer.visibility = View.GONE
    }

    private fun showCallTypeList()
    {
        if (bottomsheetbinding.llCalltype.visibility == View.GONE)
        {
            expandView(bottomsheetbinding.llCalltype)
            Utils.collapseView(bottomsheetbinding.llCustomer)
            bottomsheetbinding.ivCalltype.setImageResource(R.drawable.ic_drop_up)
        }
        else
        {
            bottomsheetbinding.llCalltype.visibility = View.GONE
            bottomsheetbinding.ivCalltype.setImageResource(R.drawable.ic_drop_down)
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onClick(view: View?) {

        if(view == binding.ivFilter)
        {
             showBottomSheet()
        }

        else if(view == bottomsheetbinding.etStartdate)
        {
            bottomsheetbinding.etLocation.clearFocus()
            selectStartDate()
            Utils.collapseView(bottomsheetbinding.llCustomer)
            Utils.collapseView(bottomsheetbinding.llCalltype)
            hideKeyboard(bottomsheetbinding.root)
        }

        else if(view == bottomsheetbinding.etEnddate)
        {
            bottomsheetbinding.etLocation.clearFocus()
            selectEndDate()
            Utils.collapseView(bottomsheetbinding.llCustomer)
            Utils.collapseView(bottomsheetbinding.llCalltype)
            hideKeyboard(bottomsheetbinding.root)
        }

        else if(view ==  bottomsheetbinding.etCustomerName)
        {
            bottomsheetbinding.etLocation.clearFocus()
            showCustomerList()
            hideKeyboard(bottomsheetbinding.root)
        }

        else if(view == bottomsheetbinding.etCalltype)
        {
            bottomsheetbinding.etLocation.clearFocus()
            showCallTypeList()
            hideKeyboard(bottomsheetbinding.root)

        }

        else if(view == bottomsheetbinding.tvApplyFilter)
        {
            checkDateValidation()
        }

        else if(view == bottomsheetbinding.tvClearFilter )
        {
            filter = FilterData()
            dialog.dismiss()
            setupTab()
        }
        else if(view == bottomsheetbinding.llForDispatch)
        {
            hideKeyboard(bottomsheetbinding.root)
            bottomsheetbinding.etLocation.clearFocus()
        }
    }

    private fun checkDateValidation()
    {
        if(bottomsheetbinding.etStartdate.text.isEmpty() && bottomsheetbinding.etEnddate.text.isNotEmpty())
        {
            bottomsheetbinding.tvStartDateError.visibility = View.VISIBLE
            bottomsheetbinding.tvStartDateError.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.top_to_bottom))

        }
        else if(bottomsheetbinding.etEnddate.text.isEmpty() && bottomsheetbinding.etStartdate.text.isNotEmpty())
        {
            bottomsheetbinding.tvEndDateError.visibility = View.VISIBLE
            bottomsheetbinding.tvEndDateError.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.top_to_bottom))
        }
        else
        {
            applyFilter()
        }
    }

    private fun applyFilter()
    {
        val etStartdate = bottomsheetbinding.etStartdate.text.toString()
        val etEnddate = bottomsheetbinding.etEnddate.text.toString()
        val etCustomerName = bottomsheetbinding.etCustomerName.text.toString()
        val etLocation = bottomsheetbinding.etLocation.text.toString()
        val etCalltype = bottomsheetbinding.etCalltype.text.toString()

        filter.startDate = etStartdate
        filter.endDate = etEnddate
        filter.startDate = etStartdate
        filter.customerName = etCustomerName
        filter.location = etLocation
        filter.callType = etCalltype

        dialog.dismiss()
        setupTab()
    }

}