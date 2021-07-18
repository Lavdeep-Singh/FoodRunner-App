package database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fav-restaurants")
data class RestaurantsEntity (
        @PrimaryKey
        val restaurant_id:String,
        val restaurant_name: String,
        val restaurant_ratings: String,
        val restaurant_cost: String,
        val restaurant_image: String
    )
