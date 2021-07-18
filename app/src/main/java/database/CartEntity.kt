package database

import android.view.autofill.AutofillId
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntity(
  @PrimaryKey(autoGenerate = true) val key:Int,
    val restaurant_id:String,
    val foodItems_id:String,
    val foodItems_price:String,
)
