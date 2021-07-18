package com.example.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodrunner.R
import com.example.foodrunner.activity.MenuActivity
import com.example.foodrunner.fragment.FavouritesFragment
import com.example.foodrunner.model.Restaurants
import com.squareup.picasso.Picasso
import database.RestaurantDatabase
import database.RestaurantsEntity

class HomeRecyclerAdapter(val context: Context, var itemList: ArrayList<Restaurants>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {
    val  shared_rest_id: SharedPreferences =context.getSharedPreferences(context.resources.getString(
        R.string.rest_id
    ),Context.MODE_PRIVATE)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_row_for_adapter_dashboard, parent, false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restSingle = itemList[position]
        holder.txtRestaurantName.text = restSingle.restaurant_name
        holder.txtRestaurantRating.text = restSingle.restaurant_ratings
        holder.txtRestaurantsPrice.text = "Rs.${restSingle.restaurant_cost}/person"
        Picasso.get().load(restSingle.restaurant_image).error(R.mipmap.ic_launcher)
            .into(holder.imgRestaurantImage)

        val listOfFavourites = GetAllFavAsyncTask(context).execute().get()

        if (listOfFavourites.isNotEmpty() && listOfFavourites.contains(restSingle.restaurant_id.toString())) {
            holder.favStar.setImageResource(R.drawable.heart)
        } else {
            holder.favStar.setImageResource(R.drawable.heart_outline)
        }

        holder.favStar.setOnClickListener {
            val restaurantEntity = RestaurantsEntity(
                restSingle.restaurant_id,
                restSingle.restaurant_name,
                restSingle.restaurant_ratings,
                restSingle.restaurant_cost,
                restSingle.restaurant_image
            )
            if (!FavouritesFragment.DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async =
                    FavouritesFragment.DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    holder.favStar.setImageResource(R.drawable.heart)
                }
            } else {
                val async = FavouritesFragment.DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()

                if (result) {
                    holder.favStar.setImageResource(R.drawable.heart_outline)
                }
            }
        }
        holder.cardRestaurant.setOnClickListener {
            val intent= Intent(context, MenuActivity::class.java)
            intent.putExtra("restaurantId",restSingle.restaurant_id)
            intent.putExtra("restaurantName",restSingle.restaurant_name)
            context.startActivity(intent)
            }
        }


    /*    //favourite button functionality
        var count = 1
        var flag=false
        if (FavouritesFragment.DBAsyncTask(context, restaurantEntity, 1).execute()
                .get()
        ) {//true if restaurant already present
            holder.favStar.setImageResource(R.drawable.heart)
            count++
        }else{
            flag=true
        }
        holder.favStar.setOnClickListener {
            if (count % 2 == 0) {//outline star
                holder.favStar.setImageResource(R.drawable.heart_outline)
                FavouritesFragment.DBAsyncTask(
                    context,
                    restaurantEntity,
                    3
                ).execute()//remove restaurant as fav
                count++
                flag=true //next time on click star can be filled

            } else {
                if (flag) {  //restaurant not present in DB
                    holder.favStar.setImageResource(R.drawable.heart)  //fill star
                    FavouritesFragment.DBAsyncTask(
                        context,
                        restaurantEntity,
                        2
                    ).execute()//save restaurant as fav
                    count++
                }
            }
        }

     */




    override fun getItemCount(): Int {
        return itemList.size
    }

class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val txtRestaurantName: TextView = view.findViewById(R.id.txt_restaurantName)
    val txtRestaurantsPrice: TextView = view.findViewById(R.id.txt_price)
    val txtRestaurantRating: TextView = view.findViewById(R.id.txt_ratings)
    val imgRestaurantImage: ImageView = view.findViewById(R.id.img_restaurant)
    val favStar: ImageView = view.findViewById(R.id.fav_star)
    val cardRestaurant:LinearLayout=view.findViewById(R.id.llContent)
}
    fun filterList(filteredList: ArrayList<Restaurants>) {
        itemList = filteredList
        notifyDataSetChanged()
    }

class GetAllFavAsyncTask(
    context: Context
) :
    AsyncTask<Void, Void, List<String>>() {

    val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()
    override fun doInBackground(vararg params: Void?): List<String> {

        val list = db.restaurantsDao().getAllRestaurants()
        val listOfIds = arrayListOf<String>()
        for (i in list) {
            listOfIds.add(i.restaurant_id.toString())
        }
        return listOfIds
    }
}
}
