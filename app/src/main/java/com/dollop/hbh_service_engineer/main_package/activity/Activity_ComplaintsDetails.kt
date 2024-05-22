package com.dollop.hbh_service_engineer.main_package.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dollop.hbh_service_engineer.R
import com.dollop.hbh_service_engineer.basic.Retrofit.APIError
import com.dollop.hbh_service_engineer.basic.Retrofit.RetrofitClient
import com.dollop.hbh_service_engineer.basic.UtilityTools.BaseActivity
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.Check_In
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.Check_Out
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.Closed
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.Complain_Id
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.In_Progress
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.Open
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.StartJob
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.Start_Job
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.Success
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.callStatus
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.checkIn
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.checkOut
import com.dollop.hbh_service_engineer.basic.UtilityTools.KeyboardUtils
import com.dollop.hbh_service_engineer.basic.UtilityTools.StatusCodeConstant
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils
import com.dollop.hbh_service_engineer.databinding.ActivityComplaintsDetailsBinding
import com.dollop.hbh_service_engineer.databinding.BottomSheetCheckinBinding
import com.dollop.hbh_service_engineer.main_package.adapter.Adapter_Updates
import com.dollop.hbh_service_engineer.main_package.model.ComplainListInProgressModel
import com.dollop.hbh_service_engineer.main_package.model.Complaint
import com.dollop.hbh_service_engineer.main_package.model.ComplaintFollowModel
import com.dollop.hbh_service_engineer.main_package.model.Update
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Activity_ComplaintsDetails : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityComplaintsDetailsBinding
    private lateinit var bundle: Bundle
    private lateinit var dialog: BottomSheetDialog
    private lateinit var bottomsheetbinding: BottomSheetCheckinBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var list: List<Address>
    private val permissionId = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityComplaintsDetailsBinding.inflate(layoutInflater)
//        init()
        setContentView(binding.root)
    }

    private fun hideUpdate(callstatus: String) {
        if (callstatus == Open)
            binding.llUpdates.visibility = View.GONE
    }

    private fun init() {
        bundle = intent.extras!!
        val callstatus = bundle.getString(callStatus).toString()

//        Utils.changeStatusBarColor(this, R.color.fragment_bg)
        binding.ivBackBtn.setOnClickListener(this)
        setIcon()
        hideUpdate(callstatus)
        setOnClickOnCheckbtn(callstatus)
        checkClosed()
    }

    private fun checkClosed() {

        val sharedPref = getSharedPreferences("application", Context.MODE_PRIVATE)
        val status = sharedPref.getBoolean(Closed, false)

        if(status)
        {
            binding.llCheckBtn.visibility = View.GONE
        }
        val editor = sharedPref.edit()
        editor.putBoolean(Closed,false)
        editor.apply()
    }

    private fun setOnClickOnCheckbtn(callstatus: String) {
        if (callstatus == In_Progress || callstatus == Open) {
            binding.mcvCheckBtn.setOnClickListener(this)
        }
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun setIcon() {
        val callstatus = bundle.getString(callStatus)

        if (callstatus == Closed) {
            binding.ivStatus.setImageResource(R.drawable.ic_closed)
            callInProgressApi()
        } else if (callstatus == In_Progress) {
            binding.llCheckBtn.visibility = View.VISIBLE

            val marginBottom =
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._30sdp) // You can replace R.dimen.bottom_margin with your desired dimension resource or a specific pixel value

            if (binding.nsvData.layoutParams is ViewGroup.MarginLayoutParams) {

                val layoutParams = binding.nsvData.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.bottomMargin = marginBottom
                binding.nsvData.layoutParams = layoutParams
            }

            binding.ivStatus.setImageResource(R.drawable.ic_inprogress)
            val layoutParams = binding.ivStatus.layoutParams
            layoutParams.width = 150
            layoutParams.height = 70
            binding.ivStatus.layoutParams = layoutParams
            callInProgressApi()
        } else if (callstatus == Open) {
            binding.llCheckBtn.visibility = View.VISIBLE

            val marginBottom =
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._30sdp) // You can replace R.dimen.bottom_margin with your desired dimension resource or a specific pixel value

            if (binding.nsvData.layoutParams is ViewGroup.MarginLayoutParams) {

                val layoutParams = binding.nsvData.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.bottomMargin = marginBottom
                binding.nsvData.layoutParams = layoutParams
            }

            binding.ivStatus.setImageResource(R.drawable.ic_pending)
            val layoutParams = binding.ivStatus.layoutParams
            layoutParams.width = 125
            layoutParams.height = 70
            binding.ivStatus.layoutParams = layoutParams
            callInProgressApi()
        }
    }

    private fun setAttendanceStatus(status: String) {
        if (status == checkIn) {
            binding.tvCheck.text = Check_In
        } else if (status == checkOut) {
            binding.tvCheck.text = Check_Out
        } else if (status == StartJob) {
            binding.tvCheck.text = Start_Job
        }
    }

    private fun checkEmptyData(data: String, linearLayout: LinearLayout, textView: TextView) {
        if (data.isEmpty()) {
            linearLayout.visibility = View.GONE
        } else {
            textView.text = data
        }
    }

    private fun setComplainDetails(data: List<Complaint>?) {
        binding.tvComplaintId.text = data?.get(0)!!.complaintId
        binding.tvCustomerName.text = data[0].customerName
        binding.tvContactPerson.text = data[0].contactPerson
        binding.tvContactNumber.text = data[0].contactNumber
        binding.tvCallType.text = data[0].callType
        binding.tvSymptom.text = data[0].symptom
        data[0].attendanceStatus?.let { setAttendanceStatus(it) }

        checkEmptyData(data[0].modelNo.toString(), binding.llModel, binding.tvModel)
        checkEmptyData(data[0].machineSrNo.toString(), binding.llMachineSrNo, binding.tvSerialNo)

        if (data[0].updates!!.isNotEmpty())
            setUpRecycler(data[0].updates)
        else
            binding.llUpdates.visibility = View.GONE

    }

    private fun callInProgressApi() {
        val complainId = bundle.getString(Constants.Complain_Id)
        val callstatus = bundle.getString(Constants.callStatus)

        RetrofitClient.client.getComplaintListApiWithComplainId(
            Utils.GetSession().token!!,
            complainId
        )!!.enqueue(object : retrofit2.Callback<ComplainListInProgressModel?> {
            override fun onResponse(
                call: Call<ComplainListInProgressModel?>,
                response: Response<ComplainListInProgressModel?>
            ) {
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        assert(response.body() != null)
                        val data = response.body()

                        setComplainDetails(data?.complaintList)

                    } else {
                        if (response.code() == StatusCodeConstant.BAD_REQUEST) {
                            assert(response.errorBody() != null)
                            val message = Gson().fromJson(
                                response.errorBody()!!.charStream(),
                                APIError::class.java
                            )
                            Utils.T(this@Activity_ComplaintsDetails, message.message)
                        } else if (response.code() == StatusCodeConstant.UNAUTHORIZED) {
                            assert(response.errorBody() != null)
                            val message = Gson().fromJson(
                                response.errorBody()!!.charStream(),
                                APIError::class.java
                            )
                            Utils.T(this@Activity_ComplaintsDetails, message.message)
                            Utils.UnAuthorizationToken(this@Activity_ComplaintsDetails)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ComplainListInProgressModel?>, t: Throwable) {
                call.cancel()
                t.printStackTrace()
                Utils.E("getMessage::" + t.message)
            }
        })
    }

    private fun setUpRecycler(arr: List<Update>?) {
        val callstatus = bundle.getString(callStatus)
        val Adapter = callstatus?.let { arr?.let { it1 -> Adapter_Updates(this, it1, it) } }
        binding.rvUpdates.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, true)
        binding.rvUpdates.adapter = Adapter

    }

    private fun setFocuChangeLisOnetComment() {
        bottomsheetbinding.etComment.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus) {
                bottomsheetbinding.mcvComment.strokeColor = getColor(R.color.blue)
            } else {
                bottomsheetbinding.mcvComment.strokeColor = getColor(R.color.black)
            }
        }
    }

    private fun setTodaysDate() {
        val time = Calendar.getInstance().time
        val outputFormat = SimpleDateFormat("dd MMM yyyy HH:mm a", Locale.getDefault())
        val formattedDate = outputFormat.format(time!!)
        bottomsheetbinding.tvDate.text = formattedDate

    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingPermission")
    private fun setCurrentLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->

                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        list = geocoder.getFromLocation(location.latitude, location.longitude, 1)!!
                        bottomsheetbinding.tvLocation.text = list[0].getAddressLine(0)
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.please_turn_on_location), Toast.LENGTH_LONG).show()
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        } else {
            requestPermissions()
        }
    }

    private fun showBottomSheet() {
        dialog = BottomSheetDialog(this)
        bottomsheetbinding = BottomSheetCheckinBinding.inflate(layoutInflater)
        setFocuChangeLisOnetComment()
        setTodaysDate()
        setCurrentLocation()
        bottomsheetbinding.mcvCheckBtn.setOnClickListener(this)
        bottomsheetbinding.llCheckIn.setOnClickListener(this)

        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.setCancelable(true)
        dialog.setContentView(bottomsheetbinding.root)
        dialog.show()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {

        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun startJob() {
        RetrofitClient.client.startJobApi(
            Utils.GetSession().token!!,
            binding.tvComplaintId.text.toString()
        )
            .enqueue(object : retrofit2.Callback<ComplaintFollowModel?> {
                @SuppressLint("SuspiciousIndentation")
                override fun onResponse(
                    call: Call<ComplaintFollowModel?>,
                    response: Response<ComplaintFollowModel?>
                ) {
                    try {
                        if (response.code() == StatusCodeConstant.OK) {
                            assert(response.body() != null)
                            val data = response.body()
                            val msg = data?.message.toString()
                                finish()

                        } else {
                            if (response.code() == StatusCodeConstant.BAD_REQUEST) {
                                assert(response.errorBody() != null)
                                val message = Gson().fromJson(
                                    response.errorBody()!!.charStream(),
                                    APIError::class.java
                                )
                                Utils.T(this@Activity_ComplaintsDetails, message.message)
                            } else if (response.code() == StatusCodeConstant.UNAUTHORIZED) {
                                assert(response.errorBody() != null)
                                val message = Gson().fromJson(
                                    response.errorBody()!!.charStream(),
                                    APIError::class.java
                                )
                                Utils.T(this@Activity_ComplaintsDetails, message.message)
                                Utils.UnAuthorizationToken(this@Activity_ComplaintsDetails)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ComplaintFollowModel?>, t: Throwable) {
                    call.cancel()
                    t.printStackTrace()
                    Utils.E("getMessage::" + t.message)
                }
            })
    }

    override fun onClick(view: View?) {
        if (view == binding.mcvCheckBtn) {
            if (binding.tvCheck.text == Check_In) {
                showBottomSheet()
            } else if (binding.tvCheck.text == Start_Job) {
                startJob()
            } else if (binding.tvCheck.text == Check_Out) {
                val bundle = Bundle()
                bundle.putString(Complain_Id, binding.tvComplaintId.text.toString())
                Utils.I(this, Activity_CheckOut::class.java, bundle)
            }
        } else if (view == binding.ivBackBtn) {
            super.onBackPressed()
        } else if (view == bottomsheetbinding.mcvCheckBtn) {
            checkIn()
        } else if (view == bottomsheetbinding.llCheckIn) {
            KeyboardUtils.hideKeyboard(bottomsheetbinding.root)
            bottomsheetbinding.etComment.clearFocus()
        }
    }

    private fun checkIn() {
        val complainId = binding.tvComplaintId.text.toString()
        val callStatus = bundle.getString(callStatus).toString()
        val punchingType = checkIn
        var comment = ""

        if (bottomsheetbinding.etComment.text.isNotEmpty())
            comment = bottomsheetbinding.etComment.text.toString()

        val location = list[0].locality.toString()
        val latitude = list[0].latitude.toString()
        val longitude = list[0].longitude.toString()

        RetrofitClient.client.complaintFollowupApiCheckIn(
            Utils.GetSession().token!!,
            complainId,
            callStatus,
            punchingType,
            comment,
            location,
            latitude,
            longitude
        )!!.enqueue(object : retrofit2.Callback<ComplaintFollowModel?> {
            override fun onResponse(
                call: Call<ComplaintFollowModel?>,
                response: Response<ComplaintFollowModel?>
            ) {
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        assert(response.body() != null)
                        val data = response.body()
                        val msg = data?.message.toString()
                        if (msg == Success) {
                            dialog.dismiss()
                            init()
                        }

                    } else {
                        if (response.code() == StatusCodeConstant.BAD_REQUEST) {
                            assert(response.errorBody() != null)
                            val message = Gson().fromJson(
                                response.errorBody()!!.charStream(),
                                APIError::class.java
                            )
                            Utils.T(this@Activity_ComplaintsDetails, message.message)
                        } else if (response.code() == StatusCodeConstant.UNAUTHORIZED) {
                            assert(response.errorBody() != null)
                            val message = Gson().fromJson(
                                response.errorBody()!!.charStream(),
                                APIError::class.java
                            )
                            Utils.T(this@Activity_ComplaintsDetails, message.message)
                            Utils.UnAuthorizationToken(this@Activity_ComplaintsDetails)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ComplaintFollowModel?>, t: Throwable) {
                call.cancel()
                t.printStackTrace()
                Utils.E("getMessage::" + t.message)
            }
        })
    }
}