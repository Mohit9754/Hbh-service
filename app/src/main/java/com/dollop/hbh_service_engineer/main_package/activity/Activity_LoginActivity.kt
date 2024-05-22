package com.dollop.hbh_service_engineer.main_package.activity

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.dollop.hbh_service_engineer.R
import com.dollop.hbh_service_engineer.basic.Database.UserDataHelper
import com.dollop.hbh_service_engineer.basic.Retrofit.APIError
import com.dollop.hbh_service_engineer.basic.Retrofit.ApiService
import com.dollop.hbh_service_engineer.basic.Retrofit.RetrofitClient
import com.dollop.hbh_service_engineer.basic.UtilityTools.*
import com.dollop.hbh_service_engineer.basic.validation.ResultReturn
import com.dollop.hbh_service_engineer.basic.validation.Validation
import com.dollop.hbh_service_engineer.basic.validation.ValidationModel
import com.dollop.hbh_service_engineer.databinding.ActivityLoginBinding
import com.dollop.hbh_service_engineer.main_package.model.AllResponseModel
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response

class Activity_LoginActivity : BaseActivity(), View.OnClickListener {

    private val activity: Activity = this@Activity_LoginActivity
    private lateinit var binding: ActivityLoginBinding
    private var apiservice: ApiService? = null
    private var isPasswordVisible = false
    private var errorValidationModels: MutableList<ValidationModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        apiservice = RetrofitClient.client
        binding.llPasswordVisibility.setOnClickListener(this)
        binding.tvLogIn.setOnClickListener(this)
        editTextListner()
    }

    private fun editTextListner() {
        binding.etUserName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                binding.tvUserNameError.visibility = View.GONE
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                binding.tvPasswordError.visibility = View.GONE
            }
            override fun afterTextChanged(editable: Editable) {}
        })
    }

    override fun onClick(view: View) {
        if (view == binding.llPasswordVisibility) {
            if (!isPasswordVisible) {
                isPasswordVisible = true
                binding.llPasswordVisibility.setImageResource(R.drawable.ic_hide)
                binding.etPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.etPassword.setSelection(binding.etPassword.length())

            } else {
                isPasswordVisible = false
                binding.llPasswordVisibility.setImageResource(R.drawable.ic_visibility)
                binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.etPassword.setSelection(binding.etPassword.length())

                //android.text.method.HideReturnsTransformationMethod@870ff8b
            }
        } else if (view == binding.tvLogIn) {
            CheckValidationTask()
        }
    }

    private fun CheckValidationTask() {
        errorValidationModels.clear()
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty,
                binding.etUserName,
                binding.tvUserNameError
            )
        )
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty,
                binding.etPassword,
                binding.tvPasswordError
            )
        )
        val validation: Validation? = Validation.instance
        val resultReturn: ResultReturn? =
            validation?.CheckValidation(activity, errorValidationModels)
        if (resultReturn?.aBoolean == true) {
            userLogin()
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

    private fun userLogin() {

        val progressDialog = Utils.initProgressDialog(activity)
        val hm = HashMap<String, String?>()
        hm[Constants.mobile] = binding.etUserName.text.toString().trim()
        hm[Constants.password] = binding.etPassword.text.toString().trim()
        apiservice!!.userLogin(hm)!!.enqueue(object : retrofit2.Callback<AllResponseModel?> {
            override fun onResponse(
                call: Call<AllResponseModel?>,
                response: Response<AllResponseModel?>
            ) {
                progressDialog.dismiss()
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        assert(response.body() != null)
                        Utils.T(activity, "Login Successfully")
                        val userModel = response.body()

                        if (userModel != null) {
                            UserDataHelper.instance.insertData(userModel.engineerInfo!!)
                            Utils.E("Code Run from know.......###########################################")
                        }
                        Utils.I_clear(activity, Activity_DashboardActivity::class.java, null)
                        Utils.E("Code Run from know.......###########################################")
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

            override fun onFailure(call: Call<AllResponseModel?>, t: Throwable) {
                call.cancel()
                t.printStackTrace()
                progressDialog.dismiss()
                Utils.E("getMessage::" + t.message)
            }
        })
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            // remove focus from edit text on click outside
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}