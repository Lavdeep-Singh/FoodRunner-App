package com.example.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrunner.R
import com.example.foodrunner.activity.CartActivity
import com.example.foodrunner.model.MenuData

class MenuRecyclerAdapter(val context:Context, val restaurantId:String, val restaurantName:String, val proceedToCartPassed: RelativeLayout,
                          val buttonProceedToCart: Button, val itemList:ArrayList<MenuData>):
    RecyclerView.Adapter<MenuRecyclerAdapter.MenuViewHolder>() {
    var itemSelectedId= arrayListOf<String>()
    lateinit var proceedToCart: RelativeLayout
    var itemSelectedCount:Int=0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.menu_recycler_single_row, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {

        val restSingle = itemList[position]
        proceedToCart = proceedToCartPassed
        holder.dishName.text = restSingle.name
        holder.dishPrice.text = "Rs.${restSingle.cost_for_one}"
        holder.srNumber.text = (position + 1).toString()
        //holder.addButton.tag = restSingle.food_id + ""

        //val sharedUserId: SharedPreferences =context.getSharedPreferences((R.string.reg_user_id).toString(),Context.MODE_PRIVATE)
        //val uId=sharedUserId.getString("user_id","n/a")

        holder.addButton.setOnClickListener {
            if (restSingle.food_id in itemSelectedId && holder.addButton.text=="Remove") {
                itemSelectedCount--
                itemSelectedId.remove(restSingle.food_id)
                println("removed ${restSingle.food_id} qwr")
                println("qwr ${itemSelectedId}")
                holder.addButton.text = "Add"
                holder.addButton.setBackgroundResource(R.color.the_black)

            } else {
                itemSelectedCount++
                itemSelectedId.add(restSingle.food_id)
                println("added ${restSingle.food_id} qwr")
                println("qwr ${itemSelectedId}")
                holder.addButton.text = "Remove"
                holder.addButton.setBackgroundColor(Color.rgb(255, 196, 0))
            }

            if (itemSelectedCount > 0) {
                proceedToCart.visibility = View.VISIBLE
            } else {
                proceedToCart.visibility = View.INVISIBLE
            }

            buttonProceedToCart.setOnClickListener {
                val intent = Intent(context, CartActivity::class.java)
                intent.putExtra("restaurantId", restaurantId)
                intent.putExtra("restaurantName", restaurantName)
                intent.putExtra("selectedItemsId", itemSelectedId)
                context.startActivity(intent)
            }


        }
    }

        override fun getItemCount(): Int {
            return itemList.size
        }

        fun getSelectedItemCount(): Int {
            return itemSelectedCount
        }


    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dishName: TextView = view.findViewById(R.id.dish_name)
        val dishPrice: TextView = view.findViewById(R.id.dish_price)
        val addButton: TextView = view.findViewById(R.id.add_button)
        val srNumber: TextView = view.findViewById(R.id.sr_number)

    }


}
