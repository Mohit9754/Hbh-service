package com.dollop.hbh_service_engineer.main_package.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.text.method.HideReturnsTransformationMethod
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.dollop.hbh_service_engineer.R
import com.dollop.hbh_service_engineer.basic.Retrofit.APIError
import com.dollop.hbh_service_engineer.basic.Retrofit.RetrofitClient
import com.dollop.hbh_service_engineer.basic.UtilityTools.BaseActivity
import com.dollop.hbh_service_engineer.basic.UtilityTools.StatusCodeConstant
import com.dollop.hbh_service_engineer.basic.UtilityTools.Utils
import com.dollop.hbh_service_engineer.basic.validation.ResultReturn
import com.dollop.hbh_service_engineer.basic.validation.Validation
import com.dollop.hbh_service_engineer.basic.validation.ValidationModel
import com.dollop.hbh_service_engineer.databinding.ActivityChangePasswordBinding
import com.dollop.hbh_service_engineer.main_package.model.ComplaintFollowModel
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response

class Activity_ChangePassword : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityChangePasswordBinding
    private var isPasswordVisible = arrayOf(false,false,false)
    private var activity = this@Activity_ChangePassword
    private var errorValidationModels: MutableList<ValidationModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        init()

        setContentView(binding.root)
    }

    private fun init()
    {
//        Utils.changeStatusBarColor(this, R.color.fragment_bg)
        binding.ivBackBtn.setOnClickListener(this)
        binding.ivCurrentPass.setOnClickListener(this)
        binding.ivNewPass.setOnClickListener(this)
        binding.ivConfirmNewPass.setOnClickListener(this)
        binding.mcvUpdatePass.setOnClickListener(this)
        textChangeLis(binding.etCurrentPass,binding.tvCurrentPassError)
        textChangeLis(binding.etNewPass,binding.tvNewPassError)
        textChangeLis(binding.etConfirmNewPass,binding.tvConfirmNewPassError)
    }

    private fun textChangeLis(editText: EditText ,textView:TextView)
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

    private fun updatePassword()
    {
        val progressDialog = Utils.initProgressDialog(this)

        val oldPassword = binding.etCurrentPass.text.toString()
        val newPassword = binding.etNewPass.text.toString()
        val confirmPassword = binding.etConfirmNewPass.text.toString()

        RetrofitClient.client.changePasswordApi(Utils.GetSession().token!!,oldPassword,newPassword,confirmPassword)!!.enqueue(object : retrofit2.Callback<ComplaintFollowModel?> {
            override fun onResponse(
                call: Call<ComplaintFollowModel?>,
                response: Response<ComplaintFollowModel?>
            ) {
                progressDialog.dismiss()
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        assert(response.body() != null)
                        val data = response.body()
                        finish()
//                        checkMessage(data?.message.toString())

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

            override fun onFailure(call: Call<ComplaintFollowModel?>, t: Throwable) {
                call.cancel()
                t.printStackTrace()
                Utils.E("getMessage::" + t.message)
            }
        })

    }

    private fun validatePass()
    {
        errorValidationModels.clear()
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty,
                binding.etCurrentPass,
                binding.tvCurrentPassError
            )
        )
        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty,
                binding.etNewPass,
                binding.tvNewPassError
            )
        )

        errorValidationModels.add(
            ValidationModel(
                Validation.Type.PasswordStrong,
                binding.etNewPass,
                binding.tvNewPassError
            )
        )

        errorValidationModels.add(
            ValidationModel(
                Validation.Type.Empty,
                binding.etConfirmNewPass,
                binding.tvConfirmNewPassError
            )
        )

        errorValidationModels.add(
            ValidationModel(
                Validation.Type.PasswordMatch,
                binding.etNewPass,
                binding.etConfirmNewPass,
                binding.tvConfirmNewPassError
            )
        )

        val validation: Validation? = Validation.instance
        val resultReturn: ResultReturn? =
            validation?.CheckValidation(activity, errorValidationModels)
        if (resultReturn?.aBoolean == true) {
               updatePassword()
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


//        if(binding.etCurrentPass.text.isEmpty())
//        {
//            showError(binding.tvCurrentPassError,getString(R.string.this_field_is_required))
//        }
//        else if(binding.etNewPass.text.isEmpty())
//        {
//            showError(binding.tvNewPassError,getString(R.string.this_field_is_required))
//        }
//
//        else if(binding.etNewPass.text.toString().length < 6)
//        {
//            showError(binding.tvNewPassError, getString(R.string.the_password_must_contain_at_least_6_characters))
//        }
//        else if(binding.etConfirmNewPass.text.isEmpty())
//        {
//            showError(binding.tvConfirmNewPassError,getString(R.string.this_field_is_required))
//        }
//        else if(binding.etNewPass.text.toString() != binding.etConfirmNewPass.text.toString())
//        {
//            showError(binding.tvConfirmNewPassError, getString(R.string.password_and_confirm_password_does_not_match))
//        }
//        else
//        {
//           // Toast.makeText(activity,"update   6666",Toast.LENGTH_LONG).show()
//        }
    }

    private fun showHidePass(pos:Int,ivImage:ImageView,etpass:EditText)
    {
        if (!isPasswordVisible[pos])
        {
            isPasswordVisible[pos] = true
            ivImage.setImageResource(R.drawable.ic_hide)
            etpass.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            etpass.setSelection(etpass.length())

        }
        else
        {
            isPasswordVisible[pos] = false
            ivImage.setImageResource(R.drawable.ic_visibility)
            etpass.transformationMethod = PasswordTransformationMethod.getInstance()
            etpass.setSelection(etpass.length())
        }
    }

    override fun onClick(view: View?) {

        if(view == binding.ivBackBtn)
        {
            super.onBackPressed()
        }

        else if(view == binding.ivCurrentPass)
        {
            showHidePass(0,binding.ivCurrentPass,binding.etCurrentPass)
        }

        else if(view == binding.ivNewPass)
        {
            showHidePass(1,binding.ivNewPass,binding.etNewPass)
        }
        else if(view == binding.ivConfirmNewPass)
        {
            showHidePass(2,binding.ivConfirmNewPass,binding.etConfirmNewPass)
        }
        else if (view == binding.mcvUpdatePass)
        {
            validatePass()
        }
    }
}