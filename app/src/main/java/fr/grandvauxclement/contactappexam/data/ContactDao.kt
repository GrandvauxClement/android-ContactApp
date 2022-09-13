package fr.grandvauxclement.contactappexam.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Query("SELECT * FROM contact")
    fun getAllContact(): Flow<List<Contact>>

    @Query("SELECT * FROM contact WHERE id = :id")
    fun findById(id: Int): Flow<Contact>

    @Query("SELECT * FROM contact WHERE favors = 1")
    fun findByIsFavorite() : Flow<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(contact: Contact)

    @Update
    suspend fun update(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)
}