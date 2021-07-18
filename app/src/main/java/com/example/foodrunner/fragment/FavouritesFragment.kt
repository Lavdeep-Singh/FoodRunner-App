package com.example.foodrunner.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodrunner.R
import com.example.foodrunner.adapter.HomeRecyclerAdapter
import com.example.foodrunner.model.Restaurants
import database.RestaurantDatabase
import database.RestaurantsEntity
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the com.example.bookhub.fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavouritesFragment.newInstance] factory method to
 * create an instance of this com.example.bookhub.fragment.
 */
class FavouritesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var recyclerFavourites: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: LinearLayout
    lateinit var progressBar: ProgressBar
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    var dbRestaurantList= arrayListOf<Restaurants>()//list of type,restaurants entity data class

    var ratingComparator = Comparator<Restaurants>{book1, book2 ->

        if (book1.restaurant_ratings.compareTo(book2.restaurant_ratings, true) == 0) {
            // sort according to name if rating is same
            book1.restaurant_name.compareTo(book2.restaurant_name, true)
        } else {
            book1.restaurant_ratings.compareTo(book2.restaurant_ratings, true)
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
        val view= inflater.inflate(R.layout.fragment_favourites, container, false)
        setHasOptionsMenu(true)//tells compiler this fragment has menu
        progressLayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)
        recyclerFavourites=view.findViewById(R.id.recyclerFavourites)
        layoutManager= LinearLayoutManager(activity as Context)
        dbRestaurantList=
            RetrieveFavRestaurants(activity as Context).execute().get() as ArrayList<Restaurants>//get list of fav restaurants from DB
        if(activity!=null){//When getActivity returns null it means that the Fragment is not attached to the Activity. so we have to check that.
            recyclerAdapter= HomeRecyclerAdapter(activity as Context,dbRestaurantList)
            recyclerFavourites.adapter=recyclerAdapter
            recyclerFavourites.layoutManager=layoutManager
            progressLayout.visibility=View.GONE
        }

        return view
    }
    class DBAsyncTask(val context:Context, val restaurantsEntity: RestaurantsEntity, val mode:Int):AsyncTask<Void,Void,Boolean>(){
        val db= Room.databaseBuilder(context,RestaurantDatabase::class.java,"restaurants-db").fallbackToDestructiveMigration().build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when(mode){
                1->{//check in DB if restaurant is present or not
                    val restaurant: RestaurantsEntity? = db.restaurantsDao().getRestaurantssById(restaurantsEntity.restaurant_id.toString())
                    db.close()
                    return restaurant!=null//true if restaurant found
                }
                2->{//save the restaurant into DB as favourite
                    db.restaurantsDao().insertRestaurants(restaurantsEntity)
                    db.close()
                    return true

                }
                3->{//remove the favourite book
                    db.restaurantsDao().deleteRestaurants(restaurantsEntity)
                    db.close()
                    return true
                }

            }

            return false
        }

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {//used to add menu items on the toolbar
        inflater.inflate(R.menu.favourites_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {//handle clicks on menu items
        val id = item.itemId
        if(id==R.id.action_refresh){//if id of item click is equal to menu item's id that we had created
            //refreshing arraylist
            dbRestaurantList=RetrieveFavRestaurants(activity as Context).execute().get() as ArrayList<Restaurants>
            recyclerAdapter= HomeRecyclerAdapter(activity as Context,dbRestaurantList)
            recyclerFavourites.adapter=recyclerAdapter
            recyclerFavourites.layoutManager=layoutManager
            Toast.makeText(activity,"Refreshed",Toast.LENGTH_SHORT).show()
            //recyclerAdapter.notifyDataSetChanged()//notify adapter that data set has changed
        }

        return super.onOptionsItemSelected(item)
    }

    //to get all restaurants of DB , have return type of Restaurants Entity
    class RetrieveFavRestaurants(val context:Context):AsyncTask<Void,Void,List<Restaurants>>(){
        override fun doInBackground(vararg params: Void?): List<Restaurants> {
            val db= Room.databaseBuilder(context,RestaurantDatabase::class.java,"restaurants-db").build()
            return db.restaurantsDao().getAllRestaurants()
        }

    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this com.example.bookhub.fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of com.example.bookhub.fragment FavouritesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavouritesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}