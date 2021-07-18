package com.example.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.widget.doOnTextChanged
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.ConnectionManager
import com.example.foodrunner.R
import com.example.foodrunner.model.Credentials
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONObject
import java.lang.Exception

class RegisterActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        var flag=true
        //shared prefrences to store registered data and show it in profile fragment
        val shared_user_id:SharedPreferences=getSharedPreferences(getString(R.string.reg_user_id),Context.MODE_PRIVATE)
        val shared_user_name:SharedPreferences=getSharedPreferences(getString(R.string.reg_name),Context.MODE_PRIVATE)
        val shared_user_email:SharedPreferences=getSharedPreferences(getString(R.string.reg_email),Context.MODE_PRIVATE)
        val shared_user_mobile_number:SharedPreferences=getSharedPreferences(getString(R.string.reg_mobile_number),Context.MODE_PRIVATE)
        val shared_user_address:SharedPreferences=getSharedPreferences(getString(R.string.reg_address),Context.MODE_PRIVATE)
        val sharedPreferences: SharedPreferences =
            getSharedPreferences(getString(R.string.prefrences_file_name), Context.MODE_PRIVATE)//is user logged in
        //show error in password edit text
        et_register_pass_et.doOnTextChanged { text, start, before, count ->
            if (text!!.length < 5) {
                et_register_pass.error = "Min 5 Characters required "
                flag=false
            } else if (text!!.length >= 5) {
                et_register_pass.error = null
                flag=true
            }
        }
        //show error in number edit text
        et_register_number_et.doOnTextChanged { text, start, before, count ->
            if (text!!.length < 10) {
                et_register_number.error = "Min 10 numbers required "
                flag=false
            } else if (text!!.length >= 10) {
                et_register_number.error = null
                flag=true
            }
        }
        println { "number is ${et_register_number.editText?.text.toString()}" }
        //email error
        et_email_et.doOnTextChanged { text, start, before, count ->
            if (text!!.length > 4 && text!!.contains("@")) {
                et_email.error = null
                flag=true
            } else {
                et_email.error = "Enter valid Email"
                flag=false
            }
        }

        btn_register.setOnClickListener {
            if (et_register_number.editText?.text.toString().isNotBlank() && et_register_pass.editText?.text.toString().isNotBlank() and flag
            ) {
                val sendDataCredentials = Credentials(
                    et_name.editText?.text.toString(),
                    et_register_number.editText?.text.toString(),
                    et_register_pass.editText?.text.toString(),
                    et_address.editText?.text.toString(),
                    et_email.editText?.text.toString()
                )
                val jsonParams = JSONObject()
                jsonParams.put("name",sendDataCredentials.name)
                jsonParams.put("mobile_number",sendDataCredentials.mobile_number)
                jsonParams.put("password",sendDataCredentials.password)
                jsonParams.put("address",sendDataCredentials.address)
                jsonParams.put("email",sendDataCredentials.email)
                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/register/fetch_result"

                if (ConnectionManager().checkConnectivity(this)) {
                    val jsonRequest = object : JsonObjectRequest(
                        Request.Method.POST, url, jsonParams, Response.Listener {
                            try {
                                val firstObject=it.getJSONObject("data")
                                val success=firstObject.getBoolean("success")
                                println("response is $it")
                                if(success){ //user is registered
                                    val secondObject=firstObject.getJSONObject("data")
                                    shared_user_id.edit().putString("user_id",secondObject.getString("user_id")).apply()
                                    shared_user_name.edit().putString("name",secondObject.getString("name")).apply()
                                    shared_user_address.edit().putString("address",secondObject.getString("address")).apply()
                                    shared_user_email.edit().putString("email",secondObject.getString("email")).apply()
                                    shared_user_mobile_number.edit().putString("mobile_number",secondObject.getString("mobile_number")).apply()
                                    sharedPreferences.edit().putBoolean("dataFile", true).apply()//user logged in?= true
                                    val intent= Intent(this@RegisterActivity, MainActivityFragment::class.java)
                                    startActivity(intent)
                                    finish()
                                }else{
                                    Toast.makeText(this, "Invalid Informtion", Toast.LENGTH_SHORT)
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
                    val dialog= AlertDialog.Builder(this@RegisterActivity)
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
            }else
                Toast.makeText(this, "Invalid Information", Toast.LENGTH_SHORT).show()

            }

        }


    }

