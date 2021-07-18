package com.example.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.widget.doOnTextChanged
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.ConnectionManager
import com.example.foodrunner.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONObject
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        var flag=true
        //error for number
        et_number_et.doOnTextChanged { text, start, before, count ->
            if (text!!.length < 10) {
                et_number.error = "Invalid number (min 10 numbers) "
                flag=false
            } else if (text!!.length >= 10) {
                et_number.error = null
                flag=true
            }
        }
        //error for password
        et_pass_et.doOnTextChanged { text, start, before, count ->
            if (text!!.length < 5) {
                et_pass.error = "Min 5 Characters required "
                flag=false
            } else if (text!!.length >= 5) {
                et_pass.error = null
                flag=true
            }
        }
        val shared_user_id:SharedPreferences=getSharedPreferences(getString(R.string.reg_user_id),Context.MODE_PRIVATE)
        val shared_user_name:SharedPreferences=getSharedPreferences(getString(R.string.reg_name),Context.MODE_PRIVATE)
        val shared_user_email:SharedPreferences=getSharedPreferences(getString(R.string.reg_email),Context.MODE_PRIVATE)
        val shared_user_mobile_number:SharedPreferences=getSharedPreferences(getString(R.string.reg_mobile_number),Context.MODE_PRIVATE)
        val shared_user_address:SharedPreferences=getSharedPreferences(getString(R.string.reg_address),Context.MODE_PRIVATE)

        val intent = Intent(this@LoginActivity, MainActivityFragment::class.java)
        val intentRegister = Intent(this@LoginActivity, RegisterActivity::class.java)
        val intentForget = Intent(this@LoginActivity, ForgetActivity::class.java)
        tv_forgot_pass.setOnClickListener {
            startActivity(intentForget)
        }
        et_register.setOnClickListener {
            startActivity(intentRegister)
            finish()
        }

        val sharedPreferences: SharedPreferences =
            getSharedPreferences(getString(R.string.prefrences_file_name), Context.MODE_PRIVATE)
        var isLoggedIn = sharedPreferences.getBoolean("dataFile", false)

        val sharedPreferences1: SharedPreferences =
            getSharedPreferences(getString(R.string.prefrences_file_name1), Context.MODE_PRIVATE)
        if (isLoggedIn) {
            startActivity(intent)
            finish()
        }

        btn_login.setOnClickListener {
            if(et_number.editText?.text.toString().isNotBlank() or et_pass.editText?.text.toString().isNotBlank() and flag==true){ //blank pass and number
                //checking for credentials in database
                val jsonParam=JSONObject()
                jsonParam.put("mobile_number",et_number.editText?.text.toString())
                jsonParam.put("password",et_pass.editText?.text.toString())

                val queue=Volley.newRequestQueue(this)
                val url=" http://13.235.250.119/v2/login/fetch_result"

                if(ConnectionManager().checkConnectivity(this)){
                    val jsonRequest = object : JsonObjectRequest(
                        Request.Method.POST, url, jsonParam, Response.Listener {
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
                                    val intent= Intent(this@LoginActivity, MainActivityFragment::class.java)
                                    startActivity(intent)
                                    finish()
                                }else{
                                    Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT)
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
                    val dialog= AlertDialog.Builder(this@LoginActivity)
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
            }else{
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show()
            }
            }
        }

     /*   class DBAsyncTaskCred(
            val context: Context,
            val cartEntity: CartEntity,
            val mode: Int
        ) : AsyncTask<Void, Void, Boolean>() {
            val db =
                Room.databaseBuilder(context, CartDatabase::class.java, "credentials-db")
                    .fallbackToDestructiveMigration().build()

            override fun doInBackground(vararg params: Void?): Boolean {
                when (mode) {
                    1 -> {//check phone number and password match any existing,use for login
                        val cred: CartEntity? = db.credentialsDao().getCredentialsById(
                            cartEntity.phone_number.toString(),
                            cartEntity.password.toString()
                        )
                        db.close()
                        return cred != null  //true if credentials present
                    }
                    2 -> {//save credentials into db
                        db.credentialsDao().insertCredentials(cartEntity)
                        db.close()
                        return true
                    }
                    3 -> {//update credentials
                        db.credentialsDao().updateCredentials(
                            cartEntity.phone_number.toString(),
                            cartEntity.password.toString()
                        )
                        return true
                    }
                    4 -> {//check phone number exists or not
                        val cred2: CartEntity? = db.credentialsDao()
                            .getCredentialsByPhone(cartEntity.phone_number.toString())
                        db.close()
                        return cred2 != null//if true phone number present
                    }
                }
                return false
            }

        }

      */
    }


