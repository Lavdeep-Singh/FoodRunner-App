package database

import androidx.room.*

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCart(cartEntity: CartEntity)

   // @Query("UPDATE credentials SET password = :pass_word WHERE phone_number = :phoneNumber")
   // fun updateCredentials(phoneNumber: String,pass_word: String )

    @Delete
    fun deleteCart(cartEntity: CartEntity)

    @Query("SELECT * FROM cart where foodItems_id=:food_item_id")
    fun getFoodItemById(food_item_id: String):CartEntity

  /*  @Query("SELECT * FROM credentials where phone_number=:phoneNumber AND password=:pass_word")
    fun getCredentialsById(phoneNumber:String,pass_word:String):CartEntity

    @Query("SELECT * FROM credentials where phone_number=:phoneNumber")
    fun getCredentialsByPhone(phoneNumber: String):CartEntity

    @Query("SELECT * FROM credentials where phone_number=:phoneNumber")
    fun getCredentialsByNumber(phoneNumber: String):List<CartEntity>

   */

}