package com.example.foodrunner.fragment


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.ConnectionManager
import com.example.foodrunner.R
import com.example.foodrunner.adapter.HomeRecyclerAdapter
import com.example.foodrunner.model.Restaurants
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap


// TODO: Rename parameter arguments, choose names that match
// the com.example.bookhub.fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this com.example.bookhub.fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var etSearch:EditText
    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    var tempArrayList= arrayListOf<Restaurants>()//temp array list to help in searchview
    var restaurantsInfoList=arrayListOf<Restaurants>()//array list of custom data type Restaurants
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayout: LinearLayout
    lateinit var progressBar:ProgressBar
    lateinit var cantFind: RelativeLayout

    //Kotlin provides Comparator interface to order the objects of user-defined classes.
    var ratingComparator = Comparator<Restaurants>{book1, book2 ->

        if (book1.restaurant_ratings.compareTo(book2.restaurant_ratings, true) == 0) {
            // sort according to name if rating is same
            book1.restaurant_ratings.compareTo(book2.restaurant_ratings, true)
        } else {
            book1.restaurant_ratings.compareTo(book2.restaurant_ratings, true)
        }
    }
    var costComparator = Comparator<Restaurants>{book1, book2 ->

        if (book1.restaurant_cost.compareTo(book2.restaurant_cost, true) == 0) {
            // sort according to name if rating is same
            book1.restaurant_ratings.compareTo(book2.restaurant_ratings, true)
        } else {
            book1.restaurant_cost.compareTo(book2.restaurant_cost, true)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this com.example.bookhub.fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
       // etSearch=view.findViewById(R.id.etSearch)
        progressLayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)
        progressLayout.visibility=View.VISIBLE
       // cantFind = view.findViewById(R.id.cantFind)
       // tempArrayList

        setHasOptionsMenu(true)//tells compiler this fragment has menu

        //making request using volley
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        if(ConnectionManager().checkConnectivity(activity as Context)){//checking for internet connection
            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener {
                                  try{//to handle json exception

                                      println("response is $it ") //to print json response in logcat
                                      //now parse json response
                                      val init=it.getJSONObject("data")//get the json objects reference
                                      val success= init.getBoolean("success")//getting value of boolean named success in response
                                      if(success){//success is true in response if we get correct response from api/server
                                          println(" success passed")
                                          val jsonArray=init.getJSONArray("data")//getting reference of json array
                                          for(i in 0 until jsonArray.length()) {//iterating in json array over json objects
                                              val jsonObjectSingle =
                                                  jsonArray.getJSONObject(i)//getting current json object reference
                                              val singleObjectRef = Restaurants(
                                                  //Restaurants is custom data type for json object elements,getting element values
                                                  jsonObjectSingle.getString("id"),
                                                  jsonObjectSingle.getString("name"),
                                                  jsonObjectSingle.getString("rating"),
                                                  jsonObjectSingle.getString("cost_for_one"),
                                                  jsonObjectSingle.getString("image_url"),
                                              )
                                              //adding parsed data to our array
                                              restaurantsInfoList.add(singleObjectRef)//adding every json object as single element of array list
                                          }
                                              tempArrayList.addAll(restaurantsInfoList)
                                              if(activity!=null){//if fragment is attached to activity
                                                  //after getting and parsing data let's fill data into adapter
                                                  recyclerDashboard = view.findViewById(R.id.recyclerDashboard)//reference to recycler view widget of xml
                                                  layoutManager = LinearLayoutManager(activity)// arranges the items in a one-dimensional list.
                                                  recyclerAdapter = HomeRecyclerAdapter(activity as Context, tempArrayList)//passing context and dataset(array list) in adapter class for binding of data
                                                  recyclerDashboard.adapter = recyclerAdapter//setting adapter of recyclerview widget in xml file
                                                  recyclerDashboard.layoutManager = layoutManager//setting layout manager of recyclerview widget (linearlayout manager= one dimensional list, gridlayout manager= 2D list)
                                              }


                                      }else{//if success key doesn't have true value means request is not send correctly
                                          Toast.makeText(
                                              activity as Context,
                                              "Error occurred while parsing data, request not sent properly",
                                              Toast.LENGTH_SHORT
                                          ).show()
                                      }
                                      progressLayout.visibility=View.GONE//hiding progress bar
                                  }catch(e:JSONException){
                                      Toast.makeText(activity as Context,"JSON exception!",
                                          Toast.LENGTH_SHORT).show()

                                  }
                    //if error occurred on volley side
                }, Response.ErrorListener {
                    println("Error is $it") //print json error in logcat
                    if (activity != null) {//When getActivity returns null it means that the Fragment is not attached to the Activity. so we have to check that.
                        Toast.makeText(activity as Context,"Volley error occurred",Toast.LENGTH_SHORT).show()
                    }
                }
            ) {//specifies content type and key in the request
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "c35f1fe5712ae1"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }else{//if not connected to internet
            //show dialog box to user
            val dialog=AlertDialog.Builder(activity as Context) //making instance of AlertDial class
            dialog.setTitle("Error")//setting title of dialog
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings"){//positive button
                text,listener->
                    val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)//open settings using intents
                startActivity(settingsIntent)
                activity?.finish() //finish current activity, in fragments we do it like this
            }
            dialog.setNegativeButton("Cancel"){
                text,listener->
                ActivityCompat.finishAffinity(activity as Activity)//stopping whole app
            }
            dialog.create()
            dialog.show()

        }







     /*   fun filterFun(strTyped: String) {
            val filteredList = arrayListOf<Restaurants>()

            for (item in restaurantsInfoList) {
                if (item.restaurant_name.toLowerCase(Locale.ROOT)
                        .contains(strTyped.toLowerCase(Locale.ROOT))
                ) {
                    filteredList.add(item)
                }
            }

            if (filteredList.size == 0) {
                cantFind.visibility = View.VISIBLE
            } else {
                cantFind.visibility = View.INVISIBLE
            }

            recyclerAdapter.filterList(filteredList)

        }

        etSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(strTyped: Editable?) {
                filterFun(strTyped.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

      */


        return view
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {//used to add menu items on the toolbar
        //implementing search on toolbar for restaurants
        inflater.inflate(R.menu.home_sort,menu)
        val item= menu.findItem(R.id.search_menu)
        val searchView=item.actionView as SearchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempArrayList.clear()
                val searchText= newText!!.toLowerCase(Locale.getDefault())
                if(searchText.isNotBlank()){
                    restaurantsInfoList.forEach{
                        if(it.restaurant_name.toLowerCase(Locale.getDefault()).contains(searchText)){
                            tempArrayList.add(it)
                        }
                    }
                    recyclerAdapter.notifyDataSetChanged()
                }else{
                    tempArrayList.clear()
                    tempArrayList.addAll(restaurantsInfoList)
                    recyclerAdapter.notifyDataSetChanged()
                }
                return false
            }

        })
        //return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {//handle clicks on menu items,in our case sort icon
        val id = item.itemId
        if(id==R.id.home_sort){//if id of item click is equal to menu item's id that we had creted
            val dialog=AlertDialog.Builder(activity as Context)
            dialog.setTitle("sort by?")
            val items= arrayOf<String>("Cost(Low to High)","Cost(High to Low","Rating")
            var checkedItem=0
            dialog.setSingleChoiceItems(items,checkedItem) { _, which ->
                checkedItem=which
            }
                .setPositiveButton("Ok"){
                        _, which->
                    when(checkedItem){
                        0->{
                            Collections.sort(tempArrayList,costComparator)
                        }
                        1->{
                            Collections.sort(tempArrayList,costComparator)
                            tempArrayList.reverse()
                        }
                        2->{
                            Collections.sort(tempArrayList,ratingComparator)
                            tempArrayList.reverse()
                        }
                    }
                    recyclerAdapter.notifyDataSetChanged()//notify adapter that data set has changed
                }
                .setNegativeButton("Cancel"){dialog,which->{
                    //do nothing
            }
                }
            dialog.create()
            dialog.show()

        //used to sort elements of bookInfoList using Comparator(used for custom sorting)
           // Collections.sort(bookInfoList,ratingComparator)
           // bookInfoList.reverse()
        }
        recyclerAdapter.notifyDataSetChanged()//notify adapter that data set has changed
        return super.onOptionsItemSelected(item)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this com.example.bookhub.fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of com.example.bookhub.fragment DashboardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}