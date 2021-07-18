package database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CartEntity::class],version=3
)
abstract class CartDatabase:RoomDatabase() {
    abstract fun CartDao():CartDao
}