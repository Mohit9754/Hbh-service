package com.dollop.hbh_service_engineer.main_package.activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.dollop.hbh_service_engineer.R
import com.dollop.hbh_service_engineer.basic.Retrofit.APIError
import com.dollop.hbh_service_engineer.basic.Retrofit.ApiService
import com.dollop.hbh_service_engineer.basic.Retrofit.Const.Companion.CAMERA_REQUEST
import com.dollop.hbh_service_engineer.basic.Retrofit.Const.Companion.GALLERY_REQUEST
import com.dollop.hbh_service_engineer.basic.Retrofit.RetrofitClient
import com.dollop.hbh_service_engineer.basic.UtilityTools.*
import com.dollop.hbh_service_engineer.basic.UtilityTools.Constants.Companion.profilePic
import com.dollop.hbh_service_engineer.basic.validation.ResultReturn
import com.dollop.hbh_service_engineer.basic.validation.Validation
import com.dollop.hbh_service_engineer.basic.validation.ValidationModel
import com.dollop.hbh_service_engineer.databinding.ActivityEditProfileBinding
import com.dollop.hbh_service_engineer.databinding.ImagepickdialogBinding
import com.dollop.hbh_service_engineer.main_package.model.ComplaintFollowModel
import com.dollop.hbh_service_engineer.main_package.model.EngineerProfile
import com.dollop.hbh_service_engineer.main_package.model.EngineerProfileModel
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Response
import java.util.*

@Suppress("UNUSED_EXPRESSION")

class Activity_EditProfileActivity : BaseActivity(), View.OnClickListener {

    private var activity: Activity = this@Activity_EditProfileActivity
    private lateinit var binding: ActivityEditProfileBinding
    private var apiService: ApiService? = null
    private lateinit var dialogBinding: ImagepickdialogBinding
    private lateinit var dialog: Dialog
    private var errorValidationModels: MutableList<ValidationModel> = ArrayList()

    var galleryActivityResultLauncher: ActivityResultLauncher<Intent?>? = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == RESULT_OK)
            {
                val image_uri: Uri? = result.data?.data
                binding.ivProfilePic.setImageURI(image_uri)
            }
        })

    var cameraActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result != null) {
            if (result.resultCode == RESULT_OK) {

                binding.ivProfilePic.setImageBitmap(result.data?.extras?.get("data") as Bitmap?)

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        init()
        setContentView(binding.root)
    }

    private fun init() {

        apiService = RetrofitClient.client
//        changeStatusBarColor(this,R.color.fragment_bg)
        binding.ivBackBtn.setOnClickListener(this)
        binding.ivSelectImg.setOnClickListener(this)
        binding.mcvUpdate.setOnClickListener(this)
        setFocusChangeLis()
        setTextChangeLis(binding.etFirstName,binding.tvFirstNameError)
        setTextChangeLis(binding.etLastName,binding.tvLastNameError)
        getProfileData()
    }

    private fun setProfileData(list: EngineerProfile)
    {
        Utils.Picasso(list.profilePic,binding.ivProfilePic,R.drawable.mystry)
        binding.etFirstName.setText(list.userName)
        binding.etLastName.setText(list.lastName)
    }

    private fun getProfileData()
    {
        val progressDialog = Utils.initProgressDialog(activity)

        RetrofitClient.client.getEngineerProfileApi(Utils.GetSession().token!!)!!.enqueue(object : retrofit2.Callback<EngineerProfileModel?> {
            override fun onResponse(
                call: Call<EngineerProfileModel?>,
                response: Response<EngineerProfileModel?>
            ) {
                progressDialog.dismiss()
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        assert(response.body() != null)
                        val EngineerProfileModel = response.body()
                        val list = EngineerProfileModel?.engineerProfile
                        setProfileData(list!!)

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
                            Utils.UnAuthorizationToken(activity)
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

    private fun setTextChangeLis(editText: EditText,textView: TextView)
    {
        editText.addTextChangedListener(object: TextWatcher {

            override fun afterTextChanged(s: Editable) {

                if(s.toString().isNotEmpty())
                    textView.visibility = View.GONE
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun setFocusChangeLis()
    {
        binding.etFirstName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->

            if (hasFocus) {
                binding.mcvFirstName.strokeColor = getColor(R.color.blue)

            } else {
                binding.mcvFirstName.strokeColor = getColor(R.color.mcv_stroke)
            }
        }

        binding.etLastName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->

            if (hasFocus) {
                binding.mcvLastName.strokeColor = getColor(R.color.blue)

            } else {
                binding.mcvLastName.strokeColor = getColor(R.color.mcv_stroke)
            }
        }
    }

    private fun updateData()
    {
        val jsonMediaType = "text/plain".toMediaTypeOrNull()

        val userName = binding.etFirstName.text.toString().toRequestBody(jsonMediaType)
        val lastName = binding.etLastName.text.toString().toRequestBody(jsonMediaType)
        val profilePic = AppHelper.prepareFilePart(profilePic, binding.ivProfilePic.drawable.toBitmap())

        RetrofitClient.client.updateSalesProfile(Utils.GetSession().token!!,userName,lastName,profilePic)
            .enqueue(object : retrofit2.Callback<ComplaintFollowModel?> {
            override fun onResponse(
                call: Call<ComplaintFollowModel?>,
                response: Response<ComplaintFollowModel?>
            ) {
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        assert(response.body() != null)
                        val msg = response.body()
                        Utils.T(this@Activity_EditProfileActivity, msg?.message)
                        finish()

                    } else {
                        if (response.code() == StatusCodeConstant.BAD_REQUEST) {
                            assert(response.errorBody() != null)
                            val message = Gson().fromJson(
                                response.errorBody()!!.charStream(),
                                APIError::class.java
                            )
                            Utils.T(this@Activity_EditProfileActivity, message.message)
                        } else if (response.code() == StatusCodeConstant.UNAUTHORIZED) {
                            assert(response.errorBody() != null)
                            val message = Gson().fromJson(
                                response.errorBody()!!.charStream(),
                                APIError::class.java
                            )
                            Utils.T(this@Activity_EditProfileActivity, message.message)
                            Utils.UnAuthorizationToken(this@Activity_EditProfileActivity)
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

    private fun validateData()
    {
        errorValidationModels.clear()
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty,
                binding.etFirstName,
                binding.tvFirstNameError
            )
        )
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty,
                binding.etLastName,
                binding.tvLastNameError
            )
        )

        val validation: Validation? = Validation.instance
        val resultReturn: ResultReturn? =
            validation?.CheckValidation(activity, errorValidationModels)
        if (resultReturn?.aBoolean == true) {
            updateData()
        } else {
            resultReturn?.errorTextView?.visibility = View.VISIBLE
            if (resultReturn?.type === Validation.Type.EmptyString) {
                resultReturn.errorTextView?.text = resultReturn.errorMessage
            } else {
                resultReturn?.errorTextView?.text = validation?.errorMessage
                val animation =
                    AnimationUtils.loadAnimation(applicationContext, R.anim.top_to_bottom)
                resultReturn?.errorTextView?.startAnimation(animation)
                validation?.EditTextPointer?.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(validation?.EditTextPointer, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    private fun pickImage()
    {
        dialog = Dialog(this)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialogBinding = ImagepickdialogBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)

        dialogBinding.mcvCamera.setOnClickListener(this)
        dialogBinding.mcvGallery.setOnClickListener(this)
        dialog.show()
    }

    private fun openGallery()
    {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryActivityResultLauncher?.launch(galleryIntent)
        dialog.dismiss()
    }

    private fun openCamera()
    {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraActivityResultLauncher.launch(cameraIntent)
        dialog.dismiss()
    }

    override fun onClick(view: View) {

        if (view == binding.ivBackBtn)
        {
            finish()
        }

        else if(view == binding.ivSelectImg)
        {
            pickImage()
        }

        else if(view == binding.mcvUpdate)
        {
            validateData()
        }

        else if(view == dialogBinding.mcvCamera)
        {

            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST)

                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                {
                    openCamera()
                    dialog.dismiss()
                }
            }
            else
            {
                openCamera()
                dialog.dismiss()
            }
        }

        else if(view == dialogBinding.mcvGallery)
        {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), GALLERY_REQUEST)

                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    openGallery()
                    dialog.dismiss()
                }
            }
            else
            {
                openGallery()
                dialog.dismiss()
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            // remove focus from edit text on click outside
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

}

