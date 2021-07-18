package com.example.foodrunner.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.ConnectionManager
import com.example.foodrunner.model.OrderHistoryRestaurant
import com.example.foodrunner.R
import com.example.foodrunner.adapter.OrderHistoryAdapter
import org.json.JSONException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderHistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrderHistoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var layoutManager1: LinearLayoutManager
    lateinit var menuAdapter1: OrderHistoryAdapter
    lateinit var recyclerViewAllOrders: RecyclerView
    //lateinit var toolBar: androidx.appcompat.widget.Toolbar
    lateinit var orderHistoryLayout: RelativeLayout
    lateinit var noOrders: RelativeLayout

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
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_order_history, container, false)
        setHasOptionsMenu(true)//tells compiler this fragment has menu
        recyclerViewAllOrders = view.findViewById(R.id.recyclerViewAllOrders)
       // toolBar = view.findViewById(R.id.toolBar)
        orderHistoryLayout = view.findViewById(R.id.orderHistoryLayout)
        noOrders = view.findViewById(R.id.noOrders)
        //(activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow) change icon of home in toolbar
        return view
    }

    fun setItemsForEachRestaurant() {
        val orderedRestaurantList = ArrayList<OrderHistoryRestaurant>()
        val sharedPreferences = requireActivity().getSharedPreferences(
            getString(R.string.reg_user_id),
            Context.MODE_PRIVATE
        )

        val userId = sharedPreferences.getString("user_id", "000")
        if (ConnectionManager().checkConnectivity(requireActivity())) {

            orderHistoryLayout.visibility = View.VISIBLE

            try {
                val queue = Volley.newRequestQueue(activity as Context)
                val url =
                    "http://13.235.250.119/v2/orders/fetch_result/$userId"
                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.GET,
                    url,
                    null,
                    Response.Listener {
                        println("response is $it")
                        val response = it.getJSONObject("data")
                        val success = response.getBoolean("success")

                        if (success) {
                            val data = response.getJSONArray("data")
                            if (data.length() == 0) {

                                Toast.makeText(
                                    activity as Context,
                                    "No Orders Placed yet!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                noOrders.visibility = View.VISIBLE

                            } else {
                                noOrders.visibility = View.INVISIBLE

                                for (i in 0 until data.length()) {
                                    val restaurantItem = data.getJSONObject(i)
                                    val restaurantObject = OrderHistoryRestaurant(
                                        restaurantItem.getString("order_id"),
                                        restaurantItem.getString("restaurant_name"),
                                        restaurantItem.getString("total_cost"),
                                        restaurantItem.getString("order_placed_at").substring(0, 10)
                                    )

                                    orderedRestaurantList.add(restaurantObject)
                                    layoutManager1 = LinearLayoutManager(activity)
                                    menuAdapter1 = OrderHistoryAdapter(activity as Context, orderedRestaurantList)
                                    recyclerViewAllOrders.adapter = menuAdapter1
                                    recyclerViewAllOrders.layoutManager = layoutManager1
                                }
                            }
                        }
                        orderHistoryLayout.visibility = View.INVISIBLE
                    },
                    Response.ErrorListener {
                        orderHistoryLayout.visibility = View.INVISIBLE

                        Toast.makeText(
                            activity as Context,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "c35f1fe5712ae1"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)

            } catch (e: JSONException) {
                Toast.makeText(
                    activity as Context,
                    "Some Unexpected error occurred!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {
            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(activity as Context)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Check Internet Connection!")
            alterDialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }
            alterDialog.setNegativeButton("Exit") { _, _ ->
                requireActivity().finishAffinity()
            }
            alterDialog.setCancelable(false)
            alterDialog.create()
            alterDialog.show()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {//used to add menu items on the toolbar
        inflater.inflate(R.menu.back_button,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.back_button -> {
                super.requireActivity().onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            setItemsForEachRestaurant()
        } else {
            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(activity as Context)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Check Internet Connection!")
            alterDialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }
            alterDialog.setNegativeButton("Exit") { _, _ ->
                requireActivity().finishAffinity()
            }
            alterDialog.setCancelable(false)
            alterDialog.create()
            alterDialog.show()
        }
        super.onResume()
    }


    }


