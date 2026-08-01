package com.example.data

import kotlinx.coroutines.flow.Flow

class QuoteRepository(private val dao: QuoteDao) {

    val rooms: Flow<List<RoomEntity>> = dao.getRoomsByQuoteId(1)
    val config: Flow<PricingConfig?> = dao.getPricingConfig()
    val savedQuotes: Flow<List<SavedQuoteEntity>> = dao.getAllSavedQuotes()

    suspend fun insertRoom(room: RoomEntity): Long {
        return dao.insertRoom(room)
    }

    suspend fun updateRoom(room: RoomEntity) {
        dao.updateRoom(room)
    }

    suspend fun deleteRoom(room: RoomEntity) {
        dao.deleteRoom(room)
    }

    suspend fun clearRooms() {
        dao.clearAllRooms(1)
    }

    suspend fun saveConfig(config: PricingConfig) {
        dao.savePricingConfig(config)
    }

    suspend fun saveQuoteToHistory(quote: SavedQuoteEntity): Long {
        return dao.insertSavedQuote(quote)
    }

    suspend fun deleteSavedQuote(id: Long) {
        dao.deleteSavedQuoteById(id)
    }
}
