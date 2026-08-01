package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {
    // Cômodos
    @Query("SELECT * FROM rooms WHERE quoteId = :quoteId ORDER BY id ASC")
    fun getRoomsByQuoteId(quoteId: Long = 1): Flow<List<RoomEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: RoomEntity): Long

    @Update
    suspend fun updateRoom(room: RoomEntity)

    @Delete
    suspend fun deleteRoom(room: RoomEntity)

    @Query("DELETE FROM rooms WHERE quoteId = :quoteId")
    suspend fun clearAllRooms(quoteId: Long = 1)

    // Configuração de Preços e Rendimento
    @Query("SELECT * FROM pricing_config WHERE id = 1")
    fun getPricingConfig(): Flow<PricingConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePricingConfig(config: PricingConfig)

    // Histórico de Orçamentos
    @Query("SELECT * FROM saved_quotes ORDER BY dateTimestamp DESC")
    fun getAllSavedQuotes(): Flow<List<SavedQuoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedQuote(savedQuote: SavedQuoteEntity): Long

    @Query("DELETE FROM saved_quotes WHERE id = :id")
    suspend fun deleteSavedQuoteById(id: Long)
}
