package database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.foodrunner.model.Restaurants

@Dao
interface RestaurantsDao {
    @Insert
    fun insertRestaurants(restaurantsEntity: RestaurantsEntity)

    @Delete
    fun deleteRestaurants(restaurantsEntity: RestaurantsEntity)

    @Query("SELECT * FROM `fav-restaurants` where restaurant_id=:Id")
    fun getRestaurantssById(Id: String):RestaurantsEntity

    @Query("SELECT * from `fav-restaurants`")//writing custom sql query
    fun getAllRestaurants():List<Restaurants>//returns all the Restaurants in the database


}