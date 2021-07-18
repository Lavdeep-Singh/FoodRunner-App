package com.example.foodrunner.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.ConnectionManager
import com.example.foodrunner.adapter.MenuRecyclerAdapter
import com.example.foodrunner.R
import com.example.foodrunner.model.MenuData
import kotlinx.android.synthetic.main.activity_menu.*
import org.json.JSONException

class MenuActivity : AppCompatActivity() {
   // lateinit var theDataArray:MutableList<getData>
    lateinit var recyclerMenu: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    val menuInfoList= arrayListOf<MenuData>()
    lateinit var recyclerAdapter: MenuRecyclerAdapter
    lateinit var restaurantId:String
    lateinit var restaurantName:String
    lateinit var proceedToCartLayout: RelativeLayout
    lateinit var btnProceedToCart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

         restaurantId = intent.getStringExtra("restaurantId")!!
         restaurantName=intent.getStringExtra("restaurantName")!!
        btnProceedToCart = findViewById(R.id.btnProceedToCart)
        proceedToCartLayout = findViewById(R.id.relativeLayoutProceedToCart)

        val queue = Volley.newRequestQueue(this@MenuActivity)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"
        if (ConnectionManager().checkConnectivity(this)) {//checking for internet connection
            val jsonRequest = object : JsonObjectRequest(
                Request.Method.GET, url, null, Response.Listener {
                    try{
                        println("response is $it")
                        val firstObject=it.getJSONObject("data")
                        val success=firstObject.getBoolean("success")
                        if(success){
                            val jsonArray=firstObject.getJSONArray("data")
                            for(i in 0 until jsonArray.length()){
                                val secondSingleObject=jsonArray.getJSONObject(i)
                                val singleObjectRef=MenuData(
                                    secondSingleObject.getString("id"),
                                    secondSingleObject.getString("name"),
                                    secondSingleObject.getString("cost_for_one"),
                                    secondSingleObject.getString("restaurant_id"),
                                )
                                menuInfoList.add(singleObjectRef)
                            }
                            println("End of for loop response")

                            recyclerMenu=findViewById(R.id.recycler_menu)
                            layoutManager=LinearLayoutManager(this@MenuActivity)
                            recyclerAdapter= MenuRecyclerAdapter(this,restaurantId,restaurantName,proceedToCartLayout,btnProceedToCart,menuInfoList)
                            recyclerMenu.layoutManager=layoutManager
                            recyclerMenu.adapter=recyclerAdapter
                        }else{
                            Toast.makeText(
                                this,
                                "Error occurred while parsing data, request not sent properly",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }catch(e:JSONException){
                        Toast.makeText(this@MenuActivity,"JSON exception!",
                            Toast.LENGTH_SHORT).show()
                    }

                }, Response.ErrorListener {
                    println("Error is $it") //print json error in logcat
                    Toast.makeText(this,"Volley error occurred",Toast.LENGTH_SHORT).show()
                }
            ) {
                //specifies content type and key in the request
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "c35f1fe5712ae1"
                    return headers
                }

            }
            queue.add(jsonRequest)

        }else{//if not connected to internet
            //show dialog box to user
            val dialog= AlertDialog.Builder(this) //making instance of AlertDial class
            dialog.setTitle("Error")//setting title of dialog
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings"){//positive button
                    text,listener->
                val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)//open settings using intents
                startActivity(settingsIntent)
                this.finish() //finish current activity, in fragments we do it like this
            }
            dialog.setNegativeButton("Cancel"){
                    text,listener->
                ActivityCompat.finishAffinity(this)//stopping whole app
            }
            dialog.create()
            dialog.show()

        }
    }

    override fun onBackPressed() {
        if (recyclerAdapter.getSelectedItemCount() > 0) {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("Alert!")
            alterDialog.setMessage("Going back will remove everything from cart")
            alterDialog.setPositiveButton("Okay") { _, _ ->
                super.onBackPressed()
            }
            alterDialog.setNegativeButton("Cancel") { _, _ ->
            }
            alterDialog.show()
        } else {
            super.onBackPressed()
        }
    }

}
