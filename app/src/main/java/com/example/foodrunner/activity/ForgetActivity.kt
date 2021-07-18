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
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONObject
import java.lang.Exception

class ForgetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget)

        btn_forget.setOnClickListener {
            if(tv_rest_number.editText?.text.toString().isNotBlank() &&
                tv_reset_email.editText?.text.toString().isNotBlank()){
                val jsonParams = JSONObject()
                jsonParams.put("email",tv_reset_email.editText?.text.toString())
                jsonParams.put("mobile_number",tv_rest_number.editText?.text.toString())

                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

                if (ConnectionManager().checkConnectivity(this)) {
                    val jsonRequest = object : JsonObjectRequest(
                        Request.Method.POST, url, jsonParams, Response.Listener {
                            try {
                                val firstObject=it.getJSONObject("data")
                                val success=firstObject.getBoolean("success")
                                println("response is $it")
                                if(success){
                                    val intent= Intent(this@ForgetActivity, ResetActivity::class.java)
                                    intent.putExtra("mobile_number",tv_rest_number.editText?.text.toString())
                                    startActivity(intent)
                                    finish()
                                }
                                else{
                                    Toast.makeText(this, "Mobile number OR Email Id is already registered", Toast.LENGTH_SHORT)
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
                    val dialog= AlertDialog.Builder(this@ForgetActivity)
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
