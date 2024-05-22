package com.dollop.hbh_service_engineer.main_package.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.dollop.hbh_service_engineer.R
import com.dollop.hbh_service_engineer.basic.Retrofit.APIError
import com.dollop.hbh_service_engineer.basic.Retrofit.Const
import com.dollop.hbh_service_engineer.basic.Retrofit.RetrofitClient
import com.dollop.hbh_service_engineer.basic.UtilityTools.AppHelper.prepareFilePart
import com.dollop.hbh_service_engineer.basic.UtilityTools.BaseActivity
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.Closed
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.In_Progress
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.Please_Enter_Signature
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.Please_turn_on_location
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.attachement
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.checkOut
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.signature
import com.dollop.hbh_service_engineer.basic.UtilityTools.StatusCodeConstant
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils
import com.dollop.hbh_service_engineer.databinding.ActivityCheckOutBinding
import com.dollop.hbh_service_engineer.main_package.model.ComplaintFollowModel
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Suppress("NAME_SHADOWING")
class Activity_CheckOut : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityCheckOutBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var list: List<Address>
    private val permissionId = 2
    private var isSignatured = false
    private var status = true

    private var galleryActivityResultLauncher: ActivityResultLauncher<Intent?>? = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == RESULT_OK)
            {
                val image_uri: Uri? = result.data?.data
                binding.rlAttachmentImg.visibility = View.VISIBLE
                binding.ivAttachment.setImageURI(image_uri)
            }
        })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckOutBinding.inflate(layoutInflater)
        init()
        setContentView(binding.root)
    }

    private fun setDate() {
        val time = Calendar.getInstance().time
        val outputFormat = SimpleDateFormat("dd MMM yyyy HH:mm a", Locale.getDefault())
        val formattedDate = outputFormat.format(time)
        binding.tvDate.text = formattedDate

    }

    private fun setLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
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

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->

                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        list = geocoder.getFromLocation(location.latitude, location.longitude, 1)!!
                        binding.tvLocation.text = list[0].getAddressLine(0)
                    }
                }
            } else {
                Toast.makeText(this, Please_turn_on_location, Toast.LENGTH_LONG).show()
                startActivity(Intent(ACTION_LOCATION_SOURCE_SETTINGS))
            }
        } else {
            requestPermissions()
        }
    }

    private fun init() {
//        Utils.changeStatusBarColor(this, R.color.fragment_bg)
        setSignaturePadLis()
        setFocusChangeLis()
        setTextChangeLis()
        setDate()
        setLocation()
        binding.ivBackBtn.setOnClickListener(this)
        binding.signaturePad.setOnClickListener(this)
        binding.mcvClear.setOnClickListener(this)
        binding.mcvInProgress.setOnClickListener(this)
        binding.mcvClosed.setOnClickListener(this)
        binding.mcvAttachment.setOnClickListener(this)
        binding.mcvCutAttac.setOnClickListener(this)
        binding.mcvCheckBtn.setOnClickListener(this)
    }

    private fun setTextChangeLis() {
        binding.etComment.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

                if (s.toString().length > 0) {
                    binding.tvCommentError.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun setFocusChangeLis() {
        binding.etComment.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus) {
                binding.mcvComment.strokeColor = getColor(R.color.blue)
            } else {
                binding.mcvComment.strokeColor = getColor(R.color.black)
            }
        }
    }

    private fun setSignaturePadLis() {
        binding.signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
            }

            override fun onSigned() {
                isSignatured = true
            }

            override fun onClear() {

                isSignatured = false
            }
        })
    }

    override fun onClick(view: View?) {

        if (view == binding.ivBackBtn) {
            super.onBackPressed()
        } else if (view == binding.mcvClear) {
            binding.signaturePad.clear()
        } else if (view == binding.mcvInProgress) {
            binding.mcvClosed.strokeColor = getColor(R.color.black)
            binding.tvClosed.setTextColor(getColor(R.color.black))
            binding.mcvClosed.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

            binding.mcvInProgress.strokeColor = getColor(R.color.blue)
            binding.tvInProgress.setTextColor(getColor(R.color.blue))
            binding.mcvInProgress.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.check_out_location
                )
            )
            status = true

        } else if (view == binding.mcvClosed) {
            binding.mcvInProgress.strokeColor = getColor(R.color.black)
            binding.tvInProgress.setTextColor(getColor(R.color.black))
            binding.mcvInProgress.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            binding.mcvClosed.strokeColor = getColor(R.color.blue)
            binding.tvClosed.setTextColor(getColor(R.color.blue))
            binding.mcvClosed.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.check_out_location
                )
            )
            status = false
        } else if (view == binding.mcvAttachment) {
            selectAttacment()
        } else if (view == binding.mcvCutAttac) {
            binding.rlAttachmentImg.visibility = View.GONE
        } else if (view == binding.mcvCheckBtn) {
            validateFields()
        }
    }

    private fun validateFields() {
        if (binding.etComment.text.isEmpty()) {
            binding.tvCommentError.visibility = View.VISIBLE
            binding.etComment.requestFocus()
            binding.tvCommentError.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.top_to_bottom
                )
            )
        } else if (!isSignatured) {
            Toast.makeText(this, Please_Enter_Signature, Toast.LENGTH_SHORT).show()
        } else {
            fetchDetails()
        }
    }

    private fun getImgMultipart(name: String, image: Bitmap): MultipartBody.Part {
        return prepareFilePart(name, image)
    }

    private fun setStatusDatatoShare(callStatus:String)
    {
        if(callStatus == Closed)
        {
            val sharedPref = getSharedPreferences("application", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean(Closed,true)
            editor.apply()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun fetchDetails() {
        val bundle = intent.extras
        val complainId = bundle?.getString(Constants.Complain_Id, Constants.Complain_Id)
        val callStatus: String
        when {
            status -> callStatus = In_Progress
            else -> callStatus = Closed
        }
        setStatusDatatoShare(callStatus)
        val punchingType = checkOut
        val comment = binding.etComment.text.toString()
        val location = list[0].locality
        val latitude = list[0].latitude
        val longitude = list[0].longitude

        var attachment =
            getImgMultipart(attachement, getDrawable(R.drawable.ic_placeholder_img)!!.toBitmap())
        val signature = getImgMultipart(signature, binding.signaturePad.signatureBitmap)

        if (binding.rlAttachmentImg.visibility == View.VISIBLE) {
            attachment = getImgMultipart(attachement, binding.ivAttachment.drawable.toBitmap())
        }

        callApi(
            complainId.toString(), callStatus, punchingType, comment, location,
            latitude.toString(), longitude.toString(), attachment, signature
        )
    }

    private fun callApi(
        complainId: String,
        CallStatus: String,
        punchingType: String,
        comment: String,
        location: String,
        latitude: String,
        longitude: String,
        attachment: MultipartBody.Part,
        signature: MultipartBody.Part
    ) {
        val jsonMediaType = "text/plain".toMediaTypeOrNull()

        val complainId = complainId.toRequestBody(jsonMediaType)
        val callStatus = CallStatus.toRequestBody(jsonMediaType)
        val punchingType = punchingType.toRequestBody(jsonMediaType)
        val comment = comment.toRequestBody(jsonMediaType)
        val location = location.toRequestBody(jsonMediaType)
        val latitude = latitude.toRequestBody(jsonMediaType)
        val longitude = longitude.toRequestBody(jsonMediaType)

        RetrofitClient.client.complaintFollowupApi(
            Utils.GetSession().token!!,
            complainId,
            callStatus,
            punchingType,
            comment,
            location,
            latitude,
            longitude,
            attachment,
            signature
        )!!.enqueue(object : retrofit2.Callback<ComplaintFollowModel?> {
            override fun onResponse(
                call: Call<ComplaintFollowModel?>,
                response: Response<ComplaintFollowModel?>
            ) {
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        assert(response.body() != null)
                        val data = response.body()
                        finish()

                    } else {
                        if (response.code() == StatusCodeConstant.BAD_REQUEST) {
                            assert(response.errorBody() != null)
                            val message = Gson().fromJson(
                                response.errorBody()!!.charStream(),
                                APIError::class.java
                            )
                            Utils.T(this@Activity_CheckOut, message.message)
                        } else if (response.code() == StatusCodeConstant.UNAUTHORIZED) {
                            assert(response.errorBody() != null)
                            val message = Gson().fromJson(
                                response.errorBody()!!.charStream(),
                                APIError::class.java
                            )
                            Utils.T(this@Activity_CheckOut, message.message)
                            Utils.UnAuthorizationToken(this@Activity_CheckOut)
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

    private fun openGallery()
    {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryActivityResultLauncher?.launch(galleryIntent)
    }

    private fun selectAttacment()
    {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Const.GALLERY_REQUEST
            )

            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                openGallery()
            }
        }
        else
        {
            openGallery()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {

        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            binding.etComment.clearFocus();
        }
        return super.dispatchTouchEvent(ev)
    }
}


