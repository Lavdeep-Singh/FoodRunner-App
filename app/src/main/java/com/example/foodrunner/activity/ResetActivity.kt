package com.example.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.ConnectionManager
import com.example.foodrunner.R
import kotlinx.android.synthetic.main.activity_forget.*
import kotlinx.android.synthetic.main.activity_reset.*
import org.json.JSONObject
import java.lang.Exception

class ResetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)
        var mobile_numberr:String="1234567890"
        //getting mobile number
        if(intent!=null){
            mobile_numberr= intent.getStringExtra("mobile_number").toString()
        }
        //println("response mobile number is $mobile_numberr")

        btn_submit.setOnClickListener {
            if(tv_rest_new_password.editText?.text.toString().isNotBlank() &&
                tv_reset_confirm_password.editText?.text.toString().isNotBlank() && tv_rest_new_password.editText?.text.toString()== tv_reset_confirm_password.editText?.text.toString()){


                val jsonParams = JSONObject()
                jsonParams.put("mobile_number",mobile_numberr)
                jsonParams.put("password",tv_rest_new_password.editText?.text.toString())
                jsonParams.put("otp",tv_rest_otp.editText?.text.toString())


                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                if (ConnectionManager().checkConnectivity(this)) {
                    val jsonRequest = object : JsonObjectRequest(
                        Request.Method.POST, url, jsonParams, Response.Listener {
                            try {

                                val firstObject=it.getJSONObject("data")
                                val success=firstObject.getBoolean("success")
                                println("response is $it")
                                if(success){
                                    Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT)
                                        .show()
                                    val intent= Intent(this@ResetActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                else{
                                    Toast.makeText(this, "Wrong Credentials", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(this, "Json exception", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }, Response.ErrorListener {
                            Toast.makeText(this, "Volley error occurred", Toast.LENGTH_SHORT).show()
                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "c35f1fe5712ae1"
                            return headers
                        }


                    }
                    queue.add(jsonRequest)

                } else {
                    val dialog= AlertDialog.Builder(this@ResetActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("No Internet Connection")
                    dialog.setPositiveButton("Open Settings"){
                            text,listener->
                        startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                        this.finish()
                    }
                    dialog.setNegativeButton("Cancel") { text, listener ->
                        ActivityCompat.finishAffinity(this)
                    }
                    dialog.create()
                    dialog.show()
                }
            }
        }
    }
}