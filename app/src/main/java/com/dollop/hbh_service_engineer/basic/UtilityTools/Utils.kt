package com.dollop.hbh_service_engineer.basic.UtilityTools

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.text.Spannable
import android.text.format.DateFormat
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.dollop.hbh_service_engineer.R
import com.dollop.hbh_service_engineer.basic.Database.UserDataHelper
import com.dollop.hbh_service_engineer.basic.Retrofit.APIError
import com.dollop.hbh_service_engineer.basic.Retrofit.Const
import com.dollop.hbh_service_engineer.basic.Retrofit.RetrofitClient
import com.dollop.hbh_service_engineer.databinding.AlertdialogBinding
import com.dollop.hbh_service_engineer.databinding.ItemCustomToastBinding
import com.dollop.hbh_service_engineer.main_package.activity.Activity_LoginActivity
import com.dollop.hbh_service_engineer.main_package.model.AllResponseModel
import com.dollop.hbh_service_engineer.main_package.model.EngineerInfo
import com.google.gson.Gson
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*


object Utils {
    @JvmStatic
    fun GetSession(): EngineerInfo {
        return UserDataHelper.instance.list[0]
    }

    @JvmStatic
    fun IS_LOGIN(): Boolean {
        return UserDataHelper.instance.list.size > 0
    }

    fun I(cx: Context, startActivity: Class<*>?, data: Bundle?) {
        val i = Intent(cx, startActivity)
        if (data != null) i.putExtras(data)
        cx.startActivity(i)
    }

    fun expandView(view: View) {
        view.visibility = View.VISIBLE
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val targetHeight = view.measuredHeight
        view.layoutParams.height = 1
        view.requestLayout()

        view.animate()
            .setDuration(200) // Adjust the duration as needed
            .setInterpolator(AccelerateDecelerateInterpolator())
            .translationY(0f)
            .alpha(1f)
            .setListener(null)
            .setUpdateListener {
                val params = view.layoutParams
                params.height = (targetHeight * it.animatedFraction).toInt()
                view.layoutParams = params
            }
    }

    fun collapseView(view: View) {
        val initialHeight = view.height
        view.animate()
            .setDuration(200) // Adjust the duration as needed
            .setInterpolator(AccelerateDecelerateInterpolator())
            .translationY(-initialHeight.toFloat())
            .alpha(0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    view.visibility = View.GONE
                }
            })
            .setUpdateListener {
                val params = view.layoutParams
                params.height = (initialHeight * (1 - it.animatedFraction)).toInt()
                view.layoutParams = params
            }
    }

    fun convert24HourToAMPM(givenTime: String): String {

        try {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val time = LocalTime.parse(givenTime, formatter)
            val ampmFormatter = DateTimeFormatter.ofPattern("HH:mm a")
            return time.format(ampmFormatter)
        } catch (e: DateTimeParseException) {
            return "Invalid time format: $givenTime"
        }
    }

    fun dateConvertor(date:String):String
    {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val inputDate = inputFormat.parse(date)
        val outputFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val formattedDate = outputFormat.format(inputDate!!)

        return formattedDate
    }

    @JvmStatic
    val deviceName: String
        get() {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer)) {
                capitalize(model)
            } else {
                capitalize(manufacturer) + " " + model
            }
        }

    private fun capitalize(s: String?): String {
        if (s == null || s.length == 0) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            first.uppercaseChar().toString() + s.substring(1)
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    fun setWebView(webView: WebView, data: String?) {
        webView.settings.javaScriptEnabled = true
        webView.settings.builtInZoomControls = true
        webView.loadData(data!!, "text/html; charset=utf-8", "UTF-8")
    }

    @JvmStatic
    fun UnAuthorizationToken(cx: Context) {
        UserDataHelper.instance.deleteAll()
        SavedData.saveRefferalCode(Constants.blank)
        SavedData.saveCouponCode(Constants.blank)
        I_clear(cx, Activity_LoginActivity::class.java, null)
    }

    fun byteArrayToBitmap(context: Context, data: ByteArray?): Bitmap {
        val bitmap: Bitmap
        if (data != null) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
        } else {
            bitmap = BitmapFactory.decodeResource(
                context.resources,
                R.drawable.dummy
            )
        }
        return bitmap
    }

    fun InternetDialog(context: Context?) {
        val dialog = Dialog(context!!, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.alertdialog)
        dialog.findViewById<View>(R.id.tvPermittManually).setOnClickListener { view: View? ->
            if (AppController.instance.isOnline) {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    fun SetHtmlContent(textView: TextView, data: String?) {
        val imageGetter = PicassoImageGetter(textView)
        val html: Spannable
        html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(data, Html.FROM_HTML_MODE_LEGACY, imageGetter, null) as Spannable
        } else {
            Html.fromHtml(data, imageGetter, null) as Spannable
        }
        textView.text = html
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    fun CustomAlertDialog(context: Context?, message: String?) {
        val dialog = Dialog(context!!, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val alertdialogBinding = AlertdialogBinding.inflate(LayoutInflater.from(context))
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(alertdialogBinding.root)
        alertdialogBinding.tvDesc.text = message
        dialog.findViewById<View>(R.id.tvPermittManually).setOnClickListener { view: View? ->
            if (AppController.instance.isOnline) {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    fun logoutAlertDialog(c: Context): Dialog {
        val dialog = Dialog(c, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        dialog.setCanceledOnTouchOutside(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.logout)
        dialog.findViewById<View>(R.id.tvOK).setOnClickListener { view: View? ->
            logout(c)
            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.tvCancel)
            .setOnClickListener { view: View? -> dialog.cancel() }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()
        return dialog
    }

    fun deleteAccountAlertDialog(c: Context): Dialog {
        val dialog = Dialog(c, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        dialog.setCanceledOnTouchOutside(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.logout)
        val textView = dialog.findViewById<TextView>(R.id.tvDesc)
        textView.setText(R.string.are_you_sure_want_to_delete_your_account)
        dialog.findViewById<View>(R.id.tvOK).setOnClickListener { view: View? ->
            //deleteUserAccount(c)
            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.tvCancel)
            .setOnClickListener { view: View? -> dialog.cancel() }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()
        return dialog
    }


    private fun logout(c: Context) {
        val progressDialog = initProgressDialog(c)
        RetrofitClient.client.studentLogOut(GetSession().token, "")
            ?.enqueue(object : Callback<AllResponseModel?> {
                override fun onResponse(
                    call: Call<AllResponseModel?>,
                    response: Response<AllResponseModel?>
                ) {
                    progressDialog.dismiss()
                    try {
                        if (response.code() == StatusCodeConstant.OK) {
                            assert(response.body() != null)
                            //Utils.T(c,Constants.Success);
                            UnAuthorizationToken(c)
                        } else {
                            assert(response.errorBody() != null)
                            val message = Gson().fromJson(
                                response.errorBody()!!.charStream(),
                                APIError::class.java
                            )
                            if (response.code() == StatusCodeConstant.BAD_REQUEST) {
                                CustomeToast(c, message.message, Constants.warning)
                            } else if (response.code() == StatusCodeConstant.UNAUTHORIZED) {
                                UnAuthorizationToken(c)
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
                    E("getMessage::" + t.message)
                }
            })
    }

    fun Picasso(Url: String, imageView: ImageView?, dummy: Int) {
        Picasso.get().load(Const.HOST_URL + Url).fetch(object : com.squareup.picasso.Callback {
            override fun onSuccess() {
                if (dummy == 0) Picasso.get()
                    .load(Const.HOST_URL + Url)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(imageView) else Picasso.get()
                    .load(Const.HOST_URL + Url)
                    .error(dummy)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(imageView)
            }

            override fun onError(e: Exception) {
                if (dummy == 0) Picasso.get()
                    .load(Const.HOST_URL + Url)
                    .into(imageView) else Picasso.get()
                    .load(Const.HOST_URL + Url)
                    .error(dummy)
                    .into(imageView)
            }
        })
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    val currentDate: String
        get() {
            val c = Calendar.getInstance().time
            E("Current time => $c")
            val df =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return df.format(c)
        }

    fun prettyCount(number: Number): String {
        val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
        val numValue = number.toLong()
        val value = Math.floor(Math.log10(numValue.toDouble())).toInt()
        val base = value / 3
        return if (value >= 3 && base < suffix.size) {
            E(
                "::prettyCount::" + numValue / Math.pow(
                    10.0,
                    (base * 3).toDouble()
                )
            )
            val i = "" + numValue / Math.pow(10.0, (base * 3).toDouble())
            val values = i.split("\\.").toTypedArray()
            if (values[1] == "0") {
                values[0] + suffix[base]
            } else {
                DecimalFormat("#0.0")
                    .format(numValue / Math.pow(10.0, (base * 3).toDouble())) + suffix[base]
            }
        } else {
            DecimalFormat("#,##0").format(numValue)
        }
    }

    fun <T> removeDuplicates(list: List<T>): List<T> {

        // Create a new ArrayList
        val newList: MutableList<T> = ArrayList()

        // Traverse through the first list
        for (element in list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {
                newList.add(element)
            }
        }

        // return the new list
        return newList
    }

    /**
     * Change the status bar Color of the Activity to the Desired Color.
     *
     * @param activity - Activity
     * @param color    - Desired Color
     */
    fun changeStatusBarColor(activity: Activity, color: Int) {
        val window = activity.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity, color)
    }

    fun printKeyHash(context: Activity): String? {
        val packageInfo: PackageInfo
        var key: String? = null
        try {
//getting application package name, as defined in manifest
            val packageName = context.applicationContext.packageName

//Retriving package info
            packageInfo = context.packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            )
            E("Package Name=" + context.packageName)
            for (signature in packageInfo.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                key = String(Base64.encode(md.digest(), 0))
                E("Key Hash=$key")
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            E("Name not found$e1")
        } catch (e: NoSuchAlgorithmException) {
            E("No such an algorithm$e")
        } catch (e: Exception) {
            E("Exception$e")
        }
        return key
    }

    fun I_finish(cx: Context, startActivity: Class<*>?, data: Bundle?) {
        val i = Intent(cx, startActivity)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        if (data != null) i.putExtras(data)
        cx.startActivity(i)
    }

    fun I_clear(cx: Context, startActivity: Class<*>?, data: Bundle?) {
        val i = Intent(cx, startActivity)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        if (data != null) i.putExtras(data)
        cx.startActivity(i)
    }

    @JvmStatic
    fun E(msg: String?) {
        if (Const.Development == Constants.Debug) Log.e("Log.E", msg!!)
    }

    fun DetectUIMode(activity: Activity): Int {
        return activity.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
    }

    fun getFormattedDate(smsTimeInMilis: Long, context: Context): String {
        val smsTime = Calendar.getInstance()
        smsTime.timeInMillis = smsTimeInMilis
        val now = Calendar.getInstance()
        val timeFormatString = "h:mm aa"
        val dateTimeFormatString = "EEE, MMM d | h:mm aa"
        val HOURS = (60 * 60 * 60).toLong()
        return if (now[Calendar.DATE] == smsTime[Calendar.DATE]) {
            context.getString(R.string.today) + " " + DateFormat.format(
                timeFormatString,
                smsTime
            )
        } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] == 1) {
            context.getString(R.string.yesterday) + " " + DateFormat.format(
                timeFormatString,
                smsTime
            )
        } else if (now[Calendar.YEAR] == smsTime[Calendar.YEAR]) {
            DateFormat.format(dateTimeFormatString, smsTime).toString()
        } else {
            DateFormat.format("MMM dd yyyy | h:mm aa", smsTime).toString()
        }
    }

    val dateAfterYear: String
        get() {
            val cal = Calendar.getInstance()
            cal.time = Date()
            cal.add(Calendar.YEAR, 1)
            val smsTime = Calendar.getInstance()
            smsTime.timeInMillis = cal.time.time
            return DateFormat.format(
                "dd'" + getOrdinal(
                    smsTime[Calendar.DAY_OF_MONTH]
                ) + "' MMM yyyy", smsTime
            ).toString()
        }

    fun getOrdinal(day: Int): String {
        val ordinal: String
        ordinal = when (day % 20) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> if (day > 30) "st" else "th"
        }
        return ordinal
    }

    fun initProgressDialog(c: Context?): Dialog {
        val dialog = Dialog(c!!)
        dialog.setCanceledOnTouchOutside(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.progress_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()
        return dialog
    }

    fun videoProgressDialog(c: Context?): Dialog {
        val dialog = Dialog(c!!)
        dialog.setCanceledOnTouchOutside(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.progress_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(true)
        dialog.show()
        return dialog
    }

    fun pdfProgressDialog(c: Context?): Dialog {
        val dialog = Dialog(c!!)
        dialog.setCanceledOnTouchOutside(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.progress_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnCancelListener { CustomeToast(c, "Unable to load Pdf", Constants.warning) }
        dialog.show()
        return dialog
    }

    @SuppressLint("InflateParams")
    fun T(c: Context?, msg: String?) {
        val toast = Toast(c)
        val view = LayoutInflater.from(c).inflate(R.layout.custom_toast, null)
        val textView = view.findViewById<TextView>(R.id.tvText)
        textView.text = msg
        toast.view = view
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }

    @JvmStatic
    @SuppressLint("ClickableViewAccessibility")
    fun CustomeToast(c: Context?, msg: String?, status: String?) {
        val toast = Toast(c)
        val itemCustomToastBinding = ItemCustomToastBinding.inflate(LayoutInflater.from(c))
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.TOP, 0, 0)
        when (status) {
            Constants.warning -> {
                itemCustomToastBinding.ivCheckBox.setImageResource(R.drawable.ic_warning)
                itemCustomToastBinding.ivCheckBox.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        c!!, R.color.yellow
                    )
                )
            }
            Constants.success -> {
                itemCustomToastBinding.ivCheckBox.setImageResource(R.drawable.ic_checkbox)
                itemCustomToastBinding.ivCheckBox.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        c!!, R.color.theme
                    )
                )
            }
            Constants.error -> {
                itemCustomToastBinding.ivCheckBox.setImageResource(R.drawable.ic_cross_circle)
                itemCustomToastBinding.ivCheckBox.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        c!!, R.color.red
                    )
                )
            }
            else -> {
                itemCustomToastBinding.ivCheckBox.setImageResource(R.drawable.ic_info)
                itemCustomToastBinding.ivCheckBox.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        c!!, R.color.theme
                    )
                )
            }
        }
        itemCustomToastBinding.tvMessage.text = msg
        toast.view = itemCustomToastBinding.root //setting the view of custom toast layout
        toast.show()
    }

    @SuppressLint("InflateParams")
    fun TLong(c: Context?, msg: String?) {
        val toast = Toast(c)
        val view = LayoutInflater.from(c).inflate(R.layout.custom_toast, null)
        val textView = view.findViewById<TextView>(R.id.tvText)
        textView.text = msg
        toast.view = view
        toast.duration = Toast.LENGTH_LONG
        toast.show()
    }

    fun share(c: Context, subject: String?, shareBody: String?) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        c.startActivity(Intent.createChooser(sharingIntent, "Share via"))
    }

    fun T_Long(c: Context?, msg: String?) {
        Toast.makeText(c, msg, Toast.LENGTH_LONG).show()
    }

    /*public static void setLanguage(String language, @NonNull Context context) {
        SavedData.saveLanguage(language);
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
        LocaleHelper.setLocale(context, language);
    }*/
    fun alert(activity: Context, message: String?) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(activity.getString(R.string.app_name))
        builder.setMessage(message)
            .setPositiveButton(R.string.ok) { dialogInterface: DialogInterface, i: Int -> dialogInterface.cancel() }
        val alertdialog = builder.create()
        alertdialog.show()
    }
}