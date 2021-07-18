package database

import androidx.room.Database
import androidx.room.RoomDatabase
@Database(entities = [RestaurantsEntity::class],version=2)
abstract class RestaurantDatabase:RoomDatabase() {
        abstract fun restaurantsDao():RestaurantsDao
    }
